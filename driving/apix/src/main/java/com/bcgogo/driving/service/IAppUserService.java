package com.bcgogo.driving.service;

import com.bcgogo.driving.model.AppUserCustomer;
import com.bcgogo.driving.model.XAppUserLoginInfo;
import com.bcgogo.pojox.api.AppUserDTO;
import com.bcgogo.pojox.api.XAppUserLoginInfoDTO;
import com.bcgogo.pojox.enums.app.AppUserType;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-7
 * Time: 上午10:10
 */
public interface IAppUserService {

  AppUserCustomer getAppUserCustomerByAppUserNo(String appUserNo);

AppUserDTO getAppUserByImei(String imei);

  boolean updateAppUserLoginInfoSuccess(String sessionId, String newSessionId);

  XAppUserLoginInfo getAppUserLoginInfoByUserNo(String appUserNo, AppUserType appUserType);

  XAppUserLoginInfoDTO getAppUserLoginInfoDTOByUserNo(String appUserNo, AppUserType appUserType);

  void updateAppUserLoginInfo(XAppUserLoginInfoDTO appUserLoginInfoDTO);

  XAppUserLoginInfoDTO getAppUserLoginInfoDTOBySessionId(String sessionId);

}
