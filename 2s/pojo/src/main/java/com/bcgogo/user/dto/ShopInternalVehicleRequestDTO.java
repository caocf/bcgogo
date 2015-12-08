package com.bcgogo.user.dto;

import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by XinyuQiu on 14-12-16.
 */
public class ShopInternalVehicleRequestDTO {
  private int page;
  private int rows;
  private Long shopId;
  private String sort;
  private String order;

  private String[] vehicleNos;
  private Set<String> vehicleNoSet;
  private Set<Long> vehicleIds;
  private Map<Long,VehicleDTO> vehicleDTOMap;
  private String startDateStr;
  private Long startDate;
  private String endDateStr;
  private Long endDate;



  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public Set<String> getVehicleNoSet() {
    return vehicleNoSet;
  }

  public void setVehicleNoSet(Set<String> vehicleNoSet) {
    this.vehicleNoSet = vehicleNoSet;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public String[] getVehicleNos() {
    return vehicleNos;
  }

  public void setVehicleNos(String[] vehicleNos) {
    this.vehicleNos = vehicleNos;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public Set<Long> getVehicleIds() {
    return vehicleIds;
  }

  public void setVehicleIds(Set<Long> vehicleIds) {
    this.vehicleIds = vehicleIds;
  }

  public Map<Long, VehicleDTO> getVehicleDTOMap() {
    return vehicleDTOMap;
  }

  public void setVehicleDTOMap(Map<Long, VehicleDTO> vehicleDTOMap) {
    this.vehicleDTOMap = vehicleDTOMap;
  }
}
