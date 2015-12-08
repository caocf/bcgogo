package com.bcgogo.admin;

import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.dto.permission.UserGroupRoleDTO;
import com.bcgogo.user.model.permission.UserGroup;
import com.bcgogo.user.service.permission.IModuleService;
import com.bcgogo.user.service.permission.IRoleService;
import com.bcgogo.user.service.permission.IUserGroupService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-15
 * Time: 下午9:32
 * 权限配置
 */
@Controller
@RequestMapping("/permissionManager.do")
public class PermissionManagerController {
  private static final Logger LOG = LoggerFactory.getLogger(PermissionManagerController.class);

  @RequestMapping(params = "method=showPermissionConfig")
  public String showPermissionConfig(HttpServletRequest request, HttpServletResponse response, Long userGroupId) throws IOException {
    if (userGroupId != null) {
      String copyPermission = request.getParameter("copyPermission");
      if (StringUtils.isBlank(copyPermission)) {
        request.setAttribute("userGroupDTO", ServiceManager.getService(IUserGroupService.class).getUserGroupDTO(userGroupId));
        request.setAttribute("defaultSysUserGroup", request.getParameter("defaultSysUserGroup"));
      } else {
        request.setAttribute("copyPermission", request.getParameter("copyPermission"));
        request.setAttribute("copyUserGroupId", userGroupId);
      }
    } else{
      //并发问题
//      request.setAttribute("userGroupNo", ServiceManager.getService(IUserGroupService.class).initUserGroupNo(WebUtil.getShopId(request)));
    }
    return "/admin/permissionManager/permissionConfig";
  }

  //module role 组成的tree
  @RequestMapping(params = "method=getTreeModuleRolesForUserConfig")
  @ResponseBody
  public Object getTreeModuleRolesForUserConfig(HttpServletRequest request, HttpServletResponse response, Long userGroupId) {
    Node node = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersion(request).getId();
      if (shopId == null) throw new Exception("shopId is null!");
      node = ServiceManager.getService(IModuleService.class).getTreeModuleRolesForUserConfig(shopId, shopVersionId, userGroupId);
    } catch (Exception e) {
      LOG.debug("/admin/user.do");
      LOG.debug("method=getDepartmentsAndOccupations");
      LOG.error(e.getMessage(), e);
    }
    return node;
  }

  //检查用户组名是否重复
  @RequestMapping(params = "method=checkUserGroupName")
  @ResponseBody
  public Object checkUserGroupName(HttpServletRequest request, String userGroupName, Long userGroupId) {
    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersion(request).getId();
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(userGroupName)) return result;
      result.put("isDuplicated", userGroupService.checkUserGroupName(userGroupName, shopVersionId, shopId, userGroupId));
      result.put("message", "操作成功!");
    } catch (Exception e) {
      LOG.debug("/admin/permission.do");
      LOG.debug("method=checkRoleBeforeDelete");
      LOG.error(e.getMessage(), e);
      result.put("message", "操作失败!");
    }
    return result;
  }

  //保存更新资源配置
  @RequestMapping(params = "method=saveOrUpdatePermissionConfig")
  @ResponseBody
  public Object saveOrUpdatePermissionConfig(HttpServletRequest request, HttpServletResponse response, UserGroupDTO userGroupDTO) {
    IRoleService roleService = ServiceManager.getService(IRoleService.class);
    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
    Long shopId = WebUtil.getShopId(request);
    String[] roleIds = request.getParameter("roleIds").split(",");
    String[] checks = request.getParameter("checks").split(",");
    List<UserGroupRoleDTO> userGroupRoleDTOList = new ArrayList<UserGroupRoleDTO>();
    UserGroupRoleDTO userGroupRoleDTO = null;
    for (int i = 0, max = roleIds.length; i < max; i++) {
      userGroupRoleDTO = new UserGroupRoleDTO();
      userGroupRoleDTO.setStatus(Status.active);
      userGroupRoleDTO.setRoleId(Long.valueOf(roleIds[i]));
      userGroupRoleDTO.setUserGroupId(userGroupDTO.getId());
      if ("true".equals(checks[i])) {
        userGroupRoleDTO.setStatus(Status.active);
      } else {
        userGroupRoleDTO.setStatus(Status.inActive);
      }
      userGroupRoleDTOList.add(userGroupRoleDTO);
    }
    if (userGroupDTO.getId() != null) {
      UserGroupDTO dto = userGroupService.getUserGroupDTO(userGroupDTO.getId());
      dto.setName(userGroupDTO.getName());
      dto.setMemo(userGroupDTO.getMemo());
      userGroupService.setUserGroup(dto);
      return roleService.updateUserGroupRoles(userGroupRoleDTOList, userGroupDTO.getId());
    } else {
      userGroupDTO.setUserGroupNo(userGroupService.initUserGroupNo(shopId));
//      userGroupDTO.setType(SystemType.SHOP);
      userGroupDTO.setStatus(Status.active);
      userGroupDTO.setVariety("CUSTOM");
      return roleService.saveUserGroupRoles(userGroupRoleDTOList, userGroupDTO, shopId);
    }
  }


}
