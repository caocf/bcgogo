package com.bcgogo.txn.dto;

/**
 * 退货单月统计DTO
 * Created by IntelliJ IDEA.
 * User:
 * Date: 12-11-3
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseReturnMonthStatDTO {

  private Long supplierIdOrdProductId;//商品或供应商id
  private Long shopId;
  private Long supplierId;
  private Long productId;
  private Integer statYear;
  private Integer statMonth;
  private int times;    //次数
  private double amount;   //数量
  private double total;     //总额
  private int returnProductCategories;//退货商品种类

  public Long getSupplierIdOrdProductId() {
    return supplierIdOrdProductId;
  }

  public void setSupplierIdOrdProductId(Long supplierIdOrdProductId) {
    this.supplierIdOrdProductId = supplierIdOrdProductId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public int getReturnProductCategories() {
    return returnProductCategories;
  }

  public void setReturnProductCategories(int returnProductCategories) {
    this.returnProductCategories = returnProductCategories;
  }

  @Override
  public String toString() {
    return "PurchaseReturnMonthStatDTO{" +
        "shopId=" + shopId +
        ", supplierId=" + supplierId +
        ", productId=" + productId +
        ", statYear=" + statYear +
        ", statMonth=" + statMonth +
        ", times=" + times +
        ", amount=" + amount +
        ", total=" + total +
        ", returnProductCategories=" + returnProductCategories +
        '}';
  }
}
