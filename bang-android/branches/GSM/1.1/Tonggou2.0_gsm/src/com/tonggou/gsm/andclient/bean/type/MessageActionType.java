package com.tonggou.gsm.andclient.bean.type;

/**
 * 消息对应的动作类型
 * 
 * <ul>
 * 	<li>{@link #SEARCH_SHOP} 跳转到店铺查询. params 参数格式：预约单Id,shopId</li>
 * 	<li>{@link #SERVICE_DETAIL}跳转到具体的服务.params 参数格式：预约单Id,shopId（没用到）</li>
 * 	<li>{@link #CANCEL_ORDER}取消服务 . params 参数格式：预约单Id,shopId（没用到）</li>
 * 	<li>{@link #ORDER_DETAIL}查看单据详情 . params 参数格式：预约单Id,shopId</li>
 * 	<li>{@link #COMMENT_SHOP}评价单据 . params 参数格式：预约单Id,shopId,服务单Id</li>
 * </ul>
 * 
 * @author lwz
 *
 */
public enum MessageActionType {
	/**
	 * 跳转到店铺查询. params 参数格式：预约单Id,shopId
	 */
	SEARCH_SHOP,
	/**
	 * 跳转到具体的服务.params 参数格式：预约单Id,shopId（没用到）
	 */
	SERVICE_DETAIL,
	/**
	 * 取消服务 . params 参数格式：预约单Id,shopId（没用到）
	 */
	CANCEL_ORDER,
	/**
	 * 查看单据详情 . params 参数格式：预约单Id,shopId
	 */
	ORDER_DETAIL,
	/**
	 * 评价单据 . params 参数格式：预约单Id,shopId,服务单Id
	 */
	COMMENT_SHOP
}
