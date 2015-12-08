package com.bcgogo.txn.service.client;

import com.bcgogo.client.FeedbackResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.client.FeedbackType;
import com.bcgogo.enums.client.RecommendScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageFeedbackType;
import com.bcgogo.enums.txn.pushMessage.PushMessagePushStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageFeedbackRecord;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.service.pushMessage.IApplyPushMessageService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RegexUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-6-20
 * Time: 下午5:47
 */
@Component
public class ClientFeedbackService implements IClientFeedbackService {
  private static final Logger LOG = LoggerFactory.getLogger(ClientFeedbackService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public FeedbackResult feedbackUserAction(Long shopId, String userNo, RecommendScene recommendScene, String recommendId, FeedbackType feedbackType) throws Exception{
    FeedbackResult result = new FeedbackResult("false");
    if (shopId == null) {
      return result;
    }
    if (StringUtils.isBlank(recommendId) || !RegexUtils.isDigital(recommendId)) {
      return result;
    }
    Long pushMessageId = Long.valueOf(recommendId);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    if (shopDTO == null) return result;
    //非系统消息
    if (StringUtils.isNotBlank(userNo) && recommendScene != RecommendScene.SYSTEM) {
      //权限校验
      if (!permissionVerifier(userNo, shopDTO)) return result;
      //如果是order 批量处理
      if (RecommendScene.ORDER == recommendScene) {
        if (!updateOrderPushMessageReceiverStatus(shopId, pushMessageId, feedbackType)) {
          return result;
        }
      } else {
        if (!updatePushMessageReceiverStatus(shopId, pushMessageId, feedbackType)) {
          return result;
        }
        if (RecommendScene.RELEVANCE == recommendScene) {
          IApplyPushMessageService applyPushMessageService = ServiceManager.getService(IApplyPushMessageService.class);
          if (feedbackType != FeedbackType.AUTO_DISAPPEAR) {
            applyPushMessageService.cancelApplyRecommendAssociated(pushMessageId);
          }
        }
      }
    } else {
      if (!updatePushMessageReceiverStatus(shopId, pushMessageId, feedbackType)) {
        return result;
      }
    }
    result.setIsSuccess("true");
    return result;
  }

  private boolean updateOrderPushMessageReceiverStatus(Long shopId, Long pushMessageId, FeedbackType feedbackType) {
    TxnWriter writer = txnDaoManager.getWriter();
    PushMessage pushMessage = writer.getById(PushMessage.class, pushMessageId);
    if (pushMessage == null) return false;
    String param = pushMessage.getParams();
    Map<String,String> paramsMap = JsonUtil.jsonToStringMap(pushMessage.getParams());
    String pushMessageReceiverIds = paramsMap.get(PushMessageParamsKeyConstant.PushMessageReceiverIds);
    if (StringUtils.isBlank(pushMessageReceiverIds)) {
      return updatePushMessageReceiverStatus(shopId, pushMessageId, feedbackType);
    }
    PushMessageReceiver pushMessageReceiver;
    List<Long> ids = NumberUtil.parseLongValues(pushMessageReceiverIds);
    Object status = writer.begin();
    try {
      if (CollectionUtils.isNotEmpty(ids)) {
        for (Long id : ids) {
          pushMessageReceiver = writer.getById(PushMessageReceiver.class, id);
          pushMessageReceiver.setStatus(FeedbackType.getByPushMessageStatus(feedbackType));
          pushMessageReceiver.setPushStatus(PushMessagePushStatus.UN_PUSH);
          pushMessage = writer.getById(PushMessage.class, pushMessageReceiver.getMessageId());
          pushMessage.setParams(null);
          writer.update(pushMessageReceiver);
          writer.update(pushMessage);
          //保存反馈记录
          if (feedbackType != FeedbackType.AUTO_DISAPPEAR) {
            writer.save(new PushMessageFeedbackRecord(shopId, pushMessageId, PushMessageFeedbackType.getByFeedbackType(feedbackType), System.currentTimeMillis()));
          }
        }
        writer.commit(status);
        return true;
      } else {
        return false;
      }
    } finally {
      writer.rollback(status);
    }
  }

  // update 新状态
  private boolean updatePushMessageReceiverStatus(Long shopId, Long pushMessageId, FeedbackType feedbackType) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<PushMessageReceiver> pushMessageReceiverList = writer.getPushMessageReceiverByMessageId(shopId,PushMessageReceiverStatus.UNREAD, pushMessageId);
      if (CollectionUtils.isNotEmpty(pushMessageReceiverList)) {
        for (PushMessageReceiver pms : pushMessageReceiverList) {
          pms.setStatus(FeedbackType.getByPushMessageStatus(feedbackType));
          pms.setPushStatus(PushMessagePushStatus.UN_PUSH);
          writer.update(pms);
          //保存反馈记录
          if (feedbackType != FeedbackType.AUTO_DISAPPEAR) {
            writer.save(new PushMessageFeedbackRecord(shopId, pushMessageId, PushMessageFeedbackType.getByFeedbackType(feedbackType), System.currentTimeMillis()));
          }
        }
        writer.commit(status);
        return true;
      } else {
        return false;
      }
    } finally {
      writer.rollback(status);
    }

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
    return true;
  }
}
