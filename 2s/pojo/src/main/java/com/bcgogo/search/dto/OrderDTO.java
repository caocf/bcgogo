package com.bcgogo.search.dto;


import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-20
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public class OrderDTO implements Serializable {

  private Long orderId;
  private OrderTypes orderType;
  private Long consumeDate;
  private String consumeDateStr;
  private String vehicle;
  private String content;
  private String services;
  private String material;
  private double totalMoney;
  private Long completedDate;
  private String completedDateStr;
  private OrderStatus status;
  private String statusStr;
  private double arrears;
  private Long paymentTime;
  private String paymentTimeStr;
  private String url;
  private Double discount;
  private Double debt;
  private Double memberBalancePay;
  private Double accumulatePointsPay;
  private Integer accumulatePointsAmount;

  private Long leaveFactoryTime;
  private String leaveFactoryTimeStr;

  //add by WLF
  private String receiptNo;
  private double productAmount;

  public OrderTypes getOrderType() {
    return orderType;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getConsumeDate() {
    return consumeDate;
  }

  public String getVehicle() {
    return vehicle;
  }

  public String getContent() {
    return content;
  }

  public String getMaterial() {
    return material;
  }

  public Long getCompletedDate() {
    return completedDate;
  }

  public OrderStatus getStatus() {
    return status;
  }


  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setConsumeDate(Long consumeDate) {
    this.consumeDate = consumeDate;
    if (consumeDate > 0) {
      this.consumeDateStr = DateUtil.convertDateLongToString(consumeDate);
    } else {
      this.consumeDateStr = "";
    }
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public void setCompletedDate(Long completedDate) {
    this.completedDate = completedDate;
    if (completedDate != null && completedDate > 0) {
      this.completedDateStr = DateUtil.convertDateLongToString(completedDate);
    } else {
      this.completedDateStr = "";
    }
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
    if (paymentTime != null && paymentTime > 0) {
      this.paymentTimeStr = DateUtil.convertDateLongToString(paymentTime);
    } else {
      this.paymentTimeStr = "";
    }
  }

  public double getArrears() {
    return arrears;
  }

  public void setArrears(double arrears) {
    this.arrears = arrears;
  }

  public String getServices() {
    return services;
  }

  public void setServices(String services) {
    this.services = services;
  }

  public double getTotalMoney() {
    return totalMoney;
  }

  public void setTotalMoney(double totalMoney) {
    this.totalMoney = totalMoney;
  }

  public String getConsumeDateStr() {
    return consumeDateStr;
  }

  public String getCompletedDateStr() {
    return completedDateStr;
  }

  public String getPaymentTimeStr() {
    return paymentTimeStr;
  }

  public void setConsumeDateStr(String consumeDateStr) {
    this.consumeDateStr = consumeDateStr;
  }

  public void setCompletedDateStr(String completedDateStr) {
    this.completedDateStr = completedDateStr;
  }

  public void setPaymentTimeStr(String paymentTimeStr) {
    this.paymentTimeStr = paymentTimeStr;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Double getDiscount() {
    return discount;
  }

  public Double getDebt() {
    return debt;
  }

  public Double getMemberBalancePay() {
    return memberBalancePay;
  }

  public Double getAccumulatePointsPay() {
    return accumulatePointsPay;
  }

  public Integer getAccumulatePointsAmount() {
    return accumulatePointsAmount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  public void setMemberBalancePay(Double memberBalancePay) {
    this.memberBalancePay = memberBalancePay;
  }

  public void setAccumulatePointsAmount(Integer accumulatePointsAmount) {
    this.accumulatePointsAmount = accumulatePointsAmount;
  }

  public void setAccumulatePointsPay(Double accumulatePointsPay) {
    this.accumulatePointsPay = accumulatePointsPay;
  }

  public Long getLeaveFactoryTime() {
    return leaveFactoryTime;
  }

  public void setLeaveFactoryTime(Long leaveFactoryTime) {
    this.leaveFactoryTime = leaveFactoryTime;
  }

  public String getLeaveFactoryTimeStr() {
    return leaveFactoryTimeStr;
  }

  public void setLeaveFactoryTimeStr(String leaveFactoryTimeStr) {
    this.leaveFactoryTimeStr = leaveFactoryTimeStr;
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public double getProductAmount() {
    return productAmount;
  }

  public void setProductAmount(double productAmount) {
    this.productAmount = productAmount;
  }
}
