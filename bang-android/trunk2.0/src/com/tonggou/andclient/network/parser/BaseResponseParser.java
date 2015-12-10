package com.tonggou.andclient.network.parser;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.BaseResponse;

/**
 * 基础解析类
 * @author lwz
 *
 * @param &lt;T extends BaseResponse&gt; 解析的目标类
 */
public class BaseResponseParser<T extends BaseResponse> implements IResponseParser<T>{

	private Class<T> classOfT;
	
	/**
	 * 
	 * @param classOfT 泛型的类
	 */
	public BaseResponseParser(Class<T> classOfT) {
		this.classOfT = classOfT;
	}
	
	/**
	 * 解析内部用 Gson 实现
	 */
	@Override
	public T parse(String jsonData) {
		if( TextUtils.isEmpty(jsonData) ) {
			return null;
		}
		return new Gson().fromJson(jsonData, classOfT);
	}

	
}
