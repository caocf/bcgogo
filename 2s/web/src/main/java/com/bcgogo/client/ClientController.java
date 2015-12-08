
package com.bcgogo.client;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.client.FeedbackType;
import com.bcgogo.enums.client.RecommendScene;
import com.bcgogo.security.BCClientLoginHandler;
import com.bcgogo.txn.service.client.IClientFeedbackService;
import com.bcgogo.txn.service.client.IClientPromptMsgSelector;
import com.bcgogo.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.UUID;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午1:23
 * 客户端对外接口
 */
@Controller
@RequestMapping("/client")
public class ClientController {
  private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);
  @Autowired
  private BCClientLoginHandler loginHandler;

  public void setLoginHandler(BCClientLoginHandler loginHandler) {
    this.loginHandler = loginHandler;
  }

  @Autowired
  private IClientPromptMsgSelector clientPromptMsgAggregator;
  @Autowired
  private ClientAssortedMessageGenerator clientAssortedMessageGenerator;
  @Autowired
  private IClientFeedbackService clientFeedbackService;


  /**
   * 当前业务场景，checkUpdate表示校验当前客户端是否需要升级（必选）；
   *
   * @param sessionId    当前登录回话ID（设计预留）；
   * @param shopId       当前店铺ID（可选）；
   * @param userNo       当前登录账号（可选）；
   * @param localVersion 当前客户端版本号（必选）；
   * @param apiVersion   接口版本号，客户端安装时设定；
   * @return ClientVersionCheckResult
   */
  @RequestMapping(params = "method=checkUpdate")
  @ResponseBody
  public ClientVersionCheckResult checkUpdate
  (HttpServletRequest request, Long sessionId, Long shopId, String userNo, String localVersion, String apiVersion) {
    ClientVersionCheckResult result = new ClientVersionCheckResult(shopId, userNo, localVersion, false);
    try {
      logRequestParams(request, UUID.randomUUID());
      String config = ConfigUtils.getClientCurrentVersion();
      if (!config.equals(result.getLocalVersion())) {
        result.setNeedUpdate(true);
        result.setUpdateUrl(ConfigUtils.getClientUpdateUrl());
      }
    } catch (Exception e) {
      LOG.debug("method=checkUpdate");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  /**
   * 校验当前火狐浏览器是否需要升级
   *
   * @param sessionId    当前登录回话ID（设计预留）
   * @param shopId       当前店铺ID（可选）
   * @param userNo       当前登录账号（可选）
   * @param localVersion 当前火狐浏览器版本号（必选）,本地未安装时值为空
   * @param apiVersion   接口版本号，客户端安装时设定
   * @return FirefoxVersionCheckResult
   */
  @RequestMapping(params = "method=checkFirefoxUpdate")
  @ResponseBody
  public FirefoxVersionCheckResult checkFirefoxUpdate
  (HttpServletRequest request, Long sessionId, Long shopId, String userNo, String localVersion, String apiVersion) {
    FirefoxVersionCheckResult result = new FirefoxVersionCheckResult(shopId, userNo, localVersion, false);
    try {
      logRequestParams(request, UUID.randomUUID());
      String config = ConfigUtils.getFirefoxCurrentVersion();
      if (!config.equals(result.getLocalVersion())) {
        result.setNeedUpdate(true);
        result.setUpdateUrl(ConfigUtils.getFirefoxUpdateUrl());
      }
    } catch (Exception e) {
      LOG.debug("method=checkFirefoxUpdate");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=login")
  @ResponseBody
  public ClientLoginResult login(HttpServletRequest request, String password, String userNo, String apiVersion, String MAC) {
    ClientLoginResult result = new ClientLoginResult(false, userNo);
    try {
      logRequestParams(request, UUID.randomUUID());
      return loginHandler.login(password, userNo, apiVersion, MAC);
    } catch (Exception e) {
      LOG.debug("method=login");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=logout")
  @ResponseBody
  public ClientLogoutResult logout(HttpServletRequest request, String userNo) {
    ClientLogoutResult result = new ClientLogoutResult(false);
    try {
      logRequestParams(request, UUID.randomUUID());
      return loginHandler.logout(userNo);
    } catch (Exception e) {
      LOG.debug("method=login");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  /**
   * 从服务端获取推送消息列表
   *
   * @param sessionId  当前登录回话ID（设计预留）
   * @param shopId     当前店铺ID，登录状态不为空，非登录状态为空
   * @param userNo     当前登录账号，登录状态不为空，非登录状态不为空
   * @param apiVersion 接口版本号，客户端安装时设定
   * @return List<ClientAssortedMessage>
   */
  @RequestMapping(params = "method=getMessages")
  @ResponseBody
  public ClientAssortedMessageResult  getMessages
  (HttpServletRequest request, Long sessionId, Long shopId, String userNo, String apiVersion) {
    logRequestParams(request, UUID.randomUUID());
    return clientAssortedMessageGenerator.getMessages(request, sessionId, shopId, userNo, apiVersion);
  }


  /**
   * 从服务端获取弹窗提示
   *
   * @param sessionId  当前登录回话ID（设计预留）
   * @param shopId     当前店铺ID，登录状态不为空，非登录状态为空
   * @param userNo     当前登录账号，登录状态不为空，非登录状态不为空
   * @param apiVersion 接口版本号，客户端安装时设定
   * @return ClientPrompt
   */
  @RequestMapping(params = "method=getPrompt")
  @ResponseBody
  public Object getPrompt
  (HttpServletRequest request, Long sessionId, Long shopId, String userNo, String apiVersion) {
    try {
      logRequestParams(request, UUID.randomUUID());
      if (!ConfigUtils.isPushMessageSwitchOn()) {
          LOG.info("pushMessageSwitch if off");
//        return JsonUtil.EMPTY_JSON_STRING;
          return "{\"nextRequestTime\":600000}";
      }
      ClientPrompt clientPrompt = clientPromptMsgAggregator.getPrompt(WebUtil.getBasePath(request), shopId, apiVersion, userNo);
      if (clientPrompt == null) {
        return JsonUtil.EMPTY_JSON_STRING;
      }
      return clientPrompt;
    } catch (Exception e) {
      LOG.debug("method=getPrompt");
      LOG.error(e.getMessage(), e);
      return JsonUtil.EMPTY_JSON_STRING;
    }
  }




  @RequestMapping(params = "method=feedbackUserAction")
  @ResponseBody
  public FeedbackResult feedbackUserAction
      (HttpServletRequest request, Long sessionId, Long shopId, String userNo, RecommendScene recommendScene,
       String recommendId, FeedbackType feedbackType, String apiVersion) {
    try {
      logRequestParams(request, UUID.randomUUID());
      return clientFeedbackService.feedbackUserAction(shopId, userNo, recommendScene, recommendId, feedbackType);
    } catch (Exception e) {
      LOG.debug("method=feedbackUserAction");
      LOG.error(e.getMessage(), e);
      return new FeedbackResult("false");
    }
  }

  private void logRequestParams(HttpServletRequest request, UUID uuid) {
    try {
      StringBuilder sb = new StringBuilder();
      String method = "";
      Enumeration pNames = request.getParameterNames();
      while (pNames.hasMoreElements()) {
        String pName = (String) pNames.nextElement();
        String pValues[] = request.getParameterValues(pName);
        StringBuilder result = new StringBuilder(pName);
        result.append('=');
        for (int i = 0; i < pValues.length; i++) {
          if (i > 0)
            result.append(", ");
          result.append(pValues[i]);
        }
        if (pName.equals("method")) {
          method = result.toString();
          continue;
        }
        sb.append(result).append(", ");
      }
      LOG.debug("标识ID: cookies :{}", uuid.toString(),JsonUtil.objectToJson(request.getCookies()));
      LOG.debug("标识ID: {}. 请求URL: {}", uuid.toString(), request.getRequestURL() + "?" + method);
      LOG.debug("标识ID: {}. 请求参数: {}", uuid.toString(), sb.substring(0, sb.length() > 1 ? sb.length() - 2 : sb.length()));
    } catch (Exception e) {
      LOG.warn("Log请求参数时出错.", e);
    }
  }



}
