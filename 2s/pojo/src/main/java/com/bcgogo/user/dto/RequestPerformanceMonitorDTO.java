package com.bcgogo.user.dto;

import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;

/**
 * 系统性能监控封装类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-6
 * Time: 下午2:21
 * To change this template use File | Settings | File Templates.
 */
public class RequestPerformanceMonitorDTO implements Serializable {
  private String url;  //请求的url
  private String node; //请求的tomcat

  private Integer year; //年
  private Integer month; //月
  private Integer day; //日

  private Long firstSectionAmount = 0L;  //第一区间请求次数
  private Double firstSectionAverage = 0D; //第一区间请求响应时间平均值
  private Double firstSectionTotal = 0D;//第一区间请求响应总耗时数

  private Long secondSectionAmount = 0L;    //第二区间请求次数
  private Double secondSectionAverage = 0D; //第二区间请求响应时间平均值
  private Double secondSectionTotal = 0D;  //第二区间请求响应总耗时数

  private Long thirdSectionAmount = 0L;   //第三区间请求次数
  private Double thirdSectionAverage = 0D;  //第三区间请求响应时间平均值
  private Double thirdSectionTotal = 0D; //第三区间请求响应总耗时数

  private Long startTime;//统计开始时间
  private Long endTime; //统计结束时间

  private boolean statUrl;//是否是长连接（超过统计下限的请求）


  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    this.node = node;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public Integer getMonth() {
    return month;
  }

  public void setMonth(Integer month) {
    this.month = month;
  }

  public Integer getDay() {
    return day;
  }

  public void setDay(Integer day) {
    this.day = day;
  }

  public Long getFirstSectionAmount() {
    return firstSectionAmount == null?0L:firstSectionAmount;
  }

  public void setFirstSectionAmount(Long firstSectionAmount) {
    this.firstSectionAmount = firstSectionAmount;
  }

  public Double getFirstSectionAverage() {
    return firstSectionAverage  == null?0D:firstSectionAverage;
  }

  public void setFirstSectionAverage(Double firstSectionAverage) {
    this.firstSectionAverage = NumberUtil.toReserve(firstSectionAverage, 4);
  }

  public Double getFirstSectionTotal() {
    return firstSectionTotal  == null?0D:firstSectionTotal;
  }

  public void setFirstSectionTotal(Double firstSectionTotal) {
    this.firstSectionTotal = NumberUtil.toReserve(firstSectionTotal, 4);;
  }

  public Long getSecondSectionAmount() {
    return secondSectionAmount== null?0L:secondSectionAmount;
  }

  public void setSecondSectionAmount(Long secondSectionAmount) {
    this.secondSectionAmount = secondSectionAmount;
  }

  public Double getSecondSectionAverage() {
    return secondSectionAverage== null?0D:secondSectionAverage;
  }

  public void setSecondSectionAverage(Double secondSectionAverage) {
    this.secondSectionAverage = NumberUtil.toReserve(secondSectionAverage, 4);;
  }

  public Double getSecondSectionTotal() {
    return secondSectionTotal== null?0D:secondSectionTotal;
  }

  public void setSecondSectionTotal(Double secondSectionTotal) {
    this.secondSectionTotal = NumberUtil.toReserve(secondSectionTotal, 4);;
  }

  public Long getThirdSectionAmount() {
    return thirdSectionAmount== null?0L:thirdSectionAmount;
  }

  public void setThirdSectionAmount(Long thirdSectionAmount) {
    this.thirdSectionAmount = thirdSectionAmount;
  }

  public Double getThirdSectionAverage() {
    return thirdSectionAverage== null?0D:thirdSectionAverage;
  }

  public void setThirdSectionAverage(Double thirdSectionAverage) {
    this.thirdSectionAverage = NumberUtil.toReserve(thirdSectionAverage, 4);;
  }

  public Double getThirdSectionTotal() {
    return thirdSectionTotal== null?0D:thirdSectionTotal;
  }

  public void setThirdSectionTotal(Double thirdSectionTotal) {
    this.thirdSectionTotal = NumberUtil.toReserve(thirdSectionTotal, 4);;
  }

  public boolean isStatUrl() {
    return statUrl;
  }

