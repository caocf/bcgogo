package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 通过关键字获取店铺建议列表
 * @author lwz
 *
 */
public class ShopSuggestionByKeywordRequest extends AbsTonggouHttpGetRequest {

	private final String KEY_DATA_KIND = "dataKind";
	private final String KEY_KEYWORDS = "keywords";
	private final String KEY_CITY_CODE = "cityCode";
	private final String KEY_AREA_ID = "areaId";
	private final String KEY_SERVICE_SCOPE_IDS = "serviceScopeIds";
	
	@Override
	public String getOriginApi() {
		return  isGuestMode() ? API.GUEST.SHOP_SUGGESTION_BY_KEYWORD : API.SHOP_SUGGESTION_BY_KEYWORD;
	}

	public void setApiParams(String keywords, String cityCode, String areaId, String serviceScopeIds ) {
		APIQueryParam params = new  APIQueryParam(false);
		params.put(KEY_KEYWORDS, keywords);
		params.put(KEY_CITY_CODE, cityCode);
		params.put(KEY_AREA_ID, areaId);
		params.put(KEY_SERVICE_SCOPE_IDS, serviceScopeIds);
		super.setApiParams(params);
	}
	
	public void setGuestApiParams(String keywords, String cityCode, String areaId, String serviceScopeIds ) {
		APIQueryParam params = new  APIQueryParam(false);
		params.put(KEY_KEYWORDS, keywords);
		params.put(KEY_CITY_CODE, cityCode);
		params.put(KEY_AREA_ID, areaId);
		params.put(KEY_SERVICE_SCOPE_IDS, serviceScopeIds);
		params.put(KEY_DATA_KIND, API.GUEST.CURRENT_DATA_KIND.getValue());
		super.setGuestApiParams(params);
	}
}
