package com.bcgogo.api.aop;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-10-30
 * Time: 下午5:25
 * To change this template use File | Settings | File Templates.
 */
@Aspect
@Component
public class LogPutController {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogPutController.class);

  @Around("@annotation(requestMapping)")
  public Object aroundControllerMethod(ProceedingJoinPoint pjp, RequestMapping requestMapping) throws Throwable{
    logPutParams(pjp, requestMapping);
    Object returnVal = pjp.proceed();
    return returnVal;
  }

  private void logPutParams(ProceedingJoinPoint pjp, RequestMapping requestMapping){
    if(requestMapping.method() == null || requestMapping.method().length == 0){
      return;
    }
    RequestMethod requestMethod = requestMapping.method()[0];
    if(requestMethod != RequestMethod.PUT){
      return;
    }
    String name = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
    LOGGER.info("invoking method {}", name);
    Object[] args = pjp.getArgs();
    if(ArrayUtils.isEmpty(args)){
      return;
    }
    List params = new ArrayList();
    for(Object obj : args){
      if(obj instanceof HttpServletRequest || obj instanceof HttpServletResponse){
        continue;
      }
      params.add(obj);
    }
    LOGGER.info("params:{}", ArrayUtils.toString(params.toArray()));
  }
}
