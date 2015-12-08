package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * 店铺服务范围 只存三级的服务范围
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-10
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_service_category")
public class ShopServiceCategory extends LongIdentifier {
  private Long shopId;
  private Long serviceCategoryId;
  private DeletedType deleted = DeletedType.FALSE;

  public ShopServiceCategory(Long serviceCategoryId, Long shopId) {
    this.shopId = shopId;
    this.serviceCategoryId = serviceCategoryId;
  }

  public ShopServiceCategory() {
  }

  public ShopServiceCategoryDTO toDTO(){
    ShopServiceCategoryDTO shopServiceCategoryDTO = new ShopServiceCategoryDTO();
    shopServiceCategoryDTO.setId(getId());
    shopServiceCategoryDTO.setShopId(getShopId());
    shopServiceCategoryDTO.setServiceCategoryId(getServiceCategoryId());
    shopServiceCategoryDTO.setDeleted(getDeleted());
    return shopServiceCategoryDTO;
  }

  public void fromDTO(ShopServiceCategoryDTO shopServiceCategoryDTO) {
     this.setShopId(shopServiceCategoryDTO.getShopId());
     this.setServiceCategoryId(shopServiceCategoryDTO.getServiceCategoryId());
     this.setDeleted(shopServiceCategoryDTO.getDeleted());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "service_category_id")
  public Long getServiceCategoryId() {
    return serviceCategoryId;
  }

  public void setServiceCategoryId(Long serviceCategoryId) {
    this.serviceCategoryId = serviceCategoryId;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
