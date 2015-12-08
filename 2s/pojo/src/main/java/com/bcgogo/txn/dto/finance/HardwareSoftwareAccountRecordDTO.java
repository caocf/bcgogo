package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.BcgogoReceivableStatus;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.enums.txn.finance.ReceivableMethod;
import com.bcgogo.utils.DateUtil;

/**
 * User: ZhangJuntao
 * Date: 13-3-27
 * Time: 上午11:01
 */
public class HardwareSoftwareAccountRecordDTO {
  private Long shopId;
  private Long bcgogoReceivableRecordId;

  private Long bcgogoReceivableOrderId;
  private Long instalmentPlanId;                //分期id
  private Long instalmentPlanItemId;          //还款item期Id
  private String receivableMethod;         //收款方式（分期/全额）
  private String paymentMethod;            //支付方式（在线支付/银联转账/上门收取）
  private String paymentType;              //支付类型（软件/硬件）

  private Double paymentAmount;    //支付金额
  private Double paidAmount;        //已付金额
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
  private Long paymentTime;         //收款时间
  private Long auditTime;           //审核日期
  private String status;  //状态  （待支付/待审核/已支付）
  private String memo;

  private String amountDetail;
  private String receivableMethodDetail; //收款方式
  private String paymentDetailInfo;       //支付详细信息
  private String auditDetailInfo;       //审核详细信息

  public void combineContent() {
    if (!BcgogoReceivableStatus.TO_BE_PAID.name().equals(this.getStatus())) {
      if (PaymentMethod.ONLINE_PAYMENT.name().equals(this.getPaymentMethod())) {
        this.setReceivableMethodDetail("银联");
      } else {
        this.setReceivableMethodDetail("现金");
      }
      if (this.getAuditorId() != null) {
        this.setAuditDetailInfo(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getAuditTime())
            + "&nbsp;&nbsp;&nbsp;" + this.getAuditorName() + "审核");
      }
      if (this.getPaymentTime() != null) {
        this.setPaymentDetailInfo(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getPaymentTime()) + "支付"
            + "&nbsp;&nbsp;&nbsp;" + this.getPayeeName() + "收取");
        this.setAmountDetail("￥" + this.getPaidAmount());
      }
    } else {
      this.setAmountDetail("￥" + this.getPaymentAmount());
      this.setPaymentDetailInfo(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getPaymentTime()) + "截止时间"
          + "&nbsp;&nbsp;&nbsp;" + this.getPayeeName() + "待收取");
    }

  }

  public String getPaymentDetailInfo() {
    return paymentDetailInfo;
  }

  public void setPaymentDetailInfo(String paymentDetailInfo) {
    this.paymentDetailInfo = paymentDetailInfo;
  }

  public String getAuditDetailInfo() {
    return auditDetailInfo;
  }

  public void setAuditDetailInfo(String auditDetailInfo) {
    this.auditDetailInfo = auditDetailInfo;
  }

  public String getPayeeName() {
    return payeeName;
  }

  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }

  public String getOperatorName() {
    return operatorName;
  }

  public void setOperatorName(String operatorName) {
    this.operatorName = operatorName;
  }

  public String getSubmitterName() {
    return submitterName;
  }

  public void setSubmitterName(String submitterName) {
    this.submitterName = submitterName;
  }

  public String getAuditorName() {
    return auditorName;
  }

  public void setAuditorName(String auditorName) {
    this.auditorName = auditorName;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getBcgogoReceivableRecordId() {
    return bcgogoReceivableRecordId;
  }

  public void setBcgogoReceivableRecordId(Long bcgogoReceivableRecordId) {
    this.bcgogoReceivableRecordId = bcgogoReceivableRecordId;
  }

  public Long getBcgogoReceivableOrderId() {
    return bcgogoReceivableOrderId;
  }

  public void setBcgogoReceivableOrderId(Long bcgogoReceivableOrderId) {
    this.bcgogoReceivableOrderId = bcgogoReceivableOrderId;
  }

  public Long getInstalmentPlanId() {
    return instalmentPlanId;
  }

  public void setInstalmentPlanId(Long instalmentPlanId) {
    this.instalmentPlanId = instalmentPlanId;
  }

  public Long getInstalmentPlanItemId() {
    return instalmentPlanItemId;
  }

  public void setInstalmentPlanItemId(Long instalmentPlanItemId) {
    this.instalmentPlanItemId = instalmentPlanItemId;
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
  }

  public String getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public Double getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(Double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public Long getOperatorTime() {
    return operatorTime;
  }

  public void setOperatorTime(Long operatorTime) {
    this.operatorTime = operatorTime;
  }

  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }

  public Long getSubmitterId() {
    return submitterId;
  }

  public void setSubmitterId(Long submitterId) {
    this.submitterId = submitterId;
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

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }


  public String getReceivableMethodDetail() {
    return receivableMethodDetail;
  }

  public void setReceivableMethodDetail(String receivableMethodDetail) {
    this.receivableMethodDetail = receivableMethodDetail;
  }

  public String getAmountDetail() {
    return amountDetail;
  }

  public void setAmountDetail(String amountDetail) {
    this.amountDetail = amountDetail;
  }
}
