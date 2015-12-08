package com.bcgogo.user.dto;

import com.bcgogo.config.dto.TrafficPackageDTO;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/18
 * Time: 9:47.
 */
public class ConsumingDetailsDTO {

    private String product;                 //商品信息
    private double productSalary;           //商品价格
    private String pictureUrl;              //商品图片地址
    private long orderCreatedTime;          //创建订单时间
    private long consumingTime;             //支付成功时间
    private double sumMoney;                //订单总额
    private double coupon;                  //优惠金额
    private double payMoney;             //实付金额
    private String receiptNo;               //订单号
    private String orderStatus;             //订单状态

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getProductSalary() {
        return productSalary;
    }

    public void setProductSalary(double productSalary) {
        this.productSalary = productSalary;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public long getOrderCreatedTime() {
        return orderCreatedTime;
    }

    public void setOrderCreatedTime(long orderCreatedTime) {
        this.orderCreatedTime = orderCreatedTime;
    }

    public long getConsumingTime() {
        return consumingTime;
    }

    public void setConsumingTime(long consumingTime) {
        this.consumingTime = consumingTime;
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

    public double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(double payMoney) {
        this.payMoney = payMoney;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
