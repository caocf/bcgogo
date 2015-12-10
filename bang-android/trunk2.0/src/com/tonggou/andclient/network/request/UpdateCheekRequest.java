package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.util.INFO;

/**
 * Éý¼¶¼ì²âÇëÇó
 * @author lwz
 *
 */
public class UpdateCheekRequest extends AbsTonggouHttpGetRequest {

	@Override
	protected String getOriginApi() {
		return API.UPDATE_CHECK;
	}

	@Override
	protected APIQueryParam getAPIQueryParams() {
		APIQueryParam params = new APIQueryParam();
		params.put("platform", INFO.MOBILE_PLATFORM);
		params.put("appVersion", INFO.VERSION);
		params.put("platformVersion", INFO.MOBILE_PLATFORM_VERSION);
		params.put("mobileModel", INFO.MOBILE_MODEL);
		return params;
	}

}
