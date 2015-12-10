package com.tonggou.gsm.andclient.bean;

import java.io.Serializable;

public class ServiceSettleAccounts implements Serializable {
	private static final long serialVersionUID = -6110237023943998231L;
	private float totalAmount; // 单据总额
	private float settledAmount; // 实收
	private float discount; // 优惠
	private float debt; // 挂账

	public float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public float getSettledAmount() {
		return settledAmount;
	}

	public void setSettledAmount(float settledAmount) {
		this.settledAmount = settledAmount;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getDebt() {
		return debt;
	}

	public void setDebt(float debt) {
		this.debt = debt;
	}

}
