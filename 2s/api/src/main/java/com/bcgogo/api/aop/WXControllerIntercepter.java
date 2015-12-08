package com.bcgogo.api.aop;

import com.bcgogo.utils.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
* Created by IntelliJ IDEA.
* Author: ndong
* Date: 2014-12-26
* Time: 17:48
*/
@Aspect
@Component
public class WXControllerIntercepter {
  private Logger LOG = LoggerFactory.getLogger(WXControllerIntercepter.class);

  @Pointcut("execution(* com.bcgogo.api.controller.wx.WXController.handleRequest(..))")
  private void handleRequest() {
  }

  @Around("handleRequest()")
  public Object aroundAnyMethod(ProceedingJoinPoint pjp) {
    try {
      StopWatchUtil sw = new StopWatchUtil("wx:handle request", "start");
      Object result= pjp.proceed();
      sw.stopAndPrintLog();
      return result;
    } catch (Throwable e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }



}
