package com.tonggou.andclient.vo;

import java.io.Serializable;

import android.text.TextUtils;

public class JuheTransgress implements Serializable {

	private static final long serialVersionUID = 7849304550314039941L;

	private String date;		// Î¥ÕÂÈÕÆÚ
	private String area;		// Î¥ÕÂµØµã
	private String act;			// Î¥ÕÂÔ­Òò
	private String code;		// Î¥ÕÂ±àºÅ
	private String fen;			// Î¥ÕÂ¿Û·Ö
	private String money;		// Î¥ÕÂ·£¿î
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public String getCode() {
		code = code.trim();
		if( TextUtils.isEmpty(code) || "-".equals(code) ) {
			code = "--";
		}
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFen() {
		fen = fen.trim();
		if( TextUtils.isEmpty(fen) || "-".equals(fen) ) {
			fen = "0";
		}
		return fen;
	}
	public void setFen(String fen) {
		this.fen = fen;
	}
	public String getMoney() {
		money = money.trim();
		if( TextUtils.isEmpty(money) || "-".equals(money)) {
			money = "0";
		}
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
}
