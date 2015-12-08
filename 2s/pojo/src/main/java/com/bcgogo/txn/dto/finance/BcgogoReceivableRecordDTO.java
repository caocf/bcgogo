package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.utils.DateUtil;

/**
 * User: ZhangJuntao
 * Date: 13-3-20
 * Time: 上午10:31
 * 支付记录
 */
public class BcgogoReceivableRecordDTO {
  //order
  private Long orderId;
  private Double orderTotalAmount;            //总金额
  private Double orderReceivableAmount;      //应付总金额
  private Double orderReceivedAmount;       //已付总金额
  private String orderPaymentStatus;
  private String orderReceiptNo;
  private String orderPaymentType;              //支付类型（软件/硬件）
  private String time;                         //试用期截止时间、分期付款 第3期截止时间、硬件未付款录入时间
  private Long orderStartTime;                //总的开始时间
  private Long orderEndTime;                   //总的截止时间
  private Long currentInstalmentPlanEndTime;    //当前分期阶段结束日期
  //relation
  private Long bcgogoReceivableOrderRecordRelationId;
  private String bcgogoReceivableOrderRecordRelationIdStr;
  private String receivableMethod;    //收款方式（分期/全额）
  private String paymentMethod;          //支付方式（在线支付/银联转账/上门收取）
  private String paymentMethodValue;          //支付方式（在线支付/银联转账/上门收取）
  //record
  private Long shopId;
  private String shopName;
  private String shopVersion;//店铺版本
  private String registrationDateStr;//注册时间
  private Long shopReviewDate;//店铺审核时间

  private Long recordPaymentTime;        //收款时间
  private String recordPaymentTimeStr;        //收款时间
  private Double recordPaymentAmount;    //record应支付金额
  private Double recordPaidAmount;    //已经支付金额
  private Long payeeId;               //收款人
  private String payeeName;          //收款人
  private Long operatorId;           //操作人
  private String operatorName;      //操作人
  private Long operatorTime;        //操作时间
  private Long submitterId;         //提交人
  private String submitterName;     //提交人
  private Long submitTime;          //提交日期
  private Long auditorId;           //审核人
  private String auditorName;       //审核人
  private Long auditTime;           //审核日期
  private String status;            //状态  （待支付/待审核/已支付）
  private String receivableContent;
  private Long followId;//跟进人
  private String followName;//跟进人

  //分期
  private Long instalmentPlanId;
  private String instalmentPlanIdStr;
  private Long instalmentPlanItemId;
  private Integer periodNumber; //当前第几期
  private Integer periods; //总期
  private Long instalmentPlanItemPaidAmount;
  private Long instalmentPlanItemPayableAmount;
  private Long instalmentPlanItemEndTime;
  private Long smsRechargeId;

