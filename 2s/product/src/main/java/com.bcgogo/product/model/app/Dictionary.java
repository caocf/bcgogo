package com.bcgogo.product.model.app;

import com.bcgogo.BooleanEnum;
import com.bcgogo.api.DictionaryDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午1:18
 */
@Entity
@Table(name = "dictionary")
public class Dictionary extends LongIdentifier {
  private String dictionaryName;//字典名称
  private String dictionaryVersion;//字典版本
  private BooleanEnum isCommon; //是否通用字典，是True，不是False

  public DictionaryDTO toDTO() {
    DictionaryDTO dto = new DictionaryDTO();
    dto.setId(getId());
    dto.setDictionaryName(getDictionaryName());
    dto.setDictionaryVersion(getDictionaryVersion());
    dto.setIsCommon(getIsCommon());
    return dto;
  }

  @Column(name = "dictionary_name")
  public String getDictionaryName() {
    return dictionaryName;
  }

  public void setDictionaryName(String dictionaryName) {
    this.dictionaryName = dictionaryName;
  }

  @Column(name = "dictionary_version")
  public String getDictionaryVersion() {
    return dictionaryVersion;
  }

  public void setDictionaryVersion(String dictionaryVersion) {
    this.dictionaryVersion = dictionaryVersion;
  }


  @Column(name = "is_common")
  @Enumerated(EnumType.STRING)
  public BooleanEnum getIsCommon() {
    return isCommon;
  }

  public void setIsCommon(BooleanEnum common) {
    isCommon = common;
  }
}
