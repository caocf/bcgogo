package com.bcgogo.security;

import com.bcgogo.common.CookieUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.enums.user.userGuide.UserGuideCookieName;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.IShoppingCartService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IRequestMonitorService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StopWatchUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 2/20/12
 * Time: 6:40 PM
 */
public class RoleBasedAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
  private static final Logger LOG = LoggerFactory.getLogger(RoleBasedAuthenticationSuccessHandler.class);
  private IPrivilegeService privilegeService;

  public IPrivilegeService getPrivilegeService() {
    if (privilegeService == null)
      privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    if (authentication.getPrincipal() instanceof UserDetails) {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      StopWatchUtil sw = new StopWatchUtil("onAuthenticationSuccess: " + userDetails.getUsername(), "get user/shop/usergroup/shopversion");
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(userDetails.getUsername());
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(userDTO.getShopId());
      UserGroupDTO userGroupDTO = ServiceManager.getService(IUserGroupService.class).getUserGroupByUserId(userDTO.getId());
      ShopVersionDTO shopVersionDTO = ServiceManager.getService(IShopVersionService.class).getShopVersionById(shopDTO == null ? null : shopDTO.getShopVersionId());

      sw.stopAndStart("setBaseInfo");

      HttpSession session = request.getSession(false);
      try {
        if (session == null || shopDTO == null || userGroupDTO == null || shopVersionDTO == null) {
          LOG.warn("session:{},shop:{},userGroup:{},shopVersion:{} is null.", new Object[]{session, shopDTO, userGroupDTO, shopVersionDTO});
          response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
          if (isBcgogo(shopDTO.getId())) {
            //bcgogo 内部人员
            if (hasSaleLoginRight(shopVersionDTO.getId(), userGroupDTO.getId())) {
              LOG.info("BCGOGO: saleman {} [userId:{}] is logining!", userDTO.getName(), userDTO.getId());
              response.sendRedirect(request.getContextPath() + "/shopRegister.do?method=registerMain&registerType=SALES_REGISTER");
            } else if (hasMainLoginRight(shopVersionDTO.getId(), userGroupDTO.getId())) {
              LOG.info("BCGOGO:" + userDTO.getName() + "[shopId" + userDTO.getShopId() + ",userId:" + userDTO.getId() + "] is logining!");
              response.sendRedirect(request.getContextPath() + "/user.do?method=createmain");
            } else {
              LOG.error("Bcgogo 内部人员 <shop登录权限> 配置出错，请查看是否有<Shop.Main.Login or Shop.Sale.Login>");
              response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
          } else {
            CookieUtil.loginSetCookies(request, response, userDTO.getId(), userDTO.getShopId(), shopVersionDTO.getId(),userGroupDTO.getId());
            //普通shop账户
            LOG.info(userDTO.getName() + "[shopId" + userDTO.getShopId() + ",userId:" + userDTO.getId() + "] is logining!");
            ServiceManager.getService(IShoppingCartService.class).updateLoginUserShoppingCartInMemCache(shopDTO.getId(), userDTO.getId());
            String clientUrl = request.getParameter("client-url");
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
            if (StringUtils.isNotBlank(clientUrl)) {
              response.sendRedirect(request.getContextPath() + "/" + clientUrl);
            } else if(isAjax) {
              PrintWriter out = response.getWriter();
              setBaseInfo(session, userDTO, shopDTO, userGroupDTO, shopVersionDTO);
//              IRequestMonitorService requestMonitorService  = ServiceManager.getService(IRequestMonitorService.class);
//              requestMonitorService.saveOrUpdateUserMonitorInfo(session.getId(),request.getRemoteAddr(),request.getHeader("user-agent"),request.getParameter("finger"),userDTO);
              //输入的不是前一次的账号，则转到首页
              if(StringUtils.isNotEmpty(request.getParameter("lastUserNo")) && !userDTO.getUserNo().equals(request.getParameter("lastUserNo"))) {
                out.print(request.getContextPath() + "/user.do?method=createmain");
              } else {
                out.print("success");
              }
              out.flush();
              out.close();
              return;
            } else {
              response.sendRedirect(request.getContextPath() + "/user.do?method=createmain");
            }
          }
          setBaseInfo(session, userDTO, shopDTO, userGroupDTO, shopVersionDTO);

          sw.stopAndStart("userMonitor");

          IRequestMonitorService requestMonitorService  = ServiceManager.getService(IRequestMonitorService.class);
//          requestMonitorService.saveOrUpdateUserMonitorInfo(session.getId(),request.getRemoteAddr(),request.getHeader("user-agent"),request.getParameter("finger"),userDTO);

          sw.stopAndPrintWarnLog();
        }
      } catch (Exception e) {
        LOG.debug("method=onAuthenticationSuccess");
        LOG.error(e.getMessage(), e);
      }
    }
  }

  private boolean isBcgogo(Long shopId) {
    return NumberUtil.isEqual(shopId, ShopConstant.BC_ADMIN_SHOP_ID);
  }

  private boolean hasSaleLoginRight(Long shopVersionId, Long userGroupId) {
    return getPrivilegeService().verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.logic, LogicResource.SALE_LOGIN);
  }

  private boolean hasMainLoginRight(Long shopVersionId, Long userGroupId) {
    return getPrivilegeService().verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.logic, LogicResource.MAIN_LOGIN);
  }

  private void setBaseInfo(HttpSession session, UserDTO userDTO, ShopDTO shopDTO, UserGroupDTO userGroupDTO, ShopVersionDTO shopVersionDTO) throws Exception {
    session.setAttribute("shopId", userDTO.getShopId());
    session.setAttribute("shopName", shopDTO.getName());
    session.setAttribute("userId", userDTO.getId());
    session.setAttribute("userName", userDTO.getName());
    session.setAttribute("shopVersion", shopVersionDTO);
    session.setAttribute("userGroupId", userGroupDTO.getId());
    session.setAttribute("userGroupName", userGroupDTO.getName());
    session.setAttribute("userNo", userDTO.getUserNo());
    //todo delete
    session.setAttribute("userGroupType", userGroupDTO.getName());
  }

  protected SavedRequest getSavedRequest(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      return new HttpSessionRequestCache().getRequest(request, response);
    }
    return null;
  }

}

