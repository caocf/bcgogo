package com.bcgogo.common;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.IMembersService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebUtil {
  public static final String SIMPLE_JS_ALERT_MESSAGE = "util_simpleJsAlertMessage";

  public static void reThrow(Logger logger, Exception e) throws Exception {
    logger.error(e.getMessage(), e);
    throw new BcgogoException(e);
  }

  private static Object getSessionKey(HttpServletRequest request, String key) {
    return request.getSession(false)==null?null:request.getSession(false).getAttribute(key);
  }

  public static String getBasePath(HttpServletRequest request) {
    String path = request.getContextPath();
    return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
  }

  public static boolean isSessionEmpty(HttpServletRequest request){
    HttpSession session = request.getSession(false);
    return (session == null || !session.getAttributeNames().hasMoreElements() || isSessionElementEmpty(request));
  }

  public static boolean isSessionElementEmpty(HttpServletRequest request) {
    return getShopId(request) == null ||
        getUserId(request) == null ||
        getUserGroupId(request) == null ||
        getUserNo(request) == null ||
        getShopVersion(request) == null;
  }

  public static Long getShopId(HttpServletRequest request) {
    return (Long) getSessionKey(request, "shopId");
  }

   public static String getShopName(HttpServletRequest request) {
    return (String) getSessionKey(request, "shopName");
  }

  public static Long getUserId(HttpServletRequest request) {
    return (Long) getSessionKey(request, "userId");
  }

  public static Long getUserGroupId(HttpServletRequest request) {
    return (Long) getSessionKey(request, "userGroupId");
  }

  public static String getUserName(HttpServletRequest request) {
    return (String) getSessionKey(request, "userName");
  }

  public static String getUserNo(HttpServletRequest request) {
    return (String) getSessionKey(request, "userNo");
  }

  public static String getUserGroupName(HttpServletRequest request) {
    return (String) getSessionKey(request, "userGroupName");
  }

  public static ShopVersionDTO getShopVersion(HttpServletRequest request) {
    return (ShopVersionDTO) getSessionKey(request, "shopVersion");
  }

  public static Long getShopVersionId(HttpServletRequest request) {
    ShopVersionDTO shopVersionDTO = getShopVersion(request);
    return shopVersionDTO == null ? null : shopVersionDTO.getId();
  }

  @Deprecated
  public static String getUserGroupType(HttpServletRequest request) {
    return (String) getSessionKey(request, "userGroupType");
  }

  public static boolean isChinese(char c) {
    return c >= 0x0391 && c <= 0xFFE5;
  }


  public static Long parseLong(String str) {
    if (str == null) return null;
    str = str.trim();
    if (StringUtils.isBlank(str)) return null;
    try {
      return Long.parseLong(str);
    } catch (Exception e) {
      return null;
    }
  }


  public static String toJsonStr(String s) {
    if (StringUtils.isBlank(s) || s.equalsIgnoreCase("null")) {
      return "";
    } else return s;
  }

  public static String toJsonStr(Integer i) {
    if (i == null) {
      return "";
    } else {
      return i.toString();
    }
  }

  public static String toJsonStr(Double d) {
    if (d == null) {
      return "";
    } else {
      return d.toString();
    }
  }

  public static boolean checkMemberStatus(HttpServletRequest request) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    return membersService.isMemberSwitchOn(getShopId(request));
  }

  public static void addSimpleJsMsg(ModelMap model, Result result) {
    model.addAttribute(SIMPLE_JS_ALERT_MESSAGE, result);
  }
}
