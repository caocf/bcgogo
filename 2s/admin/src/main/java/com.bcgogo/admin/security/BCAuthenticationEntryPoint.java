package com.bcgogo.admin.security;

import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/20/12
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class BCAuthenticationEntryPoint  extends LoginUrlAuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session Timeout.");
  }
}