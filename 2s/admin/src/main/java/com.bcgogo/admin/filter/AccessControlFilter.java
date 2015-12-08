package com.bcgogo.admin.filter;

import com.bcgogo.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-14
 * Time: 下午5:55
 * 权限 filter
 */
public class AccessControlFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(AccessControlFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }
//  private static String getResourceValue(HttpServletRequest request) {
//    return request.getRequestURI() + "?method=" + request.getParameter("method");
//  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    if (servletRequest instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      HttpSession session = request.getSession(false);
      UUID uuid = UUID.randomUUID();
      logRequestParams(request, uuid);
      if (session == null || !session.getAttributeNames().hasMoreElements() || session.getAttribute("shopId") == null || WebUtil.getShopVersion(request) == null || WebUtil.getShopVersion(request).getId() == null) {
        if ((request.getRequestURI() + "?method=" + request.getParameter("method")).contains("view.do?method=index")) {
          response.sendRedirect(request.getContextPath() + "/login.jsp?sessionExpired=true");
        }else{
          response.sendError(ResponseStatus.timeout);
        }
      } else if (session.getAttributeNames().hasMoreElements()) {
        long begin = System.currentTimeMillis();
        //todo 用户有资源使用权限处理
        filterChain.doFilter(servletRequest, servletResponse);
        long end = System.currentTimeMillis();
        LOG.debug("标识ID: {}. 请求执行时间: {} ms", uuid.toString(), end - begin);
      }
    }
  }

  private void logRequestParams(HttpServletRequest request, UUID uuid) {
    try {
      if (request.getSession(false) != null && request.getSession(false).getAttributeNames().hasMoreElements()) {
        Object[] argArray = {uuid.toString(), (Long) request.getSession().getAttribute("userId"), (Long) request.getSession().getAttribute("shopId")};
        LOG.debug("标识ID: {}. 请求userId: {}, shopId: {}", argArray);
      }
      StringBuffer sb = new StringBuffer();
      String method = "";
      Enumeration<String> pNames = request.getParameterNames();
      while (pNames.hasMoreElements()) {
        String pName = pNames.nextElement();
        String pValues[] = request.getParameterValues(pName);
        StringBuffer result = new StringBuffer(pName);
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
      logBrowserInfo(request, uuid);
    } catch (Exception e) {
      LOG.warn("Log请求参数时出错.", e);
    }
  }

  private void logBrowserInfo(HttpServletRequest request, UUID uuid) {
    try {
      StringBuffer sb = new StringBuffer();
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

  @Override
  public void destroy() {
  }
}
