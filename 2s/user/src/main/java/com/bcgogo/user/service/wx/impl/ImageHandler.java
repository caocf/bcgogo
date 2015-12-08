package com.bcgogo.user.service.wx.impl;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.IWXHandler;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXRequestParam;
import com.bcgogo.wx.action.WXActionType;
import com.bcgogo.wx.action.WXUserAction;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

//import com.bcgogo.wx.qr.QRCodeUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-5
 * Time: 下午3:35
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ImageHandler implements IWXHandler {


  @Override
  public String doHandle(WXRequestParam param) throws Exception {
    IWXMsgSender sender= ServiceManager.getService(IWXMsgSender.class);
    String fromUserName =param.getOpenId();
    String toUserName = param.getPublicNo();
    String command=null;//QRCodeUtil.decoderQRCode(requestMap.get("PicUrl"));
    if(WXConstant.CMD_KEY_ADMIN_PASS.equals(command)){
      WXUserAction action=new WXUserAction(WXActionType.COMMAND);
      WXHelper.logLastUserAction(fromUserName, action);
      Map<String,String> adminOptMap= WXConstant.commandMap.get(WXConstant.CMD_OPT_LIST_ADMIN);
      StringBuilder sb=new StringBuilder();
      sb.append("choose your operate\n");
      for(String opt:adminOptMap.keySet()){
              sb.append(opt).append(") ").append(adminOptMap.get(opt)).append("\n");
      }

      return sender.getTextMsgXml(fromUserName, toUserName,sb.toString());
    }
    //根据LastUserAction场景的情况下接受命令
    WXUserAction userAction= WXHelper.getLastUserAction(fromUserName);
    if(userAction!=null){
      return rxSceneImage(toUserName, fromUserName, command, userAction);
    }
    return rxImage(toUserName,fromUserName,command);
  }

  private String rxImage(String toUserName,String fromUserName,String command) throws IOException {
    IWXMsgSender sender= ServiceManager.getService(IWXMsgSender.class);
    return sender.getTextMsgXml(fromUserName, toUserName, command);
  }

  private String rxSceneImage(String toUserName,String fromUserName,String command,WXUserAction userAction) throws Exception {
    return null;
  }

}
