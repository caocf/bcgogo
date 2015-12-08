package com.bcgogo.schedule.bean;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IApplyPushMessageService;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.solr.IShopSolrWriterService;
import com.bcgogo.user.service.permission.IUserCacheService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-30
 * Time: 下午3:04
 */
public class ShopCheckSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(ShopCheckSchedule.class);
  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return lock;
    }
    lock = false;
    return lock;
  }

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isLock()) {
      return;
    }
    lock = true;
    try {
      processSmsJobs();
    } finally {
      lock = false;
    }
  }

  public void processSmsJobs() {
    try {
      if (LOG.isDebugEnabled()) LOG.debug("ShopCheckSchedule.............");
      IShopService shopService = ServiceManager.getService(IShopService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IApplyPushMessageService applyPushMessageService = ServiceManager.getService(IApplyPushMessageService.class);
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
      //试用期检查
      try {
        LOG.debug("ShopCheckSchedule[试用期检查].............");
        shopService.checkTrialEndTimeShop();
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      //欠款检查
      LOG.debug("ShopCheckSchedule[欠款检查].............");
      try {
        shopService.checkTrialDebtShop();
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      //生成关联匹配消息
      try {
        LOG.debug("ShopCheckSchedule[生成关联匹配消息].............");
        List<ShopDTO> shopDTOList = configService.getActiveShop();
        createAutoMatchApplyMessage(shopDTOList);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      try {
        LOG.debug("ShopCheckSchedule[生成保养里程消息].............");
        appointPushMessageService.createAppVehicleMaintainMileageMessage(1000);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      try {
        LOG.debug("ShopCheckSchedule[生成保养时间消息].............");
        appointPushMessageService.createAppVehicleMaintainTimeMessage(1000);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      try {
        LOG.debug("ShopCheckSchedule[生成保险时间消息].............");
        appointPushMessageService.createAppVehicleInsuranceTimeMessage(1000);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      try {
        LOG.debug("ShopCheckSchedule[生成验车时间消息].............");
        appointPushMessageService.createAppVehicleExamineTimeMessage(1000);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      List<ShopDTO> shopDTOList = configService.getActiveShop();
      for (ShopDTO shopDTO : shopDTOList) {
        List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopDTO.getId());
        if(CollectionUtils.isNotEmpty(userIds)){
          for(Long userId : userIds){
            pushMessageService.updatePushMessageCategoryStatNumberInMemCache(shopDTO.getId(), userId, PushMessageCategory.values());
          }
        }
      }
      ServiceManager.getService(IShopSolrWriterService.class).reCreateShopSolrIndexAll();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void createAutoMatchApplyMessage(List<ShopDTO> shopDTOList) throws Exception {
    Set<Long> ids = new HashSet<Long>();
    for (ShopDTO shopDTO : shopDTOList) {
      try {
        if (shopDTO != null && shopDTO.getShopStatus() == ShopStatus.REGISTERED_PAID) {
          ServiceManager.getService(IApplyPushMessageService.class).createSingleMobileAutoMatchApplyRelatedMessage(shopDTO, shopDTO.getShopVersionId());
        }
      } catch (Exception e) {
        if (shopDTO != null) LOG.info("shopId:[{}]create apply relate msg failed!", shopDTO.getId());
        LOG.error(e.getMessage(), e);
      }
      ids.add(shopDTO.getId());
    }
  }
}
