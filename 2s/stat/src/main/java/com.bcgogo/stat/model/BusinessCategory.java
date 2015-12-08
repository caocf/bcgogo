package com.bcgogo.stat.model;

import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.BusinessCategoryDTO;

import javax.persistence.*;

/**
 * 营业外记账类别表
 */
@Entity
@Table(name = "business_category")
public class BusinessCategory extends LongIdentifier {

  //店面Id
  private Long shopId;

  //类别项目名
  private String itemName;

  //类别项目的类别
  private String itemType;

  private Long useTime;//使用次数

  private MoneyCategory moneyCategory;

  @Column(name = "item_name")
  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  @Column(name = "item_type")
  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "use_time")
  public Long getUseTime() {
    return useTime == null ? 0L : useTime;
  }

  public void setUseTime(Long useTime) {
    this.useTime = useTime;
  }

  @Column(name = "money_category")
  @Enumerated(EnumType.STRING)
  public MoneyCategory getMoneyCategory() {
    return moneyCategory;
  }

  public void setMoneyCategory(MoneyCategory moneyCategory) {
    this.moneyCategory = moneyCategory;
  }

  public BusinessCategoryDTO toDTO() {
    BusinessCategoryDTO businessCategoryDTO = new BusinessCategoryDTO();
    businessCategoryDTO.setId(this.getId());
    businessCategoryDTO.setItemName(this.getItemName());
    businessCategoryDTO.setItemType(this.getItemType());
    businessCategoryDTO.setIdStr(this.getId().toString());
    businessCategoryDTO.setShopId(this.getShopId());
    businessCategoryDTO.setUseTime(this.getUseTime() == null ? 0L : this.getUseTime());
    businessCategoryDTO.setMoneyCategory(this.getMoneyCategory());
    return businessCategoryDTO;
  }

  public BusinessCategory fromDTO(BusinessCategoryDTO businessCategoryDTO) {
    if (businessCategoryDTO != null) {
      this.setItemName(businessCategoryDTO.getItemName());
      this.setItemType(businessCategoryDTO.getItemType());
      this.setShopId(businessCategoryDTO.getShopId());
      this.setUseTime(businessCategoryDTO.getUseTime() == null ? 0L : businessCategoryDTO.getUseTime());
      this.setMoneyCategory(businessCategoryDTO.getMoneyCategory());
    }
    return this;
  }


}
