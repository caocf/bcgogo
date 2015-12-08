package com.bcgogo.wx.message.mass;

import com.bcgogo.wx.MsgType;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据groupId群发的图片消息
 * Author: ndong
 * Date: 14-11-4
 * Time: 上午10:54
 */
public class GroupMassImage extends MassImage{
   private Map<String,String> filter=new HashMap<String,String>();

  public GroupMassImage(String mediaId,String groupId){
    super.image.put("media_id",mediaId);
    filter.put("group_id",groupId);
    setMsgtype(MsgType.mpnews);
  }

  public Map<String, String> getFilter() {
    return filter;
  }

  public void setFilter(Map<String, String> filter) {
    this.filter = filter;
  }
}
