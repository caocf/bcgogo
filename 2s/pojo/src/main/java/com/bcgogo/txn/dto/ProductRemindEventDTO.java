package com.bcgogo.txn.dto;

import com.bcgogo.product.dto.ProductDTO;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-17
 * Time: 下午5:46
 * To change this template use File | Settings | File Templates.
 */
public class ProductRemindEventDTO implements Serializable {

  public ProductRemindEventDTO() {
  }

  private Long id;
  private Long shopId;
  private Long productId;
  private ProductDTO productDTO = new ProductDTO();
  private String content;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }
}
