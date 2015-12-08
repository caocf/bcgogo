package com.bcgogo.admin.user.permission;

import com.bcgogo.util.WebUtil;
import com.bcgogo.enums.SystemType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.ModuleDTO;
import com.bcgogo.user.permission.ModuleResult;
import com.bcgogo.user.service.permission.IModuleService;
import com.bcgogo.user.service.permission.IRoleService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-30
 * Time: 下午2:30
 * 角色 controller
 */
@Controller
@RequestMapping("/module.do")
public class ModuleController {
  private static final Logger LOG = LoggerFactory.getLogger(ModuleController.class);
  private IModuleService moduleService = null;

  public IModuleService getModuleService() {
    if (moduleService == null) {
      moduleService = ServiceManager.getService(IModuleService.class);
    }
    return moduleService;
  }

  @RequestMapping(params = "method=getModulesBySystemType")
  @ResponseBody
  public Object getModulesBySystemType(HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    ModuleResult moduleResult = getModuleService().getModulesBySystemType(shopId, userId, SystemType.CRM);
    moduleResult.setSuccess(true);
    return moduleResult;
  }

  //used：crm-权限维护-角色资源维护
  //module role 组成的tree
  @RequestMapping(params = "method=getTreeModules")
  @ResponseBody
  public Object getTreeModules(HttpServletRequest request, HttpServletResponse response) {
    Node node = new Node();
    node.setName("统购平台");
    try {
      Long shopId = WebUtil.getShopId(request);
      Node shop = ServiceManager.getService(IModuleService.class).getTreeModuleRolesForBcgogoConfig(shopId, SystemType.SHOP);
      shop.setAllowDrag(false);
      node.getChildren().add(shop);
      Node crm = ServiceManager.getService(IModuleService.class).getTreeModuleRolesForBcgogoConfig(shopId, SystemType.CRM);
      crm.setAllowDrag(false);
      node.getChildren().add(crm);
    } catch (Exception e) {
      LOG.debug("/admin/module.do");
      LOG.debug("method=getTreeModules");
      LOG.error(e.getMessage(), e);
    }
    return node;
  }


  @RequestMapping(params = "method=dropModuleTree")
  @ResponseBody
  public Object dropModuleTree(HttpServletRequest request, Node node) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      if (node.getType().equals(Node.Type.ROLE)) {
        ServiceManager.getService(IRoleService.class).setRole(node.getId(), node.getParentId(), node.getSystemType());
      } else {
        getModuleService().updateModule(node.getId(), node.getParentId(), node.getSystemType());
      }
      result.put("success", true);
    } catch (Exception e) {
      LOG.debug("/admin/module.do");
      LOG.debug("method=dropModuleTree");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
    }
    return result;
  }

  @RequestMapping(params = "method=updateModule")
  @ResponseBody
  public Object updateModule(HttpServletRequest request, Node node) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      ModuleDTO moduleDTO = node.toModuleDTO();
      if (getModuleService().checkModule(moduleDTO)) {
        result.put("duplicate", true);
        return result;
      }
      moduleDTO = getModuleService().updateModule(moduleDTO);
      result.put("success", true);
      result.put("node", moduleDTO);
    } catch (Exception e) {
      LOG.debug("/admin/module.do");
      LOG.debug("method=updateModule");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteModule")
  @ResponseBody
  public Object deleteModule(HttpServletRequest request, String moduleId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(moduleId)) throw new Exception("moduleId is empty");
      result.put("success", true);
      result.put("message", getModuleService().deleteModule(Long.valueOf(moduleId), shopId));
    } catch (Exception e) {
      LOG.debug("/admin/module.do");
      LOG.debug("method=deleteModule");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }


}
