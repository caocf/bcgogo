package com.bcgogo.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.NONE)
public class UserRequest {

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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getLoginTimes() {
    return loginTimes;
  }

  public void setLoginTimes(Long loginTimes) {
    this.loginTimes = loginTimes;
  }

  public Long getLastTime() {
    return lastTime;
  }

  public void setLastTime(Long lastTime) {
    this.lastTime = lastTime;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @XmlElement(name = "shop_id")
  private Long shopId;
  @XmlElement(name = "user_no")
  private String userNo;
  @XmlElement(name = "user_name")
  private String userName;
  @XmlElement(name = "password")
  private String password;
  @XmlElement(name = "name")
  private String name;
  @XmlElement(name = "loin_times")
  private Long loginTimes;
  @XmlElement(name = "last_time")
  private Long lastTime;
  @XmlElement(name = "email")
  private String email;
  @XmlElement(name = "mobile")
  private String mobile;
  @XmlElement(name = "qq")
  private String qq;
  @XmlElement(name = "state")
  private Integer state;
  @XmlElement(name = "memo")
  private String memo;
}
