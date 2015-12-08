package com.bcgogo.txn.service.client;

import com.bcgogo.client.AssortedMessageItem;
import com.bcgogo.client.ClientAssortedMessage;
import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.client.FeedbackType;
import com.bcgogo.enums.client.RecommendScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午10:33
 */
@Component
public class ClientSystemService implements IClientSystemService {
  private static final Logger LOG = LoggerFactory.getLogger(ClientSystemService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public ClientAssortedMessage getSystemMessages(Long shopId, String basePath, String userNo, String apiVersion) {
    if (shopId == null && StringUtil.isEmpty(userNo)) return null;
    if (shopId == null) {
      UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(userNo);
      if (user == null) {
        LOG.warn("get user by userNo:[{}] is null!", userNo);
        return null;
      }
      shopId = user.getShopId();
    }
    ClientAssortedMessage message;
    AssortedMessageItem item;
    message = new ClientAssortedMessage();
    message.setNextRequestTime(ConfigUtils.getClientNextRequestTimeInterval());
    message.setTitle(ClientConstant.SYSTEM_TITLE);
    message.setRelatedTitle(ClientConstant.SYSTEM_RELATED_TITLE);
    //关联到shop系统连接地址
    message.setRelatedUrl(basePath + ClientConstant.SYSTEM_RELATED_URL);
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    List<PushMessageDTO> pushMessageDTOList = pushMessageService.getLatestPushMessageDTOList(shopId, 0, 3, PushMessageType.ANNOUNCEMENT, PushMessageType.FESTIVAL);
    if (CollectionUtil.isEmpty(pushMessageDTOList)) return null;
    message.setMsgNumber(pushMessageService.countLatestPushMessage(shopId, PushMessageType.ANNOUNCEMENT, PushMessageType.FESTIVAL));
    for (PushMessageDTO pushMessageDTO : pushMessageDTOList) {
      item = new AssortedMessageItem();
      pushMessageService.generatePushMessagePromptRedirectUrl(pushMessageDTO,true);
      item.setUrl(basePath + pushMessageDTO.getRedirectUrl());
      item.setContent(pushMessageDTO.getPromptContent());
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.SYSTEM, FeedbackType.USER_CLICK);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.SYSTEM, FeedbackType.CLOSE);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.SYSTEM, FeedbackType.AUTO_DISAPPEAR);
      message.getItems().add(item);
    }
    return message;
  }

}
