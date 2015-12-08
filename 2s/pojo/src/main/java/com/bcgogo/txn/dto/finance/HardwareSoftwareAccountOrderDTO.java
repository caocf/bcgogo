package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-27
 * Time: 上午10:49
 */
public class HardwareSoftwareAccountOrderDTO {
  private Long shopId;
  private Double totalAmount;     //应收金额
  private Double receivableAmount;     //应收金额
  private Double receivedAmount;        //已收金额
  private Long startTime;            //总的开始时间
  private Long endTime;              //总的截止时间
  private Long instalmentPlanId;                //分期id
  private Long currentInstalmentPlanEndTime;  //当前分期阶段结束日期
  private String receivableContent;
  private String memo;
  private PaymentStatus status;    //状态
  private Double onlinePaidAmount = 0.0d;
  private Double doorChargePaidAmount = 0.0d;
  private InstalmentPlanDTO instalmentPlanDTO = null;
  private List<HardwareSoftwareAccountRecordDTO> records = new ArrayList<HardwareSoftwareAccountRecordDTO>();

  private String receivableMethodDetail; //收款方式
  private String paymentDetailInfo;       //支付详细信息
  private String auditDetailInfo;       //审核详细信息
  private String buyingExpense; //软件购买费用 硬件购买费用

  public void combineContent() {
    if (instalmentPlanDTO != null) {
      this.setReceivableMethodDetail("分期付款" + this.getInstalmentPlanDTO().getPeriods() + "期");
      for (HardwareSoftwareAccountRecordDTO recordDTO : records) {
        //现金 银联
        if (PaymentMethod.DOOR_CHARGE.name().equals(recordDTO.getPaymentMethod())) {
          this.setDoorChargePaidAmount(this.getDoorChargePaidAmount() + recordDTO.getPaidAmount());
        } else if (PaymentMethod.ONLINE_PAYMENT.name().equals(recordDTO.getPaymentMethod())) {
          this.setOnlinePaidAmount(this.getOnlinePaidAmount() + recordDTO.getPaidAmount());
        }
        recordDTO.combineContent();
      }
      this.setBuyingExpense("软件购买费用");
      this.setPaymentDetailInfo("已付￥" + this.getReceivedAmount() + "（现金￥" + this.getDoorChargePaidAmount()
          + "；银联￥" + this.getOnlinePaidAmount() + "；）挂账￥" + this.getReceivableAmount() + " ");
    } else {
      if (CollectionUtil.isNotEmpty(records)) {
        HardwareSoftwareAccountRecordDTO recordDTO = records.get(0);
        if (PaymentMethod.ONLINE_PAYMENT.name().equals(recordDTO.getPaymentMethod())) {
          this.setReceivableMethodDetail("银联");
        } else {
          this.setReceivableMethodDetail("现金");
        }
        if ("SOFTWARE".equals(recordDTO.getPaymentType())) {
          this.setBuyingExpense("软件购买费用");
        } else {
          this.setBuyingExpense("硬件购买费用");
        }
        if (recordDTO.getAuditorId() != null) {
          this.setAuditDetailInfo(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getAuditTime())
              + "&nbsp;&nbsp;&nbsp;" + recordDTO.getAuditorName() + "审核");
        }
        if (recordDTO.getPaymentTime() != null) {
          this.setPaymentDetailInfo(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getPaymentTime()) + "支付"
              + "&nbsp;&nbsp;&nbsp;" + recordDTO.getPayeeName() + "收取");
        }
        records = null;
      }
    }
  }


  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Double getReceivableAmount() {
    return receivableAmount;
  }

  public void setReceivableAmount(Double receivableAmount) {
    this.receivableAmount = receivableAmount;
  }

  public Double getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(Double receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public Long getInstalmentPlanId() {
    return instalmentPlanId;
  }

  public void setInstalmentPlanId(Long instalmentPlanId) {
    this.instalmentPlanId = instalmentPlanId;
  }

  public Long getCurrentInstalmentPlanEndTime() {
    return currentInstalmentPlanEndTime;
  }

  public void setCurrentInstalmentPlanEndTime(Long currentInstalmentPlanEndTime) {
    this.currentInstalmentPlanEndTime = currentInstalmentPlanEndTime;
  }

  public String getReceivableContent() {
    return receivableContent;
  }

  public void setReceivableContent(String receivableContent) {
    this.receivableContent = receivableContent;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public List<HardwareSoftwareAccountRecordDTO> getRecords() {
    return records;
  }

  public void setRecords(List<HardwareSoftwareAccountRecordDTO> records) {
    this.records = records;
  }

  public Double getOnlinePaidAmount() {
    return onlinePaidAmount;
  }

  public void setOnlinePaidAmount(Double onlinePaidAmount) {
    this.onlinePaidAmount = onlinePaidAmount;
  }

  public Double getDoorChargePaidAmount() {
    return doorChargePaidAmount;
  }

  public void setDoorChargePaidAmount(Double doorChargePaidAmount) {
    this.doorChargePaidAmount = doorChargePaidAmount;
  }

  public InstalmentPlanDTO getInstalmentPlanDTO() {
    return instalmentPlanDTO;
  }

  public void setInstalmentPlanDTO(InstalmentPlanDTO instalmentPlanDTO) {
    this.instalmentPlanDTO = instalmentPlanDTO;
  }

  public String getReceivableMethodDetail() {
    return receivableMethodDetail;
  }

  public void setReceivableMethodDetail(String receivableMethodDetail) {
    this.receivableMethodDetail = receivableMethodDetail;
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

  public String getBuyingExpense() {
    return buyingExpense;
  }

  public void setBuyingExpense(String buyingExpense) {
    this.buyingExpense = buyingExpense;
  }
}
