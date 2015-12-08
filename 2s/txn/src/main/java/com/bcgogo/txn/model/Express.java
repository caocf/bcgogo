package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ExpressDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-11-27
 * Time: 上午11:30
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "express")
public class Express extends LongIdentifier {

	private Long shopId;
	private Long userId;
	private Long orderId;
	private OrderTypes orderType;
  private String waybills;
  private String company;
  private String memo;

	public Express(SalesOrderDTO salesOrderDTO) {
		this.shopId = salesOrderDTO.getShopId();
		this.orderId = salesOrderDTO.getId();
		this.orderType = OrderTypes.SALE;
		this.waybills = salesOrderDTO.getWaybills();
		this.company = salesOrderDTO.getCompany();
		this.memo = salesOrderDTO.getDispatchMemo();
		this.userId = salesOrderDTO.getUserId();
	}

	public Express() {
	}

	@Column(name = "shop_id")
	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "order_type")
	public OrderTypes getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderTypes orderType) {
		this.orderType = orderType;
	}

	@Column(name = "waybills" ,length = 200)
	public String getWaybills() {
		return waybills;
	}

	public void setWaybills(String waybills) {
		this.waybills = waybills;
	}

	@Column(name = "company" ,length = 50)
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Column(name = "memo")
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public ExpressDTO toDTO(){
		ExpressDTO expressDTO = new ExpressDTO();
		expressDTO.setId(getId());
		expressDTO.setLastModified(getLastModified());
		expressDTO.setShopId(getShopId());
		expressDTO.setOrderId(getOrderId());
		expressDTO.setMemo(getMemo());
		expressDTO.setCompany(getCompany());
		expressDTO.setOrderType(getOrderType());
		expressDTO.setWaybills(getWaybills());
		return expressDTO;
	}
}
