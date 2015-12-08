package com.bcgogo.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/20/12
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AjaxSessionFilter implements Filter {
  private static final Logger logger = LoggerFactory.getLogger(AjaxSessionFilter.class);
  private String timeoutPage = "login.jsp";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    if ((request instanceof HttpServletRequest)
        && (response instanceof HttpServletResponse)) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      RequestCache requestCache = new HttpSessionRequestCache();
      SavedRequest savedRequest = requestCache.getRequest(httpServletRequest, httpServletResponse);
      List<String> facesRequest = savedRequest.getHeaderValues("Faces-Request");
      if (facesRequest.size() > 0 && facesRequest.get(0).equals("partial/ajax")) {
        if (isSessionInvalid(httpServletRequest)) {
          String url = MessageFormat.format(
              "{0}://{1}:{2,number,####0}{3}/sessionexpired/",
              request.getScheme(), request.getServerName(),
              request.getServerPort(),
              httpServletRequest.getContextPath());

          PrintWriter pw = response.getWriter();
          pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

          pw.println("<partial-response><redirect url=\"" + url
              + "\"></redirect></partial-response>");
          pw.flush();
          return;
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  /**
   * session shouldn't be checked for some pages. For example: for timeout
   * page.. Since we're redirecting to timeout page from this filter, if we
   * don't disable session control for it, filter will again redirect to it
   * and this will be result with an infinite loop...
   *
   * @param httpServletRequest the http servlet request
   * @return true, if is session control required for this resource
   */
  private boolean isSessionControlRequiredForThisResource(
      HttpServletRequest httpServletRequest) {
    String requestPath = httpServletRequest.getRequestURI();

    boolean controlRequiredLogin = !StringUtils.contains(requestPath,
        "login");

    return !controlRequiredLogin ? false : true;

  }

  /**
   * Checks if is session invalid.
   *
   * @param httpServletRequest the http servlet request
   * @return true, if is session invalid
   */
  private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
    boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
        && !httpServletRequest.isRequestedSessionIdValid();
    return sessionInValid;
  }


  /**
   * Gets the timeout page.
   *
   * @return the timeout page
   */
  public String getTimeoutPage() {
    return timeoutPage;
  }

  /**
   * Sets the timeout page.
   *
   * @param timeoutPage the new timeout page
   */
  public void setTimeoutPage(String timeoutPage) {
    this.timeoutPage = timeoutPage;
  }


  @Override
  public void destroy() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}

