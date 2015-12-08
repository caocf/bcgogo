package com.bcgogo.api.controller;

/**
 * User: lw
 * Date: 14-3-11
 * Time: 上午10:55
 */


import com.bcgogo.api.*;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.user.dto.CouponDTO;
import com.bcgogo.user.model.Coupon;
import com.bcgogo.user.service.ICouponService;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.api.gsm.GSMRegisterDTO;
import com.bcgogo.api.response.ApiGsmLoginResponse;
import com.bcgogo.api.response.ApiGsmUserQRResponse;
import com.bcgogo.utils.CookieUtil;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ImageVersion;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.product.service.ILicensePlateService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.WXQRCodeDTO;
import com.bcgogo.wx.user.AppUserWXQRCodeDTO;
import com.bcgogo.wx.user.AppWXUserDTO;
import com.bcgogo.wx.user.WXUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


@Controller
public class GSMUserController {
  private static final Logger LOG = LoggerFactory.getLogger(GSMUserController.class);



  /**
   * 用户注册
   * 生成app_user app_user_customer app_vehicle 更新
   */
  @ResponseBody
  @RequestMapping(value = "/register/gsm/register", method = RequestMethod.PUT)
  public ApiResponse gsmRegister(HttpServletRequest request, HttpServletResponse response, @RequestBody GSMRegisterDTO gsmRegisterDTO) {
    try {
      String sessionId = CookieUtil.genPermissionKey();
      gsmRegisterDTO.setSessionId(sessionId);
      gsmRegisterDTO.setUserNo(gsmRegisterDTO.getImei());
      ObdDTO obdDTO = ServiceManager.getService(IObdManagerService.class).getObdByImei(gsmRegisterDTO.getImei());
      LOG.info("obdDTO-obdType:{}",obdDTO.getObdType());
      gsmRegisterDTO.setAppUserTypeByObdType(obdDTO.getObdType());
      List list = ServiceManager.getService(IAppUserService.class).gsmRegisterAppUser(gsmRegisterDTO);
      //set cookie
      ApiGsmLoginResponse apiGsmLoginResponse = (ApiGsmLoginResponse) CollectionUtil.getFirst(list);
      if (apiGsmLoginResponse != null && apiGsmLoginResponse.getMsgCode() > 0) {

        if (apiGsmLoginResponse != null && apiGsmLoginResponse.getAppVehicleDTO() != null) {
          this.setAppVehicleJuheCityCode(apiGsmLoginResponse.getAppVehicleDTO());
        }

        CookieUtil.setSessionId(response, sessionId);
        Long customerId = (Long) list.get(1);
        Long vehicleId = (Long) list.get(2);
        Long shopId = (Long) list.get(3);
        if (customerId != null) {
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
        }
        if (shopId != null && vehicleId != null) {
          IVehicleSolrWriterService vehicleSolrWriterService = ServiceManager.getService(IVehicleSolrWriterService.class);
          vehicleSolrWriterService.createVehicleSolrIndex(shopId, vehicleId);
        }
        AppShopDTO appShopDTO = apiGsmLoginResponse.getAppShopDTO();

        if (appShopDTO != null) {
          //获得图片
          List<AppShopDTO> appShopDTOList = new ArrayList<AppShopDTO>();
          appShopDTOList.add(appShopDTO);
          ImageVersion imageVersion = null;
          ServiceManager.getService(IImageService.class).addShopImageAppShopDTO(SessionUtil.getShopImageScenes(imageVersion), true, appShopDTOList);
        }

      }
      return apiGsmLoginResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.REGISTER_EXCEPTION);
    }
  }


  /**
   * gsm卡用户注册前验证
   */
  @ResponseBody
  @RequestMapping(value = "/register/gsm/validateRegister", method = RequestMethod.PUT)
  public ApiResponse validateRegister(HttpServletResponse response, @RequestBody GSMRegisterDTO gsmRegisterDTO) {
    try {
      String sessionId = CookieUtil.genPermissionKey();
      gsmRegisterDTO.setSessionId(sessionId);
      gsmRegisterDTO.setUserNo(gsmRegisterDTO.getImei());
      AppUserDTO userDTO = ServiceManager.getService(IAppUserService.class).getAppUserDTOByMobileUserType(gsmRegisterDTO.getUserNo(), null);
      gsmRegisterDTO.setAppUserType(userDTO != null ? userDTO.getAppUserType() : AppUserType.GSM);
      ApiGsmLoginResponse apiResponse = ServiceManager.getService(IAppUserService.class).validateGsmRegister(gsmRegisterDTO);
      if (apiResponse != null && apiResponse.getAppVehicleDTO() != null) {
        this.setAppVehicleJuheCityCode(apiResponse.getAppVehicleDTO());
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.REGISTER_EXCEPTION);
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
   * 获取微信粉丝
   *
   * @param request
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/gsm/userinfo/qr", method = RequestMethod.GET)
  public ApiResponse getGsmUserQRCode(HttpServletRequest request, HttpServletResponse response) {
    try {

      String appUserNo = SessionUtil.getAppUserNo(request, response);
      IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
      String publicNo = ServiceManager.getService(IConfigService.class).getConfig("MIRROR_PUBLIC_NO", ShopConstant.BC_SHOP_ID);
      LOG.info("qr:MIRROR_PUBLIC_NO is {}", publicNo);
      WXQRCodeDTO qrCodeDTO = wxUserService.getUnExpireWXQRCodeDTO(publicNo, appUserNo);
      if (qrCodeDTO == null) {
        LOG.info("qrCodeDTO is empty");
        AppUserWXQRCodeDTO appCodeDTO = wxUserService.getAppUserWXQRCodeDTO(publicNo, appUserNo);
        if (appCodeDTO == null) {
          LOG.info("create AppUserWXQRCodeDTO");
          appCodeDTO = new AppUserWXQRCodeDTO();
          appCodeDTO.setDeleted(DeletedType.FALSE);
          appCodeDTO.setPublicNo(publicNo);
          appCodeDTO.setAppUserNo(appUserNo);
        }
        qrCodeDTO = wxUserService.createTempQRCode(publicNo, null, QRScene.MIRROR_USER);
        appCodeDTO.setQrCodeId(qrCodeDTO.getId());
        wxUserService.saveOrUpdateAppUserWXQRCodeDTO(appCodeDTO);
      }
      ApiGsmUserQRResponse apiResponse = new ApiGsmUserQRResponse();
      String qr_code_show_url = WXConstant.URL_SHOW_QR_CODE;
      qr_code_show_url = qr_code_show_url.replace("{TICKET}", qrCodeDTO.getTicket());
      apiResponse.setUrl(qr_code_show_url);
      apiResponse.setExpireTime(qrCodeDTO.getExpireTime());
      apiResponse.setPublicNo(publicNo);
      List<AppWXUserDTO> wxUserDTOs = wxUserService.getAppWXUserDTO(appUserNo, null);
      if (CollectionUtil.isNotEmpty(wxUserDTOs)) {
        WXUserDTO wxUserDTO = null;
        for (AppWXUserDTO userDTO : wxUserDTOs) {
          wxUserDTO = wxUserService.getWXUserDTOByOpenId(userDTO.getOpenId());
          if (wxUserDTO != null) {
            userDTO.setName(wxUserDTO.getName());
            userDTO.setNickName(wxUserDTO.getNickname());
            userDTO.setHeadImgUrl(wxUserDTO.getHeadimgurl());
          }
        }
      }
      apiResponse.setAppWXUserDTOs(wxUserDTOs);
      apiResponse.setMessageCode(MessageCode.SUCCESS);
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }
  }

  /**
   * 下载微信二维码
   *
   * @param request
   * @param response
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/gsm/userinfo/qr/download", method = RequestMethod.GET)
  public void downloadGsmUserQRCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String appUserNo = SessionUtil.getAppUserNo(request, response);
    IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
    String publicNo = ServiceManager.getService(IConfigService.class).getConfig("MIRROR_PUBLIC_NO", ShopConstant.BC_SHOP_ID);
    WXQRCodeDTO qrCodeDTO = wxUserService.getUnExpireWXQRCodeDTO(publicNo, appUserNo);
    if (qrCodeDTO == null) {
      AppUserWXQRCodeDTO appCodeDTO = wxUserService.getAppUserWXQRCodeDTOByAppUserNo(appUserNo);
      qrCodeDTO = wxUserService.createTempQRCode(appCodeDTO.getPublicNo(), null, QRScene.MIRROR_USER);
      appCodeDTO.setQrCodeId(qrCodeDTO.getId());
      wxUserService.saveOrUpdateAppUserWXQRCodeDTO(appCodeDTO);
    }
    String qr_code_show_url = WXConstant.URL_SHOW_QR_CODE;
    qr_code_show_url = qr_code_show_url.replace("{TICKET}", qrCodeDTO.getTicket());
    LOG.info("wx:print wx qr_code,appUserNo is {},qr_code_show_url is {}", appUserNo, qr_code_show_url);
    OutputStream os = response.getOutputStream();
    try {
      response.reset();
      response.setHeader("Content-Disposition", "attachment; filename=dict.jpg");
      response.setContentType("application/octet-stream; charset=utf-8");
      byte[] byteImg = FileUtil.readUrlDate(qr_code_show_url);
      LOG.info("download,size is {}", byteImg.length);
      os.write(byteImg);
      os.flush();
    } finally {
      if (os != null) {
        os.close();
      }
    }
  }


  /**
   * 后视镜用户解绑
   *
   * @param request
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/gsm/userinfo/unbind/{openId}", method = RequestMethod.GET)
  public ApiResponse getGsmUserUnbind(HttpServletRequest request, HttpServletResponse response, @PathVariable("openId") String openId) {
    try {
      IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
      if (StringUtil.isEmpty(openId)) {
        return MessageCode.toApiResponse(MessageCode.WX_OPENID_EMPTY_FALL);
      }
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      AppWXUserDTO appWXUserDTO = CollectionUtil.getFirst(wxUserService.getAppWXUserDTO(appUserNo, openId));
      appWXUserDTO.setDeleted(DeletedType.TRUE);
      wxUserService.saveOrUpdateAppWXUser(appWXUserDTO);
      return MessageCode.toApiResponse(MessageCode.SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }
  }


}
