package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IDriveStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by XinyuQiu on 14-5-4.
 */

@Controller
@RequestMapping("/driveStat/*")
public class DriveStatController {

  private static final Logger LOG = LoggerFactory.getLogger(DriveStatController.class);

  @ResponseBody
  @RequestMapping(value = "/yearList", method = RequestMethod.GET)
  public ApiResponse getDriveStatList(HttpServletRequest request, HttpServletResponse response){
    try {
      IDriveStatService driveStatService = ServiceManager.getService(IDriveStatService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      return driveStatService.getYearDriveStat(appUserNo);
    } catch (Exception e) {
    LOG.error(e.getMessage(), e);
    return MessageCode.toApiResponse(MessageCode.DRIVE_STAT_LIST_EXCEPTION);
  }

  }
}
