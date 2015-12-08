package com.bcgogo.txn.service.messageCenter;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.txn.message.MessageType;
import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.enums.txn.message.Status;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.remind.dto.MessageReceiverDTO;
import com.bcgogo.remind.dto.message.MessageDTO;
import com.bcgogo.remind.dto.message.Operator;
import com.bcgogo.remind.dto.message.SearchMessageCondition;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PromotionsProductDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.message.*;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.model.pushMessage.PushMessageSource;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Lijinlong
 * Date: 12-11-9
 * Time: 上午10:38
 */
@Component
public class MessageService extends AbstractMessageService implements IMessageService {

  private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;


  @Override
  public void saveMessage(MessageDTO messageDTO) throws Exception {
    if (messageDTO == null) return;
    if (CollectionUtil.isEmpty(messageDTO.getMessageReceiverDTOList())) return;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopDTO shopDTO = configService.getShopById(messageDTO.getShopId());
      PushMessage pushMessage = new PushMessage();
      pushMessage.fromMessageDTO(messageDTO);
      pushMessage.setDeleted(DeletedType.FALSE);
      writer.save(pushMessage);
      PushMessageReceiver pushMessageReceiver;
      List<Long> receiverUserIds;
      List<MessageReceiverDTO> messageReceiverDTOList = messageDTO.getMessageReceiverDTOList();
      for (MessageReceiverDTO messageReceiverDTO : messageReceiverDTOList) {
        receiverUserIds = this.getUserIds(messageReceiverDTO.getReceiverShopId());
        for (Long id : receiverUserIds) {
          pushMessageReceiver = new PushMessageReceiver();
          pushMessageReceiver.setShopId(messageReceiverDTO.getReceiverShopId());
          pushMessageReceiver.setStatus(PushMessageReceiverStatus.UNREAD);
          pushMessageReceiver.setPushStatus(PushMessagePushStatus.UN_PUSH);
          pushMessageReceiver.setShowStatus(PushMessageShowStatus.ACTIVE);
          pushMessageReceiver.setMessageId(pushMessage.getId());
          pushMessageReceiver.setReceiverId(id);
          pushMessageReceiver.setReceiverType(OperatorType.USER);
          pushMessageReceiver.setLocalReceiverId(messageReceiverDTO.getReceiverId());
          pushMessageReceiver.setLocalReceiverName(messageReceiverDTO.getReceiverName());
          pushMessageReceiver.setSenderId(messageReceiverDTO.getSenderId());
          pushMessageReceiver.setSenderName(messageReceiverDTO.getSenderName());
          pushMessageReceiver.setShopKind(shopDTO.getShopKind());
          writer.save(pushMessageReceiver);
        }
      }
      writer.commit(status);
      messageDTO.setId(pushMessage.getId());
      //update memCache
      for (MessageReceiverDTO messageReceiverDTO : messageReceiverDTOList) {
        receiverUserIds = this.getUserIds(messageReceiverDTO.getReceiverShopId());
        for (Long id : receiverUserIds) {
          pushMessageService.updatePushMessageCategoryStatNumberInMemCache(messageReceiverDTO.getReceiverShopId(), id,PushMessageCategory.valueOfPushMessageType(pushMessage.getType()));
        }
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteSenderPushMessage(Long senderShopId, Long senderUserId, Long... messageIds) throws BcgogoException {
    if (senderShopId == null) throw new BcgogoException("sender shop id is null");
    if (senderUserId == null) throw new BcgogoException("sender user  id is null");
    if (ArrayUtils.isEmpty(messageIds)) throw new BcgogoException("message ids are empty");
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      PushMessage pushMessage ;
      for (Long messageId : messageIds) {
        pushMessage = writer.getById(PushMessage.class, messageId);
        if (pushMessage == null) {
          LOG.warn("get message by id:{} is null!", messageId);
          return;
        }
        pushMessage.setDeleted(DeletedType.TRUE);
        writer.update(pushMessage);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }



  @Override
  public MessageDTO getMessageById(Long id) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    PushMessage message = writer.getById(PushMessage.class,id);
    return message.toMessageDTO();
  }
  @Override
  public Result sendPromotionMsg(Result result,MessageDTO messageDTO) throws Exception {
    Long shopId=messageDTO.getShopId();
    messageDTO.setType(MessageType.PROMOTIONS_MESSAGE);
    List<MessageReceiverDTO> messageReceiverDTOList = new ArrayList<MessageReceiverDTO>();
    if (StringUtils.isNotBlank(messageDTO.getMessageReceivers())) {
      String[] customerIds = StringUtils.split(messageDTO.getMessageReceivers(), ",");
      Set<Long> receiverIdSet = new HashSet<Long>();
      for (String customerId : customerIds) {
        if (StringUtils.isNotBlank(customerId)) {
          receiverIdSet.add(Long.valueOf(customerId));
        }
      }
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
      Map<Long, CustomerDTO> customerDTOMap = customerService.getCustomerByIdSet(shopId, receiverIdSet);
      Set<Long> customerShopIds = new HashSet<Long>();
      if(!customerDTOMap.isEmpty() && CollectionUtil.isNotEmpty(customerDTOMap.values())){
        for(CustomerDTO customerDTO : customerDTOMap.values()){
          if(customerDTO != null && customerDTO.getCustomerShopId() != null){
            customerShopIds.add(customerDTO.getCustomerShopId());
          }
        }
      }
      Map<Long,SupplierDTO> supplierDTOMap =  supplierService.getSupplierByNativeShopIds(shopId,customerShopIds.toArray(new Long[customerShopIds.size()]));
      for (Long receiverId : receiverIdSet) {
        CustomerDTO customerDTO = customerDTOMap.get(receiverId);
        if (customerDTO != null) {
          MessageReceiverDTO messageReceiverDTO = new MessageReceiverDTO();
          messageReceiverDTO.setReceiverId(receiverId);
          messageReceiverDTO.setReceiverName(customerDTO.getName());
          messageReceiverDTO.setReceiverShopId(customerDTO.getCustomerShopId());
          SupplierDTO supplierDTO = supplierDTOMap.get(customerDTO.getCustomerShopId());
          if (supplierDTO != null) {
            messageReceiverDTO.setSenderName(supplierDTO.getName());
            messageReceiverDTO.setSenderId(supplierDTO.getId());
          }
          messageReceiverDTOList.add(messageReceiverDTO);
        }
      }
    }
    messageDTO.setMessageReceiverDTOList(messageReceiverDTOList);
    String promotionsIdStr =messageDTO.getPromotionsIdStr();
    if(StringUtil.isEmpty(promotionsIdStr)){
      return result.LogErrorMsg("无商品。");
    }
    List<PromotionsProductDTO> promotionsProductDTOs=ServiceManager.getService(IPromotionsService.class).getPromotionsProductDTOByPromotionsId(shopId, NumberUtil.longValue(promotionsIdStr));
    if(CollectionUtil.isEmpty(promotionsProductDTOs)){
      return result.LogErrorMsg("没有促销中的商品，无法发送促销消息。");
    }
    StringBuffer sb=new StringBuffer();
    if(CollectionUtil.isNotEmpty(promotionsProductDTOs)){
      for (PromotionsProductDTO p: promotionsProductDTOs){
          sb.append(p.getProductLocalInfoIdStr()).append(",");
      }
    }
    messageDTO.setProductIds(sb.toString());
    saveMessage(messageDTO);
    return result;
  }


  @Override
  public void moveInviteAndNoticeAndMessageToPushMessage() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Message> messageList = writer.getAllMessages();
      if (CollectionUtils.isNotEmpty(messageList)) {
        for (Message message : messageList) {
          if(message.getType()==null) continue;
          PushMessage pushMessage = new PushMessage();
          if (Status.ACTIVE.equals(message.getStatus())) {
            pushMessage.setDeleted(DeletedType.FALSE);
          } else {
            pushMessage.setDeleted(DeletedType.TRUE);
          }
          pushMessage.setValidDateFrom(message.getValidDateFrom());
          pushMessage.setValidTimePeriod(message.getValidTimePeriod());
          pushMessage.setValidDateTo(message.getValidDateTo());
          pushMessage.setContent(message.getContent());
          pushMessage.setContentText(StringUtil.Html2Text(message.getContent()));
          pushMessage.setType(PushMessageType.valueOf(message.getType().toString()));
          pushMessage.setCreator(message.getEditor());
          pushMessage.setCreatorId(message.getEditorId());
          pushMessage.setCreateTime(message.getEditDate());
          pushMessage.setCreatorType(OperatorType.SHOP);
          pushMessage.setShopId(message.getShopId());
          Map<String,String> paramsMap = new HashMap<String, String>();
          paramsMap.put(PushMessageParamsKeyConstant.ProductLocalInfoIds,message.getProductIds());
          pushMessage.setParams(JsonUtil.mapToJson(paramsMap));
          writer.save(pushMessage);

          List<MessageReceiver> messageReceiverList = writer.getAllMessageReceiverByMessageId(message.getId());
          if (CollectionUtils.isNotEmpty(messageReceiverList)) {
            for (MessageReceiver messageReceiver : messageReceiverList) {
              List<MessageUserReceiver> messageUserReceiverList = writer.getAllMessageUserReceiverByMessageReceiverId(messageReceiver.getId());
              if (CollectionUtils.isNotEmpty(messageUserReceiverList)) {
                for (MessageUserReceiver messageUserReceiver : messageUserReceiverList) {
                  PushMessageReceiver pushMessageReceiver = new PushMessageReceiver();
                  pushMessageReceiver.setShopId(messageReceiver.getReceiverShopId());
                  ShopDTO shopDTO = configService.getShopById(pushMessageReceiver.getShopId());
                  if (ReceiverStatus.DELETED.equals(messageUserReceiver.getStatus())) {
                    pushMessageReceiver.setStatus(PushMessageReceiverStatus.DISABLED);
                  } else {
                    pushMessageReceiver.setStatus(PushMessageReceiverStatus.valueOf(messageUserReceiver.getStatus().toString()));
                  }
                  pushMessageReceiver.setPushStatus(PushMessagePushStatus.UN_PUSH);
                  pushMessageReceiver.setShowStatus(PushMessageShowStatus.ACTIVE);
                  pushMessageReceiver.setMessageId(pushMessage.getId());
                  pushMessageReceiver.setReceiverId(messageUserReceiver.getReceiverUserId());
                  pushMessageReceiver.setReceiverType(OperatorType.USER);
                  pushMessageReceiver.setLocalReceiverId(messageReceiver.getReceiverId());
                  pushMessageReceiver.setLocalReceiverName(messageReceiver.getReceiverName());
                  pushMessageReceiver.setSenderId(messageReceiver.getSenderId());
                  pushMessageReceiver.setSenderName(messageReceiver.getSenderName());
                  pushMessageReceiver.setShopKind(shopDTO==null?null:shopDTO.getShopKind());
                  writer.save(pushMessageReceiver);
                }
              }
            }
          }

        }
      }
      List<Notice> noticeList = writer.getAllNotices();
      if (CollectionUtils.isNotEmpty(noticeList)) {
        for (Notice notice : noticeList) {
          if(notice.getNoticeType()==null) continue;
          PushMessage pushMessage = new PushMessage();
          if (Status.ACTIVE.equals(notice.getStatus())) {
            pushMessage.setDeleted(DeletedType.FALSE);
          } else {
            pushMessage.setDeleted(DeletedType.TRUE);
          }
          pushMessage.setContent(notice.getContent());
          pushMessage.setContentText(StringUtil.Html2Text(notice.getContent()));
          pushMessage.setType(PushMessageType.valueOf(notice.getNoticeType().toString()));
          pushMessage.setCreateTime(notice.getRequestTime());
          pushMessage.setCreatorType(OperatorType.SHOP);
          pushMessage.setShopId(notice.getSenderShopId());

          Map<String,String> paramsMap = new HashMap<String, String>();
          if(PushMessageType.CUSTOMER_ACCEPT_TO_SUPPLIER.equals(pushMessage.getType()) || PushMessageType.SUPPLIER_ACCEPT_TO_SUPPLIER.equals(pushMessage.getType())){
            paramsMap.put(PushMessageParamsKeyConstant.SimilarCustomerIds,notice.getReceiverIds());//是否存在相似相同的客户 需要合并
            if(StringUtils.isNotBlank(notice.getReceiverIds())){
              if(notice.getReceiverIds().indexOf(",")>-1){
                paramsMap.put(PushMessageParamsKeyConstant.CustomerId,notice.getReceiverIds().split(",")[0]);
              }else{
                paramsMap.put(PushMessageParamsKeyConstant.CustomerId,notice.getReceiverIds());
              }
            }
          }else if(PushMessageType.SUPPLIER_ACCEPT_TO_CUSTOMER.equals(pushMessage.getType()) || PushMessageType.CUSTOMER_ACCEPT_TO_CUSTOMER.equals(pushMessage.getType())){
            paramsMap.put(PushMessageParamsKeyConstant.SimilarSupplierIds,notice.getReceiverIds());//是否存在相似相同的供应商 需要合并
            if(StringUtils.isNotBlank(notice.getReceiverIds())){
              if(notice.getReceiverIds().indexOf(",")>-1){
                paramsMap.put(PushMessageParamsKeyConstant.SupplierId,notice.getReceiverIds().split(",")[0]);
              }else{
                paramsMap.put(PushMessageParamsKeyConstant.SupplierId,notice.getReceiverIds());
              }
            }
          }
          pushMessage.setParams(JsonUtil.mapToJson(paramsMap));
          writer.save(pushMessage);

          List<NoticeReceiver> noticeReceiverList = writer.getAllNoticeReceiverByNoticeId(notice.getId());
          if (CollectionUtils.isNotEmpty(noticeReceiverList)) {
            for (NoticeReceiver noticeReceiver : noticeReceiverList) {
              PushMessageReceiver pushMessageReceiver = new PushMessageReceiver();
              pushMessageReceiver.setShopId(notice.getReceiverShopId());
              ShopDTO shopDTO = configService.getShopById(pushMessageReceiver.getShopId());
              if (ReceiverStatus.DELETED.equals(noticeReceiver.getStatus())) {
                pushMessageReceiver.setStatus(PushMessageReceiverStatus.DISABLED);
              } else {
                pushMessageReceiver.setStatus(PushMessageReceiverStatus.valueOf(noticeReceiver.getStatus().toString()));
              }
              pushMessageReceiver.setPushStatus(PushMessagePushStatus.UN_PUSH);
              pushMessageReceiver.setShowStatus(PushMessageShowStatus.ACTIVE);
              pushMessageReceiver.setMessageId(pushMessage.getId());
              pushMessageReceiver.setReceiverId(noticeReceiver.getReceiverUserId());
              pushMessageReceiver.setReceiverType(OperatorType.USER);
              pushMessageReceiver.setShopKind(shopDTO==null?null:shopDTO.getShopKind());
              writer.save(pushMessageReceiver);
            }
          }
        }
      }

      List<PushMessage> pushMessages = writer.getAllPushMessage();
      if(CollectionUtils.isNotEmpty(pushMessages)){
        for(PushMessage pushMessage:pushMessages){
          pushMessage.setContentText(StringUtil.Html2Text(pushMessage.getContent()));
          Set<Long> idSet = new HashSet<Long>();
          idSet.add(pushMessage.getId());
          if(PushMessageType.APPLY_CUSTOMER.equals(pushMessage.getType()) || PushMessageType.APPLY_SUPPLIER.equals(pushMessage.getType())){
            List<PushMessageSource> pushMessageSourceList = writer.getPushMessageSourcesByMessageIds(idSet);
            pushMessage.setRelatedObjectId(CollectionUtil.getFirst(pushMessageSourceList).getSourceId());
          }
          writer.update(pushMessage);
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}
