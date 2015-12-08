package com.bcgogo.stat.dto;

import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-9-11
 * Time: 上午11:30
 */
public class SupplierRecordDTO {
  private Long id;
  private Long shopId;
  private Long supplierId;
  private Double creditAmount;    //应付款
  private Double debt;      //应收款

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = creditAmount;
  }

  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = NumberUtil.numberValue(debt,0D);
  }

  @Override
  public String toString() {
    return "SupplierRecordDTO{" +
        "creditAmount=" + creditAmount +
        "debt=" + debt +
        ", supplierId=" + supplierId +
        ", shopId=" + shopId +
        ", id=" + id +
        '}';
  }
}
