package com.bcgogo.user.service.permission;

import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.user.dto.permission.ResourceDTO;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-14
 * Time: 下午3:36
 * 判断是否含有资源
 */
public interface IPrivilegeService {

  /**
   * 尽量使用 PrivilegeRequestProxy verifierUserGroupResourceProxy
   * 判断WEB用户组是否含有该资源
   *
   * @param shopVersionId 版本Id
   * @param userGroupId   用户组Id
   * @param resourceType  资源类型
   * @param resourceValue 资源值
   * @return boolean true has the permission
   */
  boolean verifierUserGroupResource(Long shopVersionId, Long userGroupId, ResourceType resourceType, String resourceValue);

  /**
   * 判断版本是否含有该资源（版本base中校验）
   *
   * @param shopVersionId 版本Id
   * @param resourceType  资源类型
   * @param resourceValue 资源值
   * @return boolean true has the permission
   */
  boolean verifierShopVersionResource(Long shopVersionId, ResourceType resourceType, String resourceValue);

  //verifier 基本角色资源
  boolean verifierBaseRoleResource(ResourceType resourceType, String resourceValue);

  /**
   * 判断资源 by resourceName
   *
   * @param shopVersionId Long
   * @param userGroupId   Long
   * @param resourceName  String
   */
  boolean verifierResourceByName(Long shopVersionId, Long userGroupId, String resourceName) throws Exception;

  Set<String> getEncryptResourceValueSet(Long shopVersionId);

  Set<String> getEncryptResourceValueSet(Long shopVersionId, Long userGroupId);

  Set<String> getEncryptBaseResourceNameSet();

  /**
   * fresh all user group
   */
  void reSetAllResourcesToMemCache();

  /**
   * 清除memCache
   *
   * @param userGroupId  Long
   */
  void deleteResourcesFromCache(Long userGroupId);

  //获得资源 todo CRM临时
  List<ResourceDTO> getResourceDTOList(Long shopVersionId, Long userGroupId) throws Exception;

}
