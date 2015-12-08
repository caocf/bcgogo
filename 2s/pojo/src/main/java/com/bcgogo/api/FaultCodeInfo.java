package com.bcgogo.api;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午4:09
 */
public class FaultCodeInfo {
  private String faultCode;//故障码
  private String description;//故障描述
  private String category; //故障分类
  private String backgroundInfo;//背景知识

  public FaultCodeInfo() {
  }

  public FaultCodeInfo(String faultCode, String description) {
    this.faultCode = faultCode;
    this.description = description;
  }

  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultCode(String faultCode) {
    this.faultCode = faultCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getBackgroundInfo() {
    return backgroundInfo;
  }

  public void setBackgroundInfo(String backgroundInfo) {
    this.backgroundInfo = backgroundInfo;
  }
}
