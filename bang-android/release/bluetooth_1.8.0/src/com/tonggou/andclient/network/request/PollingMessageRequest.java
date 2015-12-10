package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.type.MessageType;

/**
 * 消息轮询 请求
 * @author lwz
 *
 */
public class PollingMessageRequest extends AbsTonggouHttpGetRequest {

	private final String KEY_USER_NO = "userNo";
	private final String KEY_TYPES = "types";
	
	@Override
	public String getOriginApi() {
		return API.POLLING_MESSAGE;
	}

	/**
	 * 
	 * @param userNo	用户名
	 * @param types		消息类型 {@link MessageType}
	 */
	public void setApiParams(String userNo, MessageType...types ) {
		APIQueryParam params = new APIQueryParam();
		StringBuffer typesStr = new StringBuffer();
		for( MessageType type : types ) {
			typesStr.append("," + type.getValue());
		}
		typesStr.deleteCharAt(0);
		params.put(KEY_TYPES, typesStr.toString());
		params.put(KEY_USER_NO, userNo);
		super.setApiParams(params);
	}

}
