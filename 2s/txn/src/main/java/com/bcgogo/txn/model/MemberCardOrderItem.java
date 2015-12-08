package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.MemberCardOrderItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午7:17
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "member_card_order_item")
public class MemberCardOrderItem extends LongIdentifier{
  private Long shopId;
  private Long memberCardOrderId;
  private Long cardId;
  private Double amount;
  private Double percentage;
  private Double percentageAmount;
  private Long salesId;
  private Double worth;
  private Double price;
  private String salesMan;
  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }
  @Column(name="member_card_order_id")
  public Long getMemberCardOrderId() {
    return memberCardOrderId;
  }
  @Column(name="card_id")
  public Long getCardId() {
    return cardId;
  }
  @Column(name="amount")
  public Double getAmount() {
    return amount;
  }
  @Column(name="percentage")
  public Double getPercentage() {
    return percentage;
  }
  @Column(name="percentage_amount")
  public Double getPercentageAmount() {
    return percentageAmount;
  }
  @Column(name="sales_id")
  public Long getSalesId() {
    return salesId;
  }
  @Column(name="worth")
  public Double getWorth() {
    return worth;
  }
  @Column(name="price")
  public Double getPrice() {
    return price;
  }

  @Column(name="sales_man")
  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setMemberCardOrderId(Long memberCardOrderId) {
    this.memberCardOrderId = memberCardOrderId;
  }

  public void setCardId(Long cardId) {
    this.cardId = cardId;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public void setSalesId(Long salesId) {
    this.salesId = salesId;
  }

  public void setWorth(Double worth) {
    this.worth = worth;
  }

  public MemberCardOrderItem()
  {

  }

  public MemberCardOrderItem(MemberCardOrderItemDTO memberCardOrderItemDTO)
  {
    if(null != memberCardOrderItemDTO)
    {
      this.setAmount(memberCardOrderItemDTO.getAmount());
      this.setCardId(memberCardOrderItemDTO.getCardId());
      this.setMemberCardOrderId(memberCardOrderItemDTO.getMemberCardOrderId());
      this.setPercentage(memberCardOrderItemDTO.getPercentage());
      this.setPercentageAmount(memberCardOrderItemDTO.getPercentageAmount());
      this.setSalesId(memberCardOrderItemDTO.getSalesId());
      this.setShopId(memberCardOrderItemDTO.getShopId());
      this.setWorth(memberCardOrderItemDTO.getWorth());
      this.setId(memberCardOrderItemDTO.getId());
      this.setPrice(memberCardOrderItemDTO.getPrice());
      this.setSalesMan(memberCardOrderItemDTO.getSalesMan());
    }
  }

  public MemberCardOrderItemDTO toDTO()
  {
    if(null == this)
    {
      return null;
    }
    MemberCardOrderItemDTO memberCardOrderItemDTO = new MemberCardOrderItemDTO();
    memberCardOrderItemDTO.setId(this.getId());
    memberCardOrderItemDTO.setShopId(this.getShopId());
    memberCardOrderItemDTO.setCardId(this.getCardId());
    memberCardOrderItemDTO.setMemberCardOrderId(this.getMemberCardOrderId());
    memberCardOrderItemDTO.setAmount(this.getAmount());
    memberCardOrderItemDTO.setPercentage(this.getPercentage());
    memberCardOrderItemDTO.setPercentageAmount(this.getPercentageAmount());
    memberCardOrderItemDTO.setSalesId(this.getSalesId());
    memberCardOrderItemDTO.setWorth(this.getWorth());
    memberCardOrderItemDTO.setPrice(this.getPrice());
    memberCardOrderItemDTO.setSalesMan(this.getSalesMan());
    return memberCardOrderItemDTO;
  }
}

