package com.bcgogo.product.dto;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-9
 * Time: 上午9:47
 * 各个店铺所关心的商品每周销量、入库量统计 作为供求中心首页显示
 */
public class ShopRegisterProductDTO {
  private Long id;
  private Long shopId;
  private Long productLocalInfoId;//product_local_info_id
  private String commodityCode;
  private String name;//
  private String brand;//
  private String model; //型号
  private String spec; //规格
  private String productVehicleModel; //车型
  private String productVehicleBrand; //车辆品牌
  private Long productId;//product表的id
  private String unit;//注册时填写的单位

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

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

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public ShopRegisterProductDTO fromProductDTO(ProductDTO productDTO) {
    this.setName(productDTO.getName());
    this.setBrand(productDTO.getBrand());
    this.setCommodityCode(productDTO.getCommodityCode());
    this.setModel(productDTO.getModel());
    this.setSpec(productDTO.getSpec());
    this.setProductVehicleModel(productDTO.getProductVehicleModel());
    this.setProductVehicleBrand(productDTO.getProductVehicleBrand());
    this.setUnit(productDTO.getSellUnit());

    this.setShopId(productDTO.getShopId());
    this.setProductId(productDTO.getId());
    this.setProductLocalInfoId(productDTO.getProductLocalInfoId());
    return this;
  }
}
