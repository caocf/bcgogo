package com.bcgogo.etl.service;

import com.bcgogo.api.AppUserLoginInfoDTO;
import com.bcgogo.api.XAppUserLoginInfoDTO;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.etl.model.XAppUserLoginInfo;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-7
 * Time: 上午10:10
 */
public interface IXAppUserService {


//  AppUserLoginInfoDTO getAppUserLoginInfoDTO(String sessionId);

//  void saveAppUserLoginInfo(AppUserLoginInfoDTO appUserLoginInfoDTO);

  boolean updateAppUserLoginInfoSuccess(String sessionId, String newSessionId);

  XAppUserLoginInfo getAppUserLoginInfoByUserNo(String appUserNo,AppUserType appUserType);

  XAppUserLoginInfoDTO getAppUserLoginInfoDTOByUserNo(String appUserNo, AppUserType appUserType);

  void updateAppUserLoginInfo(XAppUserLoginInfoDTO appUserLoginInfoDTO);

  XAppUserLoginInfoDTO getAppUserLoginInfoDTOBySessionId(String sessionId);

}
