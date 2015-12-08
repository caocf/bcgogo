package com.bcgogo.wx.message;

/**
 * 微信消息分类.
 * User: ndong
 * Date: 14-9-12
 * Time: 下午4:28
 * To change this template use File | Settings | File Templates.
 */
public enum WXMCategory {
  RESP("普通请求响应消息"),
  SERVICE("客服消息"),
  TEMPLATE("模版消息"),
  MASS("高级接口群发消息"),
  ;
  private String name;
  WXMCategory(String name){
    this.name=name;
  }

}
