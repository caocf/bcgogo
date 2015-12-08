package com.bcgogo.txn.dto.pushMessage.impact;

import com.bcgogo.enums.UploadStatus;
import com.bcgogo.enums.YesNo;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午12:00
 */
public class ImpactInfoSearchConditionDTO {
  private List<Long> ids = new ArrayList<Long>();
  private Long shopId;          //查询店铺的id
  private int maxRows = 10;
  private int startPageNo = 1;
  private String vehicleNo;
  private List<String> vehicleNoList = new ArrayList<String>();
  private YesNo isUntreated;
  private YesNo isDeleted;
  private String status;
  private UploadStatus uploadStatus;
  private Long timeStart;
  private Long timeEnd;

  public boolean hasState() {
    return isUntreated != null || isDeleted != null;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public YesNo getDeleted() {
    return isDeleted;
  }

  public void setDeleted(YesNo deleted) {
    isDeleted = deleted;
  }

  public YesNo getUntreated() {
    return isUntreated;
  }

  public void setUntreated(YesNo untreated) {
    isUntreated = untreated;
  }

  public Long getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(Long timeStart) {
    this.timeStart = timeStart;
  }

  public Long getTimeEnd() {
    return timeEnd;
  }

  public void setTimeEnd(Long timeEnd) {
    this.timeEnd = timeEnd;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public List<String> getVehicleNoList() {
    return vehicleNoList;
  }

  public void setVehicleNoList(List<String> vehicleNoList) {
    this.vehicleNoList = vehicleNoList;
  }

  public YesNo getIsUntreated() {
    return isUntreated;
  }

  public void setIsUntreated(YesNo isUntreated) {
    this.isUntreated = isUntreated;
  }

  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

  public YesNo getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(YesNo isDeleted) {
    this.isDeleted = isDeleted;
  }

  public UploadStatus getUploadStatus() {
    return uploadStatus;
  }

  public void setUploadStatus(UploadStatus uploadStatus) {
    this.uploadStatus = uploadStatus;
  }
}
