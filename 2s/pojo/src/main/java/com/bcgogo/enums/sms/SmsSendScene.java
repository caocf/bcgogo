package com.bcgogo.enums.sms;

/**
 * 短信场景
 * User: Jimuchen
 * Date: 13-3-7
 * Time: 下午2:40
 */
public enum SmsSendScene {
  INVITE_SUPPLIER("邀请供应商"),
  INVITE_CUSTOMER("邀请客户"),
  SEND_INVITATION_CODE_SMS("邀请码促销短信"),
  GUARANTEE("保险"),
  VALIDATE_CAR("验车"),
  BIRTHDAY("生日"),
  DEBT("欠款"),
  KEEP_IN_GOOD_REPAIR("保养"),
  APPOINT_SERVICE("自定义的预约服务"),
  MEMBER_SERVICE("会员服务"),
  MAINTAIN_MILEAGE("保养里程"),
  MANUALLY("手动发送"),
  APP_MESSAGE("APP消息"),
  INIT("系统自动初始化"),
  WX_CONSUME_TEMPLATE("单据消费"),
  WX_SEND_MASS_MSG("群发消息"),
  WX_APPOINT_REMIND_TEMPLATE("预约提醒"),
  WX_GIFT("赠送金额"),
  WX_V_REGULATION_TEMPLATE("车辆违章提醒"),
  WX_RECHARGE("账户充值"),
  OTHERS;
  private String name;
  private SmsSendScene(){}
  private  SmsSendScene(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }

  public static SmsSendScene getSmsSendSceneByRemindType(Integer type){
    if(type==null) return null;
    switch (type){
      case 0:
        return GUARANTEE;
      case 1:
        return VALIDATE_CAR;
      case 2:
        return BIRTHDAY;
      case 3:
        return DEBT;
      case 4:
        return KEEP_IN_GOOD_REPAIR;
      case 5:
        return APPOINT_SERVICE;
      case 6:
        return MEMBER_SERVICE;
      case 7:
        return MAINTAIN_MILEAGE;
    }
    return null;
  }
}
