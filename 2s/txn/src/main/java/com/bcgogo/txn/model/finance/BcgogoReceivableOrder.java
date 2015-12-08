package com.bcgogo.txn.model.finance;

import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.enums.txn.finance.BuyChannels;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.dto.finance.BcgogoHardwareReceivableDetailDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableOrderDTO;
import com.bcgogo.txn.dto.finance.HardwareSoftwareAccountOrderDTO;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午8:58
 * bcgogo收款单 对应多个 支付record
 */
@Entity
@Table(name = "bcgogo_receivable_order")
public class BcgogoReceivableOrder extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private Double totalAmount;     //应收金额
  private Double receivableAmount;     //应收金额
  private Double receivedAmount;        //已收金额
  private Long createdTime;
  private Long startTime;            //总的开始时间
  private Long endTime;              //总的截止时间
  private Long instalmentPlanId;                //分期id
  private Long currentInstalmentPlanEndTime;  //当前分期阶段结束日期
  private String receivableContent;
  private String memo;
  private String cancelReason;
  private Long cancelUserId;
  private Long cancelTime;

  private Long province;
  private Long city;
  private Long region;
  private String address;//收货地址
  private String contact;//收货人
  private String mobile;//收货联系人
  private PaymentType paymentType;              //支付类型（软件/硬件）
  private PaymentStatus status;    //状态
  private BuyChannels buyChannels;    //状态
  private String receiptNo;
  private ChargeType chargeType;  //付费类型，一次性付款或者年费

  public BcgogoReceivableOrder() {
    super();
  }

  //硬件新增费用
  public BcgogoReceivableOrder(BcgogoReceivableOrderDTO dto) {
    this.setShopId(dto.getShopId());
    this.setUserId(dto.getUserId());
    this.setTotalAmount(dto.getTotalAmount());
    this.setAddress(dto.getAddress());
    this.setContact(dto.getContact());
    this.setMobile(dto.getMobile());
    this.setReceivableAmount(dto.getTotalAmount());
    this.setReceivedAmount(0.0);
    this.setStatus(PaymentStatus.NON_PAYMENT);
    this.setStartTime(System.currentTimeMillis());
    this.setCreatedTime(System.currentTimeMillis());
    this.setPaymentType(PaymentType.HARDWARE);
    this.setChargeType(ChargeType.ONE_TIME);
    this.setBuyChannels(dto.getBuyChannels());
    this.setProvince(dto.getProvince());
    this.setCity(dto.getCity());
    this.setRegion(dto.getRegion());
  }

  //软件费用
  public void createSoftwareOrder(long shopId, double softPrice, String shopVersionName, long endTime,BuyChannels buyChannels, ChargeType chargeType) {
    this.setShopId(shopId);
    this.setTotalAmount(softPrice);
    this.setReceivableAmount(softPrice);
    this.setReceivedAmount(0.0);
    this.setStatus(PaymentStatus.NON_PAYMENT);
    if(ChargeType.YEARLY.equals(chargeType)){
      this.setReceivableContent(shopVersionName + " 【年费￥" + softPrice + "】");
    }else{
      this.setReceivableContent(shopVersionName + " 【总额￥" + softPrice + "】");
    }
    this.setStartTime(System.currentTimeMillis());
    this.setCreatedTime(System.currentTimeMillis());
    this.setEndTime(endTime);
    this.setCurrentInstalmentPlanEndTime(endTime);
    this.setPaymentType(PaymentType.SOFTWARE);
    this.setBuyChannels(buyChannels);
    this.setChargeType(chargeType);
  }

  public HardwareSoftwareAccountOrderDTO toHardwareSoftwareAccountOrderDTO() {
    HardwareSoftwareAccountOrderDTO dto = new HardwareSoftwareAccountOrderDTO();
    dto.setShopId(this.getShopId());
    dto.setTotalAmount(this.getTotalAmount());     //应收金额
    dto.setReceivableAmount(this.getReceivableAmount());     //应收金额
    dto.setReceivedAmount(this.getReceivedAmount());        //已收金额
    dto.setStartTime(this.getStartTime());            //总的开始时间
    dto.setEndTime(this.getEndTime());              //总的截止时间
    dto.setInstalmentPlanId(this.getInstalmentPlanId());                //分期id
    dto.setCurrentInstalmentPlanEndTime(this.getCurrentInstalmentPlanEndTime());  //当前分期阶段结束日期
    dto.setReceivableContent(this.getReceivableContent());
    dto.setMemo(this.getMemo());
    dto.setStatus(this.getStatus());    //状态
    return dto;
  }

  @Column(name="cancel_time")
  public Long getCancelTime() {
    return cancelTime;
  }

  public void setCancelTime(Long cancelTime) {
    this.cancelTime = cancelTime;
  }

  @Column(name="cancel_user_id")
  public Long getCancelUserId() {
    return cancelUserId;
  }

  public void setCancelUserId(Long cancelUserId) {
    this.cancelUserId = cancelUserId;
  }

  @Column(name = "province")
  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }
  @Column(name = "city")
  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }
  @Column(name = "region")
  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  @Column(name = "address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
  @Column(name = "contact")
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }
  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "cancel_reason")
  public String getCancelReason() {
    return cancelReason;
  }

  public void setCancelReason(String cancelReason) {
    this.cancelReason = cancelReason;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "instalment_plan_id")
  public Long getInstalmentPlanId() {
    return instalmentPlanId;
  }

  public void setInstalmentPlanId(Long instalmentPlanId) {
    this.instalmentPlanId = instalmentPlanId;
  }
  @Column(name = "created_time")
  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @Column(name = "receivable_amount")
  public Double getReceivableAmount() {
    return receivableAmount;
  }

  public void setReceivableAmount(Double receivableAmount) {
    if (receivableAmount != null && receivableAmount < 0) {
      this.receivableAmount = 0d;
    }
    this.receivableAmount = receivableAmount;
  }

  @Column(name = "received_amount")
  public Double getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(Double receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  @Column(name = "start_time")
  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @Column(name = "end_time")
  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  @Column(name = "current_instalment_plan_end_time")
  public Long getCurrentInstalmentPlanEndTime() {
    return currentInstalmentPlanEndTime;
  }

  public void setCurrentInstalmentPlanEndTime(Long currentInstalmentPlanEndTime) {
    this.currentInstalmentPlanEndTime = currentInstalmentPlanEndTime;
  }

  @Column(name = "receivable_content")
  public String getReceivableContent() {
    return receivableContent;
  }

  public void setReceivableContent(String receivableContent) {
    this.receivableContent = receivableContent;
  }

  @Column(name = "total_amount")
  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }


  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }
  @Enumerated(EnumType.STRING)
  @Column(name = "buy_channels")
  public BuyChannels getBuyChannels() {
    return buyChannels;
  }

  public void setBuyChannels(BuyChannels buyChannels) {
    this.buyChannels = buyChannels;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_type")
  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }
  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public BcgogoReceivableOrderDTO toDTO(){
    BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = new BcgogoReceivableOrderDTO();
    bcgogoReceivableOrderDTO.setId(this.getId());
    bcgogoReceivableOrderDTO.setPaymentType(this.getPaymentType());
    bcgogoReceivableOrderDTO.setShopId(this.getShopId());
    bcgogoReceivableOrderDTO.setCreatedTime(this.getCreatedTime());
    bcgogoReceivableOrderDTO.setCurrentInstalmentPlanEndTime(this.getCurrentInstalmentPlanEndTime());
    bcgogoReceivableOrderDTO.setEndTime(this.getEndTime());
    bcgogoReceivableOrderDTO.setStartTime(this.getStartTime());
    bcgogoReceivableOrderDTO.setMemo(this.getMemo());
    bcgogoReceivableOrderDTO.setInstalmentPlanId(this.getInstalmentPlanId());
    bcgogoReceivableOrderDTO.setReceiptNo(this.getReceiptNo());
    bcgogoReceivableOrderDTO.setReceivableAmount(this.getReceivableAmount());
    bcgogoReceivableOrderDTO.setTotalAmount(this.getTotalAmount());
    bcgogoReceivableOrderDTO.setStatus(this.getStatus());
    bcgogoReceivableOrderDTO.setReceivableContent(this.getReceivableContent());
    bcgogoReceivableOrderDTO.setReceivedAmount(this.getReceivedAmount());
    bcgogoReceivableOrderDTO.setBuyChannels(this.getBuyChannels());
    bcgogoReceivableOrderDTO.setCancelReason(this.getCancelReason());
    bcgogoReceivableOrderDTO.setAddress(this.getAddress());
    bcgogoReceivableOrderDTO.setContact(this.getContact());
    bcgogoReceivableOrderDTO.setMobile(this.getMobile());
    bcgogoReceivableOrderDTO.setProvince(this.getProvince());
    bcgogoReceivableOrderDTO.setCity(this.getCity());
    bcgogoReceivableOrderDTO.setRegion(this.getRegion());
    bcgogoReceivableOrderDTO.setUserId(this.getUserId());
    bcgogoReceivableOrderDTO.setCancelUserId(this.getCancelUserId());
    bcgogoReceivableOrderDTO.setCancelTime(this.getCancelTime());
    bcgogoReceivableOrderDTO.setChargeType(this.getChargeType());
    return bcgogoReceivableOrderDTO;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
  @Enumerated(EnumType.STRING)
  @Column(name = "charge_type")

  public ChargeType getChargeType() {
    return chargeType;
  }

  public void setChargeType(ChargeType chargeType) {
    this.chargeType = chargeType;
  }

  public void fromSmsRecharge(SmsRechargeDTO smsRechargeDTO) {
    if(smsRechargeDTO == null) {
      return ;
    }
    this.setShopId(smsRechargeDTO.getShopId());
    this.setTotalAmount(smsRechargeDTO.getRechargeAmount());
    this.setReceivableAmount(0.0);
    this.setReceivedAmount(smsRechargeDTO.getRechargeAmount());
    this.setStatus(PaymentStatus.FULL_PAYMENT);
    this.setReceivableContent("短信充值 【总额￥" + smsRechargeDTO.getRechargeAmount() + "】");
    this.setCreatedTime(smsRechargeDTO.getPayTime());
    this.setReceiptNo(smsRechargeDTO.getReceiptNo());
    this.setPaymentType(PaymentType.SMS_RECHARGE);
  }

}
