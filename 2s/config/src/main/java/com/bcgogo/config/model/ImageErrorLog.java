package com.bcgogo.config.model;

import com.bcgogo.config.dto.image.ImageErrorLogDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "image_error_log")
public class ImageErrorLog extends LongIdentifier {
  private Long shopId;
  private String code;
  private String message;
  private String url;
  private String content;
  private Long time;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
  @Column(name = "code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
  @Column(name = "message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "time")
  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }
  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ImageErrorLogDTO toDTO(){
    ImageErrorLogDTO imageErrorLogDTO = new ImageErrorLogDTO();
    imageErrorLogDTO.setShopId(this.getShopId());
    imageErrorLogDTO.setId(this.getId());
    imageErrorLogDTO.setTime(this.getTime());
    imageErrorLogDTO.setCode(this.getCode());
    imageErrorLogDTO.setMessage(this.getMessage());
    imageErrorLogDTO.setUrl(this.getUrl());
    imageErrorLogDTO.setContent(this.getContent());
    return imageErrorLogDTO;
  }

  public void fromDTO(ImageErrorLogDTO imageErrorLogDTO){
    if(imageErrorLogDTO==null) return;
    this.setShopId(imageErrorLogDTO.getShopId());
    this.setTime(imageErrorLogDTO.getTime());
    this.setCode(imageErrorLogDTO.getCode());
    this.setMessage(imageErrorLogDTO.getMessage());
    this.setUrl(imageErrorLogDTO.getUrl());
    this.setContent(imageErrorLogDTO.getContent());
  }
}
