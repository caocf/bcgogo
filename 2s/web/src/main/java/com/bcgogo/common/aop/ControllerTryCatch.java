package com.bcgogo.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 拦截所有含@RequestMapping注解的方法, 用try{}catch包围, 并log含有@LogParam的参数
 * User: Jimuchen
 * Date: 12-7-18
 * Time: 下午4:57
 */
@Aspect
@Component
public class ControllerTryCatch {
  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerTryCatch.class);

//  @Around("@annotation(requestMapping)")
//  public Object aroundControllerMethod(ProceedingJoinPoint pjp, RequestMapping requestMapping) throws Throwable{
//    try{
//      Object returnVal = pjp.proceed();
//      return returnVal;
//    } catch (Throwable e){
//      logBasicInfo(pjp, requestMapping);
//      LOGGER.error(e.getMessage(), e);
////      throw e;
//    }
//    return null;
//  }

  /**
   * Log出错时执行的URL和shopId, userId等基本信息
   * @param pjp
   * @param requestMapping
   */
  private void logBasicInfo(ProceedingJoinPoint pjp, RequestMapping requestMapping) {
    LOGGER.error("执行方法: {}.{} 时出错.", pjp.getSignature().getDeclaringType().getName(), pjp.getSignature().getName() );
    LOGGER.error("URL: {}?{}", getBaseUrl(pjp), getMethodUrl(requestMapping));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
  }

  private String getBaseUrl(ProceedingJoinPoint pjp){
    RequestMapping classRm = (RequestMapping) pjp.getSignature().getDeclaringType().getAnnotation(RequestMapping.class);
    if(classRm != null && classRm.value()!=null){
      return classRm.value().length>0 ? classRm.value()[0]:"";
    }
    return "";
  }

  private String getMethodUrl(RequestMapping requestMapping){
    if(requestMapping!=null && requestMapping.params()!=null){
      return requestMapping.params().length>0 ? requestMapping.params()[0] : "";
    }
    return "";
  }

}
