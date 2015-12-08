package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-6
 * Time: 下午9:03
 * To change this template use File | Settings | File Templates.
 */
public class WashOrderDTO extends BcgogoOrderDto {
  public WashOrderDTO() {
  }

  @Override
  public BcgogoOrderItemDto[] getItemDTOs() {
    return null;
  }

  //  private Long id;
//  private Long shopId;
  private String creationDate;
  private Long customerId;
  /**
   * 0:会员卡充值      OrderTypes.RECHARGE
   * 1:会员卡洗车      OrderTypes.WASH_MEMBER
   * 2:现金洗车        OrderTypes.WASH
   */
  private OrderTypes orderType;
  private Long cardId;
  private double cashNum;
  private Long state;
  private Long washTimes;
  private String washWorker;
  private Double costPrice;
  private String contactNum;//联系方式
  private Long vestDate;
  private String vestDateStr;
  private Double percentage;
  private Double PercentageAmount;

  public Double getPercentage() {
      return percentage;
  }

  public Double getPercentageAmount() {
      return PercentageAmount;
  }

  public void setPercentage(Double percentage) {
      this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
      PercentageAmount = percentageAmount;
  }

  public Long getVestDate() {
      return vestDate;
  }

  public String getVestDateStr() {
      return vestDateStr;
  }

  public void setVestDate(Long vestDate) {
      this.vestDate = vestDate;
  }

  public void setVestDateStr(String vestDateStr) {
      this.vestDateStr = vestDateStr;
  }

  private String vehicle; //add by liuWei 保存到order_index表
  private String customer; //add by liuWei 保存到order_index表

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getVehicle() {
    return vehicle;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public String getWashWorker() {
    return washWorker;
  }

  public void setWashWorker(String washWorker) {
    this.washWorker = washWorker;
  }

  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

//  public Long getId() {
//    return this.id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }
//
//  public Long getShopId() {
//    return shopId;
//  }
//
//  public void setShopId(Long shopId) {
//    this.shopId = shopId;
//  }

  public String getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public Long getCardId() {
    return cardId;
  }

  public void setCardId(Long cardId) {
    this.cardId = cardId;
  }

  public double getCashNum() {
    return cashNum;
  }

  public void setCashNum(double cashNum) {
    this.cashNum = cashNum;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public Long getWashTimes() {
    return washTimes;
  }

  public void setWashTimes(Long washTimes) {
    this.washTimes = washTimes;
  }


  @Override
  public String toString() {
    return "WashOrderDTO{" +
        "id=" + this.getId() +
        ", shopId=" + this.getShopId() +
        ", creationDate='" + creationDate + '\'' +
        ", customerId=" + customerId +
        ", orderType=" + orderType +
        ", cardId=" + cardId +
        ", cashNum=" + cashNum +
        ", state=" + state +
        ", washTimes=" + washTimes +
        ", costPrice=" + costPrice +
        '}';
  }

  public String getContactNum() {
    return contactNum;
  }

  public void setContactNum(String contactNum) {
    this.contactNum = contactNum;
  }
}
