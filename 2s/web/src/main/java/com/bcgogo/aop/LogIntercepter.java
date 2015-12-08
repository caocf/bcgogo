package com.bcgogo.aop;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.utils.ArrayUtil;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.support.BindingAwareModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志拦截器
 * Author: ndong
 * Date: 2014-12-26
 * Time: 16:12
 */
@Aspect
@Component
public class LogIntercepter {
  private Logger LOG = LoggerFactory.getLogger(LogIntercepter.class);

  @Pointcut("execution(* com.bcgogo.*.*.*(..))")
  private void anyMethod() {
  }

  @Around("anyMethod()")
  public Object aroundAnyMethod(ProceedingJoinPoint pjp) {
    try {
      return pjp.proceed();
    } catch (Throwable e) {
      printMethodArgs(pjp, e);
      return null;
    }
  }

  /**
   * 打印报错参数
   *
   * @param pjp
   * @return
   */
  private void printMethodArgs(ProceedingJoinPoint pjp, Throwable e) {
    Object[] args = pjp.getArgs();
    if (ArrayUtil.isEmpty(args)) {
      LOG.error(e.getMessage(),e);
      return;
    }
    List params = new ArrayList();
    for (Object obj : args) {
      if (obj instanceof HttpServletResponse || obj instanceof BindingAwareModelMap) {
        continue;
      }
      if (obj instanceof HttpServletRequest) {
        HttpServletRequest request = (HttpServletRequest) obj;
        params.add("shopId=" + WebUtil.getShopId(request) + ",userId=" + WebUtil.getUserId(request));
        continue;
      }
      params.add(obj);
    }
    LOG.error(e.getMessage() + ",request param is " + ArrayUtil.toString(params.toArray()), e);
  }

}
