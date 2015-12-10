package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.ShopSuggestion;

public class ShopSuggestionResponse extends BaseResponse {

	private static final long serialVersionUID = 6679123096172494007L;
	private List<ShopSuggestion> shopSuggestionList; 
	public List<ShopSuggestion> getShopSuggestionList() {
		return shopSuggestionList;
	}
	public void setShopSuggestionList(List<ShopSuggestion> shopSuggestionList) {
		this.shopSuggestionList = shopSuggestionList;
	}
}
