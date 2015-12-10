package com.tonggou.andclient.network.parser;


/**
 * JSON 解析处理接口
 * @author lwz
 *
 * @param <T>
 */
public interface IJSONParseHandler<T> {
	
	/**
	 * 解析成功
	 * <p> 当 status = SUCCESS 时，调用次方法
	 * @param result
	 * @param originResultStr 服务器返回的原始结果
	 */
	public void onParseSuccess(T result, byte[] originResult);
	
	/**
	 * 解析失败
	 * <p> 当 status = FAIL 时，调用此方法
	 * @param errorCode	出错原因
	 * @param errorMsg	出错描述
	 */
	public void onParseFailure(String errorCode, String errorMsg);
	
	/**
	 * 解析异常
	 * @param e
	 */
	public void onParseException(Exception e);
	
}
