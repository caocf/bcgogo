package com.bcgogo.config.dto;

import com.bcgogo.enums.config.AttachmentType;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-2
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
public class AttachmentDTO {

  private Long id;
  private String name;
  private byte[] content;
  private AttachmentType type;
  private Long shopId;
  private String memo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public AttachmentType getType() {
    return type;
  }

  public void setType(AttachmentType type) {
    this.type = type;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
