package com.bcgogo.driving.controller;

import com.bcgogo.pojox.api.ApiResponse;
import com.bcgogo.pojox.api.XAppUserLoginInfoDTO;
import com.bcgogo.pojox.enums.app.MessageCode;
import com.bcgogo.driving.service.IAppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-7
 * Time: 上午10:03
 */
@Controller
public class AppLoginController {
  private static final Logger LOG = LoggerFactory.getLogger(AppLoginController.class);

  @Autowired
  private IAppUserService appUserService;

  /**
   * 后视镜开机自动登陆
   */
  @Deprecated
  @ResponseBody
  @RequestMapping(value = "/mirror/login", method = RequestMethod.POST)
  public ApiResponse mirrorLogin(HttpServletRequest request, HttpServletResponse response, XAppUserLoginInfoDTO appUserLoginInfoDTO) throws Exception {
    return platLogin(request, response, appUserLoginInfoDTO);
  }

  /**
   * 后视镜开机自动登陆
   */
  @ResponseBody
  @RequestMapping(value = "/plat/login", method = RequestMethod.POST)
  public ApiResponse platLogin(HttpServletRequest request, HttpServletResponse response, XAppUserLoginInfoDTO appUserLoginInfoDTO) throws Exception {
    try {
      if (appUserLoginInfoDTO == null) {
        return MessageCode.toApiResponse(MessageCode.FAILED);
      }
      appUserLoginInfoDTO.setId(null);
      appUserService.updateAppUserLoginInfo(appUserLoginInfoDTO);
      LOG.debug("appUserNo:{},plat login success",appUserLoginInfoDTO.getAppUserNo());
      return MessageCode.toApiResponse(MessageCode.SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGIN_EXCEPTION);
    }
  }


}
