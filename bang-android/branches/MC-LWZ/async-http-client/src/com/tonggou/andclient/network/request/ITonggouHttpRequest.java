package com.tonggou.andclient.network.request;

import com.loopj.android.http.RequestParams;

/**
 * 请求接口
 * @author lwz
 *
 */
public interface ITonggouHttpRequest {
	
	/**
	 * 得到 API
	 * @return
	 */
	public String getAPI();
	
	/**
	 * 得到 请求类型
	 * @return
	 */
	public HttpMethod getHttpMethod();
	
	/**
	 * 得到请求参数。允许为 null
	 * @return
	 */
	public RequestParams getRequestParams();
	
	/**
	 * 是否为 游客模式
	 * @return true 游客模式，false 登录会员模式
	 */
	public boolean isGuestMode();
	
}
