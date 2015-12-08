package com.bcgogo.user.dto;

import java.io.Serializable;

/**
 * 用户客户端信息封装类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-19
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 */
public class UserClientInfoDTO implements Serializable {
  private Long id;
  private Long shopId;
  private String userNo;
  private String browser;//浏览器
  private String os;//操作系统
  private String cpu;//cpu
  private String finger;
  private Integer score;//finger准确率得分.共100分

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

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public String getFinger() {
    return finger;
  }

  public void setFinger(String finger) {
    this.finger = finger;
  }

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  @Override
  public String toString() {
    return "UserClientInfoDTO{" +
        "shopId=" + shopId +
        ", userNo=" + userNo +
        ", browser='" + browser + '\'' +
        ", os='" + os + '\'' +
        ", cpu='" + cpu + '\'' +
        '}';
  }
}
