package com.bcgogo.user.service.permission;

import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.RoleDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.dto.permission.UserGroupRoleDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.Role;
import com.bcgogo.user.model.permission.RoleResource;
import com.bcgogo.user.model.permission.UserGroupRole;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:19
 * 角色读写操作服务类
 */
@Component
public class RoleService implements IRoleService {
  private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public RoleDTO setRole(String name, String memo) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Role role = writer.getRoleByName(name);
      if (role == null) {
        role = new Role();
        role.setName(name);
        role.setMemo(memo);
      } else {
        role.setName(name);
        role.setMemo(memo);
      }
      writer.saveOrUpdate(role);
      writer.commit(status);
      return role.toDTO();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void setRole(Long id, Long moduleId, SystemType type) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Role role = writer.getById(Role.class, id);
      if (role == null) return;
      role.setModuleId(moduleId);
      role.setType(type);
      writer.update(role);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<RoleDTO> getAllRoles(Long shopId, Long userId, Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Role> roleList = writer.getRoles(SystemType.CRM);
    List<RoleDTO> roleDTOList = new ArrayList<RoleDTO>();
    List<UserGroupRole> userGroupRoleList = writer.getRoleByUserGroupId(userGroupId);
    StringBuilder ids = new StringBuilder();
    for (UserGroupRole userGroupRole : userGroupRoleList) {
      ids.append(userGroupRole.getRoleId()).append(',');
    }
    RoleDTO roleDTO;
    for (Role role : roleList) {
      roleDTO = role.toDTO();
      if (ids.indexOf(role.getId().toString()) == -1) roleDTO.setHasCheckedByUserGroup(false);
      else roleDTO.setHasCheckedByUserGroup(true);
      roleDTO.setUserGroupId(userGroupId);
      roleDTOList.add(roleDTO);
    }
    return roleDTOList;
  }

  @Override
  public boolean updateUserGroupRoles(List<UserGroupRoleDTO> userGroupRoleDTOList, Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserGroupRole userGroupRole;
      for (UserGroupRoleDTO userGroupRoleDTO : userGroupRoleDTOList) {
        if (userGroupRoleDTO.getStatus() == Status.active) {
          userGroupRole = new UserGroupRole(userGroupRoleDTO);
          writer.save(userGroupRole);
        } else {
          writer.deleteUserGroupRole(userGroupRoleDTO.getUserGroupId(), userGroupRoleDTO.getRoleId());
        }
      }
      writer.commit(status);
      //更新 memCache
      ServiceManager.getService(IPrivilegeService.class).deleteResourcesFromCache(userGroupId);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean saveUserGroupRoles(List<UserGroupRoleDTO> userGroupRoleDTOList, UserGroupDTO userGroupDTO, Long shopId) {
    if (userGroupDTO == null || shopId == null || CollectionUtils.isEmpty(userGroupRoleDTOList))
      return false;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
      Long userGroupId = userGroupService.createUserGroup(userGroupDTO, shopId, writer);
      UserGroupRole userGroupRole;
      for (UserGroupRoleDTO userGroupRoleDTO : userGroupRoleDTOList) {
        userGroupRoleDTO.setUserGroupId(userGroupId);
        if (userGroupRoleDTO.getStatus().equals(Status.active)) {
          userGroupRole = new UserGroupRole(userGroupRoleDTO);
          writer.save(userGroupRole);
        } else {
          if (!writer.deleteUserGroupRole(userGroupRoleDTO.getUserGroupId(), userGroupRoleDTO.getRoleId())) {
            return false;
          }
        }
      }
      writer.commit(status);
      //更新 memCache
      ServiceManager.getService(IPrivilegeService.class).deleteResourcesFromCache(userGroupId);
    } finally {
      writer.rollback(status);
    }
    return true;
  }

  @Override
  public List<Long> getRolesByUserId(Long shopId, Long userId) {
    List<Long> ids = new ArrayList<Long>();
    UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUser(shopId, userId);
    List<UserGroupRoleDTO> userGroupRoleDTOList = null;
    UserWriter writer = userDaoManager.getWriter();
    userGroupRoleDTOList = writer.getUserGroupRole(userDTO.getUserGroupId());
    for (UserGroupRoleDTO userGroupRoleDTO : userGroupRoleDTOList) {
      ids.add(userGroupRoleDTO.getRoleId());
    }
    return ids;
  }

  @Override
  public boolean checkRole(RoleDTO roleDTO) {
    return userDaoManager.getWriter().checkRole(roleDTO);
  }

  @Override
  public RoleDTO saveOrUpdateRole(RoleDTO roleDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Role role;
    try {
      if (roleDTO.getId() != null) {
        role = writer.getById(Role.class, roleDTO.getId());
      } else {
        role = new Role();
      }
      role.fromDTO(roleDTO);
      writer.save(role);
      roleDTO.setId(role.getId());
      writer.commit(status);
      return roleDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean checkRoleBeforeDelete(Long roleId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.checkRoleBeforeDelete(roleId, shopId);
  }

  @Override
  public boolean deleteRole(Long roleId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.delete(Role.class, roleId);
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<RoleDTO> getRolesByResourceId(Long resourceId) {
    UserWriter writer = userDaoManager.getWriter();
    List<RoleResource> roleResourceList = writer.getRolesByResourceId(resourceId);
    List<RoleDTO> roleDTOList = new ArrayList<RoleDTO>();
    IModuleService moduleService =ServiceManager.getService(IModuleService.class);
    if (CollectionUtils.isNotEmpty(roleResourceList)) {
      for (RoleResource roleResource : roleResourceList) {
        Role role = writer.getById(Role.class, roleResource.getRoleId());
        if (role != null) {
          RoleDTO r = role.toDTO();
          r.setMemo(moduleService.getChainModuleNamesByRoleId(r.getId()));
          roleDTOList.add(r);
        }
      }
    }
    return roleDTOList;
  }
}
