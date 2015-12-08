package com.bcgogo.user.dto;

import com.bcgogo.enums.OrderStatus;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/19
 * Time: 9:39.
 */
public class ConsumingAdminDTO {

    private String receiptNo ;         //订单号
    private String adminStatus ;       //后台订单状态
    private double sumMoney ;          //总金额
    private double coupon ;            //代金券金额
    private double actuallyPay ;       //实际支付金额
    private String product ;           //商品名称
    private String userName ;          //用户姓名
    private long mobile ;              //手机号
    private String vehicleNo ;         //车牌号

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {

        OrderStatus status = OrderStatus.valueOf(adminStatus);
        this.adminStatus = status.getName();
    }

    public double getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(double sumMoney) {
        this.sumMoney = sumMoney;
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }

    public double getActuallyPay() {
        return actuallyPay;
    }

    public void setActuallyPay(double actuallyPay) {
        this.actuallyPay = actuallyPay;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }
}
