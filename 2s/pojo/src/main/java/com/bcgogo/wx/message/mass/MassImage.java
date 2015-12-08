package com.bcgogo.wx.message.mass;

import com.bcgogo.wx.message.WXBaseMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * 群发图片消息
 * Author: ndong
 * Date: 14-11-4
 * Time: 上午10:51
 */
public class MassImage extends WXBaseMsg {
   protected Map<String,String> image=new HashMap<String, String>();

  public Map<String, String> getImage() {
    return image;
  }

  public void setImage(Map<String, String> image) {
    this.image = image;
  }
}
