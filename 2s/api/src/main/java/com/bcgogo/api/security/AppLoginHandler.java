package com.bcgogo.api.security;

import com.bcgogo.api.*;
import com.bcgogo.api.response.ApiGsmLoginResponse;
import com.bcgogo.api.response.ApiLoginResponse;
import com.bcgogo.api.response.ApiMirrorLoginResponse;
import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.config.util.AppConstant;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.utils.*;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.product.service.ILicensePlateService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IAppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午2:22
 */
@Controller
public class AppLoginHandler {
  private static final Logger LOG = LoggerFactory.getLogger(AppLoginHandler.class);






  /**
   * 登陆
   */
  @ResponseBody
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ApiResponse login(HttpServletResponse response, LoginDTO loginDTO) throws Exception {
    try {
      String newPermissionKey = CookieUtil.genPermissionKey();
      loginDTO.setSessionId(newPermissionKey);
      loginDTO.setAppUserType(AppUserType.BLUE_TOOTH);
      ApiResponse apiResponse = ServiceManager.getService(IAppUserService.class).login(loginDTO);
      //set cookie
      if (apiResponse.getMsgCode() > 0) {
        CookieUtil.setSessionId(response, newPermissionKey);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGIN_EXCEPTION);
    }
  }

  /**
   * 登陆
   */
  @ResponseBody
  @RequestMapping(value = "/guest/login", method = RequestMethod.POST)
  public ApiResponse appGuestLogin(HttpServletResponse response, AppGuestLoginInfo loginDTO) throws Exception {
    try {
      String result = loginDTO.validate();
      if (loginDTO.isSuccess(result)) {
        AppConfig appConfig = ServiceManager.getService(IAppUserService.class).login(loginDTO);
        appConfig.setImageVersion(loginDTO.getImageVersionEnum());
        ApiLoginResponse apiLoginResponse = new ApiLoginResponse(MessageCode.toApiResponse(MessageCode.LOGIN_SUCCESS));
        apiLoginResponse.setAppConfig(appConfig);
        return apiLoginResponse;
      }
      return MessageCode.toApiResponse(MessageCode.FEEDBACK_FAIL, result);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGIN_EXCEPTION);
    }
  }


  @ResponseBody
  @RequestMapping(value = "/timeout", method = RequestMethod.GET)
  public ApiResponse timeout() throws Exception {
    try {
      return MessageCode.toApiResponse(MessageCode.LOGIN_TIME_OUT);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGIN_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/gsm/login", method = RequestMethod.POST)
  public ApiResponse gsmLogin(HttpServletRequest request, HttpServletResponse response, LoginDTO loginDTO) throws Exception {
    try {
      LOG.debug("login--loginDTO:{}", JsonUtil.objectToJson(loginDTO));
      String newPermissionKey = CookieUtil.genPermissionKey();
      loginDTO.setSessionId(newPermissionKey);
      AppUserDTO userDTO = ServiceManager.getService(IAppUserService.class).getAppUserDTOByMobileUserType(loginDTO.getUserNo(), null);
      loginDTO.setAppUserType(userDTO != null ? userDTO.getAppUserType() : AppUserType.GSM);
      ApiGsmLoginResponse apiResponse = ServiceManager.getService(IAppUserService.class).gsmLogin(loginDTO);
      //set cookie
      if (apiResponse.getMsgCode() > 0) {
        CookieUtil.setSessionId(response, newPermissionKey);
      }
      SessionUtil.getAppUserLoginInfo(response, newPermissionKey);
      if (apiResponse != null && apiResponse.getAppVehicleDTO() != null) {
        this.setAppVehicleJuheCityCode(apiResponse.getAppVehicleDTO());
      }
      AppShopDTO appShopDTO = apiResponse.getAppShopDTO();
      if (appShopDTO != null) {
        //获得图片
        List<AppShopDTO> appShopDTOList = new ArrayList<AppShopDTO>();
        appShopDTOList.add(appShopDTO);
        ServiceManager.getService(IImageService.class).addShopImageAppShopDTO(SessionUtil.getShopImageScenes(loginDTO.getImageVersionEnum()), true, appShopDTOList);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGIN_EXCEPTION);
    }
  }


  public void setAppVehicleJuheCityCode(AppVehicleDTO appVehicleDTO) {
    if (StringUtil.isEmpty(appVehicleDTO.getVehicleNo()) || StringUtil.isNotEmpty(appVehicleDTO.getJuheCityCode())) {
      return;
    }
    ILicensePlateService licensePlateService = ServiceManager.getService(ILicensePlateService.class);
    AreaDTO areaDTO = licensePlateService.getAreaDTOByLicenseNo(appVehicleDTO.getVehicleNo());
    if (areaDTO != null && StringUtil.isNotEmpty(areaDTO.getJuheCityCode())) {
      IJuheService juheService = ServiceManager.getService(IJuheService.class);

      List<JuheViolateRegulationCitySearchConditionDTO> conditionDTOs = juheService.getJuheViolateRegulationCitySearchCondition(areaDTO.getJuheCityCode(), JuheStatus.ACTIVE);
      if (CollectionUtil.isNotEmpty(conditionDTOs)) {
        JuheViolateRegulationCitySearchConditionDTO conditionDTO = CollectionUtil.getFirst(conditionDTOs);
        appVehicleDTO.setJuheCityName(conditionDTO.getCityName());
        appVehicleDTO.setJuheCityCode(conditionDTO.getCityCode());
      }
    }
  }

  /**
   * 登陆
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/login", method = RequestMethod.POST)
  public ApiResponse bcgogoAppLogin(HttpServletResponse response, LoginDTO loginDTO) throws Exception {
    try {
      String newPermissionKey = CookieUtil.genPermissionKey();
      loginDTO.setSessionId(newPermissionKey);
      loginDTO.setAppUserType(AppUserType.BCGOGO_SHOP_OWNER);
      ApiResponse apiResponse = ServiceManager.getService(IAppUserService.class).bcgogoAppLogin(loginDTO);
      //set cookie
      if (apiResponse.getMsgCode() > 0) {
        CookieUtil.setSessionId(response, newPermissionKey);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGIN_EXCEPTION);
    }
  }

  /**
   * 后视镜开机自动登陆
   */
  @Deprecated
  @ResponseBody
  @RequestMapping(value = "/mirror/login/{imei}", method = RequestMethod.GET)
  public ApiResponse mirrorLogin(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("imei") String imei) throws Exception {
    return gsmLogin(request, response, imei);
  }

  @ResponseBody
  @RequestMapping(value = "/plat/login/{imei}", method = RequestMethod.GET)
  public ApiResponse gsmLogin(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable("imei") String imei) throws Exception {
    try {
      LOG.info("imei:{}",imei);
      if (StringUtil.isEmpty(imei)) {
        return MessageCode.toApiResponse(MessageCode.LOGIN_IMEI_EMPTY);
      }
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserDTO appUserDTO = appUserService.getAppUserByImei(imei, null);
      LOG.debug("appUserDTO:{}",JsonUtil.objectToJson(appUserDTO));
      if (appUserDTO == null) {
        return MessageCode.toApiResponse(MessageCode.LOGIN_USER_NOT_EXIST);
      }
      String newPermissionKey = CookieUtil.genPermissionKey();
      LoginDTO loginDTO = new LoginDTO();
      loginDTO.setSessionId(newPermissionKey);
      loginDTO.setAppUserType(appUserDTO.getAppUserType());
      loginDTO.setUserNo(appUserDTO.getUserNo());
      loginDTO.setImei(imei);
      ApiMirrorLoginResponse apiResponse = appUserService.platLogin(loginDTO);
      LOG.debug("ApiMirrorLoginResponse:{}",JsonUtil.objectToJson(apiResponse));
      apiResponse.setSynTimestamp(System.currentTimeMillis());
      //set cookie
      if (apiResponse.getMsgCode() > 0) {
        CookieUtil.setSessionId(response, newPermissionKey);
      }
      SessionUtil.getAppUserLoginInfo(response, newPermissionKey);
      LOG.debug("同步账户信息到apix开始");
      //同步账户信息到apix
      AppUserLoginInfoDTO loginInfoDTO = appUserService.getAppUserLoginInfoByUserNo(appUserDTO.getUserNo(),null);
      String url = AppConstant.URL_APIX_PLAT_LOGIN;
      HttpResponse loginResponse = HttpUtils.sendPost(url, loginInfoDTO);
      String apiResponseJson = loginResponse.getContent();
      ApiResponse tmpResponse = JsonUtil.jsonToObj(apiResponseJson, ApiResponse.class);
      if (tmpResponse == null || !MessageCode.SUCCESS.toString().equals(tmpResponse.getStatus())) {
        LOG.error("同步账户信息到apix异常。");
      }else {
        LOG.error("同步账户信息到apix成功。");
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGIN_EXCEPTION);
    }
  }


}
