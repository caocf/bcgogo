package com.bcgogo.txn.service.client;

import com.bcgogo.client.ClientPrompt;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.client.FeedbackType;
import com.bcgogo.enums.client.RecommendScene;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-6-19
 * Time: 上午11:41
 */
@Component
public class ClientPromptMsgSelector implements IClientPromptMsgSelector {
  private static final Logger LOG = LoggerFactory.getLogger(ClientPromptMsgSelector.class);
  @Autowired
  private TxnDaoManager txnDaoManager;
  private IPushMessageService pushMessageService;

  protected IPushMessageService getPushMessageService() {
    return pushMessageService == null ? pushMessageService = ServiceManager.getService(IPushMessageService.class) : pushMessageService;
  }

  @Override
  public ClientPrompt getPrompt(String basePath, Long shopId, String apiVersion, String userNo) throws Exception {
    ClientPrompt result = new ClientPrompt();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (StringUtils.isEmpty(userNo)) {
      return null;
    }
    ShopDTO shopDTO;
    if (shopId == null) {
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
    } else {
      shopDTO = configService.getShopById(shopId);
      if (shopDTO == null) {
        LOG.warn("get shop by shopId:[{}] is null!", shopId);
        return null;
      }
    }
    List<PushMessageType> pushMessageTypes = getPromptPushMessageTypes(userNo, shopDTO, shopId);
    if (CollectionUtils.isEmpty(pushMessageTypes)) return null;
    PushMessageDTO pushMessageDTO = getPushMessageService().getLatestPushMessageDTO(shopDTO.getId(),shopDTO.getId(), shopDTO.getShopKind(),
        pushMessageTypes.toArray(new PushMessageType[pushMessageTypes.size()]));
    if (pushMessageDTO == null) {
      return null;
    }
    getPushMessageService().generatePushMessagePromptRedirectUrl(pushMessageDTO,true);
    return toPromptMsg(basePath, shopDTO.getId(), apiVersion, userNo, result, pushMessageDTO);
  }

