package com.tonggou.andclient.util;

import java.util.Map;

import com.google.gson.Gson;

public class MapToJson {
	
	public static String mapToJsonStr(Map<String, String> mapTestCase){
		Gson gson = new Gson();
		String result = gson.toJson(mapTestCase);
		if(result==null){
			return "";
		}
		return result;
	}

}
