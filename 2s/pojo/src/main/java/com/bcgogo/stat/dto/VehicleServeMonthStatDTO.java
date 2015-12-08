package com.bcgogo.stat.dto;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-27
 * Time: 上午4:06
 * To change this template use File | Settings | File Templates.
 */
public class VehicleServeMonthStatDTO {
  private Long shopId;
  private String brand;
  private String model;
  private Integer statYear;
  private Integer statMonth;
  private int washTimes;    //洗车次数
  private int repairTimes;  //施工次数
  private int totalTimes;   //总次数
  private double washTotal;     //洗车消费额
  private double repairTotal;   //施工消费额
  private double totalConsume;   //消费总计

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

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

  public int getWashTimes() {
    return washTimes;
  }

  public void setWashTimes(int washTimes) {
    this.washTimes = washTimes;
  }

  public int getRepairTimes() {
    return repairTimes;
  }

  public void setRepairTimes(int repairTimes) {
    this.repairTimes = repairTimes;
  }

  public int getTotalTimes() {
    return totalTimes;
  }

  public void setTotalTimes(int totalTimes) {
    this.totalTimes = totalTimes;
  }

  public double getWashTotal() {
    return washTotal;
  }

  public void setWashTotal(double washTotal) {
    this.washTotal = washTotal;
  }

  public double getRepairTotal() {
    return repairTotal;
  }

  public void setRepairTotal(double repairTotal) {
    this.repairTotal = repairTotal;
  }

  public double getTotalConsume() {
    return totalConsume;
  }

  public void setTotalConsume(double totalConsume) {
    this.totalConsume = totalConsume;
  }
}
