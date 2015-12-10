package com.tonggou.andclient.vo;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import com.google.gson.annotations.SerializedName;
public class Shop{
	private long    id;//店铺ID                   
	private String  name;//店铺名称          
	private String  serviceScope;//;//经营范围       
	private float   distance;//距离（单位：公里） 
	private String  coordinate;//地理坐标（经纬度）
	private float   totalScore;//评分总分               
	private String  bigImageUrl;//图片地址               
	private String  smallImageUrl;//图片地址        
	private Bitmap  samllbtm;//图片地址        
	private Bitmap  bigbtm;//图片地址        
	private String mobile;//电话       
	private String landLine;//电话  2
	public String getLandLine() {
		return landLine;
	}
	public void setLandLine(String landLine) {
		this.landLine = landLine;
	}
	private String address;//地址                 
	private ShopScore shopScore;//  店铺评分详情
	private String cityCode;
	private MemberInfo	memberInfo;//会员信息    本次只需使用小部分信息，设计预留 
	private ArrayList<ShopServiceCategoryDTO> productCategoryList;
	
	public ArrayList<ShopServiceCategoryDTO> getProductCategoryList() {
		return productCategoryList;
	}
	public void setProductCategoryList(ArrayList<ShopServiceCategoryDTO> productCategoryList) {
		this.productCategoryList = productCategoryList;
	}
	public Bitmap getSamllbtm() {
		return samllbtm;
	}
	public void setSamllbtm(Bitmap samllbtm) {
		this.samllbtm = samllbtm;
	}
	public Bitmap getBigbtm() {
		return bigbtm;
	}
	public void setBigbtm(Bitmap bigbtm) {
		this.bigbtm = bigbtm;
	}
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
