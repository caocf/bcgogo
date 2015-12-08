package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ProductRemindEventDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-17
 * Time: 下午5:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_remind_event")
public class ProductRemindEvent extends LongIdentifier {
  public ProductRemindEvent() {
  }

  private Long shopId;
  private Long productId;
  private String content;

  public ProductRemindEvent fromDTO(ProductRemindEventDTO productRemindEventDTO) {
    if(productRemindEventDTO == null)
      return this;
    setId(productRemindEventDTO.getId());
    setContent(productRemindEventDTO.getContent());
    setProductId(productRemindEventDTO.getProductId());
    setShopId(productRemindEventDTO.getShopId());
    return this;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
