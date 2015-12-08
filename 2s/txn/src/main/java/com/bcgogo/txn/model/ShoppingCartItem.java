package com.bcgogo.txn.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ShoppingCartDTO;
import com.bcgogo.txn.dto.ShoppingCartItemDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shopping_cart_item")
public class ShoppingCartItem extends LongIdentifier {
  private Long shopId;
  private Double amount;
  private Long supplierId;
  private Long supplierShopId;
  private Long productLocalInfoId;
  private Long userId;
  private Long editDate;

  @Column(name = "supplier_shop_id")
  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public ShoppingCartItemDTO toDTO(){
    ShoppingCartItemDTO shoppingCartItemDTO = new ShoppingCartItemDTO();
    shoppingCartItemDTO.setId(this.getId());
    shoppingCartItemDTO.setAmount(this.getAmount());
    shoppingCartItemDTO.setEditDate(this.getEditDate());
    shoppingCartItemDTO.setProductLocalInfoId(this.getProductLocalInfoId());
    shoppingCartItemDTO.setShopId(this.getShopId());
    shoppingCartItemDTO.setUserId(this.getUserId());
//    shoppingCartItemDTO.setSupplierId(this.getSupplierId());
    shoppingCartItemDTO.setSupplierShopId(this.getSupplierShopId());
    return shoppingCartItemDTO;
  }

  public void fromDTO(ShoppingCartItemDTO shoppingCartItemDTO){
    this.setAmount(shoppingCartItemDTO.getAmount());
    this.setEditDate(shoppingCartItemDTO.getEditDate());
    this.setProductLocalInfoId(shoppingCartItemDTO.getProductLocalInfoId());
    this.setShopId(shoppingCartItemDTO.getShopId());
//    this.setSupplierId(shoppingCartItemDTO.getSupplierId());
    this.setSupplierShopId(shoppingCartItemDTO.getSupplierShopId());
    this.setUserId(shoppingCartItemDTO.getUserId());
  }

}
