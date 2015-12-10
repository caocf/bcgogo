package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.bean.type.DTCStatus;
import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;

/**
 * 查询故障码
 * @author lwz
 *
 */
public class QueryDTCRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.QEURY_DTC;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	public void setRequestParams(int pageNo, DTCStatus status) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("pageNo", pageNo);
		params.put("pageSize", Constants.APP_CONFIG.QUERY_PAGE_SIZE);
		params.put("status", status.toString());
		super.setRequestParams(params);
	}
	
}
