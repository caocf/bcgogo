package com.bcgogo.user.model.app;

import com.bcgogo.api.DriveStatDTO;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by XinyuQiu on 14-5-4.
 */

@Entity
@Table(name = "drive_stat")
public class DriveStat  extends LongIdentifier {
  private Double distance;//路程
  private Double oilCost;//耗油量
  private Double oilWear;//百公里油耗
  private Double oilMoney;//油钱
  private Integer statYear;//统计年份
  private Integer statMonth;//统计月份
  private String appUserNo;
  private Long statDate;//统计时间


  public DriveStatDTO toDTO() {
    DriveStatDTO driveStatDTO = new DriveStatDTO();
    driveStatDTO.setDistance(getDistance());
    driveStatDTO.setOilCost(getOilCost());
    driveStatDTO.setOilWear(getOilWear());
    driveStatDTO.setOilMoney(getOilMoney());
    driveStatDTO.setStatMonth(getStatMonth());
    driveStatDTO.setStatYear(getStatYear());
    driveStatDTO.setAppUserNo(getAppUserNo());
    driveStatDTO.setStatDate(getStatDate());
    return driveStatDTO;
  }

  public void fromDTO(DriveStatDTO driveStatDTO) {
    if (driveStatDTO != null) {
      this.setDistance(driveStatDTO.getDistance());
      this.setOilCost(driveStatDTO.getOilCost());
      this.setOilWear(driveStatDTO.getOilWear());
      this.setOilMoney(driveStatDTO.getOilMoney());
      this.setStatMonth(driveStatDTO.getStatMonth());
      this.setStatYear(driveStatDTO.getStatYear());
      this.setAppUserNo(driveStatDTO.getAppUserNo());
      this.setStatDate(driveStatDTO.getStatDate());
    }
  }

  public void addFromDTO(DriveStatDTO driveStatDTO) {
    if (driveStatDTO != null) {
      this.setDistance(NumberUtil.round(NumberUtil.doubleVal(getDistance()) + NumberUtil.doubleVal(driveStatDTO.getDistance()), 1));
      this.setOilCost(NumberUtil.round(NumberUtil.doubleVal(getOilCost()) + NumberUtil.doubleVal(driveStatDTO.getOilCost()), 1));
      this.setOilMoney(NumberUtil.round(NumberUtil.doubleVal(getOilMoney()) + NumberUtil.doubleVal(driveStatDTO.getOilMoney()), 1));
      cacOilWear();
    }
  }

  public void cacOilWear(){
    if (NumberUtil.doubleVal(this.getDistance()) > 0.0001) {
      this.setOilWear(NumberUtil.round(NumberUtil.doubleVal(getOilCost()) / NumberUtil.doubleVal(getDistance()) * 100, 1));
    }else {
      this.setOilWear(0d);
    }
  }

  @Column(name = "distance")
  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  @Column(name = "oil_cost")
  public Double getOilCost() {
    return oilCost;
  }

  public void setOilCost(Double oilCost) {
    this.oilCost = oilCost;
  }

  @Column(name = "oil_wear")
  public Double getOilWear() {
    return oilWear;
  }

  public void setOilWear(Double oilWear) {
    this.oilWear = oilWear;
  }

  @Column(name = "oil_money")
  public Double getOilMoney() {
    return oilMoney;
  }

  public void setOilMoney(Double oilMoney) {
    this.oilMoney = oilMoney;
  }

  @Column(name = "stat_year")
  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  @Column(name = "stat_month")
  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "stat_date")
  public Long getStatDate() {
    return statDate;
  }

  public void setStatDate(Long statDate) {
    this.statDate = statDate;
  }


}
