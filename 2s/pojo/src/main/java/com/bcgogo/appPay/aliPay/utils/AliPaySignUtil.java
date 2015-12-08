package com.bcgogo.appPay.aliPay.utils;

import com.bcgogo.appPay.aliPay.AliPayConstant;

import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/2
 * Time: 13:59.
 */
public class AliPaySignUtil {

    public static String  orderInfo(Map map){

        StringBuilder sb = new StringBuilder();
        sb.append("partner=" + "\"" + AliPayConstant.ALI_PARTNER + "\"");
        sb.append("&seller_id=" + "\"" + AliPayConstant.ALI_SELLER + "\"");
        sb.append("&out_trade_no=" + "\"" + map.get(AliPayConstant.ALI_RECEIPT_NO).toString() + "\"");
        sb.append("&subject=" + "\"" + map.get(AliPayConstant.ALI_PRODUCT).toString() + "\"");
        sb.append("&body=" + "\"" + map.get(AliPayConstant.ALI_PRODUCT_DES).toString() + "\"");
        sb.append("&total_fee=" + "\"" + map.get(AliPayConstant.ALI_PRICE).toString() + "\"");
        sb.append("&notify_url=" + "\"" + AliPayConstant.NOTIFY_URL + "\"");
        sb.append("&service=\"mobile.securitypay.pay\"");
        sb.append("&payment_type=\"1\"");
        sb.append("&_input_charset=\"utf-8\"");
        sb.append("&it_b_pay=\"30m\"");
        sb.append("&return_url=\"m.alipay.com\"");
        return sb.toString();
    }
}
