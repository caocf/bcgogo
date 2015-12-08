package com.bcgogo.api;

import com.bcgogo.BooleanEnum;
import com.bcgogo.base.BaseDTO;

/**
 * User: ZhangJuntao
 * Date: 13-8-26
 * Time: 上午11:54
 */
public class DictionaryDTO extends BaseDTO {
  private String dictionaryName;//字典名称
  private String dictionaryVersion;//字典版本
  private BooleanEnum isCommon; //是否通用字典，是True，不是False

  public String getDictionaryName() {
    return dictionaryName;
  }

  public void setDictionaryName(String dictionaryName) {
    this.dictionaryName = dictionaryName;
  }

  public String getDictionaryVersion() {
    return dictionaryVersion;
  }

  public void setDictionaryVersion(String dictionaryVersion) {
    this.dictionaryVersion = dictionaryVersion;
  }

  public BooleanEnum getIsCommon() {
    return isCommon;
  }

  public void setIsCommon(BooleanEnum common) {
    isCommon = common;
  }
}
