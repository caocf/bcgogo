package com.tonggou.andclient.vo;
/**
 * 统购消息
 * @author think
 *
 */
public class TonggouMessage {
	private String id ;          //唯一标识号      long
	private String type ;        //消息类型      String
	private String content;      //内容描述   String
	private String actionType;   //操作类型       String	
	private String searchShop;       	//跳转到店铺查询
	private String serviceDetail;    	//跳转到具体的服务
	private String cancelOrder;      	//取消服务
	private String orderDetail;      	//查看单据详情
	private String commentShop;    		//评价单据
	private String params;         //：actionType所依赖的业务数据      String
	private String time;
	private String title;         //标题
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSearchShop() {
		return searchShop;
	}
	public void setSearchShop(String searchShop) {
		this.searchShop = searchShop;
	}
	public String getServiceDetail() {
		return serviceDetail;
	}
	public void setServiceDetail(String serviceDetail) {
		this.serviceDetail = serviceDetail;
	}
	public String getCancelOrder() {
		return cancelOrder;
	}
	public void setCancelOrder(String cancelOrder) {
		this.cancelOrder = cancelOrder;
	}
	public String getOrderDetail() {
		return orderDetail;
	}
	public void setOrderDetail(String orderDetail) {
		this.orderDetail = orderDetail;
	}
	public String getCommentShop() {
		return commentShop;
	}
	public void setCommentShop(String commentShop) {
		this.commentShop = commentShop;
	}
	
	
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	

}
