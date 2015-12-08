package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.user.WXSubscribeRecordDTO;
import com.bcgogo.wx.user.WXSubscribeScene;

import javax.persistence.*;

/**
 * 公共号关注订阅记录
 * Author: ndong
 * Date: 14-10-21
 * Time: 下午8:07
 */
@Entity
@Table(name = "wx_subscribe_record")
public class WXSubscribeRecord extends LongIdentifier {
  private String publicNo;
  private String openId;
  private Long shopId;
  private Long subscribeTime;
  private WXSubscribeScene scene  ;
  private DeletedType deleted=DeletedType.FALSE;

  public void fromDTO(WXSubscribeRecordDTO recordDTO){
    this.setId(recordDTO.getId());
    this.setPublicNo(recordDTO.getPublicNo());
    this.setOpenId(recordDTO.getOpenId());
    this.setShopId(recordDTO.getShopId());
    this.setSubscribeTime(recordDTO.getSubscribeTime());
    this.setScene(recordDTO.getScene());
    this.setDeleted(recordDTO.getDeleted());
  }

  @Column(name = "public_no")
  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "subscribe_time")
  public Long getSubscribeTime() {
    return subscribeTime;
  }

  public void setSubscribeTime(Long subscribeTime) {
    this.subscribeTime = subscribeTime;
  }

  @Column(name = "scene")
  @Enumerated(EnumType.STRING)
  public WXSubscribeScene getScene() {
    return scene;
  }

  public void setScene(WXSubscribeScene scene) {
    this.scene = scene;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
