package com.bcgogo.filter;

import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-3-1
 * Time: 上午11:45
 * To change this template use File | Settings | File Templates.
 */
public class UserGuideFilter implements Filter {
  private static final Logger logger = LoggerFactory.getLogger(AjaxSessionFilter.class);
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      chain.doFilter(request, response);
    }

  }

  @Override
  public void destroy() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
