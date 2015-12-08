package com.bcgogo.api;

import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.enums.config.ImageType;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-17
 * Time: 上午11:44
 */
public class AppUserImageDTO implements Serializable {
  private Long imageId;//图片的Id data_image_relation 的Id
  private String imagePath;//相对路径 （不带版本信息）
  private String imageUrl;//绝对路径 （不带版本信息）
  private int sequence;//照片顺序
  private String smallImageUrl; //大图的完整路径
  private String bigImageUrl;//小图的完整路径
  private ImageType imageType;

  public AppUserImageDTO() {
  }

  public AppUserImageDTO(Long imageId, String imagePath, String imageUrl, int sequence, String smallImageUrl, String bigImageUrl,ImageType imageType) {
    this.imageId = imageId;
    this.imagePath = imagePath;
    this.imageUrl = imageUrl;
    this.sequence = sequence;
    this.smallImageUrl = smallImageUrl;
    this.bigImageUrl = bigImageUrl;
    this.imageType = imageType;
  }

  public Long getImageId() {
    return imageId;
  }

  public void setImageId(Long imageId) {
    this.imageId = imageId;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public int getSequence() {
    return sequence;
  }

  public void setSequence(int sequence) {
    this.sequence = sequence;
  }

  public String getSmallImageUrl() {
    return smallImageUrl;
  }

  public void setSmallImageUrl(String smallImageUrl) {
    this.smallImageUrl = smallImageUrl;
  }

  public String getBigImageUrl() {
    return bigImageUrl;
  }

  public void setBigImageUrl(String bigImageUrl) {
    this.bigImageUrl = bigImageUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public boolean validate() {
    return StringUtils.isNotBlank(getImagePath()) || getImageId() != null;
  }

  public ImageType getImageType() {
    return imageType;
  }

  public void setImageType(ImageType imageType) {
    this.imageType = imageType;
  }

  public void setImageInfo(DataImageRelationDTO dataImageRelationDTO, ImageInfoDTO imageInfoDTO) {
    if(dataImageRelationDTO != null){
      setImageId(dataImageRelationDTO.getId());
      setSequence(dataImageRelationDTO.getImageSequence());
      setImageType(dataImageRelationDTO.getImageType());
    }
    if(imageInfoDTO != null){
      setImagePath(imageInfoDTO.getPath());
    }
  }
}
