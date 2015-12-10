package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.Pager;
import com.tonggou.andclient.vo.Shop;

public class StoreQueryResponse {
	private List<Shop> shopList;
	private String status; 
	private String msgCode;  
	private String message ;  
	private String data ;
	private Pager  pager;
	public List<Shop> getShopList() {
		return shopList;
	}
	public void setShopList(List<Shop> shopList) {
		this.shopList = shopList;
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
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
}
