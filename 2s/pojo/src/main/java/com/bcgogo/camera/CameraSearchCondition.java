package com.bcgogo.camera;

/**
 * Created by jiezhang on 15-1-15.
 */
public class CameraSearchCondition {
  private String shopId;
  private Integer page;
  private Integer rows;
  private String startDateStr;
  private String endDateStr;
  private Long startDate;
  private String vehicle_nos;

  public String getVehicle_nos() {
    return vehicle_nos;
  }

  public void setVehicle_nos(String vehicle_nos) {
    this.vehicle_nos = vehicle_nos;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  private Long endDate;


  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public String getShopId() {
    return shopId;
  }

  public void setShopId(String shopId) {
    this.shopId = shopId;
  }
}
