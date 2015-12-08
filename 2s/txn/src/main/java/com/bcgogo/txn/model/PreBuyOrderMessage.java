package com.bcgogo.txn.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PreBuyOrderMessageDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "pre_buy_order_message")
public class PreBuyOrderMessage extends LongIdentifier {
  private Long preBuyOrderId;
  private Long shopId;
  private String shopName;
  private ShopKind shopKind;
  private Long shopProvince;     //省
  private Long shopCity;          //市
  private Long shopRegion;        //区域
  private Long editDate;
  private Long vestDate;
  private Long endDate;
  private DeletedType deleted = DeletedType.FALSE;

  @Column(name="pre_buy_order_id")
  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
  }

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="shop_name")
  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }
  @Column(name="shop_kind")
  @Enumerated(EnumType.STRING)
  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  @Column(name="edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name="shop_province")
  public Long getShopProvince() {
    return shopProvince;
  }

  public void setShopProvince(Long shopProvince) {
    this.shopProvince = shopProvince;
  }

  @Column(name="shop_city")
  public Long getShopCity() {
    return shopCity;
  }

  public void setShopCity(Long shopCity) {
    this.shopCity = shopCity;
  }

  @Column(name="shop_region")
  public Long getShopRegion() {
    return shopRegion;
  }

  public void setShopRegion(Long shopRegion) {
    this.shopRegion = shopRegion;
  }
  @Column(name="end_date")
  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  @Column(name="vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }
  @Column(name="deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public void fromDTO(PreBuyOrderMessageDTO preBuyOrderMessageDTO){
    this.setPreBuyOrderId(preBuyOrderMessageDTO.getPreBuyOrderId());
    this.setEditDate(preBuyOrderMessageDTO.getEditDate());
    this.setShopCity(preBuyOrderMessageDTO.getShopCity());
    this.setShopId(preBuyOrderMessageDTO.getShopId());
    this.setShopKind(preBuyOrderMessageDTO.getShopKind());
    this.setShopName(preBuyOrderMessageDTO.getShopName());
    this.setShopProvince(preBuyOrderMessageDTO.getShopProvince());
    this.setShopRegion(preBuyOrderMessageDTO.getShopRegion());
    this.setVestDate(preBuyOrderMessageDTO.getVestDate());
    this.setEndDate(preBuyOrderMessageDTO.getEndDate());
  }

  public PreBuyOrderMessageDTO toDTO(){
    PreBuyOrderMessageDTO preBuyOrderMessageDTO = new PreBuyOrderMessageDTO();
    preBuyOrderMessageDTO.setId(this.getId());
    preBuyOrderMessageDTO.setPreBuyOrderId(this.getPreBuyOrderId());
    preBuyOrderMessageDTO.setEditDate(this.getEditDate());
    preBuyOrderMessageDTO.setShopCity(this.getShopCity());
    preBuyOrderMessageDTO.setShopId(this.getShopId());
    preBuyOrderMessageDTO.setShopKind(this.getShopKind());
    preBuyOrderMessageDTO.setShopName(this.getShopName());
    preBuyOrderMessageDTO.setShopProvince(this.getShopProvince());
    preBuyOrderMessageDTO.setShopRegion(this.getShopRegion());
    preBuyOrderMessageDTO.setVestDate(this.getVestDate());
    preBuyOrderMessageDTO.setEndDate(this.getEndDate());
    return preBuyOrderMessageDTO;
  }

}
