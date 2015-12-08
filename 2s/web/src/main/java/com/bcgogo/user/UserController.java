package com.bcgogo.user;


import com.bcgogo.BooleanEnum;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.user.Status;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.IShoppingCartService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.userGuide.UserGuideHistoryDTO;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.MemberConstant;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StopWatchUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Zhuyinjia
 * Date: 9/27/11
 * Time: 10:34 PM
 */
@Controller
@RequestMapping("/user.do")
public class UserController {
  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  public UserController() {

  }

  //退出登录
  @RequestMapping(params = "method=logout")
  public String Logout(ModelMap model, HttpServletRequest request) {
    request.getSession().removeAttribute("shopId");
    request.getSession().removeAttribute("userId");
    request.getSession().removeAttribute("userName");
    request.getSession().invalidate();
    return "/login";
  }

  //获取某一用户的信息
  @RequestMapping(params = "method=getuserbyuserid")
  public String getUserByUserId(ModelMap model, @RequestParam("userId") String userId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    UserDTO userDTO = userService.getUserByUserId(Long.parseLong(userId));
    model.addAttribute("userDTO", userDTO);
    return "/user/edituserinfo";
  }

  //修改用户信息
  @RequestMapping(params = "method=updateuser")
  public String updateUser(ModelMap model, UserDTO userDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      userService.updateUser(userDTO);
    } catch (BcgogoException e) {
      LOG.debug("/user.do");
      LOG.debug("method=getuserbyuserid");
      LOG.debug(userDTO.toString());
    }

