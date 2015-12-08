package com.bcgogo.wx.qr;

import com.bcgogo.enums.DeletedType;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-3
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
public class WXQRCodeDTO {
  private Long id;
  private String publicNo;
  private Long shopId;
  private String ticket;
  private String url;
  private Long sceneId;
  private QRScene scene;
  private QRType type;
  private Long createTime;
  private Long expireSeconds;
  private Long expireTime;
  private DeletedType deleted=DeletedType.FALSE;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Long getSceneId() {
    return sceneId;
  }

  public void setSceneId(Long sceneId) {
    this.sceneId = sceneId;
  }

  public QRScene getScene() {
    return scene;
  }

  public void setScene(QRScene scene) {
    this.scene = scene;
  }

  public QRType getType() {
    return type;
  }

  public void setType(QRType type) {
    this.type = type;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Long getExpireSeconds() {
    return expireSeconds;
  }

  public void setExpireSeconds(Long expireSeconds) {
    this.expireSeconds = expireSeconds;
  }

  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
