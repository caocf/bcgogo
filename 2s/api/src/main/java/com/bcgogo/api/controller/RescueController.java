package com.bcgogo.api.controller;

import com.bcgogo.api.*;
import com.bcgogo.api.response.InsuranceCompanyResponse;
import com.bcgogo.api.response.OneKeyRescueResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.Result;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.user.userGuide.SosStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.AccidentSpecialistDTO;
import com.bcgogo.user.service.IRescueService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.wx.IWXAccountService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 一键救援/保险公司下拉列表
 * <p/>
 * Author: zj
 * Date: 2015-4-23
 * Time: 16:57
 */
@Controller
public class RescueController {
  private static final Logger LOG = LoggerFactory.getLogger(RescueController.class);


  @ResponseBody
  @RequestMapping(value = "/vehicle/oneKeyToRescue", method = RequestMethod.GET)
  public ApiResponse oneKeyToRescue(HttpServletRequest request, HttpServletResponse response) {
    String appUserNo = null;
    OneKeyRescueResponse oneKeyRescueResponse = null;
    try {
      appUserNo = SessionUtil.getAppUserNo(request, response);
      LOG.info("A key to the rescue,rescue={}", appUserNo);
      if (StringUtil.isEmpty(appUserNo)) {
        return MessageCode.toApiResponse(MessageCode.RESCUE_APP_USER_NO_EMPTY);
      }
      IRescueService rescueService = ServiceManager.getService(IRescueService.class);
      oneKeyRescueResponse = rescueService.findOneKeyRescueDetails(appUserNo);
      oneKeyRescueResponse.setMessageCode(MessageCode.SUCCESS);
      oneKeyRescueResponse.setMessage("一键救援信息获取成功！");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.RESCUE_DETAIL_GET_FAIL);
    }
    LOG.info("A key to the rescue,rescue={}", MessageCode.toApiResponse(MessageCode.RESCUE_DETAIL_GET_SUCCESS));
    return oneKeyRescueResponse;
  }

  @ResponseBody
  @RequestMapping(value = "/vehicle/insuranceCompany", method = RequestMethod.GET)
  public ApiResponse insuranceCompanyList(HttpServletRequest request, HttpServletResponse response) {
    IRescueService rescueService = ServiceManager.getService(IRescueService.class);
    InsuranceCompanyResponse insuranceCompanyResponse = rescueService.findInsuranceCompanyResponseDetails();
    insuranceCompanyResponse.setMessage("获取保险公司信息列表成功！");
    return insuranceCompanyResponse;
  }

  /**
   * 发送救援申请
   *
   * @param request
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/rescue/sos", method = RequestMethod.PUT)
  public ApiResponse rescue(HttpServletRequest request, HttpServletResponse response, @RequestBody RescueDTO rescueDTO) throws Exception {
    String appUserNo = SessionUtil.getAppUserNo(request, response);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
    Long shopId = appUserDTO.getRegistrationShopId();
    rescueDTO.setShopId(shopId);
    rescueDTO.setAppUserNo(appUserNo);
//    rescueDTO.setUploadTime(System.currentTimeMillis());
    rescueDTO.setUploadServerTime(System.currentTimeMillis());
    rescueDTO.setSosStatus(SosStatus.UNTREATED);
    IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
    rescueDTO.setAddr(geocodingService.gpsCoordinate2FullAddress(rescueDTO.getLat(), rescueDTO.getLon())); //碰撞地址
    IRescueService rescueService = ServiceManager.getService(IRescueService.class);
    rescueService.saveOrUpdateRescue(rescueDTO);
    //发送提醒
    AppVehicleDTO vehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
    rescueDTO.setMobile(vehicleDTO.getMobile());
    rescueDTO.setVehicleNo(vehicleDTO.getVehicleNo());
    rescueDTO.setUploadTimeStr(DateUtil.convertDateLongToDateString(DateUtil.ALL, rescueDTO.getUploadTime()));
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<AccidentSpecialistDTO> specialistDTOs = userService.getAccidentSpecialistByOpenId(shopId, null);
    if (CollectionUtil.isNotEmpty(specialistDTOs)) {
      IWXAccountService accountService = ServiceManager.getService(IWXAccountService.class);
      for (AccidentSpecialistDTO specialistDTO : specialistDTOs) {
        String publicNo = accountService.getWXAccountByOpenId(specialistDTO.getOpenId()).getPublicNo();
        WXMsgTemplate template = WXHelper.getRescueTemplate(publicNo, specialistDTO.getOpenId(), rescueDTO);
        if (template == null) {
          continue;
        }
        Result result = ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(publicNo, template);
        if (!result.isSuccess()) {
          LOG.error("后视镜发送一键救援提醒异常，{}", result.getMsg());
        }
      }
    }
    return MessageCode.toApiResponse(MessageCode.SUCCESS);
  }

}
