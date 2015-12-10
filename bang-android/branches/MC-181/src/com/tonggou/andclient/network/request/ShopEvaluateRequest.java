package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * µÍ√Ê∆¿º€«Î«Û
 * @author fbl
 *
 */
public class ShopEvaluateRequest extends AbsTonggouHttpGetRequest {

	/**
	 * @param shopId
	 * @param pageNo
	 * @param pageSize
	 */
	public void setApiParams(String shopId,int pageNo,int pageSize) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("shopId", shopId);
		params.put("pageNo", pageNo);
		params.put("pageSize", pageSize);
		super.setApiParams(params);
	}

	@Override
	protected String getOriginApi() {
		return API.SHOP_EVALUATE;
	}

}
