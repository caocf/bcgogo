package com.bcgogo.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-12-12
 * Time: 下午1:24
 * To change this template use File | Settings | File Templates.
 */
public class RoleBasedAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    if(isAjax){
      PrintWriter out = response.getWriter();
      out.print(exception.getMessage());
      out.flush();
      out.close();
      return;
    }else{
      super.onAuthenticationFailure(request, response, exception);
    }
  }
}
