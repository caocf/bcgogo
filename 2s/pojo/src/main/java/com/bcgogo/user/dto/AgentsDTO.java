package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-7
 * Time: 下午3:12
 * To change this template use File | Settings | File Templates.
 */
public class AgentsDTO {
  private Long id;
  private Long agentId;
  private String agentCode;
  private String name;
  private String address;
  private String mobile;
  private String respArea;
  private double monthTarget;
  private double seasonTarget;
  private double yearTarget;
  private int state;
  private String personInCharge;
  private int month;
  private String year;

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }



  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }

  public String getPersonInCharge() {
    return personInCharge;
  }

  public void setPersonInCharge(String personInCharge) {
    this.personInCharge = personInCharge;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public String getAgentCode() {
    return agentCode;
  }

  public void setAgentCode(String agentCode) {
    this.agentCode = agentCode;
  }

  public double getMonthTarget() {
    return monthTarget;
  }

  public void setMonthTarget(double monthTarget) {
    this.monthTarget = monthTarget;
  }

  public double getSeasonTarget() {
    return seasonTarget;
  }

  public void setSeasonTarget(double seasonTarget) {
    this.seasonTarget = seasonTarget;
  }

  public double getYearTarget() {
    return yearTarget;
  }

  public void setYearTarget(double yearTarget) {
    this.yearTarget = yearTarget;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getRespArea() {
    return respArea;
  }

  public void setRespArea(String respArea) {
    this.respArea = respArea;
  }
}
