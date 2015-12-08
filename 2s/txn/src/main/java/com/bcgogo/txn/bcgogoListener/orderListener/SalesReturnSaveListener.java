package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.IInventoryIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.SalesReturnSavedEvent;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-8-17
 * Time: 上午11:39
 */
public class SalesReturnSaveListener extends OrderSavedListener {


  public SalesReturnSavedEvent getSalesReturnSavedEvent() {
    return salesReturnSavedEvent;
  }

  public void setSalesReturnSavedEvent(SalesReturnSavedEvent salesReturnSavedEvent) {
    this.salesReturnSavedEvent = salesReturnSavedEvent;
  }

  private SalesReturnSavedEvent salesReturnSavedEvent;

  public SalesReturnSaveListener(SalesReturnSavedEvent salesReturnSavedEvent) {
    super();
    this.salesReturnSavedEvent = salesReturnSavedEvent;
  }

  public void run() {
    SalesReturnDTO salesReturnDTO = this.getSalesReturnSavedEvent().getSalesReturnDTO();
    if (salesReturnDTO == null) {
      LOG.error("SalesReturnSaveListener.run(), 销售退货单 salesReturnDTO 为空");
      return;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(salesReturnDTO.getShopId(),salesReturnDTO.getId());
    try {
      try {
        ISearchService searchService = ServiceManager.getService(ISearchService.class);
        IInventoryIndexService inventoryIndexService = ServiceManager.getService(IInventoryIndexService.class);
        RFITxnService rtTxnService = ServiceManager.getService(RFITxnService.class);

        txnService.saveOrUpdateOrderIndexByOrderIdAndOrderType(salesReturnDTO.getShopId(), OrderTypes.SALE_RETURN, salesReturnDTO.getId());
        //reindex solr
        reCreateSalesReturnOrderSolrIndex(salesReturnDTO);

        if (!ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())) {
          List<Long> productIdList = new ArrayList<Long>();
          for (SalesReturnItemDTO salesReturnItemDTO : salesReturnDTO.getItemDTOs()) {
            if (salesReturnItemDTO.getProductId() == null) {
              continue;
            }
            productIdList.add(salesReturnItemDTO.getProductId());
            //根据productId找到库存 SearchIndex
            InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(salesReturnItemDTO.getProductId());
            InventoryDTO inventoryDTO = rtTxnService.getInventoryByShopIdAndProductId(salesReturnDTO.getShopId(), salesReturnItemDTO.getProductId());
            if (inventorySearchIndex != null) {
              inventorySearchIndex.setEditDate(salesReturnDTO.getEditDate());
              //设置库存量
              inventorySearchIndex.setAmount(inventoryDTO.getAmount());
              inventorySearchIndex.setCommodityCode(salesReturnItemDTO.getCommodityCode());
              inventoryIndexService.saveInventorySearchIndex(inventorySearchIndex);
            }
          }
          //商品solr
          ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(salesReturnDTO.getShopId(), productIdList.toArray(new Long[productIdList.size()]));
        }
        //
        if (salesReturnDTO.getPurchaseReturnOrderId() != null) {
          RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
          PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(salesReturnDTO.getPurchaseReturnOrderId());
          if (purchaseReturnDTO != null) {
            txnService.saveOrUpdateOrderIndexByOrderIdAndOrderType(salesReturnDTO.getShopId(), OrderTypes.RETURN, purchaseReturnDTO.getId());
            //reindex solr
            reCreatePurchaseReturnOrderSolrIndex(purchaseReturnDTO);
            if (!ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())) {
              List<Long> productIdList = new ArrayList<Long>();
              for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
                if (purchaseReturnItemDTO.getProductId() == null) {
                  continue;
                }
                productIdList.add(purchaseReturnItemDTO.getProductId());
                //根据productId找到库存 SearchIndex
                InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(purchaseReturnItemDTO.getProductId());
                InventoryDTO inventoryDTO = rtTxnService.getInventoryByShopIdAndProductId(purchaseReturnDTO.getShopId(), purchaseReturnItemDTO.getProductId());
                if (inventorySearchIndex != null) {
                  inventorySearchIndex.setEditDate(purchaseReturnDTO.getEditDate());
                  //设置库存量
                  inventorySearchIndex.setAmount(inventoryDTO.getAmount());
                  inventoryIndexService.saveInventorySearchIndex(inventorySearchIndex);
                }
              }
              //如果被拒绝后   对应店铺  商品库存 有更新solr
              ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(purchaseReturnDTO.getShopId(), productIdList.toArray(new Long[productIdList.size()]));
            }
          }
        }
      } catch (Exception e) {
        LOG.error("/PurchaseReturnSaveListener salesReturnDTO:" + salesReturnDTO.toString());
        LOG.error(e.getMessage(), e);
      }

      if (salesReturnDTO.getStatus() == OrderStatus.SETTLED) {
        //销售退货单结算
        //营业统计
        if (DateUtil.isCurrentTime(salesReturnDTO.getVestDate())) {
          businessStatByOrder(salesReturnDTO, false, true, salesReturnDTO.getVestDate());
        } else {
          businessStatByOrder(salesReturnDTO, false, false, salesReturnDTO.getVestDate());
          orderRunBusinessStatChange(salesReturnDTO);
        }
      } else if (salesReturnDTO.getStatus() == OrderStatus.REPEAL && receivableDTO != null) {
        //销售退货单作废
        //更新营业统计 减去相应的销售额
        businessStatByOrder(salesReturnDTO, true, true, salesReturnDTO.getVestDate());
      }
    } catch (Exception e) {
      LOG.error("/PurchaseReturnSaveListener");
      LOG.error("method=run");
      LOG.error("销售退货单线程信息保存失败");
      LOG.error(e.getMessage(), e);
    }

  }

  private void reCreateSalesReturnOrderSolrIndex(SalesReturnDTO salesReturnDTO) {
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(salesReturnDTO.getShopId()), OrderTypes.SALE_RETURN, salesReturnDTO.getId());
    //reindex supplier in solr
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(salesReturnDTO.getCustomerId());
  }

  private void reCreatePurchaseReturnOrderSolrIndex(PurchaseReturnDTO purchaseReturnDTO) {
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseReturnDTO.getShopId()), OrderTypes.RETURN, purchaseReturnDTO.getId());
  }

}
