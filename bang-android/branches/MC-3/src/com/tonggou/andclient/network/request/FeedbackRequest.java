package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * ·´À¡
 * @author lwz
 *
 */
public class FeedbackRequest extends AbsTonggouHttpRequest {

	private final String USER_NO = "userNo";
	private final String CONTENT = "content";
	private final String MOBILE = "mobile";
	
	@Override
	public String getAPI() {
		return isGuestMode() ? API.GUEST.FEEDBACK : API.FEEDBACK;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	public void setRequestParams(String userNo, String content, String mobile) {
		HttpRequestParams params = new HttpRequestParams();
		params.put(USER_NO, userNo);
		params.put(CONTENT, content);
		params.put(MOBILE, mobile);
		super.setRequestParams(params);
	}

	public void setGuestRequestParams(String content, String mobile) {
		HttpRequestParams params = new HttpRequestParams();
		params.put(CONTENT, content);
		params.put(MOBILE, mobile);
		super.setGuestRequestParams(params);
	}

	
	
}
