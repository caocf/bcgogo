package com.bcgogo.event;

import com.bcgogo.wx.WXRequestParam;

import java.util.EventObject;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-24
 * Time: 下午5:33
 */
public class WXEventObj extends EventObject {

  public WXEventObj(WXRequestParam param) {
    super(param);
  }
}