  private ClientPrompt toPromptMsg(String basePath, Long shopId, String apiVersion, String userNo, ClientPrompt result, PushMessageDTO pushMessageDTO) {
    RecommendScene recommendScene = RecommendScene.getByPushMessageType(pushMessageDTO.getType());
    //如果是order 获得通类型的推送消失
    if (RecommendScene.ORDER == recommendScene) {
      pushMessageDTO = createBatchPushMessageDTO(pushMessageDTO);
    }
    if (pushMessageDTO == null) return null;
    result.setTitle(pushMessageDTO.getTitle());
    if (RecommendScene.APPOINT_ORDER != recommendScene)
      result.setContent(StringUtil.Html2Text(pushMessageDTO.getPromptContent()).replace(" ", ""));
    else
      result.setContent(pushMessageDTO.getPromptContent());
    if (StringUtils.isNotBlank(pushMessageDTO.getRedirectUrl()))
      result.setUrl(basePath + pushMessageDTO.getRedirectUrl());
    result.setRecommendId(pushMessageDTO.getId());
    result.setRecommendScene(recommendScene);
    result.setNextRequestTime(ConfigUtils.getClientNextRequestTimeInterval());
    //单击
    result.setFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), recommendScene, FeedbackType.USER_CLICK);
    //单击 关闭 消失
    result.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), recommendScene, FeedbackType.USER_CLICK);
    result.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), recommendScene, FeedbackType.CLOSE);
    result.addFeedbackUrl(basePath, shopId, userNo, apiVersion, pushMessageDTO.getId(), recommendScene, FeedbackType.AUTO_DISAPPEAR);
    return result;
  }

  private PushMessageDTO createBatchPushMessageDTO(PushMessageDTO pushMessageDTO) {
    if (pushMessageDTO.getCurrentPushMessageReceiverDTO() == null) return null;

    boolean isShowStatusChanged = false;
    TxnWriter writer = txnDaoManager.getWriter();
    Long receiverShopId = pushMessageDTO.getCurrentPushMessageReceiverDTO().getShopId();
    List<Object[]> objects = writer.getLatestPushMessage(receiverShopId, pushMessageDTO.getType());
    if (objects.size() <= 1) return pushMessageDTO;
    Object status = writer.begin();
    PushMessage pushMessage;
    PushMessageReceiver pushMessageReceiver;
    String pushMessageReceiverId = "";
    try {
      for (Object[] o : objects) {
        pushMessageReceiver = (PushMessageReceiver) o[1];
        pushMessageReceiverId += pushMessageReceiver.getId() + ",";
      }
      for (Object[] o : objects) {
        pushMessage = (PushMessage) o[0];
        Map<String,String> paramsMap = new HashMap<String, String>();
        paramsMap.put(PushMessageParamsKeyConstant.PushMessageReceiverIds,pushMessageReceiverId.substring(0, pushMessageReceiverId.length() - 1));
        pushMessage.setParams(JsonUtil.mapToJson(paramsMap));
        pushMessageReceiver = (PushMessageReceiver) o[1];
        pushMessageReceiver.setPushStatus(PushMessagePushStatus.PUSHING);
        if(!PushMessageShowStatus.ACTIVE.equals(pushMessageReceiver.getShowStatus())){
          isShowStatusChanged = true;
        }
        pushMessageReceiver.setShowStatus(PushMessageShowStatus.ACTIVE);
        writer.update(pushMessageReceiver);
        writer.update(pushMessage);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    pushMessageDTO.setPromptContent(pushMessageDTO.getPromptContent().replace("1", String.valueOf(objects.size())));
    if (isShowStatusChanged){
      List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(receiverShopId);
      if(CollectionUtils.isNotEmpty(userIds)){
        for(Long userId : userIds){
          ServiceManager.getService(IPushMessageService.class).updatePushMessageCategoryStatNumberInMemCache(receiverShopId, userId,PushMessageCategory.valueOfPushMessageType(pushMessageDTO.getType()));
        }
      }
    }

    return pushMessageDTO;
  }

  private List<PushMessageType> getPromptPushMessageTypes(String userNo, ShopDTO shopDTO, Long shopId) {
    List<PushMessageType> pushMessageTypeList = new ArrayList<PushMessageType>();
    pushMessageTypeList.add(PushMessageType.ANNOUNCEMENT);
    pushMessageTypeList.add(PushMessageType.FESTIVAL);
    if (shopId == null) {
      return pushMessageTypeList;
    }

    UserDTO user = ServiceManager.getService(IUserService.class).getUserByUserInfo(userNo);
    if (user == null) {
      LOG.warn("get user by userNo:[{}] is null!", userNo);
      return pushMessageTypeList;
    }
    UserGroupDTO userGroupDTO = ServiceManager.getService(IUserGroupService.class).getUserGroupByUserId(user.getId());
    if (userGroupDTO == null) {
      LOG.warn("get userGroup by id:[{}] is null!", user.getUserGroupId());
      return pushMessageTypeList;
    }

    Long shopVersionId = shopDTO.getShopVersionId(),
        userGroupId = userGroupDTO.getId();
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    pushMessageTypeList.add(PushMessageType.MATCHING_RECOMMEND_CUSTOMER);
    pushMessageTypeList.add(PushMessageType.MATCHING_RECOMMEND_SUPPLIER);

    if (privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
        "WEB.SCHEDULE.MESSAGE_CENTER.APPLY.CUSTOMER_APPLY")) {
      pushMessageTypeList.add(PushMessageType.APPLY_CUSTOMER);
    }
    if (privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
        "WEB.SCHEDULE.MESSAGE_CENTER.APPLY.SUPPLIER_APPLY")) {
      pushMessageTypeList.add(PushMessageType.APPLY_SUPPLIER);
    }
    if (privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
        "WEB.SCHEDULE.REMIND_ORDERS.PURCHASE")) {
      pushMessageTypeList.add(PushMessageType.PURCHASE_SELLER_STOCK);
      pushMessageTypeList.add(PushMessageType.PURCHASE_SELLER_DISPATCH);
      pushMessageTypeList.add(PushMessageType.PURCHASE_SELLER_REFUSED);
    }
    if (privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
        "WEB.SCHEDULE.REMIND_ORDERS.SALE")) {
      pushMessageTypeList.add(PushMessageType.SALE_NEW);
    }
    if (privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
        "WEB.TXN.ORDER_CENTER.SALE_RETURN")) {
      pushMessageTypeList.add(PushMessageType.SALE_RETURN_NEW);
    }
    //报价
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER")) {
      pushMessageTypeList.add(PushMessageType.BUYING_INFORMATION);
      pushMessageTypeList.add(PushMessageType.BUSINESS_CHANCE_SELL_WELL);
      pushMessageTypeList.add(PushMessageType.BUSINESS_CHANCE_LACK);
      //未被采纳
      pushMessageTypeList.add(PushMessageType.QUOTED_BUYING_IGNORED);
    }
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.SALES_ACCESSORY.BASE")) {
      pushMessageTypeList.add(PushMessageType.ACCESSORY_MATCH_RESULT);
    }
    //求购
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.MY_PREBUYORDER")) {
      pushMessageTypeList.add(PushMessageType.QUOTED_BUYING_INFORMATION);
      pushMessageTypeList.add(PushMessageType.BUYING_INFORMATION_MATCH_RESULT);
    }
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE")) {
      pushMessageTypeList.add(PushMessageType.ACCESSORY);
      pushMessageTypeList.add(PushMessageType.ACCESSORY_PROMOTIONS);
      pushMessageTypeList.add(PushMessageType.BUYING_MATCH_ACCESSORY);
    }
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.VEHICLE_CONSTRUCTION.APPOINT_ORDER_LIST")) {
      pushMessageTypeList.add(PushMessageType.SYS_ACCEPT_APPOINT);
      pushMessageTypeList.add(PushMessageType.APP_CANCEL_APPOINT);
      pushMessageTypeList.add(PushMessageType.APP_APPLY_APPOINT);
      pushMessageTypeList.add(PushMessageType.OVERDUE_APPOINT_TO_SHOP);
      pushMessageTypeList.add(PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP);
    }
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.SCHEDULE.ENQUIRY_LIST")) {
      pushMessageTypeList.add(PushMessageType.APP_SUBMIT_ENQUIRY);
    }
