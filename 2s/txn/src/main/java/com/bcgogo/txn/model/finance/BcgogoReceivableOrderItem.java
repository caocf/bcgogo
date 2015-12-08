package com.bcgogo.txn.model.finance;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.BcgogoReceivableOrderItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午8:58
 * bcgogo收款单 item
 */
@Entity
@Table(name = "bcgogo_receivable_order_item")
public class BcgogoReceivableOrderItem extends LongIdentifier {
  private Long orderId;
  private Double total;
  private Double amount;
  private Double price;
  private Long productId;
  private Long productPropertyId;
  private String productName;
  private String productText;
  private String productKind;
  private String productType;
  private String imagePath;
  private String memo;
  private String unit;
  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }
  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }
  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }
  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
  @Column(name = "product_property_id")
  public Long getProductPropertyId() {
    return productPropertyId;
  }

  public void setProductPropertyId(Long productPropertyId) {
    this.productPropertyId = productPropertyId;
  }
  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }
  @Column(name = "product_kind")
  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }
  @Column(name = "product_type")
  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }
  @Column(name = "product_text")
  public String getProductText() {
    return productText;
  }

  public void setProductText(String productText) {
    this.productText = productText;
  }
  @Column(name = "image_path")
  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public BcgogoReceivableOrderItemDTO toDTO() {
    BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO = new BcgogoReceivableOrderItemDTO();
    bcgogoReceivableOrderItemDTO.setId(this.getId());
    bcgogoReceivableOrderItemDTO.setOrderId(this.getOrderId());
    bcgogoReceivableOrderItemDTO.setTotal(this.getTotal());
    bcgogoReceivableOrderItemDTO.setAmount(this.getAmount());
    bcgogoReceivableOrderItemDTO.setPrice(this.getPrice());
    bcgogoReceivableOrderItemDTO.setMemo(this.getMemo());
    bcgogoReceivableOrderItemDTO.setProductId(this.getProductId());
    bcgogoReceivableOrderItemDTO.setProductName(this.getProductName());
    bcgogoReceivableOrderItemDTO.setProductText(this.getProductText());
    bcgogoReceivableOrderItemDTO.setProductType(this.getProductType());
    bcgogoReceivableOrderItemDTO.setProductKind(this.getProductKind());
    bcgogoReceivableOrderItemDTO.setProductPropertyId(this.getProductPropertyId());
    bcgogoReceivableOrderItemDTO.setUnit(this.getUnit());
    bcgogoReceivableOrderItemDTO.setImagePath(this.getImagePath());
    return bcgogoReceivableOrderItemDTO;
  }

  public void fromDTO(BcgogoReceivableOrderItemDTO orderItemDTO) {
    this.setOrderId(orderItemDTO.getOrderId());
    this.setAmount(orderItemDTO.getAmount());
    this.setProductId(orderItemDTO.getProductId());
    this.setProductPropertyId(orderItemDTO.getProductPropertyId());

    this.setTotal(orderItemDTO.getTotal());
    this.setPrice(orderItemDTO.getPrice());
    this.setProductName(orderItemDTO.getProductName());
    this.setProductKind(orderItemDTO.getProductKind());
    this.setProductText(orderItemDTO.getProductText());
    this.setProductType(orderItemDTO.getProductType());
    this.setUnit(orderItemDTO.getUnit());
    this.setMemo(orderItemDTO.getMemo());
    this.setImagePath(orderItemDTO.getImagePath());
  }
}
