package com.bcgogo.wx.filter;

import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.utils.CookieUtil;
import com.bcgogo.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-13
 * Time: 上午10:31
 */
public class RequestFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestFilter.class);

  private static final String VEHICLE_INFO_SUGGESTION = "/vehicle/info/suggestion";

  private static final String GUEST = "/guest/";

  private static final List<String> excludePaths = new ArrayList<String>();

  static {
    excludePaths.add(VEHICLE_INFO_SUGGESTION);
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String sessionId = CookieUtil.getSessionId(request);
    logRequestParams(request, response, UUID.randomUUID());
      chain.doFilter(servletRequest, response);
  }

  private void logRequestParams(HttpServletRequest request, HttpServletResponse response, UUID uuid) {
    try {
      LOG.info("标识ID: {}. 请求URI: {}", uuid.toString(), request.getRequestURL());
    } catch (Exception e) {
      LOG.warn("Log请求参数时出错.", e);
    }
  }

  private void timeout(HttpServletResponse response) throws IOException {
    try {
      response.setContentType("application/json; charset=UTF-8");
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.objectToJson(MessageCode.toApiResponse(MessageCode.LOGIN_TIME_OUT)));
      writer.close();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }
}
