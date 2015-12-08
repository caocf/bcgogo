package com.bcgogo.constant;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-6
 * Time: 下午1:41
 * To change this template use File | Settings | File Templates.
 */
public class ChinaPayConstants {

  public class ChinaPayType {
    public static final long CHINA_PAY_TYPE_FIRST = 1L; //本地提交充值表单

    public static final long CHINA_PAY_TYPE_SECOND = 2L; //银行后台通知

    public static final long CHINA_PAY_TYPE_THIRD = 3L;  //前台跳转回本地

    public static final long CHINA_PAY_TYPE_FOUR = 4L;  //定时钟自动查询

  }

  public static final String SMS_BG_RET_URL = "chinapay.do?method=smsRechargeReceive";
  public static final String SMS_PAGE_RET_URL = "smsrecharge.do?method=smsrechargecomplete";

  public static final String LOAN_TRANSFERS_BG_RET_URL = "chinapay.do?method=loanTransfersReceive";
  public static final String LOAN_TRANSFERS_PAGE_RET_URL = "loanTransfers.do?method=saveLoanComplete";

  //分期
  public static final String SOFTWARE_INSTALMENT_BG_RET_URL = "chinapay.do?method=instalmentOnLineReceivable";
  public static final String SOFTWARE_INSTALMENT_PAGE_RET_URL = "bcgogoReceivable.do?method=instalmentOnLineComplete";

  //全额或者首次
  public static final String SOFTWARE_BG_RET_URL = "chinapay.do?method=bcgogoSoftwareReceive";
  public static final String SOFTWARE_PAGE_RET_URL = "bcgogoReceivable.do?method=softwareOnlineComplete";

  //a硬件
  public static final String HARDWARE_BG_RET_URL = "chinapay.do?method=bcgogoHardwareReceive";
  public static final String HARDWARE_PAGE_RET_URL = "bcgogoReceivable.do?method=hardwareOnlineComplete";

  //合并支付
  public static final String COMBINED_BG_RET_URL = "chinapay.do?method=bcgogoCombinedPayReceive";
  public static final String COMBINED_PAGE_RET_URL = "bcgogoReceivable.do?method=combinedPayOnlineComplete";


}
