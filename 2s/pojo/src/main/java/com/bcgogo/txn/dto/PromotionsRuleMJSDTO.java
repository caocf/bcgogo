package com.bcgogo.txn.dto;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-6-28
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
public class PromotionsRuleMJSDTO {

  private Long shopId;
  private Long userId;
  private Long promotionsRuleId;
  private String giftName;
  private PromotionsEnum.GiftType giftType;
  private Double amount;
  private DeletedType deleted = DeletedType.FALSE;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getPromotionsRuleId() {
    return promotionsRuleId;
  }

  public void setPromotionsRuleId(Long promotionsRuleId) {
    this.promotionsRuleId = promotionsRuleId;
  }

  public String getGiftName() {
    return giftName;
  }

  public void setGiftName(String giftName) {
    this.giftName = giftName;
  }

  public PromotionsEnum.GiftType getGiftType() {
    return giftType;
  }

  public void setGiftType(PromotionsEnum.GiftType giftType) {
    this.giftType = giftType;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
