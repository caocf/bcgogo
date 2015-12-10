package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.Pager;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.ShopSuggestion;

public class ShopSuggestionResponse {

	private List<ShopSuggestion> shopSuggestionList; 
	private String status; 
	private String msgCode;  
	private String message ;          
	private String data ;
	public List<ShopSuggestion> getShopSuggestionList() {
		return shopSuggestionList;
	}
	public void setShopSuggestionList(List<ShopSuggestion> shopSuggestionList) {
		this.shopSuggestionList = shopSuggestionList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}
