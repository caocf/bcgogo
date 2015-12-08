package com.bcgogo.enums.app;

import java.util.HashMap;
import java.util.Map;

/**
 * 预约单预约方式
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-30
 * Time: 下午2:42
 * To change this template use File | Settings | File Templates.
 */
public enum AppointWay {
  APP("在线预约"),//在线预约
  WECHAT("微信预约"),//微信预约
  SHOP("现场预约"),//现场预约
  PHONE("电话预约");//电话预约

  private final String name;

  private AppointWay(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
