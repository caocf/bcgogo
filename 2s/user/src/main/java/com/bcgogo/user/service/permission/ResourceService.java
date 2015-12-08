package com.bcgogo.user.service.permission;

import com.bcgogo.common.AllListResult;
import com.bcgogo.common.Result;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.ResourceSuffix;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.enums.user.RoleType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.*;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.Menu;
import com.bcgogo.user.model.permission.Resource;
import com.bcgogo.user.model.permission.RoleResource;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-12
 * Time: 下午9:18
 * 权限资源读写操作服务类
 */
@Component
public class ResourceService implements IResourceService {
  private static final Logger LOG = LoggerFactory.getLogger(ResourceService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public ResourceDTO getResource(String name, String type) {
    UserWriter writer = userDaoManager.getWriter();
    Resource resource = writer.getResource(name, type);
    if (resource != null) {
      return resource.toDTO();
    }
    return null;
  }

  @Override
  public ResourceDTO getResource(String value) {
    UserWriter writer = userDaoManager.getWriter();
    Resource resource = writer.getResourceByValue(value);
    if (resource != null) {
      return resource.toDTO();
    }
    return null;
  }

  @Override
  public ResourceDTO getResourceById(Long resourceId) {
    UserWriter writer = userDaoManager.getWriter();
    Resource resource = writer.getById(Resource.class, resourceId);
    if (resource != null) {
      return resource.toDTO();
    }
    return null;
  }

//  @Override
//  public boolean setResource(ResourceDTO resourceDTO) {
//    UserWriter writer = userDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      Resource resource;
//      if (resourceDTO.getResourceId() != null) resource = writer.getById(Resource.class, resourceDTO.getResourceId());
//      else resource = new Resource();
//      resource.fromDTO(resourceDTO);
//      writer.saveOrUpdate(resource);
//      resourceDTO.setResourceId(resource.getId());
//      writer.commit(status);
//      return true;
//    } finally {
//      writer.rollback(status);
//    }
//  }

  @Override
  public Result setResource(ResourceDTO resourceDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Resource resource;
      if (writer.isResourceValueDuplicated(resourceDTO.getValue(), resourceDTO.getResourceId())) {
        return new Result(ResourceDTO.VALUE_DUPLICATED, false);
      }
      if (writer.isResourceNameDuplicated(resourceDTO.getName(), resourceDTO.getResourceId())) {
        return new Result(ResourceDTO.NAME_DUPLICATED, false);
      }
      if (resourceDTO.getResourceId() != null) {
        resource = writer.getById(Resource.class, resourceDTO.getResourceId());
      } else {
        resource = new Resource();
      }
      resource.fromDTO(resourceDTO);
      writer.saveOrUpdate(resource);
      resourceDTO.setResourceId(resource.getId());
      writer.commit(status);
      return new Result("操作成功！",true);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public boolean setMenu(MenuDTO menuDTO) {
    ResourceDTO resourceDTO = menuDTO.toResourceDTO();
    resourceDTO.setStatus("active");
    this.setResource(resourceDTO);
    if (ResourceType.menu == resourceDTO.getType()) {
      menuDTO.setResourceId(resourceDTO.getResourceId());
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        Menu menu;
        if (menuDTO.getMenuId() != null) menu = writer.getById(Menu.class, menuDTO.getMenuId());
        else menu = new Menu();
        menu.fromDTO(menuDTO);
        writer.saveOrUpdate(menu);
        menuDTO.setMenuId(menu.getId());
        writer.commit(status);

      } finally {
        writer.rollback(status);
      }
    }
    return true;
  }

  @Override
  public List<ResourceDTO> getResourceByRoleId(Long roleId) {
    UserWriter writer = userDaoManager.getWriter();
    List<RoleResource> roleResourceList = writer.getRoleResourceByRoleId(roleId);
    List<ResourceDTO> resourceDTOList = new ArrayList<ResourceDTO>();
    if (CollectionUtils.isNotEmpty(roleResourceList)) {
      for (RoleResource roleResource : roleResourceList) {
        Resource resource = writer.getById(Resource.class, roleResource.getResourceId());
        if (resource != null) {
          resourceDTOList.add(resource.toDTO());
        }
      }
    }
    return resourceDTOList;
  }

  @Override
  public RoleResourceDTO setRoleResource(long roleId, long resourceId) {
    UserWriter writer = userDaoManager.getWriter();
    RoleResourceDTO roleResourceDTO = writer.getRoleResourceByRoleIdAndResourceId(roleId, resourceId);
    if (roleResourceDTO != null) return roleResourceDTO;
    Object status = writer.begin();
    try {
      RoleResource roleResource = new RoleResource(roleId, resourceId);
      writer.save(roleResource);
      writer.commit(status);
      return roleResource.toDTO();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void copyRoleResource(Long desRoleId, Long origRoleId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Resource> desResourceList = writer.getResourceByRoleId(desRoleId);
    List<Resource> origResourceList = writer.getResourceByRoleId(origRoleId);
    StringBuilder desRoleIds = new StringBuilder();
    for (Resource r : desResourceList) {
      if (r != null)
        desRoleIds.append(r.getId()).append(",");
    }
    String roleIds = desRoleIds.toString();
    Object status = writer.begin();
    try {
      for (Resource r : origResourceList) {
        if (r == null) continue;
        if (roleIds.contains(r.getId().toString())) continue;
        RoleResource roleResource = new RoleResource(desRoleId, r.getId());
        writer.save(roleResource);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public RoleResourceDTO getRoleResourceByRoleIdAndResourceId(Long roleId, Long resourceId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getRoleResourceByRoleIdAndResourceId(roleId, resourceId);
  }

  @Override
  public boolean deleteRoleResource(Long roleId, Long resourceId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      boolean isSuccess = writer.deleteRoleResource(roleId, resourceId);
      writer.commit(status);
      return isSuccess;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public AllListResult<MenuDTO> getResourcesByCondition(ResourceSearchCondition condition) {
    AllListResult<MenuDTO> result = new AllListResult<MenuDTO>();
    UserWriter writer = userDaoManager.getWriter();
    result.setResults(writer.getResourcesByCondition(condition));
    result.setTotalRows(writer.countResourcesByCondition(condition));
    return result;
  }

  @Override
  public boolean deleteResource(Long resourceId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Resource resource = writer.getById(Resource.class, resourceId);
      writer.delete(resource);
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean checkResourceBeforeDelete(Long resourceId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.checkResourceBeforeDelete(resourceId, shopId);
  }

  @Override
  public List<Resource> getAllResourcesByUserGroupId(Long shopVersionId, Long userGroupId) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    List<Resource> resourceList = writer.getResourceByUserGroupId(userGroupId);
    // + 基本权限
    resourceList.addAll(writer.getResourceByRoleType(RoleType.LOGIN_BASE));
    // + 每个版本的特有权限
    if (shopVersionId != null) {
      ShopVersionDTO shopVersionDTO = ServiceManager.getService(IShopVersionService.class).getShopVersionById(shopVersionId);
      if (shopVersionDTO == null) throw new Exception("get shopVersion by Id[" + shopVersionId + "] is null");
      RoleType baseRole = RoleType.shopVersionBaseRoleMapping(shopVersionDTO.getId());
      if (baseRole != null) {
        List<Resource> baseResourceList = writer.getResourceByRoleType(baseRole);
        if (CollectionUtils.isNotEmpty(baseResourceList)) resourceList.addAll(baseResourceList);
      }
    }
    return resourceList;
  }

  // + 每个版本的特有权限
  @Override
  public List<Resource> getResourceByShopVersionId(Long shopVersionId) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    if (shopVersionId != null) {
      ShopVersionDTO shopVersionDTO = ServiceManager.getService(IShopVersionService.class).getShopVersionById(shopVersionId);
      if (shopVersionDTO == null) throw new Exception("get shopVersion by Id[" + shopVersionId + "] is null");
      if (!shopVersionDTO.getName().contains("BCGOGO")) {
        return writer.getResourceByRoleType(RoleType.valueOf(shopVersionDTO.getName() + ResourceSuffix.SHOP_VERSION_BASE.getValue()));
      }
    }
    return new ArrayList<Resource>();
  }

  @Override
  public List<Resource> getResourceByRoleType(RoleType roleType) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getResourceByRoleType(roleType);
  }

  @Override
  public void saveResourceToModule(Long resourceId, Long moduleId, SystemType systemType) {
    List<RoleDTO> roleDTOList = ServiceManager.getService(IModuleService.class).getRolesByModuleId(moduleId, systemType);
    RoleResource roleResource;
    UserWriter writer = userDaoManager.getWriter();
    List<RoleResource> roleResourceList = writer.getRolesByResourceId(resourceId);
    StringBuilder roleIds = new StringBuilder();
    for (RoleResource rr : roleResourceList) {
      roleIds.append(rr.getRoleId()).append(",");
    }
    String ids = roleIds.toString();
    Object status = writer.begin();
    try {
      for (RoleDTO roleDTO : roleDTOList) {
        if (ids.contains(roleDTO.getId().toString())) continue;
        roleResource = new RoleResource(roleDTO.getId(), resourceId);
        writer.save(roleResource);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<MenuDTO> getMenuByResourceIds(Set<Long> resourceIds) {
    UserWriter writer = userDaoManager.getWriter();
    List<MenuDTO> menuDTOList = new ArrayList<MenuDTO>();
    MenuDTO menuDTO;
    for (Object[] o : writer.getMenuByResourceIds(resourceIds)) {
      menuDTO = ((Menu) o[0]).toDTO();
      menuDTO.fromResourceDTO(((Resource) o[1]).toDTO());
      menuDTOList.add(menuDTO);
    }
    return menuDTOList;
  }
}
