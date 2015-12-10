package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.type.FaultCodeStatusType;

public class QueryFaultCodeListRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.QUERY_FAULT_CODE_LIST;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	/**
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param codeStatusTypes
	 */
	public void setRequestParams(int pageNo, int pageSize, FaultCodeStatusType...codeStatusTypes ) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("pageNo", pageNo);
		params.put("pageSize", pageSize);
		params.put("status", getQueryStatusStr(codeStatusTypes));
		super.setRequestParams(params);
	}
	
	/**
	 * 多个查询状态用逗号（','） 隔开
	 * @param codeStatusTypes
	 * @return
	 */
	private String getQueryStatusStr(FaultCodeStatusType...codeStatusTypes) {
		StringBuffer statusStr = new StringBuffer();
		for( FaultCodeStatusType status : codeStatusTypes ) {
			statusStr.append( status.getValue() + ",");
		}
		return statusStr.deleteCharAt(statusStr.length()-1).toString();
	}

}
