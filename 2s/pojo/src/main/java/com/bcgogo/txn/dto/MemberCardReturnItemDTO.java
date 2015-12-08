package com.bcgogo.txn.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardReturnItemDTO extends BcgogoOrderItemDto{
  private Long shopId;
  private Long memberCardReturnId;
  private Long cardId;
  private Long salesId;
  private String salesMan;
  private Double lastRecharge;
  private Double memberBalance;

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

  public Long getMemberCardReturnId() {
    return memberCardReturnId;
  }

  public void setMemberCardReturnId(Long memberCardReturnId) {
    this.memberCardReturnId = memberCardReturnId;
  }

  public Long getCardId() {
    return cardId;
  }

  public void setCardId(Long cardId) {
    this.cardId = cardId;
  }

  public Long getSalesId() {
    return salesId;
  }

  public void setSalesId(Long salesId) {
    this.salesId = salesId;
  }

  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  public Double getLastRecharge() {
    return lastRecharge;
  }

  public void setLastRecharge(Double lastRecharge) {
    this.lastRecharge = lastRecharge;
  }

  public Double getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }
}
