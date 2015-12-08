package com.bcgogo.user.service.permission;

import com.bcgogo.enums.user.Status;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.dto.permission.UserGroupRoleDTO;
import com.bcgogo.user.dto.permission.UserGroupSearchCondition;
import com.bcgogo.user.dto.permission.UserGroupUserDTO;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.UserGroup;
import com.bcgogo.user.permission.UserGroupSearchResult;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:16
 */
public interface IUserGroupService {
  UserGroup getUserGroup(Long userGroupId);

  UserGroupDTO getUserGroupDTO(Long userGroupId);

  UserGroupDTO setUserGroup(UserGroupDTO userGroupDTO);

  //创建新的user group，分别保存userGroupShop，与调用者同事务
  Long createUserGroup(UserGroupDTO userGroupDTO, Long shopId, UserWriter writer);

  //shopId 与 shopVersionId 有且只有一个不为空
  UserGroupDTO getUniqueUserGroupByName(String name, Long shopId, Long shopVersionId);

  //查找该店与对应版本的用户组
  List<UserGroupDTO> getUserGroupByName(String name,boolean isFuzzyMatching,Long shopId,Long shopVersionId);

   List<UserGroupDTO> getAllUserGroup();

  List<Long> getAllUserGroupIds();

  UserGroupUserDTO setUserGroupUser(UserGroupUserDTO userGroupUserDTO, UserWriter writer);

  //保存UserGroupUserDTO 单独事务
  UserGroupUserDTO setUserGroupUser(UserGroupUserDTO userGroupUserDTO);

  boolean deleteUserGroupUser(Long userId, Long userGroupId, UserWriter writer) throws BcgogoException;

  //删除前检查是否被使用  true 表示被使用
  boolean checkBeforeDeleteUserGroup(Long userGroupId);

  //查询 用户组 for crm
  UserGroupSearchResult getUserGroupByCondition(UserGroupSearchCondition condition);
  //查询 用户组 for shop
  UserGroupSearchResult getUserGroupsByCondition(Long shopId, Long shopVersionId, UserGroupSearchCondition condition);

  //根据用户组id集合 更新状态
  Boolean updateUserGroupsByIds(Status status, Long... ids);

  //根据shop 版本获得 userGroup
  List<UserGroupDTO> getUserGroupsByShopVersionId(Long shopVersionId);

  //为某个版本增加用户组
  void saveUserGroupForShopVersion(UserGroupDTO dto, Long shopVersionId);

  //根据版本删除所包含的用户组以及这些用户组下面的角色
  void deleteUserGroup(Long userGroupId, Long shopVersionId);

  //为用户组 设置 角色
  boolean saveRolesConfigForUserGroup(List<UserGroupRoleDTO> userGroupRoleDTOList, Long userGroupId);

  //根据userId 获得用户组
  UserGroupDTO getUserGroupByUserId(Long userId);

  //检查用户组名是否重复
  boolean checkUserGroupName(String name, Long shopVersionId, Long shopId, Long userGroupId);

  //为用户组初始编号
  String initUserGroupNo(Long shopId);

  //web 逻辑删除userGroup
  void deleteUserGroupLogical(Long userGroupId);

  List<UserGroup> getUserGroupByIds(Long... ids);

}
