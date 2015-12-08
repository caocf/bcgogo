package com.bcgogo.user.service.wx;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.impl.ImageHandler;
import com.bcgogo.user.service.wx.impl.TextMsgHandler;
import com.bcgogo.user.service.wx.impl.VoiceMsgHandler;
import com.bcgogo.user.service.wx.impl.WXEventHandler;
import com.bcgogo.wx.MsgType;
import com.bcgogo.wx.WXAccountType;
import com.bcgogo.wx.WXRequestParam;
import com.bcgogo.wx.user.WXAccountDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-19
 * Time: 上午12:05
 * To change this template use File | Settings | File Templates.
 */
public class WXHandlerFactory {


  public static IWXHandler getHandler(WXRequestParam param) throws Exception {
    String publicNo = param.getPublicNo();
    MsgType msgType = param.getMsgType();
    if (msgType == null) return null;
    WXAccountDTO accountDTO = ServiceManager.getService(IWXAccountService.class).getCachedWXAccount(publicNo);
    if (WXAccountType.YIFA.equals(accountDTO.getAccountType())) {
      switch (msgType) {
        case text:
          return ServiceManager.getService(TextMsgHandler.class);
        case image:
          return ServiceManager.getService(ImageHandler.class);
        case event:
          return ServiceManager.getService(WXEventHandler.class);
        case video:
          return ServiceManager.getService(VoiceMsgHandler.class);
        default:
          return null;
      }
    } else if (WXAccountType.MIRROR.equals(accountDTO.getAccountType())) {
      return ServiceManager.getService(WXMirrorEventHandler.class);
    }
    return null;
  }

}
