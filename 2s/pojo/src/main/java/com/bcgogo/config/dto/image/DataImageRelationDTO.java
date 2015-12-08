package com.bcgogo.config.dto.image;

import com.bcgogo.api.AppUserImageDTO;
import com.bcgogo.api.EnquiryDTO;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-29
 * Time: 上午11:09
 * To change this template use File | Settings | File Templates.
 */
public class DataImageRelationDTO implements Serializable {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long dataId;
  private DataType dataType;
  private ImageType imageType;//图片类型(跟使用场景有映射关系)；
  private int imageSequence;//图片在使用场景中的顺序位置（如在商品详情页面中详细介绍需要使用的图片可能有多张）；
  private Long imageId;//图片信息ID；
  private ObjectStatus status;
  private String appUserNo;

  private ImageInfoDTO imageInfoDTO;

  //用于新增或者更新dataImageRelation，imageInfoDTO
  public void fromEnquiryImage(EnquiryDTO enquiryDTO, AppUserImageDTO enquiryImage) {
    setDataType(DataType.APP_ENQUIRY);
    if (imageInfoDTO == null) {
      imageInfoDTO = new ImageInfoDTO();
    }
    if (enquiryDTO != null) {
      setDataId(enquiryDTO.getId());
      setAppUserNo(enquiryDTO.getAppUserNo());
      setStatus(ObjectStatus.ENABLED);
      imageInfoDTO.setAppUserNo(enquiryDTO.getAppUserNo());

    }
    if (enquiryImage != null) {
      imageInfoDTO.setStatus(ObjectStatus.ENABLED);
      imageInfoDTO.setPath(enquiryImage.getImagePath());
      setImageId(enquiryImage.getImageId());
      setImageSequence(enquiryImage.getSequence());
      if (enquiryImage.getSequence() == 0) {
        setImageType(ImageType.ENQUIRY_ORDER_MAIN_IMAGE);
      } else {
        setImageType(ImageType.ENQUIRY_ORDER_AUXILIARY_IMAGE);
      }
    }
  }

  public DataImageRelationDTO(){

  }

  public DataImageRelationDTO(Long shopId, Long dataId, DataType dataType, ImageType imageType, int imageSequence) {
    this.shopId = shopId;
    this.dataId = dataId;
    this.dataType = dataType;
    this.imageType = imageType;
    this.imageSequence = imageSequence;
    this.status = ObjectStatus.ENABLED;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public ImageInfoDTO getImageInfoDTO() {
    return imageInfoDTO;
  }

  public void setImageInfoDTO(ImageInfoDTO imageInfoDTO) {
    this.imageInfoDTO = imageInfoDTO;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null) idStr = id.toString();
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }

  public DataType getDataType() {
    return dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

  public ImageType getImageType() {
    return imageType;
  }

  public void setImageType(ImageType imageType) {
    this.imageType = imageType;
  }

  public int getImageSequence() {
    return imageSequence;
  }

  public void setImageSequence(int imageSequence) {
    this.imageSequence = imageSequence;
  }

  public Long getImageId() {
    return imageId;
  }

  public void setImageId(Long imageId) {
    this.imageId = imageId;
  }

  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }


}
