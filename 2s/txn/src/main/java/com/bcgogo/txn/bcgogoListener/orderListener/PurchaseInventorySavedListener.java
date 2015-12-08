package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.PurchaseInventorySavedEvent;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.txn.dto.PurchaseInventoryItemDTO;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.IPurchaseCostStatService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.txn.service.web.IGoodsStorageService;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-12
 * Time: 下午1:34
 */
public class PurchaseInventorySavedListener extends OrderSavedListener {
  public PurchaseInventorySavedEvent getPurchaseInventorySavedEvent() {
    return purchaseInventorySavedEvent;
  }

  public void setPurchaseInventorySavedEvent(PurchaseInventorySavedEvent purchaseInventorySavedEvent) {
    this.purchaseInventorySavedEvent = purchaseInventorySavedEvent;
  }

  private PurchaseInventorySavedEvent purchaseInventorySavedEvent;


  public PurchaseInventorySavedListener(PurchaseInventorySavedEvent purchaseInventorySavedEvent) {
    super();
    this.purchaseInventorySavedEvent = purchaseInventorySavedEvent;
  }

  public void run() {
    PurchaseInventoryDTO purchaseInventoryDTO = this.getPurchaseInventorySavedEvent().getPurchaseInventoryDTO();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(purchaseInventoryDTO.getShopId());
    if (purchaseInventoryDTO.getStatus() == OrderStatus.PURCHASE_INVENTORY_REPEAL) {
      businessStatByOrder(purchaseInventoryDTO, true, false, null);
      purchaseInventoryStat(purchaseInventoryDTO, true);
      //reindex supplier solr
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(purchaseInventoryDTO.getSupplierId());

      //入库单作废 出入库打通逻辑 并且重做使用这些入库单的索引
      Map<OrderTypes, HashSet<Long>> reIndexMap = ServiceManager.getService(IProductOutStorageService.class).productThroughByInventoryRepeal(purchaseInventoryDTO);
      if (MapUtils.isNotEmpty(reIndexMap)) {
        Set<OrderTypes> orderTypes = reIndexMap.keySet();
        for (OrderTypes orderType : orderTypes) {
          HashSet<Long> orderIdSet = reIndexMap.get(orderType);
          ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(shopDTO, orderType, orderIdSet.toArray(new Long[orderIdSet.size()]));
        }
      }

      //reindex order solr
      ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(shopDTO, OrderTypes.INVENTORY, purchaseInventoryDTO.getId());
      if (purchaseInventoryDTO.getPurchaseOrderId() != null) {
        //重做 purchase order index
        ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(shopDTO, OrderTypes.PURCHASE, purchaseInventoryDTO.getPurchaseOrderId());
      }
      return;
    }
    purchaseInventoryStat(purchaseInventoryDTO, false);

    businessStatByOrder(purchaseInventoryDTO, false, false, null);

    try {
      if (purchaseInventoryDTO != null) {
        if (purchaseInventoryDTO.getPurchaseOrderId() != null)
          ServiceManager.getService(IPushMessageService.class).disabledPushMessageReceiverBySourceId(null, purchaseInventoryDTO.getPurchaseOrderId(),null,PushMessageSourceType.SALE_NEW, PushMessageSourceType.PURCHASE_SELLER_STOCK, PushMessageSourceType.PURCHASE_SELLER_DISPATCH);

        if (purchaseInventoryDTO.getCreationDate() == null) {
          if (purchaseInventoryDTO.getVestDate() != null) {
            purchaseInventoryDTO.setCreationDate(purchaseInventoryDTO.getVestDate());
          } else if (purchaseInventoryDTO.getEditDate() != null) {
            purchaseInventoryDTO.setCreationDate(purchaseInventoryDTO.getEditDate());
          } else {
            purchaseInventoryDTO.setCreationDate(System.currentTimeMillis());
          }
        }
        //生成orderIndexDTO
        OrderIndexDTO orderIndexDTO = purchaseInventoryDTO.toOrderIndexDTO();
        ServiceManager.getService(IOrderIndexService.class).saveOrUpdateOrderIndex(orderIndexDTO);
        if (orderIndexDTO.getOrderType() != null) {
          //入库单特殊处理，判断这个单子是否有采购单。如果有，更新solr中的采购单状态
          if (orderIndexDTO.getOrderType() == OrderTypes.INVENTORY) {
            long orderId = orderIndexDTO.getOrderId();
            IGoodsStorageService goodsStorageService = ServiceManager.getService(IGoodsStorageService.class);
            try {
              PurchaseInventoryDTO dto = goodsStorageService.getPurchaseInventory(orderId, purchaseInventoryDTO.getShopId());
              if (dto != null) {
                if (dto.getPurchaseOrderId() != null && dto.getPurchaseOrderId() != 0) {
                  //修改db orderIndex的状态
                  ServiceManager.getService(ISearchService.class).updatePurchaseOrderIndexStatus(orderIndexDTO.getShopId(), dto.getPurchaseOrderId(), OrderStatus.PURCHASE_ORDER_DONE);
                }
              }
            } catch (Exception e) {
              LOG.error("/PurchaseInventorySavedListener");
              LOG.error("method=run");
              LOG.error("查询PurchaseInventoryDTO失败");
              LOG.error(e.getMessage(), e);
            }
          }
        }
        reCreateSolrIndex(purchaseInventoryDTO,shopDTO);

        //入库 不推送SALE_NEW PURCHASE_SELLER_STOCK  SALE_NEW三种消息 by ZhangJuntao
        if (purchaseInventoryDTO.getPurchaseOrderId() != null) {
          IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
          pushMessageService.disabledPushMessageReceiverBySourceId(null, purchaseInventoryDTO.getPurchaseOrderId(),null,PushMessageSourceType.PURCHASE_SELLER_DISPATCH, PushMessageSourceType.PURCHASE_SELLER_STOCK);
        }
        if(OrderStatus.PURCHASE_INVENTORY_DONE.equals(purchaseInventoryDTO.getStatus()) && !ArrayUtils.isEmpty(purchaseInventoryDTO.getProductIds())) {
          IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
          IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
          Long startTime = DateUtil.getInnerDayTime(-30);
          Long endTime = System.currentTimeMillis();
          List<ProductDTO> productDTOList = preciseRecommendService.getSalesAmountByShopIdProductIdTime(purchaseInventoryDTO.getShopId(),startTime,endTime,purchaseInventoryDTO.getProductIds());
          List<PreBuyOrderDTO> preBuyOrderDTOList = preBuyOrderService.createPreBuyOrderByProductDTO(purchaseInventoryDTO.getShopId(), BusinessChanceType.SellWell,productDTOList.toArray(new ProductDTO[productDTOList.size()]));
          if (CollectionUtil.isNotEmpty(preBuyOrderDTOList)) {
            List<Long> orderIds = new ArrayList<Long>();
            for (PreBuyOrderDTO orderDTO : preBuyOrderDTOList) {
              orderIds.add(orderDTO.getId());
            }
            ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(shopDTO, OrderTypes.PRE_BUY_ORDER, ArrayUtil.toLongArr(orderIds));
          }
        }
      } else {
        throw new Exception("入库单 purchaseInventoryDTO 为空");
      }
    } catch (Exception e) {
      LOG.error("/PurchaseInventorySavedListener");
      LOG.error("method=run");
      LOG.error("入库单信息保存失败");
      LOG.error(e.getMessage(), e);
    }

    //常用产品保存
    purchaseInventoryDTO.setCurrentUsedProductDTOList();
    purchaseInventoryDTO.setCurrentUsedVehicleDTOList();
    this.currentUsedSaved(purchaseInventoryDTO);
    //更新单位顺序
    ServiceManager.getService(IConfigService.class).updateOrderUnitSort(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO);

     //add by WLF 提醒数量，更新到缓存
    if(purchaseInventoryDTO.getPurchaseOrderId() != null) {
      ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.TXN, purchaseInventoryDTO.getShopId());
    }

