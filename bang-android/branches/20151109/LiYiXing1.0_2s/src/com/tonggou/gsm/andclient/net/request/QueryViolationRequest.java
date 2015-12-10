package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

/**
 * 查询违章信息
 * @author lwz
 *
 */
public class QueryViolationRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_VIOLATION;
	}

	/**
	 * 
	 * @param juheCityCode	聚合城市编号
	 * @param vehicleNo 车牌号
	 * @param engineNo	发动机号
	 * @param vinNo	车架号
	 * @param registerNo	登记证书号
	 */
	public void setApiParams(String juheCityCode, String vehicleNo, String engineNo, String vinNo, String registerNo) {
		APIQueryParam params = new APIQueryParam();
		params.put("city", juheCityCode);
		params.put("hphm", vehicleNo);
		params.put("hpzl", "02");
		params.put("engineno", engineNo);
		params.put("classno", vinNo);
		params.put("registno", registerNo);
		super.setApiParams(params);
	}
	
}
