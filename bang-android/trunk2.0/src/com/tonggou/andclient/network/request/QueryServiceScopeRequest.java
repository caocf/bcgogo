package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.type.ServiceScopeType;

/**
 * ²éÑ¯·þÎñ·¶Î§
 * @author lwz
 *
 */
public class QueryServiceScopeRequest extends AbsTonggouHttpGetRequest {

	private final String PARAM_KEY_SERVICE_SCOPE= "serviceScope";
	
	public void setApiParams(ServiceScopeType type) {
		APIQueryParam parmas = new APIQueryParam(true);
		parmas.put(PARAM_KEY_SERVICE_SCOPE, type.getTypeValue());
		super.setApiParams(parmas);
	}

	@Override
	protected String getOriginApi() {
		return API.SERVICE_CATS_SCOPE;
	}

}
