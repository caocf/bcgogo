package com.bcgogo.schedule.index.stat;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.IMergeService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.MergeType;
import com.bcgogo.user.model.task.MergeTask;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户或供应商合并后，单据定时reindex.
 * User: ndong
 * Date: 12-11-10
 * Time: 上午8:56
 * To change this template use File | Settings | File Templates.
 */
public class MergeReindexSchedule extends BcgogoQuartzJobBean {

  private static final Logger LOG = LoggerFactory.getLogger(MergeReindexSchedule.class);
  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isLock()) {
      return;
    }
    MergeTask nextTask = null;
    try{
      ICustomerService customerService= ServiceManager.getService(ICustomerService.class);
      ICustomerOrSupplierSolrWriteService supplierSolrWriteService= ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
      nextTask = customerService.getMergeTaskOneByOne();
      while (nextTask!=null) {
        if(MergeType.MERGE_CUSTOMER.equals(nextTask.getMergeType())){
          supplierSolrWriteService.reindexCustomerByCustomerId(nextTask.getChildId());
          supplierSolrWriteService.reindexCustomerByCustomerId(nextTask.getParentId());
          reindexCustomerOrder(nextTask);

        }else {
          supplierSolrWriteService.reindexSupplierBySupplierId(nextTask.getChildId());
          supplierSolrWriteService.reindexSupplierBySupplierId(nextTask.getParentId());
          reindexSupplierOrder(nextTask);
        }
        deleteMergeTask(nextTask);
        nextTask= customerService.getMergeTaskOneByOne();
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      saveExceptionMergeTask(nextTask);
    }finally {
      lock = false;
    }
  }

  public void deleteMergeTask(MergeTask task) throws BcgogoException {
    try{
      ICustomerService customerService= ServiceManager.getService(ICustomerService.class);
      customerService.deleteMergeTask(task.getId());
    }catch (Exception e){
      LOG.error("删除任务表MergeTask异常！！！");
      throw new BcgogoException(e.getMessage());
    }finally {
    }
  }

  public void saveExceptionMergeTask(MergeTask task){
    try{
      if(task==null){
        return;
      }
      ICustomerService customerService= ServiceManager.getService(ICustomerService.class);
      customerService.saveExceptionMergeTask(task.getId());
    }catch (Exception e){
      LOG.error("保存异常任务表MergeTask异常！！！");

    }
  }


  public void reindexSupplierOrder(MergeTask task){

    List<Long> purchaseOrderIds=new ArrayList<Long>();
    List<Long> purchaseInventoryIds=new ArrayList<Long>();
    List<Long> purchaseReturnIds=new ArrayList<Long>();
    List<Long> statementAccountIds=new ArrayList<Long>();
    IMergeService mergeService= ServiceManager.getService(IMergeService.class);
    try{
      List<PurchaseOrder> purchaseOrders=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.PURCHASE,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(purchaseOrders)){
        for(PurchaseOrder purchaseOrder:purchaseOrders){
          purchaseOrderIds.add(purchaseOrder.getId());
        }
      }
      List<PurchaseInventory> purchaseInventories=mergeService.getCustomerOrSupplierOrders(task.getShopId(), OrderTypes.INVENTORY,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(purchaseInventories)){
        for(PurchaseInventory purchaseInventory:purchaseInventories){
          purchaseInventoryIds.add(purchaseInventory.getId());
        }
      }
      List<PurchaseReturn> purchaseReturns=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.RETURN,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(purchaseReturns)){
        for(PurchaseReturn purchaseReturn:purchaseReturns){
          purchaseReturnIds.add(purchaseReturn.getId());
        }
      }
      List<StatementAccountOrder> statementAccountOrders=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.SUPPLIER_STATEMENT_ACCOUNT,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(statementAccountOrders)){
        for(StatementAccountOrder order:statementAccountOrders){
          statementAccountIds.add(order.getId());
        }
      }
    }catch (Exception e){
      LOG.error("查询供应商单据异常异常！！！,supplierId={}",task.getParentId());
      LOG.error(e.getMessage());
      return;
    }

    //begin reindex
    LOG.info("开始进行供应商单据reindex,现在时间 {}，....", DateUtil.convertDateLongToString(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_ALL));
    try{
      IOrderSolrWriterService solrWriter=ServiceManager.getService(IOrderSolrWriterService.class);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(task.getShopId());
      if(CollectionUtils.isNotEmpty(purchaseOrderIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.PURCHASE,purchaseOrderIds.toArray(new Long[purchaseOrderIds.size()]));
      if(CollectionUtils.isNotEmpty(purchaseInventoryIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.INVENTORY,purchaseInventoryIds.toArray(new Long[purchaseInventoryIds.size()]));
      if(CollectionUtils.isNotEmpty(purchaseReturnIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.RETURN, purchaseReturnIds.toArray(new Long[purchaseReturnIds.size()]));
      if(CollectionUtils.isNotEmpty(statementAccountIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.SUPPLIER_STATEMENT_ACCOUNT, statementAccountIds.toArray(new Long[statementAccountIds.size()]));

    }catch (Exception e){
      LOG.error("供应商单据reindex异常！！！");
      LOG.error(e.getMessage());
    }
    LOG.info("供应商单据reindex结束,现在时间 {}", DateUtil.convertDateLongToString(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_ALL));

  }


  public void reindexCustomerOrder(MergeTask task){
    List<Long> repairOrderIds=new ArrayList<Long>();
    List<Long> saleOrderIds=new ArrayList<Long>();
    List<Long> washBeautyOrderIds=new ArrayList<Long>();
    List<Long> memberReturnCardIds=new ArrayList<Long>();
    List<Long> memberBuyCardIds=new ArrayList<Long>();
    List<Long> salesReturnIds=new ArrayList<Long>();
    List<Long> statementAccountIds=new ArrayList<Long>();
    List<Long> appointOrderIds = new ArrayList<Long>();
    try{
      IMergeService mergeService= ServiceManager.getService(IMergeService.class);
      List<SalesOrder> salesOrders=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.SALE,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(salesOrders)){
        for(SalesOrder salesOrder:salesOrders){
          saleOrderIds.add(salesOrder.getId());
        }
      }
      List<RepairOrder> repairOrders=mergeService.getCustomerOrSupplierOrders(task.getShopId(), OrderTypes.REPAIR,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(repairOrders)){
        for(RepairOrder repairOrder:repairOrders){
          repairOrderIds.add(repairOrder.getId());
        }
      }
      List<WashBeautyOrder> washBeautyOrders=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.WASH_BEAUTY,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(washBeautyOrders)){
        for(WashBeautyOrder washBeautyOrder:washBeautyOrders){
          washBeautyOrderIds.add(washBeautyOrder.getId());
        }
      }
      List<MemberCardOrder> memberCardOrders=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.MEMBER_BUY_CARD,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(memberCardOrders)){
        for(MemberCardOrder memberCardOrder:memberCardOrders){
          memberBuyCardIds.add(memberCardOrder.getId());
        }
      }
      List<MemberCardReturn> memberCardReturns=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.MEMBER_RETURN_CARD,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(memberCardReturns)){
        for(MemberCardReturn memberCardReturn:memberCardReturns){
          memberReturnCardIds.add(memberCardReturn.getId());
        }
      }
      List<SalesReturn> salesReturns=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.SALE_RETURN,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(salesReturns)){
        for(SalesReturn salesReturn:salesReturns){
          salesReturnIds.add(salesReturn.getId());
        }
      }
      List<StatementAccountOrder> statementAccountOrders=mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.CUSTOMER_STATEMENT_ACCOUNT,new Long[]{task.getParentId()});
      if(CollectionUtils.isNotEmpty(statementAccountOrders)){
        for(StatementAccountOrder order:statementAccountOrders){
          statementAccountIds.add(order.getId());
        }
      }
