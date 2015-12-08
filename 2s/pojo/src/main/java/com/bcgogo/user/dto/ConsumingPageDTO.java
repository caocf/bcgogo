package com.bcgogo.user.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/17
 * Time: 10:13.
 */
public class ConsumingPageDTO {

    private long id;            //消费的id
    private double coupon;      //消费的代金券金额
    private long consumerTime;  //消费的时间
    private String receiptNo;   //订单号
    private String orderTypes;  //订单类型
    private String orderStatus; //订单状态
    private long productId;     //产品id
    private String product;     //产品名称
    private double sumMoney;    //消费总金额

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }

    public long getConsumerTime() {
        return consumerTime;
    }

    public void setConsumerTime(long consumerTime) {
        this.consumerTime = consumerTime;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypes(String orderTypes) {
        if (StringUtil.isEmpty(orderTypes)){
            this.orderTypes="";
            return;
        }
        OrderTypes types=OrderTypes.valueOf(orderTypes);
        this.orderTypes = types.getName();
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        if (StringUtil.isEmpty(orderStatus)){
            this.orderStatus="";
            return;
        }
        OrderStatus status=OrderStatus.valueOf(orderStatus);
        this.orderStatus = status.getName();
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(double sumMoney) {
        this.sumMoney = sumMoney;
    }
}
