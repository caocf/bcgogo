package com.bcgogo.web;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.permission.IMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: ZhangJuntao
 * Date: 13-4-17
 * Time: 上午9:44
 */
@Controller
@RequestMapping("/menu.do")
public class MenuController {
  private static final Logger LOG = LoggerFactory.getLogger(MenuController.class);

  @RequestMapping(params = "method=getMenu")
  @ResponseBody
  public Object getMenu(HttpServletRequest request) {
    Result result = new Result(true);
    try {
      result.setData(ServiceManager.getService(IMenuService.class).buildMenu(WebUtil.getShopVersionId(request), WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request)));
    } catch (Exception e) {
      result = new Result();
      result.setSuccess(false);
      LOG.debug("method=getMenu");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }
}
