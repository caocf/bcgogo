package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.constant.pushMessage.PushMessageContentTemplate;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.constant.pushMessage.PushMessagePromptTemplate;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.mq.message.*;
import com.bcgogo.notification.velocity.PushMessageVelocityContext;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.remind.dto.message.SearchMessageCondition;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderItemDTO;
import com.bcgogo.txn.dto.pushMessage.*;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.message.ShopTalkMessage;
import com.bcgogo.txn.model.pushMessage.*;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.messageCenter.AbstractMessageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-5-14
 * Time: 下午2:30
 */
@Service
public class PushMessageService extends AbstractMessageService implements IPushMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(PushMessageService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public Long createPushMessage(PushMessageDTO pushMessageDTO, boolean isUpdateStatNumberInMemCache) throws Exception {
    Set<Long> pushMessageReceiverShopIdSet = new HashSet<Long>();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      createPushMessage(pushMessageDTO, writer, pushMessageReceiverShopIdSet);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    if (CollectionUtils.isNotEmpty(pushMessageReceiverShopIdSet) && isUpdateStatNumberInMemCache) {
      for (Long pushMessageReceiverShopId : pushMessageReceiverShopIdSet) {
        List<Long> userIds = this.getUserIds(pushMessageReceiverShopId);
        for (Long userId : userIds) {
          updatePushMessageCategoryStatNumberInMemCache(pushMessageReceiverShopId, userId, PushMessageCategory.valueOfPushMessageType(pushMessageDTO.getType()));
        }
      }
    }
    return pushMessageDTO.getId();
  }

  @Override
  public void createPushMessageList(List<PushMessageDTO> pushMessageDTOList, boolean isUpdateStatNumberInMemCache) throws Exception {
    if (CollectionUtil.isEmpty(pushMessageDTOList)) return;
    Set<Long> pushMessageReceiverShopIdSet = new HashSet<Long>();
    Set<PushMessageCategory> pushMessageCategorySet = new HashSet<PushMessageCategory>();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (PushMessageDTO pushMessageDTO : pushMessageDTOList) {
        createPushMessage(pushMessageDTO, writer, pushMessageReceiverShopIdSet);
        pushMessageCategorySet.add(PushMessageCategory.valueOfPushMessageType(pushMessageDTO.getType()));
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    if (CollectionUtils.isNotEmpty(pushMessageReceiverShopIdSet) && CollectionUtils.isNotEmpty(pushMessageCategorySet) && isUpdateStatNumberInMemCache) {
      for (Long pushMessageReceiverShopId : pushMessageReceiverShopIdSet) {
        List<Long> userIds = this.getUserIds(pushMessageReceiverShopId);
        for (Long userId : userIds) {
          updatePushMessageCategoryStatNumberInMemCache(pushMessageReceiverShopId, userId, pushMessageCategorySet.toArray(new PushMessageCategory[pushMessageCategorySet.size()]));
        }
      }
    }
  }

  private void createPushMessage(PushMessageDTO pushMessageDTO, TxnWriter writer, Set<Long> pushMessageReceiverShopIdSet) throws Exception {
    PushMessage pushMessage = new PushMessage();
    pushMessage.fromDTO(pushMessageDTO);
    writer.save(pushMessage);
    pushMessageDTO.setId(pushMessage.getId());
    PushMessageReceiver pushMessageReceiver;
    //保存关联店铺

    PushMessageReceiverMatchRecord pushMessageReceiverMatchRecord = null;
    if (pushMessageDTO.getCurrentPushMessageReceiverDTO() != null) {
      pushMessageReceiver = new PushMessageReceiver();
      pushMessageReceiver.fromDTO(pushMessageDTO.getCurrentPushMessageReceiverDTO());
      pushMessageReceiver.setMessageId(pushMessage.getId());
      pushMessageReceiverShopIdSet.add(pushMessageReceiver.getShopId());
      writer.save(pushMessageReceiver);
      if (pushMessageDTO.getCurrentPushMessageReceiverDTO().getPushMessageReceiverMatchRecordDTO() != null) {
        pushMessageReceiverMatchRecord = new PushMessageReceiverMatchRecord();
        pushMessageReceiverMatchRecord.fromDTO(pushMessageDTO.getCurrentPushMessageReceiverDTO().getPushMessageReceiverMatchRecordDTO());
        pushMessageReceiverMatchRecord.setPushMessageReceiverId(pushMessageReceiver.getId());
        pushMessageReceiverMatchRecord.setMessageId(pushMessage.getId());
        writer.save(pushMessageReceiverMatchRecord);
      }
    } else {
      if (CollectionUtils.isNotEmpty(pushMessageDTO.getPushMessageReceiverDTOList())) {
        for (PushMessageReceiverDTO pushMessageReceiverDTO : pushMessageDTO.getPushMessageReceiverDTOList()) {
          pushMessageReceiver = new PushMessageReceiver();
          pushMessageReceiver.fromDTO(pushMessageReceiverDTO);
          pushMessageReceiver.setMessageId(pushMessage.getId());
          pushMessageReceiverShopIdSet.add(pushMessageReceiver.getShopId());
          writer.save(pushMessageReceiver);
          if (pushMessageReceiverDTO.getPushMessageReceiverMatchRecordDTO() != null) {
            pushMessageReceiverMatchRecord = new PushMessageReceiverMatchRecord();
            pushMessageReceiverMatchRecord.fromDTO(pushMessageReceiverDTO.getPushMessageReceiverMatchRecordDTO());
            pushMessageReceiverMatchRecord.setPushMessageReceiverId(pushMessageReceiver.getId());
            pushMessageReceiverMatchRecord.setMessageId(pushMessage.getId());
            writer.save(pushMessageReceiverMatchRecord);
          }
        }
      }
    }
    //保存源
    PushMessageSourceDTO pushMessageSourceDTO = pushMessageDTO.getPushMessageSourceDTO();
    if (pushMessageSourceDTO != null) {
      Set<PushMessageSourceType> needDisablePushMessageSourceTypeSet = new HashSet<PushMessageSourceType>();
      needDisablePushMessageSourceTypeSet.add(PushMessageSourceType.OVERDUE_APPOINT_TO_APP);
      needDisablePushMessageSourceTypeSet.add(PushMessageSourceType.SOON_EXPIRE_APPOINT_TO_APP);
      needDisablePushMessageSourceTypeSet.add(PushMessageSourceType.OVERDUE_APPOINT_TO_SHOP);
      needDisablePushMessageSourceTypeSet.add(PushMessageSourceType.SOON_EXPIRE_APPOINT_TO_SHOP);
      needDisablePushMessageSourceTypeSet.add(PushMessageSourceType.PRODUCT);
      needDisablePushMessageSourceTypeSet.add(PushMessageSourceType.PRE_BUY_ORDER_ITEM);
      needDisablePushMessageSourceTypeSet.add(PushMessageSourceType.MATCHING_RECOMMEND_CUSTOMER_SHOP);
      needDisablePushMessageSourceTypeSet.add(PushMessageSourceType.MATCHING_RECOMMEND_SUPPLIER_SHOP);

      if (needDisablePushMessageSourceTypeSet.contains(pushMessageSourceDTO.getType())) {//定时钟 使用的disable  不更新缓存  定时钟做完后全部更新
        List<PushMessageReceiver> receiverList = new ArrayList<PushMessageReceiver>();
        if (PushMessageSourceType.MATCHING_RECOMMEND_CUSTOMER_SHOP.equals(pushMessageSourceDTO.getType())
          || PushMessageSourceType.MATCHING_RECOMMEND_SUPPLIER_SHOP.equals(pushMessageSourceDTO.getType())) {
          for (Long pushMessageReceiverShopId : pushMessageReceiverShopIdSet) {
            receiverList.addAll(writer.getPushMessageReceiverBySourceId(pushMessageSourceDTO.getShopId(), pushMessageSourceDTO.getSourceId(), pushMessageReceiverShopId, pushMessageSourceDTO.getType()));
          }
        } else {
          receiverList.addAll(writer.getPushMessageReceiverBySourceId(pushMessageSourceDTO.getShopId(), pushMessageSourceDTO.getSourceId(), null, pushMessageSourceDTO.getType()));
        }
        if (CollectionUtils.isNotEmpty(receiverList)) {
          for (PushMessageReceiver receiver : receiverList) {
            receiver.setStatus(PushMessageReceiverStatus.DISABLED);
            writer.update(receiver);
          }
        }
      }
      PushMessageSource pushMessageSource = new PushMessageSource();
      pushMessageSource.fromDTO(pushMessageSourceDTO);
      pushMessageSource.setMessageId(pushMessage.getId());
      writer.save(pushMessageSource);
    }
  }

  @Override
  public List<PushMessageDTO> getPushMessageDTOBySourceId(Long sourceShopId, Long sourceId, PushMessageType pushMessageType, PushMessageSourceType pushMessageSourceType) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    List<PushMessage> pushMessageList = writer.getPushMessageBySourceId(sourceShopId, sourceId, pushMessageType, pushMessageSourceType);
    for (PushMessage p : pushMessageList) {
      pushMessageDTOList.add(p.toDTO());
    }
    return pushMessageDTOList;
  }


  @Override
  public void updatePushMessage(PushMessageDTO pushMessageDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      PushMessage pushMessage = writer.getById(PushMessage.class, pushMessageDTO.getId());
      pushMessage.fromDTO(pushMessageDTO);
      writer.update(pushMessage);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<PushMessageDTO> getTalkMessageList(TalkMessageCondition condition) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessage> pushMessages = writer.getTalkMessageList(condition);
    List<PushMessageDTO> pushMessageDTOs = new ArrayList<PushMessageDTO>();
    if (CollectionUtil.isNotEmpty(pushMessages)) {
      for (PushMessage p : pushMessages) {
        pushMessageDTOs.add(p.toDTO());
      }
    }
    return pushMessageDTOs;
  }

  @Override
  public int countShopTalkMessageList(String appUserNo,String vehicleNo, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countShopTalkMessageList(appUserNo,vehicleNo, shopId);
  }

  @Override
  public List<ShopTalkMessageDTO> getShopTalkMessageDTO(String appUserNo,String vehicleNo, Long shopId, int start, int limit) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ShopTalkMessage> talkMessageList = writer.getShopTalkMessage(appUserNo,vehicleNo, shopId, start, limit);
    List<ShopTalkMessageDTO> talkMessageDTOs = new ArrayList<ShopTalkMessageDTO>();
    if (CollectionUtil.isNotEmpty(talkMessageList)) {
      for (ShopTalkMessage talkMessage : talkMessageList) {
        talkMessageDTOs.add(talkMessage.toDTO());
      }
    }
    return talkMessageDTOs;
  }

