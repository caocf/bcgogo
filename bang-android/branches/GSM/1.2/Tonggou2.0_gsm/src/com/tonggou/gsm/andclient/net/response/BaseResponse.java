package com.tonggou.gsm.andclient.net.response;

import java.io.Serializable;

/**
 * 服务器响应 JSON 基础字段
 * @author lwz
 *
 */
public class BaseResponse implements Serializable, IResponse {

	private static final long serialVersionUID = -8136329065223334889L;

	private String status; 		// 状态码  SUCCESS | FAIL
	private int msgCode;  		// 错误码
	private String message ; 	// 描述信息
	
	/**
	 * 获得状态码
	 * @return SUCCESS 成功 | FAIL 失败
	 */
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * 是否解析成功
	 * @return true 成功
	 */
	@Override
	public final boolean isSuccess() {
		return "SUCCESS".equals(getStatus());
	}
	
	/**
	 * 获得错误码
	 * @return
	 */
	@Override
	public int getMsgCode() {
		return msgCode;
	}
	
	public void setMsgCode(int msgCode) {
		this.msgCode = msgCode;
	}
	
	/**
	 * 获得描述信息
	 * @return
	 */
	@Override
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
