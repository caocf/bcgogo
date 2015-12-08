package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * Author : ztyu
 * Date: 2015/11/6
 * Time: 11:52
 */
public class ConsumingRecordDTO {

    private Long orderId;       //订单号
    private String vehicleNo;   //车牌号
    private String customerName;    //客户信息
    private String userName;    //车主信息
    private String orderType;   //订单类型
    private Long time;          //进厂时间
    private Float coupon;       //代金券金额
    private Long id;      //代金券消费记录id
    private String idStr;//代金券消费记录id字符串
    private String orderStatusStr;   //订单状态字符串
    private OrderStatus orderStatus;   //订单状态

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.setIdStr(id.toString());
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        if (StringUtil.isEmpty(orderType)){
            this.orderType="";
            return;
        }
        OrderTypes types=OrderTypes.valueOf(orderType);
        this.orderType = types.getName();
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Float getCoupon() {
        return coupon;
    }

    public void setCoupon(Float coupon) {
        this.coupon = coupon;
    }

    public String getOrderStatusStr() {
        return orderStatusStr;
    }

    public void setOrderStatusStr(String orderStatusStr) {
        this.orderStatusStr = orderStatusStr;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.orderStatusStr=orderStatus.getName();
    }
}
