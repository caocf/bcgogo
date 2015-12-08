package com.bcgogo.appPay.wxPay;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/27
 * Time: 11:00.
 */
public class WXPayConstant {

    public static String WXPAY_SUCCESS = "SUCCESS";
    public static String WXPAY_FAIL = "FAIL";

    //微信账号相关参数
    public static String APP_ID = "wx5a49c17d6d052367";
    public static String APP_SECRET = "d4624c36b6795d1d99dcf0547af5443d";
    public static String WX_PAY_KEY = "kci2Wbs96GYCY0ZkWNkyIz5AUzyGPSD2";
    public static String MCH_ID = "1284713901";

    //微信统一下单地址
    public static String PREPARE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //微信统一下单参数
//    public static String NOTIFY_URL = "http://61.177.55.242:8099/api/user/wxprepayOrder";
    public static String NOTIFY_URL = "https://phone.bcgogo.com:1443/api/user/wxprepayOrder";
    public static String SPBILL_CREAT_IP = "127.0.0.1";
    public static String TRADE_TYPE = "APP";
    public static String PACKAGE = "Sign=WXPay";
    public static String CHARACTER_ENCODING = "UTF-8";

    //生成账单参数
    public static String ORDER_USER = "appUserNo";
    public static String ORDER_COUPON = "coupon";
    public static String ORDER_SUM_MONEY = "sumMoney";
    public static String ORDER_PRODUCT_ID = "productId";
    public static String ORDER_PRODUCT = "product";
    public static String ORDER_RECEIPT_NO = "out_trade_no";

    //微信统一下单返回参数
    public static String PREPARE_ORDER_RESULT_CODE = "result_code";
    public static String PREPARE_ORDER_RETURN_CODE = "return_code";

    //微信回调参数
    public static String NOTIFY_SUCCESS = "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    public static String NOTIFY_FAIL = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";

    //memcache
    public static long PREPARE_ORDER_TIME = 600;


}
