package com.tonggou.gsm.andclient.bean.type;

/**
 * 消息类型
 * 
 * <ul>
 * 	<li> {@link #SHOP_CHANGE_APPOINT}
 * 	<li> {@link #SHOP_FINISH_APPOINT}
 * 	<li> {@link #SHOP_ACCEPT_APPOINT}
 * 	<li> {@link #SHOP_CANCEL_APPOINT}
 * 	<li> {@link #SHOP_REJECT_APPOINT}
 * 	<li> {@link #OVERDUE_APPOINT_TO_APP}
 * 	<li> {@link #APP_VEHICLE_MAINTAIN_MILEAGE}
 * 	<li> {@link #APP_VEHICLE_INSURANCE_TIME}
 * 	<li> {@link #APP_VEHICLE_EXAMINE_TIME}
 * 	<li> {@link #VEHICLE_FAULT_2_APP}
 * 	<li> {@link #CUSTOM_MESSAGE_2_APP}
 * 	<li> {@link #SHOP_ADVERT_TO_APP}
 * 	<li> {@link #VIOLATE_REGULATION_RECORD_2_APP}
 * </ul>
 * 
 * @author lwz
 *
 */
public enum MessageType {
		/**
		 * 店铺预约修改消息
		 */
	    SHOP_CHANGE_APPOINT,	
	    
	    /**
	     * 店铺预约结束消息
	     */
	    SHOP_FINISH_APPOINT,	
	    
	    /**
	     * 店铺接受预约单
	     */
	    SHOP_ACCEPT_APPOINT,	
	    
	    /**
	     * 店铺预约拒绝消息
	     */
	    SHOP_CANCEL_APPOINT,	
	    
	    /**
	     * 店铺预约取消消息
	     */
	    SHOP_REJECT_APPOINT,	
	    
	    /**
	     * APP过期预约单
	     */
	    OVERDUE_APPOINT_TO_APP,	
	    
	    /**
	     * 保养里程
	     */
	    APP_VEHICLE_MAINTAIN_MILEAGE,	
	    
	    /**
	     * 保养时间
	     */
	    APP_VEHICLE_MAINTAIN_TIME,	
	    
	    /**
	     * 保险时间
	     */
	    APP_VEHICLE_INSURANCE_TIME,	
		
	    /**
	     * 验车时间
	     */
	    APP_VEHICLE_EXAMINE_TIME,
	    
	    /**
	     * 故障消息
	     */
	    VEHICLE_FAULT_2_APP, 
	    
	    /**
	     * 自定义消息类型
	     */
	    CUSTOM_MESSAGE_2_APP,
	    
	    /**
	     * 店铺公告
	     */
	    SHOP_ADVERT_TO_APP,
	    
	    /**
	     * 违章消息
	     */
	    VIOLATE_REGULATION_RECORD_2_APP
}
