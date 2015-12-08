package com.bcgogo.api;

import com.bcgogo.base.BaseDTO;

/**
 * User: ZhangJuntao
 * Date: 13-8-26
 * Time: 下午1:22
 */
public class DictionaryFaultInfoDTO extends BaseDTO{
  private String faultCode;//故障码
  private String description;//故障码描述
  private Long dictionaryId;//字典id
  private String category; //故障分类
  private String backgroundInfo;//背景知识

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

  public Long getDictionaryId() {
    return dictionaryId;
  }

  public void setDictionaryId(Long dictionaryId) {
    this.dictionaryId = dictionaryId;
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

  public FaultCodeInfo toFaultCodeInfo() {
    FaultCodeInfo faultCodeInfo = new FaultCodeInfo();
    faultCodeInfo.setDescription(getDescription());
    faultCodeInfo.setFaultCode(getFaultCode());
    faultCodeInfo.setCategory(getCategory());
    faultCodeInfo.setBackgroundInfo(getBackgroundInfo());
    return faultCodeInfo;
  }
}
