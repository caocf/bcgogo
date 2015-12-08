package com.bcgogo.web.init;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.schedule.bean.SmsSendSchedule;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-28
 * Time: 下午1:25
 * 定时钟触发器
 * scheduleTrigger.do?method=createOverdueAppointRemindMessage
 * scheduleTrigger.do?method=createAppVehicleMessage
 */
@Controller
@RequestMapping("/scheduleTrigger.do")
public class ScheduleTrigger {
  private static final Logger LOG = LoggerFactory.getLogger(ScheduleTrigger.class);

  @RequestMapping(params = "method=createOverdueAppointRemindMessage")
  @ResponseBody
  public Object createOverdueAppointRemindMessage() throws Exception {
    try {
      LOG.info("OverdueAppointRemind DateTime:" + DateUtil.dateLongToStr(System.currentTimeMillis()) + ".............");
      ServiceManager.getService(IAppointPushMessageService.class).createOverdueAppointRemindMessage(1000);
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=createOverdueAppointRemindMessage" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }

  @RequestMapping(params = "method=smsSendSchedule")
  @ResponseBody
  public Object smsSendSchedule() throws Exception {
    try {
      LOG.info("smsSendSchedule DateTime:" + DateUtil.dateLongToStr(System.currentTimeMillis()) + ".............");
      new SmsSendSchedule().processSmsJobs();
      return new Result("短信发送成功", true);
    } catch (Exception e) {
      LOG.error("method=smsSendSchedule" + e.getMessage(), e);
      return new Result("短信发送失败", false);
    }
  }

  @RequestMapping(params = "method=createAppVehicleMessage")
  @ResponseBody
  public Object createAppVehicleMessage() throws Exception {
    try {
      LOG.info("createAppVehicleMessage DateTime:" + DateUtil.dateLongToStr(System.currentTimeMillis()) + ".............");
      IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
      appointPushMessageService.createAppVehicleMaintainMileageMessage(1000);
      appointPushMessageService.createAppVehicleMaintainTimeMessage(1000);
      appointPushMessageService.createAppVehicleInsuranceTimeMessage(1000);
      appointPushMessageService.createAppVehicleExamineTimeMessage(1000);
      List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
      for (ShopDTO shopDTO : shopDTOList) {
        List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopDTO.getId());
        if (CollectionUtils.isNotEmpty(userIds)) {
          for (Long userId : userIds) {
            ServiceManager.getService(IPushMessageService.class).updatePushMessageCategoryStatNumberInMemCache(shopDTO.getId(), userId, PushMessageCategory.values());
          }
        }
      }
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=createAppVehicleMessage" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }


}
