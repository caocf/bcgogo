 package com.bcgogo.admin.security;

import com.bcgogo.ApiRequestContext;
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
 */
public class BCWebAuthenticationDetails  extends WebAuthenticationDetails {

  private static final Logger LOG = LoggerFactory.getLogger(BCWebAuthenticationDetails.class);


  private String userId;
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


}

