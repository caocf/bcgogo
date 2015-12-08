package com.bcgogo.stat.model;

import com.bcgogo.enums.stat.businessAccountStat.BusinessCategoryStatType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.BusinessCategoryStatDTO;

import javax.persistence.*;

/**
 * 营业外记账类别表
 */
@Entity
@Table(name = "business_category_stat")
public class BusinessCategoryStat extends LongIdentifier {

  //店面Id
  private Long shopId;
  private String businessCategory;
  private Long businessCategoryId;
  private Long statDate;
  private Long statDay;
  private Long statMonth;
  private Long statYear;
  private BusinessCategoryStatType statType;
  private Double checkAmount;
  private Double unionPay;
  private Double cash;
  private Double total;
  private MoneyCategory moneyCategory;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "business_category")
  public String getBusinessCategory() {
    return businessCategory;
  }

  public void setBusinessCategory(String businessCategory) {
    this.businessCategory = businessCategory;
  }

  @Column(name = "business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name = "stat_date")
  public Long getStatDate() {
    return statDate;
  }

  public void setStatDate(Long statDate) {
    this.statDate = statDate;
  }

  @Column(name = "stat_day")
  public Long getStatDay() {
    return statDay;
  }

  public void setStatDay(Long statDay) {
    this.statDay = statDay;
  }

  @Column(name = "stat_month")
  public Long getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Long statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name = "stat_year")
  public Long getStatYear() {
    return statYear;
  }

  public void setStatYear(Long statYear) {
    this.statYear = statYear;
  }

  @Column(name = "stat_type")
  @Enumerated(EnumType.STRING)
  public BusinessCategoryStatType getStatType() {
    return statType;
  }

  public void setStatType(BusinessCategoryStatType statType) {
    this.statType = statType;
  }

  @Column(name = "check_amount")
  public Double getCheckAmount() {
    return checkAmount;
  }

  public void setCheckAmount(Double checkAmount) {
    this.checkAmount = checkAmount;
  }

  @Column(name = "union_pay")
  public Double getUnionPay() {
    return unionPay;
  }

  public void setUnionPay(Double unionPay) {
    this.unionPay = unionPay;
  }

  @Column(name = "cash")
  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "money_category")
  @Enumerated(EnumType.STRING)
  public MoneyCategory getMoneyCategory() {
    return moneyCategory;
  }

  public void setMoneyCategory(MoneyCategory moneyCategory) {
    this.moneyCategory = moneyCategory;
  }

  public BusinessCategoryStatDTO toDTO() {
    BusinessCategoryStatDTO statDTO = new BusinessCategoryStatDTO();
    statDTO.setId(getId());
    statDTO.setShopId(getShopId());
    statDTO.setBusinessCategory(getBusinessCategory());
    statDTO.setBusinessCategoryId(getBusinessCategoryId());
    statDTO.setStatDate(getStatDate());
    statDTO.setStatDay(getStatDay());
    statDTO.setStatMonth(getStatMonth());
    statDTO.setStatYear(getStatYear());
    statDTO.setStatType(getStatType());
    statDTO.setCheckAmount(getCheckAmount());
    statDTO.setUnionPay(getUnionPay());
    statDTO.setCash(getCash());
    statDTO.setTotal(getTotal());
    statDTO.setMoneyCategory(getMoneyCategory());
    return statDTO;
  }

  public BusinessCategoryStat fromDTO(BusinessCategoryStatDTO businessCategoryStatDTO) {
    this.setId(businessCategoryStatDTO.getId());
    this.setShopId(businessCategoryStatDTO.getShopId());
    this.setBusinessCategory(businessCategoryStatDTO.getBusinessCategory());
    this.setBusinessCategoryId(businessCategoryStatDTO.getBusinessCategoryId());
    this.setStatDate(businessCategoryStatDTO.getStatDate());
    this.setStatDay(businessCategoryStatDTO.getStatDay());
    this.setStatMonth(businessCategoryStatDTO.getStatMonth());
    this.setStatYear(businessCategoryStatDTO.getStatYear());
    this.setStatType(businessCategoryStatDTO.getStatType());
    this.setCheckAmount(businessCategoryStatDTO.getCheckAmount());
    this.setUnionPay(businessCategoryStatDTO.getUnionPay());
    this.setCash(businessCategoryStatDTO.getCash());
    this.setTotal(businessCategoryStatDTO.getTotal());
    this.setMoneyCategory(businessCategoryStatDTO.getMoneyCategory());
    return this;
  }

}
