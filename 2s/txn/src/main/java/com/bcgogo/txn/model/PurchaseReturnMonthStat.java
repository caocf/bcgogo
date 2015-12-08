package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 退货单月统计，分 供应商累计，产品累计 两种，supplierId, productId互斥
 * User: Jimuchen
 * Date: 12-11-2
 * Time: 下午2:12
 */
@Entity
@Table(name="purchase_return_month_stat")
public class PurchaseReturnMonthStat extends LongIdentifier{
  private Long shopId;
  private Long supplierId;
  private Long productId;
  private Integer statYear;
  private Integer statMonth;
  private int times;    //次数
  private double amount;   //数量
  private double total;     //总额

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name="product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name="stat_year")
  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  @Column(name="stat_month")
  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name="times")
  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  @Column(name="amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name="total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }
}
