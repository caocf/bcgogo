package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-6
 * Time: 下午2:35
 * To change this template use File | Settings | File Templates.
 */
public enum ShopConfigScene {
  MEMBER("会员开关"),
  STORAGE_BIN("仓位开关"),
  TRADE_PRICE("批发价开关"),
  WX_WELCOME_WORD("微信欢迎词配置"),
  WX_MENU_TEXT("微信菜单点击返回值")
  ;

  String scene;

  ShopConfigScene(String scene) {
    this.scene = scene;
  }

  public String getScene() {
    return scene;
  }
}
