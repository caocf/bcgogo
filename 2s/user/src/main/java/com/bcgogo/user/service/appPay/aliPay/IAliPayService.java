package com.bcgogo.user.service.appPay.aliPay;

import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/2
 * Time: 14:19.
 */
public interface IAliPayService {

    String createPrepareOrder(Map map);

    void notifySuccess(Map map);
}
