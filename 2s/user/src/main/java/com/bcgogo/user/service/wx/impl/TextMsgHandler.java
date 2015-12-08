package com.bcgogo.user.service.wx.impl;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.IWXHandler;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWeChatStatService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.wx.*;
import com.bcgogo.wx.action.WXActionName;
import com.bcgogo.wx.action.WXActionType;
import com.bcgogo.wx.action.WXUserAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 接受普通文本消息
 * User: ndong
 * Date: 14-8-18
 * Time: 下午11:59
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TextMsgHandler implements IWXHandler {
  public static final Logger LOG = LoggerFactory.getLogger(TextMsgHandler.class);

  @Override
  public String doHandle(WXRequestParam param) throws Exception {
    String openId = param.getOpenId();
    String publicNo = param.getPublicNo();
    String content = param.getContent();
    //根据LastUserAction场景的情况下接受命令
    WXUserAction userAction = WXHelper.getLastUserAction(openId);
    if (userAction != null) {
      return rxSceneTextMsc(publicNo, openId, content, userAction);
    }
    //没有场景的情况下接受命令
    return rxTextMsc(param);
  }

  /**
   * 响应普通消息
   *
   * @param param
   * @return
   * @throws Exception
   */
  private String rxTextMsc(WXRequestParam param) throws Exception {
    String content = param.getContent();
    String publicNo = param.getPublicNo();
    String openId = param.getOpenId();
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    WXCommand command = WXCommand.getCommand(content);
    if (command == null) {
//      sender.sendCustomTextMsg(publicNo,openId,WXConstant.CONTENT_DEFAULT_REPLY);
      //转到客服系统
      return sender.getTransferCustomMsgXml(param.toBaseMsg());
    }
    //关键字回复
    switch (command) {
      case ADMIN:
        WXUserAction action = new WXUserAction(WXActionType.ADMIN_OPERATE, WXActionName.LOGIN);
        WXHelper.logLastUserAction(openId, action);
        return sender.getTextMsgXml(openId, publicNo, "enter your pass");
      default:
        return sender.getEmptyMsg();
    }

  }

  /**
   * 根据LastUserAction,在场景的情况下接受命令
   *
   * @param publicNo
   * @param openId
   * @param content
   * @param userAction
   * @return
   * @throws Exception
   */
  private String rxSceneTextMsc(String publicNo, String openId, String content, WXUserAction userAction) throws Exception {
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    WXActionName actionName = userAction.getActionName();
    if (actionName == null) return sender.getEmptyMsg();
    if (WXActionType.ADMIN_OPERATE.equals(userAction.getActionType())) {
      switch (actionName) {
        case LOGIN:
          return validateLogin(publicNo, openId, content);
        case AUTHORIZED:
          return doAuthorizedCommand(publicNo, openId, content);
      }
    }
    return "i don't understand";
  }

  private String doAuthorizedCommand(String publicNo, String openId, String content) throws IOException {
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    IWeChatStatService statService = ServiceManager.getService(IWeChatStatService.class);
    //根据不同指令执行操作
    if ("1".equals(content)) {
      return sender.getTextMsgXml(openId, publicNo, statService.getWXAccountStatStr());
//      return sender.getTextMsgXml(openId, publicNo,"good");
    }
    return "i don't understand";
  }

  /**
   * @param publicNo
   * @param openId
   * @param content
   * @return
   * @throws IOException
   */
  private String validateLogin(String publicNo, String openId, String content) throws IOException {
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    String reply = "";
    if ("123".equals(content)) {
      reply = "1)stat wx_user number\n2)sssssss";
      WXUserAction action = new WXUserAction(WXActionType.ADMIN_OPERATE, WXActionName.AUTHORIZED);
      WXHelper.logLastUserAction(openId, action);
    } else {
      reply = "permission denied, please try again.";
    }
    return sender.getTextMsgXml(openId, publicNo, reply);
  }

}
