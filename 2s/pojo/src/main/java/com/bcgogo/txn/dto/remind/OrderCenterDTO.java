package com.bcgogo.txn.dto.remind;

/**
 * User: ZhangJuntao
 * Date: 13-6-18
 * Time: 下午1:57
 * 订单中心参数封装
 */
public class OrderCenterDTO {
  //销售单
  private Long saleTodayNew;    //今日新增
  private Long saleEarlyNew;     //往日新增
  private Long saleNew;              //新增总数
  private Long saleStocking;     //备货中
  private Long saleDispatch;      //已发货
  private Long saleSaleDebtDone;    //欠款结算
  private Long saleInProgress;   //处理中（各个待处理状态的总和）

  //销售退货单
  private Long saleReturnTodayNew;       //今日新增
  private Long saleReturnEarlyNew;      //往日新增
  private Long saleReturnNew;           //新增总数
  private Long saleReturnInProgress;  //处理中（待入库）

  //采购单
  private Long purchaseTodayNew;  //今日新增
  private Long purchaseEarlyNew;     //往日新增
  private Long purchaseNew;        //新增总数
  private Long purchaseSellerStock;       //卖家备货中
  private Long purchaseSellerDispatch;    //卖家发货中
  private Long purchaseSellerRefused;  //卖家已拒绝
  private Long purchaseSellerStop;          //卖家终止交易
  private Long purchaseInProgress;     //处理中（各个待处理状态的总和）
  private Long purchaseTodayDone;        //今日入库
  private Long purchaseEarlyDone;     //往日入库
  private Long purchaseDone;       //入库总数

  /**
   * ****** 入库退货单 ***********
   */
  private Long purchaseReturnTodayNew;    //今日新增
  private Long purchaseReturnEarlyNew;   //往日新增
  private Long purchaseReturnNew;     //新增总数
  private Long purchaseReturnSellerAccept;       //卖家已接受
  private Long purchaseReturnSellerRefused;       //卖家已拒绝
  private Long purchaseReturnInProgress;       //处理中（各个待处理状态的总和）


  public Long getSaleTodayNew() {
    return saleTodayNew;
  }

  public void setSaleTodayNew(Long saleTodayNew) {
    this.saleTodayNew = saleTodayNew;
  }

  public Long getSaleEarlyNew() {
    return saleEarlyNew;
  }

  public void setSaleEarlyNew(Long saleEarlyNew) {
    this.saleEarlyNew = saleEarlyNew;
  }

  public Long getSaleNew() {
    return saleNew;
  }

  public void setSaleNew(Long saleNew) {
    this.saleNew = saleNew;
  }

  public Long getSaleStocking() {
    return saleStocking;
  }

  public void setSaleStocking(Long saleStocking) {
    this.saleStocking = saleStocking;
  }

  public Long getSaleDispatch() {
    return saleDispatch;
  }

  public void setSaleDispatch(Long saleDispatch) {
    this.saleDispatch = saleDispatch;
  }

  public Long getSaleSaleDebtDone() {
    return saleSaleDebtDone;
  }

  public void setSaleSaleDebtDone(Long saleSaleDebtDone) {
    this.saleSaleDebtDone = saleSaleDebtDone;
  }

  public Long getSaleInProgress() {
    return saleInProgress;
  }

  public void setSaleInProgress(Long saleInProgress) {
    this.saleInProgress = saleInProgress;
  }

  public Long getSaleReturnTodayNew() {
    return saleReturnTodayNew;
  }

  public void setSaleReturnTodayNew(Long saleReturnTodayNew) {
    this.saleReturnTodayNew = saleReturnTodayNew;
  }

  public Long getSaleReturnEarlyNew() {
    return saleReturnEarlyNew;
  }

  public void setSaleReturnEarlyNew(Long saleReturnEarlyNew) {
    this.saleReturnEarlyNew = saleReturnEarlyNew;
  }

  public Long getSaleReturnNew() {
    return saleReturnNew;
  }

  public void setSaleReturnNew(Long saleReturnNew) {
    this.saleReturnNew = saleReturnNew;
  }

  public Long getSaleReturnInProgress() {
    return saleReturnInProgress;
  }

