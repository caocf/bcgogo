package com.bcgogo.api;

import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 上午11:01
 */
public class BrandModelDTO {
  private Long modelId;
  private String modelName;
  private Long brandId;
  private String brandName;

  public BrandModelDTO() {
  }

  public BrandModelDTO(StandardVehicleBrandDTO sb, StandardVehicleModelDTO sm) {
    setModelId(sm.getId());
    setModelName(sm.getName());
    setBrandId(sb.getId());
    setBrandName(sb.getName());
  }

  @Override
  public int hashCode() {
    if (modelId != null)
      return modelId.intValue();
    else if (brandId != null) {
      return brandId.intValue();
    } else {
      return 0;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BrandModelDTO) {
      BrandModelDTO dto = (BrandModelDTO) obj;
      if (modelId != null) {
        return modelId.equals(dto.getModelId());
      } else {
        return dto.getModelId() == null && (dto.getBrandId() != null ? (dto.getBrandId().equals(brandId)) : (brandId == null || brandId.equals(dto.getBrandId())));
      }
    } else {
      return false;
    }
  }

  public void from(StandardVehicleBrandDTO dto) {
    this.setBrandId(dto.getId());
    this.setBrandName(dto.getName());
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
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


}
