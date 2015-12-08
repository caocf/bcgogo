package com.bcgogo.api.request;

import com.bcgogo.enums.app.ErrorCodeTreatStatus;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-13
 * Time: 上午11:59
 */
public class FaultCodeListRequest {
  private String appUserNo;
  private  int pageNo;
  private  int pageSize;
  private ErrorCodeTreatStatus[] status;
  private Long vehicleId;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public int getPageNo() {
    return pageNo;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public ErrorCodeTreatStatus[] getStatus() {
    return status;
  }

  public void setStatus(ErrorCodeTreatStatus[] status) {
    this.status = status;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }
}
