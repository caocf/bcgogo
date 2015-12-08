package com.bcgogo.filter;

import com.bcgogo.common.WebUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-7-1
 * Time: 上午10:54
 * 客户端 filter
 */
public class ClientFilter extends AbstractFilter {
  private static final Logger LOG = LoggerFactory.getLogger(ClientFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    if (servletRequest instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      UUID uuid = UUID.randomUUID();
      logRequestParams(request, uuid, LOG);
      String clientUserNo = request.getParameter("clientUserNo");
      String userNo = WebUtil.getUserNo(request);
      StringBuilder url = getUrlBuilder(request);
      if (isNotLoginOrTimeout(request) || StringUtil.isEmpty(clientUserNo) || !userNo.equals(clientUserNo)) {
        //状态：登录
        response.addCookie(new Cookie("clientUrl", URLEncoder.encode(url.toString(), "UTF-8")));
        response.sendRedirect(request.getContextPath() + "/login.jsp");
      } else {
        long begin = System.currentTimeMillis();
        response.sendRedirect(request.getContextPath() + "/" + url);
        filterChain.doFilter(servletRequest, servletResponse);
        long end = System.currentTimeMillis();
        LOG.debug("标识ID: {}. 请求执行时间: {} ms", uuid.toString(), end - begin);
      }
    }
  }

  private StringBuilder getUrlBuilder(HttpServletRequest request) {
    StringBuilder method = new StringBuilder();
    StringBuilder url = new StringBuilder();
    String[] uri = request.getRequestURI().split("/web/");
    if (uri != null && uri.length > 1) {
      uri = uri[1].split(".client");
      if (uri != null && uri.length > 0) {
        url.append(uri[0]).append(".do").append("?");
      }
    }
    Enumeration pNames = request.getParameterNames();
    StringBuilder result = new StringBuilder();
    int i = 0;
    while (pNames.hasMoreElements()) {
      String pName = (String) pNames.nextElement();
      String pValues[] = request.getParameterValues(pName);
      if (pName.equals("method")) {
        method.append("method").append('=').append(pValues[0]);
      } else {
        if (i > 0) result.append("&");
        result.append(pName).append('=').append(pValues[0]);
        i++;
      }
    }
    url.append(method);
    if (i > 0) {
      url.append("&").append(result);
    }
    return url;
  }

  @Override
  public void destroy() {
  }
}
