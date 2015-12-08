package com.bcgogo.product.model;

import com.bcgogo.common.PojoCommon;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "product_barcode")
public class ProductBarcode extends LongIdentifier {
  private String barcode;
  private String name;
  private String brand;
  private String model;
  private String spec;
  private Integer productVehicleStatus;
  private String productVehicleBrand;
  private String productVehicleModel;
  private String productVehicleYear;
  private String productVehicleEngine;
  private Double price;

  public ProductBarcode() {
  }

  public ProductBarcode(String barcode, Product product, Double price) {
    this.barcode = barcode;
    name = product.getName();
    brand = product.getBrand();
    spec = product.getSpec();
    model = product.getModel();
    productVehicleStatus = product.getProductVehicleStatus();
    productVehicleBrand = product.getProductVehicleBrand();
    productVehicleModel = product.getProductVehicleModel();
    productVehicleYear = product.getProductVehicleYear();
    productVehicleEngine = product.getProductVehicleEngine();
    this.price = price;
  }
  
  public String toJsonStr(){
    return "{\"barcode\":\"" + PojoCommon.toJsonStr(this.getBarcode()) + "\"," +
        "\"productName\":\"" + PojoCommon.toJsonStr(this.getName()) + "\"," +
        "\"productBrand\":\"" + PojoCommon.toJsonStr(this.getBrand()) + "\"," +
        "\"productSpec\":\"" + PojoCommon.toJsonStr(this.getSpec()) + "\"," +
        "\"productModel\":\"" + PojoCommon.toJsonStr(this.getModel()) + "\"," +
        "\"productVehicleStatus\":\"" + PojoCommon.toJsonStr(this.getProductVehicleStatus()) + "\"," +
        "\"vehicleBrand\":\"" + PojoCommon.toJsonStr(this.getProductVehicleBrand()) + "\"," +
        "\"vehicleModel\":\"" + PojoCommon.toJsonStr(this.getProductVehicleModel()) + "\"," +
        "\"vehicleYear\":\"" + PojoCommon.toJsonStr(this.getProductVehicleYear()) + "\"," +
        "\"purchasePrice\":\"" + PojoCommon.toJsonStr(this.getPrice()) + "\"," +
        "\"vehicleEngine\":\"" + PojoCommon.toJsonStr(this.getProductVehicleEngine()) + "\"}";
  }

  @Column(name = "barcode", length = 20)
  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  @Column(name = "name", length = 200)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "brand", length = 200)
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "model", length = 200)
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "spec", length = 2000)
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "product_vehicle_status")
  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }

  @Column(name = "product_vehicle_brand", length = 50)
  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  @Column(name = "product_vehicle_model", length = 50)
  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  @Column(name = "product_vehicle_year", length = 10)
  public String getProductVehicleYear() {
    return productVehicleYear;
  }

  public void setProductVehicleYear(String productVehicleYear) {
    this.productVehicleYear = productVehicleYear;
  }

  @Column(name = "product_vehicle_engine", length = 10)
  public String getProductVehicleEngine() {
    return productVehicleEngine;
  }

  public void setProductVehicleEngine(String productVehicleEngine) {
    this.productVehicleEngine = productVehicleEngine;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
}
