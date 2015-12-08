package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ProductUnitDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "product_unit")
public class ProductUnit extends LongIdentifier {
  private String productName;
  private String unit;
  private Integer count;

  @Column(name="product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }
  @Column(name="count")
  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }
  @Column(name="unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public ProductUnitDTO toDTO(){
    ProductUnitDTO productUnitDTO = new ProductUnitDTO();
    productUnitDTO.setProductName(this.getProductName());
    productUnitDTO.setUnit(this.getUnit());
    productUnitDTO.setCount(this.getCount());
    return productUnitDTO;
  }
}