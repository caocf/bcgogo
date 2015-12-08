package com.bcgogo.product.model;

import com.bcgogo.enums.Product.BcgogoProductScene;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.BcgogoProductDTO;

import javax.persistence.*;

@Entity
@Table(name = "bcgogo_product")
public class BcgogoProduct extends LongIdentifier {
  private String name;  // 品名
  private String text;  // 显示用的
  private String unit;
  private String description; // 商品描述
  private String showToShopVersions;
  private PaymentType paymentType;
  private BcgogoProductScene productScene;
  private String imagePath;

  @Column(name = "payment_type")
  @Enumerated(EnumType.STRING)
  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  @Column(name="unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name="name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name="description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  @Column(name="text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Column(name="image_path")
  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  @Column(name="show_to_shop_versions")
  public String getShowToShopVersions() {
    return showToShopVersions;
  }

  public void setShowToShopVersions(String showToShopVersions) {
    this.showToShopVersions = showToShopVersions;
  }

  @Column(name = "product_scene")
  @Enumerated(EnumType.STRING)
  public BcgogoProductScene getProductScene() {
    return productScene;
  }

  public void setProductScene(BcgogoProductScene productScene) {
    this.productScene = productScene;
  }


  public BcgogoProductDTO toDTO() {
    BcgogoProductDTO bcgogoProductDTO = new BcgogoProductDTO();
    bcgogoProductDTO.setDescription(this.getDescription());
    bcgogoProductDTO.setText(this.getText());
    bcgogoProductDTO.setName(this.getName());
    bcgogoProductDTO.setId(this.getId());
    bcgogoProductDTO.setUnit(this.getUnit());
    bcgogoProductDTO.setShowToShopVersions(this.getShowToShopVersions());
    bcgogoProductDTO.setImagePath(this.getImagePath());
    bcgogoProductDTO.setPaymentType(this.getPaymentType());
    bcgogoProductDTO.setProductScene(this.getProductScene());
    return bcgogoProductDTO;
  }

  public void fromDTO(BcgogoProductDTO bcgogoProductDTO){
    this.setDescription(bcgogoProductDTO.getDescription());
    this.setText(bcgogoProductDTO.getText());
    this.setName(bcgogoProductDTO.getName());
    this.setUnit(bcgogoProductDTO.getUnit());
    this.setShowToShopVersions(bcgogoProductDTO.getShowToShopVersions());
    this.setImagePath(bcgogoProductDTO.getImagePath());
    this.setPaymentType(bcgogoProductDTO.getPaymentType());
    this.setProductScene(bcgogoProductDTO.getProductScene());
  }
}
