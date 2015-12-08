package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "product-attached")
@XmlAccessorType(XmlAccessType.NONE)
public class ProductAttachedRequest {
  @XmlElement(name = "productId")
  private Long productId;
  @XmlElement(name = "desc")
  private String desc;
  @XmlElement(name = "term")
  private String term;
  @XmlElement(name = "shopId")
  private Long shopId;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
  }
}
