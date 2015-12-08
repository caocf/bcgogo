package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.PaymentStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-25
 * Time: 下午3:15
 */
public class InstalmentPlanDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private Double totalAmount;        //总金额
  private Double payableAmount;      //应付金额
  private Double paidAmount;         //已付金额
  private Long startTime;             //总的开始时间
  private Long endTime;               //总的截止时间
  private Integer periods;            //期数
  private PaymentStatus status;    //状态
  private Long currentItemId;        //当前处于第几期Id
  private Long currentItemEndTime;  //当前分期阶段结束日期
  private String memo;
  private InstalmentPlanItemDTO currentItem;
  private List<InstalmentPlanItemDTO> instalmentPlanItemDTOList = new ArrayList<InstalmentPlanItemDTO>();

  private Double totalPayableAmount;//处理有过期的 情况

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null) idStr = id.toString();
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

  public Double getPayableAmount() {
    return payableAmount;
  }

  public void setPayableAmount(Double payableAmount) {
    this.payableAmount = payableAmount;
  }

  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
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

  public Integer getPeriods() {
    return periods;
  }

  public void setPeriods(Integer periods) {
    this.periods = periods;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public Long getCurrentItemId() {
    return currentItemId;
  }

  public void setCurrentItemId(Long currentItemId) {
    this.currentItemId = currentItemId;
  }

  public Long getCurrentItemEndTime() {
    return currentItemEndTime;
  }

  public void setCurrentItemEndTime(Long currentItemEndTime) {
    this.currentItemEndTime = currentItemEndTime;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public InstalmentPlanItemDTO getCurrentItem() {
    return currentItem;
  }

  public void setCurrentItem(InstalmentPlanItemDTO currentItem) {
    this.currentItem = currentItem;
  }

  public List<InstalmentPlanItemDTO> getInstalmentPlanItemDTOList() {
    return instalmentPlanItemDTOList;
  }

  public void setInstalmentPlanItemDTOList(List<InstalmentPlanItemDTO> instalmentPlanItemDTOList) {
    this.instalmentPlanItemDTOList = instalmentPlanItemDTOList;
    totalPayableAmount = 0d;
    if(CollectionUtils.isNotEmpty(instalmentPlanItemDTOList)){
      for(InstalmentPlanItemDTO instalmentPlanItemDTO:instalmentPlanItemDTOList){
        if(instalmentPlanItemDTO.getEndTime()<System.currentTimeMillis()){
          totalPayableAmount+=instalmentPlanItemDTO.getPayableAmount();
        }
      }
    }
    if(totalPayableAmount==0){
      totalPayableAmount = currentItem.getPayableAmount();
    }
  }

  public Double getTotalPayableAmount() {
    return totalPayableAmount;
  }

  public void setTotalPayableAmount(Double totalPayableAmount) {
    this.totalPayableAmount = totalPayableAmount;
  }
}
