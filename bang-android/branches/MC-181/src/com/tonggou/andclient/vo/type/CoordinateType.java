package com.tonggou.andclient.vo.type;

/**
 * 坐标类型
 * 
 * <ul>
 * 	<li> {@link #NULL}
 * 	<li> {@link #CURRENT}
 * 	<li> {@link #LAST}
 * </ul>
 * 
 * @author lwz
 *
 */
public enum CoordinateType {
	
	/**
	 * 不确定
	 */
	NULL("NULL"),
	
	/**
	 * 当前
	 */
	CURRENT("CURRENT"),	
	
	/**
	 * 上次
	 */
	LAST("LAST");
	
	private String value;
	
	private CoordinateType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
