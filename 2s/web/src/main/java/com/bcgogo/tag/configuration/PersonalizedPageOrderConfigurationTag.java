package com.bcgogo.tag.configuration;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.service.customizerconfig.IPageCustomizerConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * User: ZhangJuntao
 * Date: 13-5-31
 * Time: 上午11:40
 * Order 页面个性化设置
 */
public class PersonalizedPageOrderConfigurationTag extends BodyTagSupport {
  private static final Logger LOG = LoggerFactory.getLogger(PersonalizedPageOrderConfigurationTag.class);
  private static final String HAS_NONE_OF_THE_ORDER_GROUP = "_has_none_of_the_order_group";
  private String orderNameAndResource;               // the value of the 'test' attribute
  private String orderGroupName;

  public int doStartTag() throws JspException {
    ServletRequest servletRequest = pageContext.getRequest();
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    IPageCustomizerConfigService customizerConfigService = ServiceManager.getService(IPageCustomizerConfigService.class);
    String[] orderNameAndResourceArray = orderNameAndResource.split(";");
    Long shopVersionId = WebUtil.getShopVersionId(request),
        shopId = WebUtil.getShopId(request),
        userGroupId = WebUtil.getUserGroupId(request);
    Boolean hasNoneOfTheOrderGroup = true;
    String[] array;
    String orderName, resource;
    if (StringUtils.isBlank(orderGroupName)) {
      LOG.error("orderGroupName is empty!");
      return EVAL_BODY_INCLUDE;
    }
    for (String orderNameAndResource : orderNameAndResourceArray) {
      try {
        orderNameAndResource = orderNameAndResource.replace("\n", "").replace("\r", "").trim();
        array = orderNameAndResource.replace("[", "").replace("]", "").split(",");
        orderName = array[0];
        if (array.length > 1) {
          resource = array[1];
        } else {
          resource = "";
        }
        //首先从request 中取值
        Boolean hasOrderCondition = (Boolean) request.getAttribute(getKey(orderName));
        //然后从cache中取值
        if (hasOrderCondition == null) {
          if (StringUtils.isBlank(orderName)) {
            throw new Exception("orderName is empty!");
          }
          hasOrderCondition = (StringUtils.isEmpty(resource) ||
              PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, resource) ||
              PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, resource)) &&
              customizerConfigService.verifierPageOrderConfigByName(shopId, orderGroupName, orderName);
          if (hasOrderCondition) {
            hasNoneOfTheOrderGroup = false;
          }
        }
        request.setAttribute(getKey(orderName), hasOrderCondition);
      } catch (Exception e) {
        LOG.error("个性化配置异常：", e);
      }
    }
    request.setAttribute(orderGroupName + HAS_NONE_OF_THE_ORDER_GROUP, hasNoneOfTheOrderGroup);
    return EVAL_BODY_INCLUDE;
  }

  private String getKey(String orderName) {
    return orderGroupName + "_" + orderName;
  }

  public String getOrderNameAndResource() {
    return orderNameAndResource;
  }

  public void setOrderNameAndResource(String orderNameAndResource) {
    this.orderNameAndResource = orderNameAndResource;
  }

  public String getOrderGroupName() {
    return orderGroupName;
  }

  public void setOrderGroupName(String orderGroupName) {
    this.orderGroupName = orderGroupName;
  }
}
