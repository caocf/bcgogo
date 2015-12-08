package com.bcgogo.common;

import com.bcgogo.BooleanEnum;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.user.userGuide.Status;
import com.bcgogo.enums.user.userGuide.UserGuideCookieName;
import com.bcgogo.notification.service.IReminderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.cache.UserGuideCacheManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.userGuide.UserGuideHandler;
import com.bcgogo.utils.JsonUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * cookie操作工具
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-30
 * Time: 上午10:44
 */
public class CookieUtil {

  public static final Logger LOG = LoggerFactory.getLogger(CookieUtil.class);   //Log4j 日志

  /**
   * 根据name和cookie获得value
   *
   * @param request
   * @param name
   * @return
   */
  public static String getCookieByName(HttpServletRequest request, String name) {
    if (request == null) {
      return null;
    }
    Cookie[] cookies = request.getCookies();
    Cookie cookie = CookieUtil.getCookieMapByCookies(cookies).get(name);
    return cookie == null ? null : cookie.getValue();
  }


  /**
   * 将cookie封装到Map里面
   *
   * @param cookies
   * @return
   */
  public static Map<String, Cookie> getCookieMapByCookies(Cookie[] cookies) {
    Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
    if (ArrayUtils.isEmpty(cookies)) {
      return cookieMap;
    }
    for (Cookie cookie : cookies) {
      cookieMap.put(cookie.getName(), cookie);
    }
    return cookieMap;
  }

