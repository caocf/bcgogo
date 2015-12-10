package com.tonggou.andclient.network.request;

/**
 * Get 请求基类
 * @author lwz
 *
 */
public abstract class AbsTonggouHttpGetRequest extends AbsTonggouHttpRequest {

	private APIQueryParam mParams;
	private APIQueryParam mGuestParams;
	
	
	@Override
	public String getAPI() {
		return HttpRequestClient.getAPIWithQueryParams(getOriginApi(), getAPIQueryParams());
	}
	
	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}
	
	/**
	 * 得到 不含参数的 原始 API 地址
	 * @return
	 */
	protected abstract String getOriginApi(); 
	
	/**
	 * 得到加在 API上的参数
	 * @return
	 */
	protected APIQueryParam getAPIQueryParams() {
		return isGuestMode() ? mGuestParams : mParams;
	}
	
	/**
	 * 设置登录状态的参数
	 * @param params
	 */
	protected void setApiParams(APIQueryParam params) {
		mParams = params;
	}
	
	/**
	 * 设置游客状态的参数
	 * @param params
	 */
	protected void setGuestApiParams(APIQueryParam params) {
		mGuestParams = params;
	}

}
