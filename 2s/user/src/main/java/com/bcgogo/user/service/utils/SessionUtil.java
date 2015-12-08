package com.bcgogo.user.service.utils;

import com.bcgogo.api.AppUserLoginInfoDTO;
import com.bcgogo.config.util.AppConstant;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.DataKind;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ImageVersion;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.CookieUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-9-15
 * Time: 下午4:05
 */
public class SessionUtil {
  public static final Logger LOG = LoggerFactory.getLogger(SessionUtil.class);   //slf4j 日志

  public static String getAppUserNo(HttpServletRequest request, HttpServletResponse response) throws BcgogoException, IOException, ServletException {
    String sessionId = CookieUtil.getSessionId(request);
    if (StringUtil.isEmpty(sessionId)) {
      throw new BcgogoException("session id is null!");
    }
    AppUserLoginInfoDTO appUserLoginInfoDTO = getAppUserLoginInfo(response, sessionId);
    if (appUserLoginInfoDTO == null) {
      throw new BcgogoException("appUserLoginInfoDTO is null!");
    }
    return appUserLoginInfoDTO.getAppUserNo();
  }


  public static AppUserType getAppUserType(HttpServletRequest request, HttpServletResponse response) throws BcgogoException, IOException, ServletException {
    String sessionId = CookieUtil.getSessionId(request);
    if (StringUtil.isEmpty(sessionId)) {
      throw new BcgogoException("session id is null!");
    }
    AppUserLoginInfoDTO appUserLoginInfoDTO = getAppUserLoginInfo(response, sessionId);
    if (appUserLoginInfoDTO == null) {
      throw new BcgogoException("appUserLoginInfoDTO is null!");
    }
    return appUserLoginInfoDTO.getAppUserType();
  }

  public static DataKind getAppUserDataKind(HttpServletRequest request, HttpServletResponse response) throws BcgogoException, IOException, ServletException {
    String sessionId = CookieUtil.getSessionId(request);
    if (StringUtil.isEmpty(sessionId)) {
      throw new BcgogoException("session id is null!");
    }
    AppUserLoginInfoDTO appUserLoginInfoDTO = getAppUserLoginInfo(response, sessionId);
    if (appUserLoginInfoDTO == null) {
      throw new BcgogoException("appUserLoginInfoDTO is null!");
    }
    return appUserLoginInfoDTO.getDataKind();
  }

  public static Long getAppUserId(HttpServletRequest request, HttpServletResponse response) throws BcgogoException, IOException, ServletException {
    String sessionId = CookieUtil.getSessionId(request);
    if (StringUtil.isEmpty(sessionId)) {
      throw new BcgogoException("session id is null!");
    }
    AppUserLoginInfoDTO appUserLoginInfoDTO = getAppUserLoginInfo(response, sessionId);
    if (appUserLoginInfoDTO == null) {
      throw new BcgogoException("appUserLoginInfoDTO is null!");
    }
    return appUserLoginInfoDTO.getAppUserId();
  }

  public static AppUserLoginInfoDTO getAppUserLoginInfo(HttpServletRequest request, HttpServletResponse response) throws BcgogoException, IOException, ServletException {
    String sessionId = CookieUtil.getSessionId(request);
    if (StringUtil.isEmpty(sessionId)) {
      throw new BcgogoException("session id is null!");
    }
    AppUserLoginInfoDTO appUserLoginInfoDTO = getAppUserLoginInfo(response, sessionId);
    if (appUserLoginInfoDTO == null) {
      throw new BcgogoException("appUserLoginInfoDTO is null!");
    }
    return appUserLoginInfoDTO;
  }

  public static List<ImageScene> getShopImageScenes(HttpServletRequest request, HttpServletResponse response) throws ServletException, BcgogoException, IOException {
    AppUserLoginInfoDTO loginInfo = getAppUserLoginInfo(request, response);
    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
    imageSceneList.add(ImageVersion.getBigShopImageVersion(loginInfo.getImageVersion()));
    imageSceneList.add(ImageVersion.getSmallShopImageVersion(loginInfo.getImageVersion()));
    return imageSceneList;
  }

  public static List<ImageScene> getShopImageScenes(ImageVersion imageVersion) throws ServletException, BcgogoException, IOException {
    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
    imageSceneList.add(ImageVersion.getBigShopImageVersion(imageVersion));
    imageSceneList.add(ImageVersion.getSmallShopImageVersion(imageVersion));
    return imageSceneList;
  }

  public static List<ImageScene> getAppUserBillImageScenes(HttpServletRequest request, HttpServletResponse response) throws ServletException, BcgogoException, IOException {
    AppUserLoginInfoDTO loginInfo = getAppUserLoginInfo(request, response);
    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
    imageSceneList.add(ImageVersion.getFullAppUserBillImageVersion(loginInfo.getImageVersion()));
    imageSceneList.add(ImageVersion.getSmallAppUserBillImageVersion(loginInfo.getImageVersion()));
    return imageSceneList;
  }

  public static List<ImageScene> getCommonAppUserImageScenes(HttpServletRequest request, HttpServletResponse response) throws ServletException, BcgogoException, IOException {
    AppUserLoginInfoDTO loginInfo = getAppUserLoginInfo(request, response);
    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
    imageSceneList.add(ImageVersion.getSmallShopImageVersion(loginInfo.getImageVersion()));
    imageSceneList.add(ImageVersion.getFullShopImageVersion(loginInfo.getImageVersion()));
    return imageSceneList;
  }

  /**
   * use directly in filter
   */
  public static AppUserLoginInfoDTO getAppUserLoginInfo(HttpServletResponse response, String sessionId) throws IOException, ServletException, BcgogoException {
    AppUserLoginInfoDTO appUserLoginInfoDTO = (AppUserLoginInfoDTO) MemCacheAdapter.get(MemcachePrefix.apiSession.getValue() + sessionId);
    if (appUserLoginInfoDTO == null) {
      appUserLoginInfoDTO = setMemCache(sessionId);
    }
    if (appUserLoginInfoDTO == null) return null;
    doSessionTimeoutUpdate(response, sessionId, appUserLoginInfoDTO.getSessionCreateTime());
    return appUserLoginInfoDTO;
  }

  private static AppUserLoginInfoDTO setMemCache(String sessionId) throws BcgogoException {
    AppUserLoginInfoDTO appUserLoginInfoDTO = ServiceManager.getService(IAppUserService.class)
      .getAppUserLoginInfoBySessionId(sessionId);
    if (appUserLoginInfoDTO == null) return null;
    MemCacheAdapter.set(AppConstant.MEM_CACHE_DATE, MemcachePrefix.apiSession.getValue() + sessionId, appUserLoginInfoDTO);
    return appUserLoginInfoDTO;
  }

  /**
   * 判断sessionKey是否超时
   *
   * @param sessionCreateTime long create session id time
   * @return boolean
   */
  private static boolean isOvertime(long sessionCreateTime) {
    return System.currentTimeMillis() - sessionCreateTime > AppConstant.MEM_CACHE_DATE;
  }

  private static void doSessionTimeoutUpdate(HttpServletResponse response, String sessionId, Long sessionCreateTime)
    throws IOException, ServletException, BcgogoException {
    String newSessionId = sessionId;
    if (isOvertime(sessionCreateTime != null ? sessionCreateTime : 0l)) {
      //fresh session id in db
      newSessionId = CookieUtil.genPermissionKey();
      if (!ServiceManager.getService(IAppUserService.class).updateAppUserLoginInfoSuccess(sessionId, newSessionId)) {
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

}
