package com.bcgogo.txn.service.client;

import com.bcgogo.client.AssortedMessageItem;
import com.bcgogo.client.ClientAssortedMessage;
import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.client.FeedbackType;
import com.bcgogo.enums.client.RecommendScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午10:35
 */
@Component
public class ClientAccessoryBuyingService implements IClientAccessoryBuyingService {
  private static final Logger LOG = LoggerFactory.getLogger(ClientAccessoryBuyingService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public List<ClientAssortedMessage> getAccessoryBuyingMessages(Long shopId, String basePath, String userNo, String apiVersion) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (StringUtils.isEmpty(userNo)) {
      return null;
    }
    ShopDTO shopDTO;
    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(userNo);
    if (user == null) {
      LOG.warn("get user by userNo:[{}] is null!", userNo);
      return null;
    }
    shopDTO = configService.getShopById(user.getShopId());
    if (shopDTO == null) {
      LOG.warn("get shop by shopId:[{}] is null!", user.getShopId());
      return null;
    }

    UserGroupDTO userGroupDTO = ServiceManager.getService(IUserGroupService.class).getUserGroupByUserId(user.getId());
    if (userGroupDTO == null) {
      LOG.warn("get userGroup by id:[{}] is null!", user.getUserGroupId());
      return null;
    }
    List<ClientAssortedMessage> messages = new ArrayList<ClientAssortedMessage>();
    ClientAssortedMessage message;
    message = this.getAccessoryMessages(shopDTO, basePath, userGroupDTO.getId(), userNo, apiVersion);
    if (message != null) messages.add(message);
    message = this.getBuyingMessages(shopDTO, basePath, userGroupDTO.getId(), userNo, apiVersion);
    if (message != null) messages.add(message);
    return messages;
  }

  //求购消息
  private ClientAssortedMessage getBuyingMessages(ShopDTO shopDTO, String basePath, Long userGroupId, String userNo, String apiVersion) throws Exception {
    if (shopDTO == null) return null;
    if (userGroupId == null) return null;
    Long shopId = shopDTO.getId();
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    if (!privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER")) {
      return null;
    }
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    List<PushMessageDTO> pushMessageDTOList = pushMessageService.getLatestPushMessageDTOList(shopId, 0, 3, PushMessageType.BUYING_INFORMATION, PushMessageType.QUOTED_BUYING_IGNORED, PushMessageType.ACCESSORY_MATCH_RESULT);
    if (CollectionUtil.isEmpty(pushMessageDTOList)) return null;
    ClientAssortedMessage message = new ClientAssortedMessage();
    message.setNextRequestTime(ConfigUtils.getClientNextRequestTimeInterval());
    message.setRecommendScene(RecommendScene.ACCESSORY_OR_BUYING);
    message.setTitle(ClientConstant.BUYING_TITLE);
    message.setRelatedTitle(ClientConstant.BUYING_RELATED_TITLE);
    message.setRelatedUrl(basePath + ClientConstant.BUYING_RELATED_URL);
    AssortedMessageItem item;
    for (PushMessageDTO pushMessageDTO : pushMessageDTOList) {
      item = new AssortedMessageItem();
      pushMessageService.generatePushMessagePromptRedirectUrl(pushMessageDTO,true);
      if (StringUtils.isNotEmpty(pushMessageDTO.getRedirectUrl())) {
        item.setUrl(basePath + pushMessageDTO.getRedirectUrl());
      }
      item.setContent(StringUtil.Html2Text(pushMessageDTO.getPromptContent()).replace(" ", ""));
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.ACCESSORY_OR_BUYING, FeedbackType.USER_CLICK);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.ACCESSORY_OR_BUYING, FeedbackType.CLOSE);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.ACCESSORY_OR_BUYING, FeedbackType.AUTO_DISAPPEAR);
      message.getItems().add(item);
    }
    return message;
  }

  //配件
  private ClientAssortedMessage getAccessoryMessages(ShopDTO shopDTO, String basePath, Long userGroupId, String userNo, String apiVersion) throws Exception {
    if (shopDTO == null) return null;
    if (userGroupId == null) return null;
    Long shopId = shopDTO.getId();
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    if (!privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.MY_PREBUYORDER")) {
      return null;
    }
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    List<PushMessageDTO> pushMessageDTOList =pushMessageService.getLatestPushMessageDTOList(shopId, 0, 3, PushMessageType.ACCESSORY,PushMessageType.ACCESSORY_PROMOTIONS,
            PushMessageType.QUOTED_BUYING_INFORMATION, PushMessageType.BUYING_MATCH_ACCESSORY, PushMessageType.BUYING_INFORMATION_MATCH_RESULT);
    if (CollectionUtil.isEmpty(pushMessageDTOList)) return null;

    ClientAssortedMessage message = new ClientAssortedMessage();
    message.setNextRequestTime(ConfigUtils.getClientNextRequestTimeInterval());
    message.setRecommendScene(RecommendScene.ACCESSORY_OR_BUYING);
    message.setTitle(ClientConstant.ACCESSORY_TITLE);
    message.setRelatedTitle(ClientConstant.ACCESSORY_RELATED_TITLE);
    message.setRelatedUrl(basePath + ClientConstant.ACCESSORY_RELATED_URL);
    message.setMsgNumber(pushMessageService.countLatestPushMessage(shopId, PushMessageType.ACCESSORY,PushMessageType.ACCESSORY_PROMOTIONS,
        PushMessageType.QUOTED_BUYING_INFORMATION, PushMessageType.BUYING_MATCH_ACCESSORY, PushMessageType.BUYING_INFORMATION_MATCH_RESULT));
    AssortedMessageItem item;
    for (PushMessageDTO pushMessageDTO : pushMessageDTOList) {
      item = new AssortedMessageItem();
      pushMessageService.generatePushMessagePromptRedirectUrl(pushMessageDTO,true);
      if (StringUtils.isNotEmpty(pushMessageDTO.getRedirectUrl())) {
        item.setUrl(basePath + pushMessageDTO.getRedirectUrl());
      }
      item.setContent(StringUtil.Html2Text(pushMessageDTO.getPromptContent()).replace(" ", ""));
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.ACCESSORY_OR_BUYING, FeedbackType.USER_CLICK);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.ACCESSORY_OR_BUYING, FeedbackType.CLOSE);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.ACCESSORY_OR_BUYING, FeedbackType.AUTO_DISAPPEAR);
      message.getItems().add(item);
    }
    return message;
  }


}
