package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.config.service.Apns.GsmAPNSAdapter;
import com.bcgogo.config.service.UMPush.GSMUMPushAdapter;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.service.messageCenter.AbstractMessageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 自定义消息
 * Created by Hans on 14-1-15.
 */
@Service
public class CustomPushMessageService extends AbstractMessageService implements ICustomPushMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(CustomPushMessageService.class);

  @Override
  public void createCustomPushMessage2App(Set<Long> customerIds, String content) throws Exception {
    if (CollectionUtil.isEmpty(customerIds)) {
      LOG.warn("customerIds is empty!");
      return;
    }
    if (StringUtil.isEmpty(content)) {
      LOG.warn("createCustomPushMessage2App content is null.");
      return;
    }
    Map<Long, List<AppUserDTO>> appUserDTOMap = ServiceManager.getService(IAppUserCustomerMatchService.class).getAppUserMapByCustomerIds(customerIds);
    if (MapUtils.isEmpty(appUserDTOMap)) {
      LOG.warn("there is no app user [customerIds:{}].", JsonUtil.objectToJson(customerIds));
      return;
    }
    List<CustomerDTO> customerDTOList = ServiceManager.getService(ICustomerService.class).getCustomerByIds(new ArrayList<Long>(appUserDTOMap.keySet()));
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    for (CustomerDTO customerDTO : customerDTOList) {
      List<AppUserDTO> appUserDTOs = appUserDTOMap.get(customerDTO.getId());
      if (CollectionUtils.isNotEmpty(appUserDTOs)) {
        for (AppUserDTO appUserDTO : appUserDTOs) {
          PushMessageDTO pushMessageDTO = new PushMessageDTO();
          pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
          pushMessageDTO.createCustomPushMessage2App(customerDTO, appUserDTO, content);
          pushMessageDTOList.add(pushMessageDTO);
        }
      }
    }
    ServiceManager.getService(IPushMessageService.class).createPushMessageList(pushMessageDTOList, false);
  }

  @Override
  public void createCustomPushMessage2App(String content, String appUserNo, long shopId) throws Exception {
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(appUserNo, null);
    if (appUserDTO == null) {
      LOG.warn("create custom push message to app fail. [appUserNo:{}].", appUserNo);
      return;
    }
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createCustomPushMessage2App(appUserDTO, content, shopId);
    //发送IOS推送消息
    if(StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if(StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())){
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    pushMessageDTOList.add(pushMessageDTO);
    ServiceManager.getService(IPushMessageService.class).createPushMessageList(pushMessageDTOList, false);
  }

}
