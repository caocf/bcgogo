package com.bcgogo.security;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/20/12
 * Time: 6:11 PM
 */
public class BCAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
//    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session Timeout.");
     String redirectUrl = null;
//     response.sendRedirect(response.encodeRedirectURL(redirectUrl));
//     String url = request.getRequestURI();
     // 取得登陆前的url
     String refererUrl = request.getHeader("Referer");
     if(StringUtils.isBlank(refererUrl)){
        redirectUrl=refererUrl;
     }else{
        redirectUrl = buildRedirectUrlToLoginPage(request,response,authException);
     }
     response.sendRedirect(redirectUrl);
  }
}