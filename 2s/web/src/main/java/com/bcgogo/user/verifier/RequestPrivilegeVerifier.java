package com.bcgogo.user.verifier;

import com.bcgogo.common.CommonUtil;
import com.bcgogo.common.PermissionUtils;
import com.bcgogo.common.WebUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * User: ZhangJuntao
 * Date: 12-6-14
 * Time: 下午5:50
 * URL请求资源判断
 */
@Component
public class RequestPrivilegeVerifier {
  private static final Logger LOG = LoggerFactory.getLogger(RequestPrivilegeVerifier.class);

  public static boolean verifier(HttpServletRequest request) {
    if(1==1){
      return true;
    }
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    String resourceValue = PermissionUtils.getResourceUrlValue(request);
//    if(CommonUtil.isDevMode()){
//      return true;
//    }
    if (StringUtils.isBlank(resourceValue)) {
      LOG.warn("[shopId:{},userId:{}] resourceValue is null! ", shopId, userId);
      return false;
    }
    if (shopId == null || userId == null) {
      LOG.warn("shopId:{} or userId:{} is null! ", shopId, userId);
      return false;
    }
    boolean hasThisPermission = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, resourceValue);
    if (!hasThisPermission) {
      LOG.warn("[shopId:{},userId:{}] doesn't have the request permission of  \"" + resourceValue + "\"", shopId, userId);
    } else {
      LOG.debug("[shopId:{},userId:{}] has the request permission of  \"" + resourceValue + "\"", shopId, userId);
    }
    return hasThisPermission;
  }

  public static boolean verifyBaseRole(HttpServletRequest request) {
    String value = PermissionUtils.getResourceUrlValue(request);
    return StringUtils.isNotBlank(value) && PrivilegeRequestProxy.verifierBaseRoleResourceProxy(request, value);
  }
}
