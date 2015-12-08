package com.bcgogo.user.model.SystemMonitor;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.UrlMonitorStatDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统功能使用统计表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-2-20
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "url_monitor_stat")
public class UrlMonitorStat extends LongIdentifier {
  private Long shopId;   //店铺
  private String userNo;  //用户
  private String url; //访问url
  private Integer year; //年
  private Integer month; //月
  private Integer day;  //日
  private Long count;   //访问次数
  private Long statTime; //统计时间

  public UrlMonitorStat() {
    setCount(0L);
  }

  public UrlMonitorStatDTO toDTO() {
    UrlMonitorStatDTO urlMonitorStatDTO = new UrlMonitorStatDTO();
    urlMonitorStatDTO.setId(getId());
    urlMonitorStatDTO.setShopId(getShopId());
    urlMonitorStatDTO.setUserNo(getUserNo());
    urlMonitorStatDTO.setUrl(getUrl());
    urlMonitorStatDTO.setYear(getYear());
    urlMonitorStatDTO.setMonth(getMonth());
    urlMonitorStatDTO.setDay(getDay());
    urlMonitorStatDTO.setCount(getCount());
    urlMonitorStatDTO.setStatTime(getStatTime());
    return urlMonitorStatDTO;
  }

  public UrlMonitorStat fromDTO(UrlMonitorStatDTO urlMonitorStatDTO) {
    if (urlMonitorStatDTO == null) {
      return this;
    }
    if (urlMonitorStatDTO.getId() != null) {
      this.setId(urlMonitorStatDTO.getId());
    }
    this.setShopId(urlMonitorStatDTO.getShopId());
    this.setUserNo(urlMonitorStatDTO.getUserNo());
    this.setUrl(urlMonitorStatDTO.getUrl());
    this.setYear(urlMonitorStatDTO.getYear());
    this.setMonth(urlMonitorStatDTO.getMonth());
    this.setDay(urlMonitorStatDTO.getDay());
    this.setCount(urlMonitorStatDTO.getCount());
    this.setStatTime(urlMonitorStatDTO.getStatTime());

    return this;
  }



  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_no")
  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  @Column(name = "count")
  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count == null? 0L :count.longValue();
  }

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  @Override
  public String toString() {
    return "UrlMonitorStat{" +
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
