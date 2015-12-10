package com.tonggou.andclient.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tonggou.andclient.vo.GasStation;

public class GasStationParser {
	
	private List<GasStation> stations;
	private PageInfo pageInfo;
	private String message;
	private boolean isSuccess;
	
	public static class PageInfo {
		public int pnums;
		public int current;
	}

	public List<GasStation> getStations() {
		return stations;
	}

	public void setStations(List<GasStation> stations) {
		this.stations = stations;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	public static GasStationParser parse(JSONObject response) throws JSONException {
		GasStationParser parser = new GasStationParser();
		JSONObject rootJsonObj = response;
		parser.setSuccess( rootJsonObj.getInt("resultcode") == 200 );
		parser.setMessage( rootJsonObj.getString("reason") );
		
		if ( parser.isSuccess() ) {
			JSONObject resultJSONObj = rootJsonObj.getJSONObject("result");
			JSONArray dataJsonArr = resultJSONObj.getJSONArray("data");
			JSONObject pageInfoJson = resultJSONObj.getJSONObject("pageinfo");
			parser.setStations( parseStations(dataJsonArr) );
			parser.setPageInfo( parsePageInfo(pageInfoJson) );
		}
		return parser;
	}
	
	private static List<GasStation> parseStations(JSONArray data) throws JSONException {
		final int length = data.length();
		List<GasStation> stations = new ArrayList<GasStation>();
		for (int i = 0; i < length; i++) {
			JSONObject itemJsonObj = data.getJSONObject(i);
			GasStation gasStation = jsonToBean(itemJsonObj);
			stations.add(gasStation);
		}
		return stations;
	}
	
	private static PageInfo parsePageInfo(JSONObject data) throws JSONException {
		PageInfo info = new PageInfo();
		info.pnums = data.getInt("pnums");
		info.current = data.getInt("current");
		return info;
	}

	private static GasStation jsonToBean(JSONObject itemJsonObj) throws JSONException {
		GasStation gasStation = null;
		if (itemJsonObj != null) {
			gasStation = new GasStation();
			gasStation.setId(itemJsonObj.getInt("id"));
			gasStation.setName(itemJsonObj.getString("name"));
			gasStation.setAddress(itemJsonObj.getString("address"));
			gasStation.setType(itemJsonObj.getString("type"));
			gasStation.setDiscount(itemJsonObj.getString("discount"));
			gasStation.setLon(itemJsonObj.getDouble("lon"));
			gasStation.setLat(itemJsonObj.getDouble("lat"));
			gasStation.setDistance(itemJsonObj.getInt("distance"));
			gasStation.setGasPriceInfos(parseGasPriceInfo(itemJsonObj.getJSONObject("gastprice")));
		}
		return gasStation;
	}
	
	private static List<String> parseGasPriceInfo(JSONObject rootObj) throws JSONException {
		List<String> result = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		Iterator<String> it = rootObj.keys();
		while( it.hasNext() ) {
			String key = it.next();
			Object value = rootObj.get(key);
			result.add( key + ":" + value + " " );
		}
		return result;
	}
}
