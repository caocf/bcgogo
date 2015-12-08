package com.bcgogo.txn.dto;

import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
public class PromotionsRuleDTO{
  private Long id;
  private String idStr;
  private Long promotionsId;
  private String promotionsIdStr;
  private PromotionsEnum.PromotionsRuleType promotionsRuleType;
  private int level;
  private Double minAmount;// 达到优惠下限
  private Double discountAmount;//现金减免，则是减免金额，单位元；打折，是折扣值，100算，8折就是80
  private String minAmountStr;// 达到优惠下限
  private String discountAmountStr;//现金减免，则是减免金额，单位元；打折，是折扣值，100算，8折就是80

  private boolean giveGiftFlag;
  private boolean giveDepositFlag;
  private List<PromotionsRuleMJSDTO> promotionsRuleMJSDTOs;



  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    if(promotionsId!=null) promotionsIdStr = promotionsId.toString();
    this.promotionsId = promotionsId;
  }

  public PromotionsEnum.PromotionsRuleType getPromotionsRuleType() {
    return promotionsRuleType;
  }

  public void setPromotionsRuleType(PromotionsEnum.PromotionsRuleType promotionsRuleType) {
    this.promotionsRuleType = promotionsRuleType;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public Double getMinAmount() {
    return minAmount;
  }

  public void setMinAmount(Double minAmount) {
    if(minAmount!=null){
      this.minAmount = NumberUtil.round(minAmount,NumberUtil.MONEY_PRECISION);
    }else{
      this.minAmount = minAmount;
    }
    setMinAmountStr(StringUtil.subZeroAndDot(String.valueOf(NumberUtil.doubleVal(this.minAmount))));
  }

  public Double getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(Double discountAmount) {
    if(discountAmount!=null){
      this.discountAmount = NumberUtil.round(discountAmount,NumberUtil.MONEY_PRECISION);
    }else{
      this.discountAmount = discountAmount;
    }
    setDiscountAmountStr(StringUtil.subZeroAndDot(String.valueOf(NumberUtil.doubleVal(this.discountAmount))));
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if(id!=null) idStr = id.toString();
    this.id = id;
  }

  //比较器的内部类
  public static final Comparator<PromotionsRuleDTO> SORT_BY_LEVEL = new Comparator<PromotionsRuleDTO>() {
    public int compare(PromotionsRuleDTO o1, PromotionsRuleDTO o2) {
      return o1.getLevel()-o2.getLevel();
    }
  };

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getPromotionsIdStr() {
    return promotionsIdStr;
  }

  public void setPromotionsIdStr(String promotionsIdStr) {
    this.promotionsIdStr = promotionsIdStr;
  }

  public String getMinAmountStr() {
    return minAmountStr;
  }

  public void setMinAmountStr(String minAmountStr) {
    this.minAmountStr = minAmountStr;
  }

  public String getDiscountAmountStr() {
    return discountAmountStr;
  }

  public void setDiscountAmountStr(String discountAmountStr) {
    this.discountAmountStr = discountAmountStr;
  }

  public boolean isGiveGiftFlag() {
    return giveGiftFlag;
  }

  public void setGiveGiftFlag(boolean giveGiftFlag) {
    this.giveGiftFlag = giveGiftFlag;
  }

  public boolean isGiveDepositFlag() {
    return giveDepositFlag;
  }

  public void setGiveDepositFlag(boolean giveDepositFlag) {
    this.giveDepositFlag = giveDepositFlag;
  }

  public List<PromotionsRuleMJSDTO> getPromotionsRuleMJSDTOs() {
    return promotionsRuleMJSDTOs;
  }

  public void setPromotionsRuleMJSDTOs(List<PromotionsRuleMJSDTO> promotionsRuleMJSDTOs) {
    this.promotionsRuleMJSDTOs = promotionsRuleMJSDTOs;
  }


}
