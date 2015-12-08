package com.bcgogo.enums.app;

/**
 * 手机端用户类型：蓝牙版 和GSM卡版
 * User: lw
 * Date: 14-3-11
 * Time: 下午4:35
 */
public enum AppUserType {
  BLUE_TOOTH,
  GSM,   //老一代obd
  MIRROR,  //后视镜用户
  POBD, //彭奥迪的obd
  SGSM, //2s的obd用户
  OBD,//obd硬件升级
  BCGOGO_SHOP_OWNER;  //一发app shop店主
}
