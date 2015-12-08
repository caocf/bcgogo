package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IAppPushMessageService;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午5:47
 */
@Controller
public class PushMessageController {
  private static final Logger LOG = LoggerFactory.getLogger(PushMessageController.class);

  /**
   * 消息轮询
   */
  @ResponseBody
  @RequestMapping(value = "/message/polling/types/{types}/userNo/{userNo}",
      method = RequestMethod.GET)
  public ApiResponse getPollingMessage(HttpServletRequest request, HttpServletResponse response, @PathVariable("userNo") String userNo, @PathVariable("types") String... types) throws Exception {
    try {
      Long appUserId = SessionUtil.getAppUserId(request, response);
      if (appUserId == null) {
        return MessageCode.toApiResponse(MessageCode.OBTAIN_MESSAGE_FAIL, ValidateMsg.APP_USER_NOT_EXIST);
      }
      PushMessageType[] typesArray = getPushMessageTypes(types);
      return ServiceManager.getService(IAppPushMessageService.class)
          .getPollingMessage(appUserId, 100, typesArray);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_MESSAGE_EXCEPTION);
    }
  }

  private PushMessageType[] getPushMessageTypes(String[] types) {
    PushMessageType[] typesArray;
    if (types.length == 1 && StringUtil.isEmptyAppGetParameter(types[0])) {
      typesArray = PushMessageType.getAppUserPushMessage();
    } else {
      typesArray = new PushMessageType[types.length];
      int i = 0;
      for (String str : types) {
        typesArray[i++] = PushMessageType.valueOf(str);
      }
    }
    return typesArray;
  }

}
