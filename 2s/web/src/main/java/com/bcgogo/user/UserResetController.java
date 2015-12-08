package com.bcgogo.user;

import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.messageCenter.INoticeService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.model.UserVercode;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: Xiao Jian
 * Date: 12-1-12
 */

@Controller
@RequestMapping("/userreset.do")
public class UserResetController {
  private static final Logger LOG = LoggerFactory.getLogger(UserResetController.class);

  @RequestMapping(params = "method=resetPasswordInit")
  public String resetPasswordInit(HttpServletRequest request, HttpServletResponse response) {
    String username = request.getParameter("username");
    request.setAttribute("username", username);
    return "/passwordreset";
  }


  /**
   * 发送验证码
   * 生成随机验证码
   *
   * @param request
   * @param response
   */
  @RequestMapping(params = "method=sendVerCode")
  public void sendVerCode(HttpServletRequest request, HttpServletResponse response) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    UserDTO userDTO = userService.getUserByUserInfo(request.getParameter("username"));
    String info = null;
    if (userDTO == null) {
      info = "用户无效！\n\n请检查您的用户信息。";
    } else {
      String vercode = userService.generatePassword();
      try {
        userService.createUserVercode(userDTO.getUserNo(), vercode);
        ShopBalanceDTO shopBalanceDTO = ServiceManager.getService(IShopBalanceService.class)
            .getSmsBalanceByShopId(userDTO.getShopId());
        if (shopBalanceDTO == null || shopBalanceDTO.getSmsBalance() == null || shopBalanceDTO.getSmsBalance() <= 5d) {
          info = "对不起，您的短信余额不足，不能发送重置密码短信！请短信充值后再使用！如有问题请拨打客服电话:0512-66733331与我们联系！";
        } else {
          //短信通知
          smsService.verificationCode(userDTO, vercode);
          info = "系统已将验证码发送到您手机，请注意查收！";
        }
      } catch (Exception e) {
        LOG.debug("/userreset.do");
        LOG.debug("method=sendVerCode");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        info = "发送验证码失败!请稍后重试!";
      }
    }
    try {
      PrintWriter out = response.getWriter();
      out.write(info);
      out.flush();
      out.close();
    } catch (IOException ioe) {
      LOG.debug("/userreset.do");
      LOG.debug("method=sendVerCode");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(ioe.getMessage(), ioe);
    }
  }


  /*
 * 用户通过手机接受到的验证码重新设置新密码
 * 返回1表示成功!其他为错误提示信息
 * */
  @RequestMapping(params = "method=resetPassword")
  public String resetPassword(HttpServletRequest request, HttpServletResponse response) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    String username = request.getParameter("username");
    String vercode = request.getParameter("vercode");
    String password = request.getParameter("password");

    UserDTO userDTO = null;
    String info = null;
    String url = "/passwordreset";

    request.setAttribute("username", username);
    userDTO = userService.getUserByUserInfo(username);
    if (userDTO == null) {
      info = "用户无效！\n\n请检查您的用户信息。";
      request.setAttribute("info", info);
      return url;
    }


    UserVercode userVercode = userService.getUserVercodeByUserNo(userDTO.getUserNo());
    if (userVercode == null) {
      info = "验证码无效,请重试!";
      request.setAttribute("info", info);
      return url;
    }

//     检测是否超时
    long curTime = System.currentTimeMillis();
    if (curTime - userVercode.getCreationDate() > 1000 * 60 * 3) {
      info = "该验证码已超时,请重新获取!";
      request.setAttribute("info", info);
      return url;
    }


    if (!userVercode.getVercode().equals(vercode)) {
      info = "验证码无效,请重试!";
      request.setAttribute("info", info);
      return url;
    }

    try {
      userDTO.setPassword(EncryptionUtil.encryptPassword(password, userDTO.getShopId()));
      userService.updateUser(userDTO);
    } catch (Exception e) {
      LOG.debug("/userreset.do");
      LOG.debug("method=resetPassword");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      info = "更新密码失败,请稍后再试!";
      LOG.error(e.getMessage(), e);
      request.setAttribute("info", info);
      return url;
    }

    info = "1";
    request.setAttribute("info", info);
    return url;


  }


//  public void resetPassword(HttpServletRequest request, HttpServletResponse response) {
//    IUserService userService = ServiceManager.getService(IUserService.class);
//
//    UserDTO userDTO = userService.getUserByUserInfo(request.getParameter("username"));
//
//    String info = null;
//    if (userDTO == null) {
//      info = "用户无效！\n\n请检查您的用户信息。";
//    } else {
//      String pwd = userService.generatePassword();
//      userDTO.setPassword(EncryptionUtil.encryptPassword(pwd));
//      try {
//        userService.updateUser(userDTO);
//      } catch (BcgogoException be) {
//        info = "重置密码失败！";
//      }
//
//      if (info == null) {
//
//        //短信通知
//        String content = "您好!系统已将您的密码重置。新的密码:<" + String.valueOf(pwd) + ">,用户名不变。祝您使用愉快!如有任何问题请随时拨打客服电话:0512-66733331与我们联系。";
//
//        ISmsService smsService = ServiceManager.getService(ISmsService.class);
//        smsService.sendSmsInTime(0L, userDTO.getMobile(), content);
//
//        info = "系统已将新的密码发送到您手机，请注意查收！";
//      }
//    }
//
//
//    try {
//      PrintWriter out = response.getWriter();
//      out.write(info);
//      out.flush();
//      out.close();
//    } catch (IOException ioe) {
//      LOG.error(ioe.getMessage());
//    }
//  }


}
