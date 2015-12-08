package com.bcgogo.web;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-13
 * Time: 下午5:58
 */
@Controller
@RequestMapping("/QQRedirect.do")
public class QQRedirectController {
  private static final Logger LOG = LoggerFactory.getLogger(QQRedirectController.class);

  @RequestMapping(params = "method=getQQStatus")
  public String getQQStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try{
      String redirectUrl = "http://webpresence.qq.com/getonline?Type=1&%s";
      String qqStr = request.getParameter("qqStr");
      if (StringUtils.isNotEmpty(qqStr)) {
        redirectUrl = String.format(redirectUrl,qqStr);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", redirectUrl);
        return "redirect:"+redirectUrl;
      }
      return null;
    } catch (Exception e){
      LOG.error("/QQRedirect.do?method=getQQStatus,",e);
    }
    return null;
  }


}
