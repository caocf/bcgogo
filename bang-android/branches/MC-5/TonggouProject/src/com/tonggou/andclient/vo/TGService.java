package com.tonggou.andclient.vo;

import android.graphics.Bitmap;

public class TGService {
	private String shopId ;          //店铺ID long
	private String shopName ;        //店面名称               String
	private String shopImageUrl;      //店面图片地址       String
	private String content;   			//服务内容                String
	private String orderTime ;          //服务时间              long
	private String status ;        	//服务状态                 String
	private String orderId;     		 //单据ID                  long
	private String orderType;   			//单据类型              String
	private Bitmap btm;   			//单据类型              String
	public Bitmap getBtm() {
		return btm;
	}
	public void setBtm(Bitmap btm) {
		this.btm = btm;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getShopImageUrl() {
		return shopImageUrl;
	}
	public void setShopImageUrl(String shopImageUrl) {
		this.shopImageUrl = shopImageUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
}
