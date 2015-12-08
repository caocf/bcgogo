package com.bcgogo.utils;

import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.user.RoleType;
import liquibase.util.MD5Util;

/**
 * User: ZhangJuntao
 * Date: 13-6-13
 * Time: 下午3:25
 */
public class PermissionUtils {
  public static String getUserGroupMemcacheKey(Long userGroupId) {
    if (userGroupId == null) return "";
    return MemcachePrefix.userGroupResources.getValue() + "_" + String.valueOf(userGroupId);
  }

  public static String getUserGroupResourceNameMemcacheKey(Long userGroupId) {
    if (userGroupId == null) return "";
    return MemcachePrefix.userGroupResources.getValue() + "_name_" + String.valueOf(userGroupId);
  }

  public static String getCrmUserGroupMemcacheKey(Long userGroupId) {
    if (userGroupId == null) return "";
    return MemcachePrefix.userGroupResources.getValue() + "_crm_" + String.valueOf(userGroupId);
  }

  public static String getShopVersionMemCacheKey(Long shopVersionId) {
    if (shopVersionId == null) return "";
    return MemcachePrefix.shopResource.getValue() + "_" + String.valueOf(shopVersionId);
  }

  public static String getBaseRoleMemCacheKey() {
    return MemcachePrefix.roleResource.getValue() + RoleType.BASE;
  }

  public static String getLoginBaseRoleMemCacheKey() {
    return MemcachePrefix.roleResource.getValue() + RoleType.LOGIN_BASE;
  }

  public static String getEncryptStr(String value) {
    return MD5Util.computeMD5(value);
  }

}
