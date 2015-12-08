package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PurchasePriceDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "purchase_price")
public class PurchasePrice extends LongIdentifier {

  public PurchasePrice fromDTO(PurchasePriceDTO purchasePriceDTO){
    if(purchasePriceDTO == null){
      return this;
    }
    this.setId(purchasePriceDTO.getId());
    this.setDate(purchasePriceDTO.getDate());
    this.setMemo(purchasePriceDTO.getMemo());
    this.setPrice(purchasePriceDTO.getPrice());
    this.setProductId(purchasePriceDTO.getProductId());
    this.setShopId(purchasePriceDTO.getShopId());
    this.setUnit(purchasePriceDTO.getUnit());
    return this;
  }

  public PurchasePriceDTO toDTO() {
    PurchasePriceDTO purchasePriceDTO = new PurchasePriceDTO();
    purchasePriceDTO.setId(getId());
    purchasePriceDTO.setDate(getDate());
    purchasePriceDTO.setMemo(getMemo());
    purchasePriceDTO.setPrice(getPrice());
    purchasePriceDTO.setProductId(getProductId());
    purchasePriceDTO.setShopId(getShopId());
    purchasePriceDTO.setUnit(getUnit());
    return purchasePriceDTO;
  }

  public PurchasePrice(){
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "price")
  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Column(name = "date")
  public Long getDate(){
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "unit", length = 20)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  private Long shopId;
  private Long productId;
  private double price;
  private Long date;
  private String memo;
  private String unit;

}