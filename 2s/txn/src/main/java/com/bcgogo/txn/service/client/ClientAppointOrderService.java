package com.bcgogo.txn.service.client;

import com.bcgogo.client.AssortedMessageItem;
import com.bcgogo.client.ClientAssortedMessage;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.enums.client.RecommendScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午10:35
 */
@Component
public class ClientAppointOrderService implements IClientAppointOrderService {
  private static final Logger LOG = LoggerFactory.getLogger(ClientAppointOrderService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public ClientAssortedMessage getAppointOrderMessages(Long shopId, String basePath, String userNo, String apiVersion) throws Exception {
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
    return this.getOrderMessages(shopDTO, basePath, userGroupDTO.getId(), userNo, apiVersion);
  }

  public ClientAssortedMessage getOrderMessages(ShopDTO shopDTO, String basePath, Long userGroupId, String userNo, String apiVersion) throws Exception {
    if (shopDTO == null) return null;
    if (userGroupId == null) return null;
    Long shopId = shopDTO.getId();

    if (!ServiceManager.getService(IPrivilegeService.class).verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.VEHICLE_CONSTRUCTION.APPOINT_ORDER_LIST")) {
      return null;
    }
    Map<AppointOrderStatus, Integer> map =  txnDaoManager.getWriter().countAppointOrderByShopId(shopId);

    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);

    ClientAssortedMessage message = new ClientAssortedMessage();
    message.setNextRequestTime(ConfigUtils.getClientNextRequestTimeInterval());
    message.setRecommendScene(RecommendScene.APPOINT_ORDER);
    message.setTitle(ClientConstant.APPOINT_ORDER_TITLE);
    message.setRelatedTitle(ClientConstant.APPOINT_ORDER_RELATED_TITLE);
    message.setRelatedUrl(basePath + ClientConstant.APPOINT_ORDER_RELATED_URL);

    AssortedMessageItem item;
    int number;

    //新的预约
    item = new AssortedMessageItem();
    number = NumberUtil.intValue(map.get(AppointOrderStatus.PENDING), 0);
    String content = ClientConstant.APPOINT_ORDER_PENDING_CONTENT;
    item.setContent(content.replace("{number}", String.valueOf(number)));
    item.setUrl(basePath + ClientConstant.APPOINT_ORDER_PENDING_RELATED_URL);
    message.getItems().add(item);
    message.addMsgNumber(number);

    //待办预约
    item = new AssortedMessageItem();
    number = NumberUtil.intValue(map.get(AppointOrderStatus.ACCEPTED), 0) + NumberUtil.intValue(map.get(AppointOrderStatus.TO_DO_REPAIR), 0);
    content = ClientConstant.APPOINT_ORDER_CANCELED_CONTENT;
    item.setContent(content.replace("{number}", String.valueOf(number)));
    item.setUrl(basePath + ClientConstant.APPOINT_ORDER_CANCELED_RELATED_URL);
    message.getItems().add(item);
    message.addMsgNumber(number);

    //过期
    item = new AssortedMessageItem();
    Long[] intervals = ConfigUtils.getOverdueAppointRemindIntervals();
    Long currentTime = System.currentTimeMillis(),
        upTime = currentTime + intervals[1],
        downTime = currentTime + intervals[0];
    number = txnDaoManager.getWriter().countOverdueAndSoonExpireAppointOrderByShopId(upTime, downTime, shopId);
    content = ClientConstant.APPOINT_ORDER_OVERDUE_AND_SOON_EXPIRE_CONTENT;
    item.setContent(content.replace("{number}", String.valueOf(number)));
    item.setUrl(basePath + ClientConstant.APPOINT_ORDER_OVERDUE_AND_SOON_EXPIRE_RELATED_URL);
    message.getItems().add(item);
    message.addMsgNumber(number);
    return message;
  }


}
