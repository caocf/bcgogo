package com.bcgogo.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-31
 * Time: 上午9:31
 */
public enum MessageScene {
  FINISH_MSG("完工提醒"),
  BOSS_DEBT_MSG("欠款备忘"),//发送给老板
  CUSTOMER_DEBT_MSG("消费欠款提醒"),//发送给客户
  DISCOUNT_MSG("折扣备忘"),//发给老板
  CUSTOMER_REMIND_GUARANTEE("保险到期提醒"),
  CUSTOMER_REMIND_VALIDATE_CAR("验车到期提醒"),
  CUSTOMER_REMIND_APPOINT_SERVICE("预约服务提醒"),
  CUSTOMER_REMIND_MEMBER_SERVICE("会员服务到期提醒"),
  CUSTOMER_REMIND_BIRTHDAY("生日提醒"),
  CUSTOMER_REMIND_KEEP_IN_GOOD_REPAIR("保养到期提醒"),
  CUSTOMER_REMIND_MAINTAIN_MILEAGE("到达保养里程短信"),
  AllOCATED_ACCOUNT_MSG("账号分配提醒"),
  VERIFICATION_CODE("验证码提醒"),
  INVITATION_CODE("邀请码提醒"),
  CHANGE_PASSWORD("密码修改提醒"),
  CHANGE_USER_NO("账号修改提醒"),
  TRIAL_REGISTER_SMS_SEND_TO_CUSTOMER("试用版注册提醒"),
  SYSTEM_SMS("系统短信"),
  REGISTER_MSG_SEND_TO_CUSTOMER("注册提醒"),
  MEMBER_CONSUME("会员结算通知"),
  MEMBER_BUY("会员购卡短信"),
  MEMBER_RENEW("会员续卡短信"),
  SALES_ACCEPTED("销售单接受短信"),
  SALES_REFUSE("销售单拒绝短信"),
  STOCKING_CANCEL("备货中作废短信"),
  SHIPPED_CANCEL("已发货作废短信"),
  RETURNS_ACCEPTED("退货单接受短信"),
  RETURNS_REFUSE("退货单拒绝短信"),
  APP_CHANGE_PASSWORD("APP找回密码短信"),
  MEMBER_CONSUME_SMS_SWITCH("会员消费短信开关"),
  SHOP_SMS_TEMPLATE("店铺自定义模板"),
  BCGOGO_ORDER_SMS("BCGOGO下单通知BCGOGO客服"),
  MOBILE_SMS("手机短信"),
  MOBILE_APP("手机端短信"),
  SETTLED_REMIND("结算提醒"),
  FAULT_INFO_CODE_SMS("故障短信模板"),
  //
  ;

  private final String scene;
  private static Map<String, MessageScene> lookup = new HashMap<String, MessageScene>();
  private static List<String> messageSceneList = new ArrayList<String>();

  private MessageScene(String scene) {
    this.scene = scene;
  }

  public String getScene() {
    return scene;
  }


  static {
    for (MessageScene messageScene : MessageScene.values()) {
      lookup.put(messageScene.getScene(), messageScene);
    }
    for (MessageScene messageScene : MessageScene.values()) {
      messageSceneList.add(messageScene.toString());
    }
  }

  public static List<String> getMessageSceneList() {
    return messageSceneList;
  }


}
