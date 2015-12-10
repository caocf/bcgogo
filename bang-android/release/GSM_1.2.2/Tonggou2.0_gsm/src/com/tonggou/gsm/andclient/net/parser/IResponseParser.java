package com.tonggou.gsm.andclient.net.parser;

/**
 * 服务器响应结果解析器接口
 * @author lwz
 *
 * @param &lt;T&gt; 解析的目标类
 */
public interface IResponseParser<T> {
	
	/**
	 * 解析方法
	 * @param jsonData		 要解析的数据
	 * @return T 解析的目标类
	 */
	public T parse(String jsonData);
	
}