//      List<AppointOrder> appointOrders = mergeService.getCustomerOrSupplierOrders(task.getShopId(),OrderTypes.APPOINT_ORDER,new Long[]{task.getParentId()});
//      if(CollectionUtils.isNotEmpty(appointOrders)) {
//          for(AppointOrder appointOrder : appointOrders) {
//            appointOrderIds.add(appointOrder.getId());
//          }
//      }
    }catch (Exception e){
      LOG.error("查询客户单据异常异常！！！,customerId={}",task.getParentId());
      LOG.error(e.getMessage());
      return;
    }

    //begin reindex
    LOG.info("开始进行客户单据reindex,现在时间 {}，....", DateUtil.convertDateLongToString(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_ALL));
    try{
      IOrderSolrWriterService solrWriter=ServiceManager.getService(IOrderSolrWriterService.class);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(task.getShopId());
      if(CollectionUtils.isNotEmpty(repairOrderIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.REPAIR,repairOrderIds.toArray(new Long[repairOrderIds.size()]));
      if(CollectionUtils.isNotEmpty(saleOrderIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.SALE,saleOrderIds.toArray(new Long[saleOrderIds.size()]));
      if(CollectionUtils.isNotEmpty(washBeautyOrderIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.WASH_BEAUTY, washBeautyOrderIds.toArray(new Long[washBeautyOrderIds.size()]));
      if(CollectionUtils.isNotEmpty(memberReturnCardIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.MEMBER_RETURN_CARD,memberReturnCardIds.toArray(new Long[memberReturnCardIds.size()]));
      if(CollectionUtils.isNotEmpty(memberBuyCardIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.MEMBER_BUY_CARD,memberBuyCardIds.toArray(new Long[memberBuyCardIds.size()]));
      if(CollectionUtils.isNotEmpty(salesReturnIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.SALE_RETURN,salesReturnIds.toArray(new Long[salesReturnIds.size()]));
      if(CollectionUtils.isNotEmpty(statementAccountIds))
        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.CUSTOMER_STATEMENT_ACCOUNT,statementAccountIds.toArray(new Long[statementAccountIds.size()]));
//      if(CollectionUtils.isNotEmpty(appointOrderIds)) {
//        solrWriter.reCreateOrderSolrIndex(shopDTO, OrderTypes.APPOINT_ORDER,appointOrderIds.toArray(new Long[appointOrderIds.size()]));
//      }
    }catch (Exception e){
      LOG.error("客户单据reindex异常！！！");
      LOG.error(e.getMessage(),e);
    }
    LOG.info("客户单据reindex结束,现在时间 {}", DateUtil.convertDateLongToString(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_ALL));
  }
}
