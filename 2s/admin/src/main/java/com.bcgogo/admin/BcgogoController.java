package com.bcgogo.admin;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/8/11
 * Time: 10:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/*")
public class BcgogoController {

  @RequestMapping
  public String welcomeHandler(UsernamePasswordAuthenticationToken pAuthentication, HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null || !session.getAttributeNames().hasMoreElements()) {
      //状态：登录
      return "/login";
    } else {
      return "/WEB-INF/views/index";
    }
  }

}
