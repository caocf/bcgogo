package com.bcgogo.user.service.appPay.wxPay;

import com.bcgogo.appPay.wxPay.WXPrepareOrder;

import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/29
 * Time: 13:50.
 */
public interface IWXPayService {

    //生成预付订单
    public WXPrepareOrder createPrepareOrder (Map map ,String money);

    //支付成功
    public void wxPaySuccess(Map map);
}
