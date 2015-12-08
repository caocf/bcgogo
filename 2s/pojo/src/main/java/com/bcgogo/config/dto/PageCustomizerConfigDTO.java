package com.bcgogo.config.dto;

import com.bcgogo.config.PageCustomizerOrderConfigRequest;
import com.bcgogo.config.PageCustomizerProductConfigRequest;
import com.bcgogo.enums.config.PageCustomizerConfigScene;
import com.bcgogo.enums.config.PageCustomizerConfigStatus;

import java.io.Serializable;

/**
 * User: ZhangJuntao
 * Date: 13-5-24
 * Time: 下午3:22
 */
public class PageCustomizerConfigDTO<T> implements Serializable {
  private Long id;
  private String idStr;
  private Long shopId;
  private String shopIdStr;
  private PageCustomizerConfigScene scene;
  private String content;
  private PageCustomizerConfigStatus status;
  private T contentDto;

  public void fromPageCustomizerOrderConfigDTO(PageCustomizerOrderConfigRequest request) {
    this.setId(request.getId());
    this.setShopId(request.getShopId());
    this.setScene(request.getScene());
    this.setStatus(request.getStatus());
  }
  public void fromPageCustomizerOrderConfigDTO(PageCustomizerProductConfigRequest request) {
    this.setId(request.getId());
    this.setShopId(request.getShopId());
    this.setScene(request.getScene());
    this.setStatus(request.getStatus());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if (id != null) {
      idStr = String.valueOf(id);
    }
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    if(shopId!=null){
      shopIdStr = String.valueOf(shopId);
    }
    this.shopId = shopId;
  }

  public PageCustomizerConfigScene getScene() {
    return scene;
  }

  public void setScene(PageCustomizerConfigScene scene) {
    this.scene = scene;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public PageCustomizerConfigStatus getStatus() {
    return status;
  }

  public void setStatus(PageCustomizerConfigStatus status) {
    this.status = status;
  }

  public T getContentDto() {
    return contentDto;
  }

  public void setContentDto(T contentDto) {
    this.contentDto = contentDto;
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }
}
