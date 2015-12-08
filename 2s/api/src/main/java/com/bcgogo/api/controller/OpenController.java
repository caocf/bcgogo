package com.bcgogo.api.controller;

import com.bcgogo.api.*;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IHandleAppVehicleFaultCodeService;
import com.bcgogo.user.model.app.OBD;
import com.bcgogo.user.model.app.ObdUserVehicle;
import com.bcgogo.user.service.IImpactService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IDriveLogService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-7
 * Time: 上午10:40
 */
@Controller
@RequestMapping("/guest")
public class OpenController {

  private static final Logger LOG = LoggerFactory.getLogger(OpenController.class);

  /**
   * 获取一辆车信息
   * vehicleId不能为空
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/getAppVehicle/{appUserNo}", method = RequestMethod.GET)
  public AppVehicleDTO getAppVehicleByAppUserNo(HttpServletRequest request, HttpServletResponse response, @PathVariable("appUserNo") String appUserNo) {
    try {
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      return CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

   @ResponseBody
  @RequestMapping(value = "/user/getAppUserNoByImei/{imei}", method = RequestMethod.GET)
  public String getAppUserNoByImei(HttpServletRequest request, HttpServletResponse response, @PathVariable("imei") String imei) {
    try {
      LOG.info("getAppUserNoByImei,imei:{}",imei);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserDTO appUserDTO = appUserService.getAppUserByImei(imei, AppUserType.GSM);
      LOG.info("appUserDTO:{}", JsonUtil.objectToJson(appUserDTO));
      return appUserDTO != null ? appUserDTO.getUserNo() : null;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/vehicle/saveDriveLog", method = RequestMethod.POST)
  public ApiResponse saveDriveLog(HttpServletRequest request, HttpServletResponse response, DriveLogDTO driveLogDTO) {
    try {
      LOG.info("driveLogDTO info:{}",JsonUtil.objectToJson(driveLogDTO));
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      driveLogService.generateDriveLog(driveLogDTO);
      return MessageCode.toApiResponse(MessageCode.SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }
  }


  @ResponseBody
  @RequestMapping(value = "/vehicle/sendFaultCode", method = RequestMethod.POST)
  public ApiResponse sendFaultCode(HttpServletRequest request, HttpServletResponse response, GsmVehicleDataDTO dataDTO) {
    try {
      IImpactService impactService = ServiceManager.getService(IImpactService.class);
      ObdUserVehicle obdUserVehicle = impactService.getObdUserVehicle(dataDTO.getAppUserNo());
      OBD obd = impactService.getObdById(obdUserVehicle.getObdId());
      ServiceManager.getService(IHandleAppVehicleFaultCodeService.class).sendFaultCode(obd.getImei(), dataDTO.getRdtc().toUpperCase(), dataDTO.getUploadTime());
      return MessageCode.toApiResponse(MessageCode.SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }
  }




}
