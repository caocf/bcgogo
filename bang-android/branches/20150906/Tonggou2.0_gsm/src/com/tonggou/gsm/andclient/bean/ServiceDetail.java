package com.tonggou.gsm.andclient.bean;

import java.util.ArrayList;

public class ServiceDetail {
	private String id; // 单据 id
	private String receiptNo; // 单据号
	private String status; // 状态
	private String vehicleNo; // 车牌号
	private String customerName; // 客户名
	private String shopId; // 店铺 id
	private String shopName;// 店铺名称
	private float shopTotalScore; // 店铺总得分
	private String serviceType; // 服务类型
	private String orderType; // 单据类型
	private String vehicleBrandModelStr; // 车型
	private String vehicleMobile; // 手机号
	private ArrayList<ServiceItem> orderItems;
	private long orderTime; // 单据时间
	private String actionType; // 操作类型
	private ServiceSettleAccounts settleAccounts; // 结算信息
	private Object comment; // 评价信息

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public float getShopTotalScore() {
		return shopTotalScore;
	}

	public void setShopTotalScore(float shopTotalScore) {
		this.shopTotalScore = shopTotalScore;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getVehicleBrandModelStr() {
		return vehicleBrandModelStr;
	}

	public void setVehicleBrandModelStr(String vehicleBrandModelStr) {
		this.vehicleBrandModelStr = vehicleBrandModelStr;
	}

	public ArrayList<ServiceItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(ArrayList<ServiceItem> orderItems) {
		this.orderItems = orderItems;
	}

	public long getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(long orderTime) {
		this.orderTime = orderTime;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public ServiceSettleAccounts getSettleAccounts() {
		return settleAccounts;
	}

	public void setSettleAccounts(ServiceSettleAccounts settleAccounts) {
		this.settleAccounts = settleAccounts;
	}

	public Object getComment() {
		return comment;
	}

	public void setComment(Object comment) {
		this.comment = comment;
	}

	public String getVehicleMobile() {
		return vehicleMobile;
	}

	public void setVehicleMobile(String vehicleMobile) {
		this.vehicleMobile = vehicleMobile;
	}

}
