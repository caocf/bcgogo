package com.bcgogo.wx.action;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-1-20
 * Time: 14:52
 */

public enum WXActionName {

  LOGIN("login"),
  AUTHORIZED("authorized")
  ;
  private String name;

  WXActionName(String name){
      this.name=name;
  }

  public String getName() {
    return name;
  }
}

