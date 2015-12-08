package com.bcgogo.user.model;

import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.user.ServiceCategoryDataType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.ServiceCategoryRelationDTO;

import javax.persistence.*;

/**
 * 店铺服务范围 只存2级的服务范围
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 13-9-10
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "service_category_relation")
public class ServiceCategoryRelation extends LongIdentifier {
  private Long shopId;
  private Long dataId;
  private ServiceCategoryDataType DataType;
  private Long serviceCategoryId;
  private DeletedType deleted = DeletedType.FALSE;

  public ServiceCategoryRelationDTO toDTO(){
    ServiceCategoryRelationDTO serviceCategoryRelationDTO = new ServiceCategoryRelationDTO();
    serviceCategoryRelationDTO.setId(getId());
    serviceCategoryRelationDTO.setShopId(getShopId());
    serviceCategoryRelationDTO.setDataId(getDataId());
    serviceCategoryRelationDTO.setDataType(getDataType());
    serviceCategoryRelationDTO.setServiceCategoryId(getServiceCategoryId());
    serviceCategoryRelationDTO.setDeleted(getDeleted());
    return serviceCategoryRelationDTO;
  }
  @Column(name = "data_id")
  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }
  @Column(name = "data_type")
  @Enumerated(EnumType.STRING)
  public ServiceCategoryDataType getDataType() {
    return DataType;
  }

  public void setDataType(ServiceCategoryDataType dataType) {
    DataType = dataType;
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
