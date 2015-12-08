package com.bcgogo.api.controller;

import com.bcgogo.api.*;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.api.response.ApiUserResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.utils.CookieUtil;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IHandleAppUserShopCustomerMatchService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午1:43
 */
@Controller
public class UserController {
  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  private IAppUserService appUserService;

  /**
   * 用户注册
   */
  @ResponseBody
  @RequestMapping(value = "/user/registration", method = RequestMethod.PUT)
  public ApiResponse registration(HttpServletResponse response, @RequestBody RegistrationDTO registrationDTO) {
    try {
      String sessionId = CookieUtil.genPermissionKey();
      registrationDTO.setSessionId(sessionId);
      Pair<ApiResponse, AppUserDTO> pair = ServiceManager.getService(IAppUserService.class).registerAppUser(registrationDTO);
      ApiResponse apiResponse = pair == null ? null : pair.getKey();
      AppUserDTO appUserDTO = pair == null ? null : pair.getValue();
      //set cookie
      if (apiResponse != null && apiResponse.getMsgCode() > 0) {
        CookieUtil.setSessionId(response, sessionId);
        createCustomerByAppUserNo(registrationDTO.getUserNo(), registrationDTO.getShopId());
        //立即去匹配
        if (appUserDTO != null && appUserDTO.getId() != null) {
          IHandleAppUserShopCustomerMatchService handleAppUserShopCustomerMatchService = ServiceManager.getService(IHandleAppUserShopCustomerMatchService.class);
          handleAppUserShopCustomerMatchService.handleAppUserCustomerMatch(appUserDTO.getId());
        }
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.REGISTER_EXCEPTION);
    }
  }

  private void createCustomerByAppUserNo(String appUserNo, Long shopId) throws BcgogoException {
    Long customerId = ServiceManager.getService(ICustomerService.class).createOrMatchingCustomerByAppUserNo(appUserNo, shopId);
    if (customerId != null) {
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/users/info/suggestion/{shopId}/{keyword}", method = RequestMethod.GET)
  public ApiResponse usersInfo(@PathVariable("shopId") String shopId,
                               @PathVariable("keyword") String keyword) throws Exception {
    try {
      Long _shopId = StringUtil.isEmptyAppGetParameter(shopId) ? null : Long.valueOf(shopId);
      String _keyword = StringUtil.isEmptyAppGetParameter(keyword) ? null : keyword;
      if (StringUtil.isEmpty(_keyword)) {
        return MessageCode.toApiResponse(MessageCode.USER_INFO_FAIL, " keywords is empty.");
      }
      Map<Long, AppUserDTO> appUserDTOList = ServiceManager.getService(IAppUserService.class)
          .getAppUserInfoListFromCustomer(_shopId, _keyword);
      ApiResultResponse apiResultResponse = new ApiResultResponse<Map<Long, AppUserDTO>>
          (MessageCode.toApiResponse(MessageCode.USER_INFO_SUCCESS), appUserDTOList);
      apiResultResponse.setDebug("shopId:" + shopId + ",keyword:" + keyword);
      return apiResultResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.USER_INFO_EXCEPTION);
    }
  }


  /**
   * 找回密码
   */
  @ResponseBody
  @RequestMapping(value = "/user/password/userNo/{userNo}", method = RequestMethod.GET)
  public ApiResponse retrievePassword(HttpServletRequest request, HttpServletResponse response, @PathVariable("userNo") String userNo) throws Exception {
    try {
      return ServiceManager.getService(IAppUserService.class).retrievePassword(userNo);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_EXCEPTION);
    }
  }

