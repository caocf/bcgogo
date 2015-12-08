package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.BusinessStatDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-9-19
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "business_stat_change")
public class BusinessStatChange extends LongIdentifier {
  private Long shopId;
  private Long statYear;
  private Long statMonth;
  private Long statDay;
  private double sales; //销售金额，包含施工单中的销售金额
  private double wash;  //洗车金额
  private double service; //服务金额：即 施工单中的施工费
  private double statSum; // 销售 + 洗车 + 服务
  private double productCost; // 商品成本: 销售单和施工单所卖出的产品的成本
  private Long statTime;//每次统计的时间
  private double memberIncome;//会员卡购卡续卡收入
  private double otherIncome;//营业外收入

  //小型记账相关
  private double rentExpenditure; //对应小型记账 房租支出
  private double utilitiesExpenditure; //对应小型记账 水电费支出
  private double salaryExpenditure;//对应小型记账 工资提成
  private double otherExpenditure;//对应小型记账 其他支出

  private double orderOtherIncomeCost;//施工单或者销售单 其他费用成本统计

  @Column(name = "rent_expenditure")
  public double getRentExpenditure() {
    return rentExpenditure;
  }

  public void setRentExpenditure(double rentExpenditure) {
    this.rentExpenditure = rentExpenditure;
  }

  @Column(name = "utilities_expenditure")
  public double getUtilitiesExpenditure() {
    return utilitiesExpenditure;
  }

  public void setUtilitiesExpenditure(double utilitiesExpenditure) {
    this.utilitiesExpenditure = utilitiesExpenditure;
  }

  @Column(name = "salary_expenditure")
  public double getSalaryExpenditure() {
    return salaryExpenditure;
  }

  public void setSalaryExpenditure(double salaryExpenditure) {
    this.salaryExpenditure = salaryExpenditure;
  }

  @Column(name = "other_expenditure")
  public double getOtherExpenditure() {
    return otherExpenditure;
  }

  public void setOtherExpenditure(double otherExpenditure) {
    this.otherExpenditure = otherExpenditure;
  }

  public void fromBusinessStat(BusinessStat businessStat) {
    this.setId(businessStat.getId());
    this.setShopId(businessStat.getShopId());

    this.setStatYear(businessStat.getStatYear());
    this.setStatMonth(businessStat.getStatMonth());
    this.setStatDay(businessStat.getStatDay());

    this.setService(businessStat.getService());
    this.setWash(businessStat.getWash());
    this.setProductCost(businessStat.getProductCost());
    this.setStatSum(businessStat.getStatSum());
    this.setStatTime(businessStat.getStatTime());
    this.setMemberIncome(businessStat.getMemberIncome());
    this.setOtherIncome(businessStat.getOtherIncome());
    this.setOrderOtherIncomeCost(businessStat.getOrderOtherIncomeCost());
  }

  @Column(name = "other_income")
  public double getOtherIncome() {
    return otherIncome;
  }

  public void setOtherIncome(double otherIncome) {
    this.otherIncome = otherIncome;
  }

  @Column(name = "member_income")
  public double getMemberIncome() {
    return memberIncome;
  }

  public void setMemberIncome(double memberIncome) {
    this.memberIncome = memberIncome;
  }

  public BusinessStatChange(){

  }

  public BusinessStatChange(BusinessStatDTO businessStatDTO) {
    this.setId(businessStatDTO.getId());
    this.setShopId(businessStatDTO.getShopId());

    this.setStatYear(businessStatDTO.getStatYear());
    this.setStatMonth(businessStatDTO.getStatMonth());
    this.setStatDay(businessStatDTO.getStatDay());


    this.setSales(businessStatDTO.getSales());
    this.setService(businessStatDTO.getService());
    this.setWash(businessStatDTO.getWash());
    this.setProductCost(businessStatDTO.getProductCost());
    this.setStatSum(businessStatDTO.getStatSum());
    this.setStatTime(businessStatDTO.getStatTime());
    this.setMemberIncome(businessStatDTO.getMemberIncome());
    this.setOtherIncome(businessStatDTO.getOtherIncome());

    this.setRentExpenditure(businessStatDTO.getRentExpenditure());
    this.setSalaryExpenditure(businessStatDTO.getSalaryExpenditure());
    this.setUtilitiesExpenditure(businessStatDTO.getUtilitiesExpenditure());
    this.setOtherExpenditure(businessStatDTO.getOtherExpenditure());
    this.setOrderOtherIncomeCost(businessStatDTO.getOrderOtherIncomeCost());
  }

