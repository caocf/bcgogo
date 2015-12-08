package com.bcgogo.user.dto;

import java.io.Serializable;

/**
 * 系统功能使用监控类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-23
 * Time: 下午5:46
 * To change this template use File | Settings | File Templates.
 */
public class UrlMonitorStatDTO implements Serializable {
  private static final long serialVersionUID = -7450269145853112132L;


  private Long id;
  private Long shopId;   //店铺
  private String userNo;  //用户
  private String url; //访问url
  private Integer year; //年
  private Integer month; //月
  private Integer day;  //日
  private Long count;   //访问次数
  private Long statTime; //统计时间

  public UrlMonitorStatDTO() {
    setCount(0L);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  @Override
  public String toString() {
    return "UrlMonitorStatDTO{" +
        "shopId=" + shopId +
        ", userNo='" + userNo + '\'' +
        ", url='" + url + '\'' +
        ", year=" + year +
        ", month=" + month +
        ", day=" + day +
        ", count=" + count +
        ", statTime=" + statTime +
        '}';
  }
}
