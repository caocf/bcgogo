package com.bcgogo.user.service;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.model.SystemMonitor.ClientBindingLog;
import com.bcgogo.user.model.SystemMonitor.ClientUserLoginInfo;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: ZhangJuntao
 * Date: 13-6-21
 * Time: 下午5:39
 */
@Component
public class ClientLoginService implements IClientLoginService {
  private static final Logger LOG = LoggerFactory.getLogger(ClientLoginService.class);

  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public void createClientBindingLog(Long shopId, String userNo, String mac) {
    UserWriter writer = userDaoManager.getWriter();
    if (writer.isClientBinding(shopId, userNo, mac)) {
      return;
    }
    Object status = writer.begin();
    try {
      ClientBindingLog log = new ClientBindingLog(shopId, userNo, mac, System.currentTimeMillis());
      writer.save(log);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void createOrUpdateClientLoginInfo(String userNo, String clientVersion) {
    UserWriter writer = userDaoManager.getWriter();
    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(userNo);
    if (user == null) return;
    Object status = writer.begin();
    try {
      ClientUserLoginInfo info = writer.getClientUserLoginInfo(user.getId());
      if (info == null) {
        info = new ClientUserLoginInfo();
        info.createClientUserLoginInfo(user, clientVersion);
        writer.save(info);
      } else {
        info.setLoginTime(System.currentTimeMillis());
        info.setClientVersion(clientVersion);
        writer.update(info);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void createOrUpdateClientLogoutInfo(String userNo) {
    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(userNo);
    if (user == null) return;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ClientUserLoginInfo info = writer.getClientUserLoginInfo(user.getId());
      if (info != null) {
        info.setLogoutTime(System.currentTimeMillis());
        writer.update(info);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

}
