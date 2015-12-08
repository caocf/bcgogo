package com.bcgogo.txn.dto.finance;

import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.BcgogoProductPropertyDTO;

/**
 * User: ZhangJuntao
 * Date: 13-3-20
 * Time: 上午10:31
 * 订单
 */
public class BcgogoReceivableOrderItemDTO {
  private Long id;
  private String idStr;
  private Long orderId;
  private Double total;
  private Double amount;
  private Double price;
  private Long productId;
  private String productIdStr;
  private Long productPropertyId;
  private String productName;
  private String productText;
  private String productKind;
  private String productType;
  private String memo;
  private String imagePath;
  private String imageUrl;
  private String unit;
  private Boolean canShow;

  public Boolean getCanShow() {
    return canShow;
  }

  public void setCanShow(Boolean canShow) {
    this.canShow = canShow;
  }

  public String getProductIdStr() {
    return productIdStr;
  }

  public void setProductIdStr(String productIdStr) {
    this.productIdStr = productIdStr;
  }

  public Long getProductPropertyId() {
    return productPropertyId;
  }

  public void setProductPropertyId(Long productPropertyId) {
    this.productPropertyId = productPropertyId;
  }

  public String getProductText() {
    return productText;
  }

  public void setProductText(String productText) {
    this.productText = productText;
  }

  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null) idStr = id.toString();
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
    if(productId!=null) productIdStr = productId.toString();
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void setBcgogoProductDTO(BcgogoProductDTO bcgogoProductDTO) {
    if(bcgogoProductDTO!=null){
      this.setProductText(bcgogoProductDTO.getText());
      this.setProductName(bcgogoProductDTO.getName());
      this.setUnit(bcgogoProductDTO.getUnit());
    }
  }

  public void setBcgogoProductPropertyDTO(BcgogoProductPropertyDTO bcgogoProductPropertyDTO) {
    if(bcgogoProductPropertyDTO!=null){
      this.setProductType(bcgogoProductPropertyDTO.getType());
      this.setProductKind(bcgogoProductPropertyDTO.getKind());
      this.setPrice(bcgogoProductPropertyDTO.getPrice());
      this.setImagePath(bcgogoProductPropertyDTO.getImagePath());
    }

  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
