package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ProductAdminDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "product_admin")
public class ProductAdmin extends LongIdentifier {
  private Long shopId;
  private String shopName;
  private Long productId;
  private String name;
  private String brand;
  private String model;
  private String spec;
  private Long supplierId;
  private String supplierName;
  private String carModelName;

  public ProductAdmin() {

  }

  public ProductAdmin(ProductAdminDTO productAdminDTO) {
    this.setShopId(productAdminDTO.getShopId());
    this.setShopName(productAdminDTO.getShopName());
    this.setProductId(productAdminDTO.getProductId());
    this.setName(productAdminDTO.getName());
    this.setBrand(productAdminDTO.getBrand());
    this.setModel(productAdminDTO.getModel());
    this.setSpec(productAdminDTO.getSpec());
    this.setSupplierId(productAdminDTO.getSupplierId());
    this.setCarModelName(productAdminDTO.getCarModelName());
  }

  public ProductAdminDTO toDTO() {
    ProductAdminDTO productAdminDTO = new ProductAdminDTO();

    productAdminDTO.setShopId(this.getShopId());
    productAdminDTO.setShopName(this.getShopName());
    productAdminDTO.setProductId(this.getProductId());
    productAdminDTO.setName(this.getName());
    productAdminDTO.setBrand(this.getBrand());
    productAdminDTO.setModel(this.getModel());
    productAdminDTO.setSpec(this.getSpec());
    productAdminDTO.setSupplierId(this.getSupplierId());
    productAdminDTO.setCarModelName(this.getCarModelName());

    return productAdminDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "shop_name", length = 100)
  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
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

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "supplier_name", length = 20)
  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  @Column(name = "car_model_name", length = 200)
  public String getCarModelName() {
    return carModelName;
  }

  public void setCarModelName(String carModelName) {
    this.carModelName = carModelName;
  }
}
