package com.bcgogo.constant;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-6
 * Time: 上午11:45
 * To change this template use File | Settings | File Templates.
 */
public class TransactionConstants {

    public class TransactionType{
        //支付
        public static final long TRANSACTION_TYPE_PAY = 1L;
    }

    /**
     * 引用类型
     */
    public class ReferenceType{
        public static final long REFERENCE_TYPE_SMS_RECHARGE = 1L;
    }

    public class Currency{
        //1为人民币
        public static final long CURRENCY_RMB = 1L;
    }

    public class PayMethod{
        //1表示银联支付
        public static final long PAY_METHOD_CHINAPAY = 1L;
    }

}
