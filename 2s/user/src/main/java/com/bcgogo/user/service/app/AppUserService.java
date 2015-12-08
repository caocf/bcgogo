package com.bcgogo.user.service.app;

import com.bcgogo.BooleanEnum;
import com.bcgogo.api.*;
import com.bcgogo.api.gsm.GSMRegisterDTO;
import com.bcgogo.api.response.ApiGsmLoginResponse;
import com.bcgogo.api.response.ApiLoginResponse;
import com.bcgogo.api.response.ApiMirrorLoginResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.juhe.JuheCityOilPriceDTO;
import com.bcgogo.config.service.App.IAppUserConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.*;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.etl.model.GsmPoint;
import com.bcgogo.etl.model.GsmVehicleInfo;
import com.bcgogo.etl.service.IGsmPointService;
import com.bcgogo.mq.message.MQLoginMessageDTO;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.app.*;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.model.permission.UserGroup;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.ICouponService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-22
 * Time: 上午10:42
 */
@Component
public class AppUserService implements IAppUserService {
  private static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public Pair<ApiResponse, AppUserDTO> registerAppUser(RegistrationDTO registrationDTO) {
    Pair<ApiResponse, AppUserDTO> pair = new Pair<ApiResponse, AppUserDTO>();
    UserWriter writer = userDaoManager.getWriter();
    String vResult = registerAppUserValidate(registrationDTO, writer);
    if (!registrationDTO.isSuccess(vResult)) {
      pair.setKey(MessageCode.toApiResponse(MessageCode.REGISTER_FAIL, vResult));
      return pair;
    }
    AppUserDTO appUserDTO = null;
    Object status = writer.begin();
    try {
      //add app user
      appUserDTO = addAppUser(registrationDTO, writer);
      //add vehicle
      AppVehicleDTO appVehicleDTO = addVehicle(registrationDTO, writer);
      if (appVehicleDTO != null) {
        appUserDTO.setAppVehicleDTO(appVehicleDTO);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    //create session id in cache and db
    updateAppUserLoginInfo(registrationDTO.toLoginDTO());
    pair.setKey(MessageCode.toApiResponse(MessageCode.REGISTER_SUCCESS));
    pair.setValue(appUserDTO);
    return pair;
  }

  private String registerAppUserValidate(RegistrationDTO registrationDTO, UserWriter writer) {
    String vResult = registrationDTO.validate();
    if (!registrationDTO.isSuccess(vResult)) {
      return vResult;
    }
    //mobile Unique validate
    if (writer.isAppUserMobileExisted(registrationDTO.getMobile(), registrationDTO.getAppUserType())) {
      return ValidateMsg.APP_USER_MOBILE_HAS_BEEN_USED.getValue();
    }
    //userNo Unique validate
    if (writer.isAppUserNoExisted(registrationDTO.getUserNo(), registrationDTO.getAppUserType())) {
      return ValidateMsg.APP_USER_NO_HAS_BEEN_USED.getValue();
    }
    if (registrationDTO.getShopId() != null) {
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(registrationDTO.getShopId());
      if (shopDTO == null) {
        return ValidateMsg.SHOP_NOT_EXIST.getValue();
      }
      String shopValidateResult = shopDTO.shopStateValidate();
      if (StringUtil.isNotEmpty(shopValidateResult)) {
        return shopValidateResult;
      }
    }
    return "";
  }

  private AppUserDTO addAppUser(RegistrationDTO registrationDTO, UserWriter writer) {
    registrationDTO.computeMD5();
    AppUser appUser = new AppUser(registrationDTO);
    writer.save(appUser);
    return appUser.toDTO();
  }

  private AppVehicleDTO addVehicle(RegistrationDTO registrationDTO, UserWriter writer) {
    AppVehicleDTO appVehicleDTO = registrationDTO.toAppVehicleDTO();
    if (!RegexUtils.isVehicleNo(appVehicleDTO.getVehicleNo())) {
      return null;
    }
    AppVehicle appVehicle = new AppVehicle(appVehicleDTO);
    writer.save(appVehicle);
    appVehicleDTO.setVehicleId(appVehicle.getId());
    ServiceManager.getService(IAppUserVehicleObdService.class)
      .updateDefaultAppVehicle(appVehicleDTO.getVehicleId(), registrationDTO.getUserNo(), false);
    return appVehicleDTO;
  }

  /**
   * 根据userNo、mobile获取手机端用户
   */
  @Override
  public AppUserDTO getAppUserByUserNo(String appUserNo, String mobile) {
    UserWriter writer = userDaoManager.getWriter();
    AppUser appUser = getAppUsers(appUserNo, mobile, writer);
    if (appUser == null) return null;
    return appUser.toDTO();
  }

  @Override
  public AppUserDTO getAppUserDTOById(Long appUserId) {
    UserWriter writer = userDaoManager.getWriter();
    if (appUserId != null) {
      AppUser appUser = writer.getById(AppUser.class, appUserId);
      if (appUser != null) {
        return appUser.toDTO();
      }
    }
    return null;
  }

  private AppUser getAppUsers(String appUserNo, String mobile, UserWriter writer) {
    if (StringUtils.isEmpty(appUserNo) && StringUtils.isEmpty(mobile)) {
      return null;
    }
    List<AppUser> appUserList = writer.getAppUserByUserNo(appUserNo, mobile);
    if (CollectionUtils.isEmpty(appUserList)) {
      return null;
    }
    if (appUserList.size() > 1) {
      LOG.error("appUser 有多个:appUserNo:" + appUserNo + ",mobile:" + mobile);
    }
    return appUserList.get(0);
  }

  /**
   * appUser修改个人资料
   *
   * @param appUserDTO
   * @return
   */
  public Result updateAppUserInfo(AppUserDTO appUserDTO) {
    Result result = new Result();
    result.setSuccess(false);

    AppUserDTO dbUserDTO = getAppUserByUserNo(appUserDTO.getUserNo(), null);
    if (dbUserDTO == null) {
      result.setMsg(ValidateMsg.APP_USER_NOT_EXIST.getValue());
      return result;
    }
    if (StringUtil.isNotEmpty(appUserDTO.getMobile())) {
      AppUserDTO mobileUserDTO = getAppUserByUserNo(null, appUserDTO.getMobile());
      if (mobileUserDTO != null && mobileUserDTO.getId().longValue() != dbUserDTO.getId().longValue()) {
        result.setMsg("该手机号已被占用，请重新输入");
        return result;
      }
    }

    appUserDTO.setId(dbUserDTO.getId());
    dbUserDTO.setMobile(appUserDTO.getMobile());
    dbUserDTO.setName(appUserDTO.getName());

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppUser appUser = writer.getById(AppUser.class, dbUserDTO.getId());
      appUser = appUser.fromDTO(dbUserDTO);
      writer.saveOrUpdate(appUser);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    result.setSuccess(true);
    result.setMsg("用户信息更新成功");
    return result;
  }

  /**
   * 保存用户反馈
   *
   * @param appUserFeedbackDTO
   */
  @Override
  public void saveAppUserFeedback(AppUserFeedbackDTO appUserFeedbackDTO) {
    if (appUserFeedbackDTO == null) {
      return;
    }

    appUserFeedbackDTO.setHandle(appUserFeedbackDTO.getHandle() == null ? BooleanEnum.FALSE : appUserFeedbackDTO.getHandle());
    appUserFeedbackDTO.setFeedBackTime(appUserFeedbackDTO.getFeedBackTime() == null ?
      System.currentTimeMillis() : appUserFeedbackDTO.getFeedBackTime());

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppUserFeedBack appUserFeedback = new AppUserFeedBack(appUserFeedbackDTO);
      writer.save(appUserFeedback);
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 手机端用户更改密码
   *
   * @param appUserDTO
   */
  public Result updatePassword(AppUserDTO appUserDTO) {
    Result result = new Result();
    result.setSuccess(false);

    AppUserDTO dbUserDTO = this.getAppUserByUserNo(appUserDTO.getUserNo(), null);
    if (dbUserDTO == null) {
      result.setMsg(ValidateMsg.APP_USER_NOT_EXIST.getValue());
      return result;
    }
    String oldPassword = EncryptionUtil.computeMD5Improved(appUserDTO.getOldPassword());
    if (StringUtils.isEmpty(oldPassword) || !oldPassword.equals(dbUserDTO.getPassword())) {
      result.setMsg("旧密码错误，请重新输入");
      return result;
    }

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppUser appUser = writer.getById(AppUser.class, dbUserDTO.getId());
      appUser.setPassword(EncryptionUtil.computeMD5Improved(appUserDTO.getNewPassword()));
      writer.saveOrUpdate(appUser);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    result.setSuccess(true);
    result.setMsg(ValidateMsg.PASSWORD_RESET_SUCCESS.getValue());
    return result;
  }

  public AppUserLoginInfoDTO getAppUserLoginInfoBySessionId(String sessionId) {
    UserWriter writer = userDaoManager.getWriter();
    AppUserLoginInfo log = writer.getAppUserLoginInfoBySessionId(sessionId);
    AppUserLoginInfoDTO dto = null;
    if (log != null) {
      dto = log.toDTO();
      AppUserDTO appUserDTO = this.getAppUserByUserNo(log.getAppUserNo(), null);
      dto.from(appUserDTO);
    }
    return dto;
  }


  @Override
  public boolean updateAppUserLoginInfoSuccess(String sessionId, String newSessionId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppUserLoginInfo log = writer.getAppUserLoginInfoBySessionId(sessionId);
      if (log == null) return false;
      log.setSessionId(newSessionId);
      log.setSessionCreateTime(System.currentTimeMillis());
      writer.update(log);
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ApiResponse login(LoginDTO loginDTO) {
    ApiResponse apiResponse;
    String result = loginValidate(loginDTO);
    if (loginDTO.isSuccess(result)) {
      //cache and db
      updateAppUserLoginInfo(loginDTO);
      //get login logic data
      return getApiLoginResponse(loginDTO.getUserNo());
    } else {
      apiResponse = MessageCode.toApiResponse(MessageCode.LOGIN_FAIL, result, false);
      return apiResponse;
    }
  }

  @Override
  public AppConfig login(AppGuestLoginInfo loginDTO) {
    return getAppConfig();
  }

  private AppConfig getAppConfig() {
    return new AppConfig(
      ConfigUtils.getAppObdReadInterval(),
      ConfigUtils.getAppServerReadInterval(),
      ConfigUtils.getAppMileageInformInterval(),
      ConfigUtils.getCustomerServicePhone(),
      ConfigUtils.getAppRemainOilMassWarn(),
      ConfigUtils.getAppVehicleErrorCodeWarnIntervals()
    );
  }


  public ApiResponse messageCenterLogin(MQLoginMessageDTO loginMessageDTO) {
    LOG.info("messageCenterLogin,data:{}", JsonUtil.objectCHToJson(loginMessageDTO));
    String appUserNo = loginMessageDTO.getName();
    if (StringUtil.isEmpty(appUserNo)) {
      return MessageCode.toApiResponse(MessageCode.LOGIN_USER_NO_EMPTY);
    }
    if (StringUtil.isEmpty(loginMessageDTO.getPass())) {
      return MessageCode.toApiResponse(MessageCode.PASSWORD_NO_EMPTY);
    }
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
    if (appUserDTO == null) {
      return MessageCode.toApiResponse(MessageCode.LOGIN_USER_NOT_EXIST);
    }
    AppUserLoginInfoDTO loginInfoDTO = appUserService.getAppUserLoginInfoByUserNo(appUserNo, AppUserType.MIRROR);
    LOG.info("loginInfoDTO,data:{}", JsonUtil.objectCHToJson(loginInfoDTO));
    if (loginInfoDTO == null || StringUtil.isEmpty(loginInfoDTO.getSessionId())) {
      return MessageCode.toApiResponse(MessageCode.LOGIN_USER_RE_LOGIN);
    }
    if (!loginInfoDTO.getSessionId().equals(loginMessageDTO.getPass())) {
      return MessageCode.toApiResponse(MessageCode.PASSWORD_IS_WRONG);
    }
//    loginInfoDTO.setMqSessionId(loginMessageDTO.getMqSessionId());
//    appUserService.saveOrUpdateAppUserLoginInfo(loginInfoDTO);
    return MessageCode.toApiResponse(MessageCode.LOGIN_SUCCESS);
  }


  //获取当前用户的自定义配置
  @Override
  public Map<String, String> getAppUserConfig(String appUserNo, String defaultVehicleNo) {
    Map<String, String> appUserConfigMap = new HashMap<String, String>();
    UserWriter writer = userDaoManager.getWriter();
    List<AppUserConfig> appUserConfigs = writer.getAppUserConfigByAppUserNo(appUserNo);
    boolean isHaveFirstDriveLogCreateTime = false;
    boolean isHaveAppUserOilPrice = false;
    if (CollectionUtils.isNotEmpty(appUserConfigs)) {
      for (AppUserConfig appUserConfig : appUserConfigs) {
        if (StringUtils.isNotBlank(appUserConfig.getName())) {
          appUserConfigMap.put(appUserConfig.getName(), appUserConfig.getValue());
          if (AppUserConfigConstant.FIRST_DRIVE_LOG_CREATE_TIME.equals(appUserConfig.getName())) {
            isHaveFirstDriveLogCreateTime = true;
          }
          if (AppUserConfigConstant.OIL_PRICE.equals(appUserConfig.getName())) {
            isHaveAppUserOilPrice = true;
          }
        }
      }
    }
    if (!isHaveFirstDriveLogCreateTime) {
      appUserConfigMap.put(AppUserConfigConstant.FIRST_DRIVE_LOG_CREATE_TIME, StringUtil.valueOf(System.currentTimeMillis()));
    }
    if (!isHaveAppUserOilPrice && StringUtils.isNotEmpty(defaultVehicleNo)) {
      IAppUserConfigService appUserConfigService = ServiceManager.getService(IAppUserConfigService.class);
      JuheCityOilPriceDTO juheCityOilPriceDTO = appUserConfigService.getJuheCityOilPriceDTOByFirstCarNo(defaultVehicleNo.substring(0, 1));
      if (juheCityOilPriceDTO != null && juheCityOilPriceDTO.getE93() != null) {
        appUserConfigMap.put(AppUserConfigConstant.OIL_KIND, AppUserConfigConstant.OIL_KIND_93);
        appUserConfigMap.put(AppUserConfigConstant.OIL_PRICE, StringUtil.valueOf(juheCityOilPriceDTO.getE93()));
      }
    }
    return appUserConfigMap;
  }

  private String loginValidate(LoginDTO loginDTO) {
    String result = loginDTO.validate();
    if (loginDTO.isSuccess(result)) {
      AppUserDTO userDTO = getAppUserByUserNo(loginDTO.getUserNo(), null);
      if (userDTO == null) {
        return ValidateMsg.APP_USER_NOT_EXIST.getValue();
      }
      if (!userDTO.getPassword().equals(loginDTO.computeMD5())) {
        return ValidateMsg.APP_USER_LOGIN_ERROR.getValue();
      }
      loginDTO.setAppUserType(userDTO.getAppUserType());
    }
    return result;
  }

  private ApiResponse getApiLoginResponse(String appUserNo) {
    ApiLoginResponse apiLoginResponse = new ApiLoginResponse(MessageCode.toApiResponse(MessageCode.LOGIN_SUCCESS));
    //set config
    apiLoginResponse.setAppConfig(getAppConfig());
    //set obd vehicle
    UserWriter writer = userDaoManager.getWriter();
    List<ObdUserVehicle> ouvList = writer.getObdUserVehicleByAppUserNo(appUserNo);
    Set<Long> obdIds = new HashSet<Long>();
    for (ObdUserVehicle entity : ouvList) {
      obdIds.add(entity.getObdId());
    }

    Map<Long, AppVehicleDTO> appVehicleDTOMap = getAppVehicleByAppUserNo(writer, appUserNo);
    Map<Long, ObdDTO> obdDTOMap = getObdDTOMap(writer, obdIds);

    //set appUserConfig
    String defaultVehicleNo = getDefaultVehicleNo(appVehicleDTOMap);
    apiLoginResponse.setAppUserConfig(getAppUserConfig(appUserNo, defaultVehicleNo));


    ObdInfo obdInfo;
    AppVehicleDTO appVehicleDTO;
    ObdDTO obdDTO;
    boolean isDefault = true;
    for (ObdUserVehicle entity : ouvList) {
      obdInfo = new ObdInfo();
      obdDTO = obdDTOMap.get(entity.getObdId());
      if (obdDTO == null) continue;
      obdInfo.setOBD(obdDTO);
      appVehicleDTO = appVehicleDTOMap.get(entity.getAppVehicleId());
      if (appVehicleDTO != null && ObdUserVehicleStatus.BUNDLING == entity.getStatus()) {
        obdInfo.setVehicleInfo(appVehicleDTO);
        if (isDefault) {
          obdInfo.setIsDefault(1);
          isDefault = false;
        }
        appVehicleDTOMap.remove(entity.getAppVehicleId());
      }
      apiLoginResponse.getObdList().add(obdInfo);
    }
    if (MapUtils.isNotEmpty(appVehicleDTOMap)) {
      for (AppVehicleDTO dto : appVehicleDTOMap.values()) {
        obdInfo = new ObdInfo();
        obdInfo.setVehicleInfo(dto);
        apiLoginResponse.getObdList().add(obdInfo);
      }
    }
    return apiLoginResponse;
  }

  //从appVehicleDTOMap中取出默认车辆的车牌号
  private String getDefaultVehicleNo(Map<Long, AppVehicleDTO> appVehicleDTOMap) {
    String defaultVehicleNo = null;
    if (MapUtils.isNotEmpty(appVehicleDTOMap)) {
      for (AppVehicleDTO appVehicleDTO : appVehicleDTOMap.values()) {
        if (appVehicleDTO != null && YesNo.YES.equals(appVehicleDTO.getIsDefault())) {
          defaultVehicleNo = appVehicleDTO.getVehicleNo();
          break;
        }
      }
    }
    return defaultVehicleNo;
  }


  private Map<Long, ObdDTO> getObdDTOMap(UserWriter writer, Set<Long> obdIds) {
    List<OBD> obdList = writer.getObdById(obdIds.toArray(new Long[obdIds.size()]));
    Map<Long, ObdDTO> obdDTOMap = new HashMap<Long, ObdDTO>();
    for (OBD entity : obdList) {
      obdDTOMap.put(entity.getId(), entity.toDTO());
    }
    return obdDTOMap;
  }

  private Map<Long, AppVehicleDTO> getAppVehicleByAppUserNo(UserWriter writer, String appUserNo) {
    List<AppVehicle> appVehicleList = writer.getAppVehicleByAppUserNo(appUserNo);
    Map<Long, AppVehicleDTO> map = new HashMap<Long, AppVehicleDTO>();
    for (AppVehicle entity : appVehicleList) {
      map.put(entity.getId(), entity.toDTO());
    }
    return map;
  }

  @Override
  public void updateVehicle(String appUserNo, Double curMil) {
    if (StringUtil.isEmpty(appUserNo)) {
      return;
    }
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppVehicle appVehicle = writer.getById(AppVehicle.class, appVehicleDTO.getVehicleId());
      if (appVehicle != null) {
        appVehicle.setCurrentMileage(curMil);
      }
      writer.update(appVehicle);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private void updateAppUserLoginInfo(LoginDTO loginDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppUserLoginInfo appUserLoginInfo = writer.getAppUserLoginInfoByUserNo(loginDTO.getUserNo(), loginDTO.getAppUserType());
      if (appUserLoginInfo == null) {
        AppUserLoginInfo info = new AppUserLoginInfo(loginDTO);
        writer.save(info);
        writer.commit(status);
      } else {
        String oldSessionId = appUserLoginInfo.getSessionId();
        appUserLoginInfo.from(loginDTO);
        writer.update(appUserLoginInfo);
        writer.commit(status);
        if (StringUtil.isNotEmpty(oldSessionId)) {
          MemCacheAdapter.delete(MemcachePrefix.apiSession.getValue() + oldSessionId);
        }
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateAppUserLoginInfo(AppUserLoginInfoDTO loginInfoDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    AppUserLoginInfo loginInfo = null;
    try {
      if (loginInfoDTO.getId() != null) {
        loginInfo = writer.getById(AppUserLoginInfo.class, loginInfoDTO.getId());
      } else {
        loginInfo = new AppUserLoginInfo();
      }
      loginInfo.fromDTO(loginInfoDTO);
      writer.saveOrUpdate(loginInfo);
      writer.commit(status);
      loginInfoDTO.setId(loginInfo.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ApiResponse logout(String appUserNo, AppUserType appUserType) {
    ApiResponse apiResponse;
    if (!StringUtil.isEmpty(appUserNo)) {
      if (appUserType != AppUserType.BCGOGO_SHOP_OWNER) {
        AppUserDTO userDTO = getAppUserByUserNo(appUserNo, null);
        if (userDTO == null) {
          return MessageCode.toApiResponse(MessageCode.LOGIN_FAIL, ValidateMsg.APP_USER_NO_EMPTY);
        }
      } else {
        UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(appUserNo);
        if (userDTO == null) {
          return MessageCode.toApiResponse(MessageCode.LOGIN_FAIL, ValidateMsg.APP_USER_NO_EMPTY);
        }
      }
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      String sessionId = null;
      try {
        AppUserLoginInfo appUserLoginInfo = writer.getAppUserLoginInfoByUserNo(appUserNo, appUserType);
        if (appUserLoginInfo != null) {
          sessionId = appUserLoginInfo.getSessionId();
          appUserLoginInfo.setLogoutTime(System.currentTimeMillis());
          appUserLoginInfo.setSessionCreateTime(-1L);
          appUserLoginInfo.setSessionId(null);
          writer.update(appUserLoginInfo);
          writer.commit(status);
          MemCacheAdapter.delete(MemcachePrefix.apiSession.getValue() + sessionId);
        } else {
          return MessageCode.toApiResponse(MessageCode.LOGIN_FAIL, ValidateMsg.APP_USER_NO_EMPTY);
        }
      } finally {
        writer.rollback(status);
      }
      apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.LOGOUT_SUCCESS));
      apiResponse.setDebug(sessionId);
    } else {
      apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.LOGOUT_FAIL, ValidateMsg.APP_USER_NO_EMPTY));
    }
    return apiResponse;
  }

  @Override
  public ApiResponse retrievePassword(String appUserNo) throws SmsException {
    if (StringUtil.isEmpty(appUserNo))
      return MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_FAIL, ValidateMsg.APP_USER_NO_EMPTY);
    ApiResponse apiResponse;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppUser appUser = getAppUsers(appUserNo, null, writer);
      if (appUser == null) {
        apiResponse = MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_FAIL, ValidateMsg.APP_USER_NO_ILLEGAL);
      } else if (appUser.isSendSMSLimited(ConfigUtils.getAppUserSendSMSLimits())) {
        apiResponse = MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_FAIL, ValidateMsg.PASSWORD_RESET_NUMBER_TOO_MUCH);
      } else {
        String newPassword = ServiceManager.getService(IUserService.class).generatePassword();
        appUser.setPassword(EncryptionUtil.computeMD5Improved(newPassword));
        if (ServiceManager.getService(ISmsService.class)
          .sendResetAppUserPasswordSMS(appUser.getAppUserNo(), newPassword, appUser.getMobile(), appUser.getName(), AppUserType.BLUE_TOOTH)) {
          appUser.addSendSMSLimited();
          writer.update(appUser);
          writer.commit(status);
          apiResponse = MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_SUCCESS);
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_EXCEPTION);
        }
      }
      return apiResponse;
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 根据手机端用户账号获得所有车辆。
   *
   * @param userNo
   * @return
   */
  public List<AppVehicleDTO> getAppVehicleDTOByAppUserNo(String userNo) {
    List<AppVehicle> appVehicleList = getAppVehicleByAppUserNo(userNo);
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
    if (CollectionUtils.isEmpty(appVehicleList)) {
      return appVehicleDTOList;
    }

    for (AppVehicle appVehicle : appVehicleList) {
      appVehicleDTOList.add(appVehicle.toDTO());
    }
    return appVehicleDTOList;
  }

  /**
   * 根据手机端用户账号获得所有车辆。
   *
   * @param userNo
   * @return
   */
  public List<AppVehicle> getAppVehicleByAppUserNo(String userNo) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
    return userWriter.getAppVehicleByAppUserNo(userNo);
  }

  @Override
  public AppVehicleDTO getDefaultAppVehicleByAppUserNo(String userNo) {
    if (StringUtils.isEmpty(userNo)) {
      return null;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    List<AppVehicle> appVehicleList = userWriter.getAppVehicleByAppUserNo(userNo);
    if (CollectionUtils.isNotEmpty(appVehicleList)) {
      for (AppVehicle appVehicle : appVehicleList) {
        if (YesNo.YES.equals(appVehicle.getIsDefault())) {
          return appVehicle.toDTO();
        }
      }
    }
    return null;
  }

  @Override
  public List<AppVehicleDTO> getAppVehicleByAppUserNoAndVehicleNo(String userNo, String vehicleNo) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
    if (StringUtils.isEmpty(userNo) || StringUtils.isEmpty(vehicleNo)) {
      return appVehicleDTOList;
    }
    List<AppVehicle> appVehicleList = userWriter.getAppVehicleByAppUserNoAndVehicleNo(userNo, vehicleNo, null);
    if (CollectionUtils.isEmpty(appVehicleList)) {
      return appVehicleDTOList;
    }

    for (AppVehicle appVehicle : appVehicleList) {
      appVehicleDTOList.add(appVehicle.toDTO());
    }
    return appVehicleDTOList;
  }

  @Override
  public Set<String> getAppVehicleNosByAppUserNo(String userNo) {
    return userDaoManager.getWriter().getAppVehicleNoByAppUserNo(userNo);

  }

  @Override
  public Long getCustomerIdInAppUserCustomer(Long shopId, String userNo, String mobile) {
    UserWriter writer = userDaoManager.getWriter();
    List<AppUserCustomer> appUserCustomerList = writer.getAppUserCustomer(userNo, shopId);
    if (CollectionUtils.isNotEmpty(appUserCustomerList)) {
      if (appUserCustomerList.size() == 1) {
        return appUserCustomerList.get(0).getCustomerId();
      } else {
        if (mobile.isEmpty()) {
          return null;
        }
        List<Long> customerIds = new ArrayList<Long>();
        for (AppUserCustomer entity : appUserCustomerList) {
          customerIds.add(entity.getCustomerId());
        }
        Map<Long, List<Contact>> contacts = writer.getContactsByCusIds(customerIds);
        if (MapUtils.isNotEmpty(contacts)) {
          for (List<Contact> contactList : contacts.values()) {
            for (Contact contact : contactList) {
              if (mobile.equals(contact.getMobile())) {
                return contact.getCustomerId();
              }
            }
          }
        }
      }
    }
    return null;
  }

  @Override
  public List<Long> getCustomerIdInAppUserCustomer(String... appUserNos) {
    if (ArrayUtil.isEmpty(appUserNos)) return new ArrayList<Long>();
    return userDaoManager.getWriter().getCustomerIdInAppUserCustomer(appUserNos);
  }

  @Override
  public List<CustomerDTO> getCustomerByAppUserId(Long shopId, Long appUserId) {
    List<Customer> customerList = userDaoManager.getWriter().getCustomerByAppUserId(appUserId);
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    if (CollectionUtil.isNotEmpty(customerList)) {
      for (Customer customer : customerList) {
        customerDTOs.add(customer.toDTO());
      }
    }
    return customerDTOs;
  }

  @Override
  public AppUserLoginInfoDTO getAppUserLoginInfoByUserNo(String appUserNo, AppUserType appUserType) {
    if (StringUtil.isEmpty(appUserNo)) return null;
    UserWriter writer = userDaoManager.getWriter();
    AppUserLoginInfo appUserLoginInfo = writer.getAppUserLoginInfoByUserNo(appUserNo, appUserType);
    return appUserLoginInfo == null ? null : appUserLoginInfo.toDTO();
  }

  @Override
  public AppUserLoginInfoDTO getAppUserLoginInfoByMqSessionId(String mqSessionId) {
    UserWriter writer = userDaoManager.getWriter();
    AppUserLoginInfo loginInfo = writer.getAppUserLoginInfoByMqSessionId(mqSessionId, AppUserType.MIRROR);
    return loginInfo != null ? loginInfo.toDTO() : null;
  }

  @Override
  public void updateAppUserLastExpenseShopId(Long shopId, String appUserNo) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppUser appUser = getAppUsers(appUserNo, null, writer);
      if (appUser != null) {
        appUser.setLastExpenseShopId(shopId);
        writer.update(appUser);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<String, AppUserDTO> getAppUserMapByUserNo(Set<String> appUserNoSet) {
    UserWriter writer = userDaoManager.getWriter();
    Map<String, AppUserDTO> map = new HashMap<String, AppUserDTO>();
    if (CollectionUtils.isEmpty(appUserNoSet)) return map;
    List<AppUser> appUserList = writer.getAppUserMapByUserNo(appUserNoSet);
    if (CollectionUtils.isNotEmpty(appUserList)) {
      for (AppUser entity : appUserList) {
        map.put(entity.getAppUserNo(), entity.toDTO());
      }
    }
    return map;
  }

  @Override
  public AppUserDTO getAppUserByUserNo(String appUserNo) {
    Set<String> appUserNoSet = new HashSet<String>();
    appUserNoSet.add(appUserNo);
    Map<String, AppUserDTO> appUserDTOMap = getAppUserMapByUserNo(appUserNoSet);
    return MapUtils.isNotEmpty(appUserDTOMap) ? appUserDTOMap.get(appUserNo) : null;
  }

  @Override
  public Map<String, AppUserDTO> getAppUserMapByImeis(Set<String> imeis) {
    UserWriter writer = userDaoManager.getWriter();
    Map<String, AppUserDTO> result = new HashMap<String, AppUserDTO>();
    if (CollectionUtils.isEmpty(imeis)) return result;

    List<Object[]> obdUserVehicles = writer.getOBDUserVehicleByObdImeis(imeis);
    Map<String, String> appUserNoImeiMap = new HashMap<String, String>();
    for (Object[] objects : obdUserVehicles) {
      if (!ArrayUtils.isEmpty(objects) && objects.length == 2) {
        String imei = (String) objects[0];
        ObdUserVehicle obdUserVehicle = (ObdUserVehicle) objects[1];
        if (obdUserVehicle != null && StringUtils.isNotBlank(obdUserVehicle.getAppUserNo()) && StringUtils.isNotBlank(imei)) {
          appUserNoImeiMap.put(obdUserVehicle.getAppUserNo(), imei);
        }
      }
    }
    List<AppUser> appUserList = writer.getAppUserMapByUserNo(appUserNoImeiMap.keySet());
    if (CollectionUtils.isNotEmpty(appUserList)) {
      for (AppUser entity : appUserList) {
        if (entity != null && StringUtils.isNotBlank(entity.getAppUserNo()) && StringUtils.isNotBlank(appUserNoImeiMap.get(entity.getAppUserNo()))) {
          result.put(appUserNoImeiMap.get(entity.getAppUserNo()), entity.toDTO());
        }
      }
    }
    return result;
  }

  @Override
  public List<AppVehicleDTO> getMaintainMileageApproachingAppVehicle(Double[] intervals, int start, int limit, int remindTimesLimit) {
    List<AppVehicle> appVehicleList = userDaoManager.getWriter()
      .getMaintainMileageApproachingAppVehicle(intervals, start, limit, remindTimesLimit);
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
    for (AppVehicle appVehicle : appVehicleList) {
      appVehicleDTOList.add(appVehicle.toDTO());
    }
    return appVehicleDTOList;
  }

  @Override
  public List<AppVehicleDTO> getMaintainTimeApproachingAppVehicle(Long[] intervals, int start, int limit) {
    List<AppVehicle> appVehicleList = userDaoManager.getWriter()
      .getMaintainTimeApproachingAppVehicle(intervals, start, limit);
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
    for (AppVehicle appVehicle : appVehicleList) {
      appVehicleDTOList.add(appVehicle.toDTO());
    }
    return appVehicleDTOList;
  }

  @Override
  public List<AppVehicleDTO> getInsuranceTimeApproachingAppVehicle(Long[] intervals, int start, int limit) {
    List<AppVehicle> appVehicleList = userDaoManager.getWriter()
      .getInsuranceTimeApproachingAppVehicle(intervals, start, limit);
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
    for (AppVehicle appVehicle : appVehicleList) {
      appVehicleDTOList.add(appVehicle.toDTO());
    }
    return appVehicleDTOList;
  }

  @Override
  public List<AppVehicleDTO> getExamineTimeApproachingAppVehicle(Long[] intervals, int start, int limit) {
    List<AppVehicle> appVehicleList = userDaoManager.getWriter()
      .getExamineTimeApproachingAppVehicle(intervals, start, limit);
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
    for (AppVehicle appVehicle : appVehicleList) {
      appVehicleDTOList.add(appVehicle.toDTO());
    }
    return appVehicleDTOList;
  }

  @Override
  public List<AppUserCustomerDTO> getAppUserCustomersByCustomerIds(Long[] customerIds) {
    List<AppUserCustomerDTO> dtoList = new ArrayList<AppUserCustomerDTO>();
    if (ArrayUtil.isNotEmpty(customerIds)) {
      UserWriter writer = userDaoManager.getWriter();
      List<AppUserCustomer> list = writer.getAppUserCustomersByCustomerIds(customerIds);
      Set<String> appUserNos = new HashSet<String>();
      for (AppUserCustomer entity : list) {
        appUserNos.add(entity.getAppUserNo());
      }
      AppUserCustomerDTO dto;
      Map<String, AppUserDTO> map = getAppUserMapByUserNo(appUserNos);
      for (AppUserCustomer entity : list) {
        dto = entity.toDTO();
        dto.setAppUserDTO(map.get(entity.getAppUserNo()));
        dtoList.add(dto);
      }
    }
    return dtoList;
  }

  @Override
  public List<AppUserCustomer> getAppUserCustomerByCustomerId(Long shopId, Long customerId) {
    UserWriter writer = userDaoManager.getWriter();
    List<AppUserCustomer> appUserCustomer = null;
    if (customerId == null || shopId == null) {
      return null;
    }
    appUserCustomer = writer.getAppUserCustomerByCustomerId(shopId, customerId);
    return appUserCustomer;
  }

  @Deprecated
  @Override
  public Long getDefaultOBDSellerShopId(String appUserNo) {
    return userDaoManager.getReader().getDefaultOBDSellerShopId(appUserNo);
  }

  @Override
  public Long getOBDSellerShopIdByVehicleId(Long vehicleId) {
    return userDaoManager.getReader().getOBDSellerShopIdByVehicleId(vehicleId);
  }

  @Override
  public Map<Long, AppUserDTO> getAppUserInfoListFromCustomer(Long shopId, String keyword) {
    if (RegexUtils.isMobile(keyword)) {
      return getAppUserInfoListFromCustomerMobile(shopId, keyword, 10);
    } else if (RegexUtils.isVehicleNo(keyword)) {
      return getAppUserInfoListFromVehicleNo(shopId, keyword, 10);
    }
    return null;
  }

  private Map<Long, AppUserDTO> getAppUserInfoListFromCustomerMobile(Long shopId, String keyword, int limit) {
    UserReader reader = userDaoManager.getReader();
    UserWriter writer = userDaoManager.getWriter();
    Map<Long, AppUserDTO> customerIdAppUserMapping = new HashMap<Long, AppUserDTO>();
    List<Contact> contacts = reader.getContactsByCustomerMobile(shopId, keyword, limit);
    Set<Long> customerIds = new HashSet<Long>();
    Map<Long, List<ContactDTO>> contactMap = new HashMap<Long, List<ContactDTO>>();
    Long cusId;
    List<ContactDTO> cList;
    for (Contact entity : contacts) {
      cusId = entity.getCustomerId();
      if (cusId == null) continue;
      cList = contactMap.get(cusId);
      if (!customerIds.add(cusId)) continue;
      if (cList == null) {
        cList = new ArrayList<ContactDTO>();
        contactMap.put(cusId, cList);
      }
      cList.add(entity.toDTO());
    }
    List<CustomerDTO> customerDTOs = writer.getCustomerByIds(new ArrayList<Long>(customerIds));
    List<CustomerVehicle> customerVehicleList = writer.getCustomerVehicleByCustomerIds(customerIds);
    Set<Long> vehicleIds = new HashSet<Long>();
    for (CustomerVehicle cv : customerVehicleList) {
      vehicleIds.add(cv.getId());
    }
    Map<Long, VehicleDTO> vehicleMap = writer.getVehicleByVehicleIdSet(shopId, vehicleIds);
    Map<Long, List<VehicleDTO>> customerIdVehicleMap = new HashMap<Long, List<VehicleDTO>>();
    for (CustomerVehicle cv : customerVehicleList) {
      VehicleDTO vehicleDTO = vehicleMap.get(cv.getVehicleId());
      if (vehicleDTO == null) continue;
      vehicleDTO.from(cv.toDTO());
      List<VehicleDTO> cvDTOList = customerIdVehicleMap.get(cv.getCustomerId());
      if (cvDTOList == null) {
        cvDTOList = new ArrayList<VehicleDTO>();
        customerIdVehicleMap.put(cv.getCustomerId(), cvDTOList);
      }
      cvDTOList.add(vehicleDTO);
    }
    AppUserDTO appUserDTO;
    for (CustomerDTO dto : customerDTOs) {
      appUserDTO = dto.toAppUserDTO(contactMap.get(dto.getId()), customerIdVehicleMap.get(dto.getId()));
      customerIdAppUserMapping.put(dto.getId(), appUserDTO);
    }
    return customerIdAppUserMapping;
  }

  private Map<Long, AppUserDTO> getAppUserInfoListFromVehicleNo(Long shopId, String keyword, int limit) {
    UserReader reader = userDaoManager.getReader();
    UserWriter writer = userDaoManager.getWriter();
    Map<Long, AppUserDTO> customerIdAppUserMapping = new HashMap<Long, AppUserDTO>();
    List<Vehicle> vehicleList = writer.getVehicleByShopIdOrLicenceNo(shopId, keyword, limit);
    Map<Long, VehicleDTO> vehicleMap = new HashMap<Long, VehicleDTO>();
    Set<Long> vehicleIds = new HashSet<Long>();
    for (Vehicle v : vehicleList) {
      vehicleIds.add(v.getId());
      vehicleMap.put(v.getId(), v.toDTO());
    }
    Map<Long, List<VehicleDTO>> customerIdVehicleMap = new HashMap<Long, List<VehicleDTO>>();
    List<CustomerVehicle> customerVehicleList = reader.getCustomerVehicleIdByVehicleIds(vehicleIds);
    for (CustomerVehicle cv : customerVehicleList) {
      List<VehicleDTO> vehicleDTOs = customerIdVehicleMap.get(cv.getCustomerId());
      if (vehicleDTOs == null) {
        vehicleDTOs = new ArrayList<VehicleDTO>();
        customerIdVehicleMap.put(cv.getCustomerId(), vehicleDTOs);
      }
      VehicleDTO vehicleDTO = vehicleMap.get(cv.getVehicleId());
      vehicleDTO.from(cv.toDTO());
      vehicleDTOs.add(vehicleDTO);
    }
    List<Long> customerIds = new ArrayList<Long>(customerIdVehicleMap.keySet());
    List<CustomerDTO> customerDTOs = writer.getCustomerByIds(customerIds);
    Map<Long, List<ContactDTO>> contactMap = writer.getContactDTOsByCusIds(customerIds);
    AppUserDTO appUserDTO;
    for (CustomerDTO dto : customerDTOs) {
      appUserDTO = dto.toAppUserDTO(contactMap.get(dto.getId()), customerIdVehicleMap.get(dto.getId()));
      customerIdAppUserMapping.put(dto.getId(), appUserDTO);
    }
    return customerIdAppUserMapping;
  }


  @Override
  public List<AppUserCustomerDTO> getAppUserCustomerByAppUserNoAndShopId(String appUserNo, Long shopId) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (StringUtils.isNotBlank(appUserNo) && shopId != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomer(appUserNo, shopId);
      if (CollectionUtils.isNotEmpty(appUserCustomers)) {
        for (AppUserCustomer appUserCustomer : appUserCustomers) {
          appUserCustomerDTOs.add(appUserCustomer.toDTO());
        }
      }
    }
    return appUserCustomerDTOs;
  }
  @Override
  public List<AppUserCustomerDTO> getAppUserCustomerByAppUserNo(String appUserNo) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (StringUtils.isNotBlank(appUserNo)) {
      UserWriter writer = userDaoManager.getWriter();
      List<AppUserCustomer> appUserCustomers  = writer.getAppUserCustomersByAppUserNo(appUserNo);
      if (CollectionUtils.isNotEmpty(appUserCustomers)) {
        for (AppUserCustomer appUserCustomer : appUserCustomers) {
          appUserCustomerDTOs.add(appUserCustomer.toDTO());
        }
      }
    }
    return appUserCustomerDTOs;
  }
  @Override
  public List<AppUserCustomerDTO> getAppUserCustomerByShopId(Long shopId) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (shopId != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomer(shopId);
      if (CollectionUtils.isNotEmpty(appUserCustomers)) {
        for (AppUserCustomer appUserCustomer : appUserCustomers) {
          appUserCustomerDTOs.add(appUserCustomer.toDTO());
        }
      }
    }
    return appUserCustomerDTOs;
  }


  @Override
  public Map<String, List<AppUserCustomerDTO>> getAppUserCustomerMapByAppUserNosAndShopId(Set<String> appUserNos, Long shopId) {
    if (CollectionUtils.isNotEmpty(appUserNos) && shopId != null) {
      return new HashMap<String, List<AppUserCustomerDTO>>();
    }
    UserWriter writer = userDaoManager.getWriter();
    List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomerByAppUserNo(appUserNos, shopId);
    Map<String, List<AppUserCustomerDTO>> appUserNoMap = new HashMap<String, List<AppUserCustomerDTO>>();
    if (CollectionUtils.isNotEmpty(appUserCustomers)) {
      for (AppUserCustomer appUserCustomer : appUserCustomers) {
        if (appUserCustomer != null) {
          List<AppUserCustomerDTO> appUserCustomerDTOs = appUserNoMap.get(appUserCustomer.getAppUserNo());
          if (appUserCustomerDTOs == null) {
            appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
          }
          appUserCustomerDTOs.add(appUserCustomer.toDTO());
          appUserNoMap.put(appUserCustomer.getAppUserNo(), appUserCustomerDTOs);
        }
      }
    }
    return appUserNoMap;
  }

  public Map<Long, CustomerDTO> getCustomerDTOMapByObdIds(Long shopId, Set<Long> obdIds) {
    if (CollectionUtils.isEmpty(obdIds) || shopId == null) {
      return new HashMap<Long, CustomerDTO>();
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Object[]> objects = writer.getCustomerByObdIds(shopId, obdIds);
    Map<Long, CustomerDTO> customerDTOMap = new HashMap<Long, CustomerDTO>();
    if (CollectionUtil.isNotEmpty(objects)) {
      for (Object[] object : objects) {
        Customer customer = (Customer) object[1];
        customerDTOMap.put(NumberUtil.longValue(object[0]), customer.toDTO());
      }
    }
    return customerDTOMap;
  }

  public Map<Long, VehicleDTO> getVehicleDTOMapByObdIds(Long shopId, Set<Long> obdIds) {
    if (CollectionUtils.isEmpty(obdIds) || shopId == null) {
      return new HashMap<Long, VehicleDTO>();
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Object[]> objects = writer.getVehicleDTOMapByObdIds(shopId, obdIds);
    Map<Long, VehicleDTO> customerDTOMap = new HashMap<Long, VehicleDTO>();
    if (CollectionUtil.isNotEmpty(objects)) {
      for (Object[] object : objects) {
        Vehicle vehicle = (Vehicle) object[1];
        customerDTOMap.put(NumberUtil.longValue(object[0]), vehicle.toDTO());
      }
    }
    return customerDTOMap;
  }

  @Override
  public ApiResponse validateUpdateAppUserConfigByAppUser(AppUserConfigUpdateRequest appUserConfigUpdateRequest) {
    if (appUserConfigUpdateRequest == null || ArrayUtils.isEmpty(appUserConfigUpdateRequest.getAppUserConfigDTOs())) {
      return MessageCode.toApiResponse(MessageCode.UPDATE_APP_USER_CONFIG_FAIL,
        ValidateMsg.APP_USER_CONFIG_NOT_FOUND);
    }
    for (AppUserConfigDTO appUserConfigDTO : appUserConfigUpdateRequest.getAppUserConfigDTOs()) {
      if (!AppUserConfigConstant.configNameSet.contains(appUserConfigDTO.getName())) {
        return MessageCode.toApiResponse(MessageCode.UPDATE_APP_USER_CONFIG_FAIL,
          ValidateMsg.APP_USER_CONFIG_NOT_FOUND);
      }
      if (StringUtils.isEmpty(appUserConfigDTO.getValue())) {
        return MessageCode.toApiResponse(MessageCode.UPDATE_APP_USER_CONFIG_FAIL,
          ValidateMsg.APP_USER_CONFIG_VALUE_ILLEGALITY);
      }
    }
    return MessageCode.toApiResponse(MessageCode.UPDATE_APP_USER_CONFIG_SUCCESS);
  }

  @Override
  public ApiResponse updateAppUserConfig(AppUserConfigUpdateRequest appUserConfigUpdateRequest) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (AppUserConfigDTO appUserConfigDTO : appUserConfigUpdateRequest.getAppUserConfigDTOs()) {
        appUserConfigDTO.setSyncTime(System.currentTimeMillis());
        appUserConfigDTO.setAppUserNo(appUserConfigUpdateRequest.getAppUserNo());
        AppUserConfig appUserConfig = writer.getAppUserConfigByName(appUserConfigDTO.getAppUserNo(), appUserConfigDTO.getName());
        if (appUserConfig == null) {
          appUserConfig = new AppUserConfig();
          appUserConfig.fromDTO(appUserConfigDTO);
          writer.save(appUserConfig);
        } else {
          appUserConfig.setValue(appUserConfigDTO.getValue());
          appUserConfig.setSyncTime(appUserConfigDTO.getSyncTime());
          appUserConfig.setDescription(appUserConfigDTO.getDescription());
          writer.update(appUserConfig);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return MessageCode.toApiResponse(MessageCode.UPDATE_APP_USER_CONFIG_SUCCESS);
  }

  //校验
  private Result validateGsmAllocateAppUser(GSMRegisterDTO gsmRegisterDTO) {
    UserWriter writer = userDaoManager.getWriter();
    if (StringUtil.isEmpty(gsmRegisterDTO.getImei())) {
      return new Result(false, "注册IMEI号不能为空。");
    }
    OBD obd = writer.getObdByImeiObdType(gsmRegisterDTO.getImei(), null);
    if (obd == null) {
      return new Result(false, ValidateMsg.IMEI_OBD_NOT_EXIST.getValue());
    }
    Set<Long> obdIds = new HashSet<Long>();
    obdIds.add(obd.getId());
    List<ObdUserVehicle> obdUserVehicles = writer.getOBDUserVehicle(obdIds);
    if (CollectionUtils.isNotEmpty(obdUserVehicles)) {
      return new Result(false, ValidateMsg.IMEI_SN_HAS_BEEN_USED.getValue());
    }
    List<Vehicle> vehicleList = writer.getVehicleByGsmObdImei(gsmRegisterDTO.getImei());
    if (CollectionUtils.isEmpty(vehicleList)) {
      return new Result(false, ValidateMsg.IMEI_SN_NO_EXIST.getValue());
    } else if (vehicleList.size() > 1) {
      return new Result(false, ValidateMsg.VEHICLE_IMEI_SN_MORE_THAN_ONE.getValue());
    }
    return new Result();
  }

  /**
   * 后视镜自动分配帐号
   *
   * @param gsmRegisterDTO
   * @return
   */
  @Override
  public Result gsmAllocateAppUser(GSMRegisterDTO gsmRegisterDTO) {
    Result result = validateGsmAllocateAppUser(gsmRegisterDTO);
    if (!result.isSuccess()) return result;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //add app user
      VehicleDTO vehicleDTO = CollectionUtil.getFirst(writer.getVehicleByGsmObdImei(gsmRegisterDTO.getImei())).toDTO();
      AppUser appUser = new AppUser(gsmRegisterDTO);
      String uniqueAppUserNo = EncryptionUtil.computeMD5Improved(UUID.randomUUID().toString());
      List<AppUser> checkExistAppUsers = writer.getAppUserByUserNo(uniqueAppUserNo, null);
      while (CollectionUtils.isNotEmpty(checkExistAppUsers)) {
        uniqueAppUserNo = EncryptionUtil.computeMD5Improved(UUID.randomUUID().toString());
        checkExistAppUsers = writer.getAppUserByUserNo(uniqueAppUserNo, null);
      }
      appUser.setAppUserNo(uniqueAppUserNo);
      appUser.setPassword(EncryptionUtil.computeMD5Improved(UUID.randomUUID().toString()));
      appUser.setRegistrationShopId(vehicleDTO.getShopId());
      appUser.setMobile(vehicleDTO.getGsmObdImeiMoblie());
      appUser.setAppUserType(AppUserType.MIRROR);
      writer.save(appUser);
      AppUserDTO appUserDTO = appUser.toDTO();
      appUserDTO.setImei(gsmRegisterDTO.getImei());
      //add app vehicle
      gsmRegisterDTO.setUserNo(appUser.getAppUserNo());
      gsmRegisterDTO.setVehicleNo(vehicleDTO.getLicenceNo());
      AppVehicleDTO appVehicleDTO = gsmAddVehicle(gsmRegisterDTO, writer, vehicleDTO);
      //set obd_id to vehicle
      Vehicle vehicle = writer.getById(Vehicle.class, vehicleDTO.getId());
      OBD obd = writer.getObdByImeiObdType(gsmRegisterDTO.getImei(), null);
      vehicle.setObdId(obd.getId());
      writer.update(vehicle);
      //add obd_user_vehicle
      ObdUserVehicle obdUserVehicle = new ObdUserVehicle(gsmRegisterDTO.getUserNo(), appVehicleDTO.getVehicleId(), obd.getId());
      writer.save(obdUserVehicle);
      //add app_user_customer
      AppUserCustomer appUserCustomer = new AppUserCustomer();
      appUserCustomer.setShopId(vehicleDTO.getShopId());
      appUserCustomer.setCustomerId(vehicleDTO.getCustomerId());
      appUserCustomer.setAppUserNo(gsmRegisterDTO.getUserNo());
      appUserCustomer.setMatchType(AppUserCustomerMatchType.IMEI_MATCH);
      appUserCustomer.setShopVehicleId(vehicleDTO.getId());
      appUserCustomer.setAppVehicleId(appVehicleDTO.getVehicleId());
      appUserCustomer.setIsMobileMatch(YesNo.NO);
      appUserCustomer.setIsVehicleNoMatch(YesNo.NO);
      List<CustomerVehicle> customerVehicles = writer.getCustomerVehicleByVehicleId(vehicle.getId());
      if (CollectionUtils.isNotEmpty(customerVehicles)) {
        CustomerVehicle customerVehicle = CollectionUtil.getFirst(customerVehicles);
        appUserCustomer.setCustomerId(customerVehicle.getCustomerId());
      }
      writer.save(appUserCustomer);
      //add app_user_shop_vehicle
      AppUserShopVehicle appUserShopVehicle = new AppUserShopVehicle();
      appUserShopVehicle.setAppUserNo(appUserDTO.getUserNo());
      appUserShopVehicle.setShopId(vehicleDTO.getShopId());
      appUserShopVehicle.setAppVehicleId(appVehicleDTO.getVehicleId());
      appUserShopVehicle.setObdId(obd.getId());
      appUserShopVehicle.setStatus(ObdUserVehicleStatus.BUNDLING);
      writer.save(appUserShopVehicle);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return new Result("注册成功。");
  }


  @Override
  public List gsmRegisterAppUser(GSMRegisterDTO gsmRegisterDTO) {
    List list = new ArrayList();
    UserWriter writer = userDaoManager.getWriter();
    String vResult = gsmRegisterAppUserValidate(gsmRegisterDTO, writer);
    if (!gsmRegisterDTO.isSuccess(vResult)) {
      ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.REGISTER_FAIL, vResult));
      list.add(apiGsmLoginResponse);
      return list;
    }
    AppUserDTO appUserDTO = null;
    ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.REGISTER_SUCCESS));
    Long shopId = null;
    Long customerId = null;
    Long vehicleId = null;

    Object status = writer.begin();
    try {
      //add app user
      VehicleDTO vehicleDTO = writer.getVehicleByGsmObdImei(gsmRegisterDTO.getImei()).get(0).toDTO();

      shopId = vehicleDTO.getShopId();
      gsmRegisterDTO.computeMD5();


      //更新其他设备的devieToken
      if (gsmRegisterDTO.getLoginInfo() != null && (StringUtils.isNotBlank(gsmRegisterDTO.getLoginInfo().getDeviceToken())
        || StringUtils.isNotBlank(gsmRegisterDTO.getLoginInfo().getUmDeviceToken()))) {
        updateOldAppUserDeviceToken(gsmRegisterDTO.getLoginInfo().getDeviceToken(), gsmRegisterDTO.getLoginInfo().getUmDeviceToken(), null, writer);
      }

      AppUser appUser = new AppUser(gsmRegisterDTO);
      String uniqueAppUserNo = appUser.getAppUserNo();
      List<AppUser> checkExistAppUsers = writer.getAppUserByUserNo(uniqueAppUserNo, null);

      while (CollectionUtils.isNotEmpty(checkExistAppUsers)) {
        uniqueAppUserNo = "2" + RandomUtils.randomNumeric(14);
        checkExistAppUsers = writer.getAppUserByUserNo(uniqueAppUserNo, null);
      }
      appUser.setAppUserNo(uniqueAppUserNo);
      gsmRegisterDTO.setUserNo(appUser.getAppUserNo());
      appUser.setRegistrationShopId(vehicleDTO.getShopId());

      LOG.info("AOP:appUser:{}", JsonUtil.objectToJson(appUser));
      writer.save(appUser);
      appUserDTO = appUser.toDTO();
      appUserDTO.setImei(gsmRegisterDTO.getImei());
      //add app vehicle
      AppVehicleDTO appVehicleDTO = gsmAddVehicle(gsmRegisterDTO, writer, vehicleDTO);
      if (appVehicleDTO != null) {
        appUserDTO.setAppVehicleDTO(appVehicleDTO);
        IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
        GsmPoint gsmPoint = gsmPointService.getLastGsmPointByImei(appUserDTO.getImei());
        if (gsmPoint != null && NumberUtil.doubleVal(gsmPoint.getLon()) > 0 && NumberUtil.doubleVal(gsmPoint.getLat()) > 0) {
          appVehicleDTO.setCoordinateLat(NumberUtil.convertGPSLat(gsmPoint.getLat()));
          appVehicleDTO.setCoordinateLon(NumberUtil.convertGPSLot(gsmPoint.getLon()));
        }

        apiGsmLoginResponse.setAppVehicleDTO(appVehicleDTO);
      }


      Vehicle vehicle = writer.getById(Vehicle.class, vehicleDTO.getId());

      if (vehicle != null) {
        if (StringUtils.isNotBlank(appVehicleDTO.getVehicleNo()) && !appVehicleDTO.getVehicleNo().equals(vehicle.getLicenceNo())) {
          vehicle.updateLicenceNo(appVehicleDTO.getVehicleNo());
        }
      }
      appUserDTO.setGsmObdImeiMoblie(vehicle.getGsmObdImeiMoblie());
      apiGsmLoginResponse.setAppUserDTO(appUserDTO);

      //add obd
//      OBD obd = new OBD();
//      obd.setImei(gsmRegisterDTO.getImei());
//      obd.setSellShopId(vehicle.getShopId());
//      obd.setSellTime(System.currentTimeMillis());
//      obd.setObdType(ObdType.GSM);
//      writer.save(obd);
      OBD obd = writer.getObdByImeiObdType(gsmRegisterDTO.getImei(), null);

      //set obd_id to vehicle
      vehicle.setObdId(obd.getId());
      writer.update(vehicle);
      //add obd_user_vehicle
      ObdUserVehicle obdUserVehicle = new ObdUserVehicle(gsmRegisterDTO.getUserNo(), appVehicleDTO.getVehicleId(), obd.getId());
      writer.save(obdUserVehicle);


      List<CustomerVehicle> customerVehicles = writer.getCustomerVehicleByVehicleId(vehicle.getId());

      //add app_user_customer
      AppUserCustomer appUserCustomer = new AppUserCustomer();
      appUserCustomer.setShopId(vehicleDTO.getShopId());
      appUserCustomer.setCustomerId(vehicleDTO.getCustomerId());
      appUserCustomer.setAppUserNo(gsmRegisterDTO.getUserNo());
      appUserCustomer.setMatchType(AppUserCustomerMatchType.IMEI_MATCH);
      appUserCustomer.setShopVehicleId(vehicleDTO.getId());
      appUserCustomer.setAppVehicleId(appVehicleDTO.getVehicleId());
      appUserCustomer.setIsMobileMatch(YesNo.NO);
      appUserCustomer.setIsVehicleNoMatch(YesNo.NO);


      AppUserShopVehicle appUserShopVehicle = new AppUserShopVehicle();
      appUserShopVehicle.setAppUserNo(appUserDTO.getUserNo());
      appUserShopVehicle.setShopId(vehicleDTO.getShopId());
      appUserShopVehicle.setAppVehicleId(appVehicleDTO.getVehicleId());
      appUserShopVehicle.setObdId(obd.getId());
      appUserShopVehicle.setStatus(ObdUserVehicleStatus.BUNDLING);
      writer.save(appUserShopVehicle);

      if (CollectionUtils.isNotEmpty(customerVehicles)) {
        CustomerVehicle customerVehicle = CollectionUtil.getFirst(customerVehicles);
        customerId = customerVehicle.getCustomerId();
        vehicleId = customerVehicle.getVehicleId();
        appUserCustomer.setCustomerId(customerId);
      }
      writer.save(appUserCustomer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    //add coupon info
    OBD obd = writer.getObdByImeiObdType(gsmRegisterDTO.getImei(), null);
    if (ObdType.SGSM.equals(obd.getObdType())) {
      ICouponService couponService=ServiceManager.getService(ICouponService.class);
      CouponDTO couponDTO = new CouponDTO();
      List<CouponDTO> couponDTOList=couponService.getCouponDTOsByImei(gsmRegisterDTO.getImei());
      if(CollectionUtil.isNotEmpty(couponDTOList)&&couponDTOList.size()>0&&StringUtil.isEmpty(couponDTOList.get(0).getAppUserNo())) {
        couponDTO=CollectionUtil.getFirst(couponDTOList);
      }
      else{
        couponDTO.setCreatedTime(System.currentTimeMillis());
        couponDTO.setBalance(ConfigUtils.getCouponDefaultAmount());
        couponDTO.setImei(gsmRegisterDTO.getImei());
      }
      couponDTO.setAppUserNo(appUserDTO.getUserNo());
      couponService.saveOrUpdateCoupon(couponDTO);
    }
    //保存油价
    if (NumberUtil.doubleVal(gsmRegisterDTO.getOilPrice()) > 0) {
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserConfigDTO appUserConfigDTO = new AppUserConfigDTO();
      appUserConfigDTO.setAppUserNo(appUserDTO.getUserNo());
      appUserConfigDTO.setName(AppUserConfigConstant.OIL_PRICE);
      appUserConfigDTO.setValue(StringUtil.StringValueOf(gsmRegisterDTO.getOilPrice()));
      appUserConfigDTO.setSyncTime(System.currentTimeMillis());
      appUserService.saveOrUpdateAppUserConfig(appUserConfigDTO);
    }

    //create session id in db
    updateAppUserLoginInfo(gsmRegisterDTO.toLoginDTO());

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (shopId != null) {
      ShopDTO shopDTO = configService.getShopById(shopId);
      apiGsmLoginResponse.setShopDTO(shopDTO);

      AppShopDTO appShopDTO = new AppShopDTO(shopDTO);
      apiGsmLoginResponse.setAppShopDTO(appShopDTO);

    }
    list.add(apiGsmLoginResponse);
    list.add(customerId);
    list.add(vehicleId);
    list.add(shopId);
    return list;

  }

  //置空其他相同devicetoken的帐号，writer 如果null就做事务控制，非null就不做事务,如果thisAppUserNo
  private void updateOldAppUserDeviceToken(String deviceToken, String umdeviceToken, String thisAppUserNo, UserWriter writer) {
    if (StringUtils.isNotBlank(deviceToken) || StringUtils.isNotBlank(umdeviceToken)) {
      Object status = null;
      if (writer == null) {
        writer = userDaoManager.getWriter();
        status = writer.begin();
      }
      try {
        if (StringUtils.isNotBlank(deviceToken)) {
          boolean isNeedToUpdate = true;
          List<AppUser> appUsers = writer.getAppUserByDeviceToken(deviceToken);
          if (CollectionUtil.isNotEmpty(appUsers)) {
            for (AppUser appUser : appUsers) {
              if (appUser != null) {
                if (StringUtils.isNotBlank(thisAppUserNo) && thisAppUserNo.equals(appUser.getAppUserNo())) {
                  isNeedToUpdate = false;
                  continue;
                }
                appUser.setDeviceToken(null);
                writer.update(appUser);
              }
            }
          }
          if (isNeedToUpdate && StringUtils.isNotBlank(thisAppUserNo)) {
            AppUser appUser = CollectionUtil.getFirst(writer.getAppUserByUserNo(thisAppUserNo, null));
            if (appUser != null) {
              appUser.setDeviceToken(deviceToken);
              writer.update(appUser);
            }
          }
        }
        if (StringUtils.isNotBlank(umdeviceToken)) {
          boolean isNeedToUpdate = true;
          List<AppUser> appUsers = writer.getAppUserByUMDeviceToken(umdeviceToken);
          if (CollectionUtil.isNotEmpty(appUsers)) {
            for (AppUser appUser : appUsers) {
              if (appUser != null) {
                if (StringUtils.isNotBlank(thisAppUserNo) && thisAppUserNo.equals(appUser.getAppUserNo())) {
                  isNeedToUpdate = false;
                  continue;
                }
                appUser.setUmDeviceToken(null);
                writer.update(appUser);
              }
            }
          }
          if (isNeedToUpdate && StringUtils.isNotBlank(thisAppUserNo)) {
            AppUser appUser = CollectionUtil.getFirst(writer.getAppUserByUserNo(thisAppUserNo, null));
            if (appUser != null) {
              appUser.setUmDeviceToken(umdeviceToken);
              writer.update(appUser);
            }
          }
        }
        if (status != null) {
          writer.commit(status);
        }
      } finally {
        if (status != null) {
          writer.rollback(status);
        }
      }
    }
  }

  //置空其他相同devicetoken的帐号，writer 如果null就做事务控制，非null就不做事务,如果thisAppUserNo
  private void updateUserDeviceToken(String deviceToken, String umdeviceToken, UserDTO userDTO, UserWriter writer) {
    if (userDTO != null && (StringUtils.isNotBlank(deviceToken) || StringUtils.isNotBlank(umdeviceToken))) {
      Object status = null;
      if (writer == null) {
        writer = userDaoManager.getWriter();
        status = writer.begin();
      }
      try {
        if (StringUtils.isNotBlank(deviceToken)) {
          boolean isNeedToUpdate = true;
          List<User> users = writer.getUsersByDeviceToken(deviceToken);
          if (CollectionUtil.isNotEmpty(users)) {
            for (User user : users) {
              if (user != null) {
                if (StringUtils.isNotBlank(userDTO.getUserNo()) && userDTO.getUserNo().equals(user.getUserNo())) {
                  isNeedToUpdate = false;
                  continue;
                }
                user.setDeviceToken(null);
                writer.update(user);
              }
            }
          }
          if (isNeedToUpdate && StringUtils.isNotBlank(userDTO.getUserNo())) {
            User user = writer.getUserByUserNo(userDTO.getUserNo());
            if (user != null) {
              user.setDeviceToken(deviceToken);
              writer.update(user);
            }
          }
        }
        if (StringUtils.isNotBlank(umdeviceToken)) {
          boolean isNeedToUpdate = true;
          List<User> users = writer.getUsersByUMDeviceToken(umdeviceToken);
          if (CollectionUtil.isNotEmpty(users)) {
            for (User user : users) {
              if (user != null) {
                if (StringUtils.isNotBlank(userDTO.getUserNo()) && userDTO.getUserNo().equals(user.getUserNo())) {
                  isNeedToUpdate = false;
                  continue;
                }
                user.setUmDeviceToken(null);
                writer.update(user);
              }
            }
          }
          if (isNeedToUpdate && StringUtils.isNotBlank(userDTO.getUserNo())) {
            User user = writer.getUserByUserNo(userDTO.getUserNo());
            if (user != null) {
              user.setUmDeviceToken(umdeviceToken);
              writer.update(user);
            }
          }
        }
        if (status != null) {
          writer.commit(status);
        }
      } finally {
        if (status != null) {
          writer.rollback(status);
        }
      }
    }
  }


  private String gsmRegisterAppUserValidate(GSMRegisterDTO gsmRegisterDTO, UserWriter writer) {
    String vResult = gsmRegisterDTO.validate();
    if (!gsmRegisterDTO.isSuccess(vResult)) {
      return vResult;
    }
    //mobile Unique validate
    if (writer.isAppUserMobileExisted(gsmRegisterDTO.getMobile(), gsmRegisterDTO.getAppUserType())) {
      return ValidateMsg.APP_USER_MOBILE_HAS_BEEN_USED.getValue();
    }
    //obd sn 是否生成
    OBD obd = writer.getObdByImeiObdType(gsmRegisterDTO.getImei(), null);
//    if (obd != null) {
//      return ValidateMsg.IMEI_SN_HAS_BEEN_USED.getValue();
//    }
    if (obd == null) {
      return ValidateMsg.IMEI_OBD_NOT_EXIST.getValue();
    } else {
      Set<Long> obdIds = new HashSet<Long>();
      obdIds.add(obd.getId());
      List<ObdUserVehicle> obdUserVehicles = writer.getOBDUserVehicle(obdIds);
      if (CollectionUtils.isNotEmpty(obdUserVehicles)) {
        return ValidateMsg.IMEI_SN_HAS_BEEN_USED.getValue();
      }
    }

    List<Vehicle> vehicleList = writer.getVehicleByGsmObdImei(gsmRegisterDTO.getImei());

    if (CollectionUtils.isEmpty(vehicleList)) {
      return ValidateMsg.IMEI_SN_NO_EXIST.getValue();
    } else if (vehicleList.size() > 1) {
      return ValidateMsg.VEHICLE_IMEI_SN_MORE_THAN_ONE.getValue();
    }


    return "";
  }

  public ApiGsmLoginResponse validateGsmRegister(GSMRegisterDTO gsmRegisterDTO) {
    UserWriter writer = userDaoManager.getWriter();


    String vResult = gsmRegisterDTO.validateImei();
    if (!gsmRegisterDTO.isSuccess(vResult)) {
      ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.IMEI_VALIDATE_FAIL, vResult, false));
      return apiGsmLoginResponse;
    }
    //mobile Unique validate
    if (writer.isAppUserMobileExisted(gsmRegisterDTO.getMobile(), gsmRegisterDTO.getAppUserType())) {
      ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.IMEI_VALIDATE_FAIL, ValidateMsg.APP_USER_MOBILE_HAS_BEEN_USED.getValue(), false));
      return apiGsmLoginResponse;
    }

    //obd sn 是否生成
    OBD obd = writer.getObdByImeiObdType(gsmRegisterDTO.getImei(), null);
