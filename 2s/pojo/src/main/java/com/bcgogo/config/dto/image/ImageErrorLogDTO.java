package com.bcgogo.config.dto.image;

import com.bcgogo.enums.common.ObjectStatus;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-29
 * Time: 上午11:11
 * To change this template use File | Settings | File Templates.
 */
public class ImageErrorLogDTO implements Serializable {
  private Long id;
  private Long shopId;
  private String code;
  private String message;
  private String url;
  private String content;
  private Long time;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }
}
