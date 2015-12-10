package com.tonggou.andclient.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JuheTransgressProvince implements Serializable {

	private static final long serialVersionUID = -6899964585941209882L;

	private String provinceName;					// 省
	private String provinceCode;				// 省编码
	private List<JuheTransgressCity> cities = new ArrayList<JuheTransgressCity>();		// 城市列表
	
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public List<JuheTransgressCity> getCities() {
		return cities;
	}
	public void addCities(JuheTransgressCity city) {
		cities.add(city);
	}
	
	public void parse(JSONObject provinceJsonObjet) throws JSONException {
		setProvinceName(provinceJsonObjet.getString("province"));
		JSONArray cities = provinceJsonObjet.getJSONArray("citys");
		int count = cities.length();
		for( int i=0; i<count; i++ ) {
			JuheTransgressCity city = new JuheTransgressCity();
			city.parse(cities.getJSONObject(i));
			city.setProvinceName(getProvinceName());
			addCities(city);
		}
	}
	
}
