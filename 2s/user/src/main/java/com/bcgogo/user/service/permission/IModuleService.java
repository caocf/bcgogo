package com.bcgogo.user.service.permission;

import com.bcgogo.enums.SystemType;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.ModuleDTO;
import com.bcgogo.user.dto.permission.RoleDTO;
import com.bcgogo.user.permission.ModuleResult;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:17
 * CRM -- module 单层树
 * web -- module 树的层比较深
 */
public interface IModuleService {
  /**
   * 根据SystemType 获得module 无层次关系
   * @param shopId
   * @param userId
   * @param systemType
   * @return
   */
  ModuleResult getModulesBySystemType(Long shopId, Long userId, SystemType systemType);

  //module role tree without role checked  for bcgogo
  Node getTreeModuleRolesForBcgogoConfig(Long shopId, SystemType systemType);

  //module role tree
  // if userGroupId is null without role checked else checked
  // for web user
  Node getTreeModuleRolesForUserConfig(Long shopId, Long shopVersionId, Long userGroupId);

  // 获得moduleId 下的roles
  List<RoleDTO> getRolesByModuleId(Long moduleId, SystemType systemType);

  //根据name 检查是否重复
  boolean checkModule(ModuleDTO moduleDTO);

  ModuleDTO updateModule(ModuleDTO moduleDTO);

  void updateModule(Long id, Long parentId, SystemType type);

  boolean deleteModule(Long moduleId, Long shopId);

  //module role with role checked tree checked for shopVersion
  //for CRM
  Node getTreeModuleRolesForShopVersion(Long shopId, Long shopVersionId);

  //module role with role checked tree checked for userGroup
  //for CRM
  Node getTreeModuleRolesForUserGroup(Long shopId, Long shopVersionId, Long userGroupId);

  String getChainModuleNamesByRoleId(Long roleId);

}
