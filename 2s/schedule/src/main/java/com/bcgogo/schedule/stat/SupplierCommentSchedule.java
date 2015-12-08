package com.bcgogo.schedule.stat;

import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.utils.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 后台开始统计供应商点评分数
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-1
 * Time: 下午4:52
 * To change this template use File | Settings | File Templates.
 */
public class SupplierCommentSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(SupplierCommentSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } else {
      lock = true;
      LOG.info("系统开始统计供应商点评数据,开始时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));

      try {

        IConfigService configService = ServiceManager.getService(IConfigService.class);
        ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
        List<Shop> shopList = configService.getShop();


        for (Shop shop : shopList) {
          Long shopVersionId = shop.getShopVersionId();
          if (shopVersionId == null) {
            continue;
          }

//          //如果不是汽配版 跳过
//          if (shopVersionId != 10000010017531653L && shopVersionId != 10000010017531657L && shopVersionId != 10000010037193619L
//              && shopVersionId != 10000010037193620L) {
//            continue;
//          }

          long shopId = shop.getId();
          LOG.info("店铺shop_id:" + shopId + "开始统计供应商点评数据");
          try {
            supplierCommentService.commentStatByShopId(shopId,shopVersionId);
          } catch (Exception e) {
            LOG.error("统计供应商点评数据" + "店铺id:" + shopId + "系统继续统计供应商点评数据");
            LOG.error(e.getMessage(), e);
            continue;
          }
        }


      } catch (Exception e) {
        LOG.error("SupplierCommentSchedule.executeJob");
        LOG.error(e.getMessage(), e);
      }
      lock = false;
      LOG.info("系统结束统计供应商点评数据,结束时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));
    }

  }
}
