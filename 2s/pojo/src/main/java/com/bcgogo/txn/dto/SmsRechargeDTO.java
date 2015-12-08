package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.enums.PaymentWay;
import com.bcgogo.enums.RechargeMethod;
import com.bcgogo.enums.txn.finance.BcgogoReceivableStatus;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-19
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */
public class SmsRechargeDTO implements Serializable {
  public SmsRechargeDTO() {
  }

  public Long id;
  private Long shopId;
  private Long rechargeTime;
  private String rechargeTimeStr;
  private String rechargeNumber;
  private Double smsBalance;
  private Double rechargeAmount;
  private Long payTime;
  private String payTimeStr;
  private Long userId;
  private Long state;
  private String shopName;
  private String receiptNo;
  private RechargeMethod rechargeMethod;   //充值方式：客户充值   后台充值
  private BcgogoReceivableStatus status;    //状态   待审核  已入账
  private PaymentWay paymentWay;               //支付方式 银联  现金
  private Double presentAmount;
  private Long payeeId;              //收款人（只有后台充值才会有）
  private String payeeName;
  private String submitor;      //录入人
  private OperationLogDTO operationLogDTO;   //审核日志
  private Long auditTime;

  public Double getPresentAmount() {
    return presentAmount;
  }

  public void setPresentAmount(Double presentAmount) {
    this.presentAmount = presentAmount;
  }

  public PaymentWay getPaymentWay() {
    return paymentWay;
  }

  public void setPaymentWay(PaymentWay paymentWay) {
    this.paymentWay = paymentWay;
  }

  public BcgogoReceivableStatus getStatus() {
    return status;
  }

  public void setStatus(BcgogoReceivableStatus status) {
    this.status = status;
  }

  public RechargeMethod getRechargeMethod() {
    return rechargeMethod;
  }

  public void setRechargeMethod(RechargeMethod rechargeMethod) {
    this.rechargeMethod = rechargeMethod;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  private String userName;

  private String statusDesc;

  public void setStatusDesc() {
    if (this.state == null) {
      this.statusDesc = SmsRechargeConstants.RechargeStatusDesc.RECHARGE_STATUS_DESC_FAIL;
    } else if (this.state.equals(SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMMIT)) {
      if (this.payTime == null) {
        this.statusDesc = SmsRechargeConstants.RechargeStatusDesc.RECHARGE_STATUS_DESC_WAITING;
      } else {
        this.statusDesc = SmsRechargeConstants.RechargeStatusDesc.RECHARGE_STATUS_DESC_CONFIRM;
      }
    } else if (this.state.equals(SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMPLETE)) {
      this.statusDesc = SmsRechargeConstants.RechargeStatusDesc.RECHARGE_STATUS_DESC_SUCCESS;
    } else {
      this.statusDesc = SmsRechargeConstants.RechargeStatusDesc.RECHARGE_STATUS_DESC_FAIL;
    }
  }

  public String getPayTimeStr() {
    return payTimeStr;
  }

  public void setPayTimeStr(String payTimeStr) {
    this.payTimeStr = payTimeStr;
  }

  public String getRechargeTimeStr() {
    return rechargeTimeStr;
  }

  public void setRechargeTimeStr(String rechargeTimeStr) {
    this.rechargeTimeStr = rechargeTimeStr;
  }

  public void setStatusDesc(String statusDesc) {
    this.statusDesc = statusDesc;
  }

  public String getStatusDesc() {

    return statusDesc;
  }

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

  public Long getRechargeTime() {
    return rechargeTime;
  }

  public void setRechargeTime(Long rechargeTime) {
    this.rechargeTime = rechargeTime;
    if (rechargeTime != null && !rechargeTime.equals(0L)) {
      this.rechargeTimeStr = DateUtil.dateLongToStr(this.rechargeTime);
    } else {
      this.rechargeTimeStr = "";
    }
  }

  public String getRechargeNumber() {
    return rechargeNumber;
  }

  public void setRechargeNumber(String rechargeNumber) {
    this.rechargeNumber = rechargeNumber;
  }

  public Double getSmsBalance() {
    return smsBalance;
  }

  public void setSmsBalance(Double smsBalance) {
    this.smsBalance = smsBalance;
  }

  public Double getRechargeAmount() {
    return rechargeAmount;
  }

  public void setRechargeAmount(Double rechargeAmount) {
    this.rechargeAmount = rechargeAmount;
  }

  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
    if (this.payTime != null && !this.payTime.equals(0L)) {
      this.payTimeStr = DateUtil.dateLongToStr(this.payTime);
    } else {
      this.payTimeStr = "";
    }
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public String toString() {
    return "id:" + id + "shopId:" + shopId + "rechargeTime:" + rechargeTime + "rechargeNumber:" + rechargeNumber +
        "smsBalance:" + smsBalance + "rechargeAmount:" + rechargeAmount + " payTime:" + payTime + " userId:" + userId +
        "state:" + state;
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

  public String getSubmitor() {
    return submitor;
  }

  public void setSubmitor(String submitor) {
    this.submitor = submitor;
  }

  public OperationLogDTO getOperationLogDTO() {
    return operationLogDTO;
  }

  public void setOperationLogDTO(OperationLogDTO operationLogDTO) {
    this.operationLogDTO = operationLogDTO;
  }

  public Long getAuditTime() {
    return auditTime;
  }

  public void setAuditTime(Long auditTime) {
    this.auditTime = auditTime;
  }

}
