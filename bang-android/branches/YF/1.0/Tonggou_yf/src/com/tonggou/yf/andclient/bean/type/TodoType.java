package com.tonggou.yf.andclient.bean.type;

/**
 * 待办事项的类型
 * <ul>
 * <li>故障 {@link DTC}</li>
 * <li>保养 {@link MAINTAIN}</li>
 * <li>预约 {@link APPOINTMENT}</li>
 * @author lwz
 *
 */
public enum TodoType {
	/**
	 * 故障
	 */
	DTC("faultInfo"),
	
	/**
	 * 保养
	 */
	MAINTAIN("customerRemind"),
	
	/**
	 * 预约
	 */
	APPOINTMENT("appoint");
	
	private String type;
	
	private TodoType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