//    if (obd != null) {
//      ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.IMEI_VALIDATE_FAIL, ValidateMsg.IMEI_SN_HAS_BEEN_USED.getValue(),false));
//      return apiGsmLoginResponse;
//    }
    if (obd == null) {
      ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.IMEI_VALIDATE_FAIL, ValidateMsg.IMEI_OBD_NOT_EXIST.getValue(), false));
      return apiGsmLoginResponse;
    } else {
      Set<Long> obdIds = new HashSet<Long>();
      obdIds.add(obd.getId());
      List<ObdUserVehicle> obdUserVehicles = writer.getOBDUserVehicle(obdIds);
      if (CollectionUtils.isNotEmpty(obdUserVehicles)) {
        ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.IMEI_VALIDATE_FAIL, ValidateMsg.IMEI_SN_HAS_BEEN_USED.getValue(), false));
        return apiGsmLoginResponse;
      }
    }

    List<Vehicle> vehicleList = writer.getVehicleByGsmObdImei(gsmRegisterDTO.getImei());

    if (CollectionUtils.isEmpty(vehicleList)) {
      ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.IMEI_VALIDATE_FAIL, ValidateMsg.IMEI_SN_NO_EXIST.getValue(), false));
      return apiGsmLoginResponse;
    } else if (vehicleList.size() > 1) {
      ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.IMEI_VALIDATE_FAIL, ValidateMsg.VEHICLE_IMEI_SN_MORE_THAN_ONE.getValue(), false));
      return apiGsmLoginResponse;
    }
    ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.IMEI_VALIDATE_SUCCESS));
    if (CollectionUtils.isNotEmpty(vehicleList)) {

      Vehicle vehicle = CollectionUtil.getFirst(vehicleList);
      AppUserDTO appUserDTO = new AppUserDTO();
      appUserDTO.setGsmObdImeiMoblie(vehicle.getGsmObdImeiMoblie());
      apiGsmLoginResponse.setAppUserDTO(appUserDTO);
      AppVehicleDTO appVehicleDTO = vehicle.toDTO().toAppVehicleDTO();
      apiGsmLoginResponse.setAppVehicleDTO(appVehicleDTO);
    }
    return apiGsmLoginResponse;
  }

  private AppVehicleDTO gsmAddVehicle(GSMRegisterDTO gsmRegisterDTO, UserWriter writer, VehicleDTO vehicleDTO) {

    AppVehicleDTO appVehicleDTO = gsmRegisterDTO.toAppVehicleDTO();

    List<CustomerVehicle> customerVehicles = writer.getCustomerVehicleByVehicleId(vehicleDTO.getId());
    if (CollectionUtils.isNotEmpty(customerVehicles)) {
      vehicleDTO.from(CollectionUtil.getFirst(customerVehicles).toDTO());
    }

    appVehicleDTO.fromVehicleDTO(vehicleDTO);

    AppVehicle appVehicle = new AppVehicle(appVehicleDTO);
    writer.save(appVehicle);
    appVehicleDTO.setVehicleId(appVehicle.getId());
    ServiceManager.getService(IAppUserVehicleObdService.class)
      .updateDefaultAppVehicle(appVehicleDTO.getVehicleId(), gsmRegisterDTO.getUserNo(), false);
    return appVehicleDTO;
  }


  @Override
  public ApiGsmLoginResponse gsmLogin(LoginDTO loginDTO) {
    String result = gsmLoginValidate(loginDTO);
    LOG.info("gsmLogin-gsmLoginValidate-result:{}", result);
    if (loginDTO.isSuccess(result)) {
      //delete old cache and update db
      updateAppUserLoginInfo(loginDTO);
      updateOldAppUserDeviceToken(loginDTO.getDeviceToken(), loginDTO.getUmDeviceToken(), loginDTO.getUserNo(), null);
      //get login logic data
      return getApiGsmLoginResponse(loginDTO.getUserNo());
    } else {
      ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.LOGIN_FAIL, result, false));
      return apiGsmLoginResponse;
    }
  }

  /**
   * 后视镜端登录
   *
   * @param loginDTO
   * @return
   */
  @Override
  public ApiMirrorLoginResponse platLogin(LoginDTO loginDTO) {
    updateAppUserLoginInfo(loginDTO);
    ApiMirrorLoginResponse response = new ApiMirrorLoginResponse(MessageCode.toApiResponse(MessageCode.LOGIN_SUCCESS));
    AppUserDTO appUserDTO = getAppUserByUserNo(loginDTO.getUserNo(), null);
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(appUserDTO.getRegistrationShopId());
    if (shopDTO != null) {
      appUserDTO.setRegistrationShopName(shopDTO.getName());
    }
    AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(getAppVehicleDTOByAppUserNo(appUserDTO.getUserNo()));
    if (appVehicleDTO != null) {
      appUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      appUserDTO.setName(appVehicleDTO.getVehicleNo());
    }
    response.setAppUserDTO(appUserDTO);
    return response;
  }

  public AppUserDTO getAppUserByUserType(String mobile) {
    LOG.info("getAppUserByUserType,mobile:{}", mobile);
    UserWriter writer = userDaoManager.getWriter();
    if (StringUtils.isEmpty(mobile)) {
      return null;
    }
    List<AppUser> appUserList = writer.getAppUserByUserType(mobile);
    if (CollectionUtils.isEmpty(appUserList)) {
      return null;
    }
    if (appUserList.size() > 1) {
      LOG.error("appUser 有多个:mobile:" + mobile);
    }
    return appUserList.get(0).toDTO();
  }

  @Override
  public AppUserDTO getAppUserByImei(String imei, AppUserType userType) {
    if(StringUtil.isEmpty(imei)&&userType==null){
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    AppUser appUser = writer.getAppUserByImei(imei, userType);
    return appUser != null ? appUser.toDTO() : null;
  }

  @Override
  public List<AppUserDTO> getAppUserByUserType(AppUserType appUserType, int start, int limit) {
    UserWriter writer = userDaoManager.getWriter();
    List<AppUser> appUsers = writer.getAppUserByUserType(appUserType, start, limit);
    List<AppUserDTO> appUserDTOs = new ArrayList<AppUserDTO>();
    if (CollectionUtil.isNotEmpty(appUsers)) {
      for (AppUser appUser : appUsers) {
        appUserDTOs.add(appUser.toDTO());
      }
    }
    return appUserDTOs;
  }

  /**
   * gsm卡用手机号登陆 手机号存到loginDTO.userNo字段中
   * 根据手机号查到app_user中 再把userNo放到loginDTO中
   *
   * @param loginDTO
   * @return
   */
  private String gsmLoginValidate(LoginDTO loginDTO) {

    String result = loginDTO.validate();
    if (loginDTO.isSuccess(result)) {
      AppUserDTO userDTO = getAppUserByUserType(loginDTO.getUserNo());
      if (userDTO == null) {
        return ValidateMsg.APP_USER_NOT_EXIST.getValue();
      }
      if (!userDTO.getPassword().equals(loginDTO.computeMD5())) {
        return ValidateMsg.APP_USER_LOGIN_ERROR.getValue();
      }
      loginDTO.setUserNo(userDTO.getUserNo());
      loginDTO.setAppUserType(userDTO.getAppUserType());
    }
    return result;
  }

  private ApiGsmLoginResponse getApiGsmLoginResponse(String useNo) {
    LOG.info("getApiGsmLoginResponse");
    StopWatchUtil sw = new StopWatchUtil("getApiGsmLoginResponse", "start");
    UserWriter userWriter = userDaoManager.getWriter();

    AppUserDTO appUserDTO = getAppUserByUserNo(useNo, null);
//    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    ObdUserVehicle obdUserVehicle = CollectionUtil.getFirst(userWriter.getBundlingObdUserVehicleByUserNo(useNo));
    if (obdUserVehicle != null) {
      ObdSimBind obdSimBind = userWriter.getObdSimBindsByObdId(obdUserVehicle.getObdId());
      if (obdSimBind != null) {
        ObdSim obdSim = userWriter.getById(ObdSim.class, obdSimBind.getSimId());
        if (obdSim != null && StringUtils.isNotBlank(obdSim.getMobile())) {
          appUserDTO.setGsmObdImeiMoblie(obdSim.getMobile());
        }
        OBD obd = userWriter.getById(OBD.class, obdSimBind.getObdId());
        if (obd != null && StringUtils.isNotBlank(obd.getImei())) {
          appUserDTO.setImei(obd.getImei());
        }
      }

    }
//    VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByIMei(appUserDTO.getUserNo());
//    if(vehicleDTO != null) {
//      appUserDTO.setGsmObdImeiMoblie(vehicleDTO.getGsmObdImeiMoblie());
//    }

    ApiGsmLoginResponse apiGsmLoginResponse = new ApiGsmLoginResponse(MessageCode.toApiResponse(MessageCode.LOGIN_SUCCESS));

//    appUserDTO.setImei(appUserDTO.getUserNo());
    apiGsmLoginResponse.setAppUserDTO(appUserDTO);


    List<AppVehicle> appVehicleList = userWriter.getAppVehicleByAppUserNo(appUserDTO.getUserNo());
    if (CollectionUtils.isNotEmpty(appVehicleList)) {

      AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appVehicleList).toDTO();
      apiGsmLoginResponse.setAppUserConfig(getAppUserConfig(appUserDTO.getUserNo(), appVehicleDTO.getVehicleNo()));

      AppUserConfig appUserConfig = userWriter.getAppUserConfigByName(useNo, AppUserConfigConstant.OIL_PRICE);
      if (appUserConfig != null) {
        appVehicleDTO.setOilPrice(NumberUtil.doubleVal(appUserConfig.getValue()));
      }
      IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
      //todo 带优化
//      GsmPoint gsmPoint = gsmPointService.getLastGsmPointByImei(appUserDTO.getImei());
//      if(gsmPoint != null && NumberUtil.doubleVal(gsmPoint.getLon())>0 && NumberUtil.doubleVal(gsmPoint.getLat())>0){
//        appVehicleDTO.setCoordinateLat(NumberUtil.convertGPSLat(gsmPoint.getLat()) );
//        appVehicleDTO.setCoordinateLon(NumberUtil.convertGPSLot(gsmPoint.getLon()) );
//      }
      apiGsmLoginResponse.setAppVehicleDTO(appVehicleDTO);
    }

    if (appUserDTO.getRegistrationShopId() != null) {
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(appUserDTO.getRegistrationShopId());
      apiGsmLoginResponse.setShopDTO(shopDTO);

      AppShopDTO appShopDTO = new AppShopDTO(shopDTO);
      apiGsmLoginResponse.setAppShopDTO(appShopDTO);

    }


    //set config
    apiGsmLoginResponse.setAppConfig(getAppConfig());
    sw.stopAndPrintLog();
    return apiGsmLoginResponse;
  }

  @Override
  public AppVehicleDTO getAppVehicleDTOById(Long id) {
    UserReader reader = userDaoManager.getReader();
    AppVehicle appVehicle = reader.getById(AppVehicle.class, id);
    if (appVehicle != null) {
      return appVehicle.toDTO();
    }
    return null;
  }

  @Override
  public AppUserConfig getAppUserConfigByName(String appUserNo) {
    UserWriter writer = userDaoManager.getWriter();
    writer.getAppUserConfigByName(appUserNo, AppUserConfigConstant.OIL_PRICE);
    return null;
  }

  @Override
  public void updateAppVehicleOilWearAndPosition(String appUserNo, String vehicleNo, double currentOilWear, double totalOilWear, String lat, String lon) {
    if (StringUtils.isNotEmpty(appUserNo)) {
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        AppVehicle appVehicle = CollectionUtil.getFirst(writer.getAppVehicleByUserNoVehicleNo(appUserNo, vehicleNo));
        if (appVehicle != null) {
          if (currentOilWear > 0.0001) {
            if (currentOilWear > NumberUtil.doubleVal(appVehicle.getWorstOilWear())) {
              appVehicle.setWorstOilWear(currentOilWear);
            }
            if (currentOilWear < NumberUtil.doubleVal(appVehicle.getBestOilWear()) || NumberUtil.doubleVal(appVehicle.getBestOilWear()) < 0.0001) {
              appVehicle.setBestOilWear(currentOilWear);
            }
          }
          if (totalOilWear > 0.0001) {
            appVehicle.setAvgOilWear(totalOilWear);
          }
          if (StringUtils.isNotBlank(lat) && StringUtils.isNotBlank(lon)) {
            appVehicle.setCoordinateLat(lat);
            appVehicle.setCoordinateLon(lon);
          }
          writer.update(appVehicle);
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public void updateAppVehicleInfoByGsmVehicleInfo(AppVehicleDTO appVehicleDTO, List<GsmVehicleInfo> gsmVehicleInfoList) {
    if (appVehicleDTO != null && appVehicleDTO.getVehicleId() != null && CollectionUtils.isNotEmpty(gsmVehicleInfoList)) {
      LOG.warn("updateAppVehicleInfoByGsmVehicleInfo：【{}】", appVehicleDTO.getVehicleId());
      long lastUpdateVehicleMilTime = NumberUtil.longValue(appVehicleDTO.getCurrentMileageLastUpdateTime());
      //当前里程
      BigDecimal obdMileage = new BigDecimal(appVehicleDTO.getCurrentMileage() == null ? "0" : String.valueOf(appVehicleDTO.getCurrentMileage()));
      //上一次OBD上报里程
      BigDecimal lastObdMileage = new BigDecimal(appVehicleDTO.getLastObdMileage() == null ? "0" : String.valueOf(appVehicleDTO.getLastObdMileage()));
      //
      BigDecimal gsmObdMileage = null;
      double avgOilWear = 0;
      boolean isNeedToUpdate = false;
      for (GsmVehicleInfo gsmVehicleInfo : gsmVehicleInfoList) {
        if (gsmVehicleInfo != null
          && NumberUtil.isNumber(gsmVehicleInfo.getAdMil())
          && gsmVehicleInfo.getUploadTime() != null) {
          gsmObdMileage = new BigDecimal(gsmVehicleInfo.getAdMil());   //gsm总里程
          //
          if (gsmVehicleInfo.getUploadTime() > lastUpdateVehicleMilTime
            && gsmObdMileage.doubleValue() > 0.01) {

            //lastObdMileage 大于 gsmObdMileage 拔掉了重新插 (并且新的obd adMil 小于1，lastOBDMil -新的obd adMail 大于0.2）
            if (lastObdMileage.compareTo(gsmObdMileage) == 1) {
              if (gsmObdMileage.doubleValue() < 1 && lastObdMileage.subtract(gsmObdMileage).doubleValue() > 0.2) {
                obdMileage = obdMileage.add(gsmObdMileage);
                lastUpdateVehicleMilTime = gsmVehicleInfo.getUploadTime();
                lastObdMileage = gsmObdMileage;
                if (NumberUtil.doubleVal(gsmVehicleInfo.getCacafe()) > 0) {
                  avgOilWear = NumberUtil.round(NumberUtil.doubleVal(gsmVehicleInfo.getCacafe()), 2);
                }
                isNeedToUpdate = true;
              }
              //lastObdMileage 小于 gsmObdMileage 正常行驶
            } else if (lastObdMileage.compareTo(gsmObdMileage) == -1) {
              obdMileage = obdMileage.add(gsmObdMileage.subtract(lastObdMileage));
              lastUpdateVehicleMilTime = gsmVehicleInfo.getUploadTime();
              lastObdMileage = gsmObdMileage;
              if (NumberUtil.doubleVal(gsmVehicleInfo.getCacafe()) > 0) {
                avgOilWear = NumberUtil.round(NumberUtil.doubleVal(gsmVehicleInfo.getCacafe()), 2);
              }
              isNeedToUpdate = true;
              //lastObdMileage 等于 gsmObdMileage 没有行驶，时间更新
            } else {
              lastUpdateVehicleMilTime = gsmVehicleInfo.getUploadTime();
              if (NumberUtil.doubleVal(gsmVehicleInfo.getCacafe()) > 0) {
                avgOilWear = NumberUtil.round(NumberUtil.doubleVal(gsmVehicleInfo.getCacafe()), 2);
              }
              isNeedToUpdate = true;
            }
          }
        }
      }
      appVehicleDTO.setCurrentMileage(obdMileage.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
      appVehicleDTO.setLastObdMileage(lastObdMileage.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
      appVehicleDTO.setCurrentMileageLastUpdateTime(lastUpdateVehicleMilTime);
      if (avgOilWear > 0) {
        appVehicleDTO.setAvgOilWear(avgOilWear);
      }

      if (isNeedToUpdate) {
        UserWriter writer = userDaoManager.getWriter();
        Object status = writer.begin();
        try {
          AppVehicle appVehicle = writer.getById(AppVehicle.class, appVehicleDTO.getVehicleId());
          if (appVehicle != null) {
            appVehicle.setCurrentMileage(NumberUtil.round(appVehicleDTO.getCurrentMileage(), 2));
            appVehicle.setLastObdMileage(appVehicleDTO.getLastObdMileage());
            appVehicle.setCurrentMileageLastUpdateTime(appVehicleDTO.getCurrentMileageLastUpdateTime());
            appVehicle.setAvgOilWear(appVehicleDTO.getAvgOilWear());
            writer.update(appVehicle);
            writer.commit(status);
          }
        } finally {
          writer.rollback(status);
        }
      }
    }

  }

  @Override
  public ApiResponse gsmRetrievePassword(String mobile) throws SmsException {
    if (StringUtil.isEmpty(mobile))
      return MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_FAIL, ValidateMsg.MOBILE_EMPTY);
    ApiResponse apiResponse;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<AppUser> appUserList = writer.getAppUserByUserType(mobile);
      AppUser appUser = CollectionUtil.isEmpty(appUserList) ? null : CollectionUtil.getFirst(appUserList);

      if (appUser == null) {
        apiResponse = MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_FAIL, ValidateMsg.MOBILE_NO_EXISTED);
      } else if (appUser.isSendSMSLimited(ConfigUtils.getAppUserSendSMSLimits())) {
        apiResponse = MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_FAIL, ValidateMsg.PASSWORD_RESET_NUMBER_TOO_MUCH);
      } else {
        String newPassword = ServiceManager.getService(IUserService.class).generatePassword();
        appUser.setPassword(EncryptionUtil.computeMD5Improved(newPassword));
        if (ServiceManager.getService(ISmsService.class)
          .sendResetAppUserPasswordSMS(appUser.getAppUserNo(), newPassword, appUser.getMobile(), appUser.getName(), AppUserType.GSM)) {
          appUser.addSendSMSLimited();
          writer.update(appUser);
          writer.commit(status);
          apiResponse = MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_SUCCESS);
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_EXCEPTION);
        }
      }
      return apiResponse;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public AppUserConfigDTO saveOrUpdateAppUserConfig(AppUserConfigDTO appUserConfigDTO) {
    if (appUserConfigDTO != null
      && StringUtils.isNotBlank(appUserConfigDTO.getAppUserNo())
      && StringUtils.isNotBlank(appUserConfigDTO.getName())
      && AppUserConfigConstant.configNameSet.contains(appUserConfigDTO.getName())
      && StringUtils.isNotBlank(appUserConfigDTO.getValue())) {
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        AppUserConfig appUserConfig = writer.getAppUserConfigByName(appUserConfigDTO.getName(), appUserConfigDTO.getName());
        if (appUserConfig != null) {
          appUserConfig.setValue(appUserConfigDTO.getValue());
          appUserConfig.setSyncTime(appUserConfigDTO.getSyncTime());
          appUserConfig.setDescription(appUserConfigDTO.getDescription());
          writer.update(appUserConfig);
        } else {
          appUserConfig = new AppUserConfig();
          appUserConfig.fromDTO(appUserConfigDTO);
          writer.save(appUserConfig);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
    return appUserConfigDTO;
  }


  @Override
  public Map<Long, Boolean> isAppUser(Long shopId, Long... contactIds) {
    Map<Long, Boolean> isAppMap = new HashMap<Long, Boolean>();
    IContactService contactService = ServiceManager.getService(IContactService.class);
    List<ContactDTO> contactDTOs = contactService.getContactsByIds(shopId, ArrayUtil.toLongArr(contactIds));
    Set<Long> customerIds = new HashSet<Long>();
    if (CollectionUtil.isNotEmpty(contactDTOs)) {
      for (ContactDTO contactDTO : contactDTOs) {
        if (contactDTO.getCustomerId() == null) continue;
        customerIds.add(contactDTO.getCustomerId());
      }
      Map<Long, List<AppUserDTO>> appUserDTOMap = ServiceManager.getService(IAppUserCustomerMatchService.class).getAppUserMapByCustomerIds(customerIds);
      for (ContactDTO contactDTO : contactDTOs) {
        if (contactDTO.getCustomerId() == null) continue;
        isAppMap.put(contactDTO.getId(), CollectionUtil.isNotEmpty(appUserDTOMap.get(contactDTO.getCustomerId())));
      }
    }
    Map<Long, Boolean> isAppVehicleMap = ServiceManager.getService(IVehicleService.class).isAppVehicle(contactIds);
    for (Long vehicleId : isAppVehicleMap.keySet()) {
      isAppMap.put(vehicleId, isAppVehicleMap.get(vehicleId));
    }
    return isAppMap;
  }

  public List<String> getAppUserNoByVehicleId(Long... vehicleIds) {
    if (ArrayUtil.isEmpty(vehicleIds)) return null;
    UserWriter writer = userDaoManager.getWriter();
    return writer.getAppUserNoByVehicleId(vehicleIds);
  }

  public List<String> getAppUserNoByContactId(Long... contactIds) {
    if (ArrayUtil.isEmpty(contactIds)) return null;
    List<ContactDTO> contactDTOs = ServiceManager.getService(IContactService.class).getContactsByIds(contactIds);
    if (CollectionUtil.isEmpty(contactDTOs)) return null;
    List<Long> customerIds = new ArrayList<Long>();
    for (ContactDTO contactDTO : contactDTOs) {
      if (contactDTO == null || contactDTO.getCustomerId() == null) {
        continue;
      }
      customerIds.add(contactDTO.getCustomerId());
    }
    List<String> appUserNoList = new ArrayList<String>();

    if (CollectionUtils.isNotEmpty(customerIds)) {
      UserWriter writer = userDaoManager.getWriter();
      List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomerIds(customerIds.toArray(new Long[customerIds.size()]));
      if (CollectionUtil.isNotEmpty(appUserCustomers)) {
        for (AppUserCustomer appUserCustomer : appUserCustomers) {
          appUserNoList.add(appUserCustomer.getAppUserNo());
        }
      }
    }
    return appUserNoList;
  }

  /**
   * @param customerIds
   * @return
   */
  @Override
  public List<AppUserCustomerDTO> getAppUserCustomerDTOs(Long... customerIds) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (ArrayUtils.isEmpty(customerIds)) {
      return appUserCustomerDTOs;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomerIds(customerIds);
    if (CollectionUtils.isNotEmpty(appUserCustomers)) {
      for (AppUserCustomer appUserCustomer : appUserCustomers) {
        if (appUserCustomer != null) {
          appUserCustomerDTOs.add(appUserCustomer.toDTO());
        }
      }
    }
    return appUserCustomerDTOs;
  }

  public List<String> getAppUserNoByVehicleIdOrContactId(Long... ids) {
    List<String> appUserNoList = new ArrayList<String>();
    List<String> appUserNoListTemp = getAppUserNoByVehicleId(ids);
    if (CollectionUtil.isNotEmpty(appUserNoListTemp)) {
      for (String appUserNo : appUserNoListTemp) {
        if (StringUtil.isEmpty(appUserNo) || appUserNoList.contains(appUserNo)) {
          continue;
        }
        appUserNoList.add(appUserNo);
      }
    }
    appUserNoListTemp = getAppUserNoByContactId(ids);
    if (CollectionUtil.isNotEmpty(appUserNoListTemp)) {
      for (String appUserNo : appUserNoListTemp) {
        if (StringUtil.isEmpty(appUserNo) || appUserNoList.contains(appUserNo)) {
          continue;
        }
        appUserNoList.add(appUserNo);
      }
    }
    return appUserNoList;
  }

  @Override
  public Map<String, AppVehicleDTO> getAppVehicleMapByImeis(Set<String> imeis) {

    UserWriter writer = userDaoManager.getWriter();
    Map<String, AppVehicleDTO> result = new HashMap<String, AppVehicleDTO>();
    if (CollectionUtils.isEmpty(imeis)) return result;

    List<Object[]> obdUserVehicles = writer.getOBDUserVehicleByObdImeis(imeis);
    Map<Long, String> appVehicleIdImeiMap = new HashMap<Long, String>();
    for (Object[] objects : obdUserVehicles) {
      if (!ArrayUtils.isEmpty(objects) && objects.length == 2) {
        String imei = (String) objects[0];
        ObdUserVehicle obdUserVehicle = (ObdUserVehicle) objects[1];
        if (obdUserVehicle != null && obdUserVehicle.getAppVehicleId() != null && StringUtils.isNotBlank(imei)) {
          appVehicleIdImeiMap.put(obdUserVehicle.getAppVehicleId(), imei);
        }
      }
    }
    List<AppVehicle> appVehicles = writer.getAppVehicleByIds(appVehicleIdImeiMap.keySet());
    if (CollectionUtils.isNotEmpty(appVehicles)) {
      for (AppVehicle entity : appVehicles) {
        if (entity != null && StringUtils.isNotBlank(appVehicleIdImeiMap.get(entity.getId()))) {
          result.put(appVehicleIdImeiMap.get(entity.getId()), entity.toDTO());
        }
      }
    }
    return result;
  }

  @Override
  public void generateAppInfo(List<CustomerSupplierSearchResultDTO> customerSupplierSearchResultDTOs) {
    if (CollectionUtils.isNotEmpty(customerSupplierSearchResultDTOs)) {
      Set<Long> customerIds = new HashSet<Long>();
      for (CustomerSupplierSearchResultDTO customerSearchResult : customerSupplierSearchResultDTOs) {
        if (customerSearchResult != null && customerSearchResult.getId() != null) {
          customerIds.add(customerSearchResult.getId());
        }
      }
      List<AppUserCustomerDTO> appUserCustomerDTOs = getAppUserCustomerDTOs(customerIds.toArray(new Long[customerIds.size()]));
      Set<Long> appVehicleIds = new HashSet<Long>();
      Set<Long> vehicleMatchVehicleIds = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(appUserCustomerDTOs)) {
        for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOs) {
          if (appUserCustomerDTO != null) {
            if (appUserCustomerDTO.getAppVehicleId() != null) {
              appVehicleIds.add(appUserCustomerDTO.getAppVehicleId());
            }
            if (appUserCustomerDTO.getShopVehicleId() != null
              && (AppUserCustomerMatchType.VEHICLE_MATCH.equals(appUserCustomerDTO.getMatchType())
              || AppUserCustomerMatchType.IMEI_MATCH.equals(appUserCustomerDTO.getMatchType()))) {
              vehicleMatchVehicleIds.add(appUserCustomerDTO.getShopVehicleId());
            }
          }
        }
      }
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      Map<Long, ObdUserVehicleDTO> OBDAppVehicleMap = vehicleService.getObdUserVehicles(appVehicleIds.toArray(new Long[appVehicleIds.size()]));
      Set<Long> bindVehicleIds = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(appUserCustomerDTOs)) {
        for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOs) {
          if (appUserCustomerDTO != null && appUserCustomerDTO.getAppVehicleId() != null && appUserCustomerDTO.getShopVehicleId() != null) {
            if (OBDAppVehicleMap.get(appUserCustomerDTO.getAppVehicleId()) != null) {
              bindVehicleIds.add(appUserCustomerDTO.getShopVehicleId());
            }
          }
        }
      }
      Map<Long, VehicleDTO> obdVehicleDTOs = vehicleService.getVehicleByVehicleIdSet(null, bindVehicleIds);
      Map<Long, VehicleDTO> vehicleMatchDTOs = vehicleService.getVehicleByVehicleIdSet(null, vehicleMatchVehicleIds);
      Set<String> allObdVehicleNos = new HashSet<String>();
      Set<String> vehicleMatchVehicleNos = new HashSet<String>();
      if (MapUtils.isNotEmpty(obdVehicleDTOs)) {
        for (VehicleDTO vehicleDTO : obdVehicleDTOs.values()) {
          if (vehicleDTO != null && StringUtils.isNotBlank(vehicleDTO.getLicenceNo())) {
            allObdVehicleNos.add(vehicleDTO.getLicenceNo());
          }
        }
      }
      if (MapUtils.isNotEmpty(vehicleMatchDTOs)) {
        for (VehicleDTO vehicleDTO : vehicleMatchDTOs.values()) {
          if (vehicleDTO != null && StringUtils.isNotBlank(vehicleDTO.getLicenceNo())) {
            vehicleMatchVehicleNos.add(vehicleDTO.getLicenceNo());
          }
        }
      }

      for (CustomerSupplierSearchResultDTO customerSearchResult : customerSupplierSearchResultDTOs) {
        if (customerSearchResult != null && customerSearchResult.getId() != null) {
          String[] vehicleNos = customerSearchResult.getLicenseNos();
          if (!ArrayUtils.isEmpty(vehicleNos) && CollectionUtils.isNotEmpty(allObdVehicleNos)) {
            for (String vehicle : vehicleNos) {
              if (StringUtils.isNotBlank(vehicle) && allObdVehicleNos.contains(vehicle)) {
                List<String> obdVehicleNos = customerSearchResult.getObdVehicleNo();
                if (obdVehicleNos == null) {
                  obdVehicleNos = new ArrayList<String>();
                  customerSearchResult.setObdVehicleNo(obdVehicleNos);
                }
                if (!obdVehicleNos.contains(vehicle)) {
                  obdVehicleNos.add(vehicle);
                }
              }
              if (StringUtils.isNotBlank(vehicle) && vehicleMatchVehicleNos.contains(vehicle)) {
                List<String> matchVehicleNos = customerSearchResult.getObdVehicleNo();
                if (matchVehicleNos == null) {
                  matchVehicleNos = new ArrayList<String>();
                  customerSearchResult.setAppVehicleNo(matchVehicleNos);
                }
                if (!matchVehicleNos.contains(vehicle)) {
                  matchVehicleNos.add(vehicle);
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  public void generateVehicleAppInfo(List<VehicleDTO> vehicleDTOList) {
    if (CollectionUtils.isNotEmpty(vehicleDTOList)) {
      Set<Long> vehicleIds = new HashSet<Long>();
      for (VehicleDTO vehicleDTO : vehicleDTOList) {
        if (vehicleDTO != null && vehicleDTO.getId() != null) {
          vehicleIds.add(vehicleDTO.getId());
        }
      }
      Set<AppUserCustomerMatchType> matchTypes = new HashSet<AppUserCustomerMatchType>();
      matchTypes.add(AppUserCustomerMatchType.VEHICLE_MATCH);
      matchTypes.add(AppUserCustomerMatchType.IMEI_MATCH);
      List<AppUserCustomerDTO> appUserCustomerDTOs = getAppUserCustomerDTOsByShopVehicleIds(vehicleIds, matchTypes);

      Map<Long, List<AppUserCustomerDTO>> vehicleAppUserCustomerMap = new HashMap<Long, List<AppUserCustomerDTO>>();
      Set<String> appUserNos = new HashSet<String>();
      if (CollectionUtils.isNotEmpty(appUserCustomerDTOs)) {
        for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOs) {
          if (appUserCustomerDTO != null && appUserCustomerDTO.getShopVehicleId() != null) {
            List<AppUserCustomerDTO> appUserCustomerDTOList = vehicleAppUserCustomerMap.get(appUserCustomerDTO.getShopVehicleId());
            if (appUserCustomerDTOList == null) {
              appUserCustomerDTOList = new ArrayList<AppUserCustomerDTO>();
              vehicleAppUserCustomerMap.put(appUserCustomerDTO.getShopVehicleId(), appUserCustomerDTOList);
            }
            appUserCustomerDTOList.add(appUserCustomerDTO);
            if (StringUtils.isNotBlank(appUserCustomerDTO.getAppUserNo())) {
              appUserNos.add(appUserCustomerDTO.getAppUserNo());
            }
          }
        }
      }

      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      Map<String, List<ObdUserVehicleDTO>> appUserVehicleOBDMap = vehicleService.getObdUserVehiclesByAppUserNos(appUserNos);

      for (VehicleDTO vehicleDTO : vehicleDTOList) {
        if (vehicleDTO != null && vehicleDTO.getId() != null) {
          List<AppUserCustomerDTO> appUserCustomerDTOList = vehicleAppUserCustomerMap.get(vehicleDTO.getId());
          if (CollectionUtils.isNotEmpty(appUserCustomerDTOList)) {
            vehicleDTO.setIsApp(true);
            for (AppUserCustomerDTO appUserCustomerDTO : appUserCustomerDTOList) {
              if (appUserCustomerDTO != null
                && StringUtils.isNotBlank(appUserCustomerDTO.getAppUserNo())
                && CollectionUtils.isNotEmpty(appUserVehicleOBDMap.get(appUserCustomerDTO.getAppUserNo()))) {
                vehicleDTO.setIsObd(true);
              }
            }
          }

        }
      }
    }
  }

  @Override
  public List<AppUserCustomerDTO> getAppUserCustomerDTOsByShopVehicleIds(Set<Long> vehicleIds, Set<AppUserCustomerMatchType> matchTypes) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (CollectionUtils.isEmpty(vehicleIds)) {
      return appUserCustomerDTOs;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomersByShopVehicleIds(vehicleIds, matchTypes);
    if (CollectionUtils.isNotEmpty(appUserCustomers)) {
      for (AppUserCustomer appUserCustomer : appUserCustomers) {
        if (appUserCustomer != null) {
          appUserCustomerDTOs.add(appUserCustomer.toDTO());
        }
      }
    }
    return appUserCustomerDTOs;
  }

  @Override
  public AppUserCustomerDTO getAppUserCustomerDTOByAppUserNoAndAppVehicleId(String appUserNo, Long appVehicleId, AppUserCustomerMatchType matchType) {
    if (StringUtils.isNotBlank(appUserNo) && appVehicleId != null && matchType != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<AppUserCustomer> appUserCustomers = writer.getAppUserCustomersByAppUserNoAndAppVehicleId(appUserNo, appVehicleId, matchType);
      if (CollectionUtils.isNotEmpty(appUserCustomers)) {
        return CollectionUtil.getFirst(appUserCustomers).toDTO();
      }
    }
    return null;
  }

  public int countGsmAppVehicle() {
    UserReader reader = userDaoManager.getReader();
    return reader.countGsmAppVehicle();
  }

  public List<AppVehicleDTO> getGsmAppVehicle(Pager pager) {
    UserReader reader = userDaoManager.getReader();
    List<AppVehicle> appVehicles = reader.getGsmAppVehicle(pager);
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();

    if (CollectionUtil.isEmpty(appVehicles)) {
      return appVehicleDTOList;
    }

    for (AppVehicle appVehicle : appVehicles) {
      appVehicleDTOList.add(appVehicle.toDTO());
    }

    return appVehicleDTOList;
  }

  @Override
  public ApiResponse bcgogoAppLogin(LoginDTO loginDTO) {
    ApiResponse apiResponse;
    Map<String, String> privilegeMap = new HashMap<String, String>();

    String result = bcgogoAppLoginValidate(loginDTO, privilegeMap);
    if (loginDTO.isSuccess(result)) {
      //cache and db
      updateAppUserLoginInfo(loginDTO);

      //get login logic data
      ApiLoginResponse apiLoginResponse = new ApiLoginResponse(MessageCode.toApiResponse(MessageCode.LOGIN_SUCCESS));

      apiLoginResponse.setPrivilegeMap(privilegeMap);


      return apiLoginResponse;
    } else {
      apiResponse = MessageCode.toApiResponse(MessageCode.LOGIN_FAIL, result, false);
      return apiResponse;
    }
  }

  private String bcgogoAppLoginValidate(LoginDTO loginDTO, Map<String, String> privilegeMap) {
    String result = loginDTO.validate();
    if (loginDTO.isSuccess(result)) {
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(loginDTO.getUserNo());
      if (userDTO == null || Status.deleted.equals(userDTO.getStatusEnum())) {
        return ValidateMsg.APP_USER_NOT_EXIST.getValue();
      }
      if (Status.inActive.equals(userDTO.getStatusEnum())) {
        return ValidateMsg.APP_USER_NOT_FORBID.getValue();
      }

      //密码不正确
      String encryptedPassword;
      try {
        encryptedPassword = EncryptionUtil.encryptPassword(loginDTO.getPassword(), userDTO.getShopId());
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
        return ValidateMsg.APP_USER_LOGIN_ERROR.getValue();
      }
      if (encryptedPassword != null && !userDTO.getPassword().equals(encryptedPassword)) {
        return ValidateMsg.APP_USER_LOGIN_ERROR.getValue();
      }
      userDTO = ServiceManager.getService(IUserCacheService.class).getUser(userDTO.getShopId(), userDTO.getId());
      //更新deviceToken
      updateUserDeviceToken(loginDTO.getDeviceToken(), loginDTO.getUmDeviceToken(), userDTO, null);
      return this.verifyShop(userDTO, privilegeMap);
    }
    return result;
  }

  private String verifyShop(UserDTO userDTO, Map<String, String> privilegeMap) {
    try {
      IShopService shopService = ServiceManager.getService(IShopService.class);
      ShopDTO shopDTO = shopService.checkTrialEndTimeShop(userDTO.getShopId());

      shopService.verifyShop(shopDTO);
      UserGroup userGroup = ServiceManager.getService(IUserGroupService.class).getUserGroup(userDTO.getUserGroupId());
      if (userGroup == null) {
        throw new BadCredentialsException("userRoleWrong");
      }
      //检查特殊用户组
      if (NumberUtil.isEqual(userDTO.getShopId(), ShopConstant.BC_ADMIN_SHOP_ID)) {
        //如果是bc内部人员查看是否有登录权限
        if (!hasRight(shopDTO.getShopVersionId(), userGroup.getId(), LogicResource.SHOP_LOGIN)) {
          throw new BadCredentialsException("loginPermission");
        }
      }

      privilegeMap.put("faultInfo", Boolean.valueOf(hasRight(shopDTO.getShopVersionId(), userGroup.getId(), LogicResource.WEB_SCHEDULE_SHOP_FAULT_INFO_BASE)).toString());
      privilegeMap.put("customerRemind", Boolean.valueOf(hasRight(shopDTO.getShopVersionId(), userGroup.getId(), LogicResource.WEB_SCHEDULE_REMIND_TODO_CUSTOMER_SERVICE)).toString());
      privilegeMap.put("appoint", Boolean.valueOf(hasRight(shopDTO.getShopVersionId(), userGroup.getId(), LogicResource.VEHICLE_CONSTRUCTION_APPOINT_ORDER_LIST)).toString());
    } catch (BadCredentialsException e) {
      return ValidateMsg.APP_USER_LOGIN_ERROR.getValue();
    }

    return null;
  }


  private boolean hasRight(Long versionId, Long userGroupId, String resource) {
    if (versionId == null || userGroupId == null || StringUtils.isEmpty(resource)) {
      return false;
    }
    return ServiceManager.getService(IPrivilegeService.class).verifierUserGroupResource(versionId, userGroupId, ResourceType.logic, resource);
  }

  @Override
  public boolean hasShopFaultRight(Long shopVersionId, Long userGroupId) {
    return hasRight(shopVersionId, userGroupId, LogicResource.WEB_SCHEDULE_SHOP_FAULT_INFO_BASE);
  }

  @Override
  public boolean hasRemindTodoRight(Long shopVersionId, Long userGroupId) {
    return hasRight(shopVersionId, userGroupId, LogicResource.WEB_SCHEDULE_REMIND_TODO_CUSTOMER_SERVICE);
  }

  @Override
  public boolean hasAppointRight(Long shopVersionId, Long userGroupId) {
    return hasRight(shopVersionId, userGroupId, LogicResource.VEHICLE_CONSTRUCTION_APPOINT_ORDER_LIST);
  }

  public List<ObdUserVehicleDTO> getOBDUserVehicleByObdIds(Set<Long> obdIds) {
    UserWriter writer = userDaoManager.getWriter();
    List<ObdUserVehicle> obdUserVehicleList = writer.getOBDUserVehicleByObdIds(obdIds);
    List<ObdUserVehicleDTO> userVehicleDTOs = new ArrayList<ObdUserVehicleDTO>();
    if (CollectionUtil.isNotEmpty(obdUserVehicleList)) {
      for (ObdUserVehicle obdUserVehicle : obdUserVehicleList) {
        userVehicleDTOs.add(obdUserVehicle.toDTO());
      }
    }
    return userVehicleDTOs;
  }

  public List<ObdUserVehicleDTO> getOBDUserVehicleByObdIds(Long obdId) {
    Set<Long> obdIds = new HashSet<Long>();
    obdIds.add(obdId);
    return getOBDUserVehicleByObdIds(obdIds);
  }

  public AppUserShopVehicleDTO getAppUserShopVehicleDTO(Long vehicleId, Long obdId, AppUserShopVehicleStatus status) {
    if (vehicleId == null || obdId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    AppUserShopVehicle appUserShopVehicle = CollectionUtil.getFirst(writer.getAppUserShopVehicle(vehicleId, obdId, status));
    return appUserShopVehicle != null ? appUserShopVehicle.toDTO() : null;
  }

  public ObdUserVehicleDTO getObdUserVehicle(Long vehicleId, Long obdId, ObdUserVehicleStatus status) {
    if (vehicleId == null || obdId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    ObdUserVehicle obdUserVehicle = CollectionUtil.getFirst(writer.getObdUserVehicle(vehicleId, obdId, status));
    return obdUserVehicle != null ? obdUserVehicle.toDTO() : null;
  }

  public void saveOrUpdateAppUserShopVehicle(AppUserShopVehicleDTO appUserShopVehicleDTO) {
    if (appUserShopVehicleDTO == null) {
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (appUserShopVehicleDTO.getId() != null) {
        AppUserShopVehicle appUserShopVehicle = writer.getById(AppUserShopVehicle.class, appUserShopVehicleDTO.getId());
        appUserShopVehicle.fromDTO(appUserShopVehicleDTO);
        writer.update(appUserShopVehicle);
      } else {
        AppUserShopVehicle appUserShopVehicle = new AppUserShopVehicle();
        appUserShopVehicle.fromDTO(appUserShopVehicleDTO);
        writer.save(appUserShopVehicle);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public void saveOrUpdateObdUserVehicle(ObdUserVehicleDTO obdUserVehicleDTO) {
    if (obdUserVehicleDTO == null) {
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (obdUserVehicleDTO.getId() != null) {
        ObdUserVehicle obdUserVehicle = writer.getById(ObdUserVehicle.class, obdUserVehicleDTO.getId());
        obdUserVehicle.fromDTO(obdUserVehicleDTO);
        writer.update(obdUserVehicle);
      } else {
        ObdUserVehicle obdUserVehicle = new ObdUserVehicle();
        obdUserVehicle.fromDTO(obdUserVehicleDTO);
        writer.save(obdUserVehicle);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public AppUserDTO getAppUserDTOByMobileUserType(String mobile, AppUserType appUserType) {
    UserWriter writer = userDaoManager.getWriter();
    if (StringUtils.isEmpty(mobile)) {
      return null;
    }
    List<AppUser> appUserList = writer.getAppUserByUserType(mobile);
    if (CollectionUtils.isEmpty(appUserList)) {
      return null;
    }
    if (appUserList.size() > 1) {
      LOG.error("appUser 有多个:mobile:" + mobile + ",appUserType:" + appUserType);
    }
    return appUserList.get(0).toDTO();
  }

  @Override
  public Map<String, AppUser> getAppUserByUserId(Set<String> userIdSet) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<AppUser> appUsers = userWriter.getAppUserByUserNos(userIdSet);
    Map<String, AppUser> userMap = new HashMap<String, AppUser>();
    for (AppUser appUser : appUsers) {
      userMap.put(appUser.getAppUserNo(), appUser);
    }
    return userMap;
  }

  @Override
  public Map<String, AppVehicle> getAppVehicleByUserId(Set<String> userIdSet) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<AppVehicle> appVehicles = userWriter.getAppVehicleByAppUserNos(userIdSet);
    Map<String, AppVehicle> vehicleMap = new HashMap<String, AppVehicle>();
    for (AppVehicle appVehicle : appVehicles) {
      vehicleMap.put(appVehicle.getAppUserNo(), appVehicle);
    }
    return vehicleMap;
  }

  @Override
  public AppUser getAppUserByPhone (long phone){
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.getAppUserByPhone(phone);
  }

}
