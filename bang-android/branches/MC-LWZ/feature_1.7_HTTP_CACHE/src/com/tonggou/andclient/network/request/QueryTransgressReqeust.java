package com.tonggou.andclient.network.request;

import android.text.TextUtils;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.JuheTransgressSearchCondition;

/**
 * ²éÑ¯Î¥ÕÂ¼ÇÂ¼ÇëÇó
 * @author lwz
 *
 */
public class QueryTransgressReqeust extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.JUHE.TRANSGRESS_QUERY;
	}

	public void setRequestParams(String city, String hphm, String hpzl, String engineNo, String classno, String registno, JuheTransgressSearchCondition condition) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("city", city);
		params.put("hphm", hphm);
		params.put("hpzl", hpzl);
		
		if( !TextUtils.isEmpty( engineNo ) && engineNo.length() >= condition.getEngineno() ) {
			params.put("engineno", subString( engineNo, condition.getEngineno()));
		}
		if( !TextUtils.isEmpty( classno ) && classno.length() >= condition.getClassno() ) {
			params.put("classno", subString( classno, condition.getClassno()));
		}
		if( !TextUtils.isEmpty( registno ) && registno.length() >= condition.getRegistno() ) {
			params.put("registno", subString( registno, condition.getRegistno()));
		}
		super.setRequestParams(params);
	}
	
	private String subString(String originStr, int backwardsLength) {
		if( backwardsLength <= 0 ) {
			return originStr;
		}
		return originStr.substring( originStr.length() - backwardsLength , originStr.length());
	}

}
