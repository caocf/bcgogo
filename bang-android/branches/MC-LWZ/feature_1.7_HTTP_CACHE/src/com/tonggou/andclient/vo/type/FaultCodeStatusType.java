package com.tonggou.andclient.vo.type;

/**
 * 排序类型
 * 
 * <ul>
 * 	<li> {@link #UNTREATED}
 * 	<li> {@link #FIXED}
 * 	<li> {@link #IGNORED}
 * 	<li> {@link #DELETED}
 * </ul>
 * 
 * @author lwz
 *
 */
public enum FaultCodeStatusType {
	/**
	 * 未处理
	 */
	UNTREATED("UNTREATED"),	
	/**
	 * 已修复
	 */
	FIXED("FIXED"),	
	
	/**
	 * 忽略
	 */
	IGNORED("IGNORED"),
	
	/**
	 * 删除
	 */
	DELETED("DELETED");
	
	private String value;
	
	private FaultCodeStatusType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
