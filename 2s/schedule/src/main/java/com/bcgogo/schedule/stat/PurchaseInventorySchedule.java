package com.bcgogo.schedule.stat;

import com.bcgogo.product.model.NormalProduct;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.service.INormalProductStatService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StatConstant;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 后台CRM采购分析统计
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-2
 * Time: 下午1:37
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseInventorySchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(PurchaseInventorySchedule .class);
  private static boolean LOCK = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (LOCK) {
      return;
    } else {
      LOCK = true;
      try {
        LOG.info("后台开始采购统计" + DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));

        INormalProductStatService normalProductStatService = ServiceManager.getService(INormalProductStatService.class);
        IProductService productService = ServiceManager.getService(IProductService.class);
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        txnService.deleteAllNormalProductStat();

        List<NormalProduct> normalProductList = productService.getAllNormalProducts();
        if (CollectionUtils.isEmpty(normalProductList)) {
          LOCK = false;
          return;
        }
        for (NormalProduct normalProduct : normalProductList) {
          try {
            normalProductStatService.countStatDateByNormalProductId(normalProduct.getId(), StatConstant.EMPTY_SHOP_ID);
          } catch (Exception e) {
            LOG.error("PurchaseInventorySchedule.java,normalProductId:" + normalProduct.getId());
            LOG.error(e.getMessage(), e);
            continue;
          }
        }
      } catch (Exception e) {
        LOG.error("PurchaseInventorySchedule.java");
        LOG.error(e.getMessage(), e);
      } finally {
        LOG.info("后台结束采购统计" + DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));
        LOCK = false;
      }
    }
  }
}
