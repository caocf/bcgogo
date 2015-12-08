package com.bcgogo.wx.menu;

import com.bcgogo.wx.WXConstant;

/**
 * 点击菜单，点击后出发事件
 * User: ndong
 * Date: 14-8-19
 * Time: 上午2:46
 * To change this template use File | Settings | File Templates.
 */
public class ClickButton extends OprateButton {

  private String key;

  public ClickButton(){
    super.setType(WXConstant.MENU_TYPE_CLICK);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
