package com.bcgogo.admin.security;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/7/11
 * Time: 1:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class BCARoleVoter implements AccessDecisionVoter {

  @Override
   public boolean supports(ConfigAttribute attr) {
     return attr.getAttribute() != null && attr.getAttribute().equals("CHECK");
   }


   @Override
   public boolean supports(Class<?> clazz) {
     return true;
   }


  @Override
   public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
     int result = ACCESS_ABSTAIN;
     if (!(object instanceof FilterInvocation)) {
       return result;
     }
     FilterInvocation f = (FilterInvocation) object;

     return ACCESS_GRANTED;
   }

}
