package com.bcgogo.wx.user;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-21
 * Time: 下午8:11
 */
public enum WXSubscribeScene {
  SUBSCRIBE("公共二维码订阅"),
  SUBSCRIBE_SHOP("店铺二维码订阅"),
  SCAN("订阅后扫描"),
  ACCIDENG_SPECIALIST("添加事故专员"),
  CONSUME("在店铺消费"),
  UN_SUBSCRIBE("取消关注"),
  ;
  private String scene;

  private WXSubscribeScene(String scene){
     this.scene=scene;
  }
  public String getScene() {
    return scene;
  }
}
