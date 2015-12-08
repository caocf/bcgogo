package com.bcgogo.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-30
 * Time: 下午4:44
 * To change this template use File | Settings | File Templates.
 */
public class CharsetFilter implements Filter{
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request=(HttpServletRequest)servletRequest;
    HttpServletResponse response=(HttpServletResponse)servletResponse;
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    filterChain.doFilter(request,response);
  }

  @Override
  public void destroy() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
