package com.bcgogo.appPay.wxPay;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/29
 * Time: 17:52.
 */
public class WXPrepareOrder {

    private String prepayId;
    private String nonceStr;
    private String timeStamp;
    private String sign;

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
