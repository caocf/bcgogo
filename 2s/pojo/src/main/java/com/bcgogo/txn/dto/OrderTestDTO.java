package com.bcgogo.txn.dto;


/**
 * Created by IntelliJ IDEA.
 * User: QiuXinyu
 * Date: 12-6-29
 * Time: 上午10:50
 * To change this template use File | Settings | File Templates.
 */
public class OrderTestDTO {
  private double activeRepairOrderAmount;
  private double cancelRepairOrderAmount;
  private double activePurchaseInvenoryAmount;
  private double cancelPurchaseInventoryAmount;
  private double activeSalesOrderAmount;
  private double cancelSalesOrderAmount;
  private double activePurchaseReturnAmount;
  private double cancelPurchaseReturnAmount;

  public double getActiveRepairOrderAmount() {
    return activeRepairOrderAmount;
  }

  public void setActiveRepairOrderAmount(double activeRepairOrderAmount) {
    this.activeRepairOrderAmount = activeRepairOrderAmount;
  }

  public double getCancelRepairOrderAmount() {
    return cancelRepairOrderAmount;
  }

  public void setCancelRepairOrderAmount(double cancelRepairOrderAmount) {
    this.cancelRepairOrderAmount = cancelRepairOrderAmount;
  }

  public double getActivePurchaseInvenoryAmount() {
    return activePurchaseInvenoryAmount;
  }

  public void setActivePurchaseInvenoryAmount(double activePurchaseInvenoryAmount) {
    this.activePurchaseInvenoryAmount = activePurchaseInvenoryAmount;
  }

  public double getCancelPurchaseInventoryAmount() {
    return cancelPurchaseInventoryAmount;
  }

  public void setCancelPurchaseInventoryAmount(double cancelPurchaseInventoryAmount) {
    this.cancelPurchaseInventoryAmount = cancelPurchaseInventoryAmount;
  }

  public double getActiveSalesOrderAmount() {
    return activeSalesOrderAmount;
  }

  public void setActiveSalesOrderAmount(double activeSalesOrderAmount) {
    this.activeSalesOrderAmount = activeSalesOrderAmount;
  }

  public double getCancelSalesOrderAmount() {
    return cancelSalesOrderAmount;
  }

  public void setCancelSalesOrderAmount(double cancelSalesOrderAmount) {
    this.cancelSalesOrderAmount = cancelSalesOrderAmount;
  }

  public double getActivePurchaseReturnAmount() {
    return activePurchaseReturnAmount;
  }

  public void setActivePurchaseReturnAmount(double activePurchaseReturnAmount) {
    this.activePurchaseReturnAmount = activePurchaseReturnAmount;
  }

  public double getCancelPurchaseReturnAmount() {
    return cancelPurchaseReturnAmount;
  }

  public void setCancelPurchaseReturnAmount(double cancelPurchaseReturnAmount) {
    this.cancelPurchaseReturnAmount = cancelPurchaseReturnAmount;
  }

  public double getInventoryAmount(){
//    return activePurchaseInvenoryAmount + cancelSalesOrderAmount + cancelPurchaseReturnAmount +
//        cancelRepairOrderAmount - cancelPurchaseInventoryAmount - cancelSalesOrderAmount -
//        activePurchaseReturnAmount - activeRepairOrderAmount ;
    return activePurchaseInvenoryAmount
        - activePurchaseReturnAmount
        - activeRepairOrderAmount
        - activeSalesOrderAmount ;

  }

}
