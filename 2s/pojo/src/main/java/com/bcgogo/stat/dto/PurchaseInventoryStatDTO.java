package com.bcgogo.stat.dto;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-27
 * Time: 上午4:06
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseInventoryStatDTO {
  private Long shopId;
  private String productName;
  private String productBrand;
  private String vehicleModel;
  private String vehicleBrand;
  private double amount;
  private double total;
  private int times;
  private String productIdStr;
  private List<Long> productIds;
  private Long productId;
  private Integer statYear;
  private Integer statMonth;
  private Integer statDay;

  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  public Integer getStatDay() {
    return statDay;
  }

  public void setStatDay(Integer statDay) {
    this.statDay = statDay;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getTotalStr(){
    return String.format("%.1f", total);
  }

  public String getAmountStr(){
    return String.format("%.1f", amount);
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public String getProductIdStr() {
    return productIdStr;
  }

  public void setProductIdStr(String productIdStr) {
    this.productIdStr = productIdStr;
  }

  public List<Long> getProductIds() {
    return productIds;
  }

  public void setProductIds(List<Long> productIds) {
    this.productIds = productIds;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
}
