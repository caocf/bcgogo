package com.bcgogo.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-12-6
 * Time: 上午10:13
 */
public class BrandDTO {
  private Long brandId;
  private String brandName;
  private List<ModelDTO> models = new ArrayList<ModelDTO>();

  public BrandDTO(Long brandId, String brandName) {
    this.setBrandId(brandId);
    this.setBrandName(brandName);
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public List<ModelDTO> getModels() {
    return models;
  }

  public void setModels(List<ModelDTO> models) {
    this.models = models;
  }

}
