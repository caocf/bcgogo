package com.tonggou.lib.net;

import java.util.LinkedHashMap;
import java.util.Set;

import android.text.TextUtils;

/**
 * API URL 参数
 * <p><b>是有序的</b>，按照插入顺序来排序</p>
 * 直接调用  toString() 方法来得到 url 参数
 * <br> 默认得到的 参数 url 是包含键 如 ../userNo/{userNo} ,其中 userNo 为键，{userNo} 为值
 * <br> 若想不包含键，那么可以使用 带参数的构造方法  {@link #APIQueryParam(boolean isParamsUrlContainKey)}
 * <br>
 * <p>
 * 	内部是使用 LinkedHashMap来实现的
 * </p>
 * @author lwz
 *
 */
public class APIQueryParam extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = -7262235753079488732L;
	
	// url 是否包含键 如 ../userNo/{userNo} ,其中 userNo 为键，{userNo} 为值
	private boolean isParamsUrlContainKey = true;
	
	/**
	 * 直接调用  toString() 方法来得到 url 参数
	 * <br> 默认得到的 参数 url 是包含键 如 ../userNo/{userNo} ,其中 userNo 为键，{userNo} 为值
	 * <br> 若想不包含键，那么可以使用 带参数的构造方法  {@link #APIQueryParam(boolean isParamsUrlContainKey)}
	 */
	public APIQueryParam() {
	}
	
	/**
	 * 直接调用  toString() 方法来得到 url 参数
	 * @param isParamsUrlContainKey
	 * 			url 是否包含键 如 ../userNo/{userNo} ,其中 userNo 为键，{userNo} 为值,默认为 true
	 */
	public APIQueryParam(boolean isParamsUrlContainKey) {
		this.isParamsUrlContainKey = isParamsUrlContainKey;
	}
	
	@Override
	public Object put(String key, Object value) {
		String valueStr = String.valueOf(value).trim();
		if( TextUtils.isEmpty(valueStr) || "NULL".equalsIgnoreCase(valueStr)) {
			valueStr = "NULL";
		}
		return super.put(key, valueStr);
	}
	
	@Override
	public String toString() {
		Set<Entry<String, Object>> entries = entrySet();
		StringBuffer sb = new StringBuffer("/");
		for( Entry<String, Object> entry : entries ) {
			if( isParamsUrlContainKey ) {
				sb.append(entry.getKey() + "/");
			}
			sb.append(entry.getValue() + "/");
		}
		return sb.deleteCharAt(sb.lastIndexOf("/")).toString();
	}
	
}