  public void setStatUrl(boolean statUrl) {
    this.statUrl = statUrl;
  }

  public RequestPerformanceMonitorDTO calculate(RequestPerformanceMonitorDTO requestPerformanceMonitorDTO){
    this.setFirstSectionAmount(getFirstSectionAmount() + requestPerformanceMonitorDTO.getFirstSectionAmount());
    this.setFirstSectionTotal(getFirstSectionTotal() + requestPerformanceMonitorDTO.getFirstSectionTotal());
    this.setFirstSectionAverage(getFirstSectionTotal() == 0 ? 0 : (getFirstSectionTotal() / getFirstSectionAmount()));

    this.setSecondSectionAmount(getSecondSectionAmount() + requestPerformanceMonitorDTO.getSecondSectionAmount());
    this.setSecondSectionTotal(getSecondSectionTotal() + requestPerformanceMonitorDTO.getSecondSectionTotal());
    this.setSecondSectionAverage(getSecondSectionTotal() == 0 ? 0 : (getSecondSectionTotal() / getSecondSectionAmount()));

    this.setThirdSectionAmount(getThirdSectionAmount() + requestPerformanceMonitorDTO.getThirdSectionAmount());
    this.setThirdSectionTotal(getThirdSectionTotal() + requestPerformanceMonitorDTO.getThirdSectionTotal());
    this.setThirdSectionAverage(getThirdSectionTotal() == 0 ? 0 : (getThirdSectionTotal() / getThirdSectionAmount()));

    this.setEndTime(requestPerformanceMonitorDTO.getEndTime());

    return this;
  }


  public void setRequestSection(String configValue) throws Exception {
    Long requestTime = this.getEndTime() - this.getStartTime();
    String[] configArray = configValue.split("\\|");
    String firstConfigValueBegin = configArray[0].substring(configArray[0].indexOf("[")+1, configArray[0].indexOf(","));
    if (StringUtil.isEmpty(firstConfigValueBegin)) {
      this.setStatUrl(false);
      return;
    } else if (requestTime < Long.valueOf(firstConfigValueBegin)) {
      this.setStatUrl(false);
      return;
    }
    double requestSeconds = requestTime.doubleValue() / 1000;
    String firstConfigValueEnd = configArray[0].substring(configArray[0].indexOf(",")+1, configArray[0].indexOf("]"));
    String secondConfigValueEnd = configArray[1].substring(configArray[1].indexOf(",")+1, configArray[1].indexOf("]"));
    String thirdConfigValueBegin = configArray[2].substring(configArray[2].indexOf("[")+1, configArray[2].indexOf(","));
    if (requestTime < Long.valueOf(firstConfigValueEnd)) {
      this.setFirstSectionAmount(1L);
      this.setFirstSectionAverage(requestSeconds);
      this.setFirstSectionTotal(requestSeconds);
    } else if (requestTime < Long.valueOf(secondConfigValueEnd)) {
      this.setSecondSectionAmount(1L);
      this.setSecondSectionAverage(requestSeconds);
      this.setSecondSectionTotal(requestSeconds);
    } else if (requestTime >= Long.valueOf(thirdConfigValueBegin)) {
      this.setThirdSectionAmount(1L);
      this.setThirdSectionAverage(requestSeconds);
      this.setThirdSectionTotal(requestSeconds);
    }
    this.setStatUrl(true);
  }

  @Override
  public String toString() {
    return "RequestPerformanceMonitorDTO{" +
        "url='" + url + '\'' +
        ", node='" + node + '\'' +
        ", year=" + year +
        ", month=" + month +
        ", day=" + day +
        ", firstSectionAmount=" + firstSectionAmount +
        ", firstSectionAverage=" + firstSectionAverage +
        ", firstSectionTotal=" + firstSectionTotal +
        ", secondSectionAmount=" + secondSectionAmount +
        ", secondSectionAverage=" + secondSectionAverage +
        ", secondSectionTotal=" + secondSectionTotal +
        ", thirdSectionAmount=" + thirdSectionAmount +
        ", thirdSectionAverage=" + thirdSectionAverage +
        ", thirdSectionTotal=" + thirdSectionTotal +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", statUrl=" + statUrl +
        '}';
  }
}
