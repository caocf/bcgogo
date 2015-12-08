package com.bcgogo.txn.model;

import com.bcgogo.enums.DebtStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.DebtDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.utils.UserConstant;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: kailinlin
 * Date: 12-1-4
 * Time: 下午3:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "debt")
public class Debt extends LongIdentifier {
  private Long shopId;
  private Long customerId;
  private Long orderId;
  private Long recievableId;
  private String orderType;
  private OrderTypes orderTypeEnum;
  private Long orderTime;
  private String vehicleNumber;
  private String content;      //内容
  private String service;       // 施工内容
  private String material;      //材料
  private double totalAmount;      //消费金额
  private double settledAmount;   //实收金额
  private double debt;  //欠款金额
  private Long payTime;  //消费日期，即vestDate
  private Long remindTime;  //还款日期
  private String status;
  private String receiptNo;
  private DebtStatus statusEnum;
  private String remindStatus;  //提醒状态，未提醒、已提醒、已删除

  @Column(name = "shopId")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
  @Column(name = "customerId")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }
  @Column(name = "orderId")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }
  @Column(name = "orderType")
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }
  @Column(name = "recievableId")
  public Long getRecievableId() {
    return recievableId;
  }

  public void setRecievableId(Long recievableId) {
    this.recievableId = recievableId;
  }
  @Column(name = "orderTime")
  public Long getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Long orderTime) {
    this.orderTime = orderTime;
  }
  @Column(name = "vehicleNumber",length = 20)
  public String getVehicleNumber() {
    return vehicleNumber;
  }

  public void setVehicleNumber(String vehicleNumber) {
    this.vehicleNumber = vehicleNumber;
  }
  @Column(name = "content",length = 200)
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
  @Column(name = "service",length = 200)
  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }
  @Column(name = "material",length = 500)
  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }
  @Column(name = "totalAmount")
  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }
  @Column(name = "settledAmount")
  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = settledAmount;
  }
  @Column(name = "debt")
  public double getDebt() {
    return debt;
  }

  public void setDebt(double debt) {
    this.debt = debt;
  }

  @Column(name = "pay_time")
  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }

  @Column(name = "remind_time")
  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
  }

  @Column(name = "status")
  public String getStatus(){
    return status;
  }
  public void setStatus(String status){
    this.status = status;
  }

  @Column(name="receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name="order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name="status_enum")
  @Enumerated(EnumType.STRING)
  public DebtStatus getStatusEnum() {
    return statusEnum;
  }

  @Column(name="remind_status")
  public String getRemindStatus() {
    return remindStatus;
  }

  public void setRemindStatus(String remindStatus) {
    this.remindStatus = remindStatus;
  }


  public void setStatusEnum(DebtStatus statusEnum) {
    this.statusEnum = statusEnum;
  }

  public DebtDTO toDTO() {
    DebtDTO debtDTO = new DebtDTO();
    debtDTO.setId(this.getId().toString() != null ? this.getId().toString() : "");
    debtDTO.setShopId(getShopId());
    debtDTO.setCustomerId(getCustomerId());
    debtDTO.setOrderId(getOrderId());
    debtDTO.setRecievableId(getRecievableId());
    debtDTO.setOrderType(getOrderTypeEnum());
    debtDTO.setOrderTime(getOrderTime());
    debtDTO.setVehicleNumber(getVehicleNumber());
    debtDTO.setContent(getContent());
    debtDTO.setService(getService());
    debtDTO.setMaterial(getMaterial());
    debtDTO.setTotalAmount(getTotalAmount());
    debtDTO.setSettledAmount(getSettledAmount());
    debtDTO.setDebt(getDebt());
    debtDTO.setPayTime(getPayTime());
    debtDTO.setPayTimeStr(DateUtil.convertDateLongToString(getPayTime(),DateUtil.DATE_STRING_FORMAT_DAY));
    debtDTO.setOrderTimeStr(DateUtil.convertDateLongToString(getOrderTime(),DateUtil.DATE_STRING_FORMAT_DAY));
    debtDTO.setRemindTime(getRemindTime());
    debtDTO.setRemindTimeStr(DateUtil.convertDateLongToString(getRemindTime(),DateUtil.DATE_STRING_FORMAT_DAY));
    debtDTO.setStatus(getStatusEnum());
    debtDTO.setReceiptNo(getReceiptNo());
    debtDTO.setRemindStatus(getRemindStatus());
    return debtDTO;
  }

  public Debt fromDTO(DebtDTO debtDTO, boolean setId){
    if(debtDTO == null)
      return this;
    if(setId){
      setId(Long.parseLong(debtDTO.getId()));
    }
    this.shopId = debtDTO.getShopId();
    this.customerId = debtDTO.getCustomerId();
    this.orderId = debtDTO.getOrderId();
    this.recievableId = debtDTO.getRecievableId();
    this.orderTypeEnum = debtDTO.getOrderType();
    this.orderTime = debtDTO.getOrderTime();
    this.vehicleNumber = debtDTO.getVehicleNumber();
    this.content = debtDTO.getContent();
    this.service = debtDTO.getService();
    this.material = debtDTO.getMaterial();
    this.totalAmount = debtDTO.getTotalAmount();
    this.settledAmount = debtDTO.getSettledAmount();
    this.debt = debtDTO.getDebt();
    this.payTime = debtDTO.getPayTime();
    this.remindTime = debtDTO.getRemindTime();
    this.statusEnum = debtDTO.getStatus();
    if(StringUtil.isNotEmpty(debtDTO.getRemindStatus())){
      this.remindStatus = debtDTO.getRemindStatus();
    }else{
      this.remindStatus = UserConstant.Status.ACTIVITY;
    }
    this.setReceiptNo(debtDTO.getReceiptNo());
    return this;
  }

  public Debt(){}

}
