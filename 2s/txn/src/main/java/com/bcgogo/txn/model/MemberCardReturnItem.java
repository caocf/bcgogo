package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.MemberCardReturnItemDTO;

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
@Table(name = "member_card_return_item")
public class MemberCardReturnItem extends LongIdentifier {
  private Long shopId;
  private Long memberCardReturnId;
  private Long cardId;
  private Double amount;
  private Long salesId;
  private Double lastRecharge;
  private Double memberBalance;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  @Column(name = "card_id")
  public Long getCardId() {
    return cardId;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  @Column(name = "sales_id")
  public Long getSalesId() {
    return salesId;
  }

  @Column(name = "member_card_return_id")
  public Long getMemberCardReturnId() {
    return memberCardReturnId;
  }

  public void setMemberCardReturnId(Long memberCardReturnId) {
    this.memberCardReturnId = memberCardReturnId;
  }

  @Column(name = "last_recharge")
  public Double getLastRecharge() {
    return lastRecharge;
  }

  public void setLastRecharge(Double lastRecharge) {
    this.lastRecharge = lastRecharge;
  }

  @Column(name = "member_balance")
  public Double getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setCardId(Long cardId) {
    this.cardId = cardId;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public void setSalesId(Long salesId) {
    this.salesId = salesId;
  }

  public MemberCardReturnItem() {
  }

  public MemberCardReturnItem(MemberCardReturnItemDTO memberCardReturnItemDTO) {
    if (null != memberCardReturnItemDTO) {
      this.shopId = memberCardReturnItemDTO.getShopId();
      this.memberCardReturnId = memberCardReturnItemDTO.getMemberCardReturnId();
      this.cardId = memberCardReturnItemDTO.getCardId();
      this.amount = memberCardReturnItemDTO.getAmount();
      this.salesId = memberCardReturnItemDTO.getSalesId();
      this.lastRecharge = memberCardReturnItemDTO.getLastRecharge();
      this.memberBalance = memberCardReturnItemDTO.getMemberBalance();
    }
  }

  public MemberCardReturnItemDTO toDTO() {
    MemberCardReturnItemDTO memberCardReturnItemDTO = new MemberCardReturnItemDTO();
    memberCardReturnItemDTO.setShopId(shopId);
    memberCardReturnItemDTO.setMemberCardReturnId(memberCardReturnId);
    memberCardReturnItemDTO.setCardId(cardId);
    memberCardReturnItemDTO.setAmount(amount);
    memberCardReturnItemDTO.setSalesId(salesId);
    memberCardReturnItemDTO.setLastRecharge(lastRecharge);
    memberCardReturnItemDTO.setMemberBalance(memberBalance);
    return memberCardReturnItemDTO;
  }
}

