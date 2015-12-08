package com.bcgogo.wx;

/**
 *微信端页面展示用到的字段
 * User: ndong
 * Date: 14-9-24
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class WXOrderDTO {
  private Long orderId;
  private String shopName;
  private String vehicle;
  private String orderType;
  private String orderDetailUrl;
  private String vestDateStr;
  private String total;
  private String payMethod;

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public String getOrderDetailUrl() {
    return orderDetailUrl;
  }

  public void setOrderDetailUrl(String orderDetailUrl) {
    this.orderDetailUrl = orderDetailUrl;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getTotal() {
    return total;
  }

  public void setTotal(String total) {
    this.total = total;
  }

  public String getPayMethod() {
    return payMethod;
  }

  public void setPayMethod(String payMethod) {
    this.payMethod = payMethod;
  }
}
