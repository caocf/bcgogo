package com.bcgogo.product.dto;

import com.bcgogo.product.ProductAttachedRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:41
 * To change this template use File | Settings | File Templates.
 */
public class ProductAttachedDTO implements Serializable {
  private Long productId;
  private String desc;
  private String term;
  private Long id;
  private Long shopId;

  public ProductAttachedDTO() {
  }

  public ProductAttachedDTO(ProductAttachedRequest request) {
    setProductId(request.getProductId());
    setDesc(request.getDesc());
    setTerm(request.getTerm());
    setShopId(request.getShopId());
  }

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
