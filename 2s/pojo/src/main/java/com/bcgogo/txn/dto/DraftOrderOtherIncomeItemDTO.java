package com.bcgogo.txn.dto;

import com.bcgogo.BooleanEnum;
import com.bcgogo.enums.OtherIncomeCalculateWay;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-12
 * Time: 上午9:27
 * To change this template use File | Settings | File Templates.
 */
public class DraftOrderOtherIncomeItemDTO {
  private Long id;
  private Long shopId;
  private Long orderId;
  private String name;
  private String memo;
  private Double price;
  private Long itemId;

  private BooleanEnum calculateCostPrice;//是否计算成本
  private Double otherIncomeCostPrice; //成本金额

  private OtherIncomeCalculateWay otherIncomeCalculateWay;//计算方式
  private Double otherIncomeRate;//施工单其他费用 材料管理费 计算比率
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }


  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }


  public OtherIncomeCalculateWay getOtherIncomeCalculateWay() {
    return otherIncomeCalculateWay;
  }

  public void setOtherIncomeCalculateWay(OtherIncomeCalculateWay otherIncomeCalculateWay) {
    this.otherIncomeCalculateWay = otherIncomeCalculateWay;
  }

  public BooleanEnum getCalculateCostPrice() {
    return calculateCostPrice;
  }

  public void setCalculateCostPrice(BooleanEnum calculateCostPrice) {
    this.calculateCostPrice = calculateCostPrice;
  }

  public Double getOtherIncomeCostPrice() {
    return otherIncomeCostPrice;
  }

  public void setOtherIncomeCostPrice(Double otherIncomeCostPrice) {
    this.otherIncomeCostPrice = otherIncomeCostPrice;
  }

  public Double getOtherIncomeRate() {
    return otherIncomeRate;
  }

  public void setOtherIncomeRate(Double otherIncomeRate) {
    this.otherIncomeRate = otherIncomeRate;
  }
}
