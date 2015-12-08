package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.CustomerCardDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-6
 * Time: 下午9:16
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "customer_card")
public class CustomerCard  extends LongIdentifier {
  public CustomerCard() {
  }

  public CustomerCard(CustomerCardDTO customerCardDTO) {
    this.setId(customerCardDTO.getId());
    this.setShopId(customerCardDTO.getShopId());
    this.setCreationDate(customerCardDTO.getCreationDate());
    this.setLastModified(customerCardDTO.getLastModified());
    this.setCardType(customerCardDTO.getCardType());
    this.setCustomerId(customerCardDTO.getCustomerId());
    this.setWashRemain(customerCardDTO.getWashRemain());
    this.setState(customerCardDTO.getState());
  }

  public CustomerCard fromDTO(CustomerCardDTO customerCardDTO) {
    this.setId(customerCardDTO.getId());
    this.setShopId(customerCardDTO.getShopId());
    this.setCreationDate(customerCardDTO.getCreationDate());
    this.setLastModified(customerCardDTO.getLastModified());
    this.setCardType(customerCardDTO.getCardType());
    this.setCustomerId(customerCardDTO.getCustomerId());
    this.setWashRemain(customerCardDTO.getWashRemain());
    this.setState(customerCardDTO.getState());

    return this;
  }

  public CustomerCardDTO toDTO() {
    CustomerCardDTO customerCardDTO = new CustomerCardDTO();

    customerCardDTO.setId(this.getId());
    customerCardDTO.setShopId(this.getShopId());
    customerCardDTO.setCreationDate(this.getCreationDate());
    customerCardDTO.setLastModified(this.getLastModified());
    customerCardDTO.setCardType(this.getCardType());
    customerCardDTO.setCustomerId(this.getCustomerId());
    customerCardDTO.setWashRemain(this.getWashRemain());
    customerCardDTO.setState(this.getState());
    return customerCardDTO;
  }

  private Long shopId;
  private Long customerId;
  private Long cardType;
  private Long washRemain;
  private Long state;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "card_type")
  public Long getCardType() {
    return cardType;
  }

  public void setCardType(Long cardType) {
    this.cardType = cardType;
  }

  @Column(name = "wash_remain")
  public Long getWashRemain() {
    return washRemain;
  }

  public void setWashRemain(Long washRemain) {
    this.washRemain = washRemain;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }
}
