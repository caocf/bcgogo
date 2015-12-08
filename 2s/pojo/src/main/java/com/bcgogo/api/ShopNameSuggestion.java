package com.bcgogo.api;

import com.bcgogo.enums.app.OBDSimType;
import com.bcgogo.enums.shop.ShopKind;

import java.util.Set;

/**
 * Created by XinyuQiu on 14-7-10.
 */
public class ShopNameSuggestion {
  private String queryWord;
  private Set<Long> shopVersionIds;
  private Set<ShopKind> shopKinds ;
  private int start = 0;
  private int limit = 10;


  public String getQueryWord() {
    return queryWord;
  }

  public void setQueryWord(String queryWord) {
    this.queryWord = queryWord;
  }

  public Set<Long> getShopVersionIds() {
    return shopVersionIds;
  }

  public void setShopVersionIds(Set<Long> shopVersionIds) {
    this.shopVersionIds = shopVersionIds;
  }

  public Set<ShopKind> getShopKinds() {
    return shopKinds;
  }

  public void setShopKinds(Set<ShopKind> shopKinds) {
    this.shopKinds = shopKinds;
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
}
