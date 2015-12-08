package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.BcgogoProductPropertyDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bcgogo_product_property")
public class BcgogoProductProperty extends LongIdentifier {
  private Long productId;
  private String kind;
  private String type;
  private Double price;
  private String imagePath;
  @Column(name="product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
  @Column(name="kind")
  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }
  @Column(name="type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  @Column(name="price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
  @Column(name="image_path")
  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public BcgogoProductPropertyDTO toDTO(){
    BcgogoProductPropertyDTO bcgogoProductPropertyDTO = new BcgogoProductPropertyDTO();
    bcgogoProductPropertyDTO.setId(this.getId());
    bcgogoProductPropertyDTO.setProductId(this.getProductId());
    bcgogoProductPropertyDTO.setImagePath(this.getImagePath());
    bcgogoProductPropertyDTO.setKind(this.getKind());
    bcgogoProductPropertyDTO.setPrice(this.getPrice());
    bcgogoProductPropertyDTO.setType(this.getType());
    return bcgogoProductPropertyDTO;
  }

  public void fromDTO(BcgogoProductPropertyDTO bcgogoProductPropertyDTO){
    this.setProductId(bcgogoProductPropertyDTO.getProductId());
    this.setImagePath(bcgogoProductPropertyDTO.getImagePath());
    this.setKind(bcgogoProductPropertyDTO.getKind());
    this.setPrice(bcgogoProductPropertyDTO.getPrice());
    this.setType(bcgogoProductPropertyDTO.getType());
  }
}
