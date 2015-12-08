package com.bcgogo.wx.message.mass;

import com.bcgogo.wx.MsgType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据openId群发的图文消息
 * Author: ndong
 * Date: 14-11-4
 * Time: 上午10:33
 */
public class OpenIdMassNews extends MassNews{
  private List<String> touser=new ArrayList<String>();

  public OpenIdMassNews(String mediaId,String... openIds){
    mpnews.put("media_id",mediaId);
    setMsgtype(MsgType.mpnews);
    touser.addAll(Arrays.asList(openIds));
  }

  public List<String> getTouser() {
    return touser;
  }

  public void setTouser(List<String> touser) {
    this.touser = touser;
  }
}
