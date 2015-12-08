package com.bcgogo.product.model;

import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.NormalProductDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-24
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="normal_product")
public class NormalProduct extends LongIdentifier{
  private Long productCategoryId;
  private String commodityCode;
  private String unit;
  private String brand;
  private String model;
  private String spec;
  private String vehicleModel;
  private String vehicleBrand;
  private String productName;
  private Long vehicleModelId;
  private Long vehicleBrandId;
  private VehicleSelectBrandModel selectBrandModel; //注册时选择车型时 是否是全部车型
  private Double price;

  @Enumerated(EnumType.STRING)
  @Column(name = "select_brand_model")
  public VehicleSelectBrandModel getSelectBrandModel() {
    return selectBrandModel;
  }

  public void setSelectBrandModel(VehicleSelectBrandModel selectBrandModel) {
    this.selectBrandModel = selectBrandModel;
  }



  @Column(name="product_category_id")
  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  @Column(name="commodity_code")
  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  @Column(name="unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name="brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name="model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name="spec")
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name="vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name="vehicle_brand")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name="product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name="vehicle_model_id")
  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  @Column(name="vehicle_brand_id")
  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  @Column(name="price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }


  public NormalProductDTO toDTO(){
    NormalProductDTO normalProductDTO = new NormalProductDTO();
    normalProductDTO.setBrand(this.getBrand());
    normalProductDTO.setCommodityCode(this.getCommodityCode());
    normalProductDTO.setModel(this.getModel());
    normalProductDTO.setId(this.getId());
    normalProductDTO.setProductCategoryId(this.getProductCategoryId());
    normalProductDTO.setSpec(this.getSpec());
    normalProductDTO.setUnit(this.getUnit());
    normalProductDTO.setVehicleModel(this.getVehicleModel());
    normalProductDTO.setVehicleBrand(this.getVehicleBrand());
    normalProductDTO.setProductName(this.getProductName());
    normalProductDTO.setVehicleBrandId(this.getVehicleBrandId());
    normalProductDTO.setVehicleModelId(this.getVehicleModelId());
    normalProductDTO.setPrice(this.getPrice());
    normalProductDTO.setSelectBrandModel(this.getSelectBrandModel());
    return normalProductDTO;
  }

  public NormalProduct(){
  }

  public NormalProduct(NormalProductDTO normalProductDTO)
  {
    if(null == normalProductDTO)
    {
      return;
    }
    this.setId(normalProductDTO.getId());
    this.setBrand(normalProductDTO.getBrand());
    this.setCommodityCode(normalProductDTO.getCommodityCode());
    this.setModel(normalProductDTO.getModel());
    this.setProductCategoryId(normalProductDTO.getProductCategoryId());
    this.setUnit(normalProductDTO.getUnit());
    this.setSpec(normalProductDTO.getSpec());
    this.setVehicleBrand(normalProductDTO.getVehicleBrand());
    this.setVehicleModel(normalProductDTO.getVehicleModel());
    this.setProductName(normalProductDTO.getProductName());
    this.setVehicleBrandId(normalProductDTO.getVehicleBrandId());
    this.setVehicleModelId(normalProductDTO.getVehicleModelId());
    this.setSelectBrandModel(normalProductDTO.getSelectBrandModel());
  }

  public void fromDTO(NormalProductDTO normalProductDTO)
  {
    if(null == normalProductDTO)
    {
      return;
    }
    this.setId(normalProductDTO.getId());
    this.setBrand(normalProductDTO.getBrand());
    this.setCommodityCode(normalProductDTO.getCommodityCode());
    this.setModel(normalProductDTO.getModel());
    this.setProductCategoryId(normalProductDTO.getProductCategoryId());
    this.setUnit(normalProductDTO.getUnit());
    this.setSpec(normalProductDTO.getSpec());
    this.setVehicleBrand(normalProductDTO.getVehicleBrand());
    this.setVehicleModel(normalProductDTO.getVehicleModel());
    this.setProductName(normalProductDTO.getProductName());
    this.setVehicleBrandId(normalProductDTO.getVehicleBrandId());
    this.setVehicleModelId(normalProductDTO.getVehicleModelId());
    this.setSelectBrandModel(normalProductDTO.getSelectBrandModel());
  }

}
