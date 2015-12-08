package com.bcgogo.wx.menu;

import com.bcgogo.wx.WXConstant;

/**
 * 链接菜单，点击后跳转到页面
 * User: ndong
 * Date: 14-8-28
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
public class ViewButton extends OprateButton {
  private String url;

  public ViewButton(){
    super.setType(WXConstant.MENU_TYPE_VIEW);
  }
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
