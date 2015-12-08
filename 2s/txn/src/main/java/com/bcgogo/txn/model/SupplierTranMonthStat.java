package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 供应商交易统计（按月，不累计）
 * User: Jimuchen
 * Date: 12-10-30
 * Time: 下午5:35
 */
@Entity
@Table(name="supplier_tran_month_stat")
public class SupplierTranMonthStat extends LongIdentifier {
  private Long shopId;
  private Long supplierId;
  private Integer statYear;
  private Integer statMonth;
  private int times;    //次数
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

  @Column(name="total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }
}
