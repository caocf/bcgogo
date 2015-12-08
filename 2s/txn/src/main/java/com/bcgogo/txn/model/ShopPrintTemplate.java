package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ShopPrintTemplateDTO;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-18
 * Time: 下午2:32
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_print_template")
public class ShopPrintTemplate extends LongIdentifier {

  private Long shopId;
  private Long templateId;
  private String orderType;
  private String displayName;
  private OrderTypes orderTypeEnum;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  @Column(name = "template_id")
  public Long getTemplateId() {
    return templateId;
  }

  @Column(name = "order_type")
  public String getOrderType() {
    return orderType;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  @Column(name="order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name="display_name")
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public ShopPrintTemplateDTO toDTO(){
    ShopPrintTemplateDTO dto = new ShopPrintTemplateDTO();
    dto.setId(getId());
    dto.setShopId(getShopId());
    dto.setTemplateId(getTemplateId());
    dto.setOrderType(getOrderTypeEnum());
    dto.setDisplayName(getDisplayName());
    return dto;
  }

}
