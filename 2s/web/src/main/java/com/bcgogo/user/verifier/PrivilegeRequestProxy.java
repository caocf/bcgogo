package com.bcgogo.user.verifier;

import com.bcgogo.common.PermissionUtils;
import com.bcgogo.common.WebUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.utils.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-6-13
 * Time: 下午3:18
 */
public class PrivilegeRequestProxy {
  public static boolean verifierUserGroupResourceProxy(HttpServletRequest request, String resourceValue) {
    Long userGroupId = WebUtil.getUserGroupId(request);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    String groupKey = PermissionUtils.getRequestResourceSetKey(request);
    Set<String> resourceSet = (Set<String>) request.getAttribute(groupKey);
    if (CollectionUtil.isEmpty(resourceSet)) {
      resourceSet = privilegeService.getEncryptResourceValueSet(shopVersionId, userGroupId);
      request.setAttribute(groupKey, resourceSet);
    }
    return CollectionUtils.isNotEmpty(resourceSet) && resourceSet.contains(com.bcgogo.utils.PermissionUtils.getEncryptStr(resourceValue));
  }

  public static boolean verifierBaseRoleResourceProxy(HttpServletRequest request, String resourceValue) {
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    String key = PermissionUtils.getBaseResourceSetKey(request);
    Set<String> resourceSet = (Set<String>) request.getAttribute(key);
    if (CollectionUtil.isEmpty(resourceSet)) {
      resourceSet = privilegeService.getEncryptBaseResourceNameSet();
      request.setAttribute(key, resourceSet);
    }
    return CollectionUtils.isNotEmpty(resourceSet) && resourceSet.contains(com.bcgogo.utils.PermissionUtils.getEncryptStr(resourceValue));
  }

  //
  public static boolean verifierShopVersionResourceProxy(HttpServletRequest request, String resourceValue) {
    Long shopVersionId = WebUtil.getShopVersionId(request);
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    String key = PermissionUtils.getRequestResourceSetKeyByShopVersionId(request);
    Set<String> resourceSet = (Set<String>) request.getAttribute(key);
    if (CollectionUtil.isEmpty(resourceSet)) {
      resourceSet = privilegeService.getEncryptResourceValueSet(shopVersionId);
      request.setAttribute(key, resourceSet);
    }
    return CollectionUtils.isNotEmpty(resourceSet) && resourceSet.contains(com.bcgogo.utils.PermissionUtils.getEncryptStr(resourceValue));
  }
}
