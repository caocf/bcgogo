package com.bcgogo.stat.dto;

import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-18
 * Time: 下午5:46
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class AssistantStatDTO implements Serializable,Comparable<AssistantStatDTO> {
  private Long id;
  private Long shopId;
  private Long statYear;
  private Long statMonth;
  private Long statDay;
  private Long statWeek;
  private double statSum;
  private String assistant;
  private double sales;
  private double wash;
  private double service;
  private double memberIncome;

  public double getMemberIncome() {
    return memberIncome;
  }

  public void setMemberIncome(double memberIncome) {
    this.memberIncome = NumberUtil.toReserve(memberIncome,NumberUtil.MONEY_PRECISION);
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setStatYear(Long statYear) {
    this.statYear = statYear;
  }

  public void setStatMonth(Long statMonth) {
    this.statMonth = statMonth;
  }

  public void setStatDay(Long statDay) {
    this.statDay = statDay;
  }

  public void setStatWeek(Long statWeek) {
    this.statWeek = statWeek;
  }

  public void setAssistant(String assistant) {
    this.assistant = assistant;
  }

  public void setStatSum(double statSum) {
    BigDecimal bigDecimal = new BigDecimal(statSum);
    statSum = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    this.statSum = statSum;
  }

  public void setSales(double sales) {
    BigDecimal bigDecimal = new BigDecimal(sales);
    sales = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    this.sales = sales;
  }

  public void setWash(double wash) {
    BigDecimal bigDecimal = new BigDecimal(wash);
    wash = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    this.wash = wash;
  }

  public void setService(double service) {
    BigDecimal bigDecimal = new BigDecimal(service);
    service = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    this.service = service;
  }

  public Long getId() {
    return id;
  }

  public Long getShopId() {
    return shopId;
  }

  public Long getStatYear() {
    return statYear;
  }

  public Long getStatMonth() {
    return statMonth;
  }

  public Long getStatDay() {
    return statDay;
  }

  public Long getStatWeek() {
    return statWeek;
  }

  public double getStatSum() {
    return statSum;
  }

  public String getAssistant() {
    return assistant;
  }

  public double getSales() {
    return sales;
  }

  public double getWash() {
    return wash;
  }

  public double getService() {
    return service;
  }

  //实现Comparable的方法
  public int compareTo(AssistantStatDTO AssistantStatDTO){
      double d1 = this.getStatSum();
      double d2 = AssistantStatDTO.getStatSum();
      if(d1>d2){
        return -1;
      }else if(d1==d2){
        return 0;
      }else if(d1<d2){
        return 1;
      }
      return 0;
  }
}
