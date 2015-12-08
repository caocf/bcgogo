package com.bcgogo.api;

import com.bcgogo.base.BaseDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-2
 * Time: 下午12:59
 */
public class VehicleDictionaryDTO extends BaseDTO {
  private Long dictionaryId;//字典id
  private Long brandId;//车辆品牌id
  private Long modelId;//车型id

  public Long getDictionaryId() {
    return dictionaryId;
  }

  public void setDictionaryId(Long dictionaryId) {
    this.dictionaryId = dictionaryId;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }
}
