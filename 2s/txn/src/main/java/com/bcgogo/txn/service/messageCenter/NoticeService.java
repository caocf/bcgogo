package com.bcgogo.txn.service.messageCenter;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.constant.pushMessage.PushMessageContentTemplate;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.txn.message.*;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.velocity.PushMessageVelocityContext;
import com.bcgogo.remind.dto.message.NoticeDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.message.NoticeReceiver;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 上午11:52
 */
@Component
public class NoticeService extends AbstractMessageService implements INoticeService {
  private static final Logger LOG = LoggerFactory.getLogger(NoticeService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  private IShopService shopService;

  public IShopService getShopService() {
    if (shopService == null) shopService = ServiceManager.getService(IShopService.class);
    return shopService;
  }

  private ICustomerService customerService;
  private ISupplierService supplierService;
  private IConfigService configService;

  public ICustomerService getCustomerService() {
    return customerService == null ? ServiceManager.getService(ICustomerService.class) : customerService;
  }

  public ISupplierService getSupplierService() {
    return supplierService == null ? ServiceManager.getService(ISupplierService.class) : supplierService;
  }

  public IConfigService getConfigService() {
    return configService == null ? ServiceManager.getService(IConfigService.class) : configService;
  }


  @Override
  public NoticeDTO createNotice(NoticeDTO dto) throws BcgogoException {
    if (dto == null) throw new BcgogoException("notice is null!");
    if (StringUtils.isBlank(dto.getContent())) throw new BcgogoException("notice content is null!");
    if (dto.getSenderShopId() == null) throw new BcgogoException("notice requesterShopId is null!");
    if (dto.getNoticeType() == null) throw new BcgogoException("notice NoticeType is null!");
    if (dto.getReceiverShopId() == null) throw new BcgogoException("notice receiverShopId is null!");
    ShopDTO shopDTO = getConfigService().getShopById(dto.getSenderShopId());
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    List<Long> userIds = getUserIds(dto.getReceiverShopId());
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      PushMessage pushMessage = new PushMessage();
      pushMessage.fromNoticeDTO(dto);
      pushMessage.setDeleted(DeletedType.FALSE);
      writer.save(pushMessage);
      dto.setId(pushMessage.getId());
      PushMessageReceiver pushMessageReceiver;
      for (Long userId : userIds) {
        pushMessageReceiver = new PushMessageReceiver();
        pushMessageReceiver.setShopId(dto.getReceiverShopId());
        pushMessageReceiver.setStatus(PushMessageReceiverStatus.UNREAD);
        pushMessageReceiver.setPushStatus(PushMessagePushStatus.UN_PUSH);
        pushMessageReceiver.setShowStatus(PushMessageShowStatus.ACTIVE);
        pushMessageReceiver.setMessageId(pushMessage.getId());
        pushMessageReceiver.setReceiverId(userId);
        pushMessageReceiver.setReceiverType(OperatorType.USER);
        pushMessageReceiver.setShopKind(shopDTO.getShopKind());
        writer.save(pushMessageReceiver);
      }
      writer.commit(status);
      for (Long userId : userIds) {
        pushMessageService.updatePushMessageCategoryStatNumberInMemCache(dto.getReceiverShopId(), userId,PushMessageCategory.RelatedNoticeMessage);
      }
    } finally {
      writer.rollback(status);
    }
    return dto;
  }

