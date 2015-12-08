package com.bcgogo.txn.service.client;

import com.bcgogo.client.AssortedMessageItem;
import com.bcgogo.client.ClientAssortedMessage;
import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.client.RecommendScene;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.remind.OrderCenterDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.service.remind.IOrderCenterService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.CollectionUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午10:31
 */
@Component
public class ClientOrderService implements IClientOrderService {
  private static final Logger LOG = LoggerFactory.getLogger(ClientOrderService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;


  @Override
  public ClientAssortedMessage getOrderStatMessage(Long shopId, String basePath, String userNo, String apiVersion) throws ParseException {
    if (shopId == null) return null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);

    if (StringUtils.isBlank(userNo)) {
      LOG.warn("userNo is empty!");
      return null;
    }
    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(userNo);
    if (user == null) {
      LOG.warn("get user by userNo:[{}] is null!", userNo);
      return null;
    }
    UserGroupDTO userGroupDTO = ServiceManager.getService(IUserGroupService.class).getUserGroupByUserId(user.getId());
    if (userGroupDTO == null) {
      LOG.warn("get userGroup by id:[{}] is null!", user.getUserGroupId());
      return null;
    }

    ClientAssortedMessage message = new ClientAssortedMessage();
    message.setNextRequestTime(ConfigUtils.getClientNextRequestTimeInterval());
    message.setRecommendScene(RecommendScene.ORDER);
    message.setTitle(ClientConstant.ORDER_TITLE);
    message.setRelatedTitle(ClientConstant.ORDER_RELATED_TITLE);
    message.setRelatedUrl(basePath + ClientConstant.ORDER_RELATED_URL);
    if (ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())) {
      wholesalerShopOrderCenterStatistics(basePath, shopId, message, shopDTO.getShopVersionId(), userGroupDTO.getId());
    } else {
      if (ServiceManager.getService(IPrivilegeService.class).verifierUserGroupResource(shopDTO.getShopVersionId(),
          userGroupDTO.getId(), ResourceType.menu, "WEB.SCHEDULE.REMIND_ORDERS.PURCHASE")) {
        normalShopOrderCenterStatistics(basePath, shopId, message);
      }
    }
    if (CollectionUtil.isEmpty(message.getItems())) return null;
    return message;
  }

  private void normalShopOrderCenterStatistics(String basePath, Long shopId, ClientAssortedMessage message) {
    IOrderCenterService orderCenterService = ServiceManager.getService(IOrderCenterService.class);
    OrderCenterDTO orderCenterDTO = orderCenterService.getOrderCenterPurchaseSellerStatistics(shopId);
    AssortedMessageItem item;
    //卖家备货中
    if (orderCenterDTO.getPurchaseSellerStock() != null && orderCenterDTO.getPurchaseSellerStock() >= 0) {
      item = new AssortedMessageItem();
      String content = ClientConstant.ORDER_STATISTICS_PURCHASE_SELLER_STOCK;
      item.setContent(content.replace("{number}", String.valueOf(orderCenterDTO.getPurchaseSellerStock())));
      item.setUrl(basePath + ClientConstant.ORDER_STATISTICS_PURCHASE_SELLER_STOCK_URL);
      message.getItems().add(item);
      message.addMsgNumber(orderCenterDTO.getPurchaseSellerStock().intValue());
    }
    //卖家发货中
    if (orderCenterDTO.getPurchaseSellerDispatch() != null && orderCenterDTO.getPurchaseSellerDispatch() >= 0) {
      item = new AssortedMessageItem();
      String content = ClientConstant.ORDER_STATISTICS_PURCHASE_SELLER_DISPATCH;
      item.setContent(content.replace("{number}", String.valueOf(orderCenterDTO.getPurchaseSellerDispatch())));
      item.setUrl(basePath + ClientConstant.ORDER_STATISTICS_PURCHASE_SELLER_DISPATCH_URL);
      message.getItems().add(item);
      message.addMsgNumber(orderCenterDTO.getPurchaseSellerDispatch().intValue());
    }
    //卖家已拒绝
    if (orderCenterDTO.getPurchaseSellerRefused() != null && orderCenterDTO.getPurchaseSellerRefused() >= 0) {
      item = new AssortedMessageItem();
      String content = ClientConstant.ORDER_STATISTICS_PURCHASE_SELLER_REFUSED;
      item.setContent(content.replace("{number}", String.valueOf(orderCenterDTO.getPurchaseSellerRefused())));
      item.setUrl(basePath + ClientConstant.ORDER_STATISTICS_PURCHASE_SELLER_REFUSED_URL);
      message.getItems().add(item);
      message.addMsgNumber(orderCenterDTO.getPurchaseSellerRefused().intValue());
    }
  }

  public void wholesalerShopOrderCenterStatistics(String basePath, Long shopId, ClientAssortedMessage message, Long shopVersionId, Long userGroupId) {
    IOrderCenterService orderCenterService = ServiceManager.getService(IOrderCenterService.class);
    OrderCenterDTO orderCenterDTO = orderCenterService.getOrderCenterSaleAndSaleReturnNewStatistics(shopId);
    AssortedMessageItem item;
    if (orderCenterDTO.getSaleNew() != null && orderCenterDTO.getSaleNew() >= 0) {
      if (ServiceManager.getService(IPrivilegeService.class).verifierUserGroupResource(shopVersionId,
          userGroupId, ResourceType.menu, "WEB.SCHEDULE.REMIND_ORDERS.SALE")) {
        item = new AssortedMessageItem();
        String content = ClientConstant.ORDER_STATISTICS_SALE_NEW;
        item.setContent(content.replace("{number}", String.valueOf(orderCenterDTO.getSaleNew())));
        item.setUrl(basePath + ClientConstant.ORDER_STATISTICS_SALE_NEW_URL);
        message.getItems().add(item);
        message.addMsgNumber(orderCenterDTO.getSaleNew().intValue());
      }
    }
    if (orderCenterDTO.getSaleReturnNew() != null && orderCenterDTO.getSaleReturnNew() >= 0) {
      if (ServiceManager.getService(IPrivilegeService.class).verifierUserGroupResource(shopVersionId,
          userGroupId, ResourceType.menu, "WEB.TXN.ORDER_CENTER.SALE_RETURN")) {
        item = new AssortedMessageItem();
        String content = ClientConstant.ORDER_STATISTICS_SALE_RETURN_NEW;
        item.setContent(content.replace("{number}", String.valueOf(orderCenterDTO.getSaleReturnNew())));
        item.setUrl(basePath + ClientConstant.ORDER_STATISTICS_SALE_RETURN_NEW_URL);
        message.getItems().add(item);
        message.addMsgNumber(orderCenterDTO.getSaleReturnNew().intValue());
      }
    }
  }


}
