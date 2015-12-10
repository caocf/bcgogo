package com.tonggou.andclient.vo;

public class SettleAccount {
 private String totalAmount;
 private String settledAmount;
 private String debt;
 private String discount;
public String getTotalAmount() {
	return totalAmount;
}
public void setTotalAmount(String totalAmount) {
	this.totalAmount = totalAmount;
}
public String getSettledAmount() {
	return settledAmount;
}
public void setSettledAmount(String settledAmount) {
	this.settledAmount = settledAmount;
}
public String getDebt() {
	return debt;
}
public void setDebt(String debt) {
	this.debt = debt;
}
public String getDiscount() {
	return discount;
}
public void setDiscount(String discount) {
	this.discount = discount;
}
}
