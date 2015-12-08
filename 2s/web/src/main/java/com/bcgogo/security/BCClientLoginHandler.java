package com.bcgogo.security;

import com.bcgogo.client.ClientLoginResult;
import com.bcgogo.client.ClientLogoutResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.shop.ShopState;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.user.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IClientLoginService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午2:22
 */
@Component
public class BCClientLoginHandler {
  private static final Logger LOG = LoggerFactory.getLogger(BCClientLoginHandler.class);

  public ClientLoginResult login(String password, String userNo, String apiVersion, String MAC) {
    ClientLoginResult result = new ClientLoginResult(false, userNo);
    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(userNo);
    if (user == null || Status.deleted.equals(user.getStatusEnum()) || Status.inActive.equals(user.getStatusEnum()))
      return result;
    String encryptedPassword;
    try {
      encryptedPassword = EncryptionUtil.encryptPassword(password, user.getShopId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (encryptedPassword != null && !user.getPassword().equals(encryptedPassword))
      return result;
    if (user.getShopId() == null) {
      LOG.error("user id:{} shopId is null", user.getId());
      return result;
    }
    IShopService shopService = ServiceManager.getService(IShopService.class);
    ShopDTO shopDTO = shopService.checkTrialEndTimeShop(user.getShopId());
    if (shopDTO == null) {
      LOG.error("cannot find shop by shopId:{}", user.getShopId());
      return result;
    }
    if (ShopState.DELETED == shopDTO.getShopState()) {
      return result;
    }
    //过期
    if (ShopState.OVERDUE == shopDTO.getShopState() && ShopStatus.REGISTERED_TRIAL == shopDTO.getShopStatus()) {
      return result;
    }
    if (ShopState.ARREARS == shopDTO.getShopState()) {
      return result;
    }
    result.setShopName(shopDTO.getName());
    result.setSuccess(true);
    result.setUserName(user.getName());
    result.setShopId(user.getShopId());
    //创建绑定日志
    ServiceManager.getService(IClientLoginService.class).createClientBindingLog(shopDTO.getId(), userNo, MAC);
    ServiceManager.getService(IClientLoginService.class).createOrUpdateClientLoginInfo(userNo,apiVersion);
    return result;
  }

  public ClientLogoutResult logout(String userNo) {
    ClientLogoutResult result = new ClientLogoutResult(true);
    ServiceManager.getService(IClientLoginService.class).createOrUpdateClientLogoutInfo(userNo);
    return result;
  }
}