  public void setBcgogoReceivableOrderRecordRelationDTO(BcgogoReceivableOrderRecordRelationDTO bcgogoReceivableOrderRecordRelationDTO){
    if(bcgogoReceivableOrderRecordRelationDTO!=null){
      this.setBcgogoReceivableOrderRecordRelationId(bcgogoReceivableOrderRecordRelationDTO.getId());
      this.setReceivableMethod(bcgogoReceivableOrderRecordRelationDTO.getReceivableMethod().toString());
      this.setPaymentMethod(bcgogoReceivableOrderRecordRelationDTO.getPaymentMethod()==null?null:bcgogoReceivableOrderRecordRelationDTO.getPaymentMethod().toString());
      this.setInstalmentPlanId(bcgogoReceivableOrderRecordRelationDTO.getInstalmentPlanId());
    }
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getPaymentMethodValue() {
    return paymentMethodValue;
  }

  public void setPaymentMethodValue(String paymentMethodValue) {
    this.paymentMethodValue = paymentMethodValue;
  }

  public String getRecordPaymentTimeStr() {
    return recordPaymentTimeStr;
  }

  public void setRecordPaymentTimeStr(String recordPaymentTimeStr) {
    this.recordPaymentTimeStr = recordPaymentTimeStr;
  }

  public String getOrderPaymentStatus() {
    return orderPaymentStatus;
  }

  public void setOrderPaymentStatus(String orderPaymentStatus) {
    this.orderPaymentStatus = orderPaymentStatus;
  }

  public Long getShopReviewDate() {
    return shopReviewDate;
  }

  public void setShopReviewDate(Long shopReviewDate) {
    this.shopReviewDate = shopReviewDate;
  }

  public Long getFollowId() {
    return followId;
  }

  public void setFollowId(Long followId) {
    this.followId = followId;
  }

  public String getFollowName() {
    return followName;
  }

  public void setFollowName(String followName) {
    this.followName = followName;
  }

  public Double getOrderTotalAmount() {
    return orderTotalAmount;
  }

  public void setOrderTotalAmount(Double orderTotalAmount) {
    this.orderTotalAmount = orderTotalAmount;
  }

  public Double getOrderReceivableAmount() {
    return orderReceivableAmount;
  }

  public void setOrderReceivableAmount(Double orderReceivableAmount) {
    this.orderReceivableAmount = orderReceivableAmount;
  }

  public Double getOrderReceivedAmount() {
    return orderReceivedAmount;
  }

  public void setOrderReceivedAmount(Double orderReceivedAmount) {
    this.orderReceivedAmount = orderReceivedAmount;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Long getOrderStartTime() {
    return orderStartTime;
  }

  public void setOrderStartTime(Long orderStartTime) {
    this.orderStartTime = orderStartTime;
  }

  public Long getOrderEndTime() {
    return orderEndTime;
  }

  public void setOrderEndTime(Long orderEndTime) {
    this.orderEndTime = orderEndTime;
  }

  public Long getCurrentInstalmentPlanEndTime() {
    return currentInstalmentPlanEndTime;
  }

  public void setCurrentInstalmentPlanEndTime(Long currentInstalmentPlanEndTime) {
    this.currentInstalmentPlanEndTime = currentInstalmentPlanEndTime;
  }

  public Long getBcgogoReceivableOrderRecordRelationId() {
    return bcgogoReceivableOrderRecordRelationId;
  }

  public void setBcgogoReceivableOrderRecordRelationId(Long bcgogoReceivableOrderRecordRelationId) {
    this.bcgogoReceivableOrderRecordRelationId = bcgogoReceivableOrderRecordRelationId;
    if(bcgogoReceivableOrderRecordRelationId != null){
      this.bcgogoReceivableOrderRecordRelationIdStr = bcgogoReceivableOrderRecordRelationId.toString();
    }
  }

  public String getReceivableMethod() {
    return receivableMethod;
  }

  public void setReceivableMethod(String receivableMethod) {
    this.receivableMethod = receivableMethod;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
    if(PaymentMethod.DOOR_CHARGE.toString().equals(paymentMethod)){
      this.paymentMethodValue="现金支付";
    }else {
      this.paymentMethodValue="银联支付";
    }
  }

  public String getOrderReceiptNo() {
    return orderReceiptNo;
  }

  public void setOrderReceiptNo(String orderReceiptNo) {
    this.orderReceiptNo = orderReceiptNo;
  }

  public String getOrderPaymentType() {
    return orderPaymentType;
  }

  public void setOrderPaymentType(String orderPaymentType) {
    this.orderPaymentType = orderPaymentType;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public Long getRecordPaymentTime() {
    return recordPaymentTime;
  }

  public void setRecordPaymentTime(Long recordPaymentTime) {
    this.recordPaymentTime = recordPaymentTime;
    if(recordPaymentTime!=null){
      this.recordPaymentTimeStr = DateUtil.convertDateLongToString(recordPaymentTime,DateUtil.DATE_STRING_FORMAT_DEFAULT);
    }
  }

  public Double getRecordPaymentAmount() {
    return recordPaymentAmount;
  }

  public void setRecordPaymentAmount(Double recordPaymentAmount) {
    this.recordPaymentAmount = recordPaymentAmount;
  }

  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  public String getPayeeName() {
    return payeeName;
  }

  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public String getOperatorName() {
    return operatorName;
  }

  public void setOperatorName(String operatorName) {
    this.operatorName = operatorName;
  }

  public Long getOperatorTime() {
    return operatorTime;
  }

  public void setOperatorTime(Long operatorTime) {
    this.operatorTime = operatorTime;
  }

  public Long getSubmitterId() {
    return submitterId;
  }

  public void setSubmitterId(Long submitterId) {
    this.submitterId = submitterId;
  }

  public String getSubmitterName() {
    return submitterName;
  }

  public void setSubmitterName(String submitterName) {
    this.submitterName = submitterName;
  }

  public Long getSubmitTime() {
    return submitTime;
  }

  public void setSubmitTime(Long submitTime) {
    this.submitTime = submitTime;
  }

  public Long getAuditorId() {
    return auditorId;
  }

  public void setAuditorId(Long auditorId) {
    this.auditorId = auditorId;
  }

  public String getAuditorName() {
    return auditorName;
  }

  public void setAuditorName(String auditorName) {
    this.auditorName = auditorName;
  }

  public Long getAuditTime() {
    return auditTime;
  }

  public void setAuditTime(Long auditTime) {
    this.auditTime = auditTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getReceivableContent() {
    return receivableContent;
  }

  public void setReceivableContent(String receivableContent) {
    this.receivableContent = receivableContent;
  }

  public Long getInstalmentPlanId() {
    return instalmentPlanId;
  }

  public void setInstalmentPlanId(Long instalmentPlanId) {
    this.instalmentPlanId = instalmentPlanId;
    if (instalmentPlanId != null) {
      this.instalmentPlanIdStr = instalmentPlanId.toString();
    }
  }

  public Long getInstalmentPlanItemId() {
    return instalmentPlanItemId;
  }

  public void setInstalmentPlanItemId(Long instalmentPlanItemId) {
    this.instalmentPlanItemId = instalmentPlanItemId;
  }

  public Long getInstalmentPlanItemPaidAmount() {
    return instalmentPlanItemPaidAmount;
  }

  public void setInstalmentPlanItemPaidAmount(Long instalmentPlanItemPaidAmount) {
    this.instalmentPlanItemPaidAmount = instalmentPlanItemPaidAmount;
  }

  public Long getInstalmentPlanItemPayableAmount() {
    return instalmentPlanItemPayableAmount;
  }

  public void setInstalmentPlanItemPayableAmount(Long instalmentPlanItemPayableAmount) {
    this.instalmentPlanItemPayableAmount = instalmentPlanItemPayableAmount;
  }

  public Long getInstalmentPlanItemEndTime() {
    return instalmentPlanItemEndTime;
  }

  public void setInstalmentPlanItemEndTime(Long instalmentPlanItemEndTime) {
    this.instalmentPlanItemEndTime = instalmentPlanItemEndTime;
  }

  public Double getRecordPaidAmount() {
    return recordPaidAmount;
  }

  public void setRecordPaidAmount(Double recordPaidAmount) {
    this.recordPaidAmount = recordPaidAmount;
  }

  public Integer getPeriodNumber() {
    return periodNumber;
  }

  public void setPeriodNumber(Integer periodNumber) {
    this.periodNumber = periodNumber;
  }

  public Integer getPeriods() {
    return periods;
  }

  public void setPeriods(Integer periods) {
    this.periods = periods;
  }

  public String getRegistrationDateStr() {
    return registrationDateStr;
  }

  public void setRegistrationDateStr(String registrationDateStr) {
    this.registrationDateStr = registrationDateStr;
  }

  public String getShopVersion() {
    return shopVersion;
  }

  public void setShopVersion(String shopVersion) {
    this.shopVersion = shopVersion;
  }

  public String getBcgogoReceivableOrderRecordRelationIdStr() {
    return bcgogoReceivableOrderRecordRelationIdStr;
  }

  public void setBcgogoReceivableOrderRecordRelationIdStr(String bcgogoReceivableOrderRecordRelationIdStr) {
    this.bcgogoReceivableOrderRecordRelationIdStr = bcgogoReceivableOrderRecordRelationIdStr;
  }

  public String getInstalmentPlanIdStr() {
    return instalmentPlanIdStr;
  }

  public void setInstalmentPlanIdStr(String instalmentPlanIdStr) {
    this.instalmentPlanIdStr = instalmentPlanIdStr;
  }

  public Long getSmsRechargeId() {
    return smsRechargeId;
  }

  public void setSmsRechargeId(Long smsRechargeId) {
    this.smsRechargeId = smsRechargeId;
  }
}
