package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ShopRegisterProductDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-9
 * Time: 上午9:47
 * 各个店铺所关心的商品每周销量、入库量统计 作为供求中心首页显示
 */
@Entity
@Table(name = "shop_register_product")
public class ShopRegisterProduct extends LongIdentifier {

  private Long shopId;
  private Long productLocalInfoId;//product_local_info表的id
  private String commodityCode;
  private String name;//
  private String brand;//品牌
  private String model; //型号
  private String spec; //规格
  private String productVehicleModel; //车型
  private String productVehicleBrand; //车辆品牌
  private Long productId;//product表的id
  private String unit;//注册时填写的单位

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  @Column(name = "commodity_code")
  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "spec")
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
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

  public ShopRegisterProduct fromDTO(ShopRegisterProductDTO shopRegisterProductDTO) {
    this.setId(shopRegisterProductDTO.getId());
    this.setShopId(shopRegisterProductDTO.getShopId());
    this.setProductLocalInfoId(shopRegisterProductDTO.getProductLocalInfoId());
    this.setName(shopRegisterProductDTO.getName());
    this.setCommodityCode(shopRegisterProductDTO.getCommodityCode());
    this.setBrand(shopRegisterProductDTO.getBrand());
    this.setModel(shopRegisterProductDTO.getModel());
    this.setSpec(shopRegisterProductDTO.getSpec());
    this.setProductVehicleBrand(shopRegisterProductDTO.getProductVehicleBrand());
    this.setProductVehicleModel(shopRegisterProductDTO.getProductVehicleModel());
    this.setProductId(shopRegisterProductDTO.getProductId());
    this.setUnit(shopRegisterProductDTO.getUnit());
    return this;
  }

  public ShopRegisterProductDTO toDTO() {
    ShopRegisterProductDTO shopRegisterProductDTO = new ShopRegisterProductDTO();
    shopRegisterProductDTO.setId(this.getId());
    shopRegisterProductDTO.setShopId(this.getShopId());
    shopRegisterProductDTO.setProductLocalInfoId(this.getProductLocalInfoId());
    shopRegisterProductDTO.setName(this.getName());
    shopRegisterProductDTO.setCommodityCode(this.getCommodityCode());
    shopRegisterProductDTO.setBrand(this.getBrand());
    shopRegisterProductDTO.setModel(this.getModel());
    shopRegisterProductDTO.setSpec(this.getSpec());
    shopRegisterProductDTO.setProductVehicleBrand(this.getProductVehicleBrand());
    shopRegisterProductDTO.setProductVehicleModel(this.getProductVehicleModel());
    shopRegisterProductDTO.setProductId(this.getProductId());
    shopRegisterProductDTO.setUnit(this.getUnit());
    return shopRegisterProductDTO;
  }
}