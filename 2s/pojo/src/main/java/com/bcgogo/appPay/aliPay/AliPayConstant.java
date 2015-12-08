package com.bcgogo.appPay.aliPay;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/2
 * Time: 13:25.
 */
public class AliPayConstant {


    /** 商户PID**/
    public static String ALI_PARTNER = "2088601002395888";
    /** 商户收款账号**/
    public static String ALI_SELLER  = "bcgogoyifa@163.com";
    /**公钥**/
    public static String ALI_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    /**私钥 pkcs8格式 **/
    public static String ALI_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMAig/TRnSQr0DPq70iWF05CEFDzU0heivIHTqqDnC++oeGuNCCwUY0biCW9vFuF4dG52FD/WdBtw+7FbPOvu9z2xS8oMaah6J6S9Dd1lFW0qcHVh048pRJYafM/tA6aWvyQGrkPV3Uj8M8B3f1OucpzwVvbw9jyTf51aYProXIhAgMBAAECgYBWu/4XubyXhJPTrB3jBaZ1tIsLEB0rkUpmIfhYL1uFjigj6fEsRIw8CA65AuMR6elcNOo6/lr8JWEK2+LoSw+h0vmftCre3RpHenH148+sEPpisI97GXrp3660BjI80IePByuVhioaUqa3BroO/wYNjzoHUfmCx1VYkCq9UukgwQJBAOb2hFIenPpb9lF6R5QHIYqdcUl/UrBpmYFk2DP8rQ+o5GQEzs9V5yywrCUfc6YQfNqST0XGw3pHKP/MbZje72kCQQDU9nlcKCRmuDzQPSn0G4M65EM6/nnSZBhPZ5Rnyn7XzLTSBzdXkk6TaVg/Z8sggWPpgTsfM4N56XdhE811iE35AkEA4kEyhOPacePu3AiG4DtQH2N23EIvdgQszbZFmj/4JTQVcOnI0xHbIkt8h+1UtKTIOrJDmB5L1pipUFUqNCyIqQJAXQEK/Rk21HlPba3YfZfqVrTLO59Y+m3mQ/87S5yeioYGzdaC78agdDZEaOReTGLVjGt4s2ae24X/MbEhQFv3mQJBAIkcPF+JSlzOuK+JCMDuEkQIaeE+VEnw+txlO4EZABEdmruGl62rZocPeG3+s47GRHcYXMpNr+kOTKIyYJNqHOA=";

    //生成签名所需参数
    public static String ALI_APPUSER_NO = "appUserNo";
    public static String ALI_PRODUCT = "product";
    public static String ALI_PRODUCT_ID = "productId";
    public static String ALI_PRODUCT_DES = "productDes";
    public static String ALI_PRODUCT_PRICE = "price";
    public static String ALI_COUPON = "coupon";
    public static String ALI_PRICE = "realMoney";
    public static String ALI_RECEIPT_NO = "receiptNo";
    public static String NOTIFY_URL = "https://phone.bcgogo.com:1443/api/user/aliprepayOrder";
//    public static String NOTIFY_URL = "http://61.177.55.242:8099/api/user/aliprepayOrder";


    //获取签名的方式
    public static String ALI_SIGN_TYPE = "sign_type=\"RSA\"";

    //编码格式
    public static String ALI_ECODING = "UTF-8";

    //memcache
    public static long ALI_PAY_TIME = 600L;
    public static String ALI_NOTIFY_RECEIPT_NO = "out_trade_no";
}
