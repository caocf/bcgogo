package com.bcgogo.security;

import com.bcgogo.common.WebUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.IShoppingCartService;
import com.bcgogo.user.service.IRequestMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 系统登出时监控
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-19
 * Time: 下午3:26
 * To change this template use File | Settings | File Templates.
 */
public class BCWebLogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
  private static final Logger LOG = LoggerFactory.getLogger(BCWebLogoutSuccessHandler.class);

  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      if(shopId!=null && userId!=null){
        ServiceManager.getService(IShoppingCartService.class).removeLogoutUserShoppingCartInMemCache(shopId, userId);
      }
    }catch (Exception e){
      LOG.error("用户logout删除 shopping cart from memCache  出错!");
      LOG.error("shopId:{},userId:{}",WebUtil.getShopId(request),WebUtil.getUserId(request));
    }
    HttpSession session  = request.getSession(false);
    if (authentication != null && session!=null) {
      IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String userName = userDetails.getUsername();
      String sessionId = session.getId();
      String ip = request.getRemoteAddr();
      requestMonitorService.saveOrUpdateUserLogOutInfo(ip,sessionId, userName,request.getParameter("finger"));
    }
    if(session!=null){
      session.invalidate();
    }
    response.sendRedirect(request.getContextPath());
  }

}
