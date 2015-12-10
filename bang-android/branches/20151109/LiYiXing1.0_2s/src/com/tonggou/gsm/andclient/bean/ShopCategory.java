package com.tonggou.gsm.andclient.bean;

import java.util.ArrayList;

/**
 * 服务类型
 * 
 * @author lwz
 * 
 */
public class ShopCategory {
	public String name;
	public String id;

	ShopCategory(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}

	@Override
	public String toString() {
		return name;
	}

	public static final ArrayList<ShopCategory> createServiceType(String[] servicesName, String[] servicesId) {
		final int SIZE = Math.min(servicesName.length, servicesId.length);
		ArrayList<ShopCategory> data = new ArrayList<ShopCategory>();
		for( int i=0; i<SIZE; i++ ) {
			data.add(new ShopCategory(servicesName[i], servicesId[i]));
		}
		return data;
	}
}