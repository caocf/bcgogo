package com.bcgogo.wx.message.mass;

import com.bcgogo.wx.MsgType;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据groupId群发的图文消息
 * Author: ndong
 * Date: 14-11-4
 * Time: 上午10:31
 */
public class GroupMassNews extends MassNews{
  private Map<String,String> filter=new HashMap<String,String>();

  public GroupMassNews(String mediaId,String groupId){
    super.mpnews.put("media_id",mediaId);
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
