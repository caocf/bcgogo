package com.bcgogo.config.dto.juhe;

import com.bcgogo.utils.StringUtil;

/**
 * User: ZhangJuntao
 * Date: 13-11-8
 * Time: 上午10:42
 */
public class ViolateRegulationCitySearchConditionElement {
  private String key;
  private boolean necessary = false;
  private String orgValue;
  private int valueNum;
  private String targetValue;
  private boolean success = true;
  private String message = "";

  public ViolateRegulationCitySearchConditionElement(String key) {
    this.setKey(key);
  }

  //0,不需要
  //1,需要
  public ViolateRegulationCitySearchConditionElement buildNecessary(int necessary) {
    setNecessary(necessary == 1);
    return this;
  }

  public ViolateRegulationCitySearchConditionElement buildTargetValue(String targetValue) {
    this.setTargetValue(targetValue);
    return this;
  }

  public ViolateRegulationCitySearchConditionElement buildOrgValue(String orgValue) {
    this.setOrgValue(orgValue);
    if (StringUtil.isNotEmpty(orgValue)) {
      if (getValueNum() != 0 && getValueNum() < orgValue.length())
        this.buildTargetValue(orgValue.substring(orgValue.length() - getValueNum(), orgValue.length()));
      else
        this.buildTargetValue(orgValue);
    }
    return this;
  }

  public ViolateRegulationCitySearchConditionElement buildValueNum(int valueNum) {
    this.setValueNum(valueNum);
    return this;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public boolean isNecessary() {
    return necessary;
  }

  public void setNecessary(boolean necessary) {
    this.necessary = necessary;
  }

  public int getValueNum() {
    return valueNum;
  }

  public void setValueNum(int valueNum) {
    this.valueNum = valueNum;
  }

  public String getOrgValue() {
    return orgValue;
  }

  public void setOrgValue(String orgValue) {
    this.orgValue = orgValue;
  }

  public String getTargetValue() {
    return targetValue;
  }

  public void setTargetValue(String targetValue) {
    this.targetValue = targetValue;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
