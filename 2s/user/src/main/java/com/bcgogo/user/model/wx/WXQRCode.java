package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.QRType;
import com.bcgogo.wx.qr.WXQRCodeDTO;

import javax.persistence.*;

/**
 * 分配给店铺的公众号二维码。
 * 使用永久二维码，上限为10w个
 * User: ndong
 * Date: 14-9-1
 * Time: 下午5:55
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_qr_code")
public class WXQRCode extends LongIdentifier {
  private String publicNo;
  private Long shopId;
  private String ticket;
  private String url;
  private Long sceneId;
  private QRType type;
  private QRScene scene;
  //创建时间，临时二维码参照expireSeconds判断二维码是否过期
  private Long createTime;
  private Long expireSeconds;
  private Long expireTime;

  private DeletedType deleted = DeletedType.FALSE;

  public void fromDTO(WXQRCodeDTO codeDTO) {
    if (codeDTO == null) return;
    this.setId(codeDTO.getId());
    this.setPublicNo(codeDTO.getPublicNo());
    this.setShopId(codeDTO.getShopId());
    this.setTicket(codeDTO.getTicket());
    this.setUrl(codeDTO.getUrl());
    this.setSceneId(codeDTO.getSceneId());
    this.setType(codeDTO.getType());
    this.setScene(codeDTO.getScene());
    this.setCreateTime(codeDTO.getCreateTime());
    this.setExpireSeconds(codeDTO.getExpireSeconds());
    this.setExpireTime(codeDTO.getExpireTime());
    this.setDeleted(codeDTO.getDeleted());
  }

  public WXQRCodeDTO toDTO() {
    WXQRCodeDTO qrCodeDTO = new WXQRCodeDTO();
    qrCodeDTO.setId(getId());
    qrCodeDTO.setPublicNo(getPublicNo());
    qrCodeDTO.setSceneId(this.getSceneId());
    qrCodeDTO.setTicket(this.getTicket());
    qrCodeDTO.setShopId(this.getShopId());
    qrCodeDTO.setType(this.getType());
    qrCodeDTO.setUrl(this.getUrl());
    qrCodeDTO.setScene(this.getScene());
    qrCodeDTO.setCreateTime(this.getCreateTime());
    qrCodeDTO.setExpireSeconds(this.getExpireSeconds());
    qrCodeDTO.setExpireTime(this.getExpireTime());
    qrCodeDTO.setDeleted(this.getDeleted());
    return qrCodeDTO;
  }


  @Column(name = "public_no")
  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "ticket")
  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  @Column(name = "create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }


  @Column(name = "expire_seconds")
  public Long getExpireSeconds() {
    return expireSeconds;
  }

  public void setExpireSeconds(Long expireSeconds) {
    this.expireSeconds = expireSeconds;
  }

  @Column(name = "expire_time")
  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public QRType getType() {
    return type;
  }

  public void setType(QRType type) {
    this.type = type;
  }

  @Column(name = "scene")
  @Enumerated(EnumType.STRING)
  public QRScene getScene() {
    return scene;
  }

  public void setScene(QRScene scene) {
    this.scene = scene;
  }

  @Column(name = "scene_id")
  public Long getSceneId() {
    return sceneId;
  }

  public void setSceneId(Long sceneId) {
    this.sceneId = sceneId;
  }

//  @Column(name = "expire_time")
//  public Long getExpireTime() {
//    return expireTime;
//  }
//
//  public void setExpireTime(Long expireTime) {
//    this.expireTime = expireTime;
//  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }


}