    return getUserByUserId(model, userDTO.getId().toString());
  }

  @RequestMapping(params = "method=createmain")
  public String createMain(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    String userNo = WebUtil.getUserNo(request);
    StopWatchUtil sw=  new StopWatchUtil("createMain: " + userNo, "getShop");

    Long shopId = WebUtil.getShopId(request);
    if(shopId == null){
      return "/WEB-INF/views/main";
    }
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);

    sw.stopAndStart("getImages");

    IImageService imageService = ServiceManager.getService(IImageService.class);
    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
    imageSceneList.add(ImageScene.SHOP_RQ_IMAGE);
    imageService.addImageToShopDTO(imageSceneList, false, shopDTO);
    model.addAttribute("shopDTO", shopDTO);

    sw.stopAndPrintWarnLog();
    return "/WEB-INF/views/main";
  }

  @ResponseBody
  @RequestMapping(params = "method=checkSession")
  public Result checkSession(HttpServletRequest request) {
    return new Result(!WebUtil.isSessionElementEmpty(request));
  }

  @ResponseBody
  @RequestMapping(params = "method=getUserScreenInfo")
  public Object getUserScreenInfo(HttpServletRequest request) {
    Object[] arr = {
        request.getParameter("width"),
        request.getParameter("height"),
        request.getParameter("uuid"),
        WebUtil.getShopId(request),
        WebUtil.getShopName(request),
        request.getHeader("User-Agent")
    };
    LOG.warn("用户浏览器主页监控,width:[{}],height:[{}],uuid:[{}],shopId:[{}],shopName:[{}],userAgent:[{}]",arr);
    return "suc";
  }

  /**
   * 不登录获取店铺信息
   *
   * @param model
   * @param finger
   * @return
   */
  @RequestMapping(params = "method=getProbableShopByFinger")
  public Object getProbableShopByFinger(ModelMap model, String finger) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    return userService.getProbableShopByFinger(finger);
  }


  @RequestMapping(params = "method=changeMemberPassword")
  public void changeMemberPassword(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                   String memberId, String oldPw, String newPw) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    Long id = Long.parseLong(memberId);
    String changeResult = "";
    try {
      changeResult = userService.changeMemberPassword(id, oldPw, newPw);
      //发送短信逻辑
      Boolean isSendSms = BooleanUtils.toBoolean(request.getParameter("sendSms"));
      if (BooleanUtils.isTrue(isSendSms) && changeResult.equals(MemberConstant.CHANGE_PASSWORD_SUCCESS)) {
        UserDTO userDTO = new UserDTO();
        userDTO.setPasswordWithoutEncrypt(newPw);
        userDTO.setName(request.getParameter("userName"));   //会员名
//        userDTO.setUserNo(request.getParameter("memberNo"));//
        userDTO.setMobile(request.getParameter("mobile"));//
        smsService.changeMemberPassword(WebUtil.getShopId(request), userDTO);
      }
    } catch (Exception e) {
      changeResult = MemberConstant.CHANGE_PASSWORD_FAIL;
      LOG.debug("/user.do");
      LOG.debug("method=changeMemberPassword");
      LOG.debug(changeResult);
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(changeResult);
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(params = "method=resetUserPassword")
  @ResponseBody
  public Object resetUserPassword(HttpServletRequest request, String userId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (StringUtils.isBlank(userId) || shopId == null) throw new Exception("userId or shopId is empty!");
      UserDTO userDTO = userCacheService.getUser(shopId, Long.valueOf(userId));
      String passwordWithEncrypt = EncryptionUtil.encryptPassword("123456", shopId);
      userDTO.setPassword(passwordWithEncrypt);
      userCacheService.setUser(userDTO);
      result.put("success", true);
      result.put("password", passwordWithEncrypt);
    } catch (Exception e) {
      LOG.debug("/user.do");
      LOG.debug("method=resetUserPassword");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  //密码修改
  @RequestMapping(params = "method=changePassword")
  @ResponseBody
  public Object ChangePassword(HttpServletRequest request, String oldPassword, String newPassword, Boolean sendSms) {
    long shopId = WebUtil.getShopId(request);
    long userId = WebUtil.getUserId(request);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    if (StringUtils.isBlank(oldPassword) && StringUtils.isBlank(newPassword)) {
      result.put("success", false);
      result.put("info", "密码修改失败！");
      return result;
    }
    UserDTO userDTO = userService.getUserByUserId(userId);
    if (!EncryptionUtil.encryptPassword(oldPassword, shopId).equals(userDTO.getPassword())) {
      result.put("success", false);
      result.put("info", "您输入的密码有误，请重新输入！");
      return result;
    }
    userDTO.setPassword(EncryptionUtil.encryptPassword(newPassword, shopId));
    userDTO.setPasswordWithoutEncrypt(newPassword);
    try {
      userService.updateUser(userDTO);
      //短信通知
      if (sendSms != null && sendSms) {
        smsService.changePassword(shopId, userDTO);
      }
      result.put("success", true);
      result.put("info", "密码修改成功！");
    } catch (Exception e) {
      LOG.debug("/user.do");
      LOG.debug("method=changePassword");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      request.setAttribute("info", e.getMessage());
    }
    return result;
  }

  //分配账户
  @RequestMapping(params = "method=allocatedUserNo")
  @ResponseBody
  public Object allocatedUserNo(HttpServletRequest request, Long salesManId, String userNo, Long userGroupId, Long userId) {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<String, Object> result;
    try {
      Long shopId = WebUtil.getShopId(request);
      if (salesManId == null || shopId == null || userGroupId == null) throw new Exception("salesManId or shopId or userGroupId is empty!");
      if (StringUtils.isBlank(userNo)) throw new Exception("userNo is empty!");
      result = userCacheService.allocatedUserNoByStaff(shopId, salesManId, userNo, userGroupId, userId);

    } catch (Exception e) {
      LOG.debug("/user.do");
      LOG.debug("method=resetUserPassword");
      result = new HashMap<String, Object>();
      result.put("success", false);
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=updateUsersStatus")
  @ResponseBody
  public Object updateUsersStatus(HttpServletRequest request, String ids, String status) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("success", true);
    result.put("message", "操作成功!");
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(ids) || StringUtils.isBlank(status)) {
        LOG.warn("user id[{}] or status[{}]  is empty.", ids, status);
        return null;
      }
      String[] idsArray = ids.split(",");
      if (ArrayUtils.isEmpty(idsArray)) {
        LOG.warn("user id is empty.");
        return null;
      }
      Long[] idsLong = new Long[idsArray.length];
      for (int i = 0, max = idsArray.length; i < max; i++) {
        idsLong[i] = Long.valueOf(idsArray[i]);
      }
      ServiceManager.getService(IUserCacheService.class).updateUsersStatusByIds(Status.valueOf(status), shopId, idsLong);
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", "操作失败!");
      LOG.debug("/user.do");
      LOG.debug("method=updateUsersStatus");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  //添加用户 - 查找用户名是否重复
  @ResponseBody
  @RequestMapping(params = "method=checkUserNo")
  public Object checkUserNo(HttpServletRequest request, HttpServletResponse response) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      String userNo = request.getParameter("userNo");
      String salesManId = request.getParameter("salesManId");
      IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
      if(StringUtils.isNotBlank(salesManId)){
        result.put("isDuplicate", userCacheService.checkUserNo(userNo,Long.valueOf(salesManId)));
      } else{
        result.put("isDuplicate", userCacheService.checkUserNo(userNo));
      }
    } catch (Exception e) {
      LOG.debug("/user.do");
      LOG.debug("method=checkUserNo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @ResponseBody
  @RequestMapping(params = "method=getUserIdBySalesManId")
  public Object getUserIdBySalesManId(HttpServletRequest request, HttpServletResponse response) {
       IUserService userService = ServiceManager.getService(IUserService.class);
       try {
         Long shopId = WebUtil.getShopId(request);
         if(shopId == null) throw new Exception("shopId is null!");
         String salesManIdStr = request.getParameter("salesManId");
         if(StringUtils.isEmpty(salesManIdStr)) throw new Exception("salesManId is null");
         User user = userService.getUserBySalesManId(shopId, NumberUtil.longValue(salesManIdStr));
         if(user == null) return null;
         return user.toDTO();
       } catch (Exception e) {
         LOG.error(e.getMessage(),e);
         return null;
       }
  }

}
