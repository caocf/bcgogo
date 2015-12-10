package com.tonggou.gsm.andclient.bean;

import java.util.ArrayList;

/**
 * 服务类型
 * 
 * @author lwz
 * 
 */
public class ServiceCategory {
	public String name;
	public String id;

	ServiceCategory(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public static final ArrayList<ServiceCategory> createServiceType(String[] servicesName, String[] servicesId) {
		final int SIZE = Math.min(servicesName.length, servicesId.length); 
		ArrayList<ServiceCategory> data = new ArrayList<ServiceCategory>();
		for( int i=0; i<SIZE; i++ ) {
			data.add(new ServiceCategory(servicesName[i], servicesId[i]));
		}
		return data;
	}
}
