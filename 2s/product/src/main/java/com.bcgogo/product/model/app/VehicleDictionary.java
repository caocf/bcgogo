package com.bcgogo.product.model.app;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午1:18
 */
@Entity
@Table(name = "vehicle_dictionary")
public class VehicleDictionary extends LongIdentifier {
  private Long dictionaryId;//字典id
  private Long brandId;//车辆品牌id
  private Long modelId;//车型id

  @Column(name = "dictionary_id")
  public Long getDictionaryId() {
    return dictionaryId;
  }

  public void setDictionaryId(Long dictionaryId) {
    this.dictionaryId = dictionaryId;
  }

  @Column(name = "brand_id")
  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  @Column(name = "model_id")
  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }
}
