package com.bcgogo.config.model;

import com.bcgogo.config.dto.PageCustomizerConfigDTO;
import com.bcgogo.enums.config.PageCustomizerConfigScene;
import com.bcgogo.enums.config.PageCustomizerConfigStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-5-24
 * Time: 上午11:30
 * 自定义配置
 */
@Entity
@Table(name = "page_customizer_config")
public class PageCustomizerConfig extends LongIdentifier {
  private Long shopId;
  private PageCustomizerConfigScene scene;
  private PageCustomizerConfigStatus status;
  private String content;

  public PageCustomizerConfig() {
    super();
  }

  public <T> PageCustomizerConfig(PageCustomizerConfigDTO<T> dto, boolean includeId) {
    this.setContent(dto.getContent());
    this.setShopId(dto.getShopId());
    this.setScene(dto.getScene());
    this.setStatus(dto.getStatus());
    if (includeId) this.setId(dto.getId());
  }

  public <T> PageCustomizerConfigDTO<T> toDTO() {
    PageCustomizerConfigDTO<T> dto = new PageCustomizerConfigDTO<T>();
    dto.setId(this.getId());
    dto.setContent(this.getContent());
    dto.setShopId(this.getShopId());
    dto.setScene(this.getScene());
    dto.setStatus(this.getStatus());
    return dto;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "scene")
  @Enumerated(EnumType.STRING)
  public PageCustomizerConfigScene getScene() {
    return scene;
  }

  public void setScene(PageCustomizerConfigScene scene) {
    this.scene = scene;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public PageCustomizerConfigStatus getStatus() {
    return status;
  }

  public void setStatus(PageCustomizerConfigStatus status) {
    this.status = status;
  }

  @Column(name = "content", length = 8000)
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
