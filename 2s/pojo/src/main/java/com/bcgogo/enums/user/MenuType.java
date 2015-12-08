package com.bcgogo.enums.user;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-25
 * Time: 上午10:04
 * menu 枚举
 */
public enum MenuType {
  VEHICLE_CONSTRUCTION("车辆施工"),
  AUTO_ACCESSORY_ONLINE_COMMODITYQUOTATIONS("配件报价"),
  AUTO_ACCESSORY_ONLINE_ORDER_CENTER("订单中心"),
  AUTO_ACCESSORY_ONLINE_SHOPPINGCART("购物车"),
  AUTO_ACCESSORY_ONLINE_RETURN_ONLINE("在线退货"),
  TXN_INVENTORY_MANAGE_REPAIR_PICKING("维修领料"),
  CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER("推荐客户"),
  AUTO_ACCESSORY_ONLINE("汽配在线"),
  WEB_SYSTEM_SETTINGS_CUSTOM_CONFIG_PAGE_CONFIG("页面配置")
  ;

  private String name;

  MenuType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
