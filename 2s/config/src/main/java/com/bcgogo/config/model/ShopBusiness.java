package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopBusinessDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-25
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "shop_business")
public class ShopBusiness extends LongIdentifier {
  public ShopBusiness(){
  }

  private Long shopId;
  private Long businessId;
  private Long state;
  private String memo;

  public ShopBusiness(ShopBusinessDTO shopBusinessDTO){
    this.setId(shopBusinessDTO.getId());
    this.setShopId(shopBusinessDTO.getShopId());
    this.setBusinessId(shopBusinessDTO.getBusinessId());
    this.setState(shopBusinessDTO.getState());
    this.setMemo(shopBusinessDTO.getMemo());
  }

  public ShopBusiness fromDTO(ShopBusinessDTO shopBusinessDTO){
    this.setId(shopBusinessDTO.getId());
    this.setShopId(shopBusinessDTO.getShopId());
    this.setBusinessId(shopBusinessDTO.getBusinessId());
    this.setState(shopBusinessDTO.getState());
    this.setMemo(shopBusinessDTO.getMemo());

    return this;
  }

  public ShopBusinessDTO toDTO(){
    ShopBusinessDTO shopBusinessDTO = new ShopBusinessDTO();

    shopBusinessDTO.setId(this.getId());
    shopBusinessDTO.setShopId(this.getShopId());
    shopBusinessDTO.setBusinessId(this.getBusinessId());
    shopBusinessDTO.setState(this.getState());
    shopBusinessDTO.setMemo(this.getMemo());

    return shopBusinessDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "business_id")
  public Long getBusinessId() {
    return businessId;
  }

  public void setBusinessId(Long businessId) {
    this.businessId = businessId;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

}
