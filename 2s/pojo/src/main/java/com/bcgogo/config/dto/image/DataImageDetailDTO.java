package com.bcgogo.config.dto.image;

import com.bcgogo.api.AppUserImageDTO;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-29
 * Time: 下午5:44
 * To change this template use File | Settings | File Templates.
 */
public class DataImageDetailDTO {
  private Long dataImageRelationId;
  private String dataImageRelationIdStr;
  private Long shopId;
  private Long dataId;
  private DataType dataType;
  private ImageType imageType;//图片类型(跟使用场景有映射关系)；
  private int imageSequence;//图片在使用场景中的顺序位置（如在商品详情页面中详细介绍需要使用的图片可能有多张）；
  private Long imageId;//图片信息ID；
  private ObjectStatus status;
  private String imageURL;//完整路径
  private String imagePath;//相对路径

  public DataImageDetailDTO(){

  }
  public DataImageDetailDTO(int imageSequence,String imageURL){
    this.imageSequence =imageSequence;
    this.imageURL = imageURL;
  }
  public DataImageDetailDTO(DataImageRelationDTO dataImageRelationDTO,String imageURL,String imagePath){
    if(dataImageRelationDTO!=null && StringUtils.isNotBlank(imageURL)){
      this.shopId = dataImageRelationDTO.getShopId();
      this.dataId = dataImageRelationDTO.getDataId();
      this.dataType = dataImageRelationDTO.getDataType();
      this.imageType = dataImageRelationDTO.getImageType();
      this.imageSequence = dataImageRelationDTO.getImageSequence();
      this.imageId = dataImageRelationDTO.getImageId();
      this.dataImageRelationId = dataImageRelationDTO.getId();
      this.dataImageRelationIdStr=StringUtil.valueOf(dataImageRelationDTO.getId());
      this.status = dataImageRelationDTO.getStatus();

      this.imageURL = imageURL;
      this.imagePath = imagePath;
    }
  }

  public AppUserImageDTO toAppUserImageDTO() {
    AppUserImageDTO imageDTO = new AppUserImageDTO();
    imageDTO.setImageId(getImageId());
    imageDTO.setImagePath(getImagePath());
    imageDTO.setImageType(getImageType());
    imageDTO.setImageUrl(getImageURL());
    imageDTO.setSequence(getImageSequence());
    return imageDTO;
  }

  public Long getDataImageRelationId() {
    return dataImageRelationId;
  }

  public void setDataImageRelationId(Long dataImageRelationId) {
    this.setDataImageRelationIdStr(StringUtil.valueOf(dataImageRelationId));
    this.dataImageRelationId = dataImageRelationId;
  }

  public String getDataImageRelationIdStr() {
    return dataImageRelationIdStr;
  }

  public void setDataImageRelationIdStr(String dataImageRelationIdStr) {
    this.dataImageRelationIdStr = dataImageRelationIdStr;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
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

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public int getImageSequence() {
    return imageSequence;
  }

  public void setImageSequence(int imageSequence) {
    this.imageSequence = imageSequence;
  }

}
