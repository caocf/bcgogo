package com.bcgogo.api;

import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by XinyuQiu on 14-4-30.
 */
public class DriveStatDTO implements Serializable {
  private Double distance;//路程
  private Double oilCost;//耗油量
  private Double oilWear;//百公里油耗
  private Double oilMoney;//油钱
  private Integer statYear;//统计年份
  private Integer statMonth;//统计月份
  private String appUserNo;
  private Long statDate;//统计时间

  public void addDriveLogStat(DriveLogDTO driveLogDTO) {
    if (driveLogDTO != null) {
      this.setDistance(NumberUtil.round(NumberUtil.doubleVal(getDistance()) + NumberUtil.doubleVal(driveLogDTO.getDistance()), 1));
      this.setOilCost(NumberUtil.round(NumberUtil.doubleVal(getOilCost()) + NumberUtil.doubleVal(driveLogDTO.getOilCost()), 1));
      this.setOilMoney(NumberUtil.round(NumberUtil.doubleVal(getOilMoney()) + NumberUtil.doubleVal(driveLogDTO.getTotalOilMoney()), 1));
      cacOilWear();
    }
  }

  public void setDriveLogDTO(DriveLogDTO driveLogDTO) {
    if (driveLogDTO != null) {
      this.setAppUserNo(driveLogDTO.getAppUserNo());
      this.setStatYear(DateUtil.getYear(driveLogDTO.getStartTime()));
      this.setStatMonth(DateUtil.getMonth(driveLogDTO.getStartTime()));
      this.setDistance(NumberUtil.round(driveLogDTO.getDistance(), 1));
      this.setOilCost(NumberUtil.round(driveLogDTO.getOilCost(), 1));
      this.setOilMoney(NumberUtil.round(driveLogDTO.getTotalOilMoney(), 1));
      this.setStatDate(DateUtil.getDateByYearMonth(this.getStatYear(), this.getStatMonth()));
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

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public Double getOilCost() {
    return oilCost;
  }

  public void setOilCost(Double oilCost) {
    this.oilCost = oilCost;
  }

  public Double getOilWear() {
    return oilWear;
  }

  public void setOilWear(Double oilWear) {
    this.oilWear = oilWear;
  }

  public Double getOilMoney() {
    return oilMoney;
  }

  public void setOilMoney(Double oilMoney) {
    this.oilMoney = oilMoney;
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

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getStatDate() {
    return statDate;
  }

  public void setStatDate(Long statDate) {
    this.statDate = statDate;
  }
}
