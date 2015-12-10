package com.tonggou.andclient.vo;

import java.util.List;

public class ServerDetail {
	private String id;						//：单据ID                   long
	private String receiptNo; 				//：单据号            long
	private String status;				//：状态                 String
	private String vehicleNo;				//：车牌号            String
	private String vehicleContact;		//:车辆联系人    String
	public String getShopImageUrl() {
		return shopImageUrl;
	}
	public void setShopImageUrl(String shopImageUrl) {
		this.shopImageUrl = shopImageUrl;
	}
	public String getVehicleBrandModelStr() {
		return vehicleBrandModelStr;
	}
	public void setVehicleBrandModelStr(String vehicleBrandModelStr) {
		this.vehicleBrandModelStr = vehicleBrandModelStr;
	}
	private String vehicleMobile;		//:车辆联系方式   String
	private String customerName;		//：客户名         String
	private String serviceType;			//:服务类型    洗车、保养、保险、验车、维修 String 
	private String shopId;				//：店面ID                  long
	private String shopName;				//：店面名称              String
	private double shopTotalScore;			//：店面总评分      double
	private String orderType;			//：单据类型          String 预约单
	private String orderId;
	private String remark;			//:备注（预约单的备注）
	private long   orderTime;				//：单据时间（预约时间） long
	private String actionType;				//：操作类型         String
	private String shopImageUrl;
	private SettleAccount settleAccounts;          //价格部分
	private List<OrderItem> orderItems;
	private String vehicleBrandModelStr;
	private String content;
	private Comment  comment; 
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public SettleAccount getSettleAccounts() {
		return settleAccounts;
	}
	public void setSettleAccounts(SettleAccount settleAccounts) {
		this.settleAccounts = settleAccounts;
	}
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
	public String getVehicleContact() {
		return vehicleContact;
	}
	public void setVehicleContact(String vehicleContact) {
		this.vehicleContact = vehicleContact;
	}
	public String getVehicleMobile() {
		return vehicleMobile;
	}
	public void setVehicleMobile(String vehicleMobile) {
		this.vehicleMobile = vehicleMobile;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
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
	public double getShopTotalScore() {
		return shopTotalScore;
	}
	public void setShopTotalScore(double shopTotalScore) {
		this.shopTotalScore = shopTotalScore;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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

}
