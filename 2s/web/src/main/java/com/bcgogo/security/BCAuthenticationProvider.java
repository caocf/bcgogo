package com.bcgogo.security;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.shop.ShopState;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.model.permission.UserGroup;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StopWatchUtil;
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

//web
public class BCAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
  private static final Logger LOG = LoggerFactory.getLogger(BCAuthenticationProvider.class);

  private IPrivilegeService privilegeService;

  public IPrivilegeService getPrivilegeService() {
    if (privilegeService == null) privilegeService = ServiceManager.getService(IPrivilegeService.class);
    return privilegeService;
  }

  @Override
  protected UserDetails retrieveUser(String pUsername, UsernamePasswordAuthenticationToken pAuthentication) throws AuthenticationException {
    StopWatchUtil sw = new StopWatchUtil("retrieveUser: " + pUsername, "getUserByUserName");

    if (LOG.isDebugEnabled()) LOG.debug("Entering retrieveUser() with credentials " + pUsername);
    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(pUsername);

    sw.stopAndStart("getUserById");
    //禁止被删除的用户
    if (user == null || Status.deleted.equals(user.getStatusEnum())) {
      LOG.debug("无效的用户名，请重新输入或咨询客服." + pUsername);
      throw new BadCredentialsException(messages.getMessage("login.submit.error.usernamepassword", "userNoWrong"));
    }
    //禁止被管理员禁用的账户
    if (Status.inActive.equals(user.getStatusEnum())) {
      throw new BadCredentialsException("shopForbid");
    }
    user = ServiceManager.getService(IUserCacheService.class).getUser(user.getShopId(), user.getId());
    sw.stopAndStart("verifyPass and shop");

    if (null == user) {
      LOG.debug("用户名和密码都通过，不过user和usergroupuser联查的时候没有查到user." + "shopId:" + user.getShopId() + ",userid:" + user.getId());
      throw new BadCredentialsException(messages.getMessage("login.submit.error.usernamepassword", "userNoWrong"));
    }
    //密码不正确
    String encryptedPassword;
    try {
      encryptedPassword = EncryptionUtil.encryptPassword(pAuthentication.getCredentials().toString(), user.getShopId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (encryptedPassword != null && !user.getPassword().equals(encryptedPassword)) {
      LOG.debug("无效密码，请重新输入或咨询客服." + pUsername);
      throw new BadCredentialsException(messages.getMessage("login.submit.error.usernamepassword", "passwordWrong"));
    }
    IShopService shopService = ServiceManager.getService(IShopService.class);
    //检查shop
    ShopDTO shopDTO = shopService.checkTrialEndTimeShop(user.getShopId());
    shopService.verifyShop(shopDTO);

    sw.stopAndStart("validate group");
    //检查用户组
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    Long shopId = user.getShopId();
    UserGroup userGroup = ServiceManager.getService(IUserGroupService.class).getUserGroup(user.getUserGroupId());
    if (userGroup != null) {
      authorities.add(new GrantedAuthorityImpl(userGroup.getName()));
      LOG.debug("[shopId:{},userId{}] userGroup is " + userGroup.getName(), user.getShopId(), user.getId());
    } else {
      LOG.error("user[shopId:{},userId:{}] has no userGroup!", user.getShopId(), user.getId());
      throw new BadCredentialsException("userRoleWrong");
    }
    //检查特殊用户组
    if (isBcgogo(shopId)) {
      //如果是bc内部人员查看是否有登录权限
      if (!hasLoginRight(shopDTO.getShopVersionId(), userGroup.getId())) {
        throw new BadCredentialsException("loginPermission");
      }
    }

    sw.stopAndPrintWarnLog();
    return new User(pUsername, user.getPassword(), true, true, true, true, authorities);
  }


  private boolean isBcgogo(Long shopId) {
    return NumberUtil.isEqual(shopId, ShopConstant.BC_ADMIN_SHOP_ID);
  }

  private boolean hasLoginRight(Long versionId, Long userGroupId) {
    return getPrivilegeService().verifierUserGroupResource(versionId, userGroupId, ResourceType.logic, LogicResource.SHOP_LOGIN);
  }

  @Override
  protected void additionalAuthenticationChecks(UserDetails pUserDetails, UsernamePasswordAuthenticationToken pAuthentication) throws AuthenticationException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering additionalAuthenticationChecks() with credentials ");
    }
  }
}

