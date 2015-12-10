package com.tonggou.andclient.vo.type;

/**
 * 服务类型 枚举类
 * <ul>
 * 	<li>{@link #ALL}
 * 	<li>{@link #OVERHAUL_AND_MAINTENANCE}
 * 	<li>{@link #DECORATION_BEAUTY}
 * 	<li>{@link #PAINTING}
 * 	<li>{@link #INSURANCE}
 * 	<li>{@link #WASH}
 * </ul>
 * @author lwz
 *
 */
public enum ServiceScopeType {
	/**
	 * 所有的服务
	 */
	ALL("NULL", "所有服务"),
	
	/**
	 * 机修保养
	 */
	OVERHAUL_AND_MAINTENANCE("OVERHAUL_AND_MAINTENANCE", "机修保养"),
	
	/**
	 * 美容装潢
	 */
	DECORATION_BEAUTY("DECORATION_BEAUTY", "美容装潢"),
	
	/**
	 * 钣金喷漆
	 */
	PAINTING("PAINTING", "钣金喷漆"),
	
	/**
	 * 保险验车
	 */
	INSURANCE("INSURANCE", "保险验车"),
	
	/**
	 * 洗车服务
	 */
	WASH("WASH", "洗车");
	
	private final String type;
	private final String name;
	
	private ServiceScopeType(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	/**
	 * 得到类型的值
	 * @return
	 */
	public String getTypeValue() {
		return type;
	}
	
	/**
	 * 得到类型名
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getTypeValue();
	}
}
