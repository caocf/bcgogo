package com.bcgogo.schedule.bean.recommend;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ShopBusinessScope;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.model.ProductCategory;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.recommend.ShopRecommendDTO;
import com.bcgogo.txn.service.recommend.IRecommendService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ShopRecommendSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(ShopRecommendSchedule.class);
  private static boolean lock = false;

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processShopRecommendJobs();
  }

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }


  public void processShopRecommendJobs() {
    if (isLock()) {
      LOG.warn("ShopRecommendSchedule isLock!");
      return;
    }
    try {
      if (LOG.isDebugEnabled()) LOG.debug("ShopRecommendSchedule start.............");
      long currentTime = System.currentTimeMillis();
      IRecommendService recommendService = ServiceManager.getService(IRecommendService.class);
      recommendService.processShopRecommend();
      if (LOG.isDebugEnabled()) LOG.debug((System.currentTimeMillis()-currentTime)+",ShopRecommendSchedule end.............");
    } catch (Exception e) {
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      debugResourceLeak();
      lock = false;
    }
  }

}
