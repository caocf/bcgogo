package com.bcgogo.product.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PromotionsRuleMJSDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-6-28
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "promotions_rule_mjs")
public class PromotionsRuleMJS  extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private Long promotionsRuleId;
  private String giftName;
  private PromotionsEnum.GiftType giftType;
  private Double amount;
  private DeletedType deleted = DeletedType.FALSE;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "promotions_rule_id")
  public Long getPromotionsRuleId() {
    return promotionsRuleId;
  }

  public void setPromotionsRuleId(Long promotionsRuleId) {
    this.promotionsRuleId = promotionsRuleId;
  }

  @Column(name = "gift_name")
  public String getGiftName() {
    return giftName;
  }

  public void setGiftName(String giftName) {
    this.giftName = giftName;
  }

  @Column(name = "gift_type")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.GiftType getGiftType() {
    return giftType;
  }

  public void setGiftType(PromotionsEnum.GiftType giftType) {
    this.giftType = giftType;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public PromotionsRuleMJS fromDTO(PromotionsRuleMJSDTO dto){
    this.setShopId(dto.getShopId());
    this.setUserId(dto.getUserId());
    this.setGiftType(dto.getGiftType());
    this.setGiftName(dto.getGiftName());
    this.setAmount(dto.getAmount());
    return this;
  }

  public PromotionsRuleMJSDTO toDTO(){
    PromotionsRuleMJSDTO dto = new PromotionsRuleMJSDTO();
    dto.setGiftType(this.getGiftType());
    dto.setGiftName(this.getGiftName());
    dto.setAmount(this.getAmount());
    return dto;
  }


}
