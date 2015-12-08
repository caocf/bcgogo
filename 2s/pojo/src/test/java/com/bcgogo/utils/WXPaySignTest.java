package com.bcgogo.utils;

import com.bcgogo.appPay.wxPay.utils.MD5Util;

import java.util.*;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/29
 * Time: 15:29.
 */
public class WXPaySignTest {
    private static String Key = "kci2Wbs96GYCY0ZkWNkyIz5AUzyGPSD2";

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(">>>模拟微信支付<<<");
        System.out.println("==========华丽的分隔符==========");
        //微信api提供的参数
//        <appid>wx5a49c17d6d052367</appid>
//        <body>weixin</body>
//        <mch_id>1284713901</mch_id>
//        <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
//        <notify_url>http://121.40.35.3/test</notify_url>
//        <out_trade_no>20150806125346</out_trade_no>
//        <spbill_create_ip>127.0.0.1</spbill_create_ip>
//        <total_fee>1</total_fee>
//        <trade_type>APP</trade_type>
        String appid = "wx5a49c17d6d052367";
        String mch_id = "1284713901";
        String body = "weixin";
        String nonce_str = "ibuaiVcKdpRxkhJA";
        String notifyUrl = "http://121.40.35.3/test";
        String outTradeNo = "20150806125346";
        String spbillCreateIp = "127.0.0.1";
        int totalFee = 2;
        String tradeType = "APP";

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("appid", appid);
        parameters.put("mch_id", mch_id);
        parameters.put("body", body);
        parameters.put("nonce_str", nonce_str);
        parameters.put("notify_url",notifyUrl);
        parameters.put("out_trade_no",outTradeNo);
        parameters.put("spbill_create_ip",spbillCreateIp);
        parameters.put("total_fee",totalFee);
        parameters.put("trade_type", tradeType);

        String characterEncoding = "UTF-8";
        String weixinApiSign = "9A0A8659F005D6984697E2CA0A9CF3B7";
        System.out.println("微信的签名是：" + weixinApiSign);
        String mySign = createSign(characterEncoding,parameters);
        System.out.println("我     的签名是："+mySign);

    }

    /**
     * 微信支付签名算法sign
     * @param characterEncoding
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String createSign(String characterEncoding,SortedMap<Object,Object> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + Key);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }
}
