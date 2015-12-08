package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.api.response.UpYunParamResponse;
import com.bcgogo.config.dto.upYun.UpYunFileDTO;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.app.MessageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-22
 * Time: 下午4:12
 */
@Controller
@RequestMapping("/upYun/*")
public class UpYunController {
  private static final Logger LOG = LoggerFactory.getLogger(UpYunController.class);


  @ResponseBody
  @RequestMapping(value = "/uploadParam", method = RequestMethod.GET)
  public ApiResponse getEnquiryList(HttpServletRequest request, HttpServletResponse response) {
    try {
      Long appUserId = SessionUtil.getAppUserId(request, response);
      UpYunFileDTO upYunFileDTO = UpYunManager.getInstance().generateAppUserUpYunFileDTO(appUserId);
      int upYunExpiration = ConfigUtils.getUpYunExpiration();
      // 手机上过期时间缩短一点
      upYunExpiration = (upYunExpiration > 10)? upYunExpiration-5:upYunExpiration;
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.GET_UPYUN_PARAM_SUCCESS);
      UpYunParamResponse upYunParamResponse = new UpYunParamResponse(apiResponse);
      upYunParamResponse.setUpYunFileDTO(upYunFileDTO);
      upYunParamResponse.setExpireTime(upYunExpiration);
      return upYunParamResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.GET_UPYUN_PARAM_EXCEPTION);
    }
  }
}
