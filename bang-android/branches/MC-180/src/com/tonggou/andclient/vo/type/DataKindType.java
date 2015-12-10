package com.tonggou.andclient.vo.type;

/**
 * Guest 模式的请求环境类型
 * 
 * <ul>
 * 	<li> {@link #TEST}
 * 	<li> {@link #OFFICIAL}
 * </ul>
 * 
 * @author lwz
 *
 */
public enum DataKindType {
	/**
	 * 测试
	 */
	TEST("TEST"),	
	
	/**
	 * 正式
	 */
	OFFICIAL("OFFICIAL");
	
	private String value;
	
	private DataKindType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
