package com.bcgogo.filter;

import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.user.verifier.RequestPrivilegeVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-14
 * Time: 下午5:55
 * 权限 filter
 */
public class AccessControlFilter extends AbstractFilter {
  private static final Logger LOG = LoggerFactory.getLogger(AccessControlFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    if (servletRequest instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      HttpSession session = request.getSession(false);
      UUID uuid = UUID.randomUUID();
      logRequestParams(request, uuid, LOG);
      if (RequestPrivilegeVerifier.verifyBaseRole(request)) {
        //状态：未登录 最基本的权限
        filterChain.doFilter(servletRequest, servletResponse);
      } else if (isNotLoginOrTimeout(request)) {
        //状态：登录
        response.sendRedirect(request.getContextPath() + "/login.jsp?sessionExpired=true");
      } else if (session.getAttributeNames().hasMoreElements()) {
        if (!RequestPrivilegeVerifier.verifier(request)) {
          //用户没有资源使用权限处理
          if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/user.do?method=createmain")) {
            response.sendRedirect(request.getContextPath() + "/user.do?method=createmain&permissionFlag=true");
          } else {
            LOG.error("权限配置异常！");
            response.sendRedirect(request.getContextPath() + "/login.jsp?permissionFlag=true");
          }
        } else {
          long begin = System.currentTimeMillis();
          //用户有资源使用权限处理
          filterChain.doFilter(servletRequest, servletResponse);
          long end = System.currentTimeMillis();
          LOG.debug("标识ID: {}. 请求执行时间: {} ms", uuid.toString(), end - begin);
        }
      }
    }
  }

  @Override
  public void destroy() {
  }
}
