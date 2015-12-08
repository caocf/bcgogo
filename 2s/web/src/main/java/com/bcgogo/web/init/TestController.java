package com.bcgogo.web.init;

import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.common.Result;
import com.bcgogo.common.StringUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.mq.message.MQMessageDTO;
import com.bcgogo.mq.message.MQMessageItemDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-9
 * Time: 下午1:09
 */
@Controller
@RequestMapping("/tc")
public class TestController {
  private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

  @RequestMapping(params = "/createAppVehicleMessage")
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

  String test_appUserNo = "356824200008005";

  @RequestMapping(value = "/startPushMessageScheduleWork")
  @ResponseBody
  public void startPushMessageScheduleWork(String appUserNo) {
    try {
      appUserNo = StringUtil.isEmpty(appUserNo) ? test_appUserNo : appUserNo;
      IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      List<AppVehicleDTO> appVehicleDTOs = appUserService.getAppVehicleDTOByAppUserNo(appUserNo);
      //保险
      appointPushMessageService.createAppVehicleInsuranceTimeMessage(appVehicleDTOs);
      //验车
      appointPushMessageService.createAppVehicleExamineTimeMessage(appVehicleDTOs);
      ServiceManager.getService(IPushMessageService.class).startPushMessageScheduleWork();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  String test_openId = "oCFjjt2gpABhzgNAjkR1qsB_r6B8";

  @RequestMapping(value = "/pushTalkPacket")
  @ResponseBody
  public void pushTalkPacket(String openId, String appUserNo) {
    try {
      appUserNo = StringUtil.isEmpty(appUserNo) ? test_appUserNo : appUserNo;
      openId = StringUtil.isEmpty(appUserNo) ? test_openId : openId;
      MQMessageDTO messageDTO = new MQMessageDTO();
      messageDTO.setAppUserNo(appUserNo);
      MQMessageItemDTO itemDTO = new MQMessageItemDTO();
      itemDTO.setMsgId(10000010724352328L);
      itemDTO.setContent("hi");
      itemDTO.setFromUserName(openId);
      itemDTO.setToUserName(appUserNo);
      itemDTO.setType(0);
      itemDTO.setCreateTime(System.currentTimeMillis());
      List<MQMessageItemDTO> itemDTOs = new ArrayList<MQMessageItemDTO>();
      itemDTOs.add(itemDTO);
      messageDTO.setItemDTOs(itemDTOs);
//      RmiClientPushTools.push(messageDTO);
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

//  @RequestMapping(value = "/generationDriveLog")
//  @ResponseBody
//  public ApiResponse generationDriveLog() {
//    ServiceManager.getService(IGSMVehicleDataService.class).generationDriveLog();
//    return MessageCode.toApiResponse(MessageCode.SUCCESS);
//  }


}
