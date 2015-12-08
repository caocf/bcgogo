package com.bcgogo.schedule.stat;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.IAppointOrderService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 手机端用户、店铺客户匹配逻辑
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-9-6
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class ShopAutoAcceptAppointSchedule extends BcgogoQuartzJobBean {

  public static final Logger LOG = LoggerFactory.getLogger(ShopAutoAcceptAppointSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } else {
      lock = true;
      LOG.info("系统开始30分钟自动接受预约单,开始时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));

      try {
        //30分钟自动接受预约单
        IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
        appointOrderService.autoAcceptAppointOrderHalfHour();
        List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
        for (ShopDTO shopDTO : shopDTOList) {
          List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopDTO.getId());
          if(CollectionUtils.isNotEmpty(userIds)){
            for(Long userId : userIds){
              ServiceManager.getService(IPushMessageService.class).updatePushMessageCategoryStatNumberInMemCache(shopDTO.getId(), userId, PushMessageCategory.values());
            }
          }
        }
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      lock = false;
      LOG.info("系统结束30分钟自动接受预约单,结束时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));
    }

  }
}
