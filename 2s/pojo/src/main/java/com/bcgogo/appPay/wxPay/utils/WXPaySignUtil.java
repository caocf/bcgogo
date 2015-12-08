package com.bcgogo.appPay.wxPay.utils;

import com.bcgogo.appPay.wxPay.WXPayConstant;

import java.util.*;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/29
 * Time: 17:47.
 */
public class WXPaySignUtil {

    private static String Key = WXPayConstant.WX_PAY_KEY;

    public static String postXML (String money , String product , String receiptNo){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml><appid>");
        sb.append(WXPayConstant.APP_ID);
        sb.append("</appid><body>");
        sb.append(product);
        sb.append("</body><mch_id>");
        sb.append(WXPayConstant.MCH_ID);
        sb.append("</mch_id><nonce_str>");

        String nonceStr = WXPaySignUtil.getCharAndNumr(20);
        sb.append(nonceStr);

        sb.append("</nonce_str><notify_url>");
        sb.append(WXPayConstant.NOTIFY_URL);
        sb.append("</notify_url><out_trade_no>");
        sb.append(receiptNo);
        sb.append("</out_trade_no><spbill_create_ip>");
        sb.append(WXPayConstant.SPBILL_CREAT_IP);
        sb.append("</spbill_create_ip><total_fee>");
        sb.append(money);
        sb.append("</total_fee><trade_type>");
        sb.append(WXPayConstant.TRADE_TYPE);
        sb.append("</trade_type><sign>");

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("appid", WXPayConstant.APP_ID);
        parameters.put("mch_id", WXPayConstant.MCH_ID);
        parameters.put("body", product);
        parameters.put("nonce_str", nonceStr);
        parameters.put("notify_url",WXPayConstant.NOTIFY_URL);
        parameters.put("out_trade_no",receiptNo);
        parameters.put("spbill_create_ip",WXPayConstant.SPBILL_CREAT_IP);
        parameters.put("total_fee",money);
        parameters.put("trade_type", WXPayConstant.TRADE_TYPE);

        sb.append(WXPaySignUtil.createSign(WXPayConstant.CHARACTER_ENCODING, parameters));
        sb.append("</sign></xml>");

        return sb.toString();
    }

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

    //生成随机字母和数字组合
    public static String getCharAndNumr(int length)
    {
        String val = "";

        Random random = new Random();
        for(int i = 0; i < length; i++)
        {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

            if("char".equalsIgnoreCase(charOrNum)) // 字符串
            {
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
                val += (char) (choice + random.nextInt(26));
            }
            else if("num".equalsIgnoreCase(charOrNum)) // 数字
            {
                val += String.valueOf(random.nextInt(10));
            }
        }

        return val;
    }
}
