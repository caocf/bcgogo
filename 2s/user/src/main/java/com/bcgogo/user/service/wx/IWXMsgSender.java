package com.bcgogo.user.service.wx;

import com.bcgogo.common.Result;
import com.bcgogo.wx.WXArticleDTO;
import com.bcgogo.wx.article.CustomArticle;
import com.bcgogo.wx.message.resp.BaseMsg;
import com.bcgogo.wx.message.resp.TextMsg;
import com.bcgogo.wx.message.template.WXMsgTemplate;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-4
 * Time: 上午10:15
 * To change this template use File | Settings | File Templates.
 */
public interface IWXMsgSender {

  String getNewsMessage(String toUserName, String fromUserName, WXArticleDTO... articles) throws IOException;

  TextMsg getTextMessage(String toUserName, String fromUserName, String content);

  String getEmptyMsg();

  String getTextMsgXml(String toUserName,String fromUserName,String content) throws IOException;

  String getTransferCustomMsgXml(BaseMsg baseMsg) throws Exception;

  Result sendMassTextMsg(String publicNo,String msg,String... openIds) throws Exception;

  Result sendMassNewsMsg(String publicNo,String mediaId,String... tousers) throws Exception;

  Result sendMassNewsMsg(String publicNo,String mediaId,String groupId) throws Exception;

   Result sendMassImageMsg(String publicNo,String mediaId,String[] tousers) throws Exception;

  Result sendCustomTextMsg(String publicNo,String touser,String text) throws Exception;

  Result sendCustomTextMsg(Long shopId,String touser,String text) throws Exception;

  Result sendCustomNewsMsg(String publicNo,String touser,CustomArticle article) throws Exception;

  Result sendTemplateMsg(String publicNo,WXMsgTemplate template) throws Exception;


}
