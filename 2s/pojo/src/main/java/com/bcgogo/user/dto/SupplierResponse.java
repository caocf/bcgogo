package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-20
 * Time: 下午2:08
 * To change this template use File | Settings | File Templates.
 */
public class SupplierResponse implements Comparable<SupplierResponse> {

  private Long supplierId;
  private String unit;
  private String name;
  private String mobile;
  private String address;
  private double totalMoney;         //累计入库金额
  private String lastOrderType;      //单据类型
  private String lastTxnProduct;     //上次交易产品
  private Long lastTxnTime;          //上次交易时间
  private String lastTxnTimeStr;
  private Long lastId;

  public Long getLastId() {
    return lastId;
  }

  public void setLastId(Long lastId) {
    this.lastId = lastId;
  }

  public String getLastOrderType() {
    return lastOrderType;
  }

  public void setLastOrderType(String lastOrderType) {
    this.lastOrderType = lastOrderType;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public double getTotalMoney() {
    return totalMoney;
  }

  public void setTotalMoney(double totalMoney) {
    this.totalMoney = totalMoney;
  }

  public String getLastTxnProduct() {
    return lastTxnProduct;
  }

  public void setLastTxnProduct(String lastTxnProduct) {
    this.lastTxnProduct = lastTxnProduct;
  }

  public Long getLastTxnTime() {
    return lastTxnTime;
  }

  public void setLastTxnTime(Long lastTxnTime) {
    this.lastTxnTime = lastTxnTime;
  }

  public String getLastTxnTimeStr() {
    return lastTxnTimeStr;
  }

  public void setLastTxnTimeStr(String lastTxnTimeStr) {
    this.lastTxnTimeStr = lastTxnTimeStr;
  }

  @Override
  public int compareTo(SupplierResponse o) {
    if (this.getLastTxnTime() != null && o.getLastTxnTime() != null) {
      return (int) (o.getLastTxnTime() - this.getLastTxnTime());
    } else if (this.getLastTxnTime() == null && o.getLastTxnTime() != null) {
      return 1;
    } else if (this.getLastTxnTime() != null && o.getLastTxnTime() == null) {
      return -1;
    } else {
      return 0;
    }
  }
}