  /**
   * 用户反馈
   *
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/user/feedback", method = RequestMethod.POST)
  public ApiResponse feedback(AppUserFeedbackDTO feedbackDTO) throws Exception {
    return feedbackInternal(feedbackDTO);
  }

  /**
   * 用户反馈
   *
   * @param feedbackDTO AppUserFeedbackDTO
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/guest/feedback", method = RequestMethod.POST)
  public ApiResponse guestFeedback(AppUserFeedbackDTO feedbackDTO) throws Exception {
    if (feedbackDTO.getUserNo() == null) {
      feedbackDTO.setUserNo(AppUserDTO.APP_GUEST);
    }
    return feedbackInternal(feedbackDTO);
  }

  private ApiResponse feedbackInternal(AppUserFeedbackDTO feedbackDTO) {
    try {
      ApiResponse apiResponse;
      String result = feedbackDTO.validate();
      if (feedbackDTO.isSuccess(result)) {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.FEEDBACK_SUCCESS));
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        appUserService.saveAppUserFeedback(feedbackDTO);
        return apiResponse;
      } else {
        apiResponse = MessageCode.toApiResponse(MessageCode.FEEDBACK_FAIL, result);
        return apiResponse;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FEEDBACK_EXCEPTION);
    }
  }


  /**
   * 手机端用户更改个人资料
   *
   * @param appUserDTO AppUserDTO
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/user/information", method = RequestMethod.PUT)
  public ApiResponse updateUserInfo(@RequestBody AppUserDTO appUserDTO) throws Exception {
    try {
      ApiResponse apiResponse;

      String validateResult = appUserDTO.validateUpdateUserInfo();
      if (appUserDTO.isSuccess(validateResult)) {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_INFO_UPDATE_SUCCESS));

        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);

        Result result = appUserService.updateAppUserInfo(appUserDTO);
        if (result.isSuccess()) {
          return apiResponse;
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.USER_INFO_UPDATE_FAIL, result.getMsg());
          return apiResponse;
        }

      } else {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_INFO_UPDATE_FAIL, validateResult));
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.USER_INFO_UPDATE_EXCEPTION);
    }
  }


  /**
   * 手机端用户更改密码
   *
   * @param appUserDTO AppUserDTO
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/user/password", method = RequestMethod.PUT)
  public ApiResponse updateUserPassword(HttpServletRequest request,HttpServletResponse response,@RequestBody AppUserDTO appUserDTO) throws Exception {
    try {
      IAppUserService appUserService =  ServiceManager.getService(IAppUserService.class);
      ApiResponse apiResponse;
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      appUserDTO.setUserNo(appUserNo);
      String result = appUserDTO.validateUpdatePassword();
      if (appUserDTO.isSuccess(result)) {
        Result updateResult = appUserService.updatePassword(appUserDTO);

        if (updateResult.isSuccess()) {
          apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_PASSWORD_UPDATE_SUCCESS));
          return apiResponse;
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.USER_PASSWORD_UPDATE_FAIL, updateResult.getMsg());
          return apiResponse;
        }
      } else {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.USER_PASSWORD_UPDATE_FAIL, result));
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.USER_PASSWORD_UPDATE_EXCEPTION);
    }
  }


  /**
   * 手机端用户查看个人资料
   *
   * @param userNo
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/user/information/userNo/{userNo}", method = RequestMethod.GET)
  public ApiResponse updateUserPassword(HttpServletRequest request, HttpServletResponse response, @PathVariable String userNo) throws Exception {
    try {
      ApiUserResponse apiResponse;
      userNo = SessionUtil.getAppUserNo(request, response);
      if (!StringUtil.isEmpty(userNo)) {
        AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(userNo, null);
        if (appUserDTO != null) {
          apiResponse = new ApiUserResponse(MessageCode.toApiResponse(MessageCode.USER_INFO_GET_SUCCESS));
        } else {
          apiResponse = new ApiUserResponse(MessageCode.toApiResponse(MessageCode.USER_INFO_GET_FAIL, "用户账号不存在"));
        }
        apiResponse.setUserInfo(appUserDTO);
      } else {
        apiResponse = new ApiUserResponse(MessageCode.toApiResponse(MessageCode.USER_INFO_GET_FAIL, "用户账号为空"));
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.USER_INFO_GET_EXCEPTION);
    }
  }

  /**
   * app下载跳转
   */
  @RequestMapping(value = "/bcgogo/app/download")
  public void appDownload(HttpServletResponse response, HttpServletRequest request) {
    try {

      String userAgent = request.getHeader("user-agent");
      if (StringUtil.isNotEmpty(userAgent) && userAgent.contains("Android")) {
        response.sendRedirect(ConfigUtils.getAndroidAppUpgradeURL());
      } else if (StringUtil.isNotEmpty(userAgent) && userAgent.contains("iPhone")) {
        response.sendRedirect(ConfigUtils.getISOAppUpgradeURL());
      } else {
        response.sendRedirect("http://www.baidu.com");
      }
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }

  }

  @ResponseBody
  @RequestMapping(value = "/user/updateAppUserConfig", method = RequestMethod.POST)
  public ApiResponse updateAppUserConfig(HttpServletRequest request, HttpServletResponse response,
                                         AppUserConfigUpdateRequest appUserConfigUpdateRequest) throws Exception {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    try {
      ApiResponse apiResponse;
      String userNo = SessionUtil.getAppUserNo(request, response);
      if (StringUtil.isNotEmpty(userNo)) {
        if (appUserConfigUpdateRequest != null && !ArrayUtils.isEmpty(appUserConfigUpdateRequest.getAppUserConfigDTOs())) {
          appUserConfigUpdateRequest.setAppUserNo(userNo);
          apiResponse = appUserService.validateUpdateAppUserConfigByAppUser(appUserConfigUpdateRequest);
          if (MessageCode.UPDATE_APP_USER_CONFIG_SUCCESS.getCode() == apiResponse.getMsgCode()) {
            apiResponse =  appUserService.updateAppUserConfig(appUserConfigUpdateRequest);
          }
        } else {
          apiResponse = MessageCode.toApiResponse(MessageCode.UPDATE_APP_USER_CONFIG_FAIL,
              ValidateMsg.APP_USER_CONFIG_NOT_FOUND);
        }
      } else {
        apiResponse = MessageCode.toApiResponse(MessageCode.UPDATE_APP_USER_CONFIG_FAIL,
            ValidateMsg.APP_USER_NOT_FOUND);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.USER_INFO_GET_EXCEPTION);
    }
  }


  /**
   * gsm找回密码专用
   */
  @ResponseBody
  @RequestMapping(value = "/user/gsm/password/mobile/{mobile}", method = RequestMethod.GET)
  public ApiResponse gsmRetrievePassword(HttpServletRequest request, HttpServletResponse response, @PathVariable("mobile") String mobile) throws Exception {
    try {
      if(StringUtils.isNotBlank(mobile)&& BcgogoConcurrentController.lock(ConcurrentScene.GSM_APP_FIND_PWD,mobile)){
        return ServiceManager.getService(IAppUserService.class).gsmRetrievePassword(mobile);
      }else {
       return MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_FAIL, ValidateMsg.PASSWORD_IS_RESTING);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.RETRIEVE_PASSWORD_EXCEPTION);
    }finally {
      if(StringUtils.isNotBlank(mobile)) {
        BcgogoConcurrentController.release(ConcurrentScene.GSM_APP_FIND_PWD, mobile);
      }
    }
  }


}
