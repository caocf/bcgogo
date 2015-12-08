package com.bcgogo.txn.bcgogoListener.publisher;

import com.bcgogo.txn.bcgogoListener.orderEvent.*;
import com.bcgogo.txn.bcgogoListener.orderListener.*;
import com.bcgogo.txn.bcgogoListener.threadPool.OrderThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午4:47
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoEventPublisher {
  private final static Logger LOG = LoggerFactory.getLogger(BcgogoEventPublisher.class);

  public void publisherWashOrderSaved(WashOrderSavedEvent washOrderSavedEvent) {
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new WashOrderSavedListener(washOrderSavedEvent));
  }

  public void publisherSaleOrderSaved(SaleOrderSavedEvent saleOrderSavedEvent) {
    LOG.info("AOP_thread:publish SalesOrderSavedListener start,shopId:{}", saleOrderSavedEvent.getSalesOrderDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new SalesOrderSavedListener(saleOrderSavedEvent));
  }

  public void publisherRepairOrderSaved(RepairOrderSavedEvent repairOrderSavedEvent) {
    LOG.info("AOP_thread:publish RepairOrderSavedListener start,shopId:{}", repairOrderSavedEvent.getRepairOrderDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new RepairOrderSavedListener(repairOrderSavedEvent));
  }

  public void publisherPurchaseOrderSaved(PurchaseOrderSavedEvent purchaseOrderSavedEvent) {
    LOG.info("AOP_thread:publish PurchaseOrderSavedListener start,shopId:{}", purchaseOrderSavedEvent.getPurchaseOrderDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new PurchaseOrderSavedListener(purchaseOrderSavedEvent));
  }

  public void publisherPurchaseInventorySaved(PurchaseInventorySavedEvent purchaseInventorySavedEvent) {
     LOG.info("AOP_thread:publish PurchaseInventorySavedListener start,shopId:{}", purchaseInventorySavedEvent.getPurchaseInventoryDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new PurchaseInventorySavedListener(purchaseInventorySavedEvent));
  }

  public void publisherMemberCardOrderSaved(MemberCardOrderSavedEvent memberCardOrderSavedEvent) {
    LOG.info("AOP_thread:publish MemberCardOrderSavedListener start,shopId:{}", memberCardOrderSavedEvent.getMemberCardOrderDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new MemberCardOrderSavedListener(memberCardOrderSavedEvent));
  }

  public void publisherMemberCardReturnSaved(MemberCardReturnSavedEvent memberCardReturnSavedEvent) {
    LOG.info("AOP_thread:publish MemberCardReturnSavedListener start,shopId:{}", memberCardReturnSavedEvent.getMemberCardReturnDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new MemberCardReturnSavedListener(memberCardReturnSavedEvent));
  }

  public void publisherWashBeautyOrderSaved(WashOrderSavedEvent washOrderSavedEvent) {
    LOG.info("AOP_thread:publish WashOrderSavedListener start,shopId:{}", washOrderSavedEvent.getWashBeautyOrderDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new WashOrderSavedListener(washOrderSavedEvent));
  }

  public void publisherPurchaseReturnSaved(PurchaseReturnSavedEvent purchaseReturnSavedEvent) {
    LOG.info("AOP_thread:publish SalesReturnSaveListener start,shopId:{}", purchaseReturnSavedEvent.getPurchaseReturnDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new PurchaseReturnSaveListener(purchaseReturnSavedEvent));
  }

  public void publisherSalesReturnSaved(SalesReturnSavedEvent salesReturnSavedEvent) {
    LOG.info("AOP_thread:publish SalesReturnSaveListener start,shopId:{}", salesReturnSavedEvent.getSalesReturnDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new SalesReturnSaveListener(salesReturnSavedEvent));
  }

  /**
   * 用作一些特殊单据reindex使用（不计入营业统计和流水统计的单据）
   *
   * @param bcgogoOrderReindexEvent
   */
  public void bcgogoOrderReindex(BcgogoOrderReindexEvent bcgogoOrderReindexEvent) {
    LOG.info("AOP_thread:publish BcgogoOrderReindexListener start,shopId:{}", bcgogoOrderReindexEvent.getBcgogoOrderDto().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new BcgogoOrderReindexListener(bcgogoOrderReindexEvent));
  }

  public void publisherStatementAccount(StatementAccountEvent statementAccountEvent) {
    LOG.info("AOP_thread:publish StatementAccountListener start,shopId:{}", statementAccountEvent.getStatementAccountOrderDTO().getShopId());
    Executor executor = OrderThreadPool.getInstance();
    executor.execute(new StatementAccountListener(statementAccountEvent));

  }

}
