package com.bcgogo.filter;

import com.bcgogo.common.WebUtil;
import org.slf4j.Logger;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-7-1
 * Time: 上午10:56
 */
public abstract class AbstractFilter implements Filter {
  public boolean isNotLoginOrTimeout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return session == null || !session.getAttributeNames().hasMoreElements() || WebUtil.isSessionElementEmpty(request);
  }

  public void logRequestParams(HttpServletRequest request, UUID uuid, Logger LOG) {
    try {
      if (request.getSession(false) != null && request.getSession(false).getAttributeNames().hasMoreElements()) {
        Object[] argArray = {uuid.toString(), (Long) request.getSession().getAttribute("userId"), (Long) request.getSession().getAttribute("shopId")};
        LOG.debug("标识ID: {}. 请求userId: {}, shopId: {}", argArray);
      }
      StringBuilder sb = new StringBuilder();
      String method = "";
      Enumeration<String> pNames = request.getParameterNames();
      while (pNames.hasMoreElements()) {
        String pName = pNames.nextElement();
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
      LOG.debug("标识ID: {}. 请求URL: {}", uuid.toString(), request.getRequestURL() + "?" + method);
      LOG.debug("标识ID: {}. 请求参数: {}", uuid.toString(), sb.substring(0, sb.length() > 1 ? sb.length() - 2 : sb.length()));
      logBrowserInfo(request, uuid, LOG);
    } catch (Exception e) {
      LOG.warn("Log请求参数时出错.", e);
    }
  }

  private void logBrowserInfo(HttpServletRequest request, UUID uuid, Logger LOG) {
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("IP地址=").append(request.getRemoteAddr()).append(":").append(request.getServerPort()).append(", ")
          .append("客户端用户名=").append(request.getRemoteHost()).append(", ");
      Enumeration<String> hNames = request.getHeaderNames();
      while (hNames.hasMoreElements()) {
        String hName = hNames.nextElement();
        String hValue = request.getHeader(hName);
        sb.append(hName).append('=').append(hValue).append(", ");
      }
      LOG.debug("标识ID: {}. 请求user浏览器详细信息:[{}]", uuid.toString(), sb.substring(0, sb.length() > 1 ? sb.length() - 2 : sb.length()));
    } catch (Exception e) {
      LOG.warn("Log请求参数时出错.", e);
    }
  }
}
