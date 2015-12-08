package com.bcgogo.user.service.permission;

import com.bcgogo.common.AllListResult;
import com.bcgogo.common.Result;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.RoleType;
import com.bcgogo.user.dto.permission.MenuDTO;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.dto.permission.ResourceSearchCondition;
import com.bcgogo.user.dto.permission.RoleResourceDTO;
import com.bcgogo.user.model.permission.Resource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:18
 */
public interface IResourceService {
  public ResourceDTO getResource(String name, String type);

  public ResourceDTO getResource(String value);

  public ResourceDTO getResourceById(Long id);

//  public boolean setResource(ResourceDTO resourceDTO);
  public Result setResource(ResourceDTO resourceDTO);

  public boolean setMenu(MenuDTO menuDTO);

  List<ResourceDTO> getResourceByRoleId(Long roleId);

  RoleResourceDTO setRoleResource(long roleId, long resourceId);

  //复制role resource
  void copyRoleResource(Long desRoleId, Long origRoleId);

  RoleResourceDTO getRoleResourceByRoleIdAndResourceId(Long roleId, Long resourceId);

  List<Resource> getAllResourcesByUserGroupId(Long shopVersionId, Long userGroupId) throws Exception;

  List<Resource> getResourceByShopVersionId(Long shopVersionId) throws Exception;

  //delete RoleResource from db by roleId resourceId
  boolean deleteRoleResource(Long roleId, Long resourceId);

  //根据条件过滤相应的资源
  AllListResult<MenuDTO> getResourcesByCondition(ResourceSearchCondition condition);

  //delete Resource from db by resourceId
  boolean deleteResource(Long resourceId, Long shopId);

  //check roleResource
  boolean checkResourceBeforeDelete(Long resourceId, Long shopId);

  List<Resource> getResourceByRoleType(RoleType roleType);

  //把resource 保存在一个module的所有Role中
  void saveResourceToModule(Long resourceId, Long moduleId, SystemType systemType);

  List<MenuDTO> getMenuByResourceIds(Set<Long> resourceIds);
}
