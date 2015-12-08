package com.bcgogo.tag.permission;

import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-14
 * Time: 下午1:58
 */
public abstract class AbstractPermissionTag extends ConditionalTagSupport {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractPermissionTag.class);
  private String permissions;               // the value of the 'test' attribute
  private String resourceType;

  public String getPermissions() {
    return permissions;
  }

  public void setPermissions(String permissions) {
    this.permissions = permissions;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  private String toKey(String str) throws Exception {
    if (StringUtil.isEmpty(str)) throw new Exception("key is empty!");
    if (StringUtils.isNotBlank(resourceType) && resourceType.equals(ResourceType.request.name())) {
      return StringUtil.subUpString(str.replace("/", "_").replaceAll("\\.do\\?method=", "_"));
    } else {
      return str.replace(".", "_");
    }
  }

  protected boolean condition() {
    ServletRequest servletRequest = pageContext.getRequest();
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    if(WebUtil.isSessionEmpty(request)){
      return false;
    }
    boolean andOperation = true;
    String[] permissionArray;
    //或运算
    permissions = permissions.replace(" ", "");
    if (permissions.contains("||")) {
      andOperation = false;
      permissionArray = permissions.split("\\|\\|");
    } else {
      //与运算
      permissionArray = permissions.split("&&");
    }
    try {
      Boolean hasPermission;
      String key;
      ShopVersionDTO shopVersionDTO = WebUtil.getShopVersion(request);
      if (shopVersionDTO == null){
        LOG.error("shopVersion is null ");
        return false;
      }
      for (String str : permissionArray) {
        key = toKey(str);
        //首先从request 中取值
        hasPermission = (Boolean) request.getAttribute(key);
        //然后从cache中取值
        if (hasPermission == null) {
          IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
          if (StringUtils.isNotBlank(resourceType) && resourceType.equals(ResourceType.logic.name())) {
            hasPermission = PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, str);
          } else {
//            hasPermission = privilegeService.verifierUserGroupResource(shopVersionDTO.getId(), WebUtil.getUserGroupId(request), (StringUtils.isEmpty(resourceType) ? ResourceType.render : ResourceType.valueOf(resourceType)), str);
            hasPermission = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, str);
          }
          request.setAttribute(key, hasPermission);
        }
        if (andOperation) {
          if (!hasPermission) return false;
        } else {
          if (hasPermission) return true;
        }

      }
      return andOperation;
    } catch (Exception e) {
      LOG.warn("权限信息：shopId:{},userId:{},userGroupId:{},shopVersion:{},resourceType:{},resource:{}.",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request), resourceType, permissions});
      LOG.error("权限校验异常:", e);
    }
    return false;
  }

}
