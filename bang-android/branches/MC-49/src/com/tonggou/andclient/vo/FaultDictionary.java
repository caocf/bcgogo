package com.tonggou.andclient.vo;

import android.graphics.Bitmap;
/**
 * ¹ÊÕÏ×Öµä
 * @author think
 *
 */
public class FaultDictionary {
	private String name;	 //System.currentTimeMillis()+"";Ê±¼äÃüÃû
	private String type;
	private int TempOne;
	private int TempTwo;
	private float startRate;
	public float getStartRate() {
		return startRate;
	}
	public void setStartRate(float startRate) {
		this.startRate = startRate;
	}
	private String beizhu;	
	private Bitmap potoBitmap;
	
	public Bitmap getPotoBitmap() {
		return potoBitmap;
	}
	public void setPotoBitmap(Bitmap potoBitmap) {
		this.potoBitmap = potoBitmap;
	}
	public String getBeizhu() {
		return beizhu;
	}
	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTempOne() {
		return TempOne;
	}
	public void setTempOne(int tempOne) {
		TempOne = tempOne;
	}
	public int getTempTwo() {
		return TempTwo;
	}
	public void setTempTwo(int tempTwo) {
		TempTwo = tempTwo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	private String url;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
