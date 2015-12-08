package com.bcgogo.util;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.IMembersService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {

  public static void reThrow(Logger logger, Exception e) throws Exception {
    logger.error(e.getMessage(), e);
    throw new BcgogoException(e);
  }

  private static Object getSession(HttpServletRequest request, String key) {
    return request.getSession().getAttribute(key);
    }

  public static Long getShopId(HttpServletRequest request) {
    return (Long) getSession(request, "shopId");
  }

  public static Long getUserId(HttpServletRequest request) {
    return (Long) getSession(request, "userId");
  }

  public static Long getUserGroupId(HttpServletRequest request) {
    return (Long) getSession(request, "userGroupId");
  }

  public static String getUserName(HttpServletRequest request) {
    return (String) getSession(request, "userName");
  }

  public static String getUserGroupName(HttpServletRequest request) {
    return (String) getSession(request, "userGroupName");
  }

  public static ShopVersionDTO getShopVersion(HttpServletRequest request) {
    return (ShopVersionDTO) getSession(request, "shopVersion");
  }

  public static Long getShopVersionId(HttpServletRequest request) {
    ShopVersionDTO shopVersionDTO = getShopVersion(request);
    return shopVersionDTO == null ? null : shopVersionDTO.getId();
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

  public static Double parseDouble(String str) {
    if (str == null) return null;
    str = str.trim();
    if (StringUtils.isBlank(str)) return null;
    try {
      return Double.parseDouble(str);
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
}
