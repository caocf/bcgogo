package com.bcgogo.stat.dto;

import org.apache.commons.lang.ArrayUtils;

/**
 * 成本统计查询条件
 * User: Jimuchen
 * Date: 12-10-27
 * Time: 上午2:50
 * To change this template use File | Settings | File Templates.
 */
public class CostStatConditionDTO {
  private static final String ALL_MONTH = "所有月份";

  public static final String FIELD_PRODUCT_NAME = "name";
  public static final String FIELD_BRAND = "brand";
  public static final String FIELD_VEHICLE_MODEL = "vehicle_model";

  private Integer year;
  private String monthStr;

  private Integer month;
  private Boolean allYear;

  private String quantityOrTotal;

  private String[] queryFields;

  private String[] allYearOptions;

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public String getMonthStr() {
    if(allYear){
      monthStr = ALL_MONTH;
    }else{
      monthStr = month.toString();
    }
    return monthStr;
  }

  public void setMonthStr(String monthStr) {
    if(monthStr.equals(ALL_MONTH)){
      allYear = true;
    }else{
      allYear = false;
      monthStr = monthStr.replace("月", "");
      month = Integer.parseInt(monthStr);
    }
    this.monthStr = monthStr;
  }

  public Integer getMonth() {
    return month;
  }

  public void setMonth(Integer month) {
    this.month = month;
  }

  public Boolean getAllYear() {
    return allYear;
  }

  public void setAllYear(Boolean allYear) {
    this.allYear = allYear;
  }

  public String getQuantityOrTotal() {
    return quantityOrTotal;
  }

  public void setQuantityOrTotal(String quantityOrTotal) {
    this.quantityOrTotal = quantityOrTotal;
  }

  public String[] getQueryFields() {
    return queryFields;
  }

  public void setQueryFields(String[] queryFields) {
    this.queryFields = queryFields;
  }

  public Boolean getNameSelected(){
    if(ArrayUtils.contains(queryFields, FIELD_PRODUCT_NAME)){
      return true;
    }else{
      return false;
    }
  }

  public Boolean getBrandSelected(){
    if(ArrayUtils.contains(queryFields, FIELD_BRAND)){
      return true;
    }else{
      return false;
    }
  }

  public Boolean getVehicleModelSelected(){
    if(ArrayUtils.contains(queryFields, FIELD_VEHICLE_MODEL)){
      return true;
    }else{
      return false;
    }
  }

  public String[] getAllYearOptions() {
    return allYearOptions;
  }

  public void setAllYearOptions(String[] allYearOptions) {
    this.allYearOptions = allYearOptions;
  }
}
