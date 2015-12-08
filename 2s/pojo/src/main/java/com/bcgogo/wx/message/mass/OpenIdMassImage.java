package com.bcgogo.wx.message.mass;

import com.bcgogo.wx.MsgType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据openId群发的图片消息
 * Author: ndong
 * Date: 14-11-4
 * Time: 上午10:52
 */
public class OpenIdMassImage extends MassImage{
  private List<String> touser=new ArrayList<String>();

  public OpenIdMassImage(String mediaId,String... openIds){
    image.put("media_id",mediaId);
    setMsgtype(MsgType.image);
    touser.addAll(Arrays.asList(openIds));
  }

  public List<String> getTouser() {
    return touser;
  }

  public void setTouser(List<String> touser) {
    this.touser = touser;
  }
}
