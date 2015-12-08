package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.*;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.SaleOrderSavedEvent;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.dto.SalesOrderItemDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.IVehicleStatService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午5:00
 */
public class SalesOrderSavedListener extends OrderSavedListener {
  private SaleOrderSavedEvent saleOrderSavedEvent;
	private ISearchService searchService;

	public ISearchService getSearchService() {
		if(searchService == null){
			searchService = ServiceManager.getService(ISearchService.class);
		}
		return searchService;
	}

	public SalesOrderSavedListener(SaleOrderSavedEvent saleOrderSavedEvent) {
    super();
    this.saleOrderSavedEvent = saleOrderSavedEvent;
  }

  public void run() {
    IItemIndexService itemIndexService = ServiceManager.getService(IItemIndexService.class);
    IVehicleStatService vehicleStatService = ServiceManager.getService(IVehicleStatService.class);
    SalesOrderDTO salesOrderDTO = this.saleOrderSavedEvent.getSalesOrderDTO();
    ReceivableDTO receivableDTO = null;
    try {
      if (salesOrderDTO == null) {
        throw new Exception("销售单为空");
      }

      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      ISmsService smsService = ServiceManager.getService(ISmsService.class);
      receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(salesOrderDTO.getShopId(), salesOrderDTO.getId());

      if (salesOrderDTO.getStatus() == null) {
        throw new Exception(" salesOrderDTO 状态为空 " + salesOrderDTO.toString());
      }

      if (salesOrderDTO.getCreationDate() == null) {
        if (salesOrderDTO.getVestDate() != null) {
          salesOrderDTO.setCreationDate(salesOrderDTO.getVestDate());
        } else if (salesOrderDTO.getEditDate() != null) {
          salesOrderDTO.setCreationDate(salesOrderDTO.getEditDate());
        } else {
          salesOrderDTO.setCreationDate(System.currentTimeMillis());
        }
      }

      if (salesOrderDTO.getStatus() == OrderStatus.SALE_DONE || salesOrderDTO.getStatus() == OrderStatus.SALE_DEBT_DONE) {

        //销售单结算
        //营业统计
        ServiceManager.getService(ITxnService.class).saveOrUpdateOrderIndexByOrderIdAndOrderType(salesOrderDTO.getShopId(), OrderTypes.SALE, salesOrderDTO.getId());

        if (DateUtil.isCurrentTime(salesOrderDTO.getVestDate())) {
          businessStatByOrder(salesOrderDTO, false, true, salesOrderDTO.getVestDate());
        } else {
          businessStatByOrder(salesOrderDTO, false, false, salesOrderDTO.getVestDate());
          orderRunBusinessStatChange(salesOrderDTO);
        }

        if (salesOrderDTO.getVehicleId() != null) {
          try {
            vehicleStatService.customerVehicleConsumeStat(salesOrderDTO, OrderTypes.SALE, false, receivableDTO);
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
        }

      } else if (salesOrderDTO.getStatus() == OrderStatus.SALE_REPEAL && receivableDTO != null) {
        //销售单作废
        //更新营业统计 减去相应的销售额
        businessStatByOrder(salesOrderDTO, true, true, salesOrderDTO.getVestDate());

        //更新memcache  product 更新的标识和时间
        ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(salesOrderDTO);

        //更新search itemindex 中的状态
        itemIndexService.updateItemIndexPurchaseOrderStatus(salesOrderDTO.getShopId(), OrderTypes.SALE,salesOrderDTO.getId(), OrderStatus.SALE_REPEAL);
        //更新orderindex 状态
        getSearchService().updateOrderIndex(salesOrderDTO.getShopId(), salesOrderDTO.getId(),OrderTypes.SALE, OrderStatus.SALE_REPEAL);

        if (salesOrderDTO.getVehicleId() != null) {
          try {
            vehicleStatService.customerVehicleConsumeStat(salesOrderDTO, OrderTypes.SALE, true, receivableDTO);
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
        }

      } else if (OrderStatus.PENDING.equals(salesOrderDTO.getStatus()) || OrderStatus.STOCKING.equals(salesOrderDTO.getStatus())) {
        ServiceManager.getService(ITxnService.class).saveOrUpdateOrderIndexByOrderIdAndOrderType(salesOrderDTO.getShopId(), OrderTypes.SALE, salesOrderDTO.getId());
      } else {
        LOG.error("/SalesOrderSavedListener");
        LOG.error("method=run");
        LOG.error("销售单状态:" + salesOrderDTO.getStatus());
        LOG.error(" salesOrderDTO 状态不合法 " + salesOrderDTO.toString());
      }
      //更新单位顺序
      configService.updateOrderUnitSort(salesOrderDTO.getShopId(), salesOrderDTO);
          //短信逻辑
      String time = salesOrderDTO.getVestDateStr();
      ShopDTO shopDTO = configService.getShopById(salesOrderDTO.getShopId());
      //如果总计大于实收和欠款和的话，就代表打折了，要发送折扣短信给店老板
      if (NumberUtil.doubleVal(salesOrderDTO.getTotal()) > NumberUtil.doubleVal(salesOrderDTO.getSettledAmount())
          + NumberUtil.doubleVal(salesOrderDTO.getDebt())) {
        smsService.sendSalesOrderCustomerCheapMsgToBoss(salesOrderDTO, salesOrderDTO.getShopId(), shopDTO, time);
      }
      //如果有欠款就要发送欠款备忘给店老板
      if (NumberUtil.doubleVal(salesOrderDTO.getDebt()) > 0) {
        smsService.sendSalesOrderCustomerDebtMsgToBoss(salesOrderDTO, salesOrderDTO.getShopId(), shopDTO, time, salesOrderDTO.getPaymentTime());
      }
      // add by WLF 更新缓存
      if (NumberUtil.doubleVal(salesOrderDTO.getDebt()) > 0){
        txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, salesOrderDTO.getShopId());
      }

      //单据已经被结算
      if (receivableDTO != null) {
        salesStat(salesOrderDTO, salesOrderDTO.getStatus());
      }

      reCreateSolrIndex(salesOrderDTO);
      //常用商品 add by zhangjuntao
      salesOrderDTO.setCurrentUsedProductDTOList();
      salesOrderDTO.setCurrentUsedVehicleDTOList();
      this.currentUsedSaved(salesOrderDTO);
      saleOrderSavedEvent.setOrderFlag(true);
    } catch (Exception e) {
      LOG.error("/SalesOrderSavedListener method=run");
      LOG.error(e.getMessage(), e);
    }
  }

  private void reCreateSolrIndex(SalesOrderDTO salesOrderDTO) throws Exception {
    ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(salesOrderDTO.getShopId(), salesOrderDTO.getProductIds());

    ICustomerOrSupplierSolrWriteService supplierSolrWriteService = ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(salesOrderDTO.getShopId()), OrderTypes.SALE, salesOrderDTO.getId());
    //reindex customer in solr
    supplierSolrWriteService.reindexCustomerByCustomerId(salesOrderDTO.getCustomerId());

    if (!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())) {
      List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
      for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
        if (salesOrderItemDTO.isAddVehicleLicenceNoToSolr()) {
          VehicleDTO vehicleDTOForSolr = new VehicleDTO(salesOrderItemDTO);
          if (vehicleDTOForSolr.getId() != null) {
            vehicleDTOs.add(vehicleDTOForSolr);
          }
        }
      }
      ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOs);
    }
    if(salesOrderDTO.getVehicleId()!=null)
      ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(salesOrderDTO.getShopId(), salesOrderDTO.getVehicleId());
  }
}