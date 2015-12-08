package com.bcgogo.product.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PromotionsRuleDTO;

import javax.persistence.*;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "promotions_rule")
public class PromotionsRule extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private Long promotionsId;
  private PromotionsEnum.PromotionsRuleType promotionsRuleType;
  private int level;
  private Double minAmount;// 达到优惠下限.金额或数量
  private Double discountAmount;//现金减免，则是减免金额，单位元；打折，是折扣值，100算，8折就是80
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

  @Column(name = "promotions_id")
  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }

  @Column(name = "level")
  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  @Column(name = "min_amount")
  public Double getMinAmount() {
    return minAmount;
  }

  public void setMinAmount(Double minAmount) {
    this.minAmount = minAmount;
  }

  @Column(name = "discount_amount")
  public Double getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(Double discountAmount) {
    this.discountAmount = discountAmount;
  }

  @Column(name = "promotions_rule_type")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.PromotionsRuleType getPromotionsRuleType() {
    return promotionsRuleType;
  }

  public void setPromotionsRuleType(PromotionsEnum.PromotionsRuleType promotionsRuleType) {
    this.promotionsRuleType = promotionsRuleType;
  }

  @Column(name="deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public static final Comparator<PromotionsRule> SORT_BY_LEVEL = new Comparator<PromotionsRule>() {
    public int compare(PromotionsRule o1, PromotionsRule o2) {
      return o1.getLevel()-o2.getLevel();
    }
  };

  public PromotionsRuleDTO toDTO(){
    PromotionsRuleDTO promotionsRuleDTO = new PromotionsRuleDTO();
    promotionsRuleDTO.setDiscountAmount(this.getDiscountAmount());
    promotionsRuleDTO.setLevel(this.getLevel());
    promotionsRuleDTO.setMinAmount(this.getMinAmount());
    promotionsRuleDTO.setPromotionsId(this.getPromotionsId());
    promotionsRuleDTO.setId(this.getId());
    promotionsRuleDTO.setPromotionsRuleType(this.getPromotionsRuleType());
    return promotionsRuleDTO;
  }

  public PromotionsRule fromDTO(PromotionsRuleDTO promotionsRuleDTO) {
    this.setDiscountAmount(promotionsRuleDTO.getDiscountAmount());
    this.setLevel(promotionsRuleDTO.getLevel());
    this.setMinAmount(promotionsRuleDTO.getMinAmount());
    this.setPromotionsId(promotionsRuleDTO.getPromotionsId());
    this.setPromotionsRuleType(promotionsRuleDTO.getPromotionsRuleType());
    return this;
  }
}