    purchaseInventorySavedEvent.setOrderFlag(true);

  }

  public void reCreateSolrIndex(PurchaseInventoryDTO purchaseInventoryDTO,ShopDTO shopDTO)throws Exception{
    ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getProductIds());

    //reindex supplier solr
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(purchaseInventoryDTO.getSupplierId());
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(shopDTO, OrderTypes.INVENTORY, purchaseInventoryDTO.getId());
    if (purchaseInventoryDTO.getPurchaseOrderId() != null) {
      //purchase order index
      ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(shopDTO, OrderTypes.PURCHASE, purchaseInventoryDTO.getPurchaseOrderId());
    }

    if (!ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())) {
      List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        if (purchaseInventoryItemDTO.isAddVehicleInfoToSolr()) {
          VehicleDTO vehicleDTOForSolr = new VehicleDTO(purchaseInventoryItemDTO);
          if (vehicleDTOForSolr.getId() != null) {
            vehicleDTOs.add(vehicleDTOForSolr);
          }
        }
      }
      ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOs);
    }

  }

  /**
   * 采购成本统计
   *
   * @param purchaseInventoryDTO
   * @param isRepeal
   */
  private void purchaseInventoryStat(PurchaseInventoryDTO purchaseInventoryDTO, boolean isRepeal) {
    try {
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
      purchaseCostStatService.purchaseCostStat(purchaseInventoryDTO, isRepeal);

    } catch (Exception e) {
      LOG.error("采购成本统计出错 /PurchaseInventorySavedListener method=purchaseInventoryStat shopId:" + purchaseInventoryDTO.getShopId());
      LOG.error(e.getMessage(), e);
      LOG.error("单据内容: {}", purchaseInventoryDTO);
    }
  }

}
