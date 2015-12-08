package com.bcgogo.web;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


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

    if (pAuthentication == null) {
      request.getSession().invalidate ();
      return "/login";
    }

    GrantedAuthority ga = (GrantedAuthority) pAuthentication.getAuthorities().iterator().next();
    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(((User) pAuthentication.getPrincipal()).getUsername());

    if (null != ga) {
      String role = ga.getAuthority();
      if (role.trim().equals("ROLE_SUPERADMIN") || role.trim().equals("ROLE_MANAGER") || role.trim().equals("ROLE_SHOPADMIN")) {
        return "/WEB-INF/views/main";
      } else if (role.trim().equals("ROLE_SALES")) {
        return "redirect:shop.do?method=shopregbasicinfo";
      } else if (role.trim().equals("ROLE_BUYER")) {
        return "redirect:user.do?method=add";
      }
    }
    return "/WEB-INF/views/main";
  }

}