  public BusinessStatChange fromDTO(BusinessStatDTO businessStatDTO) {
    this.setId(businessStatDTO.getId());
    this.setShopId(businessStatDTO.getShopId());

    this.setStatYear(businessStatDTO.getStatYear());
    this.setStatMonth(businessStatDTO.getStatMonth());
    this.setStatDay(businessStatDTO.getStatDay());

    this.setSales(businessStatDTO.getSales());
    this.setService(businessStatDTO.getService());
    this.setWash(businessStatDTO.getWash());
    this.setProductCost(businessStatDTO.getProductCost());
    this.setStatSum(businessStatDTO.getStatSum());
    this.setStatTime(businessStatDTO.getStatTime());
    this.setMemberIncome(businessStatDTO.getMemberIncome());
    this.setOtherIncome(businessStatDTO.getOtherIncome());

    this.setRentExpenditure(businessStatDTO.getRentExpenditure());
    this.setSalaryExpenditure(businessStatDTO.getSalaryExpenditure());
    this.setUtilitiesExpenditure(businessStatDTO.getUtilitiesExpenditure());
    this.setOtherExpenditure(businessStatDTO.getOtherExpenditure());
    this.setOrderOtherIncomeCost(businessStatDTO.getOrderOtherIncomeCost());
    return this;
  }

  public BusinessStatDTO toDTO() {
    BusinessStatDTO businessStatDTO = new BusinessStatDTO();

    businessStatDTO.setId(this.getId());
    businessStatDTO.setShopId(this.getShopId());

    businessStatDTO.setStatYear(this.getStatYear());
    businessStatDTO.setStatMonth(this.getStatMonth());
    businessStatDTO.setStatDay(this.getStatDay());

    businessStatDTO.setSales(this.getSales());
    businessStatDTO.setWash(this.getWash());
    businessStatDTO.setService(this.getService());
    businessStatDTO.setProductCost(this.getProductCost());
    businessStatDTO.setStatSum(this.getStatSum());
    businessStatDTO.setStatTime(this.getStatTime());
    businessStatDTO.setMemberIncome(this.getMemberIncome());
    businessStatDTO.setOtherIncome(this.getOtherIncome());

    businessStatDTO.setRentExpenditure(this.getRentExpenditure());
    businessStatDTO.setSalaryExpenditure(this.getSalaryExpenditure());
    businessStatDTO.setUtilitiesExpenditure(this.getUtilitiesExpenditure());
    businessStatDTO.setOtherExpenditure(this.getOtherExpenditure());
    businessStatDTO.setOrderOtherIncomeCost(this.getOrderOtherIncomeCost());
    return businessStatDTO;
  }

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  @Column(name = "sales")
  public double getSales() {
    return sales;
  }
  public void setSales(double sales) {
    this.sales = sales;
  }

  @Column(name = "wash")
  public double getWash() {
    return wash;
  }

  public void setWash(double wash) {
    this.wash = wash;
  }

  @Column(name = "service")
  public double getService() {
    return service;
  }

  public void setService(double service) {
    this.service = service;
  }

  @Column(name = "product_cost")
  public double getProductCost() {
    return productCost;
  }

  public void setProductCost(double productCost) {
    this.productCost = productCost;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  @Column(name = "stat_sum")
  public double getStatSum() {
    return statSum;
  }

  public void setStatSum(double statSum) {
    this.statSum = statSum;
  }

  @Column(name = "order_other_income_cost")
  public double getOrderOtherIncomeCost() {
    return orderOtherIncomeCost;
  }

  public void setOrderOtherIncomeCost(double orderOtherIncomeCost) {
    this.orderOtherIncomeCost = orderOtherIncomeCost;
  }




  public void clearBusinessStat() {
    this.setSales(0D);
    this.setWash(0D);
    this.setService(0D);
    this.setProductCost(0D);
    this.setStatSum(0D);
    this.setOrderOtherIncomeCost(0D);
  }

}
