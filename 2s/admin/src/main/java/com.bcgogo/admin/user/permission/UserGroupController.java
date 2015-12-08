package com.bcgogo.admin.user.permission;

import com.bcgogo.util.WebUtil;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.*;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IRoleService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
  @RequestMapping("/userGroup.do")
  public class UserGroupController {
    private static final Logger LOG = LoggerFactory.getLogger(UserGroupController.class);
    private IUserGroupService userGroupService = null;

    public IUserGroupService getUserGroupService() {
      if (userGroupService == null) {
        userGroupService = ServiceManager.getService(IUserGroupService.class);
      }
      return userGroupService;
    }

    @RequestMapping(params = "method=refreshPermission")
    @ResponseBody
    public Object refreshPermission(HttpServletRequest request, HttpServletResponse response) {
      Map<String, Object> result = new HashMap<String, Object>();
      try {
        ServiceManager.getService(IPrivilegeService.class).reSetAllResourcesToMemCache();
        result.put("success", true);
      } catch (Exception e) {
        result.put("success", false);
        LOG.debug("/admin/userGroup.do");
        LOG.debug("method=refreshPermission");
        LOG.error(e.getMessage(), e);
      }
      return result;
    }

    @RequestMapping(params = "method=getUserGroupByCondition")
    @ResponseBody
    public Object getUserGroupByCondition(HttpServletRequest request, HttpServletResponse response, UserGroupSearchCondition condition) {
      IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
      condition.setShopId(WebUtil.getShopId(request));
      condition.setShopVersionId(WebUtil.getShopVersion(request).getId());
      return userGroupService.getUserGroupByCondition(condition);
    }

    //CRM 角色资源配置
    @RequestMapping(params = "method=updateUserGroupRoles")
    @ResponseBody
    public Object updateUserGroupRoles(HttpServletRequest request, HttpServletResponse response) {
      IRoleService roleService = ServiceManager.getService(IRoleService.class);
      String[] roleIds = request.getParameter("roleIds").split(",");
      String[] userGroupIds = request.getParameter("userGroupIds").split(",");
      String[] checks = request.getParameter("checks").split(",");
      List<UserGroupRoleDTO> userGroupRoleDTOList = new ArrayList<UserGroupRoleDTO>();
      UserGroupRoleDTO userGroupRoleDTO = null;
      for (int i = 0, max = roleIds.length; i < max; i++) {
        userGroupRoleDTO = new UserGroupRoleDTO();
        userGroupRoleDTO.setRoleId(Long.valueOf(roleIds[i]));
        userGroupRoleDTO.setUserGroupId(Long.valueOf(userGroupIds[i]));
        if ("true".equals(checks[i])) {
          userGroupRoleDTO.setStatus(Status.active);
        } else {
          userGroupRoleDTO.setStatus(Status.inActive);
        }
        userGroupRoleDTOList.add(userGroupRoleDTO);
      }
      return roleService.updateUserGroupRoles(userGroupRoleDTOList, userGroupRoleDTOList.get(0).getUserGroupId());
    }


    //获得某个版本的userGroup
    @RequestMapping(params = "method=getUserGroupsByShopVersionId")
    @ResponseBody
    public Object getUserGroupsByShopVersionId(HttpServletRequest request, Long shopVersionId) {
      Map<String, List<UserGroupDTO>> result = new HashMap<String, List<UserGroupDTO>>();
      try {
        Long shopId = WebUtil.getShopId(request);
        if (shopId == null) throw new Exception("shopId is null!");
        if (shopVersionId == null) return result;
        result.put("results", getUserGroupService().getUserGroupsByShopVersionId(shopVersionId));
      } catch (Exception e) {
        LOG.debug("/admin/userGroup.do");
        LOG.debug("method=getUserGroupsByShopVersionId");
        LOG.error(e.getMessage(), e);
      }
      return result;
    }

    @RequestMapping(params = "method=saveUserGroupForShopVersion")
    @ResponseBody
    public Object saveUserGroupForShopVersion(HttpServletRequest request, UserGroupDTO dto, Long shopVersionId) {
      Map<String, Object> result = new HashMap<String, Object>();
      try {
        Long shopId = WebUtil.getShopId(request);
        if (shopId == null) throw new Exception("shopId is null!");
        if (shopVersionId == null) throw new Exception("shopVersionId is null!");
        dto.setStatus(Status.active);
        dto.setVariety("SYSTEM_DEFAULT");
        getUserGroupService().saveUserGroupForShopVersion(dto, shopVersionId);
        result.put("success", true);
        result.put("message", "操作成功!");
      } catch (Exception e) {
        LOG.debug("/admin/userGroup.do");
        LOG.debug("method=saveUserGroupForShopVersion");
        LOG.error(e.getMessage(), e);
        result.put("success", false);
        result.put("message", "操作失败!");
      }
      return result;
    }

    @RequestMapping(params = "method=updateUserGroupsStatusByIds")
    @ResponseBody
    public Object updateUserGroupsStatusByIds(String ids, String status) {
      if (StringUtils.isBlank(ids) || StringUtils.isBlank(status)) {
        LOG.warn("updateUserGroups or status[{},{}] id is empty.", ids, status);
        return null;
      }
      String[] idsArray = ids.split(",");
      if (ArrayUtils.isEmpty(idsArray)) {
        LOG.warn("updateUserGroups id is empty.");
        return null;
      }
      Long[] idsLong = new Long[idsArray.length];
      for (int i = 0, max = idsArray.length; i < max; i++) {
        idsLong[i] = Long.valueOf(idsArray[i]);
      }
      return ServiceManager.getService(IUserGroupService.class).updateUserGroupsByIds(Status.valueOf(status), idsLong);
    }

  //  @RequestMapping(params = "method=updateUserGroup")
  //  @ResponseBody
  //  public Object updateUserGroup(HttpServletRequest request, UserGroupDTO dto) {
  //    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
  //    dto.setType(SystemType.CRM);
  //    dto.setShopId(ShopConstant.BC_SHOP_ID);
  //    userGroupService.setUserGroup(dto);
  //    Map<String, Object> result = new HashMap<String, Object>();
  //    result.put("success", true);
  //    result.put("message", "操作成功!");
  //    return result;
  //  }

    @RequestMapping(params = "method=updateUserGroup")
    @ResponseBody
    public Object updateUserGroup(HttpServletRequest request, UserGroupDTO dto) {
      Map<String, Object> result = new HashMap<String, Object>();
      try {
        Long shopId = WebUtil.getShopId(request);
        dto.setShopId(shopId);
        getUserGroupService().setUserGroup(dto);
        result.put("success", true);
        result.put("message", "操作成功!");
      } catch (Exception e) {
        LOG.debug("/admin/userGroup.do");
        LOG.debug("method=updateUserGroup");
        LOG.error(e.getMessage(), e);
        result.put("success", false);
        result.put("message", "操作失败!");
      }
      return result;
    }

    @RequestMapping(params = "method=deleteUserGroup")
    @ResponseBody
    public Object deleteUserGroup(HttpServletRequest request, Long userGroupId, Long shopVersionId) {
      Map<String, Object> result = new HashMap<String, Object>();
      try {
        Long shopId = WebUtil.getShopId(request);
        if (shopId == null) throw new Exception("shopId is null!");
        if (userGroupId == null) throw new Exception("userGroupId is null!");
        if (shopVersionId == null) throw new Exception("shopVersionId is null!");
        getUserGroupService().deleteUserGroup(userGroupId, shopVersionId);
        result.put("success", true);
        result.put("message", "操作成功!");
      } catch (Exception e) {
        LOG.debug("/admin/userGroup.do");
        LOG.debug("method=deleteUserGroup");
        LOG.error(e.getMessage(), e);
        result.put("success", false);
        result.put("message", "操作失败!");
      }
      return result;
    }

    //保存 更新 userGroup 的role
    @RequestMapping(params = "method=saveRolesConfigForUserGroup")
    @ResponseBody
    public Object saveRolesConfigForUserGroup(HttpServletRequest request, HttpServletResponse response, Long userGroupId) {

      Map<String, Object> result = new HashMap<String, Object>();
      try {
        Long shopId = WebUtil.getShopId(request);
        if (shopId == null) throw new Exception("shopId is null!");
        if (userGroupId == null) throw new Exception("userGroupId is null!");
        String roleIds = request.getParameter("roleIds");
        List<UserGroupRoleDTO> userGroupRoleDTOList = new ArrayList<UserGroupRoleDTO>();
        if (StringUtils.isNotBlank(roleIds)) {
          String[] roleIdArray = roleIds.split(",");
          UserGroupRoleDTO userGroupShopDTO = null;
          for (int i = 0, max = roleIdArray.length; i < max; i++) {
            userGroupShopDTO = new UserGroupRoleDTO();
            userGroupShopDTO.setRoleId(Long.valueOf(roleIdArray[i]));
            userGroupShopDTO.setUserGroupId(userGroupId);
            userGroupShopDTO.setStatus(Status.active);
            userGroupRoleDTOList.add(userGroupShopDTO);
          }
        }
        result.put("success", getUserGroupService().saveRolesConfigForUserGroup(userGroupRoleDTOList, userGroupId));
      } catch (Exception e) {
        result.put("success", false);
        LOG.debug("/admin/shopVersion.do");
        LOG.debug("method=saveRolesConfigForShopVersion");
        LOG.error(e.getMessage(), e);
      }
      return result;
    }

  }
