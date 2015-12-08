package com.bcgogo.txn.dto;

import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-11
 * Time: 下午1:18
 * To change this template use File | Settings | File Templates.
 */
public class OtherIncomeKindDTO{
  private Long id;
  private Long shopId;
  private String kindName;
  private String idStr;
  private String label;
  private Long useTimes;//使用次数

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(null != this.id)
    {
      this.idStr = this.id.toString();
    }
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getKindName() {
    return kindName;
  }

  public void setKindName(String kindName) {
    this.kindName = kindName;
    this.label = this.kindName;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Long getUseTimes() {
    return useTimes;
  }

  public void setUseTimes(Long useTimes) {
    this.useTimes = useTimes;
  }
}
