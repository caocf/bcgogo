package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.type.AreaType;

/**
 * 查询支持的地区列表
 * @author lwz
 *
 */
public class QuerySuportAreaListRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_SUPPORT_AREA_LIST;
	}
	
	/**
	 * 
	 * @param type			查询类型， {@link AreaType}
	 * @param provinceId	当 type 为  {@link AreaType #PROVINCE} 时， 可为 null
	 */
	public void setApiParams(AreaType type, String provinceId ) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("type", String.valueOf(type));
		params.put("provinceId", provinceId == null ? "NULL": provinceId);
		super.setApiParams(params);
	}

}
