package com.bcgogo.user.service.wx.impl;

import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.common.Result;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.service.wx.IWXAccountService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.HttpUtils;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.*;
import com.bcgogo.wx.article.CustomArticle;
import com.bcgogo.wx.message.MsgErrorCode;
import com.bcgogo.wx.message.custom.CustomNewsMsg;
import com.bcgogo.wx.message.custom.CustomTextMsg;
import com.bcgogo.wx.message.mass.MassTextMsg;
import com.bcgogo.wx.message.mass.OpenIdMassImage;
import com.bcgogo.wx.message.mass.OpenIdMassNews;
import com.bcgogo.wx.message.resp.BaseMsg;
import com.bcgogo.wx.message.resp.NewsMsg;
import com.bcgogo.wx.message.resp.TextMsg;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import com.bcgogo.wx.user.WXAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-4
 * Time: 上午10:14
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WXMsgSender implements IWXMsgSender {
  public static final Logger LOG = LoggerFactory.getLogger(WXMsgSender.class);

  @Autowired
  private UserDaoManager daoManager;

  /**
   * 生成微信图文消息
   * @param toUserName
   * @param fromUserName
   * @param articles
   * @return
   * @throws java.io.IOException
   */
  public  String getNewsMessage(String toUserName,String fromUserName,WXArticleDTO... articles) throws IOException {
    if(ArrayUtil.isEmpty(articles)){
      return null;
    }
    NewsMsg message = new NewsMsg();
    message.setToUserName(toUserName);
    message.setFromUserName(fromUserName);
    message.setCreateTime(new Date().getTime());
    message.setMsgType(MsgType.news);
    message.setFuncFlag(0);
    message.setArticleCount(String.valueOf(articles.length));
    message.getArticles().addAll(Arrays.asList(articles));
    return WXXMLParse.toNewsMessageXMl(message);
  }

  /**
   * 生成普通文本消息
   * @param toUserName
   * @param fromUserName
   * @param content
   * @return
   * @throws java.io.IOException
   */
  public  TextMsg getTextMessage(String toUserName,String fromUserName,String content) {
    TextMsg textMessage = new TextMsg();
    textMessage.setToUserName(toUserName);
    textMessage.setFromUserName(fromUserName);
    textMessage.setCreateTime(new Date().getTime());
    textMessage.setMsgType(MsgType.text);
    textMessage.setFuncFlag(0);
    textMessage.setContent(content);
    return textMessage;
  }

  /**
   * 假如服务器无法保证在五秒内处理并回复，必须直接回复空串（是指回复一个空字符串，而不是一个XML结构体中content字段的内容为空，请切勿误解），
   * 微信服务器不会对此作任何处理，并且不会发起重试。
   * @return
   */
  @Override
  public  String getEmptyMsg(){
    return "";
  }

  /**
   * 生成普通文本消息xml
   * @param toUserName
   * @param fromUserName
   * @param content
   * @return
   * @throws java.io.IOException
   */
  @Override
  public  String getTextMsgXml(String toUserName,String fromUserName,String content) throws IOException {
    TextMsg textMessage=getTextMessage(toUserName,fromUserName,content);
    return WXXMLParse.toTextMessageXMl(textMessage);
  }

  /**
   * 转发至多客服系统消息的xml
   * @param transferMsg
   * @return
   * @throws IOException
   */
  @Override
  public String getTransferCustomMsgXml(BaseMsg transferMsg) throws IOException {
    return WXXMLParse.getTransferCustomMsgXml(transferMsg);
  }

  /**
   * 高级群发接口。注意用户接收消息数量的限制
   * @param publicNo
   * @param msg
   * @param openIds
   * @return
   * @throws Exception
   */
  @Override
  public Result sendMassTextMsg(String publicNo,String msg,String... openIds) throws Exception{
    Result result=new Result();
    if(ArrayUtil.isEmpty(openIds)){
      return result.LogErrorMsg("illegal param");
    }
    MassTextMsg message=new MassTextMsg(msg,openIds);
    String json= JsonUtil.objectToJson(message);
    String url =WXConstant.URL_SEND_MASS_MSG;
    return sendMsg(publicNo,url,json);
  }

  /**
   * 高级群发接口。根据groupId群发 注意用户接收消息数量的限制
   * @param publicNo
   * @param mediaId
   * @param groupId
   * @return
   * @throws Exception
   */
  @Override
  public Result sendMassNewsMsg(String publicNo,String mediaId,String groupId) throws Exception{
    Result result=new Result();
    if(StringUtil.isEmpty(groupId)){
      return result.LogErrorMsg("illegal param");
    }
    OpenIdMassNews massNews=new OpenIdMassNews(mediaId,groupId);
    String url =WXConstant.URL_SEND_GROUP_MASS_MSG;
    return sendMsg(publicNo,url,JsonUtil.objectToJson(massNews));
  }

  /**
   * 高级群发接口。根据openId列表群发
   * @param publicNo
   * @param mediaId   用于群发的消息的media_id
   * @param tousers
   * @return
   * @throws Exception
   */
  @Override
  public Result sendMassNewsMsg(String publicNo,String mediaId,String[] tousers) throws Exception{
    Result result=new Result();
    if(ArrayUtil.isEmpty(tousers)){
      return result.LogErrorMsg("illegal param");
    }
    OpenIdMassNews massNews=new OpenIdMassNews(mediaId,tousers);
    String url =WXConstant.URL_SEND_MASS_MSG;
    return sendMsg(publicNo,url,JsonUtil.objectToJson(massNews));
  }

  /**
   * 群发图片消息。根据openId列表群发
   * @param publicNo
   * @param mediaId   用于群发的消息的media_id
   * @param tousers
   * @return
   * @throws Exception
   */
  @Override
  public Result sendMassImageMsg(String publicNo,String mediaId,String[] tousers) throws Exception{
    Result result=new Result();
    if(ArrayUtil.isEmpty(tousers)){
      return result.LogErrorMsg("illegal param");
    }
    OpenIdMassImage massImage=new OpenIdMassImage(mediaId,tousers);
    String url =WXConstant.URL_SEND_MASS_MSG;
    return sendMsg(publicNo,url,JsonUtil.objectToJson(massImage));
  }

  public Result sendCustomTextMsg(Long shopId,String touser,String text) throws Exception {
    IWXAccountService accountService= ServiceManager.getService(WXAccountService.class);
    WXAccountDTO accountDTO=accountService.getDecryptedWXAccountByShopId(shopId);
    if(accountDTO==null) {
      return new Result("公共号信息异常",false);
    }
    return sendCustomTextMsg(accountDTO.getPublicNo(),touser,text);
  }

  /**
   * 发送客服消息(文本)
   * @param publicNo
   * @param touser
   * @param text
   * @return
   * @throws Exception
   */
  public Result sendCustomTextMsg(String publicNo,String touser,String text) throws Exception {
    CustomTextMsg textMsg=new CustomTextMsg(touser,text);
    String json= JsonUtil.objectCHToJson(textMsg);
    String url =WXConstant.URL_SEND_CUSTOM_MSG;
    return sendMsg(publicNo,url,json);
  }

  /**
   * 发送客服消息(图文)
   * @param publicNo
   * @param touser
   * @param article
   * @return
   * @throws Exception
   */
  @Override
  public Result sendCustomNewsMsg(String publicNo,String touser,CustomArticle article) throws Exception {
    Result result=new Result();
    if(StringUtil.isEmpty(touser)||article==null) {
      return result.LogErrorMsg("illegal param");
    }
    CustomNewsMsg newsMsg=new CustomNewsMsg(touser,article);
    String json=JsonUtil.objectCHToJson(newsMsg);
    String url =WXConstant.URL_SEND_CUSTOM_MSG;
    result=sendMsg(publicNo,url,json);
    return result;
  }

  /**
   * 发送模版消息
   * @param template
   * @return
   * @throws Exception
   */
  public Result sendTemplateMsg(String publicNo,WXMsgTemplate template) throws Exception {
    String toUrl = WXConstant.URL_SEND_TEMPLATE_MSG;
    String json=JsonUtil.objectCHToJson(template);
    return sendMsg(publicNo,toUrl,json);
  }

  private Result sendMsg(String publicNo,String toUrl,String json) throws Exception {
    Result result=doSendMsg(publicNo,toUrl,json);
    if(result.isSuccess()) return result;
    //clear mem，and try again
    if("invalid credential".equals(result.getMsg())){
      LOG.warn("wx:invalid credential,clear memcached expire access_token ，and try again");
      WXHelper.clearMemKeyAccessToken(publicNo);
      return doSendMsg(publicNo,toUrl,json);
    }else if("require subscribe".equals(result.getMsg())){
      LOG.error("wx:require subscribe");
      //TODO handle
    }
    return result;
  }

  /**
   * send custom msg,custom msg,or mass msg
   * @param publicNo
   * @param toUrl
   * @param json
   * @return
   * @throws Exception
   */
  private Result doSendMsg(String publicNo,String toUrl,String json) throws Exception {
    Result result=new Result();
    //获取access_token
    String accessToken= WXHelper.getAccessTokenByPublicNo(publicNo);
    if(StringUtil.isEmpty(accessToken)){
      return result.LogErrorMsg("can't get access_token");
    }
    toUrl=toUrl.replace("{ACCESS_TOKEN}",accessToken);
    LOG.info("wx:send msg,content json is {}",json);
    HttpResponse response= HttpUtils.sendPost(toUrl,json);
    MsgErrorCode errCode= JsonUtil.jsonToObj(response.getContent(), MsgErrorCode.class);
    if(ErrCode.SUCCESS.equals(errCode.getErrcode())){
      result.setMsg(true,"sent msg success");
      result.setData(StringUtil.isNotEmpty(errCode.getMsg_id())?errCode.getMsg_id():errCode.getMsgid());
      return result;
    }else {
      LOG.error("wx;send msg failed ,errMsg is {}",errCode.getErrmsg());
      return result.LogErrorMsg(errCode.getErrmsg());
    }
  }

}