  /**
   * 根据request中的cookie内容获取当前步骤并设置下一步到cookie中
   *
   * @param request
   * @param response
   * @param needNextStep
   * @param currentStepScene
   */
  public static void rebuildCookiesForUserGuide(HttpServletRequest request, HttpServletResponse response, boolean needNextStep, String... currentStepScene) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    //开关
    String userGuideConfig = configService.getConfig("userGuideConfig", -1L);
    if (!"ON".equals(userGuideConfig)) return;
    //Cookies 是否为空
    if (ArrayUtils.isEmpty(request.getCookies())) return;
    //是否保持当前step 不继续下一个step
    if (isKeepCurrentStep(request, response)) return;
    String flowName = CookieUtil.getCookieByName(request, UserGuideCookieName.currentFlowName.name());
    String stepName = CookieUtil.getCookieByName(request, UserGuideCookieName.currentStepName.name());
    String hasUserGuide = CookieUtil.getCookieByName(request, UserGuideCookieName.hasUserGuide.name());
    String excludeFlowName = CookieUtil.getCookieByName(request, UserGuideCookieName.excludeFlowName.name());
    //是否为用户指引用户
    if (!"YES".equals(hasUserGuide)) return;
    //当前step 是否为指定step场景
    if (!isCurrentScene(stepName, currentStepScene)) return;
    UserGuideDTO userGuideDTO;
    UserGuideHandler handler = ServiceManager.getService(UserGuideHandler.class);
    if (needNextStep && flowName != null && stepName != null) {
      userGuideDTO = handler.getNextStepByShopIdStepName(WebUtil.getShopVersionId(request), WebUtil.getShopId(request),
          WebUtil.getUserId(request), flowName, stepName, StringUtil.isEmpty(excludeFlowName) ? null : URLDecoder.decode(excludeFlowName, "utf-8").split(","));
      if (userGuideDTO != null) {
        userGuideDTO.setCurrentStep(userGuideDTO.getNextStep());
      }
    } else {
      if (StringUtils.isNotBlank(stepName)) return;
      userGuideDTO = handler.getHandledCurrentUserGuideStep(WebUtil.getShopVersionId(request), WebUtil.getShopId(request),
          WebUtil.getUserId(request), StringUtil.isEmpty(excludeFlowName) ? null : URLDecoder.decode(excludeFlowName, "utf-8").split(","));
      if (userGuideDTO != null) {
        userGuideDTO.setContinueGuide(true);
      }
    }
    setCookies(response, userGuideDTO);
  }

  private static boolean isKeepCurrentStep(HttpServletRequest request, HttpServletResponse response) {
    String keepCurrentStep = CookieUtil.getCookieByName(request, UserGuideCookieName.keepCurrentStep.name());
    if ("YES".equals(keepCurrentStep)) {
      Cookie newCookie = new Cookie(UserGuideCookieName.keepCurrentStep.name(), "NO");
      newCookie.setMaxAge(-1);
      response.addCookie(newCookie);
      return true;
    }
    return false;
  }

  public static void loginSetCookies(HttpServletRequest request, HttpServletResponse response, long userId, long shopId, long shopVersionId, long userGroupId) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    String userGuideConfig = configService.getConfig("userGuideConfig", -1L);
    //新手指引
    if ("ON".equals(userGuideConfig)) {
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserId(userId);
      if (YesNo.YES.equals(userDTO.getHasUserGuide()) && YesNo.NO.equals(userDTO.getFinishUserGuide())) {
        UserGuideCacheManager.unitTest();
        UserGuideHandler handler = ServiceManager.getService(UserGuideHandler.class);
        UserGuideDTO userGuideDTO = handler.getHandledCurrentUserGuideStep(shopVersionId, shopId, userId);
        if (userGuideDTO != null && userGuideDTO.getCurrentStep() != null) {
          UserGuideStepDTO userGuideStepDTO = userGuideDTO.getCurrentStep();
          Cookie cookie = new Cookie(UserGuideCookieName.currentStepName.name(), userGuideStepDTO.getName());
          response.addCookie(cookie);
          cookie = new Cookie(UserGuideCookieName.currentFlowName.name(), userGuideStepDTO.getFlowName());
          response.addCookie(cookie);
          cookie = new Cookie(UserGuideCookieName.currentStepStatus.name(), Status.WAITING.name());
          response.addCookie(cookie);
          cookie = new Cookie(UserGuideCookieName.nextStepName.name(), userGuideStepDTO.getNextStep());
          response.addCookie(cookie);
          if (userGuideStepDTO.getHead() != BooleanEnum.TRUE) {
            response.addCookie(new Cookie(UserGuideCookieName.isContinueGuide.name(), YesNo.YES.name()));
          } else {
            response.addCookie(new Cookie(UserGuideCookieName.isContinueGuide.name(), YesNo.NO.name()));
          }
          response.addCookie(new Cookie(UserGuideCookieName.hasUserGuide.name(), YesNo.YES.name()));
          response.addCookie(new Cookie(UserGuideCookieName.currentStepIsHead.name(), userGuideStepDTO.getHead().name()));
          if (StringUtils.isNotBlank(userGuideStepDTO.getUrl())) {
            response.addCookie(new Cookie(UserGuideCookieName.url.name(), URLEncoder.encode(userGuideStepDTO.getUrl(), "UTF-8")));
          }

        }
      }
      //试用提醒
      response.addCookie(new Cookie(UserGuideCookieName.hasTrialReminder.name(),
          JsonUtil.mapToJson(ServiceManager.getService(IReminderService.class).isTrialExpired(userDTO.getShopId()))));
    }
  }

  private static void setCookies(HttpServletResponse response, UserGuideDTO userGuideDTO) throws UnsupportedEncodingException {
    if (userGuideDTO != null && userGuideDTO.getCurrentFlow() != null && userGuideDTO.getCurrentStep() != null) {
      response.addCookie(new Cookie(UserGuideCookieName.currentFlowName.name(), userGuideDTO.getCurrentStep().getFlowName()));
      response.addCookie(new Cookie(UserGuideCookieName.currentStepName.name(), userGuideDTO.getCurrentStep().getName()));
      response.addCookie(new Cookie(UserGuideCookieName.currentStepIsHead.name(), userGuideDTO.getCurrentStep().getHead().name()));
      if (StringUtils.isNotBlank(userGuideDTO.getCurrentStep().getUrl())) {
        response.addCookie(new Cookie(UserGuideCookieName.url.name(), URLEncoder.encode(userGuideDTO.getCurrentStep().getUrl(), "UTF-8")));
      }
      if (userGuideDTO.isContinueGuide() && userGuideDTO.getCurrentStep().getHead() != BooleanEnum.TRUE) {
        response.addCookie(new Cookie(UserGuideCookieName.isContinueGuide.name(), YesNo.YES.name()));
      } else {
        response.addCookie(new Cookie(UserGuideCookieName.isContinueGuide.name(), YesNo.NO.name()));
      }
      response.addCookie(new Cookie(UserGuideCookieName.hasUserGuide.name(), YesNo.YES.name()));
    } else {
      Cookie newCookie = new Cookie(UserGuideCookieName.currentFlowName.name(), null);
      newCookie.setMaxAge(-1);
      response.addCookie(newCookie);
      newCookie = new Cookie(UserGuideCookieName.currentStepName.name(), null);
      newCookie.setMaxAge(-1);
      response.addCookie(newCookie);
      newCookie = new Cookie(UserGuideCookieName.currentStepIsHead.name(), null);
      newCookie.setMaxAge(-1);
      response.addCookie(newCookie);
      newCookie = new Cookie(UserGuideCookieName.url.name(), null);
      newCookie.setMaxAge(-1);
      response.addCookie(newCookie);
      newCookie = new Cookie(UserGuideCookieName.currentStepStatus.name(), null);
      newCookie.setMaxAge(-1);
      response.addCookie(newCookie);
      newCookie = new Cookie(UserGuideCookieName.isContinueGuide.name(), null);
      newCookie.setMaxAge(-1);
      response.addCookie(newCookie);
      newCookie = new Cookie(UserGuideCookieName.keepCurrentStep.name(), null);
      newCookie.setMaxAge(-1);
      response.addCookie(newCookie);
    }
  }

  private static boolean isCurrentScene(String stepName, String... currentStepScene) {
    boolean isCurrentScene = false;
    if (!ArrayUtils.isEmpty(currentStepScene) && stepName != null) {
      for (String scene : currentStepScene) {
        if (stepName.equals(scene)) {
          isCurrentScene = true;
          break;
        }
      }
    } else {
      isCurrentScene = true;
    }
    return isCurrentScene;
  }

}
