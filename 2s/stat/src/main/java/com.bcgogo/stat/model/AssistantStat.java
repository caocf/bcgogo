package com.bcgogo.stat.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.AssistantStatDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-18
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
@Entity
@Table(name = "assistant_stat")
public class AssistantStat extends LongIdentifier {
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

  @Column(name = "member_income")
  public double getMemberIncome() {
    return memberIncome;
  }

  public void setMemberIncome(double memberIncome) {
    this.memberIncome = memberIncome;
  }

  public AssistantStat(){}

  public AssistantStat(AssistantStatDTO assistantStatDTO) {
    this.setId(assistantStatDTO.getId());
    this.setShopId(assistantStatDTO.getShopId());
    this.setStatYear(assistantStatDTO.getStatYear());
    this.setStatMonth(assistantStatDTO.getStatMonth());
    this.setStatDay(assistantStatDTO.getStatDay());
    this.setStatWeek(assistantStatDTO.getStatWeek());
    this.setStatSum(assistantStatDTO.getStatSum());
    this.setAssistant(assistantStatDTO.getAssistant());
    this.setSales(assistantStatDTO.getSales());
    this.setWash(assistantStatDTO.getWash());
    this.setService(assistantStatDTO.getService());
    this.setMemberIncome(assistantStatDTO.getMemberIncome());
  }

  public AssistantStat fromDTO(AssistantStatDTO assistantStatDTO) {
    this.setId(assistantStatDTO.getId());
    this.setShopId(assistantStatDTO.getShopId());
    this.setStatYear(assistantStatDTO.getStatYear());
    this.setStatMonth(assistantStatDTO.getStatMonth());
    this.setStatDay(assistantStatDTO.getStatDay());
    this.setStatWeek(assistantStatDTO.getStatWeek());
    this.setStatSum(assistantStatDTO.getStatSum());
    this.setAssistant(assistantStatDTO.getAssistant());
    this.setSales(assistantStatDTO.getSales());
    this.setWash(assistantStatDTO.getWash());
    this.setService(assistantStatDTO.getService());
    this.setMemberIncome(assistantStatDTO.getMemberIncome());
    return this;
  }

  public AssistantStatDTO toDTO() {
    AssistantStatDTO assistantStatDTO = new AssistantStatDTO();
    assistantStatDTO.setId(this.getId());
    assistantStatDTO.setShopId(this.getShopId());
    assistantStatDTO.setStatYear(this.getStatYear());
    assistantStatDTO.setStatMonth(this.getStatMonth());
    assistantStatDTO.setStatDay(this.getStatDay());
    assistantStatDTO.setStatWeek(this.getStatWeek());
    assistantStatDTO.setStatSum(this.getStatSum());
    assistantStatDTO.setAssistant(this.getAssistant());
    assistantStatDTO.setSales(this.getSales());
    assistantStatDTO.setWash(this.getWash());
    assistantStatDTO.setService(this.getService());
    assistantStatDTO.setMemberIncome(this.getMemberIncome());
    return assistantStatDTO;
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

  @Column(name = "assistant")
  public String getAssistant() {
    return assistant;
  }
  public void setAssistant(String assistant) {
    this.assistant = assistant;
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





























}
