package com.bcgogo.txn.dto.finance;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.enums.shop.BargainStatus;
import com.bcgogo.enums.txn.finance.*;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-20
 * Time: 上午10:31
 * 订单
 */
public class BcgogoReceivableOrderDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long userId;
  private String shopName;
  private String shopMobile;
  private String shopVersion;
  private BargainStatus bargainStatus;
  private Double bargainPrice;

  private Double total;     //硬件总数
  private Double totalAmount;     //应收金额
  private Double receivableAmount;     //应收金额
  private Double receivedAmount;        //已收金额
  private Long startTime;            //总的开始时间
  private Long endTime;              //总的截止时间
  private Long instalmentPlanId;                //分期id
  private Long currentInstalmentPlanEndTime;  //当前分期阶段结束日期
  private String receivableContent;
  private String memo;
  private String cancelReason;
  private Long cancelUserId;
  private Long cancelTime;

  private String cancelOptInfo;
  private PaymentType paymentType;              //类型（软件/硬件）
  private PaymentStatus status;    //状态
  private String statusValue;    //状态
  private String receiptNo;
  private Long followId;//跟进人
  private String followName;//跟进人
  private Long createdTime;//销售时间
  private String createdTimeStr;//销售时间
  private BuyChannels buyChannels;
  private ChargeType chargeType;
  private Long bcgogoReceivableOrderToBePaidRecordRelationId;//待支付记录和order 关系

  private ReceivableMethod receivableMethod;

  private BcgogoReceivableRecordDTO bcgogoReceivableOrderToBePaidRecordDTO;//详细

  private InstalmentPlanDTO instalmentPlanDTO;//分期
  private List<BcgogoReceivableOrderItemDTO> bcgogoReceivableOrderItemDTOList;
  private List<BcgogoReceivableRecordDTO> bcgogoReceivableOrderRecordDTOList;
  private List<BcgogoReceivableRecordDTO> bcgogoReceivableOrderPaidRecordDTOList;
  private List<OperationLogDTO> operationLogDTOList;
  private Long province;
  private Long city;
  private Long region;
  private String address;
  private String addressDetail;
  private String contact;
  private String mobile;

  private Double currentPayableAmount;//如果是分期   那把所有过期和当期的 累加
  private String currentPeriodNumberInfo;//如果是分期   那把所有过期和当期
  private String shopOwner;
  private Double oldTotalAmount;

  public Long getCancelTime() {
    return cancelTime;
  }

  public void setCancelTime(Long cancelTime) {
    this.cancelTime = cancelTime;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public ChargeType getChargeType() {
    return chargeType;
  }

  public void setChargeType(ChargeType chargeType) {
    this.chargeType = chargeType;
  }

  public BargainStatus getBargainStatus() {
    return bargainStatus;
  }

  public void setBargainStatus(BargainStatus bargainStatus) {
    this.bargainStatus = bargainStatus;
  }

  public Double getBargainPrice() {
    return bargainPrice;
  }

  public void setBargainPrice(Double bargainPrice) {
    this.bargainPrice = bargainPrice;
  }

  public String getStatusValue() {
    return statusValue;
  }

  public void setStatusValue(String statusValue) {
    this.statusValue = statusValue;
  }

  public String getCancelOptInfo() {
    return cancelOptInfo;
  }

  public void setCancelOptInfo(String cancelOptInfo) {
    this.cancelOptInfo = cancelOptInfo;
  }

  public Long getCancelUserId() {
    return cancelUserId;
  }

  public void setCancelUserId(Long cancelUserId) {
    this.cancelUserId = cancelUserId;
  }

  public Double getCurrentPayableAmount() {
    return currentPayableAmount;
  }

  public void setCurrentPayableAmount(Double currentPayableAmount) {
    this.currentPayableAmount = currentPayableAmount;
  }

  public String getCurrentPeriodNumberInfo() {
    return currentPeriodNumberInfo;
  }

  public void setCurrentPeriodNumberInfo(String currentPeriodNumberInfo) {
    this.currentPeriodNumberInfo = currentPeriodNumberInfo;
  }

  public List<BcgogoReceivableRecordDTO> getBcgogoReceivableOrderRecordDTOList() {
    return bcgogoReceivableOrderRecordDTOList;
  }

  public void setBcgogoReceivableOrderRecordDTOList(List<BcgogoReceivableRecordDTO> bcgogoReceivableOrderRecordDTOList) {
    this.bcgogoReceivableOrderRecordDTOList = bcgogoReceivableOrderRecordDTOList;
  }

  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  public List<BcgogoReceivableOrderItemDTO> getBcgogoReceivableOrderItemDTOList() {
    return bcgogoReceivableOrderItemDTOList;
  }

  public void setBcgogoReceivableOrderItemDTOList(List<BcgogoReceivableOrderItemDTO> bcgogoReceivableOrderItemDTOList) {
    this.bcgogoReceivableOrderItemDTOList = bcgogoReceivableOrderItemDTOList;
    this.total = 0d;
    if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderItemDTOList)){
      for(BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO : bcgogoReceivableOrderItemDTOList){
        this.total+=bcgogoReceivableOrderItemDTO.getAmount();
      }
    }
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getShopMobile() {
    return shopMobile;
  }

  public void setShopMobile(String shopMobile) {
    this.shopMobile = shopMobile;
  }

  public BuyChannels getBuyChannels() {
    return buyChannels;
  }

  public void setBuyChannels(BuyChannels buyChannels) {
    this.buyChannels = buyChannels;
  }

  public String getFollowName() {
    return followName;
  }

  public void setFollowName(String followName) {
    this.followName = followName;
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

  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
    if(PaymentStatus.CANCELED.equals(status)){
      this.statusValue="交易已取消";
    }else if(PaymentStatus.FULL_PAYMENT.equals(status)){
      this.statusValue="已支付";
    }else if(PaymentStatus.NON_PAYMENT.equals(status)){
      this.statusValue="待支付";
    }else if(PaymentStatus.SHIPPED.equals(status)){
      this.statusValue="已发货";
    }
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getFollowId() {
    return followId;
  }

  public void setFollowId(Long followId) {
    this.followId = followId;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
    this.createdTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT,createdTime);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null) idStr = id.toString();
  }

  public Long getBcgogoReceivableOrderToBePaidRecordRelationId() {
    return bcgogoReceivableOrderToBePaidRecordRelationId;
  }

  public void setBcgogoReceivableOrderToBePaidRecordRelationId(Long bcgogoReceivableOrderToBePaidRecordRelationId) {
    this.bcgogoReceivableOrderToBePaidRecordRelationId = bcgogoReceivableOrderToBePaidRecordRelationId;
  }

  public String getCancelReason() {
    return cancelReason;
  }

  public void setCancelReason(String cancelReason) {
    this.cancelReason = cancelReason;
  }

  public String getAddressDetail() {
    return addressDetail;
  }

  public void setAddressDetail(String addressDetail) {
    this.addressDetail = addressDetail;
  }

  public List<OperationLogDTO> getOperationLogDTOList() {
    return operationLogDTOList;
  }

  public void setOperationLogDTOList(List<OperationLogDTO> operationLogDTOList) {
    this.operationLogDTOList = operationLogDTOList;
  }

  public BcgogoReceivableRecordDTO getBcgogoReceivableOrderToBePaidRecordDTO() {
    return bcgogoReceivableOrderToBePaidRecordDTO;
  }

  public void setBcgogoReceivableOrderToBePaidRecordDTO(BcgogoReceivableRecordDTO bcgogoReceivableOrderToBePaidRecordDTO) {
    this.bcgogoReceivableOrderToBePaidRecordDTO = bcgogoReceivableOrderToBePaidRecordDTO;
    if(bcgogoReceivableOrderToBePaidRecordDTO!=null){
      this.bcgogoReceivableOrderToBePaidRecordRelationId = bcgogoReceivableOrderToBePaidRecordDTO.getBcgogoReceivableOrderRecordRelationId();
      this.receivableMethod = StringUtils.isNotBlank(bcgogoReceivableOrderToBePaidRecordDTO.getReceivableMethod())?ReceivableMethod.valueOf(bcgogoReceivableOrderToBePaidRecordDTO.getReceivableMethod()):null;
    }
  }

  public ReceivableMethod getReceivableMethod() {
    return receivableMethod;
  }

  public void setReceivableMethod(ReceivableMethod receivableMethod) {
    this.receivableMethod = receivableMethod;
  }

  public InstalmentPlanDTO getInstalmentPlanDTO() {
    return instalmentPlanDTO;
  }

  public void setInstalmentPlanDTO(InstalmentPlanDTO instalmentPlanDTO) {
    this.instalmentPlanDTO = instalmentPlanDTO;
  }

  public String getCreatedTimeStr() {
    return createdTimeStr;
  }

  public void setCreatedTimeStr(String createdTimeStr) {
    this.createdTimeStr = createdTimeStr;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public List<BcgogoReceivableRecordDTO> getBcgogoReceivableOrderPaidRecordDTOList() {
    return bcgogoReceivableOrderPaidRecordDTOList;
  }

  public void setBcgogoReceivableOrderPaidRecordDTOList(List<BcgogoReceivableRecordDTO> bcgogoReceivableOrderPaidRecordDTOList) {
    this.bcgogoReceivableOrderPaidRecordDTOList = bcgogoReceivableOrderPaidRecordDTOList;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void generateInstallmentInfo(){
    if(this.getBcgogoReceivableOrderToBePaidRecordDTO()!=null){
      Double currentPayableAmount =0d;
      String currentPeriodNumberInfo = "";
      if(ReceivableMethod.INSTALLMENT.toString().equals(this.getBcgogoReceivableOrderToBePaidRecordDTO().getReceivableMethod())
          && PaymentStatus.PARTIAL_PAYMENT.equals(this.getStatus()) && this.getInstalmentPlanDTO()!=null){
        if(this.getInstalmentPlanDTO().getCurrentItem().isExpired()){
          for(InstalmentPlanItemDTO instalmentPlanItemDTO:this.getInstalmentPlanDTO().getInstalmentPlanItemDTOList()){
            if(!PaymentStatus.FULL_PAYMENT.equals(instalmentPlanItemDTO.getStatus()) && instalmentPlanItemDTO.isExpired()){
              currentPayableAmount+=instalmentPlanItemDTO.getPayableAmount();
              currentPeriodNumberInfo+=instalmentPlanItemDTO.getPeriodNumber()+",";
            }
          }
          currentPeriodNumberInfo = currentPeriodNumberInfo.substring(0,currentPeriodNumberInfo.length()-1);
        }else{
          currentPayableAmount = this.getInstalmentPlanDTO().getCurrentItem().getPayableAmount();
          currentPeriodNumberInfo = this.getInstalmentPlanDTO().getCurrentItem().getPeriodNumber().toString();
        }
      }else{
        if(this.getBcgogoReceivableOrderToBePaidRecordDTO().getRecordPaymentAmount()!=null){
          currentPayableAmount = this.getBcgogoReceivableOrderToBePaidRecordDTO().getRecordPaymentAmount();
        }else{
          currentPayableAmount = this.getTotalAmount();
        }
      }
      this.setCurrentPayableAmount(currentPayableAmount);
      this.setCurrentPeriodNumberInfo(currentPeriodNumberInfo);
    }
  }

  public void setShopVersion(String shopVersion) {
    this.shopVersion = shopVersion;
  }

  public String getShopVersion() {
    return shopVersion;
  }

  public void setShopOwner(String shopOwner) {
    this.shopOwner = shopOwner;
  }

  public String getShopOwner() {
    return shopOwner;
  }

  public void setOldTotalAmount(Double oldTotalAmount) {
    this.oldTotalAmount = oldTotalAmount;
  }

  public Double getOldTotalAmount() {
    return oldTotalAmount;
  }

}