  @Override
  public void createSupplierAcceptNoticeToSupplier(ShopDTO customerShopDTO, ShopRelationInviteDTO shopRelationInviteDTO,
                                                   CustomerDTO customerDTO) throws Exception {
    List<CustomerDTO> similarCustomers = getCustomerService().getSimilarCustomer(customerDTO);
    if(CollectionUtils.isNotEmpty(similarCustomers)){
      NoticeDTO noticeDTO = new NoticeDTO();
      if (shopRelationInviteDTO != null) {
        noticeDTO.setNoticeType(NoticeType.SUPPLIER_ACCEPT_TO_SUPPLIER);
        noticeDTO.setShopRelationInviteId(shopRelationInviteDTO.getId());
        noticeDTO.setSenderShopId(shopRelationInviteDTO.getInvitedShopId());
        noticeDTO.setOriginShopId(customerShopDTO.getId());
        noticeDTO.setUserId(shopRelationInviteDTO.getOperationManId());
        noticeDTO.setReceiverShopId(shopRelationInviteDTO.getInvitedShopId());
        noticeDTO.setRequestTime(System.currentTimeMillis());
        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setShopDTO(customerShopDTO);
        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.SUPPLIER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT, "SUPPLIER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.SUPPLIER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT_TEXT, "SUPPLIER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT_TEXT");
        noticeDTO.setContent(content);
        noticeDTO.setContentText(contentText);
        noticeDTO.setSameCustomers(customerDTO, similarCustomers);
        createNotice(noticeDTO);
      }
    }
  }

