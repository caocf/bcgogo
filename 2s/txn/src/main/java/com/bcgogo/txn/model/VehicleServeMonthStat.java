package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.VehicleServeMonthStatDTO;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 服务车型统计
 * User: Jimuchen
 * Date: 12-10-24
 * Time: 下午5:06
 */
@Entity
@Table(name="vehicle_serve_month_stat")
public class VehicleServeMonthStat extends LongIdentifier {
  private static final String EMPTY_WORD = "(空)";

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

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name="model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name="stat_year")
  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  @Column(name="stat_month")
  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name="wash_times")
  public int getWashTimes() {
    return washTimes;
  }

  public void setWashTimes(int washTimes) {
    this.washTimes = washTimes;
  }

  @Column(name="repair_times")
  public int getRepairTimes() {
    return repairTimes;
  }

  public void setRepairTimes(int repairTimes) {
    this.repairTimes = repairTimes;
  }

  @Column(name="total_times")
  public int getTotalTimes() {
    return totalTimes;
  }

  public void setTotalTimes(int totalTimes) {
    this.totalTimes = totalTimes;
  }

  @Column(name="wash_total")
  public double getWashTotal() {
    return washTotal;
  }

  public void setWashTotal(double washTotal) {
    this.washTotal = washTotal;
  }

  @Column(name="repair_total")
  public double getRepairTotal() {
    return repairTotal;
  }

  public void setRepairTotal(double repairTotal) {
    this.repairTotal = repairTotal;
  }

  @Column(name="total_consume")
  public double getTotalConsume() {
    return totalConsume;
  }

  public void setTotalConsume(double totalConsume) {
    this.totalConsume = totalConsume;
  }

  public VehicleServeMonthStatDTO toDTO(){
    VehicleServeMonthStatDTO statDTO = new VehicleServeMonthStatDTO();
    statDTO.setBrand(StringUtils.isBlank(getBrand())?EMPTY_WORD:getBrand());
    statDTO.setModel(StringUtils.isBlank(getModel())?EMPTY_WORD:getModel());
    statDTO.setRepairTimes(getRepairTimes());
    statDTO.setRepairTotal(getRepairTotal());
    statDTO.setShopId(getShopId());
    statDTO.setStatMonth(getStatMonth());
    statDTO.setStatYear(getStatYear());
    statDTO.setTotalConsume(getTotalConsume());
    statDTO.setTotalTimes(getTotalTimes());
    statDTO.setWashTimes(getWashTimes());
    statDTO.setWashTotal(getWashTotal());
    return statDTO;
  }
}
