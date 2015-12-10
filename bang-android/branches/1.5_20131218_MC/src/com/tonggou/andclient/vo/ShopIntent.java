package com.tonggou.andclient.vo;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ShopIntent  implements Serializable{
	long    id;//店铺ID                   
	String  name;//店铺名称          
	String  serviceScope;//;//经营范围       
	float   distance;//距离（单位：公里） 
	String  coordinate;//地理坐标（经纬度）
	float   totalScore;//评分总分               
	String  bigImageUrl;//图片地址               
	String  smallImageUrl;//图片地址      
	String mobile;//电话             
	String address;//地址                 
	ShopScore shopScore;//  店铺评分详情
	String   cityCode;
	MemberInfo	memberInfo;//会员信息    本次只需使用小部分信息，设计预留 
	public String getBigImageUrl() {
		return bigImageUrl;
	}
	public void setBigImageUrl(String bigImageUrl) {
		this.bigImageUrl = bigImageUrl;
	}
	public String getSmallImageUrl() {
		return smallImageUrl;
	}
	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;
	}
	public float getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(float totalScore) {
		this.totalScore = totalScore;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public ShopScore getShopScore() {
		return shopScore;
	}
	public void setShopScore(ShopScore shopScore) {
		this.shopScore = shopScore;
	}
	public MemberInfo getMemberInfo() {
		return memberInfo;
	}
	public void setMemberInfo(MemberInfo memberInfo) {
		this.memberInfo = memberInfo;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getServiceScope() {
		return serviceScope;
	}
	public void setServiceScope(String serviceScope) {
		this.serviceScope = serviceScope;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
}