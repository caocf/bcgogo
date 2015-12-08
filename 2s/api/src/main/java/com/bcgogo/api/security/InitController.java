package com.bcgogo.api.security;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.controller.OBDController;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
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
 * 2013.10.26 暂时使用
 * User: ZhangJuntao
 * Date: 13-10-26
 * Time: 上午9:17
 */
@Deprecated
@Controller
@RequestMapping("/init/{userNo}/{password}/*")
public class InitController {
  private static final Logger LOG = LoggerFactory.getLogger(OBDController.class);

  @ResponseBody
  @RequestMapping(value = "/shop/serviceScopes", method = RequestMethod.GET)
  public ApiResponse binding(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable("userNo") String userNo,
                             @PathVariable("password") String password) throws Exception {
    try {
      if ("bcgogo2012".equals(password) && userNo.equals("zjt_hans")) {
        ServiceManager.getService(IShopService.class).initShopServiceScope();
        return MessageCode.toApiResponse(MessageCode.INIT_SUCCESS);
      }
      return MessageCode.toApiResponse(MessageCode.PERMISSION_DENY);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.INIT_EXCEPTION);
    }
  }
}
