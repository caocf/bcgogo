package com.bcgogo.common;

import javax.servlet.http.HttpServletRequest;

/**
 * User: ZhangJuntao
 * Date: 13-6-13
 * Time: 下午3:25
 */
public class PermissionUtils extends com.bcgogo.utils.PermissionUtils {
  public static String getResourceUrlValue(HttpServletRequest request) {
    return request.getRequestURI() + "?method=" + request.getParameter("method");
  }

  public static String getRequestResourceSetKey(HttpServletRequest request) {
    return "PERMISSION_USER_GROUP_ID_" + WebUtil.getUserGroupId(request);
  }

  public static String getRequestResourceSetKeyByShopVersionId(HttpServletRequest request) {
    return "PERMISSION_SHOP_VERSION_ID_" + WebUtil.getShopVersionId(request);
  }
  public static String getBaseResourceSetKey(HttpServletRequest request) {
    return "PERMISSION_SHOP_BASE";
  }
}
