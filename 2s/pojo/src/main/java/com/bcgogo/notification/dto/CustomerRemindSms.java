package com.bcgogo.notification.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-4-10
 * Time: 下午4:04
 * To change this template use File | Settings | File Templates.
 */
public class CustomerRemindSms {
  private Long shopId;
  private Long customerServiceJobId;
  private String mobile;
  private String name;
  private String userName;
  private Integer type;
  private String money;
  private String licenceNo;
  private String year;
  private String month;
  private String day;
  //返回内容
  private String title;
  private String content;
  private String appointName;
  //会员服务项目
  private Long memberServiceId;
  private String serviceName;
  private boolean templateFlag=false;
  private boolean appFlag=false;
  private boolean smsFlag=false;

  public boolean isAppFlag() {
    return appFlag;
  }

  public void setAppFlag(boolean appFlag) {
    this.appFlag = appFlag;
  }

  public boolean isSmsFlag() {
    return smsFlag;
  }

  public void setSmsFlag(boolean smsFlag) {
    this.smsFlag = smsFlag;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMoney() {
    return money;
  }

  public void setMoney(String money) {
    this.money = money;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getAppointName() {
    return appointName;
  }

  public void setAppointName(String appointName) {
    this.appointName = appointName;
  }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {

    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Long getCustomerServiceJobId() {
    return customerServiceJobId;
  }

  public void setCustomerServiceJobId(Long customerServiceJobId) {
    this.customerServiceJobId = customerServiceJobId;
  }

  public Long getMemberServiceId() {
    return memberServiceId;
  }

  public void setMemberServiceId(Long memberServiceId) {
    this.memberServiceId = memberServiceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public boolean isTemplateFlag() {
    return templateFlag;
  }

  public void setTemplateFlag(boolean templateFlag) {
    this.templateFlag = templateFlag;
  }
}
