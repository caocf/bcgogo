package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppShopSuggestion;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午4:08
 */
public class ApiShopSuggestionResponse extends ApiResponse {
  private List<AppShopSuggestion> shopSuggestionList = new ArrayList<AppShopSuggestion>();

  public ApiShopSuggestionResponse() {
    super();
  }

  public ApiShopSuggestionResponse(ApiResponse response) {
    super(response);
  }

  public List<AppShopSuggestion> getShopSuggestionList() {
    return shopSuggestionList;
  }

  public void setShopSuggestionList(List<AppShopSuggestion> shopSuggestionList) {
    this.shopSuggestionList = shopSuggestionList;
  }
}
