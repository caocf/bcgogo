package com.bcgogo.txn.dto;

import com.bcgogo.enums.DebtStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhouxiaochen
 * Date: 11-12-15
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class DebtDTO implements Serializable {

  private String id;
  private Long shopId;
  private Long customerId;
  private String customerIdStr;
  private String customerName;
  private Long orderId;
  private String orderIdStr;
  private Long recievableId;
  private String receivableIdStr;
  private OrderTypes orderType;
  private Long orderTime;
  private String vehicleNumber;
  private String content;      //内容
  private String service;       // 施工内容
  private String material;      //材料
  private double totalAmount;      //消费金额
  private double settledAmount;   //实收金额
  private Double discount;
  private double debt;  //欠款金额
  private Long payTime;
  private String payTimeStr;
  private Long remindTime;
  private String date;
  private DebtStatus status;
  private String orderTimeStr;
  private String shortMaterialStr;//施工内容缩写
  private String receiptNo;

  private Double totalDebt;
  private Double afterMemberDiscountTotal;
  private String remindStatus;

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getShortMaterialStr() {
    return shortMaterialStr;
  }

  public void setShortMaterialStr(String shortMaterialStr) {
    this.shortMaterialStr = shortMaterialStr;
  }

  public DebtStatus getStatus() {
    return status;
  }

  public void setStatus(DebtStatus status) {
    this.status = status;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  private String remindTimeStr;

  public String getRemindTimeStr() {
    return remindTimeStr;
  }

  public void setRemindTimeStr(String remindTimeStr) {
    this.remindTimeStr = remindTimeStr;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerIdStr=StringUtil.longToString(customerId,"");
    this.customerId = customerId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderIdStr=StringUtil.longToString(orderId,"");
    this.orderId = orderId;
  }

  public Long getRecievableId() {
    return recievableId;
  }

  public void setRecievableId(Long receivableId) {
    if (receivableId != null) {
      receivableIdStr = String.valueOf(receivableId);
  }
    this.recievableId = receivableId;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public Long getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Long orderTime) {
    this.orderTime = orderTime;
  }

  public String getVehicleNumber() {
    return vehicleNumber;
  }

  public void setVehicleNumber(String vehicleNumber) {
    this.vehicleNumber = vehicleNumber;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
    setShortMaterialStr(StringUtil.getShortString(material,0,5));
    }

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public double getDebt() {
    return debt;
  }

  public void setDebt(double debt) {
    this.debt = debt;
  }

  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }

  public String getPayTimeStr() {
    return payTimeStr;
  }

  public void setPayTimeStr(String payTimeStr) {
    this.payTimeStr = payTimeStr;
  }

  public String getOrderTimeStr() {
    return orderTimeStr;
  }

  public void setOrderTimeStr(String orderTimeStr) {
    this.orderTimeStr = orderTimeStr;
  }

  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;

  }

  public String getReceivableIdStr() {
    return receivableIdStr;
  }

  public void setReceivableIdStr(String receivableIdStr) {
    this.receivableIdStr = receivableIdStr;
  }

  public Double getTotalDebt() {
    return totalDebt;
  }

  public void setTotalDebt(Double totalDebt) {
    this.totalDebt = NumberUtil.round(totalDebt,NumberUtil.MONEY_PRECISION);
  }

  public Double getAfterMemberDiscountTotal() {
    return afterMemberDiscountTotal;
  }

  public void setAfterMemberDiscountTotal(Double afterMemberDiscountTotal) {
    this.afterMemberDiscountTotal = NumberUtil.round(afterMemberDiscountTotal,NumberUtil.MONEY_PRECISION);
  }

  public String getRemindStatus() {
    return remindStatus;
  }

  public void setRemindStatus(String remindStatus) {
    this.remindStatus = remindStatus;
  }
}
