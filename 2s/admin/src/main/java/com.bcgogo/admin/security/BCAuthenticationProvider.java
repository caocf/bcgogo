package com.bcgogo.admin.security;

import com.bcgogo.constant.PermissionConstants;
import com.bcgogo.enums.user.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.model.permission.UserGroup;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

//admin
public class BCAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  private static final Logger LOG = LoggerFactory.getLogger(BCAuthenticationProvider.class);

  @Override
  protected UserDetails retrieveUser(String pUsername, UsernamePasswordAuthenticationToken pAuthentication)
      throws AuthenticationException {
    IUserService userService = ServiceManager.getService(IUserService.class);
    if (LOG.isDebugEnabled()) LOG.debug("Entering retrieveUser() with credentials " + pUsername);
    UserDTO user = userService.getUserByUserInfo(pUsername);
    //用户是否被删除
    if (user == null || Status.deleted.equals(user.getStatusEnum())) {
      LOG.debug("无效的用户名或密码，请重新输入或咨询客服." + pUsername);
      throw new BadCredentialsException(messages.getMessage("login.submit.error.usernamepassword",
          "无效的用户名或密码，请重新输入或咨询客服."));
    }
    //用户是否被禁用
    if (user.getStatusEnum() != null && Status.inActive.equals(user.getStatusEnum())) {
      throw new BadCredentialsException("此账户已被禁用，请联系管理员.");
    }
    //密码是否相等
    String encryptedPassword;
    try {
      encryptedPassword = EncryptionUtil.encryptPassword(pAuthentication.getCredentials().toString(), user.getShopId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (encryptedPassword != null && !user.getPassword().equals(encryptedPassword)) {
      LOG.debug("无效的用户名或密码，请重新输入或咨询客服." + pUsername);
      throw new BadCredentialsException(messages.getMessage("login.submit.error.usernamepassword",
          "无效的用户名或密码，请重新输入或咨询客服."));
    }
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    user = ServiceManager.getService(IUserCacheService.class).getUser(user.getShopId(), user.getId());
    //用户组是否配置正确
    UserGroup userGroup = ServiceManager.getService(IUserGroupService.class).getUserGroup(user.getUserGroupId());
    if (userGroup == null) {
      LOG.warn("user[shopId:" + user.getShopId() + ",userId:" + user.getId() + "] has no userGroup!");
    }
    if (userGroup == null || !(NumberUtil.isEqual(ShopConstant.BC_ADMIN_SHOP_ID, user.getShopId())))
      throw new BadCredentialsException(messages.getMessage("login.submit.error.usernamepassword",
          "此用户名没有权限，请重新输入或咨询客服."));
    authorities.add(new GrantedAuthorityImpl(userGroup.getName()));
    LOG.debug("[shopId:" + user.getShopId() + ",userId:" + user.getId() + "] userGroup is " + userGroup.getName());
    return new User(pUsername, user.getPassword(), true, true, true, true, authorities);
  }

  @Override
  protected void additionalAuthenticationChecks(UserDetails pUserDetails, UsernamePasswordAuthenticationToken pAuthentication) throws AuthenticationException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering additionalAuthenticationChecks() with credentials ");
    }
  }
}

