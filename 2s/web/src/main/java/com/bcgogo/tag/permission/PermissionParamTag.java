package com.bcgogo.tag.permission;

import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-14
 * Time: 下午2:33
 */
public class PermissionParamTag extends BodyTagSupport {
  private static final Logger LOG = LoggerFactory.getLogger(PermissionParamTag.class);
  private static final String PERMISSION_COUNTS = "PermissionCounts";

  public PermissionParamTag() {
    super();
    init();
  }

  private void init() {
    permissions = null;                          // reset flag
    resourceType = null;
    permissionKey = null;
  }


  private String permissions;               // the value of the 'test' attribute
  private String resourceType;
  private String permissionKey;           //key of this tag major key

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public String getPermissions() {
    return permissions;
  }

  public void setPermissions(String permissions) {
    this.permissions = permissions;
  }

  public String getPermissionKey() {
    return permissionKey;
  }

  public void setPermissionKey(String permissionKey) {
    this.permissionKey = permissionKey;
  }

  private String toKey(String str) throws Exception {
    if (StringUtil.isEmpty(str)) throw new Exception("key is empty!");
    if (StringUtils.isNotBlank(resourceType) && resourceType.equals(ResourceType.request.name())) {
      return StringUtil.subUpString(str.replace("/", "_").replaceAll("\\.do\\?method=", "_"));
    } else {
      return str.replace(".", "_");
    }
  }

  public int doStartTag() throws JspException {
    ServletRequest servletRequest = pageContext.getRequest();
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    permissions = permissions.replace(" ", "");
    String[] permissionArray = permissions.split(",");
    int i = 1, permissionCounts = 0;
    String key;
    for (String permission : permissionArray) {
      try {
        permission = permission.replace("\n", "").replace("\r", "").trim();
        key = toKey(permission);
        //首先从request 中取值
        Boolean hasPermission = (Boolean) request.getAttribute(key);
        //然后从cache中取值
        if (hasPermission == null) {
          IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
          if (StringUtils.isNotBlank(resourceType) && resourceType.equals(ResourceType.logic.name())) {
            hasPermission = PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, permission);
          } else {
//            hasPermission = privilegeService.verifierUserGroupResource(WebUtil.getShopVersionId(request), WebUtil.getUserGroupId(request), StringUtil.isEmpty(resourceType) ? ResourceType.render : ResourceType.valueOf(resourceType), permission);
            hasPermission = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, permission);
          }
        }
        //todo delete
        request.setAttribute("permissionParam" + i++, hasPermission);

        if (hasPermission) permissionCounts++;
        request.setAttribute(key, hasPermission);
      } catch (Exception e) {
        LOG.error("权限校验异常：", e);
      }
    }
    if (StringUtil.isNotEmpty(this.permissionKey)) {
      //可以针对page context
      request.setAttribute(this.permissionKey + PERMISSION_COUNTS, permissionCounts);
    }
    return EVAL_BODY_INCLUDE;
  }
}
