package com.bcgogo.etl.service;

import com.bcgogo.api.XAppUserLoginInfoDTO;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.etl.dao.XAppUserLoginInfoDao;
import com.bcgogo.etl.model.XAppUserLoginInfo;
import com.bcgogo.etl.model.mongodb.XNumberLong;
import com.bcgogo.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-7
 * Time: 上午10:10
 */
@Component
public class XAppUserService implements IXAppUserService {

  @Autowired
  private XAppUserLoginInfoDao appUserLoginInfoDao;
//  @Override
//  public AppUserLoginInfoDTO getAppUserLoginInfoDTO(String sessionId) {
//    return null;  //To change body of implemented methods use File | Settings | File Templates.
//  }

//  @Override
//  public String getAppUserNo(String sessionId) {
//    if(StringUtil.isEmpty(sessionId)) return null;
//   AppUserLoginInfoDTO appUserLoginInfoDTO=getAppUserLoginInfoDTO(sessionId);
//    return appUserLoginInfoDTO!=null?appUserLoginInfoDTO.getAppUserNo():null;  //To change body of implemented methods use File | Settings | File Templates.
//  }

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
