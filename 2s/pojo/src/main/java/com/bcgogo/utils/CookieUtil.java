package com.bcgogo.utils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CookieUtil {
  public static final Logger LOG = LoggerFactory.getLogger(CookieUtil.class);   //slf4j 日志
  private static final String SESSION_ID_POSTFIX = ".app";
  private static final String COOKIE_NAME_SESSION = "JSESSIONID";
  private static final int SESSION_MAX_AGE = 60 * 60 * 24 * 365; //365年

  public static String getSessionId(HttpServletRequest request) {
    return CookieUtil.getCookieByName(request, COOKIE_NAME_SESSION);
  }

  public static void setSessionId(HttpServletResponse response, String sessionId) {
    CookieUtil.setCookie(response, COOKIE_NAME_SESSION, sessionId, SESSION_MAX_AGE);
  }

  public static void removeSessionId(HttpServletResponse response) {
    CookieUtil.setCookie(response, COOKIE_NAME_SESSION, null, 0);
  }


  /**
   * 根据name和cookie获得value
   *
   * @param name cookies name
   * @return cookies value
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
   * @param cookies Cookie[]
   * @return Map
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
   * 生成新的授权码
   *
   * @return sessionId
   */
  public static String genPermissionKey() {
    return RandomUtils.randomAlphanumeric(8) + System.currentTimeMillis()
        + RandomUtils.randomAlphanumeric(8) + SESSION_ID_POSTFIX;
  }

  public static Long getSessionIdCreatedTime(String sessionId) {
    if (StringUtils.isBlank(sessionId) || sessionId.length() < 30) return 0L;
    return Long.valueOf(sessionId.substring(8, 21));
  }

  /**
   * 设置cookie内容
   *
   * @param key    键
   * @param value  值
   * @param maxAge 设置Cookie的有效时间，以秒作为单位
   */
  public static void setCookie(HttpServletResponse response, String key, String value, int maxAge) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

}
