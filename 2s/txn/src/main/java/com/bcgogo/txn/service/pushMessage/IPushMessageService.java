package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.mq.message.MQAckMessageDTO;
import com.bcgogo.mq.message.MQMessageDTO;
import com.bcgogo.mq.message.MQTalkMessageDTO;
import com.bcgogo.remind.dto.message.SearchMessageCondition;
import com.bcgogo.txn.dto.QuotedPreBuyOrderDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderItemDTO;
import com.bcgogo.txn.dto.pushMessage.*;
import com.bcgogo.txn.model.pushMessage.PushMessage;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 上午10:44
 */
public interface IPushMessageService {
  /**
   * create  pushMessage PushMessageReceiver pushMessageSource
   *
   * @param pushMessageDTO PushMessageDTO
   * @return pushMessageId
   */
  Long createPushMessage(PushMessageDTO pushMessageDTO,boolean isUpdateStatNumberInMemCache) throws Exception;

  void createPushMessageList(List<PushMessageDTO> pushMessageDTOList,boolean isUpdateStatNumberInMemCache) throws Exception;

  /**
   * 只拿出推送消息 不更新状态
   *
   * @param shopId Long
   * @param start  int
   * @param limit  int
   * @param type   PushMessageType[]
   */
  List<PushMessageDTO> getLatestPushMessageDTOList(Long shopId, int start, int limit, PushMessageType... type);

  Integer countLatestPushMessage(Long shopId,PushMessageType... type);

  /**
   * @param sourceShopId Long
   * @param sourceId     Long
   * @param pushMessageSourceType         PushMessageSourceType
   * @return List<PushMessageDTO>
   */
  List<PushMessageDTO> getPushMessageDTOBySourceId(Long sourceShopId, Long sourceId,PushMessageType pushMessageType, PushMessageSourceType pushMessageSourceType);

  List<PushMessageDTO> getTalkMessageList(TalkMessageCondition condition);

//  int countTalkMessageList(Long receiverId ,Long shopId,PushMessageType... types);

  List<ShopTalkMessageDTO> getShopTalkMessageDTO(String appUserNo,String vehicleNo, Long shopId, int start, int limit);

  int countShopTalkMessageList(String appUserNo,String vehicleNo, Long shopId);

  void updatePushMessage(PushMessageDTO pushMessageDTO);

  /**
   * 获取推送消息 更新状态成推送中
   *
   * @param shopId   Long
   * @param receiverId   Long
   * @param shopKind ShopKind
   * @param types    PushMessageType[]
   * @return PushMessageDTO
   * @throws Exception
   */
  PushMessageDTO getLatestPushMessageDTO(Long shopId,Long receiverId, ShopKind shopKind, PushMessageType... types) throws Exception;

  List<PushMessage> getLatestUnPushPushMessages(Long receiverId, PushMessageType... types) throws Exception ;

  void disabledPushMessageReceiverBySourceId(Long shopId, Long sourceId,Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) throws Exception;

  Map<Long, PushMessageSourceDTO> getUnreadPushMessageSourceMapBySourceIds(Set<Long> sourceIdSet, PushMessageSourceType... pushMessageSourceType);
  Map<String, PushMessageSourceDTO> getCombinationKeyUnreadPushMessageSourceMapBySourceIds(Set<Long> sourceIdSet, PushMessageSourceType... pushMessageSourceType);

  void pushMessageMigration(PushMessageType[] pushMessageTypes,int size);

  void pushMessageReceiverMigration(int pageSize);

  void pushMessageFeedbackRecordMigration(int size);

  void movePushMessageReceiverRecordToTraceByPushTime(Long pushTime) throws Exception;

  void processPushMessageFeedback(PushMessageFeedbackRecordDTO pushMessageFeedbackRecordDTO, PushMessageReceiverDTO pushMessageReceiverDTO) throws Exception;



  void deletePushMessageReceiverById(Long shopId,Long... ids) throws Exception;


  void updatePushMessageCategoryStatNumberInMemCache(Long shopId, Long userId, PushMessageCategory... pushMessageCategorys);

  Map<PushMessageReceiverStatus, Integer> getPushMessageCategoryStatNumberInMemCache(PushMessageCategory pushMessageCategory, Long shopId, Long userId);

  void createPushMessageDTOByQuotedPreBuyOrderItemDTO(ShopDTO shopDTO, Long preBuyOrderEndDate, QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO, QuotedPreBuyOrderDTO quotedPreBuyOrderDTO,PushMessageType pushMessageType) throws Exception;

  List<PushMessageDTO> searchReceiverPushMessageDTOList(List<PushMessageType> pushMessageTypeList,SearchMessageCondition searchMessageCondition) throws ParseException;

  Integer countReceiverPushMessageDTO(List<PushMessageType> pushMessageTypeList,SearchMessageCondition searchMessageCondition) throws ParseException;

  void filterPushMessageTypes(Long userGroupId, Long shopVersionId, List<PushMessageType> pushMessageTypeList);

  void readPushMessageReceiverById(Long shopId, Long... ids) throws Exception;

  void readPushMessageReceiverByPushMessageType(Long shopId, Long userId, PushMessageType... pushMessageTypes) throws Exception;

  void generatePushMessagePromptRedirectUrl(PushMessageDTO pushMessageDTO,boolean isClient);

  List<PushMessageDTO> searchSenderPushMessageDTOs(SearchMessageCondition condition) throws Exception;

  Integer countSenderMessages(SearchMessageCondition condition) throws Exception;

  List<PushMessageReceiverDTO> getPushMessageReceiverByMsgId(Long... ids) throws Exception;

  void readPushMessageReceiverBySourceId(Long shopId, Long sourceId,Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) throws Exception;

  void saveOrUpdatePushMessageReceiver(PushMessageReceiverDTO receiverDTO);

  void startPushMessageScheduleWork() throws Exception;

  void handleMsgDateAck(MQAckMessageDTO ackMessageDTO) throws Exception;

  MQAckMessageDTO handleTalkMessage(MQMessageDTO mqMessageDTO) throws Exception;

  void saveOrUpdateShopTalkMessage(ShopTalkMessageDTO messageDTO);

  List<ShopTalkMessageDTO> getShopTalkMessageDTOByAppUserNo(Long shopId,String appUserNo);

 void saveWXShopTalkMessageDTO(MQTalkMessageDTO talkMessageDTO);

}
