package com.bcgogo.constant;

/**
 * 存放短信充值常量
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-5
 * Time: 下午3:56
 * To change this template use File | Settings | File Templates.
 */
public class SmsRechargeConstants {


  public static final String CHINA_PAY_ORDER_DEC_SMS = "短信充值";
  public static final String CHINA_PAY_LOAN_TRANSFERS = " 货款转账";

  /**
   * 充值记录状态描述
   */
  public class RechargeStatusDesc {
    public static final String RECHARGE_STATUS_DESC_FAIL = "充值失败";
    public static final String RECHARGE_STATUS_DESC_WAITING = "正在充值";
    public static final String RECHARGE_STATUS_DESC_CONFIRM = "充值待确认";
    public static final String RECHARGE_STATUS_DESC_SUCCESS = "充值成功";
  }

  /**
   * 充值记录完成状态
   */
  public class RechargeState {
    //新记录
    public static final long RECHARGE_STATE_INIT = 0L;
    //已提交银联
    public static final long RECHARGE_STATE_COMMIT = 1L;
    //完成
    public static final long RECHARGE_STATE_COMPLETE = 2L;
    //失败
    public static final long RECHARGE_STATE_FAIL = 3L;
  }


}
