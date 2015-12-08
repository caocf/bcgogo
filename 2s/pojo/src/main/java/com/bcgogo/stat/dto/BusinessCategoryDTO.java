package com.bcgogo.stat.dto;

import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-19
 * Time: 下午4:59
 * To change this template use File | Settings | File Templates.
 */
public class BusinessCategoryDTO implements Serializable {

   //类别项目名
  private String itemName;

  //类别项目的类别
  private String itemType;

  private Long id;

  private String idStr;

  private Long shopId;

  private Long useTime;//使用次数

  private MoneyCategory moneyCategory;//该分类是属于收入 还是支出

  private String label;


  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
    this.label = this.itemName;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

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

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getUseTime() {
    return useTime;
  }

  public void setUseTime(Long useTime) {
    this.useTime = useTime;
  }

  public MoneyCategory getMoneyCategory() {
    return moneyCategory;
  }

  public void setMoneyCategory(MoneyCategory moneyCategory) {
    this.moneyCategory = moneyCategory;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
