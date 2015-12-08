package com.bcgogo.enums.shop;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-22
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
public enum ShopOperateTaskScene {
  DISABLE_REGISTERED_PAID_SHOP,  //禁用缴费使用的shop
  ARREARS_REGISTERED_PAID_SHOP,//如果店铺使用截止时间小于当前系统时间 把店铺状态改为欠费状态
  ENABLE_REGISTERED_PAID_SHOP     //启用缴费使用的shop
}
