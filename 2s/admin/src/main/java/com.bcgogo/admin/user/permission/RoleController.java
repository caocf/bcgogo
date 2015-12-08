package com.bcgogo.admin.user.permission;

import com.bcgogo.util.WebUtil;
import com.bcgogo.enums.SystemType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.dto.permission.ResourceSearchCondition;
import com.bcgogo.user.dto.permission.RoleDTO;
import com.bcgogo.user.dto.permission.RoleResourceDTO;
import com.bcgogo.user.permission.ResourceSearchResult;
import com.bcgogo.user.permission.RoleResult;
import com.bcgogo.user.service.permission.IResourceService;
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
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-30
 * Time: 下午2:30
 * 角色 controller
 */
@Controller
@RequestMapping("/role.do")
public class RoleController {
  private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);
  private IRoleService roleService = null;

  public IRoleService getRoleService() {
    if (roleService == null) {
      roleService = ServiceManager.getService(IRoleService.class);
    }
    return roleService;
  }

  @RequestMapping(params = "method=getAllRoles")
  @ResponseBody
  public Object getAllRoles(HttpServletRequest request, HttpServletResponse response, Long userGroupId) {
    IRoleService roleService = ServiceManager.getService(IRoleService.class);
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    List<RoleDTO> roleDTOList = roleService.getAllRoles(shopId, userId, userGroupId);
    RoleResult roleResult = new RoleResult();
    roleResult.setResults(roleDTOList);
    roleResult.setSuccess(true);
    return roleResult;
  }

  @RequestMapping(params = "method=getRolesByResourceId")
  @ResponseBody
  public Object getRolesByResourceId(HttpServletRequest request, HttpServletResponse response, Long resourceId) {
    Map<String, List<RoleDTO>> map = new HashMap<String, List<RoleDTO>>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null || resourceId == null)
        throw new Exception("shopId or resourceId is null!");
      List<RoleDTO> roleDTOList = this.getRoleService().getRolesByResourceId(resourceId);
      map.put("results", roleDTOList);
    } catch (Exception e) {
      LOG.debug("/admin/role.do");
      LOG.debug("method=getResourcesByRole");
      LOG.error(e.getMessage(), e);
    }
    return map;
  }

  @RequestMapping(params = "method=updateRole")
  @ResponseBody
  public Object updateRole(HttpServletRequest request, Node node) {
    IRoleService roleService = ServiceManager.getService(IRoleService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      RoleDTO roleDTO = node.toRoleDTO();
      if (roleService.checkRole(roleDTO)) {
        result.put("duplicate", true);
        return result;
      }
      roleDTO = roleService.saveOrUpdateRole(roleDTO);
      result.put("success", true);
      result.put("node", roleDTO);
    } catch (Exception e) {
      LOG.debug("/admin/permission.do");
      LOG.debug("method=updateRole");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteRole")
  @ResponseBody
  public Object deleteRole(HttpServletRequest request, String roleId) {
    IRoleService roleService = ServiceManager.getService(IRoleService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(roleId)) throw new Exception("roleId is empty");
      result.put("success", true);
      result.put("message", roleService.deleteRole(Long.valueOf(roleId), shopId));
    } catch (Exception e) {
      LOG.debug("/admin/permission.do");
      LOG.debug("method=deleteRole");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=checkRoleBeforeDelete")
  @ResponseBody
  public Object checkRoleBeforeDelete(HttpServletRequest request, String roleId) {
    IRoleService roleService = ServiceManager.getService(IRoleService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(roleId)) throw new Exception("roleId is empty");
      result.put("success", true);
      result.put("hasBeUsed", roleService.checkRoleBeforeDelete(Long.valueOf(roleId), shopId));
    } catch (Exception e) {
      LOG.debug("/admin/permission.do");
      LOG.debug("method=checkRoleBeforeDelete");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteRoleResource")
  @ResponseBody
  public Object deleteRoleResource(HttpServletRequest request, Long roleId, Long resourceId) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null || roleId == null || resourceId == null)
        throw new Exception("shopId[" + shopId + "] or roleId[" + roleId + "] or resourceId[" + resourceId + "] is null!");
      result.put("success", true);
      result.put("message", ServiceManager.getService(IResourceService.class).deleteRoleResource(roleId, resourceId));
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
