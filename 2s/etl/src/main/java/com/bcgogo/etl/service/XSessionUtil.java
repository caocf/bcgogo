package com.bcgogo.etl.service;

import com.bcgogo.api.AppUserLoginInfoDTO;
import com.bcgogo.api.XAppUserLoginInfoDTO;
import com.bcgogo.config.util.AppConstant;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CookieUtil;
import com.bcgogo.utils.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午1:13
 */
public class XSessionUtil {

  public static String getAppUserNo(HttpServletRequest request, HttpServletResponse response) throws BcgogoException, IOException, ServletException {
    String sessionId = CookieUtil.getSessionId(request);
    if (StringUtil.isEmpty(sessionId)) {
      throw new BcgogoException("session id is null!");
    }
    XAppUserLoginInfoDTO appUserLoginInfoDTO = (XAppUserLoginInfoDTO) MemCacheAdapter.get(MemcachePrefix.xApiSession.getValue() + sessionId);
    if (appUserLoginInfoDTO == null) {
      appUserLoginInfoDTO = setMemCache(sessionId);
    }
    if (appUserLoginInfoDTO == null) return null;
//    doSessionTimeoutUpdate(response, sessionId, appUserLoginInfoDTO.getSessionCreateTime());
    if (appUserLoginInfoDTO == null) {
      throw new BcgogoException("appUserLoginInfoDTO is null!");
    }
    return appUserLoginInfoDTO.getAppUserNo();
  }

  private static void doSessionTimeoutUpdate(HttpServletResponse response, String sessionId, Long sessionCreateTime)
    throws IOException, ServletException, BcgogoException {
    String newSessionId = sessionId;
    if (isOvertime(sessionCreateTime != null ? sessionCreateTime : 0l)) {
      //fresh session id in db
      newSessionId = CookieUtil.genPermissionKey();
      if (!ServiceManager.getService(IXAppUserService.class).updateAppUserLoginInfoSuccess(sessionId, newSessionId)) {
        return;
      }
      //fresh session id in cache
      if (StringUtil.isNotEmpty(sessionId)) {
        MemCacheAdapter.delete(MemcachePrefix.apiSession.getValue() + sessionId);
      }
      if (StringUtil.isNotEmpty(newSessionId)) {
        setMemCache(newSessionId);
      }
    }
    CookieUtil.setSessionId(response, newSessionId);
  }

  public static final Long MEM_CACHE_DATE = 60 * 60 * 24 * 30 * 1000L;//30天

  private static XAppUserLoginInfoDTO setMemCache(String sessionId) throws BcgogoException {
    XAppUserLoginInfoDTO appUserLoginInfoDTO = ServiceManager.getService(IXAppUserService.class).getAppUserLoginInfoDTOBySessionId(sessionId);
    if (appUserLoginInfoDTO == null) return null;
    MemCacheAdapter.set(MEM_CACHE_DATE, MemcachePrefix.xApiSession.getValue() + sessionId, appUserLoginInfoDTO);
    return appUserLoginInfoDTO;
  }

  private static boolean isOvertime(long sessionCreateTime) {
    return System.currentTimeMillis() - sessionCreateTime > AppConstant.MEM_CACHE_DATE;
  }

}
