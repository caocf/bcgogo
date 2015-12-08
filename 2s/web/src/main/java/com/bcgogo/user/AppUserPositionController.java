package com.bcgogo.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-3
 * Time: 上午10:11
 */
@Controller
public class AppUserPositionController {
  @RequestMapping(value = "/1/{code}")
  public String resetPasswordInit1(HttpServletRequest request, HttpServletResponse response, @PathVariable("code") String code) {
    String userAgent = request.getHeader("user-agent");
    String platform = "其他";
    if (StringUtils.isNotBlank(userAgent)) {
      if (userAgent.toUpperCase().contains("IPHONE")) {
        platform = "苹果";
      } else if (userAgent.toUpperCase().contains("ANDROID")) {
        platform = "安卓";
      }
    }
    request.setAttribute("paramVal", code);
    request.setAttribute("platform", platform);
    return "/user/appUserAppointPosition";
  }
}