//  @Override
//  public int countTalkMessageList(Long receiverId, Long shopId, PushMessageType... types) {
//    TxnWriter writer = txnDaoManager.getWriter();
//    return writer.countTalkMessageList(receiverId, shopId, types);
//  }


  @Override
  public List<PushMessageDTO> getLatestPushMessageDTOList(Long shopId, int start, int limit, PushMessageType... type) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> objectsList = writer.getLatestPushMessage(shopId, start, limit, type);
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    PushMessageDTO pushMessageDTO;
    PushMessageReceiver pushMessageReceiver;
    Object status;
    status = writer.begin();
    try {
      for (Object[] objects : objectsList) {
        if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
          PushMessage pushMessage = (PushMessage) objects[0];
          pushMessageDTO = pushMessage.toDTO();
          pushMessageReceiver = (PushMessageReceiver) objects[1];
          pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiver.toDTO());
          pushMessageDTOList.add(pushMessageDTO);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return pushMessageDTOList;
  }


  @Override
  public Integer countLatestPushMessage(Long shopId, PushMessageType... type) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countLatestPushMessage(shopId, type);
  }

  @Override
  public List<PushMessage> getLatestUnPushPushMessages(Long receiverId, PushMessageType... types) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getLatestUnPushPushMessages(receiverId, types);
  }

  @Override
  public PushMessageDTO getLatestPushMessageDTO(Long shopId, Long receiverId, ShopKind shopKind, PushMessageType... types) throws Exception {
    if (!ConfigUtils.isPushMessageSwitchOn() || ArrayUtils.isEmpty(types)) {
        LOG.info("pushMessageSwitch if off");
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    PushMessageReceiver pushMessageReceiver = null;
    try {
      Object[] objects = writer.getLatestPushMessage(shopId, receiverId, shopKind, types);
      if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {

        PushMessage pushMessage = (PushMessage) objects[0];
        PushMessageDTO pushMessageDTO = pushMessage.toDTO();
        pushMessageReceiver = (PushMessageReceiver) objects[1];
        boolean isShowStatusChanged = false;
        if (BcgogoConcurrentController.lock(ConcurrentScene.PUSH_MESSAGE_RECEIVER, pushMessageReceiver.getId())) {
          pushMessageReceiver = writer.getById(PushMessageReceiver.class, pushMessageReceiver.getId());
          if (!PushMessageShowStatus.ACTIVE.equals(pushMessageReceiver.getShowStatus())) {
            isShowStatusChanged = true;
          }
          pushMessageReceiver.setPushStatus(PushMessagePushStatus.PUSHING);
          pushMessageReceiver.setShowStatus(PushMessageShowStatus.ACTIVE);
          writer.update(pushMessageReceiver);
        }

        PushMessageReceiverRecord pushMessageReceiverRecord = new PushMessageReceiverRecord();
        pushMessageReceiverRecord.setPushTime(System.currentTimeMillis());
        pushMessageReceiverRecord.setPushMessageReceiverId(pushMessageReceiver.getId());
        pushMessageReceiverRecord.setMessageId(pushMessageDTO.getId());
        pushMessageReceiverRecord.setShopId(pushMessageDTO.getShopId());
        writer.save(pushMessageReceiverRecord);

        writer.commit(status);

        pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiver.toDTO());
        if (isShowStatusChanged) {
          List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopId);
          if (CollectionUtils.isNotEmpty(userIds)) {
            for (Long userId : userIds) {
              updatePushMessageCategoryStatNumberInMemCache(shopId, userId, PushMessageCategory.valueOfPushMessageType(pushMessage.getType()));
            }
          }
        }
        return pushMessageDTO;
      }
    } finally {
      writer.rollback(status);
      if (pushMessageReceiver != null) {
        BcgogoConcurrentController.release(ConcurrentScene.PUSH_MESSAGE_RECEIVER, pushMessageReceiver.getId());
      }
    }
    return null;
  }

  @Override
  public void readPushMessageReceiverBySourceId(Long shopId, Long sourceId, Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) throws Exception {
    updatePushMessageReceiverBySourceId(shopId, sourceId, PushMessageReceiverStatus.READ, pushMessageReceiverShopId, pushMessageSourceType);
  }

  private void updatePushMessageReceiverBySourceId(Long shopId, Long sourceId, PushMessageReceiverStatus pushMessageReceiverStatus, Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> objects = writer.getPushMessageAndReceiverBySourceId(shopId, sourceId, pushMessageReceiverShopId, pushMessageSourceType);
    if (CollectionUtils.isEmpty(objects)) return;
    Object status = writer.begin();
    try {
      Set<PushMessageCategory> pushMessageCategorySet = new HashSet<PushMessageCategory>();
      Set<Long> updateShopIdSet = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(objects)) {
        PushMessage pushMessage = null;
        PushMessageReceiver pushMessageReceiver = null;
        for (Object[] objs : objects) {
          if (!ArrayUtils.isEmpty(objs) && objs.length == 2 && objs[0] != null && objs[1] != null) {
            pushMessage = (PushMessage) objs[0];
            pushMessageReceiver = (PushMessageReceiver) objs[1];
            pushMessageReceiver.setStatus(pushMessageReceiverStatus);
            writer.update(pushMessageReceiver);
            PushMessageCategory pushMessageCategory = PushMessageCategory.valueOfPushMessageType(pushMessage.getType());
            if (pushMessageCategory != null) {
              pushMessageCategorySet.add(pushMessageCategory);
            }
            updateShopIdSet.add(pushMessageReceiver.getReceiverId());
          }
        }
      }
      writer.commit(status);

      if (CollectionUtils.isNotEmpty(updateShopIdSet) && CollectionUtils.isNotEmpty(pushMessageCategorySet)) {
        for (Long updateShopId : updateShopIdSet) {
          List<Long> updateUserIds = this.getUserIds(updateShopId);
          for (Long updateUserId : updateUserIds) {
            updatePushMessageCategoryStatNumberInMemCache(updateShopId, updateUserId, pushMessageCategorySet.toArray(new PushMessageCategory[pushMessageCategorySet.size()]));
          }
        }
      }

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void disabledPushMessageReceiverBySourceId(Long shopId, Long sourceId, Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) throws Exception {
    updatePushMessageReceiverBySourceId(shopId, sourceId, PushMessageReceiverStatus.DISABLED, pushMessageReceiverShopId, pushMessageSourceType);
  }

  public Map<Long, PushMessageSourceDTO> getUnreadPushMessageSourceMapBySourceIds(Set<Long> sourceIdSet, PushMessageSourceType... pushMessageSourceType) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessageSource> sourceList = writer.getUnreadPushMessageSourceBySourceIds(sourceIdSet, pushMessageSourceType);
    Map<Long, PushMessageSourceDTO> map = new HashMap<Long, PushMessageSourceDTO>();
    if (CollectionUtil.isEmpty(sourceIdSet)) {
      return map;
    }
    for (PushMessageSource source : sourceList) {
      map.put(source.getSourceId(), source.toDTO());
    }
    return map;
  }

  public Map<String, PushMessageSourceDTO> getCombinationKeyUnreadPushMessageSourceMapBySourceIds(Set<Long> sourceIdSet, PushMessageSourceType... pushMessageSourceType) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessageSource> sourceList = writer.getUnreadPushMessageSourceBySourceIds(sourceIdSet, pushMessageSourceType);
    Map<String, PushMessageSourceDTO> map = new HashMap<String, PushMessageSourceDTO>();
    if (CollectionUtil.isEmpty(sourceIdSet)) {
      return map;
    }
    for (PushMessageSource source : sourceList) {
      map.put(source.getShopId() + "_" + source.getSourceId(), source.toDTO());
    }
    return map;
  }


  @Override
  public void pushMessageMigration(PushMessageType[] pushMessageTypes, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    Long keepDate = ConfigUtils.getPushMessageKeepDay();
    if (keepDate == 0l) return;
    if (ArrayUtils.isEmpty(pushMessageTypes)) return;
    long startId = 0;
    int timeout = 0;
    while (true) {
      Object status = writer.begin();
      try {
        timeout++;
        List<PushMessage> pushMessageList = writer.getMovePushMessage(pushMessageTypes, System.currentTimeMillis() + keepDate, startId, pageSize);
        if (CollectionUtils.isEmpty(pushMessageList)) break;
        startId = pushMessageList.get(pushMessageList.size() - 1).getId();
        PushMessageTrace trace;
        for (PushMessage entity : pushMessageList) {
          trace = new PushMessageTrace(entity.toDTO());
          writer.saveOrUpdate(trace);
          writer.delete(entity);
        }
        if (timeout++ > 100) {
          LOG.error("push message migration timeout!");
          break;
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public void pushMessageReceiverMigration(int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    int timeout = 0;
    long startId = 0;
    while (true) {
      Object status = writer.begin();
      try {
        List<PushMessageReceiver> receiverList = writer.getMovePushMessageReceiver(startId, pageSize);
        if (CollectionUtils.isEmpty(receiverList)) break;
        startId = receiverList.get(receiverList.size() - 1).getId();
        PushMessageReceiverTrace trace;
        for (PushMessageReceiver entity : receiverList) {
          trace = new PushMessageReceiverTrace(entity.toDTO());
          writer.saveOrUpdate(trace);
          writer.delete(entity);
        }
        if (timeout++ > 100) {
          LOG.error("push message migration timeout!");
          break;
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public void pushMessageFeedbackRecordMigration(int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    int timeout = 0;
    long startId = 0;
    while (true) {
      Object status = writer.begin();
      try {
        List<PushMessageFeedbackRecord> recordList = writer.getMovePushMessageFeedbackRecord(startId, pageSize);
        if (CollectionUtils.isEmpty(recordList)) break;
        startId = recordList.get(recordList.size() - 1).getId();
        PushMessageFeedbackRecordTrace trace;
        for (PushMessageFeedbackRecord entity : recordList) {
          trace = new PushMessageFeedbackRecordTrace(entity.toDTO());
          writer.saveOrUpdate(trace);
          writer.delete(entity);
        }
        if (timeout++ > 100) {
          LOG.error("push message migration timeout!");
          break;
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public void movePushMessageReceiverRecordToTraceByPushTime(Long pushTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    int start = 0;
    int pageSize = 5000;
    while (true) {
      Object status = writer.begin();
      try {
        List<PushMessageReceiverRecord> pushMessageReceiverRecordList = writer.getMovePushMessageReceiverRecordListByPushTime(pushTime, start, pageSize);
        if (CollectionUtils.isEmpty(pushMessageReceiverRecordList)) {
          break;
        }
        start += pageSize;
        PushMessageReceiverRecordTrace pushMessageReceiverRecordTrace = null;
        for (PushMessageReceiverRecord pushMessageReceiverRecord : pushMessageReceiverRecordList) {
          pushMessageReceiverRecordTrace = new PushMessageReceiverRecordTrace();
          pushMessageReceiverRecordTrace.fromPushMessageReceiverRecordDTO(pushMessageReceiverRecord.toDTO());
          writer.save(pushMessageReceiverRecordTrace);
          writer.delete(pushMessageReceiverRecord);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }


  @Override
  public void processPushMessageFeedback(PushMessageFeedbackRecordDTO pushMessageFeedbackRecordDTO, PushMessageReceiverDTO pushMessageReceiverDTO) throws Exception {
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (BcgogoConcurrentController.lock(ConcurrentScene.PUSH_MESSAGE_RECEIVER, pushMessageReceiverDTO.getId())) {
        PushMessageReceiver pushMessageReceiver = writer.getById(PushMessageReceiver.class, pushMessageReceiverDTO.getId());
        pushMessageReceiver.setPushStatus(PushMessagePushStatus.UN_PUSH);
        pushMessageReceiver.setStatus(pushMessageReceiverDTO.getStatus());

        writer.update(pushMessageReceiver);
        if (pushMessageFeedbackRecordDTO != null) {
          PushMessageFeedbackRecord pushMessageFeedbackRecord = new PushMessageFeedbackRecord();
          pushMessageFeedbackRecord.fromDTO(pushMessageFeedbackRecordDTO);
          writer.save(pushMessageFeedbackRecord);

          if (PushMessageFeedbackType.WEB_HIT.equals(pushMessageFeedbackRecord.getType()) || PushMessageFeedbackType.CLIENT_HIT.equals(pushMessageFeedbackRecord.getType())) {
            PushMessage pushMessage = writer.getById(PushMessage.class, pushMessageFeedbackRecord.getMessageId());
            if (PushMessageType.BUYING_MATCH_ACCESSORY.equals(pushMessage.getType())) {
              //给对应配件店铺   补    资讯消息

              List<PushMessageSource> pushMessageSourceList = writer.getPushMessageSourceByMessageId(pushMessage.getId(), PushMessageSourceType.PRODUCT);
              if (CollectionUtils.isNotEmpty(pushMessageSourceList)) {
                PushMessageSource pushMessageSource = CollectionUtil.getFirst(pushMessageSourceList);
                List<PushMessageDTO> otherPushMessageDTOList = pushMessageService.getPushMessageDTOBySourceId(pushMessage.getShopId(), pushMessage.getRelatedObjectId(), PushMessageType.BUYING_INFORMATION, PushMessageSourceType.PRE_BUY_ORDER_ITEM);
                if (CollectionUtils.isNotEmpty(otherPushMessageDTOList)) {
                  PushMessageDTO otherPushMessageDTO = CollectionUtil.getFirst(otherPushMessageDTOList);
                  List<PushMessageReceiver> otherPushMessageReceiverList = writer.getPushMessageReceiverByMessageId(pushMessageSource.getShopId(), null, otherPushMessageDTO.getId());
                  if (CollectionUtils.isEmpty(otherPushMessageReceiverList)) {
                    PushMessageReceiver otherPushMessageReceiver = new PushMessageReceiver();
                    otherPushMessageReceiver.setMessageId(otherPushMessageDTO.getId());
                    otherPushMessageReceiver.setShopId(pushMessageSource.getShopId());
                    otherPushMessageReceiver.setShopKind(pushMessageReceiver.getShopKind());
                    otherPushMessageReceiver.setStatus(PushMessageReceiverStatus.UNREAD);
                    otherPushMessageReceiver.setPushStatus(PushMessagePushStatus.UN_PUSH);
                    otherPushMessageReceiver.setShowStatus(PushMessageShowStatus.UN_ACTIVE);
                    writer.save(otherPushMessageReceiver);
                  }
                } else {
                  IConfigService configService = ServiceManager.getService(IConfigService.class);
                  IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
                  PreBuyOrderItemDTO preBuyOrderItemDTO = preBuyOrderService.getPreBuyOrderItemDTOById(pushMessage.getRelatedObjectId());
                  ShopDTO otherSourceShopDTO = configService.getShopById(pushMessage.getShopId());

                  PreBuyOrderDTO preBuyOrderDTO = preBuyOrderService.getPreBuyOrderDTOById(pushMessageSource.getShopId(), preBuyOrderItemDTO.getPreBuyOrderId());

                  PushMessage otherPushMessage = new PushMessage();
                  otherPushMessage.setShopId(pushMessageSource.getShopId());
                  otherPushMessage.setRelatedObjectId(preBuyOrderItemDTO.getId());

                  Map<String, String> params = new HashMap<String, String>();
                  params.put(PushMessageParamsKeyConstant.PreBuyOrderId, preBuyOrderItemDTO.getPreBuyOrderId().toString());
                  params.put(PushMessageParamsKeyConstant.PreBuyOrderItemId, preBuyOrderItemDTO.getId().toString());
                  params.put(PushMessageParamsKeyConstant.ShopId, otherSourceShopDTO.getId().toString());
                  otherPushMessage.setParams(JsonUtil.mapToJson(params));

                  otherPushMessage.setLevel(PushMessageLevel.LOW.getValue());

                  otherPushMessage.setEndDate(preBuyOrderDTO.getEndDate());


                  PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
                  pushMessageVelocityContext.setShopDTO(otherSourceShopDTO);
                  pushMessageVelocityContext.setPreBuyOrderItemDTO(preBuyOrderItemDTO);
                  VelocityContext context = new VelocityContext();
                  context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);

                  PushMessageType pushMessageType = null;
                  String title = null, promptContent = null, content = null, contentText = null;
                  if (BusinessChanceType.Normal.equals(preBuyOrderDTO.getBusinessChanceType())) {
                    pushMessageType = PushMessageType.BUYING_INFORMATION;
                    title = PushMessagePromptTemplate.BUYING_INFORMATION_TITLE;
                    promptContent = String.format(PushMessagePromptTemplate.BUYING_INFORMATION_PROMPT_CONTENT, otherSourceShopDTO.getAreaName(), otherSourceShopDTO.getName(), preBuyOrderItemDTO.getProductInfo() + " " + preBuyOrderItemDTO.getAmount() + StringUtil.formateStr(preBuyOrderItemDTO.getUnit()));
                    content = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUYING_INFORMATION_CONTENT, "BUYING_INFORMATION_CONTENT");
                    contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUYING_INFORMATION_CONTENT_TEXT, "BUYING_INFORMATION_CONTENT_TEXT");
                  } else if (BusinessChanceType.Lack.equals(preBuyOrderDTO.getBusinessChanceType())) {
                    pushMessageType = PushMessageType.BUSINESS_CHANCE_LACK;
                    title = PushMessagePromptTemplate.BUSINESS_CHANCE_LACK_TITLE;
                    promptContent = String.format(PushMessagePromptTemplate.BUSINESS_CHANCE_LACK_PROMPT_CONTENT, otherSourceShopDTO.getAreaName(), otherSourceShopDTO.getName(), preBuyOrderItemDTO.getProductInfo());
                    content = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUSINESS_CHANCE_LACK_CONTENT, "BUSINESS_CHANCE_LACK_CONTENT");
                    contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUSINESS_CHANCE_LACK_CONTENT_TEXT, "BUSINESS_CHANCE_LACK_CONTENT_TEXT");
                  } else if (BusinessChanceType.SellWell.equals(preBuyOrderDTO.getBusinessChanceType())) {
                    pushMessageType = PushMessageType.BUSINESS_CHANCE_SELL_WELL;
                    title = PushMessagePromptTemplate.BUSINESS_CHANCE_SELL_WELL_TITLE;
                    promptContent = String.format(PushMessagePromptTemplate.BUSINESS_CHANCE_SELL_WELL_PROMPT_CONTENT, otherSourceShopDTO.getAreaName(), otherSourceShopDTO.getName(), preBuyOrderItemDTO.getProductInfo(), preBuyOrderItemDTO.getFuzzyAmountStr() + StringUtil.formateStr(preBuyOrderItemDTO.getUnit()));
                    content = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUSINESS_CHANCE_SELL_WELL_CONTENT, "BUSINESS_CHANCE_SELL_WELL_CONTENT");
                    contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUSINESS_CHANCE_SELL_WELL_CONTENT_TEXT, "BUSINESS_CHANCE_SELL_WELL_CONTENT_TEXT");
                  }
                  otherPushMessage.setType(pushMessageType);
                  otherPushMessage.setTitle(title);
                  otherPushMessage.setPromptContent(promptContent);
                  otherPushMessage.setContent(content);
                  otherPushMessage.setContentText(contentText);
                  otherPushMessage.setDeleted(DeletedType.FALSE);
                  otherPushMessage.setCreateTime(System.currentTimeMillis());
                  otherPushMessage.setCreatorType(OperatorType.SYS);
                  otherPushMessage.setCreatorId(-1l);
                  writer.save(otherPushMessage);

                  PushMessageSource otherPushMessageSource = new PushMessageSource();
                  otherPushMessageSource.setSourceId(preBuyOrderItemDTO.getId());
                  otherPushMessageSource.setCreateTime(System.currentTimeMillis());
                  otherPushMessageSource.setShopId(otherSourceShopDTO.getId());
                  otherPushMessageSource.setType(PushMessageSourceType.PRE_BUY_ORDER_ITEM);
                  otherPushMessageSource.setMessageId(otherPushMessage.getId());
                  writer.save(otherPushMessageSource);

                  PushMessageReceiver otherPushMessageReceiver = new PushMessageReceiver();
                  otherPushMessageReceiver.setShopId(pushMessageSource.getShopId());
                  otherPushMessageReceiver.setShopKind(pushMessageReceiver.getShopKind());
                  otherPushMessageReceiver.setMessageId(otherPushMessage.getId());
                  otherPushMessageReceiver.setStatus(PushMessageReceiverStatus.UNREAD);
                  otherPushMessageReceiver.setPushStatus(PushMessagePushStatus.UN_PUSH);
                  otherPushMessageReceiver.setShowStatus(PushMessageShowStatus.UN_ACTIVE);
                  writer.save(otherPushMessageReceiver);

                  PushMessageReceiverMatchRecord otherPushMessageReceiverMatchRecord = new PushMessageReceiverMatchRecord();
                  otherPushMessageReceiverMatchRecord.setCreateTime(System.currentTimeMillis());
                  otherPushMessageReceiverMatchRecord.setPushMessageReceiverId(otherPushMessageReceiver.getId());
                  otherPushMessageReceiverMatchRecord.setMessageId(otherPushMessage.getId());
                  writer.save(otherPushMessageReceiverMatchRecord);

                }
              }
            }
          }
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
      if (pushMessageReceiverDTO != null) {
        BcgogoConcurrentController.release(ConcurrentScene.PUSH_MESSAGE_RECEIVER, pushMessageReceiverDTO.getId());
      }
    }
  }

  @Override
  public void createPushMessageDTOByQuotedPreBuyOrderItemDTO(ShopDTO shopDTO, Long preBuyOrderEndDate, QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO, QuotedPreBuyOrderDTO quotedPreBuyOrderDTO, PushMessageType pushMessageType) throws Exception {
    String title = null, promptContent = null, content = null, contentText = null;
    PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
    pushMessageVelocityContext.setShopDTO(shopDTO);
    quotedPreBuyOrderItemDTO.setProductDTOWithOutUnit(ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(quotedPreBuyOrderItemDTO.getProductId(), quotedPreBuyOrderItemDTO.getShopId()));
    pushMessageVelocityContext.setQuotedPreBuyOrderItemDTO(quotedPreBuyOrderItemDTO);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
    Map<String, String> params = new HashMap<String, String>();
    params.put(PushMessageParamsKeyConstant.QuotedPreBuyOrderItemId, quotedPreBuyOrderItemDTO.getId().toString());
    params.put(PushMessageParamsKeyConstant.ProductLocalInfoId, quotedPreBuyOrderItemDTO.getProductId().toString());
    params.put(PushMessageParamsKeyConstant.ProductShopId, shopDTO.getId().toString());
    params.put(PushMessageParamsKeyConstant.ShopId, shopDTO.getId().toString());

    if (PushMessageType.QUOTED_BUYING_INFORMATION.equals(pushMessageType)) {
      params.put(PushMessageParamsKeyConstant.PreBuyOrderId, quotedPreBuyOrderItemDTO.getPreBuyOrderId().toString());
      title = PushMessagePromptTemplate.QUOTED_BUYING_INFORMATION_TITLE;
      promptContent = String.format(PushMessagePromptTemplate.QUOTED_BUYING_INFORMATION_PROMPT_CONTENT, shopDTO.getAreaName(), shopDTO.getName());
      content = generateMsgUsingVelocity(context, PushMessageContentTemplate.QUOTED_BUYING_INFORMATION_CONTENT, "QUOTED_BUYING_INFORMATION_CONTENT");
      contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.QUOTED_BUYING_INFORMATION_CONTENT_TEXT, "QUOTED_BUYING_INFORMATION_CONTENT_TEXT");
    } else {
      title = PushMessagePromptTemplate.RECOMMEND_ACCESSORY_BY_QUOTED_TITLE;
      promptContent = String.format(PushMessagePromptTemplate.RECOMMEND_ACCESSORY_BY_QUOTED_PROMPT_CONTENT, shopDTO.getAreaName(), shopDTO.getName(), quotedPreBuyOrderItemDTO.getProductInfo());
      content = generateMsgUsingVelocity(context, PushMessageContentTemplate.RECOMMEND_ACCESSORY_BY_QUOTED_CONTENT, "RECOMMEND_ACCESSORY_BY_QUOTED_CONTENT");
      contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.RECOMMEND_ACCESSORY_BY_QUOTED_CONTENT_TEXT, "RECOMMEND_ACCESSORY_BY_QUOTED_CONTENT_TEXT");
    }
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setShopId(shopDTO.getId());
    pushMessageDTO.setType(pushMessageType);
    pushMessageDTO.setLevel(PushMessageLevel.HIGH);
    pushMessageDTO.setTitle(title);
    pushMessageDTO.setPromptContent(promptContent);
    pushMessageDTO.setContent(content);
    pushMessageDTO.setContentText(contentText);

    pushMessageDTO.setParams(JsonUtil.mapToJson(params));
    pushMessageDTO.setEndDate(preBuyOrderEndDate);
    pushMessageDTO.setCreateTime(System.currentTimeMillis());

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setSourceId(quotedPreBuyOrderItemDTO.getId());
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setShopId(shopDTO.getId());
    pushMessageSourceDTO.setType(PushMessageSourceType.QUOTED_PRE_BUY_ORDER_ITEM);
    pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);
    pushMessageDTO.setCurrentPushMessageReceiverDTO(new PushMessageReceiverDTO(quotedPreBuyOrderDTO.getCustomerShopId(), shopDTO.getShopKind(), quotedPreBuyOrderDTO.getCustomerShopId(), OperatorType.SHOP));
    this.createPushMessage(pushMessageDTO, true);
  }

  /**
   * 有的定位到userId 级别   有的是shop   所以这边 memCache 中  以uerId 级别 为准
   *
   * @param shopId
   * @param userId
   * @param pushMessageCategorys
   */
  @Override
  public void updatePushMessageCategoryStatNumberInMemCache(Long shopId, Long userId, PushMessageCategory... pushMessageCategorys) {
    List<PushMessageCategory> pushMessageCategoryList = new ArrayList<PushMessageCategory>(Arrays.asList(pushMessageCategorys));
    pushMessageCategoryList.removeAll(Collections.singleton(null));

    if (CollectionUtils.isEmpty(pushMessageCategoryList)) return;
    UserGroupDTO userGroupDTO = ServiceManager.getService(IUserGroupService.class).getUserGroupByUserId(userId);
    if (userGroupDTO == null) {
      LOG.warn("get userGroup by useId:[{}] is null!", userId);
      return;
    }
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getSimpleShopById(shopId);
    if (shopDTO == null) {
      LOG.warn("get shopDTO by shopId:[{}] is null!", shopId);
      return;
    }
    for (PushMessageCategory pushMessageCategory : pushMessageCategoryList) {
      String key = getMemCacheKey(shopId, userId, pushMessageCategory, MemcachePrefix.receiverMessage);
      Map<PushMessageReceiverStatus, Integer> statMap = new HashMap<PushMessageReceiverStatus, Integer>();
      TxnWriter writer = txnDaoManager.getWriter();
      List<PushMessageType> pushMessageTypeList = new ArrayList<PushMessageType>();
      pushMessageTypeList.addAll(PushMessageCategory.pushMessageCategoryTypeMap.get(pushMessageCategory));
      filterPushMessageTypes(userGroupDTO.getId(), shopDTO.getShopVersionId(), pushMessageTypeList);
      if (CollectionUtils.isNotEmpty(pushMessageTypeList)) {
        Integer unreadNum = writer.countPushMessageByStatus(pushMessageTypeList, shopId, PushMessageReceiverStatus.UNREAD, shopId, userId);
        statMap.put(PushMessageReceiverStatus.UNREAD, unreadNum);
      } else {
        statMap.put(PushMessageReceiverStatus.UNREAD, 0);
      }
      MemCacheAdapter.set(key, statMap);
    }
  }

  @Override
  public void filterPushMessageTypes(Long userGroupId, Long shopVersionId, List<PushMessageType> pushMessageTypeList) {
    if (CollectionUtils.isEmpty(pushMessageTypeList)) return;

    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE")) {
      pushMessageTypeList.remove(PushMessageType.ACCESSORY);
      pushMessageTypeList.remove(PushMessageType.ACCESSORY_PROMOTIONS);
      pushMessageTypeList.remove(PushMessageType.RECOMMEND_ACCESSORY_BY_QUOTED);
      pushMessageTypeList.remove(PushMessageType.BUYING_MATCH_ACCESSORY);
    }
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER")) {
      pushMessageTypeList.remove(PushMessageType.BUYING_INFORMATION);
      pushMessageTypeList.remove(PushMessageType.BUSINESS_CHANCE_SELL_WELL);
      pushMessageTypeList.remove(PushMessageType.BUSINESS_CHANCE_LACK);
      pushMessageTypeList.remove(PushMessageType.QUOTED_BUYING_IGNORED);
    }
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.AUTOACCESSORYONLINE.SALES_ACCESSORY.BASE")) {
      pushMessageTypeList.remove(PushMessageType.ACCESSORY_MATCH_RESULT);
    }
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.AUTOACCESSORYONLINE.MY_PREBUYORDER")) {
      pushMessageTypeList.remove(PushMessageType.QUOTED_BUYING_INFORMATION);
      pushMessageTypeList.remove(PushMessageType.BUYING_INFORMATION_MATCH_RESULT);
    }
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.SCHEDULE.MESSAGE_CENTER.APPLY.CUSTOMER_APPLY")) {
      pushMessageTypeList.remove(PushMessageType.APPLY_CUSTOMER);
    }
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.SCHEDULE.MESSAGE_CENTER.APPLY.SUPPLIER_APPLY")) {
      pushMessageTypeList.remove(PushMessageType.APPLY_SUPPLIER);
    }
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.SCHEDULE.MESSAGE_CENTER.NOTICE.ASSOCIATION_NOTICE")) {
      pushMessageTypeList.remove(PushMessageType.CANCEL_ASSOCIATION_NOTICE);
      pushMessageTypeList.remove(PushMessageType.ASSOCIATION_REJECT_NOTICE);
      pushMessageTypeList.remove(PushMessageType.CUSTOMER_ACCEPT_TO_SUPPLIER);
      pushMessageTypeList.remove(PushMessageType.SUPPLIER_ACCEPT_TO_CUSTOMER);
      pushMessageTypeList.remove(PushMessageType.SUPPLIER_ACCEPT_TO_SUPPLIER);
      pushMessageTypeList.remove(PushMessageType.CUSTOMER_ACCEPT_TO_CUSTOMER);
    }
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.VEHICLE_CONSTRUCTION.APPOINT_ORDER_LIST")) {
      pushMessageTypeList.remove(PushMessageType.APP_CANCEL_APPOINT);
      pushMessageTypeList.remove(PushMessageType.APP_APPLY_APPOINT);
      pushMessageTypeList.remove(PushMessageType.OVERDUE_APPOINT_TO_SHOP);
      pushMessageTypeList.remove(PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP);
      pushMessageTypeList.remove(PushMessageType.SYS_ACCEPT_APPOINT);
    }
    if (!privilegeService.verifierUserGroupResource(shopVersionId, userGroupId, ResourceType.menu,
      "WEB.SCHEDULE.ENQUIRY_LIST")) {
      pushMessageTypeList.remove(PushMessageType.APP_SUBMIT_ENQUIRY);
    }
    //      pushMessageTypeList.remove(PushMessageType.MATCHING_RECOMMEND_SUPPLIER); 汽配汽修都有
    //版本权限过滤
    if (ConfigUtils.isWholesalerVersion(shopVersionId)) {//汽配需要过滤
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
      pushMessageTypeList.remove(PushMessageType.ACCESSORY);
      pushMessageTypeList.remove(PushMessageType.ACCESSORY_PROMOTIONS);
      pushMessageTypeList.remove(PushMessageType.BUYING_MATCH_ACCESSORY);
      pushMessageTypeList.remove(PushMessageType.APPLY_SUPPLIER);

      pushMessageTypeList.remove(PushMessageType.APP_CANCEL_APPOINT);
      pushMessageTypeList.remove(PushMessageType.APP_APPLY_APPOINT);
      pushMessageTypeList.remove(PushMessageType.OVERDUE_APPOINT_TO_SHOP);
      pushMessageTypeList.remove(PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP);
      pushMessageTypeList.remove(PushMessageType.SYS_ACCEPT_APPOINT);

      pushMessageTypeList.remove(PushMessageType.SUPPLIER_ACCEPT_TO_CUSTOMER);
      pushMessageTypeList.remove(PushMessageType.CUSTOMER_ACCEPT_TO_CUSTOMER);

      pushMessageTypeList.remove(PushMessageType.PROMOTIONS_MESSAGE);
      pushMessageTypeList.remove(PushMessageType.WARN_MESSAGE);
    } else {
      pushMessageTypeList.remove(PushMessageType.MATCHING_RECOMMEND_CUSTOMER);
      pushMessageTypeList.remove(PushMessageType.BUYING_INFORMATION);
      pushMessageTypeList.remove(PushMessageType.QUOTED_BUYING_IGNORED);
      pushMessageTypeList.remove(PushMessageType.ACCESSORY_MATCH_RESULT);
      pushMessageTypeList.remove(PushMessageType.APPLY_CUSTOMER);

      pushMessageTypeList.remove(PushMessageType.CUSTOMER_ACCEPT_TO_SUPPLIER);
      pushMessageTypeList.remove(PushMessageType.SUPPLIER_ACCEPT_TO_SUPPLIER);
    }
    //--------需求特意过滤--------------
    pushMessageTypeList.remove(PushMessageType.ACCESSORY);
    pushMessageTypeList.remove(PushMessageType.ACCESSORY_PROMOTIONS);
    pushMessageTypeList.remove(PushMessageType.ACCESSORY_MATCH_RESULT);
    pushMessageTypeList.remove(PushMessageType.BUYING_MATCH_ACCESSORY);
  }

  @Override
  public void readPushMessageReceiverByPushMessageType(Long shopId, Long userId, PushMessageType... pushMessageTypes) throws Exception {
    if (ArrayUtils.isEmpty(pushMessageTypes)) return;

    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessageReceiver> pushMessageReceiverList = writer.getUnreadPushMessageReceiver(shopId, userId, pushMessageTypes);
    if (CollectionUtils.isEmpty(pushMessageReceiverList)) return;
    Object status = writer.begin();
    try {
      Set<PushMessageCategory> pushMessageCategorySet = new HashSet<PushMessageCategory>();
      Set<Long> updateShopIdSet = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(pushMessageReceiverList)) {
        for (PushMessageReceiver pushMessageReceiver : pushMessageReceiverList) {
          pushMessageReceiver.setStatus(PushMessageReceiverStatus.READ);
          writer.update(pushMessageReceiver);
          updateShopIdSet.add(pushMessageReceiver.getShopId());
        }
      }
      writer.commit(status);
      for (PushMessageType pushMessageType : pushMessageTypes) {
        PushMessageCategory pushMessageCategory = PushMessageCategory.valueOfPushMessageType(pushMessageType);
        if (pushMessageCategory != null) {
          pushMessageCategorySet.add(pushMessageCategory);
        }
      }
      if (CollectionUtils.isNotEmpty(updateShopIdSet) && CollectionUtils.isNotEmpty(pushMessageCategorySet)) {
        for (Long updateShopId : updateShopIdSet) {
          List<Long> updateUserIds = this.getUserIds(updateShopId);
          for (Long updateUserId : updateUserIds) {
            updatePushMessageCategoryStatNumberInMemCache(updateShopId, updateUserId, pushMessageCategorySet.toArray(new PushMessageCategory[pushMessageCategorySet.size()]));
          }
        }
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void readPushMessageReceiverById(Long shopId, Long... ids) throws Exception {
    updatePushMessageReceiverById(shopId, PushMessageReceiverStatus.READ, ids);
  }

  @Override
  public Map<PushMessageReceiverStatus, Integer> getPushMessageCategoryStatNumberInMemCache(PushMessageCategory pushMessageCategory, Long shopId, Long userId) {
    String key = getMemCacheKey(shopId, userId, pushMessageCategory, MemcachePrefix.receiverMessage);
    Map<PushMessageReceiverStatus, Integer> statMap = (Map<PushMessageReceiverStatus, Integer>) MemCacheAdapter.get(key);
    if (statMap == null) {
      updatePushMessageCategoryStatNumberInMemCache(shopId, userId, pushMessageCategory);
      statMap = (Map<PushMessageReceiverStatus, Integer>) MemCacheAdapter.get(key);
    }
    return statMap;
  }

  @Override
  public void deletePushMessageReceiverById(Long shopId, Long... ids) throws Exception {
    updatePushMessageReceiverById(shopId, PushMessageReceiverStatus.DISABLED, ids);
  }


  private void updatePushMessageReceiverById(Long shopId, PushMessageReceiverStatus pushMessageReceiverStatus, Long... ids) throws Exception {
    if (shopId == null) throw new BcgogoException("shop id is null");
    if (ArrayUtils.isEmpty(ids)) throw new BcgogoException("PushMessageReceiver ids is empty");
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> objects = writer.getPushMessageAndReceivers(shopId, ids);
    if (CollectionUtils.isEmpty(objects)) return;
    Object status = writer.begin();
    try {
      Set<PushMessageCategory> pushMessageCategorySet = new HashSet<PushMessageCategory>();
      Set<Long> updateShopIdSet = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(objects)) {
        PushMessage pushMessage = null;
        PushMessageReceiver pushMessageReceiver = null;
        for (Object[] objs : objects) {
          if (!ArrayUtils.isEmpty(objs) && objs.length == 2 && objs[0] != null && objs[1] != null) {
            pushMessage = (PushMessage) objs[0];
            pushMessageReceiver = (PushMessageReceiver) objs[1];
            pushMessageReceiver.setStatus(pushMessageReceiverStatus);

            writer.update(pushMessageReceiver);
            PushMessageCategory pushMessageCategory = PushMessageCategory.valueOfPushMessageType(pushMessage.getType());
            if (pushMessageCategory != null) {
              pushMessageCategorySet.add(pushMessageCategory);
            }
            updateShopIdSet.add(pushMessageReceiver.getShopId());
          }
        }
      }
      writer.commit(status);

      if (CollectionUtils.isNotEmpty(pushMessageCategorySet) && CollectionUtils.isNotEmpty(updateShopIdSet)) {
        for (Long updateShopId : updateShopIdSet) {
          List<Long> updateUserIds = this.getUserIds(updateShopId);
          for (Long updateUserId : updateUserIds) {
            updatePushMessageCategoryStatNumberInMemCache(updateShopId, updateUserId, pushMessageCategorySet.toArray(new PushMessageCategory[pushMessageCategorySet.size()]));
          }
        }
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<PushMessageDTO> searchReceiverPushMessageDTOList(List<PushMessageType> pushMessageTypeList, SearchMessageCondition searchMessageCondition) throws ParseException {
    if (CollectionUtils.isNotEmpty(pushMessageTypeList)) {
      List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
      PushMessageDTO pushMessageDTO = null;
      PushMessage pushMessage = null;
      PushMessageReceiver pushMessageReceiver = null;
      TxnWriter writer = txnDaoManager.getWriter();
      List<Object[]> objectList = writer.searchReceiverPushMessageDTO(pushMessageTypeList, searchMessageCondition);
      if (CollectionUtils.isNotEmpty(objectList)) {
        for (Object[] objects : objectList) {
          if (!ArrayUtils.isEmpty(objects) && objects.length == 2 && objects[0] != null && objects[1] != null) {
            pushMessage = (PushMessage) objects[0];
            pushMessageReceiver = (PushMessageReceiver) objects[1];
            pushMessageDTO = pushMessage.toDTO();
            pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiver.toDTO());
            generatePushMessageContentRedirectUrl(pushMessageDTO);
            pushMessageDTOList.add(pushMessageDTO);
          }
        }
        return pushMessageDTOList;
      }
    }
    return null;
  }

  @Override
  public Integer countReceiverPushMessageDTO(List<PushMessageType> pushMessageTypeList, SearchMessageCondition searchMessageCondition) throws ParseException {
    if (CollectionUtils.isNotEmpty(pushMessageTypeList)) {
      TxnWriter writer = txnDaoManager.getWriter();
      return writer.countReceiverPushMessageDTO(pushMessageTypeList, searchMessageCondition);
    }
    return 0;
  }

  private void generatePushMessageContentRedirectUrl(PushMessageDTO pushMessageDTO) {
    if (pushMessageDTO != null && StringUtils.isNotBlank(pushMessageDTO.getContent())) {
      Map<String, String> paramsMap = JsonUtil.jsonToStringMap(pushMessageDTO.getParams());
      if (PushMessageType.getHaveRedirectShopUrlPushMessageTypes().contains(pushMessageDTO.getType())) {
        String shopId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.ShopId));
        if (StringUtils.isNotBlank(shopId)) {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectShopUrl.getKey(), String.format(PushMessageRedirectUrl.RedirectShopUrl.getUrl(), shopId)));
        } else {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectShopUrl.getKey(), "javascript:void(0);"));
        }
      }
      if (PushMessageType.getHaveRedirectShopProductDetailUrlPushMessageTypes().contains(pushMessageDTO.getType())) {
        String productShopId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.ProductShopId));
        String productLocalInfoId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.ProductLocalInfoId));
        String quotedPreBuyOrderItemId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.QuotedPreBuyOrderItemId));
        if (StringUtils.isNotBlank(productShopId) && StringUtils.isNotBlank(productLocalInfoId)) {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey(), String.format(PushMessageRedirectUrl.RedirectShopProductDetailUrl.getUrl(), productShopId, productLocalInfoId, quotedPreBuyOrderItemId)));
        } else {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey(), "javascript:void(0);"));
        }
      }
      if (PushMessageType.getHaveRedirectBuyingInformationDetailUrlPushMessageTypes().contains(pushMessageDTO.getType())) {
        String preBuyOrderItemId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.PreBuyOrderItemId));
        if (StringUtils.isNotBlank(preBuyOrderItemId)) {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectBuyInformationDetailUrl.getKey(), String.format(PushMessageRedirectUrl.RedirectBuyInformationDetailUrl.getUrl(), preBuyOrderItemId)));
        } else {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectBuyInformationDetailUrl.getKey(), "javascript:void(0);"));
        }
      }
      if (PushMessageType.getHaveRedirectPreBuyOrderUrlPushMessageTypes().contains(pushMessageDTO.getType())) {
        String preBuyOrderId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.PreBuyOrderId));
        if (StringUtils.isNotBlank(preBuyOrderId)) {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectPreBuyOrderUrl.getKey(), String.format(PushMessageRedirectUrl.RedirectPreBuyOrderUrl.getUrl(), preBuyOrderId)));
        } else {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectPreBuyOrderUrl.getKey(), "javascript:void(0);"));
        }
      }
      if (PushMessageType.getHaveRedirectCustomerUrlPushMessageTypes().contains(pushMessageDTO.getType())) {
        String customerId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.CustomerId));
        if (StringUtils.isNotBlank(customerId)) {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectCustomerDetailUrl.getKey(), String.format(PushMessageRedirectUrl.RedirectCustomerDetailUrl.getUrl(), customerId)));
        } else {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectCustomerDetailUrl.getKey(), "javascript:void(0);"));
        }
      }

      if (PushMessageType.getHaveRedirectSupplierUrlPushMessageTypes().contains(pushMessageDTO.getType())) {
        String supplierId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.SupplierId));
        if (StringUtils.isNotBlank(supplierId)) {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectSupplierDetailUrl.getKey(), String.format(PushMessageRedirectUrl.RedirectSupplierDetailUrl.getUrl(), supplierId)));
        } else {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.RedirectSupplierDetailUrl.getKey(), "javascript:void(0);"));
        }
      }
      if (PushMessageType.getSendSmsPushMessageTypes().contains(pushMessageDTO.getType())) {
        String mobile = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.Mobile));
        String appUserName = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.AppUserName));
        if (StringUtils.isNotBlank(mobile)) {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.SendSms2ShopUrl.getKey(), String.format(PushMessageRedirectUrl.SendSms2ShopUrl.getUrl(), mobile, appUserName)));
        } else {
          pushMessageDTO.setContent(pushMessageDTO.getContent().replace(PushMessageRedirectUrl.SendSms2ShopUrl.getKey(), "javascript:void(0);"));
        }
      }
    }
  }

  @Override
  public void generatePushMessagePromptRedirectUrl(PushMessageDTO pushMessageDTO, boolean isClient) {
    if (pushMessageDTO != null) {
      Map<String, String> paramsMap = JsonUtil.jsonToStringMap(pushMessageDTO.getParams());
      switch (pushMessageDTO.getType()) {
        case ACCESSORY:
        case ACCESSORY_PROMOTIONS:
        case RECOMMEND_ACCESSORY_BY_QUOTED:
        case BUYING_MATCH_ACCESSORY:
        case ACCESSORY_MATCH_RESULT:
          String productShopId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.ProductShopId));
          String productLocalInfoId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.ProductLocalInfoId));
          String quotedPreBuyOrderItemId = StringUtil.formateStr(paramsMap.get(PushMessageParamsKeyConstant.QuotedPreBuyOrderItemId));
          if (StringUtils.isNotBlank(productShopId) && StringUtils.isNotBlank(productLocalInfoId))
            pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.RedirectShopProductDetailUrl.getUrl(), productShopId, productLocalInfoId, quotedPreBuyOrderItemId));
          break;
        case BUYING_INFORMATION:
        case BUSINESS_CHANCE_SELL_WELL:
        case BUSINESS_CHANCE_LACK:
          String preBuyOrderItemId = paramsMap.get(PushMessageParamsKeyConstant.PreBuyOrderItemId);
          if (StringUtils.isNotBlank(preBuyOrderItemId))
            pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.RedirectBuyInformationDetailUrl.getUrl(), preBuyOrderItemId));
          break;
        case QUOTED_BUYING_INFORMATION:
        case BUYING_INFORMATION_MATCH_RESULT:
          String preBuyOrderId = paramsMap.get(PushMessageParamsKeyConstant.PreBuyOrderId);
          if (StringUtils.isNotBlank(preBuyOrderId))
            pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.RedirectPreBuyOrderUrl.getUrl(), preBuyOrderId));
          break;
        case QUOTED_BUYING_IGNORED:
          quotedPreBuyOrderItemId = paramsMap.get(PushMessageParamsKeyConstant.QuotedPreBuyOrderItemId);
          if (StringUtils.isNotBlank(quotedPreBuyOrderItemId))
            pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.RedirectIgnoredQuotedPreBuyOrderUrl.getUrl(), quotedPreBuyOrderItemId));
          break;
        case APPLY_CUSTOMER:
        case APPLY_SUPPLIER:
          if (pushMessageDTO.getRelatedObjectId() != null)
            pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.RedirectRelatedApplyPushMessageUrl.getUrl(), pushMessageDTO.getRelatedObjectId()));
          break;
        case MATCHING_RECOMMEND_CUSTOMER:
        case MATCHING_RECOMMEND_SUPPLIER:
          String shopId = paramsMap.get(PushMessageParamsKeyConstant.ShopId);
          if (StringUtils.isNotBlank(shopId))
            pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.RedirectShopUrl.getUrl(), shopId));
          break;
        case PURCHASE_SELLER_STOCK:
          pushMessageDTO.setRedirectUrl(ClientConstant.getUrlByPushMessageSourceType(PushMessageSourceType.PURCHASE_SELLER_STOCK));
          break;
        case PURCHASE_SELLER_DISPATCH:
          pushMessageDTO.setRedirectUrl(ClientConstant.getUrlByPushMessageSourceType(PushMessageSourceType.PURCHASE_SELLER_DISPATCH));
          break;
        case PURCHASE_SELLER_REFUSED:
          pushMessageDTO.setRedirectUrl(ClientConstant.getUrlByPushMessageSourceType(PushMessageSourceType.PURCHASE_SELLER_REFUSED));
          break;
        case SALE_NEW:
          pushMessageDTO.setRedirectUrl(ClientConstant.getUrlByPushMessageSourceType(PushMessageSourceType.SALE_NEW));
          break;
        case SALE_RETURN_NEW:
          pushMessageDTO.setRedirectUrl(ClientConstant.getUrlByPushMessageSourceType(PushMessageSourceType.SALE_RETURN_NEW));
          break;
        case ANNOUNCEMENT:
          pushMessageDTO.setRedirectUrl(PushMessageRedirectUrl.RedirectAnnouncementUrl.getUrl());
          break;
        case FESTIVAL:
          pushMessageDTO.setRedirectUrl(PushMessageRedirectUrl.RedirectSMSWriteUrl.getUrl());
          break;
        case APP_SUBMIT_ENQUIRY:
          String enquiryId = paramsMap.get(PushMessageParamsKeyConstant.enquiryId);
          if (StringUtils.isNotBlank(enquiryId))
            pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.RedirectEnquiryPushMessageUrl.getUrl(), enquiryId));
          break;
        //故障消息
        case VEHICLE_FAULT_2_SHOP:
          //保养里程
        case APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP:
          //保养时间
        case APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP:
          //保险时间
        case APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP:
          //验车时间
        case APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP:
          //保养里程
        case APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP:
          //保养时间
        case APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP:
          //保险时间
        case APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP:
          //验车时间
        case APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP:
          pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.BackToPushMessageCenterPage.getUrl(), PushMessageCategory.BuyingInformationStationMessage.toString()));
          break;
        case APP_APPLY_APPOINT:
        case OVERDUE_APPOINT_TO_SHOP:
        case SOON_EXPIRE_APPOINT_TO_SHOP:
        case APP_CANCEL_APPOINT:
        case SYS_ACCEPT_APPOINT:
          String appointOrderId = paramsMap.get(PushMessageParamsKeyConstant.AppointOrderId);
          if (StringUtils.isNotBlank(appointOrderId))
            pushMessageDTO.setRedirectUrl(String.format(PushMessageRedirectUrl.RedirectAppointOrderDetailUrl.getUrl(), appointOrderId));
          break;

      }
      if (StringUtil.isNotEmpty(pushMessageDTO.getRedirectUrl())) {
        if (isClient) {
          pushMessageDTO.setRedirectUrl(pushMessageDTO.getRedirectUrl().replace(".do", ".client"));
        } else {
          pushMessageDTO.setRedirectUrl(pushMessageDTO.getRedirectUrl().replace(".client", ".do"));
        }
      }
    }
  }

  @Override
  public List<PushMessageDTO> searchSenderPushMessageDTOs(SearchMessageCondition condition) throws Exception {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    TxnWriter writer = txnDaoManager.getWriter();

    List<PushMessage> pushMessageList = writer.searchSenderPushMessages(condition);
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    if (CollectionUtils.isNotEmpty(pushMessageList)) {
      Set<Long> pushMessageIdSet = new HashSet<Long>();
      for (PushMessage dto : pushMessageList) {
        pushMessageDTOList.add(dto.toDTO());
        pushMessageIdSet.add(dto.getId());
      }
      Set<Long> customerIdSet = new HashSet<Long>();
      List<PushMessageReceiver> pushMessageReceiverList = writer.getPushMessageReceiverByMessageId(null, null, pushMessageIdSet.toArray(new Long[pushMessageIdSet.size()]));
      Map<Long, Set<Long>> localReceiverIdMap = new HashMap<Long, Set<Long>>();
      for (PushMessageReceiver receiver : pushMessageReceiverList) {
        if (localReceiverIdMap.get(receiver.getMessageId()) == null) {
          localReceiverIdMap.put(receiver.getMessageId(), new HashSet<Long>());
        }
        localReceiverIdMap.get(receiver.getMessageId()).add(receiver.getLocalReceiverId());
        customerIdSet.add(receiver.getLocalReceiverId());
      }
      Set<Long> localReceiverIdSet;
      Map<Long, CustomerDTO> customerDTOMap = customerService.getCustomerByIdSet(condition.getShopId(), customerIdSet);
      for (PushMessageDTO pushMessageDTO : pushMessageDTOList) {
        pushMessageDTO.generateValidStatusStr();
        localReceiverIdSet = localReceiverIdMap.get(pushMessageDTO.getId());
        if (CollectionUtil.isEmpty(localReceiverIdSet)) {
          LOG.warn("get PushMessageReceiver by messageId:{} is empty!", pushMessageDTO.getId());
          continue;
        }
        List<CustomerDTO> senderCustomerDTOList = new ArrayList<CustomerDTO>();
        for (Long localReceiverId : localReceiverIdSet) {
          CustomerDTO customerDTO = customerDTOMap.get(localReceiverId);
          if (customerDTO == null) {
            LOG.warn("get customer by localReceiverId:{} is null!", localReceiverId);
            continue;
          }
          senderCustomerDTOList.add(customerDTO);
        }
        pushMessageDTO.setSenderCustomerDTOList(senderCustomerDTOList);
      }
    }

    return pushMessageDTOList;
  }

  @Override
  public Integer countSenderMessages(SearchMessageCondition condition) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countSenderPushMessages(condition);
  }

  @Override
  public List<PushMessageReceiverDTO> getPushMessageReceiverByMsgId(Long... ids) throws Exception {
    if (ArrayUtils.isEmpty(ids)) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMsgId(ids);
    List<PushMessageReceiverDTO> receiverDTOs = new ArrayList<PushMessageReceiverDTO>();
    if (CollectionUtil.isNotEmpty(receiverList)) {
      for (PushMessageReceiver receiver : receiverList) {
        receiverDTOs.add(receiver.toDTO());
      }
    }
    return receiverDTOs;
  }

  @Override
  public void startPushMessageScheduleWork() throws Exception {
    LOG.info("startPushMessageScheduleWork ...");
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    PushMessageType[] messageTypes = PushMessageType.getAppUserPushMessage();
    int start = 0;
    int limit = 1000;
    while (true) {
      List<AppUserDTO> appUserDTOs = appUserService.getAppUserByUserType(AppUserType.MIRROR, start, limit);
      if (CollectionUtil.isEmpty(appUserDTOs)) {
        break;
      }
      for (AppUserDTO appUserDTO : appUserDTOs) {
        List<PushMessage> pushMessages = getLatestUnPushPushMessages(appUserDTO.getId(), messageTypes);
        if (CollectionUtil.isEmpty(pushMessages)) {
          continue;
        }
        List<MQMessageItemDTO> itemDTOs = new ArrayList<MQMessageItemDTO>();
        for (PushMessage pushMessage : pushMessages) {
          MQMessageItemDTO itemDTO = pushMessage.toMQMessageItemDTO();
          itemDTO.setToUserName(appUserDTO.getUserNo());
          itemDTO.setFromUserName(pushMessage.getCreator());
          itemDTOs.add(itemDTO);
        }
        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setAppUserNo(appUserDTO.getUserNo());
        messageDTO.setSendTime(System.currentTimeMillis());
        messageDTO.setItemDTOs(itemDTOs);
//        RmiClientPushTools.push(messageDTO);
      }
      start += limit;
    }
  }

  @Override
  public void saveOrUpdatePushMessageReceiver(PushMessageReceiverDTO receiverDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    PushMessageReceiver receiver = null;
    try {
      if (receiverDTO.getId() != null) {
        receiver = writer.getById(PushMessageReceiver.class, receiverDTO.getId());
      } else {
        receiver = new PushMessageReceiver();
      }
      receiver.fromDTO(receiverDTO);
      writer.saveOrUpdate(receiver);
      writer.commit(status);
      receiverDTO.setId(receiver.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void handleMsgDateAck(MQAckMessageDTO ackMessageDTO) throws Exception {
    List<MQAckMessageItemDTO> itemDTOs = ackMessageDTO.getItemDTOs();
    if (CollectionUtil.isEmpty(itemDTOs)) return;
    List<Long> messageIds = new ArrayList<Long>();
    for (MQAckMessageItemDTO itemDTO : itemDTOs) {
      messageIds.add(itemDTO.getId());
    }
    List<PushMessageReceiverDTO> receiverDTOs = getPushMessageReceiverByMsgId(messageIds.toArray(new Long[itemDTOs.size()]));
    if (CollectionUtil.isEmpty(receiverDTOs)) return;
    for (PushMessageReceiverDTO receiverDTO : receiverDTOs) {
      receiverDTO.setPushStatus(PushMessagePushStatus.PUSHED);
      receiverDTO.setStatus(PushMessageReceiverStatus.READ);
      saveOrUpdatePushMessageReceiver(receiverDTO);
    }
  }

  //              ackMessageDTO = ServiceManager.getService(IPushMessageService.class).handleTalkMessage(messageDTO);
//              msgProtocol = new MMsgProtocol(MQConstant.MIRROR_MSG_DATA_ACK, ByteUtil.complementZero(JsonUtil.objectToJson(ackMessageDTO).getBytes("UTF-8")));
//              MProtocol protocol = new MProtocol(MProtocolType.MIRROR, msgProtocol.toProtocol());
//              session.write(protocol);

  @Override
  public MQAckMessageDTO handleTalkMessage(MQMessageDTO mqMessageDTO) throws Exception {
    List<MQMessageItemDTO> mqMessageItemDTOs = mqMessageDTO.getItemDTOs();
    if (CollectionUtil.isEmpty(mqMessageItemDTOs)) return null;
    List<MQAckMessageItemDTO> ackItemDTOs = new ArrayList<MQAckMessageItemDTO>();
    for (MQMessageItemDTO itemDTO : mqMessageItemDTOs) {
      MQAckMessageItemDTO ackItemDTO = new MQAckMessageItemDTO();
      ackItemDTO.setId(itemDTO.getMsgId());
      ackItemDTOs.add(ackItemDTO);
    }
    MQAckMessageDTO ackMessageDTO = new MQAckMessageDTO();
    ackMessageDTO.setSendTime(System.currentTimeMillis());
    ackMessageDTO.setItemDTOs(ackItemDTOs);
    return ackMessageDTO;
  }

  @Override
  public List<ShopTalkMessageDTO> getShopTalkMessageDTOByAppUserNo(Long shopId, String appUserNo) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ShopTalkMessage> messageList = writer.getShopTalkMessageDTOByAppUserNo(shopId, appUserNo);
    List<ShopTalkMessageDTO> messageDTOs = new ArrayList<ShopTalkMessageDTO>();
    if (CollectionUtil.isNotEmpty(messageList)) {
      for (ShopTalkMessage message : messageList) {
        messageDTOs.add(message.toDTO());
      }
    }
    return messageDTOs;
  }


  @Override
  public void saveOrUpdateShopTalkMessage(ShopTalkMessageDTO messageDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    ShopTalkMessage talkMessage = null;
    try {
      if (messageDTO.getId() != null) {
        talkMessage = writer.getById(ShopTalkMessage.class, messageDTO.getId());
      } else {
        talkMessage = new ShopTalkMessage();
      }
      talkMessage.fromDTO(messageDTO);
      writer.saveOrUpdate(talkMessage);
      writer.commit(status);
      messageDTO.setId(talkMessage.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveWXShopTalkMessageDTO(MQTalkMessageDTO talkMessageDTO) {
    String appUserNo = talkMessageDTO.getAppUserNo();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
    Long shopId = appUserDTO.getRegistrationShopId();
    ShopTalkMessageDTO shopTalkMessageDTO = CollectionUtil.getFirst(getShopTalkMessageDTOByAppUserNo(shopId, appUserNo));
    if (shopTalkMessageDTO == null) {
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(appUserNo, shopId));
      AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
      if (appUserCustomerDTO == null) {
        LOG.error("AppUserCustomerDTO if appUsrNo{} does not existed",appUserNo);
        return;
      }
      CustomerDTO customerDTO = customerService.getCustomerById(appUserCustomerDTO.getCustomerId());
      shopTalkMessageDTO = new ShopTalkMessageDTO();
      shopTalkMessageDTO.setShopId(shopId);
      shopTalkMessageDTO.setAppUserNo(appUserNo);
      shopTalkMessageDTO.setFromUserName(talkMessageDTO.getFromUserName());
      shopTalkMessageDTO.setCustomer(customerDTO.getName());
      shopTalkMessageDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      saveOrUpdateShopTalkMessage(shopTalkMessageDTO);
    }
    shopTalkMessageDTO.setSendTime(System.currentTimeMillis());
    shopTalkMessageDTO.setContent(talkMessageDTO.getContent());
    saveOrUpdateShopTalkMessage(shopTalkMessageDTO);
  }


}
