package com.bcgogo.security;

import com.bcgogo.web.filter.MDCInsertingServletFilter;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/7/11
 * Time: 1:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class BCWebAuthenticationDetails  extends WebAuthenticationDetails  {

  private static final Logger LOG = LoggerFactory.getLogger(BCWebAuthenticationDetails.class);



  private Long userId;
  private Long shopId;
  private String requestid;


  public BCWebAuthenticationDetails(HttpServletRequest pRequest) {
    super(pRequest);
  }

  @Override
  protected void doPopulateAdditionalInformation(HttpServletRequest pRequest) {

    if (requestid == null) {
       requestid = MDC.get(MDCInsertingServletFilter.REQUEST_ID);
      if (requestid == null) {
        requestid = (System.currentTimeMillis() + RandomUtils.nextInt()) + "";
      }
    }
  }
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}

