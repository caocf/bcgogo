package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * µÍ∆ÃœÍ«È«Î«Û
 * @author lwz
 *
 */
public class ShopDetailRequest extends AbsTonggouHttpGetRequest {

	@Override
	protected String getOriginApi() {
		return isGuestMode() ? API.GUEST.SHOP_DETAIL : API.SHOP_DETAIL;
	}
	
	public void setApiParams(String userNo, String shopId) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("shopId", shopId);
		params.put("userNo", "userNo/" + userNo);
		super.setApiParams(params);
	}
	
	public void setGuestApiParams(String shopId) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("shopId", shopId);
		super.setGuestApiParams(params);
	}

	
}
