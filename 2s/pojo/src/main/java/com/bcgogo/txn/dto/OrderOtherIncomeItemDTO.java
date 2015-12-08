package com.bcgogo.txn.dto;

import com.bcgogo.enums.OtherIncomeCalculateWay;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-19
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */
public class OrderOtherIncomeItemDTO {
   private Long id;
  private String idStr;
  private Long shopId;
  private Long orderId;
  private String name;
  private String memo;
  private Double price;

  private String calculateCostPrice;//是否计算成本
  private Double otherIncomeCostPrice; //成本金额

  private OtherIncomeCalculateWay otherIncomeCalculateWay;//计算方式
  private Double otherIncomeRate;//施工单其他费用 材料管理费 计算比率




  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getCalculateCostPrice() {
    return calculateCostPrice;
  }

  public void setCalculateCostPrice(String calculateCostPrice) {
    this.calculateCostPrice = calculateCostPrice;
  }

  public Double getOtherIncomeCostPrice() {
    return otherIncomeCostPrice;
  }

  public void setOtherIncomeCostPrice(Double otherIncomeCostPrice) {
    this.otherIncomeCostPrice = otherIncomeCostPrice;
  }

  public OtherIncomeCalculateWay getOtherIncomeCalculateWay() {
    return otherIncomeCalculateWay;
  }

  public void setOtherIncomeCalculateWay(OtherIncomeCalculateWay otherIncomeCalculateWay) {
    this.otherIncomeCalculateWay = otherIncomeCalculateWay;
  }

  public Double getOtherIncomeRate() {
    return otherIncomeRate;
  }

  public void setOtherIncomeRate(Double otherIncomeRate) {
    this.otherIncomeRate = otherIncomeRate;
  }

}
