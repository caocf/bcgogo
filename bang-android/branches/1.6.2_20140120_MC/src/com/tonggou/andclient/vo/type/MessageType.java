package com.tonggou.andclient.vo.type;

/**
 * 消息类型
 * 
 * <ul>
 * 	<li> {@link #ALL}
 * 	<li> {@link #SHOP_CHANGE_APPOINT}
 * 	<li> {@link #SHOP_FINISH_APPOINT}
 * 	<li> {@link #SHOP_ACCEPT_APPOINT}
 * 	<li> {@link #SHOP_CANCEL_APPOINT}
 * 	<li> {@link #SHOP_REJECT_APPOINT}
 * 	<li> {@link #OVERDUE_APPOINT_TO_APP}
 * 	<li> {@link #APP_VEHICLE_MAINTAIN_MILEAGE}
 * 	<li> {@link #APP_VEHICLE_INSURANCE_TIME}
 * 	<li> {@link #APP_VEHICLE_EXAMINE_TIME}
 * </ul>
 * 
 * @author lwz
 *
 */
public enum MessageType {
	
	/**
	 * 所有类型
	 */
	ALL("NULL"),
	
	/**
	 * 店铺预约修改消息
	 */
    SHOP_CHANGE_APPOINT("SHOP_CHANGE_APPOINT"),	
    
    /**
     * 店铺预约结束消息
     */
    SHOP_FINISH_APPOINT("SHOP_FINISH_APPOINT"),	
    
    /**
     * 店铺接受预约单
     */
    SHOP_ACCEPT_APPOINT("SHOP_ACCEPT_APPOINT"),	
    
    /**
     * 店铺预约拒绝消息
     */
    SHOP_CANCEL_APPOINT("SHOP_CANCEL_APPOINT"),	
    
    /**
     * 店铺预约取消消息
     */
    SHOP_REJECT_APPOINT("SHOP_REJECT_APPOINT"),	
    
    /**
     * APP过期预约单
     */
    OVERDUE_APPOINT_TO_APP("OVERDUE_APPOINT_TO_APP"),	
    
    /**
     * 保养里程
     */
    APP_VEHICLE_MAINTAIN_MILEAGE("APP_VEHICLE_MAINTAIN_MILEAGE"),	
    
    /**
     * 保养时间
     */
    APP_VEHICLE_MAINTAIN_TIME("APP_VEHICLE_MAINTAIN_TIME"),	
    
    /**
     * 保险时间
     */
    APP_VEHICLE_INSURANCE_TIME("APP_VEHICLE_INSURANCE_TIME"),	
	
    /**
     * 验车时间
     */
    APP_VEHICLE_EXAMINE_TIME("APP_VEHICLE_EXAMINE_TIME");
    
	
	private String value;
	
	private MessageType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	
}