//    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.logic,"")) {
      pushMessageTypeList.add(PushMessageType.VEHICLE_FAULT_2_SHOP);
      pushMessageTypeList.add(PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP);
      pushMessageTypeList.add(PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP);
      pushMessageTypeList.add(PushMessageType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP);
      pushMessageTypeList.add(PushMessageType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP);
      pushMessageTypeList.add(PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP);
      pushMessageTypeList.add(PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP);
      pushMessageTypeList.add(PushMessageType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP);
      pushMessageTypeList.add(PushMessageType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP);
//    }

    //      pushMessageTypeList.remove(PushMessageType.MATCHING_RECOMMEND_SUPPLIER); 汽配汽修都有
    //版本权限过滤
    if(ConfigUtils.isWholesalerVersion(shopVersionId)){//汽配需要过滤
      pushMessageTypeList.remove(PushMessageType.VEHICLE_FAULT_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP);

      pushMessageTypeList.remove(PushMessageType.APP_SUBMIT_ENQUIRY);
      pushMessageTypeList.remove(PushMessageType.APPLY_SUPPLIER);

      pushMessageTypeList.remove(PushMessageType.ACCESSORY);
      pushMessageTypeList.remove(PushMessageType.ACCESSORY_PROMOTIONS);
      pushMessageTypeList.remove(PushMessageType.BUYING_MATCH_ACCESSORY);
      pushMessageTypeList.remove(PushMessageType.APPLY_SUPPLIER);
      pushMessageTypeList.remove(PushMessageType.QUOTED_BUYING_INFORMATION);
      pushMessageTypeList.remove(PushMessageType.BUYING_INFORMATION_MATCH_RESULT);
      pushMessageTypeList.remove(PushMessageType.SYS_ACCEPT_APPOINT);
      pushMessageTypeList.remove(PushMessageType.APP_CANCEL_APPOINT);
      pushMessageTypeList.remove(PushMessageType.APP_APPLY_APPOINT);
      pushMessageTypeList.remove(PushMessageType.OVERDUE_APPOINT_TO_SHOP);
      pushMessageTypeList.remove(PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP);

    }else{
      pushMessageTypeList.remove(PushMessageType.APPLY_CUSTOMER);
      pushMessageTypeList.remove(PushMessageType.MATCHING_RECOMMEND_CUSTOMER);
      pushMessageTypeList.remove(PushMessageType.PURCHASE_SELLER_STOCK);
      pushMessageTypeList.remove(PushMessageType.PURCHASE_SELLER_DISPATCH);
      pushMessageTypeList.remove(PushMessageType.PURCHASE_SELLER_REFUSED);
      pushMessageTypeList.remove(PushMessageType.SALE_NEW);
      pushMessageTypeList.remove(PushMessageType.SALE_RETURN_NEW);
      pushMessageTypeList.remove(PushMessageType.BUYING_INFORMATION);
      //未被采纳
      pushMessageTypeList.remove(PushMessageType.QUOTED_BUYING_IGNORED);
      pushMessageTypeList.remove(PushMessageType.ACCESSORY_MATCH_RESULT);
    }
    //--------需求特意过滤--------------
    pushMessageTypeList.remove(PushMessageType.ACCESSORY);
    pushMessageTypeList.remove(PushMessageType.ACCESSORY_PROMOTIONS);
    pushMessageTypeList.remove(PushMessageType.ACCESSORY_MATCH_RESULT);
    pushMessageTypeList.remove(PushMessageType.BUYING_MATCH_ACCESSORY);
    return pushMessageTypeList;
  }

}