  public void setSaleReturnInProgress(Long saleReturnInProgress) {
    this.saleReturnInProgress = saleReturnInProgress;
  }

  public Long getPurchaseTodayNew() {
    return purchaseTodayNew;
  }

  public void setPurchaseTodayNew(Long purchaseTodayNew) {
    this.purchaseTodayNew = purchaseTodayNew;
  }

  public Long getPurchaseEarlyNew() {
    return purchaseEarlyNew;
  }

  public void setPurchaseEarlyNew(Long purchaseEarlyNew) {
    this.purchaseEarlyNew = purchaseEarlyNew;
  }

  public Long getPurchaseNew() {
    return purchaseNew;
  }

  public void setPurchaseNew(Long purchaseNew) {
    this.purchaseNew = purchaseNew;
  }

  public Long getPurchaseSellerStock() {
    return purchaseSellerStock;
  }

  public void setPurchaseSellerStock(Long purchaseSellerStock) {
    this.purchaseSellerStock = purchaseSellerStock;
  }

  public Long getPurchaseSellerDispatch() {
    return purchaseSellerDispatch;
  }

  public void setPurchaseSellerDispatch(Long purchaseSellerDispatch) {
    this.purchaseSellerDispatch = purchaseSellerDispatch;
  }

  public Long getPurchaseSellerRefused() {
    return purchaseSellerRefused;
  }

  public void setPurchaseSellerRefused(Long purchaseSellerRefused) {
    this.purchaseSellerRefused = purchaseSellerRefused;
  }

  public Long getPurchaseSellerStop() {
    return purchaseSellerStop;
  }

  public void setPurchaseSellerStop(Long purchaseSellerStop) {
    this.purchaseSellerStop = purchaseSellerStop;
  }

  public Long getPurchaseInProgress() {
    return purchaseInProgress;
  }

  public void setPurchaseInProgress(Long purchaseInProgress) {
    this.purchaseInProgress = purchaseInProgress;
  }

  public Long getPurchaseTodayDone() {
    return purchaseTodayDone;
  }

  public void setPurchaseTodayDone(Long purchaseTodayDone) {
    this.purchaseTodayDone = purchaseTodayDone;
  }

  public Long getPurchaseEarlyDone() {
    return purchaseEarlyDone;
  }

  public void setPurchaseEarlyDone(Long purchaseEarlyDone) {
    this.purchaseEarlyDone = purchaseEarlyDone;
  }

  public Long getPurchaseDone() {
    return purchaseDone;
  }

  public void setPurchaseDone(Long purchaseDone) {
    this.purchaseDone = purchaseDone;
  }

  public Long getPurchaseReturnTodayNew() {
    return purchaseReturnTodayNew;
  }

  public void setPurchaseReturnTodayNew(Long purchaseReturnTodayNew) {
    this.purchaseReturnTodayNew = purchaseReturnTodayNew;
  }

  public Long getPurchaseReturnEarlyNew() {
    return purchaseReturnEarlyNew;
  }

  public void setPurchaseReturnEarlyNew(Long purchaseReturnEarlyNew) {
    this.purchaseReturnEarlyNew = purchaseReturnEarlyNew;
  }

  public Long getPurchaseReturnNew() {
    return purchaseReturnNew;
  }

  public void setPurchaseReturnNew(Long purchaseReturnNew) {
    this.purchaseReturnNew = purchaseReturnNew;
  }

  public Long getPurchaseReturnSellerAccept() {
    return purchaseReturnSellerAccept;
  }

  public void setPurchaseReturnSellerAccept(Long purchaseReturnSellerAccept) {
    this.purchaseReturnSellerAccept = purchaseReturnSellerAccept;
  }

  public Long getPurchaseReturnSellerRefused() {
    return purchaseReturnSellerRefused;
  }

  public void setPurchaseReturnSellerRefused(Long purchaseReturnSellerRefused) {
    this.purchaseReturnSellerRefused = purchaseReturnSellerRefused;
  }

  public Long getPurchaseReturnInProgress() {
    return purchaseReturnInProgress;
  }

  public void setPurchaseReturnInProgress(Long purchaseReturnInProgress) {
    this.purchaseReturnInProgress = purchaseReturnInProgress;
  }
}
