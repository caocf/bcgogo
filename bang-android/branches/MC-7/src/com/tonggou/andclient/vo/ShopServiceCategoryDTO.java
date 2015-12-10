package com.tonggou.andclient.vo;

import java.io.Serializable;

public class ShopServiceCategoryDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String serviceCategoryId;
	private String serviceCategoryName;
	
	private boolean isSelect = false;
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	
	public String getServiceCategoryId() {
		return serviceCategoryId;
	}
	public void setServiceCategoryId(String serviceCategoryId) {
		this.serviceCategoryId = serviceCategoryId;
	}
	public String getServiceCategoryName() {
		return serviceCategoryName;
	}
	public void setServiceCategoryName(String serviceCategoryName) {
		this.serviceCategoryName = serviceCategoryName;
	}

}
