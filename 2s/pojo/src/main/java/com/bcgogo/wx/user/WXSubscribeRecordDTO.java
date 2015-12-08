package com.bcgogo.wx.user;

import com.bcgogo.enums.DeletedType;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-21
 * Time: 下午8:23
 */
public class WXSubscribeRecordDTO {
  private Long id;
  private String publicNo;
  private String openId;
  private Long shopId;
  private Long subscribeTime;
  private WXSubscribeScene scene  ;
  private DeletedType deleted;



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

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getSubscribeTime() {
    return subscribeTime;
  }

  public void setSubscribeTime(Long subscribeTime) {
    this.subscribeTime = subscribeTime;
  }

  public WXSubscribeScene getScene() {
    return scene;
  }

  public void setScene(WXSubscribeScene scene) {
    this.scene = scene;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
