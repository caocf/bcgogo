package com.bcgogo.txn.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardOrderItemDTO extends BcgogoOrderItemDto{
  private Long shopId;
  private Long memberCardOrderId;
  private Long cardId;
  private Double percentage;
  private Double percentageAmount;
  private String sales;
  private Double worth;
  private Long salesId;
  private String salesMan;
  private Double price;
  public Long getId() {
    return id;
  }

  public Long getShopId() {
    return shopId;
  }

  public Long getMemberCardOrderId() {
    return memberCardOrderId;
  }

  public Long getCardId() {
    return cardId;
  }

  public Double getPercentage() {
    return percentage;
  }

  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public String getSales() {
    return sales;
  }

  public Double getWorth() {
    return worth;
  }

  public Long getSalesId() {
    return salesId;
  }

  public Double getPrice() {
    return price;
  }

  public void setId(Long id) {
    this.id = id;
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

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public void setSales(String sales) {
    this.sales = sales;
  }

  public void setWorth(Double worth) {
    this.worth = worth;
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

  public void setPrice(Double price) {
    this.price = price;
  }
}
