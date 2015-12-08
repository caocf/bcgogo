package com.bcgogo.driving.service.impl;

import com.bcgogo.driving.dao.AppUserCustomerDao;
import com.bcgogo.driving.dao.AppUserDao;
import com.bcgogo.driving.dao.XAppUserLoginInfoDao;
import com.bcgogo.driving.model.AppUser;
import com.bcgogo.driving.model.AppUserCustomer;
import com.bcgogo.driving.model.XAppUserLoginInfo;
import com.bcgogo.driving.model.mongodb.XNumberLong;
import com.bcgogo.pojox.api.AppUserCustomerDTO;
import com.bcgogo.pojox.api.AppUserDTO;
import com.bcgogo.pojox.api.XAppUserLoginInfoDTO;
import com.bcgogo.pojox.cache.MemCacheAdapter;
import com.bcgogo.pojox.constant.MemcachePrefix;
import com.bcgogo.pojox.enums.app.AppUserType;
import com.bcgogo.driving.service.IAppUserService;

import com.bcgogo.pojox.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-7
 * Time: 上午10:10
 */
@Component
public class AppUserService implements IAppUserService {

  @Autowired
  private XAppUserLoginInfoDao appUserLoginInfoDao;
  @Autowired
  private AppUserDao appUserDao;
  @Autowired
  private AppUserCustomerDao appUserCustomerDao;

  @Override
  public AppUserDTO getAppUserByImei(String imei) {
    AppUser appUser = appUserDao.getAppUserByImei(imei);
    return appUser != null ? appUser.toDTO() : null;
  }

  @Override
  public AppUserCustomer getAppUserCustomerByAppUserNo(String appUserNo) {
    if (StringUtil.isEmpty(appUserNo)) return null;
    return appUserCustomerDao.getAppUserCustomerByAppUserNo(appUserNo);
  }

  @Override
  public XAppUserLoginInfoDTO getAppUserLoginInfoDTOByUserNo(String appUserNo, AppUserType appUserType) {
    XAppUserLoginInfo appUserLoginInfo = appUserLoginInfoDao.getAppUserLoginInfoByUserNo(appUserNo, appUserType);
    return appUserLoginInfo != null ? appUserLoginInfo.toDTO() : null;
  }


  @Override
  public XAppUserLoginInfo getAppUserLoginInfoByUserNo(String appUserNo, AppUserType appUserType) {
    if (StringUtil.isEmpty(appUserNo) || appUserType == null) {
      return null;
    }
    return appUserLoginInfoDao.getAppUserLoginInfoByUserNo(appUserNo, appUserType);
  }

//  public AppUserLoginInfoDTO getAppUserLoginInfoBySessionId(String sessionId) {
//    AppUserLoginInfo log = appUserLoginInfoDao.getAppUserLoginInfoBySessionId(sessionId);
//    AppUserLoginInfoDTO dto = null;
//    if (log != null) {
//      dto = log.toDTO();
//      AppUserDTO appUserDTO = this.getAppUserByUserNo(log.getAppUserNo(), null);
//      dto.from(appUserDTO);
//    }
//    return dto;
//  }

  @Override
  public boolean updateAppUserLoginInfoSuccess(String sessionId, String newSessionId) {
    XAppUserLoginInfo loginInfo = getAppUserLoginInfoBySessionId(sessionId);
    if (loginInfo == null) return false;
    loginInfo.setSessionId(newSessionId);
    loginInfo.setSessionCreateTime(new XNumberLong(System.currentTimeMillis()));
//    appUserLoginInfoDao.update(loginInfo, loginInfo.get_id());
    return true;

  }

  public XAppUserLoginInfo getAppUserLoginInfoBySessionId(String sessionId) {
    if (StringUtil.isEmpty(sessionId)) {
      return null;
    }
    return appUserLoginInfoDao.getAppUserLoginInfoBySessionId(sessionId);
  }

  @Override
  public XAppUserLoginInfoDTO getAppUserLoginInfoDTOBySessionId(String sessionId) {
    if (StringUtil.isEmpty(sessionId)) {
      return null;
    }
    XAppUserLoginInfo appUserLoginInfo = getAppUserLoginInfoBySessionId(sessionId);
    return appUserLoginInfo != null ? appUserLoginInfo.toDTO() : null;
  }

  public void updateAppUserLoginInfo(XAppUserLoginInfoDTO appUserLoginInfoDTO) {
    XAppUserLoginInfo appUserLoginInfo = getAppUserLoginInfoByUserNo(appUserLoginInfoDTO.getAppUserNo(), appUserLoginInfoDTO.getAppUserType());
    if (appUserLoginInfo == null) {
      appUserLoginInfo = new XAppUserLoginInfo();
    } else {
      String oldSessionId = appUserLoginInfo.getSessionId();
      if (StringUtil.isNotEmpty(oldSessionId)) {
        MemCacheAdapter.delete(MemcachePrefix.xApiSession.getValue() + oldSessionId);
      }
    }
    appUserLoginInfo.fromDTO(appUserLoginInfoDTO);
    appUserLoginInfoDao.saveOrUpdate(appUserLoginInfo);

  }


}
