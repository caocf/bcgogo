package com.bcgogo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-6-4
 * Time: 下午6:53
 */
@Aspect
@Component
public class ServiceTimeLog {
  private Logger LOG = LoggerFactory.getLogger(ServiceTimeLog.class);

  @Pointcut("execution(* com.bcgogo.txn.service.RepairService.*(..)) || execution(* com.bcgogo.txn.service.TxnService.*(..)) " +
      "|| execution(* com.bcgogo.txn.RepairController.*(..)) || execution(* com.bcgogo.txn.service.RFTxnService.*(..))" +
      " || execution(* com.bcgogo.user.service.CustomerService.*(..)) || execution(* com.bcgogo.user.service.UserService.*(..))" +
      " || execution(* com.bcgogo.txn.service.StoreHouseService.*(..)) || execution(* com.bcgogo.txn.service.InventoryService.*(..))" +
      " || execution(* com.bcgogo.product.service.ProductService.*(..)) || execution(* com.bcgogo.stat.service.ServiceVehicleCountService.*(..)) " +
      " || execution(* com.bcgogo.txn.service.productThrough.ProductThroughService.*(..))" +
      " || execution(* com.bcgogo.txn.service.productThrough.ProductOutStorageService.*(..))" +
      " || execution(* com.bcgogo.txn.service.productThrough.ProductInStorageService.*(..)) " +
      " || execution(* com.bcgogo.txn.service.ProductHistoryService.*(..)) " +
      " || execution(* com.bcgogo.txn.GoodSaleController.*(..)) " +
      " || execution(* com.bcgogo.txn.service.ServiceHistoryService.*(..))")
//  @Pointcut("execution(* com.bcgogo.txn.service.RepairService.*(..)) || execution(* com.bcgogo.txn.service.TxnService.*(..)) " +
//       "|| execution(* com.bcgogo.txn.RepairController.*(..))")
  private void repairOrTxn(){}

  @Around("repairOrTxn()")
  public Object aroundControllerMethod(ProceedingJoinPoint pjp) throws Throwable{
    Long current =System.currentTimeMillis();
    LOG.debug(pjp.getSignature().getDeclaringType().getName()+":"+ pjp.getSignature().getName()+" start");
    Object returnVal = pjp.proceed();
    LOG.debug(pjp.getSignature().getDeclaringType().getName()+":"+ pjp.getSignature().getName() + " end ");
    LOG.debug("AOP_END:用时:{} ms",System.currentTimeMillis()-current);
    return returnVal;
  }

}
