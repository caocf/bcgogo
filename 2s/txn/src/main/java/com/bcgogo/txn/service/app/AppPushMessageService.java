package com.bcgogo.txn.service.app;

import com.bcgogo.api.PushAppMessageDTO;
import com.bcgogo.api.response.MessageResponse;
import com.bcgogo.constant.BMSConstant;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.model.pushMessage.PushMessageSource;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-9-15
 * Time: 下午3:54
 */
@Component
public class AppPushMessageService implements IAppPushMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(AppPushMessageService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;
  private static  int count=0;

  @Override
  public PushMessageType[] getPushMessageTypes(String[] types) {
    PushMessageType[] typesArray;
    if (types.length == 1 && StringUtil.isEmptyAppGetParameter(types[0])) {
      typesArray = PushMessageType.getAppUserPushMessage();
    } else {
      typesArray = new PushMessageType[types.length];
      int i = 0;
      for (String str : types) {
        typesArray[i++] = PushMessageType.valueOf(str);
      }
    }
    return typesArray;
  }

//  @Override
//  public void pushMessage() {
//    TxnWriter writer = txnDaoManager.getWriter();
//    int limit = 1000;
//    do {
//      List<Object[]> objList = writer.getAppUserUnReadPushMessage(limit, PushMessageType.getAppUserPushMessage());
//      if (CollectionUtil.isEmpty(objList)) {
//        break;
//      }
//      Map<Long, List<PushMessage>> pushMessageMap = new HashMap<Long, List<PushMessage>>();
//      for (Object[] obj : objList) {
//        PushMessageReceiver receiver = (PushMessageReceiver) obj[0];
//        PushMessage pushMessage = (PushMessage) obj[1];
//        List<PushMessage> pushMessageList = pushMessageMap.get(receiver.getId());
//        if (CollectionUtil.isEmpty(pushMessageList)) {
//          pushMessageList = new ArrayList<PushMessage>();
//          pushMessageMap.put(receiver.getReceiverId(),pushMessageList);
//        }
//        pushMessageList.add(pushMessage);
//      }
//      for (Long receiverId : pushMessageMap.keySet()) {
//        List<PushMessage> pushMessageList = pushMessageMap.get(receiverId);
//        List<PushAppMessageDTO> messageDTOs = handleMessage(pushMessageList);
//        AppMessage appMessage = new AppMessage();
//        appMessage.setMsgId(12356254585471548L);
//        appMessage.setCreateTime(System.currentTimeMillis());
//        appMessage.setMessageList(messageDTOs);
//        String msg = JsonUtil.objectToJson(appMessage);
//        String subject = BMSConstant.PREFIX_SUBJECT_PUSH + "test1155665";
//        MQProductHelper.produce(subject, msg);
//        count++;
//      }
//      LOG.info("pushMessage count:{}",count);
//      break;
//    } while (true);
//
//  }


  public MessageResponse getPollingMessage(Long appUserId, int limit, PushMessageType... types) {
    MessageResponse response = new MessageResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_MESSAGE_SUCCESS));
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessage> pushMessageList = writer.getUnReadPushMessageByReceiverId(appUserId, limit, types);
    response.setMessageList(handleMessage(pushMessageList));
    return response;
  }

  private List<PushAppMessageDTO> handleMessage(List<PushMessage> pushMessageList) {
    if (CollectionUtil.isEmpty(pushMessageList)) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    Set<Long> ids = new HashSet<Long>();
    Set<Long> appVehicleNextMaintainMileagePushMessageIds = new HashSet<Long>();
    Set<Long> shopFinishAppointPushMessageIds = new HashSet<Long>();
    for (PushMessage pushMessage : pushMessageList) {
      ids.add(pushMessage.getId());
      if (pushMessage.getType() == PushMessageType.APP_VEHICLE_MAINTAIN_MILEAGE) {
        appVehicleNextMaintainMileagePushMessageIds.add(pushMessage.getId());
      }
      if (pushMessage.getType() == PushMessageType.SHOP_FINISH_APPOINT) {
        shopFinishAppointPushMessageIds.add(pushMessage.getId());
      }
    }
    Map<Long, Long> isCommentedMap = writer.getPushMessageIdCommentedIdMap(shopFinishAppointPushMessageIds);
    List<PushAppMessageDTO> messageList = new ArrayList<PushAppMessageDTO>();
    for (PushMessage pushMessage : pushMessageList) {
      messageList.add(pushMessage.toPushAppMessageDTO(isCommentedMap.get(pushMessage.getId()) != null));
    }
    Set<Long> vehicleIds = new HashSet<Long>();
    if (CollectionUtil.isNotEmpty(appVehicleNextMaintainMileagePushMessageIds)) {
      List<PushMessageSource> sourceList = writer.getPushMessageSourcesByMessageIds(appVehicleNextMaintainMileagePushMessageIds);
      for (PushMessageSource source : sourceList) {
        vehicleIds.add(source.getSourceId());
      }
    }
//    ServiceManager.getService(IAppUserVehicleObdService.class).updateNextMaintainMileagePushMessageRemindLimit(vehicleIds);
//    Object status = writer.begin();
//    try {
//      if (CollectionUtil.isNotEmpty(ids)) {
//        writer.updatePushMessageReceiverStatusByMessageId(PushMessageReceiverStatus.READ, ids.toArray(new Long[ids.size()]));
//      }
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
    return messageList;
  }


}
