package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.NONE)
public class ProductLocalInfoRequest {
  @XmlElement(name = "productId")
  private Long productId;
  @XmlElement(name = "shopId")
  private Long shopId;
  @XmlElement(name = "price")
  private Double price;
  @XmlElement(name = "purchasePrice")
  private Double purchasePrice;

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
}
