package com.bcgogo.product.model.app;

import com.bcgogo.BooleanEnum;
import com.bcgogo.api.DictionaryFaultInfoDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;
import java.lang.String;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午1:19
 */
@Entity
@Table(name = "dictionary_fault_info")
public class DictionaryFaultInfo extends LongIdentifier {
  private String faultCode;//故障码
  private String description;//故障码描述
  private Long dictionaryId;//字典id
  private String category; //故障分类
  private String backgroundInfo;//背景知识

  public DictionaryFaultInfoDTO toDTO(){
    DictionaryFaultInfoDTO dto = new DictionaryFaultInfoDTO();
    dto.setId(getId());
    dto.setDescription(getDescription());
    dto.setDictionaryId(getDictionaryId());
    dto.setFaultCode(getFaultCode());
    dto.setCategory(getCategory());
    dto.setBackgroundInfo(getBackgroundInfo());
    return dto;
  }

  @Column(name = "fault_code")
  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultCode(String faultCode) {
    this.faultCode = faultCode;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "dictionary_id")
  public Long getDictionaryId() {
    return dictionaryId;
  }

  public void setDictionaryId(Long dictionaryId) {
    this.dictionaryId = dictionaryId;
  }

  @Column(name = "category")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Column(name = "background_info")
  public String getBackgroundInfo() {
    return backgroundInfo;
  }

  public void setBackgroundInfo(String backgroundInfo) {
    this.backgroundInfo = backgroundInfo;
  }
}
