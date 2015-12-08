package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.ProductAchievementHistoryDTO;

import javax.persistence.*;

/**
 * 会员业绩统计-商品员工业绩提成记录表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_achievement_history")
public class ProductAchievementHistory extends LongIdentifier {

  private Long shopId;
  private Long productId;
  private String productName;

  private Long productKindId;
  private String productKindName;

  private AchievementType salesTotalAchievementType;//商品销售额提成方式 按金额 按比率
  private Double salesTotalAchievementAmount;//商品销售额提成数额
  private Long changeTime;//更改时间
  private Long changeUserId;//更改时用户id

  private AchievementType salesProfitAchievementType;      //商品销售利润配置方式 按金额 按比率
  private Double salesProfitAchievementAmount;   //商品销售利润配置金额


  public ProductAchievementHistoryDTO toDTO() {
    ProductAchievementHistoryDTO productAchievementHistoryDTO = new ProductAchievementHistoryDTO();
    productAchievementHistoryDTO.setId(getId());
    productAchievementHistoryDTO.setShopId(getShopId());
    productAchievementHistoryDTO.setProductId(getProductId());
    productAchievementHistoryDTO.setProductName(getProductName());
    productAchievementHistoryDTO.setProductKindId(getProductKindId());
    productAchievementHistoryDTO.setProductKindName(getProductKindName());
    productAchievementHistoryDTO.setAchievementType(getSalesTotalAchievementType());
    productAchievementHistoryDTO.setAchievementAmount(getSalesTotalAchievementAmount());
    productAchievementHistoryDTO.setChangeTime(getChangeTime());
    productAchievementHistoryDTO.setChangeUserId(getChangeUserId());
    productAchievementHistoryDTO.setSalesTotalAchievementType(getSalesTotalAchievementType());
    productAchievementHistoryDTO.setSalesTotalAchievementAmount(getSalesTotalAchievementAmount());
    productAchievementHistoryDTO.setSalesProfitAchievementAmount(getSalesProfitAchievementAmount());
    productAchievementHistoryDTO.setSalesProfitAchievementType(getSalesProfitAchievementType());

    return productAchievementHistoryDTO;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "product_kind_id")
  public Long getProductKindId() {
    return productKindId;
  }

  public void setProductKindId(Long productKindId) {
    this.productKindId = productKindId;
  }

  @Column(name = "product_kind_name")
  public String getProductKindName() {
    return productKindName;
  }

  public void setProductKindName(String productKindName) {
    this.productKindName = productKindName;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }


  @Column(name = "sales_total_achievement_type")
  @Enumerated(EnumType.STRING)
  public AchievementType getSalesTotalAchievementType() {
    return salesTotalAchievementType;
  }

  public void setSalesTotalAchievementType(AchievementType salesTotalAchievementType) {
    this.salesTotalAchievementType = salesTotalAchievementType;
  }

  @Column(name = "sales_total_achievement_amount")
  public Double getSalesTotalAchievementAmount() {
    return salesTotalAchievementAmount;
  }

  public void setSalesTotalAchievementAmount(Double salesTotalAchievementAmount) {
    this.salesTotalAchievementAmount = salesTotalAchievementAmount;
  }

  @Column(name = "change_time")
  public Long getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(Long changeTime) {
    this.changeTime = changeTime;
  }

  @Column(name = "change_user_id")
  public Long getChangeUserId() {
    return changeUserId;
  }

  public void setChangeUserId(Long changeUserId) {
    this.changeUserId = changeUserId;
  }

  @Column(name = "sales_profit_achievement_amount")
  public Double getSalesProfitAchievementAmount() {
    return salesProfitAchievementAmount;
  }

  public void setSalesProfitAchievementAmount(Double salesProfitAchievementAmount) {
    this.salesProfitAchievementAmount = salesProfitAchievementAmount;
  }

  @Column(name = "sales_profit_achievement_type")
  @Enumerated(EnumType.STRING)
  public AchievementType getSalesProfitAchievementType() {
    return salesProfitAchievementType;
  }

  public void setSalesProfitAchievementType(AchievementType salesProfitAchievementType) {
    this.salesProfitAchievementType = salesProfitAchievementType;
  }
}
