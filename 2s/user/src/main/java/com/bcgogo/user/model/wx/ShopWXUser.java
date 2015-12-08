package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.user.ShopWXUserDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-4
 * Time: 下午4:08
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_wx_user")
public class ShopWXUser extends LongIdentifier{
  private Long shopId;
  private String openId;
  private DeletedType deleted=DeletedType.FALSE;

  public void fromDTO(ShopWXUserDTO dto){
    this.setId(dto.getId());
    this.setShopId(dto.getShopId());
    this.setOpenId(dto.getOpenId());
    this.setDeleted(dto.getDeleted());
  }

  public ShopWXUserDTO toDTO(){
    ShopWXUserDTO dto=new ShopWXUserDTO();
    dto.setId(getId());
    dto.setShopId(this.getShopId());
    dto.setOpenId(this.getOpenId());
    dto.setDeleted(getDeleted());
    return dto;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
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
