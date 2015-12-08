package com.bcgogo.wx.message.custom;

import com.bcgogo.wx.MsgType;
import com.bcgogo.wx.article.CustomArticle;

import java.util.HashMap;
import java.util.Map;

/**
 * 客服图文消息
 * User: ndong
 * Date: 14-9-23
 * Time: 上午9:58
 * To change this template use File | Settings | File Templates.
 */
public class CustomNewsMsg extends BaseCustomMsg{
  private Map<String,CustomArticle[]> news;

  public CustomNewsMsg(String touser,CustomArticle... articles){
    super.setMsgtype(MsgType.news);
    super.setTouser(touser);
    news=new HashMap<String, CustomArticle[]>();
    news.put("articles",articles);
  }
}
