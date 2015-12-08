package com.bcgogo.txn.service.client;

import com.bcgogo.client.AssortedMessageItem;
import com.bcgogo.client.ClientAssortedMessage;
import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.client.FeedbackType;
import com.bcgogo.enums.client.RecommendScene;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
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

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午9:55
 */
@Component
public class ClientApplyService implements IClientApplyService {
  private static final Logger LOG = LoggerFactory.getLogger(ClientApplyService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public ClientAssortedMessage getApplyMessage(Long shopId, String basePath, String userNo, String apiVersion) throws Exception {
    if (shopId == null) return null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    //权限校验
    if (!permissionVerifier(userNo, shopDTO)) return null;
    ClientAssortedMessage message = new ClientAssortedMessage();
    message.setNextRequestTime(ConfigUtils.getClientNextRequestTimeInterval());
    message.setRecommendScene(RecommendScene.RELEVANCE);
    message.setTitle(ClientConstant.RELEVANCE_TITLE);
    message.setRelatedTitle(ClientConstant.RELEVANCE_RELATED_TITLE);
    message.setRelatedUrl(basePath + ClientConstant.RELEVANCE_RELATED_URL);
    List<AssortedMessageItem> items = message.getItems();
    AssortedMessageItem item;
    String content;
    //关联推荐
    PagingListResult<ShopRelationInviteDTO> listResult = getShopRelationInvites(shopId, shopDTO.getShopVersionId());
    for (ShopRelationInviteDTO inviteDTO : listResult.getResults()) {
      item = new AssortedMessageItem();
      content = ClientConstant.RELEVANCE_RELATED_MSG;
      item.setContent(content.replace("{shopName}", inviteDTO.getOriginShopName()));
      item.setUrl(basePath + ClientConstant.RELEVANCE_RELATED_URL + "&relatedObjectId="+inviteDTO.getId());
      items.add(item);
    }
    //匹配供应商
    addApplyRelateMsg(shopId, basePath, userNo, apiVersion, shopDTO.getShopVersionId(), listResult.getPager().getTotalRows(), message);
    if (CollectionUtil.isEmpty(message.getItems())) return null;
    return message;
  }

  private boolean permissionVerifier(String userNo, ShopDTO shopDTO) {
    if (StringUtils.isBlank(userNo)) {
      LOG.warn("userNo is empty!");
      return false;
    }
    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(userNo);
    if (user == null) {
      LOG.warn("get user by userNo:[{}] is null!", userNo);
      return false;
    }
    UserGroupDTO userGroupDTO = ServiceManager.getService(IUserGroupService.class).getUserGroupByUserId(user.getId());
    if (userGroupDTO == null) {
      LOG.warn("get userGroup by id:[{}] is null!", user.getUserGroupId());
      return false;
    }
    if (ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())) {
      return ServiceManager.getService(IPrivilegeService.class)
          .verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupDTO.getId(), ResourceType.render,
              "WEB.SCHEDULE.MESSAGE_CENTER.APPLY.CUSTOMER_APPLY");
    } else {
      return ServiceManager.getService(IPrivilegeService.class)
          .verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupDTO.getId(), ResourceType.render,
              "WEB.SCHEDULE.MESSAGE_CENTER.APPLY.SUPPLIER_APPLY");
    }
  }

  //获得关联请求
  private PagingListResult<ShopRelationInviteDTO> getShopRelationInvites(Long shopId, Long shopVersionId) throws Exception {
    //获得 InviteType
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    List<InviteStatus> inviteStatuses = new ArrayList<InviteStatus>();
    inviteStatuses.add(InviteStatus.PENDING);
    InviteType inviteType;
    Pager pager = new Pager(1, 3, true);
    if (ConfigUtils.isWholesalerVersion(shopVersionId)) {
      inviteType = InviteType.CUSTOMER_INVITE;
    } else {
      inviteType = InviteType.SUPPLIER_INVITE;
    }
    return applyService.getShopRelationInvites(shopId, inviteType, inviteStatuses, null, pager);
  }

  //匹配推荐
  private void addApplyRelateMsg(Long shopId, String basePath, String userNo, String apiVersion, Long shopVersionId, int totalRows, ClientAssortedMessage message) {
    List<AssortedMessageItem> itemList = null;
    TxnWriter writer = txnDaoManager.getWriter();
    PushMessageType type = null;
    if (ConfigUtils.isWholesalerVersion(shopVersionId)) {
      itemList = getCustomerApplyRelatedMsg(basePath, shopId, userNo, apiVersion, writer, 3 - totalRows);
      type = PushMessageType.MATCHING_RECOMMEND_CUSTOMER;
    } else {
      itemList = getSupplierApplyRelatedMsg(basePath, shopId, userNo, apiVersion, writer, 3 - totalRows);
      type = PushMessageType.MATCHING_RECOMMEND_SUPPLIER;
    }
    if (CollectionUtil.isNotEmpty(itemList)) {
      message.getItems().addAll(itemList);
    }
    Integer totalApplyMsg = writer.countLatestPushMessage(shopId, type);
    message.setMsgNumber(totalRows + totalApplyMsg);

  }

  private List<AssortedMessageItem> getCustomerApplyRelatedMsg(String basePath, Long shopId, String userNo, String apiVersion, TxnWriter writer, Integer limit) {
    if (limit <= 0) return null;
    List<AssortedMessageItem> itemList = new ArrayList<AssortedMessageItem>();
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    List<PushMessageDTO> pushMessageDTOList = pushMessageService.getLatestPushMessageDTOList(shopId, 0, limit, PushMessageType.MATCHING_RECOMMEND_CUSTOMER);
    for (PushMessageDTO pushMessageDTO : pushMessageDTOList) {
      if (pushMessageDTO == null) {
        continue;
      }
      pushMessageService.generatePushMessagePromptRedirectUrl(pushMessageDTO,true);
      AssortedMessageItem item = new AssortedMessageItem();
      item.setContent(pushMessageDTO.getPromptContent());
      item.setUrl(basePath + pushMessageDTO.getRedirectUrl());
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.RELEVANCE, FeedbackType.USER_CLICK);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.RELEVANCE, FeedbackType.CLOSE);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.RELEVANCE, FeedbackType.AUTO_DISAPPEAR);
      itemList.add(item);
    }
    return itemList;
  }

  private List<AssortedMessageItem> getSupplierApplyRelatedMsg(String basePath, Long shopId, String userNo, String apiVersion, TxnWriter writer, Integer limit) {
    if (limit <= 0) return null;
    List<AssortedMessageItem> itemList = new ArrayList<AssortedMessageItem>();
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    List<PushMessageDTO> pushMessageDTOList = pushMessageService.getLatestPushMessageDTOList(shopId, 0, limit, PushMessageType.MATCHING_RECOMMEND_SUPPLIER);
    for (PushMessageDTO pushMessageDTO : pushMessageDTOList) {
      if (pushMessageDTO == null) {
        continue;
      }
      pushMessageService.generatePushMessagePromptRedirectUrl(pushMessageDTO,true);
      AssortedMessageItem item = new AssortedMessageItem();
      item.setContent(pushMessageDTO.getPromptContent());
      item.setUrl(basePath + pushMessageDTO.getRedirectUrl());
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.RELEVANCE, FeedbackType.USER_CLICK);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.RELEVANCE, FeedbackType.CLOSE);
      item.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), RecommendScene.RELEVANCE, FeedbackType.AUTO_DISAPPEAR);
      itemList.add(item);
    }
    return itemList;
  }

}
