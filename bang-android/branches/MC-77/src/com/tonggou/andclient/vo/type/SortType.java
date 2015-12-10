package com.tonggou.andclient.vo.type;

/**
 * 排序类型
 * 
 * <ul>
 * 	<li> {@link #DISTANCE}
 * 	<li> {@link #EVALUATION}
 * </ul>
 * 
 * @author lwz
 *
 */
public enum SortType {
	/**
	 * 按距离排序
	 */
	DISTANCE("DISTANCE"),	
	
	/**
	 * 按价格排序
	 */
	EVALUATION("EVALUATION");
	
	private String value;
	
	private SortType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
