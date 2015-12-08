package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PaymentTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.PurchaseReturnSavedEvent;
import com.bcgogo.txn.dto.PayableDTO;
import com.bcgogo.txn.dto.PurchaseReturnDTO;
import com.bcgogo.txn.dto.SalesReturnDTO;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-8-17
 * Time: 上午11:39
 */
public class PurchaseReturnSaveListener extends OrderSavedListener {


  public PurchaseReturnSavedEvent getPurchaseReturnSavedEvent() {
    return purchaseReturnSavedEvent;
  }

  public void setPurchaseReturnSavedEvent(PurchaseReturnSavedEvent purchaseReturnSavedEvent) {
    this.purchaseReturnSavedEvent = purchaseReturnSavedEvent;
  }

  private PurchaseReturnSavedEvent purchaseReturnSavedEvent;


  public PurchaseReturnSaveListener(PurchaseReturnSavedEvent purchaseReturnSavedEvent) {
    super();
    this.purchaseReturnSavedEvent = purchaseReturnSavedEvent;
  }

  public void run() {
    PurchaseReturnDTO purchaseReturnDTO = this.getPurchaseReturnSavedEvent().getPurchaseReturnDTO();
    if (purchaseReturnDTO == null) {
      LOG.error("PurchaseReturnSaveListener.run(), 入库退货单 purchaseReturnDTO 为空");
      return;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    try {
      //update Order_index,Item_index
      txnService.saveOrUpdateOrderIndexByOrderIdAndOrderType(purchaseReturnDTO.getShopId(), OrderTypes.RETURN, purchaseReturnDTO.getId());
      //reindex solr
      reCreatePurchaseReturnOrderSolrIndex(purchaseReturnDTO);

      ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
      if (purchaseReturnDTO.getSupplierShopId() != null) {
        SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOByPurchaseReturnOrderId(purchaseReturnDTO.getId());
        if (salesReturnDTO != null) {
          txnService.saveOrUpdateOrderIndexByOrderIdAndOrderType(salesReturnDTO.getShopId(), OrderTypes.SALE_RETURN, salesReturnDTO.getId());
          //reindex solr
          reCreateSalesReturnOrderSolrIndex(salesReturnDTO);
        }
      }
    } catch (Exception e) {
      LOG.error("/PurchaseReturnSaveListener");
      LOG.error("method=run");
      LOG.error("采购退货单信息线程保存失败");
      LOG.error(e.getMessage(), e);
    }

    //add by zhangjuntao
    this.currentUsedSaved(purchaseReturnDTO);
    purchaseReturnSavedEvent.setOrderFlag(true);

    boolean isRepeal = false;
    if (purchaseReturnDTO.getStatus() == OrderStatus.REPEAL) {
      isRepeal = true;
    }

    businessStatByOrder(purchaseReturnDTO, isRepeal, false, purchaseReturnDTO.getVestDate());

    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
//    SupplierReturnPayableDTO supplierReturnPayableDTO =  supplierPayableService.getSupplierReturnPayableByPurchaseReturnId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId());
    PayableDTO payableDTO = supplierPayableService.getPayableDTOByOrderId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId());
    if (payableDTO != null) {
      purchaseReturnStat(purchaseReturnDTO, isRepeal);
    }
    
  }

  private void reCreatePurchaseReturnOrderSolrIndex(PurchaseReturnDTO purchaseReturnDTO) {
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseReturnDTO.getShopId()), OrderTypes.RETURN, purchaseReturnDTO.getId());
    //reindex supplier in solr
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(purchaseReturnDTO.getSupplierId());

    List<Long> purchaseInventoryIds = ServiceManager.getService(ITxnService.class).getPurchaseInventoryIdFromPayableHistory(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId(), PaymentTypes.INVENTORY_DEBT);
    if (CollectionUtils.isNotEmpty(purchaseInventoryIds)) {
      ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseReturnDTO.getShopId()), OrderTypes.INVENTORY, purchaseInventoryIds.toArray(new Long[purchaseInventoryIds.size()]));
    }
  }

  private void reCreateSalesReturnOrderSolrIndex(SalesReturnDTO salesReturnDTO) {
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(salesReturnDTO.getShopId()), OrderTypes.SALE_RETURN, salesReturnDTO.getId());
  }

  private void purchaseReturnStat(PurchaseReturnDTO purchaseReturnDTO, boolean isRepeal) {
    try {
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
      purchaseCostStatService.purchaseReturnStat(purchaseReturnDTO, isRepeal);
    } catch (Exception e) {
      LOG.error("采购成本统计出错 /PurchaseReturnSavedListener method=purchaseReturnStat shopId:" + purchaseReturnDTO.getShopId());
      LOG.error(e.getMessage(), e);
      LOG.error("purchaseReturnDTO单据内容: {}", purchaseReturnDTO);
    }
  }
}
