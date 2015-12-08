package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-11-27
 * Time: 上午11:43
 * To change this template use File | Settings | File Templates.
 */
public class ExpressDTO implements Serializable {
	private Long id;
	private Long shopId;
	private Long userId;
	private Long orderId;
	private OrderTypes orderType;
	private String waybills;
	private Long lastModified;//发货时间
	private String company;  //物流公司名
	private String memo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public OrderTypes getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderTypes orderType) {
		this.orderType = orderType;
	}

	public String getWaybills() {
		return waybills;
	}

	public void setWaybills(String waybills) {
		this.waybills = waybills;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
