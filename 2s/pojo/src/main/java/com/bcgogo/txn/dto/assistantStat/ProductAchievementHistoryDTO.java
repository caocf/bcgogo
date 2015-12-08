package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.assistantStat.AchievementType;

/**
 * 员工业绩统计-商品业绩提成变更记录表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:41
 * To change this template use File | Settings | File Templates.
 */
public class ProductAchievementHistoryDTO extends AssistantAchievementBaseDTO{
  private Long productId;
  private String productName;
  private Long productKindId; //产品分类id
  private String productKindName;//产品分类名称

  private AchievementType salesTotalAchievementType;//商品销售额提成方式 按金额 按比率
  private Double salesTotalAchievementAmount;//商品销售额提成数额
  private AchievementType salesProfitAchievementType;      //商品销售利润配置方式 按金额 按比率
  private Double salesProfitAchievementAmount;   //商品销售利润配置金额


  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Long getProductKindId() {
    return productKindId;
  }

  public void setProductKindId(Long productKindId) {
    this.productKindId = productKindId;
  }

  public String getProductKindName() {
    return productKindName;
  }

  public void setProductKindName(String productKindName) {
    this.productKindName = productKindName;
  }

  public AchievementType getSalesTotalAchievementType() {
    return salesTotalAchievementType;
  }

  public void setSalesTotalAchievementType(AchievementType salesTotalAchievementType) {
    this.salesTotalAchievementType = salesTotalAchievementType;
  }

  public Double getSalesTotalAchievementAmount() {
    return salesTotalAchievementAmount;
  }

  public void setSalesTotalAchievementAmount(Double salesTotalAchievementAmount) {
    this.salesTotalAchievementAmount = salesTotalAchievementAmount;
  }

  public AchievementType getSalesProfitAchievementType() {
    return salesProfitAchievementType;
  }

  public void setSalesProfitAchievementType(AchievementType salesProfitAchievementType) {
    this.salesProfitAchievementType = salesProfitAchievementType;
  }

  public Double getSalesProfitAchievementAmount() {
    return salesProfitAchievementAmount;
  }

  public void setSalesProfitAchievementAmount(Double salesProfitAchievementAmount) {
    this.salesProfitAchievementAmount = salesProfitAchievementAmount;
  }
}
