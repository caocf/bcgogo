package com.bcgogo.api;

import com.bcgogo.enums.shop.ShopKind;

import java.util.Set;

/**
 * Created by XinyuQiu on 14-7-10.
 */
public class AgentNameSuggestion {
  private String queryWord;
  private Set<Long> departmentIds;
  private int start = 0;
  private int limit = 10;
  private Long shopId;


  public String getQueryWord() {
    return queryWord;
  }

  public void setQueryWord(String queryWord) {
    this.queryWord = queryWord;
  }

  public Set<Long> getDepartmentIds() {
    return departmentIds;
  }

  public void setDepartmentIds(Set<Long> departmentIds) {
    this.departmentIds = departmentIds;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
