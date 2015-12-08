package com.bcgogo.txn.service;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.RegistrationDTO;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.model.pushMessage.PushMessageSource;
import com.bcgogo.txn.service.pushMessage.IEnquiryPushMessageService;
import com.bcgogo.user.service.app.IAppUserService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EnquiryMessageServiceTest extends AbstractPushMessageTest {

  @Test
  public void testCreateShopQuoteEnquiryMessageToApp() throws Exception {
    Long shopId = createShop();
    Long sourceId = shopId + 1l;
    String userNo = "15851654273";
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    ServiceManager.getService(IEnquiryPushMessageService.class).createShopQuoteEnquiryMessageToApp(new ShopQuoteEnquiryParameter(shopId, userNo, sourceId, System.currentTimeMillis()));
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(shopId, PushMessageType.SHOP_QUOTE_TO_APP);
    System.out.println("createShopQuoteEnquiryMessageToApp pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("createShopQuoteEnquiryMessageToApp receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("createShopQuoteEnquiryMessageToApp sourceList" + sourceList);
  }

  @Test
  public void createAppSubmitEnquiryMessageToShop() throws Exception {
    Long shopId = createShop();
    Long sourceId = shopId + 1l;
    String userNo = "15851654273";
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    ServiceManager.getService(IEnquiryPushMessageService.class).createAppSubmitEnquiryMessageToShop(new AppEnquiryParameter(shopId, userNo, sourceId, "苏A00001"));
    TxnWriter writer = txnDaoManager.getWriter();
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(userNo, null);
    Assert.assertNotNull(appUserDTO.getId());
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(appUserDTO.getId(), PushMessageType.APP_SUBMIT_ENQUIRY);
    System.out.println("createAppSubmitEnquiryMessageToShop pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("createAppSubmitEnquiryMessageToShop receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("createAppSubmitEnquiryMessageToShop sourceList" + sourceList);
  }
}