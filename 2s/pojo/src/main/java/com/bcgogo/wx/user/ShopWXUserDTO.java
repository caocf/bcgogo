package com.bcgogo.wx.user;

import com.bcgogo.enums.DeletedType;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-4
 * Time: 下午5:16
 * To change this template use File | Settings | File Templates.
 */
public class ShopWXUserDTO {
  private Long id;
  private Long shopId;
  private String openId;
  private String publicNo;
  private DeletedType deleted;

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

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
