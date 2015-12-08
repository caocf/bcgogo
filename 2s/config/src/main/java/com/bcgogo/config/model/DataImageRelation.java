package com.bcgogo.config.model;

import com.bcgogo.api.AppUserImageDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "data_image_relation")
public class DataImageRelation extends LongIdentifier {
  private Long shopId;
  private Long dataId;
  private DataType dataType;
  private ImageType imageType;//图片类型(跟使用场景有映射关系)；
  private int imageSequence;//图片在使用场景中的顺序位置（如在商品详情页面中详细介绍需要使用的图片可能有多张）；
  private Long imageId;//图片信息ID；
  private ObjectStatus status;
  private String appUserNo;

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="data_id")
  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "data_type")
  public DataType getDataType() {
    return dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "image_type")
  public ImageType getImageType() {
    return imageType;
  }

  public void setImageType(ImageType imageType) {
    this.imageType = imageType;
  }

  @Column(name="image_sequence")
  public int getImageSequence() {
    return imageSequence;
  }

  public void setImageSequence(int imageSequence) {
    this.imageSequence = imageSequence;
  }

  @Column(name="image_id")
  public Long getImageId() {
    return imageId;
  }

  public void setImageId(Long imageId) {
    this.imageId = imageId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public DataImageRelationDTO toDTO(){
    DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO();
    dataImageRelationDTO.setDataId(this.getDataId());
    dataImageRelationDTO.setDataType(this.getDataType());
    dataImageRelationDTO.setId(this.getId());
    dataImageRelationDTO.setImageId(this.getImageId());
    dataImageRelationDTO.setImageType(this.getImageType());
    dataImageRelationDTO.setImageSequence(this.getImageSequence());
    dataImageRelationDTO.setStatus(this.getStatus());
    dataImageRelationDTO.setShopId(this.getShopId());
    dataImageRelationDTO.setAppUserNo(this.getAppUserNo());
    return dataImageRelationDTO;
  }

  public void fromDTO(DataImageRelationDTO dataImageRelationDTO){
    this.setDataId(dataImageRelationDTO.getDataId());
    this.setDataType(dataImageRelationDTO.getDataType());
    this.setImageType(dataImageRelationDTO.getImageType());
    this.setImageSequence(dataImageRelationDTO.getImageSequence());
    this.setStatus(dataImageRelationDTO.getStatus());
    this.setShopId(dataImageRelationDTO.getShopId());
    this.setAppUserNo(dataImageRelationDTO.getAppUserNo());
    this.setImageId(dataImageRelationDTO.getImageId());
  }

  public void fromAppUserImageDTO(String appUserNo, Long dataId, AppUserImageDTO imageDTO, DataType dataType) {
    this.setAppUserNo(appUserNo);
    this.setDataId(dataId);
    this.setDataType(dataType);
    if (imageDTO != null) {
      this.setImageType(imageDTO.getImageType());
      this.setImageSequence(imageDTO.getSequence());
      this.setStatus(ObjectStatus.ENABLED);
    }
  }
}
