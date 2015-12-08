package com.bcgogo.admin;

import com.bcgogo.common.TreeMenuDTO;
import com.bcgogo.util.WebUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.IViewerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-9
 * Time: 下午5:18
 * 页面展示
 */
@Controller
@RequestMapping("/view.do")
public class ViewController {
  private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);

  @RequestMapping(params = "method=getTreeMenuByParentId")
  @ResponseBody
  public Object getTreeMenuByParentId(HttpServletRequest request, HttpServletResponse response, String id) {
    IViewerService viewerService = ServiceManager.getService(IViewerService.class);
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    List<TreeMenuDTO> treeMenuDTOList = viewerService.getTreeMenuByParentId(shopId, userId, StringUtils.isNotBlank(id) ? Long.valueOf(id) : null);
    return treeMenuDTOList;
  }


  //退出登录
  @RequestMapping(params = "method=logout")
  public Object Logout(ModelMap model, HttpServletRequest request) {
    request.getSession().removeAttribute("shopId");
    request.getSession().removeAttribute("userId");
    request.getSession().removeAttribute("userName");
    request.getSession().removeAttribute("userGroupType");
    request.getSession().invalidate();
    return "/login";
  }

  //判断session是否存在
  @RequestMapping(params = "method=sessionTimeout")
  @ResponseBody
  public Object sessionTimeout(HttpServletRequest request, HttpServletResponse response) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("success", false);
    result.put("message", "session过期!");
    return result;
  }

  @RequestMapping(params = "method=index")
  public String createMain(HttpServletRequest request) {
    String loginType = request.getParameter("loginType");
    if (loginType.equals("crm")) {
      return "/WEB-INF/views/index";
    } else {
      return "/WEB-INF/views/main";
    }
  }


}
