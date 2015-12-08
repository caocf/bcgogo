package com.bcgogo.admin.security;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.user.userGuide.UserGuideCookieName;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.user.service.permission.IUserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 2012-11-10
 * Time: 15:28 PM
 * admin 登录
 */
public class RoleBasedAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
  private static final Logger LOG = LoggerFactory.getLogger(RoleBasedAuthenticationSuccessHandler.class);

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    if (authentication.getPrincipal() instanceof UserDetails) {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(userDetails.getUsername());
      UserGroupDTO userGroupDTO = ServiceManager.getService(IUserGroupService.class).getUserGroupByUserId(userDTO.getId());
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(userDTO.getShopId());
      ShopVersionDTO shopVersionDTO = ServiceManager.getService(IShopVersionService.class).getShopVersionById(shopDTO == null ? null : shopDTO.getShopVersionId());
      HttpSession session = request.getSession(false);
      String loginType = request.getParameter("loginType");
      if (session == null || userGroupDTO == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
      } else {
        if (!"jackchen".equals(userDTO.getUserNo()) && "admin".equals(loginType)) {   //todo delete
          response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else{
          setBaseInfo(session, userDTO, shopDTO, userGroupDTO, shopVersionDTO);
          LOG.info("BCGOGO admin {} [userId:{}] is logining!", userDTO.getName(), userDTO.getId());
          response.sendRedirect(request.getContextPath() + "/view.do?method=index&loginType=" + loginType);
        }
      }
    }
  }

  private void setBaseInfo(HttpSession session, UserDTO userDTO, ShopDTO shopDTO, UserGroupDTO userGroupDTO, ShopVersionDTO shopVersionDTO) {
    session.setAttribute("shopId", userDTO.getShopId());
    session.setAttribute("shopName", shopDTO.getName());
    session.setAttribute("userId", userDTO.getId());
    session.setAttribute("userName", userDTO.getName());
    session.setAttribute("shopVersion", shopVersionDTO);
    session.setAttribute("userGroupId", userGroupDTO.getId());
    session.setAttribute("userGroupName", userGroupDTO.getName());
  }

}

