package com.bcgogo.user.dto;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.user.ServiceCategoryDataType;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 13-9-10
 * Time: 上午10:06
 * To change this template use File | Settings | File Templates.
 */
public class ServiceCategoryRelationDTO extends BaseDTO{
  private Long shopId;
  private Long serviceCategoryId;
  private String serviceCategoryName;
  private Long dataId;
  private ServiceCategoryDataType DataType;
  private DeletedType deleted;

  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }

  public ServiceCategoryDataType getDataType() {
    return DataType;
  }

  public void setDataType(ServiceCategoryDataType dataType) {
    DataType = dataType;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getServiceCategoryId() {
    return serviceCategoryId;
  }

  public void setServiceCategoryId(Long serviceCategoryId) {
    this.serviceCategoryId = serviceCategoryId;
  }

  public String getServiceCategoryName() {
    return serviceCategoryName;
  }

  public void setServiceCategoryName(String serviceCategoryName) {
    this.serviceCategoryName = serviceCategoryName;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
