package com.bcgogo.listener;

import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;

public class EnhancedHttpSessionEventPublisher extends HttpSessionEventPublisher {
  @Override
  public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    ServletContext servletContext = httpSessionEvent.getSession().getServletContext();
    Integer sessionCount = (Integer) servletContext.getAttribute("sessionCount");
    if (sessionCount == null) {
      sessionCount = new Integer(1);
    } else {
      sessionCount = new Integer(sessionCount.intValue() + 1);
    }
    servletContext.setAttribute("sessionCount", sessionCount);
    super.sessionCreated(httpSessionEvent);
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    ServletContext servletContext = httpSessionEvent.getSession().getServletContext();
    Integer sessionCount = (Integer) servletContext.getAttribute("sessionCount");
    if (sessionCount == null) {
      sessionCount = new Integer(0);
    } else if (sessionCount.intValue() > 0) {
      sessionCount = new Integer(sessionCount.intValue() - 1);
    }
    servletContext.setAttribute("sessionCount", sessionCount);
    super.sessionDestroyed(httpSessionEvent);
  }
}