package com.bcgogo.user.model.SystemMonitor;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.RequestPerformanceMonitorDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统性能监控实体类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-6
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "request_performance_monitor")
public class RequestPerformanceMonitor extends LongIdentifier {

  private String url;  //请求的url
  private String node; //请求的tomcat

  private Integer year; //年
  private Integer month; //月
  private Integer day; //日

  private Long firstSectionAmount;  //第一区间请求次数
  private Double firstSectionAverage; //第一区间请求响应时间平均值
  private Double firstSectionTotal;//第一区间请求响应总耗时数

  private Long secondSectionAmount;    //第二区间请求次数
  private Double secondSectionAverage; //第二区间请求响应时间平均值
  private Double secondSectionTotal;  //第二区间请求响应总耗时数

  private Long thirdSectionAmount;   //第三区间请求次数
  private Double thirdSectionAverage;  //第三区间请求响应时间平均值
  private Double thirdSectionTotal; //第三区间请求响应总耗时数

  private Long startTime;//统计开始时间
  private Long endTime; //统计结束时间

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "node")
  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    this.node = node;
  }

  @Column(name = "year")
  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  @Column(name = "month")
  public Integer getMonth() {
    return month;
  }

  public void setMonth(Integer month) {
    this.month = month;
  }

  @Column(name = "day")
  public Integer getDay() {
    return day;
  }

  public void setDay(Integer day) {
    this.day = day;
  }

  @Column(name = "first_section_amount")
  public Long getFirstSectionAmount() {
    return firstSectionAmount;
  }

  public void setFirstSectionAmount(Long firstSectionAmount) {
    this.firstSectionAmount = firstSectionAmount;
  }

  @Column(name = "first_section_average")
  public Double getFirstSectionAverage() {
    return firstSectionAverage;
  }

  public void setFirstSectionAverage(Double firstSectionAverage) {
    this.firstSectionAverage = firstSectionAverage;
  }

  @Column(name = "first_section_total")
  public Double getFirstSectionTotal() {
    return firstSectionTotal;
  }

  public void setFirstSectionTotal(Double firstSectionTotal) {
    this.firstSectionTotal = firstSectionTotal;
  }

  @Column(name = "second_section_amount")
  public Long getSecondSectionAmount() {
    return secondSectionAmount;
  }

  public void setSecondSectionAmount(Long secondSectionAmount) {
    this.secondSectionAmount = secondSectionAmount;
  }

  @Column(name = "second_section_average")
  public Double getSecondSectionAverage() {
    return secondSectionAverage;
  }

  public void setSecondSectionAverage(Double secondSectionAverage) {
    this.secondSectionAverage = secondSectionAverage;
  }

  @Column(name = "second_section_total")
  public Double getSecondSectionTotal() {
    return secondSectionTotal;
  }

  public void setSecondSectionTotal(Double secondSectionTotal) {
    this.secondSectionTotal = secondSectionTotal;
  }

  @Column(name = "third_section_amount")
  public Long getThirdSectionAmount() {
    return thirdSectionAmount;
  }

  public void setThirdSectionAmount(Long thirdSectionAmount) {
    this.thirdSectionAmount = thirdSectionAmount;
  }

  @Column(name = "third_section_average")
  public Double getThirdSectionAverage() {
    return thirdSectionAverage;
  }

  public void setThirdSectionAverage(Double thirdSectionAverage) {
    this.thirdSectionAverage = thirdSectionAverage;
  }

  @Column(name = "third_section_total")
  public Double getThirdSectionTotal() {
    return thirdSectionTotal;
  }

  public void setThirdSectionTotal(Double thirdSectionTotal) {
    this.thirdSectionTotal = thirdSectionTotal;
  }

  @Column(name = "start_time")
  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @Column(name = "end_time")
  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public RequestPerformanceMonitorDTO toDTO() {
    RequestPerformanceMonitorDTO requestPerformanceMonitorDTO = new RequestPerformanceMonitorDTO();

    requestPerformanceMonitorDTO.setUrl(getUrl());
    requestPerformanceMonitorDTO.setNode(getNode());

    requestPerformanceMonitorDTO.setYear(getYear());
    requestPerformanceMonitorDTO.setMonth(getMonth());
    requestPerformanceMonitorDTO.setDay(getDay());

    requestPerformanceMonitorDTO.setFirstSectionAmount(getFirstSectionAmount());
    requestPerformanceMonitorDTO.setFirstSectionAverage(getFirstSectionAverage());
    requestPerformanceMonitorDTO.setFirstSectionTotal(getFirstSectionTotal());

    requestPerformanceMonitorDTO.setSecondSectionAmount(getSecondSectionAmount());
    requestPerformanceMonitorDTO.setSecondSectionAverage(getSecondSectionAverage());
    requestPerformanceMonitorDTO.setSecondSectionTotal(getSecondSectionTotal());

    requestPerformanceMonitorDTO.setThirdSectionAverage(getThirdSectionAverage());
    requestPerformanceMonitorDTO.setThirdSectionAmount(getThirdSectionAmount());
    requestPerformanceMonitorDTO.setThirdSectionTotal(getThirdSectionTotal());

    requestPerformanceMonitorDTO.setStartTime(getStartTime());
    requestPerformanceMonitorDTO.setEndTime(getEndTime());

    return requestPerformanceMonitorDTO;
  }


  public RequestPerformanceMonitor fromDTO(RequestPerformanceMonitorDTO requestPerformanceMonitorDTO) {
    this.setUrl(requestPerformanceMonitorDTO.getUrl());
    this.setNode(requestPerformanceMonitorDTO.getNode());

    this.setYear(requestPerformanceMonitorDTO.getYear());
    this.setMonth(requestPerformanceMonitorDTO.getMonth());
    this.setDay(requestPerformanceMonitorDTO.getDay());

    this.setFirstSectionAmount(requestPerformanceMonitorDTO.getFirstSectionAmount());
    this.setFirstSectionAverage(requestPerformanceMonitorDTO.getFirstSectionAverage());
    this.setFirstSectionTotal(requestPerformanceMonitorDTO.getFirstSectionTotal());

    this.setSecondSectionAmount(requestPerformanceMonitorDTO.getSecondSectionAmount());
    this.setSecondSectionAverage(requestPerformanceMonitorDTO.getSecondSectionAverage());
    this.setSecondSectionTotal(requestPerformanceMonitorDTO.getSecondSectionTotal());

    this.setThirdSectionAverage(requestPerformanceMonitorDTO.getThirdSectionAverage());
    this.setThirdSectionAmount(requestPerformanceMonitorDTO.getThirdSectionAmount());
    this.setThirdSectionTotal(requestPerformanceMonitorDTO.getThirdSectionTotal());

    this.setStartTime(requestPerformanceMonitorDTO.getStartTime());
    this.setEndTime(requestPerformanceMonitorDTO.getEndTime());

    return this;
  }

}
