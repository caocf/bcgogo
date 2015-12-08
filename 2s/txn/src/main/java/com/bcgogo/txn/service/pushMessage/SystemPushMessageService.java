package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageSourceDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-20
 * Time: 上午10:49
 */
@Component
public class SystemPushMessageService implements ISystemPushMessageService {
  @Override
  public void createOrUpdateAnnouncementPushMessage(Long announcementId, String content, PushMessageLevel level) throws Exception {
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    PushMessageDTO pushMessageDTO;
    List<PushMessageDTO> pushMessageDTOList = pushMessageService.getPushMessageDTOBySourceId(null,announcementId,PushMessageType.ANNOUNCEMENT, PushMessageSourceType.ANNOUNCEMENT);
    pushMessageDTO = CollectionUtil.getFirst(pushMessageDTOList);
    if (pushMessageDTO != null) {
      pushMessageDTO.setLevel(level);
      pushMessageService.updatePushMessage(pushMessageDTO);
      return;
    }
    pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setShopId(ShopConstant.BC_SHOP_ID);
    pushMessageDTO.setCreatorType(OperatorType.SYS);
    pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.setLevel(level);
    pushMessageDTO.setType(PushMessageType.ANNOUNCEMENT);
    pushMessageDTO.setPromptContent(ClientConstant.SYSTEM_ANNOUNCEMENT_CONTENT);
    pushMessageDTO.setContent(ClientConstant.SYSTEM_ANNOUNCEMENT_CONTENT);
    pushMessageDTO.setContentText(ClientConstant.SYSTEM_ANNOUNCEMENT_CONTENT);
    PushMessageReceiverDTO pushMessageReceiverDTO;
    List<PushMessageReceiverDTO> pushMessageReceiverDTOList = new ArrayList<PushMessageReceiverDTO>();
    List<ShopDTO> shopDTOList = configService.getActiveShop();
    for (ShopDTO s : shopDTOList) {
      pushMessageReceiverDTO = new PushMessageReceiverDTO();
      pushMessageReceiverDTO.setShopId(s.getId());
      pushMessageReceiverDTO.setReceiverId(pushMessageReceiverDTO.getShopId());
      pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
      pushMessageReceiverDTO.setShowStatus(PushMessageShowStatus.ACTIVE);
      pushMessageReceiverDTO.setShopKind(s.getShopKind());
      pushMessageReceiverDTOList.add(pushMessageReceiverDTO);
    }
    pushMessageDTO.setPushMessageReceiverDTOList(pushMessageReceiverDTOList);
    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(announcementId);
    pushMessageSourceDTO.setShopId(ShopConstant.BC_SHOP_ID);
    pushMessageSourceDTO.setType(PushMessageSourceType.ANNOUNCEMENT);
    pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);
    pushMessageService.createPushMessage(pushMessageDTO,true);
  }

  @Override
  public void createOrUpdateFestivalPushMessage(Long festivalId, PushMessageLevel level, String name, Long start, Long end) throws Exception {
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    PushMessageDTO pushMessageDTO;
    List<PushMessageDTO> pushMessageDTOList = pushMessageService.getPushMessageDTOBySourceId(null,festivalId,PushMessageType.FESTIVAL, PushMessageSourceType.FESTIVAL);
    pushMessageDTO = CollectionUtil.getFirst(pushMessageDTOList);
    if (pushMessageDTO != null) {
      pushMessageDTO.setLevel(level);
      pushMessageDTO.setCreateTime(start);
      pushMessageDTO.setEndDate(end);
      pushMessageDTO.setLevel(level);
      String content = ClientConstant.SYSTEM_FESTIVAL_CONTENT;
      pushMessageDTO.setPromptContent(content.replace("{name}", name));
      pushMessageDTO.setContent(content.replace("{name}", name));
      pushMessageDTO.setContentText(content.replace("{name}", name));
      pushMessageService.updatePushMessage(pushMessageDTO);
      return;
    }
    pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setShopId(ShopConstant.BC_SHOP_ID);
    pushMessageDTO.setCreatorType(OperatorType.SYS);
    pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
    pushMessageDTO.setCreateTime(start);
    pushMessageDTO.setEndDate(end);
    pushMessageDTO.setLevel(level);
    pushMessageDTO.setTitle(ClientConstant.SYSTEM_TITLE);
    pushMessageDTO.setType(PushMessageType.FESTIVAL);
    String content = ClientConstant.SYSTEM_FESTIVAL_CONTENT;
    pushMessageDTO.setPromptContent(content.replace("{name}", name));
    pushMessageDTO.setContent(content.replace("{name}", name));
    pushMessageDTO.setContentText(content.replace("{name}", name));
    PushMessageReceiverDTO pushMessageReceiverDTO;
    List<PushMessageReceiverDTO> pushMessageReceiverDTOList = new ArrayList<PushMessageReceiverDTO>();
    List<ShopDTO> shopDTOList = configService.getActiveShop();
    for (ShopDTO s : shopDTOList) {
      pushMessageReceiverDTO = new PushMessageReceiverDTO();
      pushMessageReceiverDTO.setShopId(s.getId());
      pushMessageReceiverDTO.setReceiverId(pushMessageReceiverDTO.getShopId());
      pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
      pushMessageReceiverDTO.setShowStatus(PushMessageShowStatus.ACTIVE);
      pushMessageReceiverDTO.setShopKind(s.getShopKind());
      pushMessageReceiverDTOList.add(pushMessageReceiverDTO);
    }
    pushMessageDTO.setPushMessageReceiverDTOList(pushMessageReceiverDTOList);
    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(festivalId);
    pushMessageSourceDTO.setShopId((ShopConstant.BC_SHOP_ID));
    pushMessageSourceDTO.setType(PushMessageSourceType.FESTIVAL);
    pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);
    pushMessageService.createPushMessage(pushMessageDTO,true);
  }

}
