package com.bcgogo;

import com.bcgogo.service.RequestContext;


public interface ApiRequestContext extends RequestContext {

  boolean verifyToken();

  boolean useReader();

  Long getUserId();

 public Long getShopId();

}