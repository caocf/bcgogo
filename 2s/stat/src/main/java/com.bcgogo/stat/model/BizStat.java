package com.bcgogo.stat.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.BizStatDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

@Entity
@Table(name = "biz_stat")
public class BizStat extends LongIdentifier {
  private Long shopId;
  private String statType;
  private Long statYear;
  private Long statMonth;
  private Long statDay;
  private Long statWeek;
  private double statSum;

  public BizStat() {
  }

  public BizStat(BizStatDTO bizStatDTO) {
    this.setId(bizStatDTO.getId());
    this.setShopId(bizStatDTO.getShopId());
    this.setStatType(bizStatDTO.getStatType());
    this.setStatYear(bizStatDTO.getStatYear());
    this.setStatMonth(bizStatDTO.getStatMonth());
    this.setStatDay(bizStatDTO.getStatDay());
    this.setStatWeek(bizStatDTO.getStatWeek());
    this.setStatSum(bizStatDTO.getStatSum());
  }

  public BizStat fromDTO(BizStatDTO bizStatDTO) {
    this.setId(bizStatDTO.getId());
    this.setShopId(bizStatDTO.getShopId());
    this.setStatType(bizStatDTO.getStatType());
    this.setStatYear(bizStatDTO.getStatYear());
    this.setStatMonth(bizStatDTO.getStatMonth());
    this.setStatDay(bizStatDTO.getStatDay());
    this.setStatWeek(bizStatDTO.getStatWeek());
    this.setStatSum(bizStatDTO.getStatSum());
    return this;
  }

  public BizStatDTO toDTO() {
    BizStatDTO bizStatDTO = new BizStatDTO();
    bizStatDTO.setId(this.getId());
    bizStatDTO.setShopId(this.getShopId());
    bizStatDTO.setStatType(this.getStatType());
    bizStatDTO.setStatYear(this.getStatYear());
    bizStatDTO.setStatMonth(this.getStatMonth());
    bizStatDTO.setStatDay(this.getStatDay());
    bizStatDTO.setStatWeek(this.getStatWeek());
    bizStatDTO.setStatSum(this.getStatSum());
    return bizStatDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "stat_type", length = 20)
  public String getStatType() {
    return statType;
  }

  public void setStatType(String statType) {
    this.statType = statType;
  }

  @Column(name = "stat_year")
  public Long getStatYear() {
    return statYear;
  }

  public void setStatYear(Long statYear) {
    this.statYear = statYear;
  }

  @Column(name = "stat_month")
  public Long getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Long statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name = "stat_day")
  public Long getStatDay() {
    return statDay;
  }

  public void setStatDay(Long statDay) {
    this.statDay = statDay;
  }

  @Column(name = "stat_week")
  public Long getStatWeek() {
    return statWeek;
  }

  public void setStatWeek(Long statWeek) {
    this.statWeek = statWeek;
  }

  @Column(name = "stat_sum")
  public double getStatSum() {
    return statSum;
  }

  public void setStatSum(double statSum) {
    this.statSum = statSum;
  }

}
