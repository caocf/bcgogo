package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IBaseProductService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.product.service.PromotionsService;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.ShopRelation.IShopRelationService;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

import static java.lang.String.valueOf;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-25
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class GoodSaleService implements IGoodSaleService {
  private static final Logger LOG = LoggerFactory.getLogger(GoodSaleService.class);
  private RFITxnService rfTxnService;
  private IInventoryService inventoryService;
  private IProductService productService;
  private ISearchService searchService;
  private IItemIndexService itemIndexService;
  private IProductCurrentUsedService productCurrentUsedService;
  private IOrderStatusChangeLogService orderStatusChangeLogService;
  private IProductOutStorageService productOutStorageService;
  private IProductInStorageService productInStorageService;
  private ISmsService smsService;
  private IOperationLogService operationLogService;
  private ITxnService txnService;

  @Autowired
  private TxnDaoManager txnDaoManager;
  @Autowired
  private RepairOrderCostCaculator repairOrderCostCaculator;

  public RFITxnService getRfTxnService() {
    if (rfTxnService == null) {
      rfTxnService = ServiceManager.getService(RFITxnService.class);
    }
    return rfTxnService;
  }

  public IInventoryService getInventoryService() {
    if (inventoryService == null) {
      inventoryService = ServiceManager.getService(IInventoryService.class);
    }
    return inventoryService;
  }

  public IProductService getProductService() {
    if (productService == null) {
      productService = ServiceManager.getService(IProductService.class);
    }
    return productService;
  }

  public ISearchService getSearchService() {
    if (searchService == null) {
      searchService = ServiceManager.getService(ISearchService.class);
    }
    return searchService;
  }


  public IItemIndexService getItemIndexService() {
    return itemIndexService == null ? ServiceManager.getService(IItemIndexService.class) : itemIndexService;
  }

  public IProductCurrentUsedService getProductCurrentUsedService() {
    if (productCurrentUsedService == null) {
      productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
    }
    return productCurrentUsedService;
  }

  public IOrderStatusChangeLogService getOrderStatusChangeLogService() {
    if (orderStatusChangeLogService == null) {
      orderStatusChangeLogService = ServiceManager.getService(IOrderStatusChangeLogService.class);
    }
    return orderStatusChangeLogService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }

  public IProductInStorageService getProductInStorageService() {
    return productInStorageService == null ? ServiceManager.getService(IProductInStorageService.class) : productInStorageService;
  }

  public ISmsService getSmsService() {
    return smsService == null ? ServiceManager.getService(ISmsService.class) : smsService;
  }

  public IOperationLogService getOperationLogService() {
    return operationLogService == null ? ServiceManager.getService(IOperationLogService.class) : operationLogService;
  }

  public ITxnService getTxnService() {
    return txnService == null ? ServiceManager.getService(ITxnService.class) : txnService;
  }

  @Override
  public void acceptSaleOrder(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO, Map<Long, OutStorageRelationDTO[]> relationDTOMap) throws Exception {
    if (salesOrderDTO == null || purchaseOrderDTO == null || ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs()) ||
        ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      LOG.error("接受订单异常：saleOrderDTO：{},purchaseOrderDTO:{}", salesOrderDTO, purchaseOrderDTO);
      return;
    }
    boolean isThroughSelectSupplier = BcgogoShopLogicResourceUtils.isThroughSelectSupplier(salesOrderDTO.getShopVersionId());
    List<InventorySearchIndex> inventorySearchIndexes = new ArrayList<InventorySearchIndex>();
    //更新销售单状态，更新采购单状态
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderDTO.getId(), salesOrderDTO.getShopId());
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        if (OrderStatus.PENDING.equals(salesOrders.get(0).getStatusEnum())) {
          salesOrders.get(0).setStatusEnum(OrderStatus.STOCKING);
          salesOrders.get(0).setPreDispatchDate(salesOrderDTO.getPreDispatchDate());
          salesOrders.get(0).setMemo(salesOrderDTO.getMemo());
          salesOrders.get(0).setGoodsSaler(salesOrderDTO.getGoodsSaler());
          salesOrders.get(0).setGoodsSalerId(salesOrderDTO.getGoodsSalerId());
          salesOrders.get(0).setVestDate(salesOrderDTO.getVestDate());
          salesOrders.get(0).setStorehouseId(salesOrderDTO.getStorehouseId());
          salesOrders.get(0).setStorehouseName(salesOrderDTO.getStorehouseName());
          salesOrderDTO.setStatus(salesOrders.get(0).getStatusEnum());
          writer.update(salesOrders.get(0));
          getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), OrderStatus.STOCKING, OrderStatus.PENDING, salesOrderDTO.getId(), OrderTypes.SALE));
        }
      }
      //接受的时候扣库存
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())) {
        getInventoryService().updateSaleOrderLackBySaleOrderIdAndStorehouse(writer, salesOrderDTO, inventorySearchIndexes);
      } else {
        getInventoryService().updateSaleOrderLackBySaleOrderId(writer, salesOrderDTO, inventorySearchIndexes);
      }
      List<PurchaseOrder> purchaseOrders = writer.getPurchaseOrderById(purchaseOrderDTO.getId(), purchaseOrderDTO.getShopId());
      if (CollectionUtils.isNotEmpty(purchaseOrders)) {
        if (OrderStatus.SELLER_PENDING.equals(purchaseOrders.get(0).getStatusEnum())) {
          purchaseOrders.get(0).setStatusEnum(OrderStatus.SELLER_STOCK);
          purchaseOrders.get(0).setPreDispatchDate(salesOrderDTO.getPreDispatchDate());
          writer.update(purchaseOrders.get(0));
          getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getUserId(), OrderStatus.SELLER_STOCK, OrderStatus.SELLER_PENDING, purchaseOrderDTO.getId(), OrderTypes.PURCHASE));
          purchaseOrderDTO.setStatus(purchaseOrders.get(0).getStatusEnum());
          purchaseOrderDTO.setPreDispatchDate(purchaseOrders.get(0).getPreDispatchDate());
        }
      }
      SalesOrderDTO salesOrderDTOClone = salesOrderDTO.clone();
      if (isThroughSelectSupplier && !ArrayUtil.isEmpty(salesOrderDTOClone.getItemDTOs())) {
        for (BcgogoOrderItemDto itemDto : salesOrderDTOClone.getItemDTOs()) {
          if(relationDTOMap != null) {
            itemDto.setOutStorageRelationDTOs(relationDTOMap.get(itemDto.getProductId()));
          }
          if (NumberUtil.doubleVal(itemDto.getReserved()) < NumberUtil.doubleVal(itemDto.getAmount())) {
            itemDto.setAmount(itemDto.getReserved());
          }
        }
      }
      getProductOutStorageService().productThroughByOrder(salesOrderDTOClone, OrderTypes.SALE, salesOrderDTOClone.getStatus(), writer, null);
      writer.commit(status);
      if (!salesOrderDTO.getIsShortage() && !(OrderStatus.PURCHASE_ORDER_DONE.equals(purchaseOrderDTO.getStatus()) || OrderStatus.PURCHASE_ORDER_REPEAL.equals(purchaseOrderDTO.getStatus()))) {
        List<InventoryRemindEventDTO> inventoryRemindEventDTOs = getTxnService().getInventoryRemindEventDTOByPurchaseOrderId(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId());
        if (CollectionUtils.isEmpty(inventoryRemindEventDTOs)) {
          //生成待入库提醒
          if (!ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())) {
            for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
              getRfTxnService().saveInventoryRemindEvent(writer, purchaseOrderDTO, purchaseOrderItemDTO);
            }
          }
        }
        //add by WLF 更新缓存中待办采购单的数量
        if (purchaseOrderDTO.getShopId() != null) {
          List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseOrderDTO.getShopId());
          getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, purchaseOrderDTO.getShopId(), supplierIdList);
        }
      }
      if (salesOrderDTO != null) {
        //更新上架量
        getProductService().handleInSalesAmountByOrder(salesOrderDTO, -1);
        getInventoryService().addOrUpdateInventorySearchIndexWithList(salesOrderDTO.getShopId(), inventorySearchIndexes);
      }      //操作记录
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.ACCEPT));
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseOrderDTO.getShopId(), salesOrderDTO.getUserId(), purchaseOrderDTO.getId(), ObjectTypes.PURCHASE_ORDER, OperationTypes.ACCEPT));

      //add by WLF 更新缓存中待办销售单的数量
      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesOrderDTO.getShopId());
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, salesOrderDTO.getShopId(), customerIdList);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void refuseSaleOrder(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    if (salesOrderDTO == null || purchaseOrderDTO == null || ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs()) ||
        ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      LOG.error("拒绝订单异常：saleOrderDTO：{},purchaseOrderDTO:{}", salesOrderDTO, purchaseOrderDTO);
      return;
    }
    //更新销售单状态，更新采购单状态
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderDTO.getId(), salesOrderDTO.getShopId());
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        if (OrderStatus.PENDING.equals(salesOrders.get(0).getStatusEnum())) {
          salesOrders.get(0).setStatusEnum(OrderStatus.REFUSED);
          salesOrders.get(0).setMemo(salesOrderDTO.getMemo());
          salesOrders.get(0).setRefuseMsg(salesOrderDTO.getRefuseMsg());
          getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), OrderStatus.REFUSED, OrderStatus.PENDING, salesOrderDTO.getId(), OrderTypes.SALE));
          salesOrderDTO.setStatus(salesOrders.get(0).getStatusEnum());
          writer.update(salesOrders.get(0));
          //采购不扣库存，拒绝不需要还库存
        }
      }
      List<PurchaseOrder> purchaseOrders = writer.getPurchaseOrderById(purchaseOrderDTO.getId(), purchaseOrderDTO.getShopId());
      if (CollectionUtils.isNotEmpty(purchaseOrders)) {
        if (OrderStatus.SELLER_PENDING.equals(purchaseOrders.get(0).getStatusEnum())) {
          purchaseOrders.get(0).setStatusEnum(OrderStatus.SELLER_REFUSED);
          purchaseOrders.get(0).setRefuseMsg(salesOrderDTO.getRefuseMsg());
          purchaseOrderDTO.setStatus(purchaseOrders.get(0).getStatusEnum());
          purchaseOrderDTO.setRefuseMsg(purchaseOrders.get(0).getRefuseMsg());
          writer.update(purchaseOrders.get(0));
          getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getUserId(), OrderStatus.SELLER_REFUSED, OrderStatus.SELLER_PENDING, purchaseOrderDTO.getId(), OrderTypes.PURCHASE));
          purchaseOrderDTO.setStatus(purchaseOrders.get(0).getStatusEnum());
        }
      }
      writer.commit(status);
      //保存操作记录
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.REFUSE));
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseOrderDTO.getShopId(), salesOrderDTO.getUserId(), purchaseOrderDTO.getId(), ObjectTypes.PURCHASE_ORDER, OperationTypes.REFUSE));
      ServiceManager.getService(IPromotionsService.class).updatePromotionOrderRecordStatus(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), purchaseOrderDTO.getStatus());

      //add by WLF 更新缓存中待办销售单的数量
      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesOrderDTO.getShopId());
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, salesOrderDTO.getShopId(), customerIdList);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
	public void refusePendingPurchaseOrder(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
		if(salesOrderDTO == null || purchaseOrderDTO == null || ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())) {
      LOG.error("拒绝订单异常：saleOrderDTO：{},purchaseOrderDTO:{}", salesOrderDTO, purchaseOrderDTO);
      return;
    }
    //更新销售单状态，更新采购单状态
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
//      List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderDTO.getId(), salesOrderDTO.getShopId());
//      if (CollectionUtils.isNotEmpty(salesOrders)) {
//        if (OrderStatus.PENDING.equals(salesOrders.get(0).getStatusEnum())) {
//          salesOrders.get(0).setStatusEnum(OrderStatus.REFUSED);
//          salesOrders.get(0).setMemo(salesOrderDTO.getMemo());
//          salesOrders.get(0).setRefuseMsg(salesOrderDTO.getRefuseMsg());
//	        getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), OrderStatus.REFUSED, OrderStatus.PENDING, salesOrderDTO.getId(), OrderTypes.SALE));
//          salesOrderDTO.setStatus(salesOrders.get(0).getStatusEnum());
//          writer.update(salesOrders.get(0));
//           //采购不扣库存，拒绝不需要还库存
//        }
//      }
			List<PurchaseOrder> purchaseOrders = writer.getPurchaseOrderById(purchaseOrderDTO.getId(),purchaseOrderDTO.getShopId());
			if (CollectionUtils.isNotEmpty(purchaseOrders)) {
				if (OrderStatus.SELLER_PENDING.equals(purchaseOrders.get(0).getStatusEnum())) {
					purchaseOrders.get(0).setStatusEnum(OrderStatus.SELLER_REFUSED);
					purchaseOrders.get(0).setRefuseMsg(salesOrderDTO.getRefuseMsg());
					purchaseOrderDTO.setStatus(purchaseOrders.get(0).getStatusEnum());
          purchaseOrderDTO.setRefuseMsg(purchaseOrders.get(0).getRefuseMsg());
					writer.update(purchaseOrders.get(0));
					getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseOrderDTO.getShopId(),
              purchaseOrderDTO.getUserId(), OrderStatus.SELLER_REFUSED, OrderStatus.SELLER_PENDING, purchaseOrderDTO.getId(), OrderTypes.PURCHASE));
					purchaseOrderDTO.setStatus(purchaseOrders.get(0).getStatusEnum());
				}
			}
      writer.commit(status);
	    //保存操作记录
