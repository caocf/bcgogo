package com.bcgogo.config.dto;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.enums.DeletedType;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-10
 * Time: 上午10:06
 * To change this template use File | Settings | File Templates.
 */
public class ShopServiceCategoryDTO extends BaseDTO{
  private Long shopId;
  private Long serviceCategoryId;
  private String serviceCategoryName;
  private DeletedType deleted;

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
