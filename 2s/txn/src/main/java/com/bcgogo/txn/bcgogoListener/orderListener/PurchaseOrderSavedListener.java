package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.PurchaseOrderSavedEvent;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.txn.dto.PurchaseOrderItemDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderDTO;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-4-12
 * Time: 下午1:27
 */
public class PurchaseOrderSavedListener extends OrderSavedListener {
  private PurchaseOrderSavedEvent purchaseOrderSavedEvent;

  public PurchaseOrderSavedListener(PurchaseOrderSavedEvent purchaseOrderSavedEvent) {
    super();
    this.purchaseOrderSavedEvent = purchaseOrderSavedEvent;
  }

  public void run() {
    PurchaseOrderDTO purchaseOrderDTO = this.purchaseOrderSavedEvent.getPurchaseOrderDTO();
	  try {
		  if (purchaseOrderDTO != null) {
        if (purchaseOrderDTO.getCreationDate() == null) {
          if (purchaseOrderDTO.getVestDate() != null) {
            purchaseOrderDTO.setCreationDate(purchaseOrderDTO.getVestDate());
          } else if (purchaseOrderDTO.getEditDate() != null) {
            purchaseOrderDTO.setCreationDate(purchaseOrderDTO.getEditDate());
          } else {
            purchaseOrderDTO.setCreationDate(System.currentTimeMillis());
          }
        }

        ServiceManager.getService(ITxnService.class).saveOrUpdateOrderIndexByOrderIdAndOrderType(purchaseOrderDTO.getShopId(), OrderTypes.PURCHASE, purchaseOrderDTO.getId());

        reCreateSolrIndex(purchaseOrderDTO);
      }else{
        throw new Exception("采购单 purchaseOrderDTO 为空");
      }
    } catch (Exception e) {
      LOG.error("/PurchaseOrderSavedListener");
      LOG.error("method=run");
      LOG.error("orderIndex保存失败");
      LOG.error(e.getMessage(), e);
    }
     //modify by zhangjuntao
    this.currentUsedSaved(purchaseOrderSavedEvent.getPurchaseOrderDTO());
    purchaseOrderSavedEvent.setOrderFlag(true);
  }

  public void reCreateSolrIndex(PurchaseOrderDTO purchaseOrderDTO) throws Exception{
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(purchaseOrderDTO.getShopId()), OrderTypes.PURCHASE, purchaseOrderDTO.getId());
    //reindex supplier in solr
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(purchaseOrderDTO.getSupplierId());
    //reindex 对应 报价单的索引
    if(!ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())){
      Set<Long> quotedPreBuyOrderItemIdSet = new HashSet<Long>();
      for(PurchaseOrderItemDTO purchaseOrderItemDTO: purchaseOrderDTO.getItemDTOs()){
        quotedPreBuyOrderItemIdSet.add(purchaseOrderItemDTO.getQuotedPreBuyOrderItemId());
      }
      if(CollectionUtils.isNotEmpty(quotedPreBuyOrderItemIdSet)){
        List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOList =  ServiceManager.getService(IPreBuyOrderService.class).getQuotedPreBuyOrderIdsByItemId(null, quotedPreBuyOrderItemIdSet.toArray(new Long[quotedPreBuyOrderItemIdSet.size()]));
        if(CollectionUtils.isNotEmpty(quotedPreBuyOrderDTOList)){
          Long quotedShopId = quotedPreBuyOrderDTOList.get(0).getShopId();
          Set<Long> quotedPreBuyOrderIdSet = new HashSet<Long>();
          for(QuotedPreBuyOrderDTO quotedPreBuyOrderDTO:quotedPreBuyOrderDTOList){
            quotedPreBuyOrderIdSet.add(quotedPreBuyOrderDTO.getId());
          }
          LOG.debug("reindex 【采购单ID】"+purchaseOrderDTO.getId()+"对应 报价单的索引!");
          ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(quotedShopId), OrderTypes.QUOTED_PRE_BUY_ORDER, quotedPreBuyOrderIdSet.toArray(new Long[quotedPreBuyOrderIdSet.size()]));
        }
      }
    }
  }
}
