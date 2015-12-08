package com.bcgogo.api.security;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.LoginDTO;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.utils.CookieUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IAppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * User: ZhangJuntao
 * Date: 13-8-29
 * Time: 下午5:30
 */
@Controller
public class AppLogoutSuccessHandler {
  private static final Logger LOG = LoggerFactory.getLogger(AppLoginHandler.class);

  /**
   * 用户注销
   *
   * @param loginDTO
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/logout", method = RequestMethod.PUT)
  public ApiResponse logout(HttpServletResponse response,@RequestBody LoginDTO loginDTO) throws Exception {
    try {
      String userNo = loginDTO.getUserNo();
      IAppUserService appUserService  = ServiceManager.getService(IAppUserService.class);
//      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(userNo,null);
      ApiResponse apiResponse = ServiceManager.getService(IAppUserService.class).logout(userNo,null);
      CookieUtil.removeSessionId(response);
      apiResponse.setDebug(userNo);
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGOUT_EXCEPTION);
    }
  }

  /**
   * 用户注销
   *
   * @param loginDTO
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/logout", method = RequestMethod.PUT)
  public ApiResponse bcgogoLogout(HttpServletResponse response,@RequestBody LoginDTO loginDTO) throws Exception {
    try {
      String userNo = loginDTO.getUserNo();
      loginDTO.setAppUserType(AppUserType.BCGOGO_SHOP_OWNER);
      ApiResponse apiResponse = ServiceManager.getService(IAppUserService.class).logout(userNo,loginDTO.getAppUserType());
      CookieUtil.removeSessionId(response);
      apiResponse.setDebug(userNo);
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.LOGOUT_EXCEPTION);
    }
  }

}
