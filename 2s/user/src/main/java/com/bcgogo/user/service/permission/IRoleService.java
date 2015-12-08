package com.bcgogo.user.service.permission;

import com.bcgogo.enums.SystemType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.dto.permission.UserGroupRoleDTO;
import com.bcgogo.user.dto.permission.RoleDTO;
import com.bcgogo.user.dto.permission.ShopRoleDTO;
import com.bcgogo.user.dto.permission.UserGroupRoleDTO;
import com.bcgogo.user.model.permission.Role;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:19
 */
public interface IRoleService {
  RoleDTO setRole(String name, String memo) throws BcgogoException;

  void setRole(Long id, Long moduleId, SystemType type);

  List<RoleDTO> getAllRoles(Long shopId, Long userId, Long userGroupId);

  //保存权限配置 db and cache
  boolean saveUserGroupRoles(List<UserGroupRoleDTO> userGroupRoleDTOList, UserGroupDTO userGroupDTO, Long shopId);

  //更新权限配置 db and cache
  boolean updateUserGroupRoles(List<UserGroupRoleDTO> userGroupRoleDTOList, Long userGroupId);

  List<Long> getRolesByUserId(Long shopId, Long userId);

  boolean checkRole(RoleDTO roleDTO);

  RoleDTO saveOrUpdateRole(RoleDTO roleDTO);

  boolean checkRoleBeforeDelete(Long roleId, Long shopId);

  boolean deleteRole(Long roleId, Long shopId);

  List<RoleDTO> getRolesByResourceId(Long resourceId);

}
