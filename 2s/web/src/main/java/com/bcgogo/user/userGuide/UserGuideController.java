package com.bcgogo.user.userGuide;

import com.bcgogo.common.CookieUtil;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.user.userGuide.UserGuideCookieName;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.IUserGuideService;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-3-1
 * Time: 上午9:45
 */
@Controller
@RequestMapping("/guide.do")
public class UserGuideController {
  private static final Logger LOG = LoggerFactory.getLogger(UserGuideController.class);

  @ResponseBody
  @RequestMapping(params = "method=skipUserGuideFlow")
  public Object skipUserGuideFlow(HttpServletRequest request, String flowName) {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    Result result;
    if (StringUtil.isEmpty(flowName)) return new Result("跳出指引失败", "引导名为空", false);
    try {
      return userGuideService.skipUserGuideFlow(WebUtil.getShopVersionId(request), WebUtil.getUserId(request), flowName);
    } catch (Exception e) {
      result = new Result(false);
      LOG.info("/user/guide.do");
      LOG.info("method=skipUserGuideFlow");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  //cookie stepName 置为空
  @ResponseBody
  @RequestMapping(params = "method=updateCurrentUserGuideFlowFinished")
  public Object updateCurrentUserGuideFlowFinished(HttpServletRequest request,HttpServletResponse response,  String flowName) {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    Result result;
    if (StringUtil.isEmpty(flowName)) return new Result("跳出指引失败", "引导名为空", false);
    try {
      userGuideService.updateCurrentUserGuideFlowFinished(WebUtil.getUserId(request), flowName);
      Cookie cookie = new Cookie(UserGuideCookieName.currentStepName.name(), null);
      response.addCookie(cookie);
      CookieUtil.rebuildCookiesForUserGuide(request, response, false);
      result = new Result(true);
    } catch (Exception e) {
      result = new Result(false);
      LOG.info("/user/guide.do");
      LOG.info("method=skipUserGuideFlow");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @ResponseBody
  @RequestMapping(params = "method=nextUserGuideStep")
  public Object nextUserGuideStep(HttpServletRequest request, HttpServletResponse response, String flowName) {
    Result result = new Result(true);
    try {
      CookieUtil.rebuildCookiesForUserGuide(request, response, true);
    } catch (Exception e) {
      result = new Result(false);
      LOG.info("/user/guide.do");
      LOG.info("method=skipUserGuideFlow");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  //以后不再提醒
  @ResponseBody
  @RequestMapping(params = "method=notRemind")
  public Object notRemind(HttpServletRequest request, HttpServletResponse response, String flowName) {
    Result result = new Result(true);
    try {
      IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
      userGuideService.notRemind(WebUtil.getShopId(request), WebUtil.getUserId(request), flowName);
    } catch (Exception e) {
      result = new Result(false);
      LOG.info("/user/guide.do");
      LOG.info("method=notRemind");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

}
