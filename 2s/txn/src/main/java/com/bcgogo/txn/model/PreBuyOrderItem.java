package com.bcgogo.txn.model;

import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.AllocateRecordItemDTO;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "pre_buy_order_item")
public class PreBuyOrderItem extends LongIdentifier {
  private Long shopId;
  private Long preBuyOrderId;
  private Long productId;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private String productVehicleModel;
  private String productVehicleBrand;
  private String commodityCode;//商品编码
  private ShopKind shopKind;
  private Double amount;
  private String unit;
  private Integer quotedCount;
  private String memo;

  public PreBuyOrderItem(){
  }

  public PreBuyOrderItem(PreBuyOrderItemDTO itemDTO){
    this.fromDTO(itemDTO);
  }

   @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_kind")
  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "pre_buy_order_id")
  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
  }
  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "product_brand")
  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  @Column(name = "product_spec")
  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  @Column(name = "product_model")
  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  @Column(name = "product_vehicle_model")
  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  @Column(name = "product_vehicle_brand")
  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  @Column(name = "commodity_code")
  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }


  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name="quoted_count")
  public Integer getQuotedCount() {
    return quotedCount;
  }

  public void setQuotedCount(Integer quotedCount) {
    this.quotedCount = quotedCount;
  }
  public PreBuyOrderItemDTO toDTO(){
    PreBuyOrderItemDTO preBuyOrderItemDTO = new PreBuyOrderItemDTO();
    preBuyOrderItemDTO.setShopId(this.getShopId());
    preBuyOrderItemDTO.setProductId(this.getProductId());
    preBuyOrderItemDTO.setProductName(this.getProductName());
    preBuyOrderItemDTO.setBrand(this.getProductBrand());
    preBuyOrderItemDTO.setModel(this.getProductModel());
    preBuyOrderItemDTO.setSpec(this.getProductSpec());
    preBuyOrderItemDTO.setVehicleBrand(this.getProductVehicleBrand());
    preBuyOrderItemDTO.setVehicleModel(this.getProductVehicleModel());
    preBuyOrderItemDTO.setCommodityCode(this.getCommodityCode());
    preBuyOrderItemDTO.setPreBuyOrderId(this.getPreBuyOrderId());
    preBuyOrderItemDTO.setAmount(this.getAmount());
    preBuyOrderItemDTO.setId(this.getId());
    preBuyOrderItemDTO.setUnit(this.getUnit());
    preBuyOrderItemDTO.setQuotedCount(this.getQuotedCount());
    preBuyOrderItemDTO.setMemo(this.getMemo());
    return preBuyOrderItemDTO;
  }

  public PreBuyOrderItem fromDTO(PreBuyOrderItemDTO preBuyOrderItemDTO){
    this.setCommodityCode(preBuyOrderItemDTO.getCommodityCode());
    this.setProductId(preBuyOrderItemDTO.getProductId());
    this.setProductName(preBuyOrderItemDTO.getProductName());
    this.setProductBrand(preBuyOrderItemDTO.getBrand());
    this.setProductModel(preBuyOrderItemDTO.getModel());
    this.setProductSpec(preBuyOrderItemDTO.getSpec());
    this.setProductVehicleBrand(preBuyOrderItemDTO.getVehicleBrand());
    this.setProductVehicleModel(preBuyOrderItemDTO.getVehicleModel());
    this.setPreBuyOrderId(preBuyOrderItemDTO.getPreBuyOrderId());
    this.setAmount(preBuyOrderItemDTO.getAmount());
    this.setUnit(preBuyOrderItemDTO.getUnit());
    this.setMemo(preBuyOrderItemDTO.getMemo());
    return this;
  }

}