  @Override
  public void createSupplierAcceptNoticeToCustomer(ShopDTO supplierShopDTO, ShopRelationInviteDTO shopRelationInviteDTO,
                                                   SupplierDTO supplierDTO) throws Exception {
    NoticeDTO noticeDTO = new NoticeDTO();
    if (shopRelationInviteDTO != null) {
      noticeDTO.setNoticeType(NoticeType.SUPPLIER_ACCEPT_TO_CUSTOMER);
      noticeDTO.setShopRelationInviteId(shopRelationInviteDTO.getId());
      noticeDTO.setSenderShopId(shopRelationInviteDTO.getInvitedShopId());
      noticeDTO.setUserId(shopRelationInviteDTO.getOperationManId());
      noticeDTO.setReceiverShopId(shopRelationInviteDTO.getOriginShopId());
      noticeDTO.setOriginShopId(supplierShopDTO.getId());
      noticeDTO.setRequestTime(System.currentTimeMillis());
      PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
      pushMessageVelocityContext.setShopDTO(supplierShopDTO);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
      String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.SUPPLIER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT, "SUPPLIER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT");
      String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.SUPPLIER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT_TEXT, "SUPPLIER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT_TEXT");
      noticeDTO.setContent(content);
      noticeDTO.setContentText(contentText);
      List<SupplierDTO> similarSuppliers = getSupplierService().getSimilarSupplier(supplierDTO);
      noticeDTO.setSameSuppliers(supplierDTO, similarSuppliers);
      createNotice(noticeDTO);
    }
  }

  @Override
  public void createCustomerAcceptNoticeToSupplier(ShopDTO customerShopDTO, ShopRelationInviteDTO shopRelationInviteDTO, CustomerDTO customerDTO) throws Exception {
    NoticeDTO noticeDTO = new NoticeDTO();
    if (shopRelationInviteDTO != null) {
      noticeDTO.setNoticeType(NoticeType.CUSTOMER_ACCEPT_TO_SUPPLIER);
      noticeDTO.setShopRelationInviteId(shopRelationInviteDTO.getId());
      noticeDTO.setSenderShopId(shopRelationInviteDTO.getInvitedShopId());
      noticeDTO.setUserId(shopRelationInviteDTO.getOperationManId());
      noticeDTO.setReceiverShopId(shopRelationInviteDTO.getOriginShopId());
      noticeDTO.setOriginShopId(customerShopDTO.getId());

      noticeDTO.setRequestTime(System.currentTimeMillis());
      PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
      pushMessageVelocityContext.setShopDTO(customerShopDTO);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
      String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.CUSTOMER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT, "CUSTOMER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT");
      String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.CUSTOMER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT_TEXT, "CUSTOMER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT_TEXT");

      noticeDTO.setContent(content);
      noticeDTO.setContentText(contentText);

      List<CustomerDTO> customerDTOs = getCustomerService().getSimilarCustomer(customerDTO);
      noticeDTO.setSameCustomers(customerDTO, customerDTOs);
      createNotice(noticeDTO);
    }
  }

  @Override
  public void createCustomerAcceptNoticeToCustomer(ShopDTO supplierShopDTO, ShopRelationInviteDTO shopRelationInviteDTO, SupplierDTO supplierDTO) throws Exception {
    List<SupplierDTO> similarSuppliers = getSupplierService().getSimilarSupplier(supplierDTO);
    if(CollectionUtils.isNotEmpty(similarSuppliers)){
      NoticeDTO noticeDTO = new NoticeDTO();
      if (shopRelationInviteDTO != null) {
        noticeDTO.setNoticeType(NoticeType.CUSTOMER_ACCEPT_TO_CUSTOMER);
        noticeDTO.setShopRelationInviteId(shopRelationInviteDTO.getId());
        noticeDTO.setSenderShopId(shopRelationInviteDTO.getInvitedShopId());
        noticeDTO.setUserId(shopRelationInviteDTO.getOperationManId());
        noticeDTO.setReceiverShopId(shopRelationInviteDTO.getInvitedShopId());
        noticeDTO.setOriginShopId(supplierShopDTO.getId());
        noticeDTO.setRequestTime(System.currentTimeMillis());
        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setShopDTO(supplierShopDTO);
        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.CUSTOMER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT, "CUSTOMER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.CUSTOMER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT_TEXT, "CUSTOMER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT_TEXT");

        noticeDTO.setContent(content);
        noticeDTO.setContentText(contentText);
        noticeDTO.setSameSuppliers(supplierDTO, similarSuppliers);
        createNotice(noticeDTO);
      }
    }
  }

  @Override
  public void createCancelNotice(Long senderShopId, Long receiverShopId, String cancelMsg) throws Exception {
    NoticeDTO noticeDTO = new NoticeDTO();
    if (senderShopId != null && receiverShopId != null) {
      noticeDTO.setNoticeType(NoticeType.CANCEL_ASSOCIATION_NOTICE);
      noticeDTO.setSenderShopId(senderShopId);
      noticeDTO.setReceiverShopId(receiverShopId);
      noticeDTO.setRequestTime(System.currentTimeMillis());

      noticeDTO.setOriginShopId(senderShopId);

      ShopDTO senderShopDTO = getConfigService().getShopById(senderShopId);
      PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
      pushMessageVelocityContext.setShopDTO(senderShopDTO);
      pushMessageVelocityContext.setCancelMsg(StringUtils.defaultIfEmpty(cancelMsg,"无"));
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
      String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.CANCEL_NOTICE_CONTENT, "CANCEL_NOTICE_CONTENT");
      String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.CANCEL_NOTICE_CONTENT_TEXT, "CANCEL_NOTICE_CONTENT_TEXT");
      noticeDTO.setContent(content);
      noticeDTO.setContentText(contentText);
      createNotice(noticeDTO);
    }
  }

  @Override
  public void createRefuseNotice(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO) throws Exception {
    NoticeDTO noticeDTO = new NoticeDTO();
    if (shopRelationInviteDTO != null) {
      noticeDTO.setNoticeType(NoticeType.ASSOCIATION_REJECT_NOTICE);
      noticeDTO.setShopRelationInviteId(shopRelationInviteDTO.getId());
      noticeDTO.setSenderShopId(shopRelationInviteDTO.getInvitedShopId());
      noticeDTO.setUserId(shopRelationInviteDTO.getOperationManId());
      noticeDTO.setReceiverShopId(shopRelationInviteDTO.getOriginShopId());
      noticeDTO.setOriginShopId(shopDTO.getId());
      noticeDTO.setRequestTime(System.currentTimeMillis());
      PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
      pushMessageVelocityContext.setShopDTO(shopDTO);
      pushMessageVelocityContext.setRefuseMsg(StringUtils.defaultIfEmpty(shopRelationInviteDTO.getRefuseMsg(),"无"));
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
      String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.REFUSE_NOTICE_CONTENT, "REFUSE_NOTICE_CONTENT");
      String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.REFUSE_NOTICE_CONTENT_TEXT, "REFUSE_NOTICE_CONTENT_TEXT");

      noticeDTO.setContent(content);
      noticeDTO.setContentText(contentText);
      createNotice(noticeDTO);
    }
  }

}