//	    getOperationLogService().saveOperationLog(new OperationLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.REFUSE));
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseOrderDTO.getShopId(), salesOrderDTO.getUserId(), purchaseOrderDTO.getId(), ObjectTypes.PURCHASE_ORDER, OperationTypes.REFUSE));
      ServiceManager.getService(IPromotionsService.class).updatePromotionOrderRecordStatus(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), purchaseOrderDTO.getStatus());

      //add by WLF 更新缓存中待办销售单的数量
//      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesOrderDTO.getShopId());
//      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, salesOrderDTO.getShopId(), customerIdList);
    } finally {
			writer.rollback(status);
		}
	}

  @Override
  public void dispatchSaleOrder(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO, Map<Long,OutStorageRelationDTO[]> relationDTOMap) throws Exception {
    if(salesOrderDTO == null || purchaseOrderDTO == null || ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs()) ||
        ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      LOG.error("销售单发货异常：saleOrderDTO：{},purchaseOrderDTO:{}", salesOrderDTO, purchaseOrderDTO);
      return;
    }
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    //更新销售单状态，更新采购单状态
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderDTO.getId(), salesOrderDTO.getShopId());
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      Express express = null;
      Map<Long, SalesOrderItemDTO> unReservedItem = new HashMap<Long, SalesOrderItemDTO>();
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        if (OrderStatus.STOCKING.equals(salesOrders.get(0).getStatusEnum())) {
          Set<Long> productIds = new HashSet<Long>();
          Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
          Map<Long, Inventory> inventoryMap = new HashMap<Long, Inventory>();
          Map<Long, InventorySearchIndex> inventorySearchIndexMap = new HashMap<Long, InventorySearchIndex>();
          Map<Long, SalesOrderItemDTO> salesOrderItemDTOMap = new HashMap<Long, SalesOrderItemDTO>();
          List<SalesOrderItem> salesOrderItems = new ArrayList<SalesOrderItem>();
          express = new Express(salesOrderDTO);
          writer.save(express);
          salesOrders.get(0).setStatusEnum(OrderStatus.DISPATCH);
          salesOrders.get(0).setExpressId(express.getId());
          writer.update(salesOrders.get(0));
          getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), OrderStatus.DISPATCH, OrderStatus.STOCKING, salesOrderDTO.getId(), OrderTypes.SALE));
          salesOrderDTO.setStatus(salesOrders.get(0).getStatusEnum());
          salesOrderDTO.setExpressId(salesOrders.get(0).getExpressId());
          salesOrderItems = writer.getSalesOrderItemsByOrderId(salesOrders.get(0).getId());
          if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
            for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
              if (salesOrderItemDTO.getProductId() != null) {
                productIds.add(salesOrderItemDTO.getProductId());
              }
              if (salesOrderItemDTO.getId() != null) {
                salesOrderItemDTOMap.put(salesOrderItemDTO.getId(), salesOrderItemDTO);
              }
            }
          }
          if (CollectionUtils.isNotEmpty(productIds)) {
            inventoryMap = getInventoryService().getInventoryMap(salesOrderDTO.getShopId(), productIds);
            productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
            inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
          }
          if (CollectionUtils.isNotEmpty(salesOrderItems)) {
            for (SalesOrderItem salesOrderItem : salesOrderItems) {
              if (salesOrderItem.getProductId() != null) {
                ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItem.getProductId());
                Inventory inventory = inventoryMap.get(salesOrderItem.getProductId());
                if (inventory != null && NumberUtil.doubleVal(salesOrderItem.getAmount()) > NumberUtil.doubleVal(salesOrderItem.getReserved())) {
                  getInventoryService().caculateBeforeLimit(inventory.toDTO(), salesOrderDTO.getInventoryLimitDTO());
                  double itemReserved = NumberUtil.doubleVal(salesOrderItem.getReserved());
                  double itemAmount = NumberUtil.doubleVal(salesOrderItem.getAmount());
                  if (UnitUtil.isStorageUnit(salesOrderItem.getUnit(), productLocalInfoDTO)) {
                    itemReserved = itemReserved * productLocalInfoDTO.getRate();
                    itemAmount = itemAmount * productLocalInfoDTO.getRate();
                  }
                  if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())) {
                    storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(salesOrderDTO.getStorehouseId(), salesOrderItem.getProductId(), null, (itemAmount - itemReserved)));
                  }
                  inventory.setAmount(inventory.getAmount() - (itemAmount - itemReserved));
                  writer.update(inventory);
                  salesOrderItem.setReserved(salesOrderItem.getAmount());
                  getInventoryService().caculateAfterLimit(inventory.toDTO(), salesOrderDTO.getInventoryLimitDTO());
                  InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(salesOrderItem.getProductId());
                  if (inventorySearchIndex != null) {
                    inventorySearchIndex.setAmount(inventory.getAmount());
                    inventorySearchIndexList.add(inventorySearchIndex);
                  } else {
                    if (salesOrderItemDTOMap.get(salesOrderItem.getId()) != null) {
                      getRfTxnService().addInventorySearchIndex(inventorySearchIndexList, salesOrderDTO, salesOrderItemDTOMap.get(salesOrderItem.getId()));
                    }
                  }
                  unReservedItem.put(salesOrderItem.getProductId(), salesOrderItem.toDTO());
                }
              }
            }
          }
        }
      }
      List<PurchaseOrder> purchaseOrders = writer.getPurchaseOrderById(purchaseOrderDTO.getId(), purchaseOrderDTO.getShopId());
      if (CollectionUtils.isNotEmpty(purchaseOrders)) {
        if (OrderStatus.SELLER_STOCK.equals(purchaseOrders.get(0).getStatusEnum())) {
          purchaseOrders.get(0).setStatusEnum(OrderStatus.SELLER_DISPATCH);
          if (express != null) {
            purchaseOrders.get(0).setExpressId(express.getId());
          }
          purchaseOrderDTO.setStatus(purchaseOrders.get(0).getStatusEnum());
          writer.update(purchaseOrders.get(0));
          getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getUserId(), OrderStatus.SELLER_DISPATCH, OrderStatus.SELLER_STOCK, purchaseOrderDTO.getId(), OrderTypes.PURCHASE));
          purchaseOrderDTO.setStatus(purchaseOrders.get(0).getStatusEnum());
          purchaseOrderDTO.setExpressId(purchaseOrders.get(0).getExpressId());
        }
      }
      //出入口打通为接受过程中未打通的商品进行打通
      boolean isThroughSelectSupplier = BcgogoShopLogicResourceUtils.isThroughSelectSupplier(salesOrderDTO.getShopVersionId());
      if (!unReservedItem.isEmpty()) {
        List<SalesOrderItemDTO> unReservedItemList = new ArrayList<SalesOrderItemDTO>();
        for (Long key : unReservedItem.keySet()) {
          SalesOrderItemDTO itemDTO = unReservedItem.get(key);
          if (isThroughSelectSupplier && relationDTOMap != null && !relationDTOMap.isEmpty()) {
            itemDTO.setOutStorageRelationDTOs(relationDTOMap.get(key));
          }
          unReservedItemList.add(itemDTO);
        }
        SalesOrderDTO salesOrderDTOClone = salesOrderDTO.clone();
        salesOrderDTOClone.setItemDTOs(unReservedItemList.toArray(new SalesOrderItemDTO[unReservedItemList.size()]));
        getProductOutStorageService().productThroughByOrder(salesOrderDTOClone, OrderTypes.SALE, OrderStatus.STOCKING, writer, null);
      }
      writer.commit(status);

      if (!(OrderStatus.PURCHASE_ORDER_DONE.equals(purchaseOrderDTO.getStatus()) || OrderStatus.PURCHASE_ORDER_REPEAL.equals(purchaseOrderDTO.getStatus()))) {
        List<InventoryRemindEventDTO> inventoryRemindEventDTOs = getTxnService().getInventoryRemindEventDTOByPurchaseOrderId(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId());
        if (CollectionUtils.isEmpty(inventoryRemindEventDTOs)) {
          //生成待入库提醒
          if (!ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())) {
            for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
              getRfTxnService().saveInventoryRemindEvent(writer, purchaseOrderDTO, purchaseOrderItemDTO);
            }
          }
        }
      }

      //保存操作记录
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.DISPATCH));
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseOrderDTO.getShopId(), salesOrderDTO.getUserId(), purchaseOrderDTO.getId(), ObjectTypes.PURCHASE_ORDER, OperationTypes.DISPATCH));
      if (salesOrderDTO.getInventoryLimitDTO() != null) {
        getInventoryService().updateMemocacheLimitByInventoryLimitDTO(salesOrderDTO.getShopId(), salesOrderDTO.getInventoryLimitDTO());
      }
      if (CollectionUtils.isNotEmpty(inventorySearchIndexList)) {
        getInventoryService().addOrUpdateInventorySearchIndexWithList(salesOrderDTO.getShopId(), inventorySearchIndexList);
      }
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 结算前进行校验
   *
   * @param shopId
   * @param salesOrderDTO
   * @param status
   * @return
   */
  public String checkSalesOrderBeforeSettle(Long shopId, SalesOrderDTO salesOrderDTO, String status) {
    if (StringUtils.isBlank(status) || !(status.equals(OrderStatus.SALE_DEBT_DONE.toString()) || status.equals(OrderStatus.SALE_DONE.toString()))) {
      if (salesOrderDTO.getStatus() != OrderStatus.DISPATCH) {
        return ValidatorConstant.ORDER_STATUS_NO_CORRECT;
      }
    } else {
      if (salesOrderDTO.getStatus() != OrderStatus.SALE_DEBT_DONE && salesOrderDTO.getStatus() != OrderStatus.SALE_DONE) {
        return ValidatorConstant.ORDER_STATUS_NO_CORRECT;
      }
    }
    return null;
  }

  /**
   * 根据结算信息保存 receivable reception_record 单据状态 成本 欠款信息
   *
   * @param salesOrderDTO
   * @param accountInfoDTO
   * @throws Exception
   */
  public void saveReceivableDebtReceptionRecord(SalesOrderDTO salesOrderDTO, AccountInfoDTO accountInfoDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    ReceivableDTO receivableDTO = this.getReceivableFromAccountInfoDTO(salesOrderDTO, accountInfoDTO);

    receivableDTO = this.getReceptionRecordFromAccountInfoDTO(salesOrderDTO, accountInfoDTO, receivableDTO);

    Object status = writer.begin();
    try {
      SalesOrder salesOrder = writer.getById(SalesOrder.class, salesOrderDTO.getId());
//      if (accountInfoDTO.getAccountDebtAmount() > 0) {
//        salesOrder.setStatusEnum(OrderStatus.SALE_DEBT_DONE);
//        salesOrderDTO.setStatus(OrderStatus.SALE_DEBT_DONE);
//        salesOrderDTO.setVestDate(System.currentTimeMillis());
//        salesOrder.setVestDate(System.currentTimeMillis());
//      } else {
      salesOrder.setStatusEnum(OrderStatus.SALE_DONE);
      salesOrderDTO.setStatus(OrderStatus.SALE_DONE);
      if (salesOrder.getVestDate() == null) {
        salesOrder.setVestDate(System.currentTimeMillis());
        salesOrderDTO.setVestDate(System.currentTimeMillis());
      }
//      }
      receivableDTO.setVestDate(System.currentTimeMillis());
      repairOrderCostCaculator.calculate(salesOrderDTO, null, null);
      salesOrder.setTotalCostPrice(salesOrderDTO.getTotalCostPrice());
      salesOrder.setOtherTotalCostPrice(salesOrderDTO.getOtherTotalCostPrice());
      if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
        for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
          if (salesOrderItemDTO == null || salesOrderItemDTO.getProductId() == null) {
            continue;
          }
          SalesOrderItem salesOrderItem = writer.getById(SalesOrderItem.class, salesOrderItemDTO.getId());
          salesOrderItem.setTotalCostPrice(salesOrderItemDTO.getTotalCostPrice());
          salesOrderItem.setCostPrice(salesOrderItemDTO.getCostPrice());
          writer.update(salesOrderItem);

          Inventory inventory = writer.getById(Inventory.class, salesOrderItemDTO.getProductId());
          if (null != inventory) {
            if ((salesOrderDTO.getStatus() == OrderStatus.SALE_DONE || salesOrderDTO.getStatus() == OrderStatus.SALE_DEBT_DONE) && NumberUtil.longValue(inventory.getLastSalesTime()) < salesOrderDTO.getVestDate()) {
              inventory.setLastSalesTime(salesOrderDTO.getVestDate());
            }
            writer.update(inventory);
          }
        }
      }
      salesOrder.setAfterMemberDiscountTotal(salesOrder.getTotal());
      writer.update(salesOrder);
      if (null != receivableDTO) {
        receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getTotal());
      }

      ReceivableHistoryDTO receivableHistoryDTO = receivableDTO.toReceivableHistoryDTO();
      ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
      receivableHistory.setCheckNo(accountInfoDTO.getBankCheckNo());
      writer.save(receivableHistory);
      ReceptionRecordDTO recordDTOs[] = receivableDTO.getRecordDTOs();
      if (recordDTOs != null) {
        for (ReceptionRecordDTO recordDTO : recordDTOs) {
          recordDTO.setReceivableHistoryId(receivableHistory.getId());
          Long remindTime = StringUtils.isNotBlank(accountInfoDTO.getHuankuanTime()) ? DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, accountInfoDTO.getHuankuanTime()) : null;
          recordDTO.setToPayTime(remindTime);
        }
      }

      txnService.createOrUpdateReceivable(writer, receivableDTO);
      this.saveDebtFromAccountInfoDTO(salesOrderDTO, accountInfoDTO, receivableDTO, writer);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void stopSaleOrder(Long toStorehouseId, SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    //销售中止短信
    if (OrderStatus.STOCKING.equals(salesOrderDTO.getStatus())) {
      getSmsService().stockingCancelSMS(purchaseOrderDTO.getShopId(), salesOrderDTO.getShopId(), purchaseOrderDTO.getReceiptNo(), salesOrderDTO.getMobile());
    } else if (OrderStatus.DISPATCH.equals(salesOrderDTO.getStatus())) {
      getSmsService().shippedCancelSMS(purchaseOrderDTO.getShopId(), salesOrderDTO.getShopId(), purchaseOrderDTO.getReceiptNo(), salesOrderDTO.getMobile());
    }

    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderDTO.getId(), salesOrderDTO.getShopId());
      List<PurchaseOrder> purchaseOrders = writer.getPurchaseOrderById(purchaseOrderDTO.getId(), purchaseOrderDTO.getShopId());
      List<SalesOrderItem> salesOrderItems = null;
      Map<Long, Inventory> inventoryMap = new HashMap<Long, Inventory>();
      Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
      Set<Long> productIds = new HashSet<Long>();
      Map<Long, InventorySearchIndex> oldInventoryServiceMap = new HashMap<Long, InventorySearchIndex>();
      List<InventorySearchIndex> inventorySearchIndexListToUpdate = new ArrayList<InventorySearchIndex>();
      OrderStatus lastOrderStatus = salesOrderDTO.getStatus();
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        salesOrders.get(0).setStatusEnum(OrderStatus.SELLER_STOP);
        salesOrders.get(0).setRepealMsg(salesOrderDTO.getRepealMsg());
        writer.update(salesOrders.get(0));
        salesOrderDTO.setStatus(salesOrders.get(0).getStatusEnum());
        getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(),
            salesOrderDTO.getUserId(),
            salesOrderDTO.getStatus(),
            lastOrderStatus,
            salesOrderDTO.getId(),
            OrderTypes.SALE));
        salesOrderItems = writer.getSalesOrderItemsByOrderId(salesOrders.get(0).getId());
      }
      if (CollectionUtils.isNotEmpty(purchaseOrders)) {
        OrderStatus lastPurchaseOrderStatus = purchaseOrders.get(0).getStatusEnum();
        purchaseOrders.get(0).setStatusEnum(OrderStatus.PURCHASE_SELLER_STOP);
        purchaseOrders.get(0).setRefuseMsg(salesOrderDTO.getRepealMsg());
        writer.update(purchaseOrders.get(0));
        getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getUserId(), OrderStatus.PURCHASE_SELLER_STOP, lastPurchaseOrderStatus, purchaseOrderDTO.getId(), OrderTypes.PURCHASE));
        purchaseOrderDTO.setStatus(purchaseOrders.get(0).getStatusEnum());
        purchaseOrderDTO.setRefuseMsg(purchaseOrders.get(0).getRefuseMsg());
        writer.deleteInventoryRemindEventByShopIdAndPurchaseOrderId(purchaseOrders.get(0).getShopId(), purchaseOrders.get(0).getId());
        writer.cancelRemindEventByOrderId(RemindEventType.TXN, purchaseOrders.get(0).getId());
        getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.TXN, purchaseOrders.get(0).getShopId());
      }
      if (CollectionUtils.isNotEmpty(salesOrderItems)) {
        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          if (salesOrderItem.getProductId() != null) {
            productIds.add(salesOrderItem.getProductId());
          }
        }
        inventoryMap = getInventoryService().getInventoryMap(salesOrderDTO.getShopId(), productIds);
        productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
        oldInventoryServiceMap = getSearchService().getInventorySearchIndexMapByProductIds(salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));

        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          Inventory inventory = inventoryMap.get(salesOrderItem.getProductId());
          if (NumberUtil.doubleVal(salesOrderItem.getReserved()) > 0 && inventory != null) {
            double reservedWithSellUnit = NumberUtil.doubleVal(salesOrderItem.getReserved());
            ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItem.getProductId());
            if (UnitUtil.isStorageUnit(salesOrderItem.getUnit(), productLocalInfoDTO)) {
              reservedWithSellUnit = reservedWithSellUnit * productLocalInfoDTO.getRate();
            }
            getInventoryService().caculateBeforeLimit(inventory.toDTO(), salesOrderDTO.getInventoryLimitDTO());
            inventory.setAmount(inventory.getAmount() + reservedWithSellUnit);
            if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())) {
              storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, new StoreHouseInventoryDTO(salesOrderDTO.getStorehouseId(), salesOrderItem.getProductId(), null, reservedWithSellUnit));
            }
            writer.update(inventory);
            salesOrderItem.setReserved(0d);
            writer.update(salesOrderItem);
            getInventoryService().caculateAfterLimit(inventory.toDTO(), salesOrderDTO.getInventoryLimitDTO());
            InventorySearchIndex inventorySearchIndex = oldInventoryServiceMap.get(salesOrderItem.getProductId());
            if (inventorySearchIndex != null) {
              inventorySearchIndex.setAmount(inventory.getAmount());
              inventorySearchIndexListToUpdate.add(inventorySearchIndex);
            }
          }
        }
      }

      //自动生成调拨单
      AllocateRecordDTO allocateRecordDTO = null;
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId()) && toStorehouseId != null) {
        allocateRecordDTO = createAllocateRecordBySalesOrderDTO(writer, toStorehouseId, salesOrderDTO);
      }
      getProductInStorageService().productThroughByOrder(salesOrderDTO, OrderTypes.SALE, OrderStatus.SALE_REPEAL, writer);
      writer.commit(status);
      //自动调拨索引
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId()) && toStorehouseId != null) {
        BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
        BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(allocateRecordDTO, OrderTypes.ALLOCATE_RECORD);
        bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);
      }
      //更新上架量
      getProductService().handleInSalesAmountByOrder(salesOrderDTO, 1);

      getTxnService().saveOperationLogTxnService(new OperationLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.SELL_STOP));
      getTxnService().saveOperationLogTxnService(new OperationLogDTO(purchaseOrderDTO.getShopId(), salesOrderDTO.getUserId(), purchaseOrderDTO.getId(), ObjectTypes.PURCHASE_ORDER, OperationTypes.SELL_STOP));

      //add by WLF 更新缓存中待办销售单的数量
      List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesOrderDTO.getShopId());
      getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, salesOrderDTO.getShopId(), customerIdList);

      //add by WLF 更新缓存中待办采购单的数量
      if (purchaseOrderDTO.getShopId() != null) {
        List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseOrderDTO.getShopId());
        getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, purchaseOrderDTO.getShopId(), supplierIdList);
      }
      getRfTxnService().updateSaleOrderStatus(salesOrderDTO.getShopId(), salesOrderDTO.getId(), OrderStatus.SELLER_STOP);
      getItemIndexService().updateItemIndexPurchaseOrderStatus(salesOrderDTO.getShopId(), OrderTypes.SALE,
          salesOrderDTO.getId(), OrderStatus.SELLER_STOP);
      getRfTxnService().updateSaleOrderStatus(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId(), OrderStatus.PURCHASE_SELLER_STOP);
      getItemIndexService().updateItemIndexPurchaseOrderStatus(purchaseOrderDTO.getShopId(), OrderTypes.PURCHASE,
          purchaseOrderDTO.getId(), OrderStatus.PURCHASE_SELLER_STOP);
      ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(salesOrderDTO.getShopId()), OrderTypes.SALE, salesOrderDTO.getId());
      ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseOrderDTO.getShopId()), OrderTypes.PURCHASE, purchaseOrderDTO.getId());

      getInventoryService().updateMemocacheLimitByInventoryLimitDTO(salesOrderDTO.getShopId(), salesOrderDTO.getInventoryLimitDTO());
      if (CollectionUtils.isNotEmpty(inventorySearchIndexListToUpdate)) {
        getInventoryService().addOrUpdateInventorySearchIndexWithList(salesOrderDTO.getShopId(), inventorySearchIndexListToUpdate);
      }
      getProductCurrentUsedService().saveRecentChangedProductInMemory(salesOrderDTO);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<InventorySearchIndex> updateInventoryAndInventorySearchIndex(Long shopId, SalesOrderDTO salesOrderDTO, TxnWriter writer) throws Exception {
    ISearchService searchService = ServiceManager.getService(SearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      for (int i = 0; i < salesOrderDTO.getItemDTOs().length; i++) {
        Inventory inventory = writer.getById(Inventory.class, salesOrderDTO.getItemDTOs()[i].getProductId());
        InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
        ProductDTO productDTO = productService.getProductByProductLocalInfoId(salesOrderDTO.getItemDTOs()[i].getProductId(), shopId);
        double itemAmount = salesOrderDTO.getItemDTOs()[i].getAmount();
        if (UnitUtil.isStorageUnit(salesOrderDTO.getItemDTOs()[i].getUnit(), productDTO)) {
          itemAmount = itemAmount * productDTO.getRate();
        }
        inventory.setAmount(inventory.getAmount() + itemAmount);
        inventorySearchIndex.createInventorySearchIndex(inventory.toDTO(), productDTO);
        inventorySearchIndexList.add(inventorySearchIndex);

        Sort sort = new Sort(" a.vestDate ", " desc ");
        Long salesVestDate = NumberUtil.longValue(writer.getSalesVestDateByShopId(shopId, inventory.getId(), sort));
        Long repairVestDate = NumberUtil.longValue(writer.getRepairVestDateByShopId(shopId, inventory.getId(), sort));

        Long lastSalesVestDate = salesVestDate > repairVestDate ? salesVestDate : repairVestDate;
        if (NumberUtil.longValue(lastSalesVestDate) > 0) {
          inventory.setLastSalesTime(lastSalesVestDate);
        } else {
          inventory.setLastSalesTime(null);
        }

        writer.update(inventory);
        if (UnitUtil.isStorageUnit(salesOrderDTO.getItemDTOs()[i].getUnit(), productDTO)) {
          salesOrderDTO.getItemDTOs()[i].setInventoryAmount(inventory.getAmount() / productDTO.getRate());
        } else {
          salesOrderDTO.getItemDTOs()[i].setInventoryAmount(inventory.getAmount());
        }
        //处理缺料事项
        List<LackMaterialDTO> lackMaterialDTOs = getTxnService().getLackMaterialByProductId(shopId, RepairRemindEventTypes.LACK, salesOrderDTO.getItemDTOs()[i].getProductId());
        if (lackMaterialDTOs != null && lackMaterialDTOs.size() > 0) {
          for (LackMaterialDTO lackMaterialDTO : lackMaterialDTOs) {
            double lackMaterialAmount = lackMaterialDTO.getAmount();
            if (UnitUtil.isStorageUnit(lackMaterialDTO.getUnit(), productDTO)) {
              lackMaterialAmount = lackMaterialAmount * productDTO.getRate();
            }
            if (lackMaterialAmount < itemAmount) {
              salesOrderDTO.setReturnType("1");
              salesOrderDTO.setReturnIndex(valueOf(i));
            }
          }
        }
      }
    }
    return inventorySearchIndexList;
  }

  @Override
  public List<InventorySearchIndex> updateInventoryAndInventorySearchIndexByStoreHouse(Long shopId, Long toStorehouseId, SalesOrderDTO salesOrderDTO, TxnWriter writer) throws Exception {
    ISearchService searchService = ServiceManager.getService(SearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    AllocateRecordDTO allocateRecordDTO = null;
    if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
      List<Long> productIdList = new ArrayList<Long>();
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        productIdList.add(salesOrderItemDTO.getProductId());
      }
      Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, salesOrderDTO.getStorehouseId(), productIdList.toArray(new Long[productIdList.size()]));

      StoreHouseInventoryDTO storeHouseInventoryDTO = null;
      SalesOrderItemDTO salesOrderItemDTO = null;
      for (int i = 0; i < salesOrderDTO.getItemDTOs().length; i++) {
        salesOrderItemDTO = salesOrderDTO.getItemDTOs()[i];
        storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(salesOrderItemDTO.getProductId());
        if (storeHouseInventoryDTO == null) {
          storeHouseInventoryDTO = new StoreHouseInventoryDTO(salesOrderDTO.getStorehouseId(), salesOrderItemDTO.getProductId(), 0d);
        }
        Inventory inventory = writer.getById(Inventory.class, salesOrderItemDTO.getProductId());
        InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
        ProductDTO productDTO = productService.getProductByProductLocalInfoId(salesOrderItemDTO.getProductId(), shopId);
        productDTOMap.put(salesOrderItemDTO.getProductId(), productDTO);
        double itemAmount = salesOrderItemDTO.getAmount();
        if (UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productDTO)) {
          itemAmount = itemAmount * productDTO.getRate();
        }
        storeHouseInventoryDTO.setAmount(storeHouseInventoryDTO.getAmount() + itemAmount);
        inventory.setAmount(inventory.getAmount() + itemAmount);
        inventorySearchIndex.createInventorySearchIndex(inventory.toDTO() , productDTO);
        inventorySearchIndexList.add(inventorySearchIndex);

        Sort sort = new Sort(" a.vestDate ", " desc ");
        Long salesVestDate = NumberUtil.longValue(writer.getSalesVestDateByShopId(shopId, inventory.getId(), sort));
        Long repairVestDate = NumberUtil.longValue(writer.getRepairVestDateByShopId(shopId, inventory.getId(), sort));

        Long lastSalesVestDate = salesVestDate > repairVestDate ? salesVestDate : repairVestDate;
        if (NumberUtil.longValue(lastSalesVestDate) > 0) {
          inventory.setLastSalesTime(lastSalesVestDate);
        } else {
          inventory.setLastSalesTime(null);
        }

        storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
        writer.update(inventory);
        if (UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productDTO)) {
          salesOrderItemDTO.setInventoryAmount(inventory.getAmount() / productDTO.getRate());
        } else {
          salesOrderItemDTO.setInventoryAmount(inventory.getAmount());
        }
      }
      Long checkLackStorehouseId = salesOrderDTO.getStorehouseId();
      //自动生成调拨单
      if (toStorehouseId != null) {
        allocateRecordDTO = createAllocateRecordBySalesOrderDTO(writer, toStorehouseId, salesOrderDTO);
        checkLackStorehouseId = toStorehouseId;
      }
      for (int i = 0; i < salesOrderDTO.getItemDTOs().length; i++) {
        salesOrderItemDTO = salesOrderDTO.getItemDTOs()[i];
        ProductDTO productDTO = productDTOMap.get(salesOrderItemDTO.getProductId());
        double itemAmount = salesOrderItemDTO.getAmount();
        if (UnitUtil.isStorageUnit(salesOrderItemDTO.getUnit(), productDTO)) {
          itemAmount = itemAmount * productDTO.getRate();
        }
        //处理缺料事项
        List<LackMaterialDTO> lackMaterialDTOs = getTxnService().getLackMaterialByProductIdAndStorehouse(shopId, RepairRemindEventTypes.LACK, salesOrderItemDTO.getProductId(), checkLackStorehouseId);
        if (CollectionUtils.isNotEmpty(lackMaterialDTOs)) {
          for (LackMaterialDTO lackMaterialDTO : lackMaterialDTOs) {
            double lackMaterialAmount = lackMaterialDTO.getAmount();
            if (UnitUtil.isStorageUnit(lackMaterialDTO.getUnit(), productDTO)) {
              lackMaterialAmount = lackMaterialAmount * productDTO.getRate();
            }
            if (lackMaterialAmount < itemAmount) {
              salesOrderDTO.setReturnType("1");
              salesOrderDTO.setReturnIndex(valueOf(i));
            }
          }
        }
      }
    }
    if (toStorehouseId != null && allocateRecordDTO != null) {
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(allocateRecordDTO, OrderTypes.ALLOCATE_RECORD);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);
    }
    return inventorySearchIndexList;

  }

  private AllocateRecordDTO createAllocateRecordBySalesOrderDTO(TxnWriter writer, Long toStorehouseId, SalesOrderDTO salesOrderDTO) throws Exception {
    AllocateRecordDTO allocateRecordDTO = new AllocateRecordDTO();
    allocateRecordDTO.setOutStorehouseId(salesOrderDTO.getStorehouseId());
    allocateRecordDTO.setInStorehouseId(toStorehouseId);
    allocateRecordDTO.setOriginOrderId(salesOrderDTO.getId());
    allocateRecordDTO.setOriginOrderType(OrderTypes.SALE);
    allocateRecordDTO.setEditorId(salesOrderDTO.getEditorId());
    allocateRecordDTO.setEditor(salesOrderDTO.getEditor());
    allocateRecordDTO.setEditDate(System.currentTimeMillis());
    allocateRecordDTO.setVestDate(System.currentTimeMillis());
    allocateRecordDTO.setShopId(salesOrderDTO.getShopId());
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    allocateRecordDTO.setReceiptNo(txnService.getReceiptNo(allocateRecordDTO.getShopId(), OrderTypes.ALLOCATE_RECORD, null));

    AllocateRecordItemDTO allocateRecordItemDTO = null;
    Double totalCostPrice = 0d, totalAmount = 0d;
    if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      List<AllocateRecordItemDTO> allocateRecordItemDTOList = new ArrayList<AllocateRecordItemDTO>();
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        allocateRecordItemDTO = new AllocateRecordItemDTO();
        allocateRecordItemDTO.setAmount(salesOrderItemDTO.getAmount());
        allocateRecordItemDTO.setTotalCostPrice(salesOrderItemDTO.getTotalCostPrice());
        allocateRecordItemDTO.setCostPrice(salesOrderItemDTO.getCostPrice());
        allocateRecordItemDTO.setProductHistoryId(salesOrderItemDTO.getProductHistoryId());
        allocateRecordItemDTO.setProductId(salesOrderItemDTO.getProductId());
        allocateRecordItemDTO.setUnit(salesOrderItemDTO.getUnit());
        allocateRecordItemDTO.setStorageBin(salesOrderItemDTO.getStorageBin());
        totalCostPrice += NumberUtil.doubleVal(allocateRecordItemDTO.getTotalCostPrice());
        totalAmount += NumberUtil.doubleVal(allocateRecordItemDTO.getAmount());
        allocateRecordItemDTO.setOutStorageRelationDTOs(salesOrderItemDTO.getOutStorageRelationDTOs());
        allocateRecordDTO.setSelectSupplier(true);
        allocateRecordItemDTOList.add(allocateRecordItemDTO);
      }
      allocateRecordDTO.setItemDTOs(allocateRecordItemDTOList.toArray(new AllocateRecordItemDTO[allocateRecordItemDTOList.size()]));
    }

    allocateRecordDTO.setTotalCostPrice(totalCostPrice);
    allocateRecordDTO.setTotalAmount(totalAmount);

    IAllocateRecordService allocateRecordService = ServiceManager.getService(IAllocateRecordService.class);
    allocateRecordService.saveOrUpdateAllocateRecord(writer, salesOrderDTO.getShopId(), allocateRecordDTO);
    return allocateRecordDTO;
  }

  @Override
  public SalesOrderDTO getSimpleSalesOrderByPurchaseOrderId(Long purchaseOrderId) {
    if (purchaseOrderId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    SalesOrderDTO salesOrderDTO = null;
    List<SalesOrder> salesOrders = writer.getSalesOrderByPurchaseOrderId(purchaseOrderId);
    if (CollectionUtils.isNotEmpty(salesOrders)) {
      salesOrderDTO = salesOrders.get(0).toDTO();
      List<SalesOrderItem> salesOrderItems = writer.getSalesOrderItemsByOrderId(salesOrderDTO.getId());
      List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
      if (CollectionUtils.isNotEmpty(salesOrderItems)) {
        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          salesOrderItemDTOs.add(salesOrderItem.toDTO());
        }
        salesOrderDTO.setItemDTOs(salesOrderItemDTOs.toArray(new SalesOrderItemDTO[salesOrderItemDTOs.size()]));
      }
    }
    return salesOrderDTO;
  }

  /**
   * 生成实收信息
   *
   * @param salesOrderDTO
   * @param accountInfoDTO
   * @return
   */
  public ReceivableDTO getReceivableFromAccountInfoDTO(SalesOrderDTO salesOrderDTO, AccountInfoDTO accountInfoDTO) {

    IMembersService membersService = ServiceManager.getService(IMembersService.class);

    ReceivableDTO receivableDTO = new ReceivableDTO();
    receivableDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
    receivableDTO.setOrderType(OrderTypes.SALE);
    receivableDTO.setStatus(ReceivableStatus.FINISH);
    receivableDTO.setOrderId(salesOrderDTO.getId());
    receivableDTO.setShopId(salesOrderDTO.getShopId());

    receivableDTO.setSettledAmount(accountInfoDTO.getSettledAmount());
    receivableDTO.setDebt(accountInfoDTO.getAccountDebtAmount());
    receivableDTO.setDiscount(salesOrderDTO.getTotal() - accountInfoDTO.getSettledAmount() - accountInfoDTO.getAccountDebtAmount());
    receivableDTO.setTotal(salesOrderDTO.getTotal());
    receivableDTO.setLastPayee(salesOrderDTO.getUserName());
    receivableDTO.setLastPayeeId(salesOrderDTO.getUserId());
    receivableDTO.setCustomerId(salesOrderDTO.getCustomerId());
    receivableDTO.setReceiptNo(salesOrderDTO.getReceiptNo());
    receivableDTO.setVestDate(salesOrderDTO.getVestDate());
    try {
      Long remindTime = StringUtils.isNotBlank(accountInfoDTO.getHuankuanTime()) ? DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, accountInfoDTO.getHuankuanTime()) : null;
      receivableDTO.setRemindTime(remindTime);
    } catch (ParseException e) {
      receivableDTO.setRemindTime(null);
    }

    //如果含有会员结算信息 把member_id保存到receivable表中
    if (NumberUtil.doubleVal(accountInfoDTO.getMemberAmount()) > 0) {
      String memberNo = salesOrderDTO.getAccountMemberNo();
      Member member = membersService.getMemberByShopIdAndMemberNo(salesOrderDTO.getShopId(), memberNo);
      if (member == null) {
        LOG.error("/GoodSaleService.java method=saveReceivableDebtReceptionRecord");
        LOG.error("shopId:" + salesOrderDTO.getShopId() + ",memberNo:" + memberNo);
        LOG.error("会员查询出错");
      } else {
        receivableDTO.setMemberId(member.getId());
      }
    }
    //添加会员相关
    receivableDTO.setMemberBalancePay(NumberUtil.doubleVal(accountInfoDTO.getMemberAmount()));
    receivableDTO.setCash(NumberUtil.doubleVal(accountInfoDTO.getCashAmount()));  //现金
    receivableDTO.setBankCard(NumberUtil.doubleVal(accountInfoDTO.getBankAmount()));    //银行卡
    receivableDTO.setCheque(NumberUtil.doubleVal(accountInfoDTO.getBankCheckAmount()));    //支票
    return receivableDTO;

  }

  /**
   * 生成流水记录
   *
   * @param salesOrderDTO
   * @param accountInfoDTO
   * @param receivableDTO
   * @return
   */
  public ReceivableDTO getReceptionRecordFromAccountInfoDTO(SalesOrderDTO salesOrderDTO, AccountInfoDTO accountInfoDTO, ReceivableDTO receivableDTO) {
    ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
    receptionRecordDTO.setDayType(DayType.OTHER_DAY);
    receptionRecordDTO.setAmount(receivableDTO.getSettledAmount());
    receptionRecordDTO.setOrderTotal(salesOrderDTO.getTotal());
    receptionRecordDTO.setMemberBalancePay(NumberUtil.doubleVal(accountInfoDTO.getMemberAmount()));
    receptionRecordDTO.setChequeNo(accountInfoDTO.getBankCheckNo());

    receptionRecordDTO.setCash(NumberUtil.doubleVal(accountInfoDTO.getCashAmount()));
    receptionRecordDTO.setBankCard(NumberUtil.doubleVal(accountInfoDTO.getBankAmount()));
    receptionRecordDTO.setCheque(NumberUtil.doubleVal(accountInfoDTO.getBankCheckAmount()));
    receptionRecordDTO.setMemberId(receivableDTO.getMemberId());
    receptionRecordDTO.setRecordNum(0);
    receptionRecordDTO.setOriginDebt(0d);
    receptionRecordDTO.setDiscount(receivableDTO.getDiscount());
    receptionRecordDTO.setRemainDebt(receivableDTO.getDebt());

    receptionRecordDTO.setShopId(salesOrderDTO.getShopId());
    receptionRecordDTO.setOrderId(salesOrderDTO.getId());
    receptionRecordDTO.setReceptionDate(System.currentTimeMillis());
    receptionRecordDTO.setOrderTypeEnum(OrderTypes.SALE);
    receptionRecordDTO.setPayee(salesOrderDTO.getUserName());
    receptionRecordDTO.setPayeeId(salesOrderDTO.getUserId());
//    if (accountInfoDTO.getAccountDebtAmount() > 0) {
//      receptionRecordDTO.setOrderStatusEnum(OrderStatus.SALE_DEBT_DONE);
//    } else {
    receptionRecordDTO.setOrderStatusEnum(OrderStatus.SALE_DONE);
//    }
    ReceptionRecordDTO[] receptionRecordDTOs = new ReceptionRecordDTO[1];
    receptionRecordDTOs[0] = receptionRecordDTO;
    receivableDTO.setRecordDTOs(receptionRecordDTOs);
    return receivableDTO;
  }

  /**
   * 生成欠款记录
   *
   * @param salesOrderDTO
   * @param accountInfoDTO
   * @param receivableDTO
   * @param writer
   * @throws Exception
   */
  public void saveDebtFromAccountInfoDTO(SalesOrderDTO salesOrderDTO, AccountInfoDTO accountInfoDTO, ReceivableDTO receivableDTO, TxnWriter writer) throws Exception {
    //添加欠款信息
    IProductService productService = ServiceManager.getService(IProductService.class);
    if (accountInfoDTO.getAccountDebtAmount() > 0) {
      Long payTime = salesOrderDTO.getVestDate();
      Long remindTime = StringUtils.isNotBlank(accountInfoDTO.getHuankuanTime()) ? DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, accountInfoDTO.getHuankuanTime()) : null;
      StringBuffer materials = new StringBuffer();
      if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
        for (int i = 0; i < salesOrderDTO.getItemDTOs().length; i++) {
          if (salesOrderDTO.getItemDTOs()[i].getProductId() != null) {
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(salesOrderDTO.getItemDTOs()[i].getProductId(), salesOrderDTO.getShopId());
            if (productDTO != null) {
              if (i == salesOrderDTO.getItemDTOs().length - 1) {
                materials.append(productDTO.getName());
              } else {
                materials.append(productDTO.getName()).append(",");
              }
            }
          }
        }
      }
      Debt debt = new Debt();
      debt.setOrderTypeEnum(OrderTypes.SALE);
      debt.setContent(BcgogoI18N.getMessageByKey("debt.type.sales"));
      debt.setCustomerId(salesOrderDTO.getCustomerId());
      debt.setDebt(receivableDTO.getDebt());
      debt.setMaterial(materials.toString());
      debt.setService(" ");
      debt.setOrderId(salesOrderDTO.getId());
      debt.setOrderTime(salesOrderDTO.getEditDate());
      debt.setRecievableId(receivableDTO.getId());
      debt.setSettledAmount(receivableDTO.getSettledAmount());
      debt.setShopId(salesOrderDTO.getShopId());
      debt.setTotalAmount(salesOrderDTO.getTotal());
      debt.setVehicleNumber(" ");
      debt.setPayTime(payTime);
      debt.setRemindTime(remindTime);
      debt.setOrderTime(salesOrderDTO.getVestDate());
      debt.setStatusEnum(DebtStatus.ARREARS);
      debt.setReceiptNo(salesOrderDTO.getReceiptNo());
      debt.setRemindStatus(UserConstant.Status.ACTIVITY);
      writer.save(debt);
      getTxnService().saveRemindEvent(writer, debt, salesOrderDTO.getCustomer(), salesOrderDTO.getMobile());
      //更新缓存
      getTxnService().updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, debt.getShopId());
    }
  }

  //对店铺的product重建索引
  public void reindexCustomerProductSolr(SalesOrderDTO salesOrderDTO) throws Exception {
    if (salesOrderDTO == null || salesOrderDTO.getPurchaseOrderId() == null) {
      return;
    }
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    CustomerDTO customerDTO = customerService.getCustomerById(salesOrderDTO.getCustomerId());
    if (customerDTO == null) {
      return;
    }
    List<Long> productIds = new ArrayList<Long>();
    PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrder(salesOrderDTO.getPurchaseOrderId(), customerDTO.getCustomerShopId());
    if (purchaseOrderDTO == null || ArrayUtil.isEmpty(salesOrderDTO.getItemDTOs())) {
      return;
    }
    for (PurchaseOrderItemDTO orderItemDTO : purchaseOrderDTO.getItemDTOs()) {
      productIds.add(orderItemDTO.getProductId());
    }
    ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(customerDTO.getCustomerShopId(), productIds.toArray(new Long[productIds.size()]));

  }

  @Override
  public Map<Long, List<SalesOrderItem>> getLackSalesOrderItemByProductIds(Long shopId, Long firstSalesOrderId, Long... productIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesOrderItem> salesOrderItems = writer.getLackSalesOrderItemByProductIds(shopId, productIds);
    return generateSalesOrderItemListMap(firstSalesOrderId, salesOrderItems);
  }

  @Override
  public Map<Long, List<SalesOrderItem>> getLackSalesOrderItemByProductIdsAndStorehouse(Long shopId, Long firstSalesOrderId, Long storehouseId, Long... productIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesOrderItem> salesOrderItems = writer.getLackSalesOrderItemByProductIdsAndStorehouse(shopId, storehouseId, productIds);
    return generateSalesOrderItemListMap(firstSalesOrderId, salesOrderItems);
  }

  private Map<Long, List<SalesOrderItem>> generateSalesOrderItemListMap(Long firstSalesOrderId, List<SalesOrderItem> salesOrderItems) {
    Map<Long, List<SalesOrderItem>> salesOrderItemMap = new HashMap<Long, List<SalesOrderItem>>();
    if (CollectionUtils.isNotEmpty(salesOrderItems)) {
      //把优先的saleorderItem 拿出来
      if (firstSalesOrderId != null) {
        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          if (salesOrderItem.getSalesOrderId().longValue() != firstSalesOrderId.longValue()) {
            continue;
          }
          generateSalesOrderItemMap(salesOrderItemMap, salesOrderItem);
        }
        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          if (salesOrderItem.getSalesOrderId().longValue() == firstSalesOrderId.longValue()) {
            continue;
          }
          generateSalesOrderItemMap(salesOrderItemMap, salesOrderItem);
        }
      } else {
        for (SalesOrderItem salesOrderItem : salesOrderItems) {
          generateSalesOrderItemMap(salesOrderItemMap, salesOrderItem);
        }
      }
    }
    return salesOrderItemMap;
  }

  @Override
  public Map<Long, List<SalesOrderItem>> getSalesOrderItemsByOrderId(Long saleOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesOrderItem> salesOrderItems = writer.getSalesOrderItemsByOrderId(saleOrderId);
    Map<Long, List<SalesOrderItem>> salesOrderItemMap = new HashMap<Long, List<SalesOrderItem>>();
    if (CollectionUtils.isNotEmpty(salesOrderItems)) {
      for (SalesOrderItem salesOrderItem : salesOrderItems) {
        generateSalesOrderItemMap(salesOrderItemMap, salesOrderItem);
      }
    }
    return salesOrderItemMap;
  }

  private void generateSalesOrderItemMap(Map<Long, List<SalesOrderItem>> salesOrderItemMap, SalesOrderItem salesOrderItem) {
    List<SalesOrderItem> salesOrderItemList = salesOrderItemMap.get(salesOrderItem.getProductId());
    if (CollectionUtils.isEmpty(salesOrderItemList)) {
      List<SalesOrderItem> newSaleOrderItems = new ArrayList<SalesOrderItem>();
      newSaleOrderItems.add(salesOrderItem);
      salesOrderItemMap.put(salesOrderItem.getProductId(), newSaleOrderItems);
    } else {
      salesOrderItemList.add(salesOrderItem);
      salesOrderItemMap.put(salesOrderItem.getProductId(), salesOrderItemList);
    }
  }

  @Override
  public Result validateCopy(Long salesOrderId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderId, shopId);
    SalesOrder salesOrder = CollectionUtil.getFirst(salesOrders);
    if (salesOrder == null) {
      return new Result("无法复制", "单据不存在，无法复制！", false, Result.Operation.ALERT);
    }
    SalesOrderDTO salesOrderDTO = salesOrder.toDTO();
    CustomerDTO customerDTO = salesOrderDTO.generateCustomerDTO();
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    boolean customerSame = customerService.compareCustomerSameWithHistory(customerDTO, shopId);
    List<SalesOrderItem> items = writer.getSalesOrderItemsByOrderId(salesOrderId);
    Map<Long, Long> localInfoIdAndHistoryIdMap = new HashMap<Long, Long>();
    if (CollectionUtils.isNotEmpty(items)) {
      for (SalesOrderItem item : items) {
        localInfoIdAndHistoryIdMap.put(item.getProductId(), item.getProductHistoryId());
      }
    }
    boolean productSame = productHistoryService.compareProductSameWithHistory(localInfoIdAndHistoryIdMap, shopId);
    if (customerSame && productSame) {
      return new Result("通过校验", true);
    } else if (customerSame) {
      return new Result("提示", "此单据中的商品信息已被修改，请确认是否继续复制。<br/><br/>如果继续，已被修改过的商品将不会被复制。", false, Result.Operation.CONFIRM);
    } else if (productSame) {
      return new Result("提示", "此单据中的客户信息已被修改，请确认是否继续复制。<br/><br/>如果继续，客户信息将不会被复制。", false, Result.Operation.CONFIRM);
    } else {
      return new Result("提示", "此单据中的客户信息与商品信息已被修改，请确认是否继续复制。<br/><br/>如果继续，客户信息与已被修改过的商品将不会被复制。", false, Result.Operation.CONFIRM);
    }
  }

  @Override
  public SalesOrderDTO createOrUpdateSalesOrder(SalesOrderDTO salesOrderDTO, String huankuanTime) throws Exception {
    StopWatchUtil sw = new StopWatchUtil("createOrUpdateSalesOrder", "start");
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);

    SalesOrderItemDTO[] itemDTOs = salesOrderDTO.getItemDTOs();
    if (itemDTOs == null || itemDTOs.length == 0) return salesOrderDTO;
    TxnWriter writer = txnDaoManager.getWriter();
    sw.stopAndStart("step1");

    Object status = writer.begin();
    try {
      //微型版自动补充库存
      addInventoryForSpecialShopVersion(salesOrderDTO, salesOrderDTO.getShopId(), salesOrderDTO.getShopVersionId(), writer);

      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      Set<Long> productIds = salesOrderDTO.getProductIdSet();
      Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(salesOrderDTO.getShopId(), productIds);
      Map<Long, Inventory> inventoryMap = getInventoryService().getInventoryMap(salesOrderDTO.getShopId(), productIds);
      Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(
          salesOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
      Map<Long, ProductHistory> productHistoryMap = new HashMap<Long, ProductHistory>();//key 是productLocalInfoId


      repairOrderCostCaculator.calculate(salesOrderDTO, inventoryMap, productLocalInfoDTOMap);
      SalesOrder order = null;
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())) {
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(salesOrderDTO.getShopId(), salesOrderDTO.getStorehouseId());
        salesOrderDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
      }
       //在线单据，则设置customerShopId
       if(salesOrderDTO.getPurchaseOrderId() != null) {
          PurchaseOrderDTO purchaseOrderDTO = ServiceManager.getService(ITxnService.class).getPurchaseOrderById(salesOrderDTO.getPurchaseOrderId());
          if(purchaseOrderDTO != null) {
            salesOrderDTO.setCustomerShopId(purchaseOrderDTO.getShopId());
          }
       }
      if (null == salesOrderDTO.getId() || salesOrderDTO.getId() == 0) {
        order = new SalesOrder();
        order.fromDTO(salesOrderDTO);
        order.setStatusEnum(OrderStatus.SALE_DONE);
        salesOrderDTO.setStatus(OrderStatus.SALE_DONE);
        writer.save(order);
        salesOrderDTO.setId(order.getId());
      } else {
        order = writer.getById(SalesOrder.class, salesOrderDTO.getId());
        order.fromDTO(salesOrderDTO);
        order.setStatusEnum(OrderStatus.SALE_DONE);
        salesOrderDTO.setStatus(OrderStatus.SALE_DONE);
        writer.update(order);
      }
      sw.stopAndStart("step_r");
      //结算
      postProcessingForSavingSaleOrder(writer, salesOrderDTO, huankuanTime);

      sw.stopAndStart("step_s");
      for (SalesOrderItemDTO salesOrderItemDTO : itemDTOs) {
        if (salesOrderItemDTO.getProductId() != null) {
          ProductDTO productDTO = productDTOMap.get(salesOrderItemDTO.getProductId());


          Inventory inventory = inventoryMap.get(salesOrderItemDTO.getProductId());
          ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItemDTO.getProductId());
          if (salesOrderItemDTO.getBusinessCategoryId() != null && productLocalInfoDTO != null
              && StringUtil.isNotEmpty(salesOrderItemDTO.getBusinessCategoryName())) {
            productLocalInfoDTO.setBusinessCategoryName(salesOrderItemDTO.getBusinessCategoryName());
          }
          ProductHistory productHistory = new ProductHistory();
          productHistory.setInventoryDTO(inventory == null ? null : inventory.toDTO());
          productHistory.setProductDTO(productDTO);
          productHistory.setProductLocalInfoDTO(productLocalInfoDTO);
          productHistoryMap.put(salesOrderItemDTO.getProductId(), productHistory);
        }
      }
      sw.stopAndStart("step_s_1");
      productHistoryService.batchSaveProductHistory(productHistoryMap, writer);
      sw.stopAndStart("step_e");
      for (SalesOrderItemDTO itemDTO : itemDTOs) {
        ProductHistory productHistory = productHistoryMap.get(itemDTO.getProductId());
        if (productHistory != null) {
          itemDTO.setProductHistoryId(productHistory.getId());
        }
        Long newSalesOrderItemId = null;
        if (null == itemDTO.getId() || itemDTO.getId() == 0) {
          SalesOrderItem item = new SalesOrderItem();
          if (itemDTO.getProductId() == null) {
            LOG.error("product id is null for when saving sales order");
            continue;
          }
          item.setShopId(salesOrderDTO.getShopId());
          item.setAmount(itemDTO.getAmount());
          item.setMemo(itemDTO.getMemo());
          item.setPrice(itemDTO.getPrice());
          item.setProductId(itemDTO.getProductId());
          item.setProductHistoryId(itemDTO.getProductHistoryId());
          item.setTotal(itemDTO.getTotal());
          item.setQuotedPrice(itemDTO.getQuotedPrice());
          item.setSalesOrderId(order.getId());
          item.setCostPrice(NumberUtil.doubleVal(itemDTO.getCostPrice()));
          item.setTotalCostPrice(NumberUtil.doubleVal(itemDTO.getTotalCostPrice()));
          item.setUnit(itemDTO.getUnit());
          item.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
          item.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
          writer.save(item);
          newSalesOrderItemId = item.getId();
        } else {
          SalesOrderItem salesOrderItem = writer.getById(SalesOrderItem.class, itemDTO.getId());
          salesOrderItem.fromDTO(itemDTO);
          salesOrderItem.setSalesOrderId(order.getId());
          salesOrderItem.setShopId(salesOrderDTO.getShopId());
          salesOrderItem.setCostPrice(NumberUtil.doubleVal(itemDTO.getCostPrice()));
          salesOrderItem.setTotalCostPrice(NumberUtil.doubleVal(itemDTO.getTotalCostPrice()));
          salesOrderItem.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
          salesOrderItem.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
          writer.update(salesOrderItem);
          itemDTO.setCostPrice(salesOrderItem.getCostPrice());
          itemDTO.setTotalCostPrice(salesOrderItem.getTotalCostPrice());
          newSalesOrderItemId = salesOrderItem.getId();
        }
        itemDTO.setId(newSalesOrderItemId);
        itemDTO.setSalesOrderId(order.getId());
        //save inventory    getPurchasePrice check the unit

        Inventory inventory = inventoryMap.get(itemDTO.getProductId());
        iInventoryService.caculateBeforeLimit(inventory.toDTO(), salesOrderDTO.getInventoryLimitDTO());
        ProductDTO productDTO = productDTOMap.get(itemDTO.getProductId());
        Double saleOrderItemAmout = 0d;
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {   //销售单单位是库存大单位
          saleOrderItemAmout = itemDTO.getAmount() * productDTO.getRate();
        } else {
          saleOrderItemAmout = itemDTO.getAmount();
        }
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(salesOrderDTO.getStorehouseId(), itemDTO.getProductId(), null, saleOrderItemAmout * -1);
          storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
        }
        if (inventory != null) {
          inventory.setAmount(inventory.getAmount() - saleOrderItemAmout);
          if ((salesOrderDTO.getStatus() == OrderStatus.SALE_DONE || salesOrderDTO.getStatus() == OrderStatus.SALE_DEBT_DONE)
              && NumberUtil.longValue(inventory.getLastSalesTime()) < salesOrderDTO.getVestDate()) {
            inventory.setLastSalesTime(salesOrderDTO.getVestDate());
          }
          writer.update(inventory);

          iInventoryService.caculateAfterLimit(inventory.toDTO(), salesOrderDTO.getInventoryLimitDTO());
        } else {
          LOG.error("cannot find inventory: product id = " + itemDTO.getProductId());
        }
        productDTO.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, salesOrderDTO.getVestDateStr()));
        InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
        inventorySearchIndex.createInventorySearchIndex(inventory.toDTO(), productDTO);
        inventorySearchIndexList.add(inventorySearchIndex);
      }


      //此处已过滤空行
      if (CollectionUtils.isNotEmpty(salesOrderDTO.getOtherIncomeItemDTOList())) {
        for (SalesOrderOtherIncomeItemDTO orderOtherIncomeItemDTO : salesOrderDTO.getOtherIncomeItemDTOList()) {
          if (null == orderOtherIncomeItemDTO.getId()) {
            SalesOrderOtherIncomeItem salesOrderOtherIncomeItem = new SalesOrderOtherIncomeItem(orderOtherIncomeItemDTO);
            salesOrderOtherIncomeItem.setShopId(salesOrderDTO.getShopId());
            salesOrderOtherIncomeItem.setOrderId(salesOrderDTO.getId());
            writer.save(salesOrderOtherIncomeItem);
            orderOtherIncomeItemDTO.setId(salesOrderOtherIncomeItem.getId());
            orderOtherIncomeItemDTO.setShopId(salesOrderDTO.getShopId());
          } else {
            SalesOrderOtherIncomeItem salesOrderOtherIncomeItem = writer.getById(SalesOrderOtherIncomeItem.class, orderOtherIncomeItemDTO.getId());
            salesOrderOtherIncomeItem.setMemo(orderOtherIncomeItemDTO.getMemo());
            salesOrderOtherIncomeItem.setName(orderOtherIncomeItemDTO.getName());
            salesOrderOtherIncomeItem.setPrice(orderOtherIncomeItemDTO.getPrice());
            writer.update(salesOrderOtherIncomeItem);
            orderOtherIncomeItemDTO.setShopId(salesOrderDTO.getShopId());
          }

        }
      }
      sw.stopAndStart("step4");
      getProductOutStorageService().productThroughByOrder(salesOrderDTO, OrderTypes.SALE, salesOrderDTO.getStatus(), writer, null);
      writer.commit(status);
      ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(salesOrderDTO.getShopId(),inventorySearchIndexList);
      salesOrderDTO.setId(order.getId());
      sw.stopAndPrintLog();
      return salesOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }


  //为初级版添加销售库存
  private void addInventoryForSpecialShopVersion(SalesOrderDTO salesOrderDTO, Long shopId, Long shopVersionId, TxnWriter writer) throws Exception {
    if (shopVersionId == null || shopId == null || !BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId) || salesOrderDTO == null) {
      return;
    }
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    SalesOrderItemDTO[] itemDTOs = salesOrderDTO.getItemDTOs();
    if (itemDTOs == null) {
      return;
    }
    Set<Long> productIds = salesOrderDTO.getProductIdSet();
    Map<Long, Inventory> inventoryMap = getInventoryService().getInventoryMap(shopId, productIds);
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(shopId, productIds.toArray(new Long[productIds.size()]));
    for (SalesOrderItemDTO itemDTO : itemDTOs) {
      if (itemDTO == null || StringUtils.isBlank(itemDTO.getProductName()) || itemDTO.getProductId() == null) {
        continue;
      }
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(itemDTO.getProductId());
      double supplierInventoryAmountChange = 0d;
      String supplierInventoryUnit = null;
      Inventory inventory = inventoryMap.get(itemDTO.getProductId());
      if (inventory == null) {
        inventory = new Inventory();
        inventory.setId(itemDTO.getProductId());
        inventory.setShopId(shopId);
        inventory.setAmount(itemDTO.getAmount());
        inventory.setNoOrderInventory(itemDTO.getAmount());
        inventory.setUnit(itemDTO.getUnit());
        writer.save(inventory);
        inventoryMap.put(itemDTO.getProductId(), inventory);
        supplierInventoryAmountChange = NumberUtil.doubleVal(itemDTO.getAmount());
        supplierInventoryUnit = itemDTO.getUnit();
      } else {
        double amount = itemDTO.getAmount();
        double inventoryAmount = inventory.getAmount();
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }
        if (amount > inventoryAmount - 0.0001) {
          double noOrderInventoryAmount = amount - inventoryAmount;
          supplierInventoryAmountChange = noOrderInventoryAmount;
          supplierInventoryUnit = inventory.getUnit();
          inventory.setAmount(amount);
          inventory.setNoOrderInventory(NumberUtil.doubleVal(inventory.getNoOrderInventory()) + amount - inventoryAmount);
          writer.update(inventory);
        }
      }
      if (supplierInventoryAmountChange > 0.0001) {
        SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
        supplierInventoryDTO.setShopId(shopId);
        supplierInventoryDTO.setProductId(itemDTO.getProductId());
        supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        supplierInventoryDTO.setChangeAmount(supplierInventoryAmountChange);
        supplierInventoryDTO.setUnit(supplierInventoryUnit);
        supplierInventoryDTOs.add(supplierInventoryDTO);
      }
    }
    if (CollectionUtils.isNotEmpty(supplierInventoryDTOs)) {
      productThroughService.saveOrUpdateSupplierInventory(writer, supplierInventoryDTOs);
    }
  }


  @Override
  public void batchSaveCategory(SalesOrderDTO salesOrderDTO) {
    if (salesOrderDTO != null && !ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      Set<String> categoryNames = salesOrderDTO.getCategoryNames();
      Map<String, CategoryDTO> categoryDTOMap = getRfTxnService().batchSaveAndGetCateGory(salesOrderDTO.getShopId(), categoryNames);
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        if (salesOrderItemDTO == null || salesOrderItemDTO.getProductId() == null) {
          continue;
        }
        if (StringUtils.isNotBlank(salesOrderItemDTO.getBusinessCategoryName())) {
          CategoryDTO categoryDTO = categoryDTOMap.get(salesOrderItemDTO.getBusinessCategoryName());
          if (categoryDTO != null) {
            salesOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
          }
        }
      }
    }
  }


  private void postProcessingForSavingSaleOrder(TxnWriter writer, SalesOrderDTO salesOrderDTO, String huankuanTime) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long remindTime = StringUtils.isNotBlank(huankuanTime) ? DateUtil.convertDateStringToDateLong("yyyy-MM-dd", huankuanTime) : null;
    Long shopId = salesOrderDTO.getShopId();
    ReceivableDTO receivableDTO = new ReceivableDTO();
    receivableDTO.setOrderType(OrderTypes.SALE);
    receivableDTO.setStatus(ReceivableStatus.FINISH);
    receivableDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
    receivableDTO.setRemindTime(remindTime);

    receivableDTO.setOrderId(salesOrderDTO.getId());
    receivableDTO.setShopId(shopId);
    receivableDTO.setSettledAmount(salesOrderDTO.getSettledAmount());
    receivableDTO.setDebt(salesOrderDTO.getDebt());
    receivableDTO.setDiscount(salesOrderDTO.getOrderDiscount());
    //实收表 销售单没有存total
    receivableDTO.setTotal(salesOrderDTO.getTotal());
    receivableDTO.setAfterMemberDiscountTotal(salesOrderDTO.getAfterMemberDiscountTotal());
    receivableDTO.setMemberDiscountRatio(salesOrderDTO.getMemberDiscountRatio());
    receivableDTO.setLastPayee(salesOrderDTO.getUserName());
    receivableDTO.setLastPayeeId(salesOrderDTO.getUserId());
    receivableDTO.setCustomerId(salesOrderDTO.getCustomerId());
    receivableDTO.setVestDate(salesOrderDTO.getVestDate());
    receivableDTO.setReceiptNo(salesOrderDTO.getReceiptNo());
    receivableDTO.setBankCheckNo(salesOrderDTO.getBankCheckNo());
    //如果含有会员结算信息 把member_id保存到receivable表中
    if (NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()) > 0 || null != receivableDTO.getMemberDiscountRatio()) {
      String memberNo = salesOrderDTO.getAccountMemberNo();
      Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
      if (member == null) {
        LOG.error("/TxnService.java method=postProcessingForSavingSaleOrder");
        LOG.error("shopId:" + shopId + ",memberNo:" + memberNo);
        LOG.error("会员查询出错");
      } else {
        receivableDTO.setMemberId(member.getId());
        receivableDTO.setMemberNo(member.getMemberNo());
      }
    }

    //添加会员相关
    receivableDTO.setMemberBalancePay(NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()));
    receivableDTO.setCash(NumberUtil.doubleVal(salesOrderDTO.getCashAmount()));  //现金
    receivableDTO.setBankCard(NumberUtil.doubleVal(salesOrderDTO.getBankAmount()));    //银行卡
    receivableDTO.setCheque(NumberUtil.doubleVal(salesOrderDTO.getBankCheckAmount()));    //支票
    receivableDTO.setDeposit(NumberUtil.doubleVal(salesOrderDTO.getCustomerDeposit())); // add by zhj


    ReceivableHistoryDTO receivableHistoryDTO = receivableDTO.toReceivableHistoryDTO();
    ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
    writer.save(receivableHistory);
    receivableHistoryDTO.setId(receivableHistory.getId());


    if (salesOrderDTO.getSettledAmount() >= 0) {
      ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
      receptionRecordDTO.setReceivableHistoryId(receivableHistory.getId());
      receptionRecordDTO.setDayType(DayType.OTHER_DAY);
      receptionRecordDTO.setAmount(salesOrderDTO.getSettledAmount());
      receptionRecordDTO.setReceivableId(receivableDTO.getId());
      receptionRecordDTO.setOrderTotal(salesOrderDTO.getTotal());
      receptionRecordDTO.setMemberBalancePay(NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()));
      receptionRecordDTO.setChequeNo(salesOrderDTO.getBankCheckNo());
      receptionRecordDTO.setDeposit(NumberUtil.doubleVal(salesOrderDTO.getCustomerDeposit())); //add by zhuj
      receptionRecordDTO.setCash(NumberUtil.doubleVal(salesOrderDTO.getCashAmount()));
      receptionRecordDTO.setBankCard(NumberUtil.doubleVal(salesOrderDTO.getBankAmount()));
      receptionRecordDTO.setCheque(NumberUtil.doubleVal(salesOrderDTO.getBankCheckAmount()));
      receptionRecordDTO.setMemberId(receivableDTO.getMemberId());
      receptionRecordDTO.setRecordNum(0);
      receptionRecordDTO.setOriginDebt(0d);
      receptionRecordDTO.setDiscount(salesOrderDTO.getOrderDiscount());
      receptionRecordDTO.setRemainDebt(salesOrderDTO.getDebt());
      receptionRecordDTO.setToPayTime(remindTime);

      receptionRecordDTO.setShopId(salesOrderDTO.getShopId());
      receptionRecordDTO.setOrderId(salesOrderDTO.getId());
      if(StringUtil.isNotEmpty(salesOrderDTO.getAccountDateStr())){
        receptionRecordDTO.setReceptionDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,salesOrderDTO.getAccountDateStr()));
      }else{
        receptionRecordDTO.setReceptionDate(System.currentTimeMillis());
      }
      receptionRecordDTO.setOrderTypeEnum(OrderTypes.SALE);
      receptionRecordDTO.setPayee(salesOrderDTO.getUserName());
      receptionRecordDTO.setPayeeId(salesOrderDTO.getUserId());
      //      if(NumberUtil.doubleVal(salesOrderDTO.getDebt()) > 0) {
      //        receptionRecordDTO.setOrderStatusEnum(OrderStatus.SALE_DEBT_DONE);
      //      }else {
      receptionRecordDTO.setOrderStatusEnum(OrderStatus.SALE_DONE);
      //      }
      receptionRecordDTO.setAfterMemberDiscountTotal(salesOrderDTO.getAfterMemberDiscountTotal());
      receptionRecordDTO.setMemberDiscountRatio(salesOrderDTO.getMemberDiscountRatio());
      ReceptionRecordDTO[] receptionRecordDTOs = new ReceptionRecordDTO[1];
      receptionRecordDTOs[0] = receptionRecordDTO;
      receivableDTO.setRecordDTOs(receptionRecordDTOs);
    }
    getTxnService().createOrUpdateReceivable(writer, receivableDTO);
    if (receivableDTO.getDeposit() > 0.001) {
      ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
      CustomerDepositDTO customerDepositDTO = new CustomerDepositDTO();
      customerDepositDTO.setOperator(salesOrderDTO.getUserName());
      customerDepositDTO.setShopId(salesOrderDTO.getShopId());
      customerDepositDTO.setActuallyPaid(receivableDTO.getDeposit());
      customerDepositDTO.setCustomerId(receivableDTO.getCustomerId());
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      //基础字段在service方法中有填充
      depositOrderDTO.setDepositType(DepositType.SALES.getScene());
      depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
      depositOrderDTO.setRelatedOrderId(salesOrderDTO.getId()); // 记录销售单id
      depositOrderDTO.setRelatedOrderNo(salesOrderDTO.getReceiptNo()); // 记录销售单按单据号

      customerDepositService.customerDepositUse(customerDepositDTO, depositOrderDTO, writer);
    }

    //添加欠款信息
    if (salesOrderDTO.getDebt() > 0.001) {
      Long payTime = salesOrderDTO.getVestDate();
      StringBuffer materials = new StringBuffer();
      for (int i = 0; i < salesOrderDTO.getItemDTOs().length; i++) {
        if (salesOrderDTO.getItemDTOs()[i].getProductId() != null) {
          materials.append(salesOrderDTO.getItemDTOs()[i].getProductName()).append(",");
        } else {
          materials.append(salesOrderDTO.getItemDTOs()[i].getProductName());
        }
      }
      Debt debt = new Debt();
      debt.setOrderTypeEnum(OrderTypes.SALE);
      debt.setContent(BcgogoI18N.getMessageByKey("debt.type.sales"));
      debt.setCustomerId(salesOrderDTO.getCustomerId());
      debt.setDebt(receivableDTO.getDebt());
      debt.setMaterial(materials.toString());
      debt.setService(" ");
      debt.setOrderId(salesOrderDTO.getId());
      debt.setOrderTime(salesOrderDTO.getEditDate());
      debt.setRecievableId(receivableDTO.getId());
      debt.setSettledAmount(salesOrderDTO.getSettledAmount());
      debt.setShopId(shopId);
      debt.setTotalAmount(salesOrderDTO.getTotal());
      debt.setVehicleNumber(" ");
      debt.setPayTime(payTime);
      debt.setRemindTime(remindTime);
      debt.setStatusEnum(DebtStatus.ARREARS);
      debt.setReceiptNo(salesOrderDTO.getReceiptNo());
      debt.setRemindStatus(UserConstant.Status.ACTIVITY);
      writer.save(debt);
      //更新remind_event的deleted_type
      writer.updateDebtRemindDeletedType(shopId,salesOrderDTO.getCustomerId(),"customer",DeletedType.FALSE);
      // add by WLF 在提醒总表中保存提醒
      getTxnService().saveRemindEvent(writer, debt, salesOrderDTO.getCustomer(), salesOrderDTO.getMobile());


    }

  }


  /**
   * 销售单 所填车辆若为新车型，则新增此车辆，并将ID保存到此销售单
   *
   * @param salesOrderDTO
   * @return
   * @throws Exception
   * @author wjl
   */
  @Override
  public void populateSalesOrderDTO(SalesOrderDTO salesOrderDTO) throws Exception {
    if (salesOrderDTO == null) {
      return;
    }
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        if ((salesOrderItemDTO.getVehicleBrandId() == null || salesOrderItemDTO.getVehicleModelId() == null
            || salesOrderItemDTO.getVehicleYearId() == null || salesOrderItemDTO.getVehicleEngineId() == null)
            && (StringUtils.isNotBlank(salesOrderItemDTO.getVehicleBrand()))) {
          VehicleDTO vehicleDTO = baseProductService.addVehicleToDB(salesOrderItemDTO.getVehicleBrand(), salesOrderItemDTO.getVehicleModel(),
              salesOrderItemDTO.getVehicleYear(), salesOrderItemDTO.getVehicleEngine());
          salesOrderItemDTO.setVehicleBrandId(vehicleDTO.getVirtualBrandId());
          salesOrderItemDTO.setVehicleModelId(vehicleDTO.getVirtualModelId());
          salesOrderItemDTO.setVehicleYearId(vehicleDTO.getVirtualYearId());
          salesOrderItemDTO.setVehicleEngineId(vehicleDTO.getVirtualEngineId());
          salesOrderItemDTO.setAddVehicleLicenceNoToSolr(true);
        }
      }
    }
  }


  @Override
  public void saveOrUpdateProductForSaleOrder(SalesOrderDTO salesOrderDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = salesOrderDTO.getShopId();
    //新增或者更新产品信息
    List<ProductDTO> toUpdateProductDTOs = new ArrayList<ProductDTO>();
    Set<Long> productIds = new HashSet<Long>();
    List<SalesOrderItemDTO> toAddUnitItemDTOs = new ArrayList<SalesOrderItemDTO>();
    Set<Long> deletedProductIds = new HashSet<Long>();
    //1.没有productId的item去根据6字段匹配取出product表的id  todo by qxy 优化方案可以批量查询
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
      if (StringUtils.isBlank(salesOrderItemDTO.getProductName())) {
        continue;
      }
      if (BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(salesOrderDTO.getShopVersionId()) &&
          salesOrderDTO.getShopId() != null && salesOrderItemDTO.getProductId() == null) {
        ProductDTO productSearchCondition = new ProductDTO(salesOrderDTO.getShopId(), salesOrderItemDTO);
        List<ProductDTO> productDTOs = productService.getProductDTOsBy7P(salesOrderDTO.getShopId(), productSearchCondition);
        ProductDTO productDTO = CollectionUtil.getFirst(productDTOs);
        if (productDTO != null) {
          salesOrderItemDTO.setProductOriginId(productDTO.getId());
          productIds.add(productDTO.getId());
        }
      }
    }
    //根据productId取出productLocalInfoId
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMapByProductIds(shopId, productIds);
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
      if (StringUtils.isBlank(salesOrderItemDTO.getProductName())) {
        continue;
      }
      if (salesOrderItemDTO.getProductOriginId() != null) {
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItemDTO.getProductOriginId());
        if (productLocalInfoDTO != null) {
          salesOrderItemDTO.setProductId(productLocalInfoDTO.getId());
        }
      }
    }
    //新增产品  初级版 新商品直接销售
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
      if (StringUtils.isBlank(salesOrderItemDTO.getProductName())) {
        continue;
      }
      boolean isNewProductFlag = false;
      ProductDTO productDTO = new ProductDTO(salesOrderDTO.getShopId(), salesOrderItemDTO);
      productDTO.setProductLocalInfoId(salesOrderItemDTO.getProductId());
      if (salesOrderItemDTO.getProductId() == null &&
          BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(salesOrderDTO.getShopVersionId())) {
        isNewProductFlag = productService.saveNewProduct(productDTO);
        salesOrderItemDTO.setProductOriginId(productDTO.getId());
        salesOrderItemDTO.setProductId(productDTO.getProductLocalInfoId());
      }
      if (!isNewProductFlag) {
        toUpdateProductDTOs.add(productDTO);
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(salesOrderItemDTO.getProductOriginId());
        //需要添加单位的item
        if (StringUtils.isNotBlank(salesOrderItemDTO.getUnit()) && productLocalInfoDTO != null
            && StringUtils.isBlank(productLocalInfoDTO.getSellUnit())) {
          toAddUnitItemDTOs.add(salesOrderItemDTO);
        }
      }
    }
    //更新产品信息
    if (CollectionUtils.isNotEmpty(toUpdateProductDTOs)) {
      productService.updateProductWithRepairOrder(salesOrderDTO.getShopId(), deletedProductIds,
          toUpdateProductDTOs.toArray(new ProductDTO[toUpdateProductDTOs.size()]));
    }
    //删除商品恢复重新计算上下限
    if (CollectionUtils.isNotEmpty(deletedProductIds)) {
      Map<Long, InventoryDTO> deleteInventoryDTOs = getInventoryService().getInventoryDTOMap(shopId, deletedProductIds);
      if (MapUtils.isNotEmpty(deleteInventoryDTOs)) {
        for (InventoryDTO inventoryDTO : deleteInventoryDTOs.values()) {
          getInventoryService().caculateAfterLimit(inventoryDTO, salesOrderDTO.getInventoryLimitDTO());
        }
      }
    }
    //更新销售单中原先库存存在且不带单位的商品的单位
    getTxnService().updateProductUnit(salesOrderDTO.getShopId(), toAddUnitItemDTOs.toArray(new RepairOrderItemDTO[toAddUnitItemDTOs.size()]));
  }

  @Override
  public SalesOrderDTO getSimpleSalesOrderById(Long salesOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    SalesOrder salesOrder = writer.getById(SalesOrder.class, salesOrderId);
    return salesOrder == null ? null : salesOrder.toDTO();
  }

  @Override
  public SalesOrderItemDTO getSalesOrderItemById(Long itemId) {
    if (itemId == null || itemId == 0L) {
      throw new RuntimeException("getSalesOrderItemByIdAndShopId,itemId is null.");
    }
    TxnWriter writer = txnDaoManager.getWriter();
    SalesOrderItem salesOrderItem = writer.getById(SalesOrderItem.class,itemId);
    if (salesOrderItem == null) {
      return null;
    }
    return salesOrderItem.toDTO();
  }


  @Override
  public List<SalesOrderDTO> getUnSettledSalesOrdersByCustomerId(Long shopId, Long customerId) {
    List<SalesOrderDTO> salesOrderDTOs = new ArrayList<SalesOrderDTO>();
    if (shopId == null || customerId == null) {
      return salesOrderDTOs;
    }
    List<SalesOrder> salesOrders = txnDaoManager.getWriter().getUnsettledSalesOrdersByCustomerId(shopId, customerId);
    for (SalesOrder salesOrder : salesOrders) {
      salesOrderDTOs.add(salesOrder.toDTO());
    }
    return salesOrderDTOs;
  }

  @Override
  public SalesOrderDTO generateSaleOrderDTOFromPurchase(PurchaseOrderDTO purchaseOrderDTO) {
    SalesOrderDTO salesOrderDTO = null;
    if(purchaseOrderDTO != null){
//      IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
      salesOrderDTO = new SalesOrderDTO();
      salesOrderDTO.setPurchaseOrderDTO(purchaseOrderDTO);
      Set<Long> supplierProductIds = purchaseOrderDTO.getSupplierProductIdsSet();
//      Set<Long> supplierProductHistoryIds = purchaseOrderDTO.getProductHistoryIds();

      Map<Long,ProductDTO> supplierProductDTOMap =  getProductService().getProductDTOMapByProductLocalInfoIds(supplierProductIds);
//      Map<Long,ProductHistoryDTO> productHistoryDTOMap =  productHistoryService.getProductHistoryDTOMapByProductHistoryIds(supplierProductHistoryIds);
      if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
        for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
          salesOrderItemDTO.setSalesOrderId(salesOrderDTO.getId());
          ProductDTO productDTO = supplierProductDTOMap.get(salesOrderItemDTO.getProductId());
          if (productDTO != null) {
            salesOrderItemDTO.setProductDTOWithOutUnit(productDTO);
          }
        }
      }

    }
    return salesOrderDTO;
  }

  @Override
  public SalesOrderDTO createOnlineSalesOrderDTO(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    if (purchaseOrderDTO == null || ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())) {
      LOG.error("采购单的Item为空，无法生成销售单");
      return null;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IShopRelationService shopRelationService = ServiceManager.getService(IShopRelationService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    Long supplierShopId = purchaseOrderDTO.getSupplierShopId();
    Long customerShopId = purchaseOrderDTO.getShopId();

    SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
    //保存新的销售单
    salesOrderDTO.setPurchaseOrderDTO(purchaseOrderDTO);
    if (StringUtils.isBlank(salesOrderDTO.getReceiptNo())) {
      salesOrderDTO.setReceiptNo(getTxnService().getReceiptNo(salesOrderDTO.getShopId(), OrderTypes.SALE, null));
    }
    CustomerDTO customerDTO = userService.getCustomerByCustomerShopIdAndShopId(supplierShopId, customerShopId);
    if (customerDTO == null) {
      ShopDTO customerShopDTO = configService.getShopById(purchaseOrderDTO.getShopId());
      ShopDTO supplierShopDTO = configService.getShopById(purchaseOrderDTO.getSupplierShopId());
      customerDTO = shopRelationService.collectCustomerShop(supplierShopDTO, customerShopDTO);
    }
    salesOrderDTO.setCustomerDTO(customerDTO);
    Set<Long> supplierProductIds = purchaseOrderDTO.getSupplierProductIdsSet();
    Map<Long, ProductDTO> supplierProductDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(supplierProductIds);
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = ServiceManager.getService(IProductHistoryService.class)
        .getOrSaveProductHistoryByLocalInfoId(salesOrderDTO.getShopId(), supplierProductIds.toArray(new Long[supplierProductIds.size()]));
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {


//    //采购改单逻辑,删除旧的采购单
//    if (salesOrderDTO.getId() != null) {
//      List<SalesOrderItem> salesOrderItems = writer.getSalesOrderItemsByOrderId(salesOrderDTO.getId());
//      if (CollectionUtils.isNotEmpty(salesOrderItems)) {
//        for (SalesOrderItem salesOrderItem : salesOrderItems) {
//          writer.delete(salesOrderItem);
//        }
//      }
//    }
      salesOrderDTO.setStatus(OrderStatus.PENDING);

      SalesOrder salesOrder = new SalesOrder();
//    OrderStatus lastOrderStatus = null;
//    if (salesOrderDTO.getId() != null) {
//      List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderDTO.getId(), salesOrderDTO.getShopId());
//      if (CollectionUtils.isNotEmpty(salesOrders)) {
//        salesOrder = salesOrders.get(0);
//        lastOrderStatus = salesOrders.get(0).getStatusEnum();
//      }
//    }
      salesOrder.fromDTO(salesOrderDTO);
      writer.saveOrUpdate(salesOrder);
      salesOrderDTO.setId(salesOrder.getId());

//    if (salesOrderDTO.getId() == null) {
//      //ad by WLF 保存销售单的创建日志
//      ServiceManager.getService(IOperationLogService.class).saveOperationLog(
//          new OperationLogDTO(salesOrder.getShopId(), purchaseOrderDTO.getUserId(), salesOrder.getId(), ObjectTypes.SALE_ORDER, OperationTypes.CREATE));
//
//    } else {
//      //ad by WLF 保存销售单的更新日志
//      ServiceManager.getService(IOperationLogService.class).saveOperationLog(
//          new OperationLogDTO(salesOrder.getShopId(), purchaseOrderDTO.getUserId(), salesOrder.getId(), ObjectTypes.SALE_ORDER, OperationTypes.UPDATE));
//    }
      //add by WLF 更新缓存中待办销售单和采购的数量
//    List<Long> customerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(salesOrder.getShopId());
//    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_SALE_ORDER, salesOrder.getShopId(), customerIdList);
//    List<Long> supplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(purchaseOrderDTO.getShopId());
//    getTxnService().updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType.TODO_PURCHASE_ORDER, purchaseOrderDTO.getShopId(), supplierIdList);
//
//    salesOrderDTO.setId(salesOrder.getId());
//    //保存单据状态变更记录
//    getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getStatus(), lastOrderStatus, salesOrderDTO.getId(), OrderTypes.PURCHASE));
//

      if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
        for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
          salesOrderItemDTO.setSalesOrderId(salesOrderDTO.getId());
          ProductDTO productDTO = supplierProductDTOMap.get(salesOrderItemDTO.getProductId());
          if (productDTO != null) {
            salesOrderItemDTO.setProductDTOWithOutUnit(productDTO);
            salesOrderItemDTO.setUnit(productDTO.getSellUnit());
          }
          salesOrderItemDTO.setReserved(0d);
          ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(salesOrderItemDTO.getProductId());
          salesOrderItemDTO.setProductHistoryId(productHistoryDTO == null ? null : productHistoryDTO.getId());
          SalesOrderItem salesOrderItem = new SalesOrderItem();
          salesOrderItem.fromDTO(salesOrderItemDTO);
          writer.save(salesOrderItem);
          salesOrderItemDTO.setId(salesOrderItem.getId());
        }
      }
      savePromotionRecord(purchaseOrderDTO,writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return salesOrderDTO;
  }


  private void savePromotionRecord(PurchaseOrderDTO purchaseOrderDTO,TxnWriter writer){
    //item的和促销record的关联
    Map<Long,PromotionOrderRecordDTO> recordMap=ServiceManager.getService(PromotionsService.class).getPromotionOrderRecordDTOMap(purchaseOrderDTO.getId());
    if(recordMap!=null&&!recordMap.keySet().isEmpty()){
        for (PurchaseOrderItemDTO itemDTO : purchaseOrderDTO.getItemDTOs()) {
          PromotionOrderRecordDTO recordDTO=recordMap.get(itemDTO.getSupplierProductId());
          if(recordDTO==null){
            continue;
          }
          OrderItemPromotion itemPromotion=new OrderItemPromotion();
          itemPromotion.setOrderItemId(itemDTO.getId());
          itemPromotion.setOrderTypes(OrderTypes.SALE);
          itemPromotion.setPromotionOrderRecordId(recordDTO.getId());
          writer.save(itemPromotion);
        }
    }
  }



  @Override
  public void repealSalesOrder(Long shopId, Long toStorehouseId, SalesOrderDTO salesOrderDTO) throws Exception{
    StopWatchUtil sw = new StopWatchUtil("goodsaleService.repealSalesOrder", "productThrough");
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    List<InventorySearchIndex> inventorySearchIndexList = null;
    ReceivableDTO receivableDTO = null;

    //txn库
    try {
      //销售单作废出入库打通
      getProductInStorageService().productThroughByOrder(salesOrderDTO, OrderTypes.SALE, OrderStatus.SALE_REPEAL, writer);
      //更新库存量
      sw.stopAndStart("inventory");
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())) {
        inventorySearchIndexList = updateInventoryAndInventorySearchIndexByStoreHouse(shopId, toStorehouseId, salesOrderDTO, writer);
      } else {
        inventorySearchIndexList = updateInventoryAndInventorySearchIndex(shopId, salesOrderDTO, writer);
      }
      //更新收款记录
      receivableDTO = rfiTxnService.updateReceivable(shopId, salesOrderDTO.getId(), OrderTypes.SALE, ReceivableStatus.REPEAL);
      sw.stopAndStart("deposit");
      //add by zhuj 回滚预收款金额
      if (receivableDTO.getDeposit() > 0.001) {
        ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
        CustomerDepositDTO customerDepositDTO= customerDepositService.queryCustomerDepositByShopIdAndCustomerId(shopId,salesOrderDTO.getCustomerId());
        DepositOrderDTO depositOrderDTO = customerDepositService.queryDepositOrderByShopIdCustomerIdAndRelatedOrderId(shopId, salesOrderDTO.getCustomerId(), salesOrderDTO.getId());
        if (InOutFlag.getInOutFlagEnumByCode(depositOrderDTO.getInOut()) == InOutFlag.OUT_FLAG) {
          depositOrderDTO.setInOut(InOutFlag.IN_FLAG.getCode());
        }
        if(DepositType.getDepositTypeBySceneAndInOutFlag(depositOrderDTO.getDepositType(),InOutFlag.OUT_FLAG)!=null ){
          depositOrderDTO.setDepositType(DepositType.SALES_REPEAL.getScene());
        }
        customerDepositDTO.setOperator(salesOrderDTO.getEditor());
        customerDepositDTO.setCash(depositOrderDTO.getCash());
        customerDepositDTO.setBankCardAmount(depositOrderDTO.getBankCardAmount());
        customerDepositDTO.setCheckAmount(depositOrderDTO.getCheckAmount());
        customerDepositDTO.setActuallyPaid(depositOrderDTO.getActuallyPaid());
        customerDepositService.customerDepositUse(customerDepositDTO,depositOrderDTO,writer);
      }

      //更新txn salesOrder 中的状态
      rfiTxnService.updateSaleOrderStatus(shopId, salesOrderDTO.getId(), OrderStatus.SALE_REPEAL, writer);
      sw.stopAndStart("commit");
      writer.commit(status);

      salesOrderDTO.setStatus(OrderStatus.SALE_REPEAL);
    } catch (Exception e) {
      LOG.error("GoodSaleService.repealSalesOrder", e);
      throw e;
    } finally {
      writer.rollback(status);
    }

    //product, user等
    ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(shopId,inventorySearchIndexList);

    //更新上架量
    sw.stopAndStart("inSales");
    getProductService().handleInSalesAmountByOrder(salesOrderDTO, 1);

    //回滚销售会员消费金额
    sw.stopAndStart("memberInfo");
    if (receivableDTO != null && NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()) > 0) {
      userService.rollBackMemberInfo(shopId, receivableDTO.getMemberId(), receivableDTO.getMemberBalancePay());
    }

    //更新欠款记录
    rfiTxnService.updateDebtByRepealOrder(shopId, salesOrderDTO.getId(), salesOrderDTO.getCustomerId(), DebtStatus.REPEAL);

    sw.stopAndStart("log");
    //生成一张对冲单据
    rfiTxnService.saveRepealOrderByOrderIdAndOrderType(shopId, salesOrderDTO.getId(), OrderTypes.SALE);
    getTxnService().saveOperationLogTxnService(new OperationLogDTO(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getId(), ObjectTypes.SALE_ORDER, OperationTypes.INVALID));
    sw.stopAndPrintLog();
  }

}
