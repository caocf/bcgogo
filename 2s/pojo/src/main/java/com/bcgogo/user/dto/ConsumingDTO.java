package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/16
 * Time: 16:25.
 */
public class ConsumingDTO {

    private double coupon;
    private long consumingTime;

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(Double coupon) {
        this.coupon = coupon;
    }

    public long getConsumingTime() {
        return consumingTime;
    }

    public void setConsumingTime(long consumingTime) {
        this.consumingTime = consumingTime;
    }
}
