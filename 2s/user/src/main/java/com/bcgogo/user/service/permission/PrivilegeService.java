package com.bcgogo.user.service.permission;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.enums.user.RoleType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.permission.Resource;
import com.bcgogo.utils.PermissionUtils;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-14
 * Time: 下午3:35
 * 判断是否含有资源
 */
@Component
public class PrivilegeService implements IPrivilegeService {
  private static final Logger LOG = LoggerFactory.getLogger(PrivilegeService.class);
  private static final String PERMISSION_TAG = "on";
  @Autowired
  private UserDaoManager userDaoManager;

  //=====================================================verifier=======================================================
  public boolean verifierUserGroupResource(Long shopVersionId, Long userGroupId, ResourceType resourceType, String resourceValue) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String permissionTag = configService.getConfig("PermissionTag", ShopConstant.BC_SHOP_ID);
    if (StringUtils.isBlank(permissionTag) || !permissionTag.equals(PERMISSION_TAG)) return true;
    if (StringUtils.isBlank(resourceValue)) return false;
    Set<String> resourceSet = getEncryptResourceValueSet(shopVersionId, userGroupId);
    return CollectionUtils.isNotEmpty(resourceSet) && resourceSet.contains(PermissionUtils.getEncryptStr(resourceValue));
  }

  @Override
  public boolean verifierShopVersionResource(Long shopVersionId, ResourceType resourceType, String resourceValue) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String permissionTag = configService.getConfig("PermissionTag", ShopConstant.BC_SHOP_ID);
    if (StringUtils.isBlank(permissionTag) || !permissionTag.equals(PERMISSION_TAG)) return true;
    Set<String> resourceSet = getEncryptResourceValueSet(shopVersionId);
    return CollectionUtils.isNotEmpty(resourceSet) && resourceSet.contains(PermissionUtils.getEncryptStr(resourceValue));
  }

  public boolean verifierBaseRoleResource(ResourceType resourceType, String resourceValue) {
    if (StringUtils.isBlank(resourceValue)) return false;
    Set<String> resourceSet = getEncryptBaseResourceNameSet();
    return CollectionUtils.isNotEmpty(resourceSet) && resourceSet.contains(PermissionUtils.getEncryptStr(resourceValue));
  }

  @Override
  public boolean verifierResourceByName(Long shopVersionId, Long userGroupId, String resourceName) throws Exception {
    Set<String> resourceSet = getResourceNameSet(shopVersionId, userGroupId);
    return !CollectionUtils.isEmpty(resourceSet) && resourceSet.contains(resourceName);
  }

  @Override
  public List<ResourceDTO> getResourceDTOList(Long shopVersionId, Long userGroupId) throws Exception {
    String key = PermissionUtils.getCrmUserGroupMemcacheKey(userGroupId);
    if (StringUtils.isBlank(key)) return null;
    List<ResourceDTO> resourceDTOList = (List<ResourceDTO>) MemCacheAdapter.get(key);
    if (CollectionUtils.isEmpty(resourceDTOList)) {
      try {
        if (StringUtils.isBlank(key)) return new ArrayList<ResourceDTO>();
        //get from db
        List<Resource> resourceList = ServiceManager.getService(IResourceService.class).getAllResourcesByUserGroupId(shopVersionId, userGroupId);
        resourceDTOList = new ArrayList<ResourceDTO>();
        for (Resource resource : resourceList) {
          resourceDTOList.add(resource.toDTO());
        }
        MemCacheAdapter.set(key, resourceDTOList);
      } catch (Exception e) {
        LOG.error("[权限verifier fail]" + e.getMessage(), e);
        return null;
      }
    }
    return resourceDTOList;
  }


  public Set<String> getEncryptResourceValueSet(Long shopVersionId) {
    String key = PermissionUtils.getShopVersionMemCacheKey(shopVersionId);
    if (StringUtils.isBlank(key)) return new HashSet<String>();
    Set<String> resourceSet = (Set<String>) MemCacheAdapter.get(key);
    if (CollectionUtils.isEmpty(resourceSet)) {
      resourceSet = new HashSet<String>();
      try {
        List<Resource> resourceList = ServiceManager.getService(IResourceService.class).getResourceByShopVersionId(shopVersionId);
        if (CollectionUtils.isNotEmpty(resourceList)) {
          for (Resource resource : resourceList) {
            resourceSet.add(PermissionUtils.getEncryptStr(resource.getValue()));
          }
          MemCacheAdapter.set(key, resourceSet);
        }
      } catch (Exception e) {
        LOG.error("[权限verifier fail]" + e.getMessage(), e);
        return resourceSet;
      }
    }
    return resourceSet;
  }

  public Set<String> getEncryptResourceValueSet(Long shopVersionId, Long userGroupId) {
    String key = PermissionUtils.getUserGroupMemcacheKey(userGroupId);
    if (StringUtils.isBlank(key)) return null;
    Set<String> resourceSet = (Set<String>) MemCacheAdapter.get(key);
//    LOG.warn("resource size [{}]k in memcache.", StringUtils.join(resourceSet.toArray(new String[resourceSet.size()]), "").length() / (1024 * 1.0));
    if (CollectionUtils.isEmpty(resourceSet)) {
      try {
        if (StringUtils.isBlank(key)) return new HashSet<String>();
        //get from db
        List<Resource> resourceList = ServiceManager.getService(IResourceService.class).getAllResourcesByUserGroupId(shopVersionId, userGroupId);
        resourceSet = new HashSet<String>();
        for (Resource resource : resourceList) {
          resourceSet.add(PermissionUtils.getEncryptStr(resource.getValue()));
        }
        MemCacheAdapter.set(key, resourceSet);
      } catch (Exception e) {
        LOG.error("[权限verifier fail]" + e.getMessage(), e);
        return null;
      }
    }
    return resourceSet;
  }

  public Set<String> getEncryptBaseResourceNameSet() {
    String key = PermissionUtils.getBaseRoleMemCacheKey();
    if (StringUtils.isBlank(key)) return null;
    Set<String> resourceSet = (Set<String>) MemCacheAdapter.get(key);
    if (CollectionUtils.isEmpty(resourceSet)) {
      try {
        if (StringUtils.isBlank(key)) return new HashSet<String>();
        //get from db
        List<Resource> resourceList =  ServiceManager.getService(IResourceService.class).getResourceByRoleType(RoleType.BASE);
        resourceSet = new HashSet<String>();
        for (Resource resource : resourceList) {
          resourceSet.add(PermissionUtils.getEncryptStr(resource.getValue()));
        }
        MemCacheAdapter.set(key, resourceSet);
      } catch (Exception e) {
        LOG.error("[权限verifier fail]" + e.getMessage(), e);
        return null;
      }
    }
    return resourceSet;
  }

  public Set<String> getResourceNameSet(Long shopVersionId, Long userGroupId) {
    String key = PermissionUtils.getUserGroupResourceNameMemcacheKey(userGroupId);
    if (StringUtils.isBlank(key)) return null;
    Set<String> resourceSet = (Set<String>) MemCacheAdapter.get(key);
    if (CollectionUtils.isEmpty(resourceSet)) {
      try {
        if (StringUtils.isBlank(key)) return new HashSet<String>();
        //get from db
        List<Resource> resourceList = ServiceManager.getService(IResourceService.class).getAllResourcesByUserGroupId(shopVersionId, userGroupId);
        resourceSet = new HashSet<String>();
        for (Resource resource : resourceList) {
          resourceSet.add(resource.getName());
        }
        MemCacheAdapter.set(key, resourceSet);
      } catch (Exception e) {
        LOG.error("[权限verifier fail]" + e.getMessage(), e);
        return null;
      }
    }
    return resourceSet;
  }


  //=======================================================delete=======================================================
  public void reSetAllResourcesToMemCache() {
    deleteUserGroupResourcesFromCache();
    deleteBaseResourcesFromCache();
    deleteLoginBaseResourcesFromCache();
    deleteShopVersionResourcesFromCache();
  }

  @Override
  public void deleteResourcesFromCache(Long userGroupId) {
    MemCacheAdapter.delete(PermissionUtils.getUserGroupMemcacheKey(userGroupId));
    MemCacheAdapter.delete(PermissionUtils.getUserGroupResourceNameMemcacheKey(userGroupId));
    MemCacheAdapter.delete(PermissionUtils.getCrmUserGroupMemcacheKey(userGroupId));
  }

  private boolean deleteUserGroupResourcesFromCache() {
    List<Long> userGroupIds = ServiceManager.getService(IUserGroupService.class).getAllUserGroupIds();
    for (Long userGroupId : userGroupIds) {
      deleteResourcesFromCache(userGroupId);
    }
    return true;
  }

  private boolean deleteBaseResourcesFromCache() {
    return MemCacheAdapter.delete(PermissionUtils.getBaseRoleMemCacheKey());
  }

  private boolean deleteLoginBaseResourcesFromCache() {
    return MemCacheAdapter.delete(PermissionUtils.getLoginBaseRoleMemCacheKey());
  }

  private void deleteShopVersionResourcesFromCache() {
    List<ShopVersionDTO> shopVersionDTOList = ServiceManager.getService(IShopVersionService.class).getAllShopVersion();
    for (ShopVersionDTO shopVersionDTO : shopVersionDTOList) {
      MemCacheAdapter.delete(PermissionUtils.getShopVersionMemCacheKey(shopVersionDTO.getId()));
    }
  }

}
