package com.bcgogo.txn.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.LoginDTO;
import com.bcgogo.api.RegistrationDTO;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.ShopAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.SysAppointParameter;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.app.AppointOrder;
import com.bcgogo.txn.model.app.AppointOrderServiceItem;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.model.pushMessage.PushMessageSource;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.RandomUtils;
import com.bcgogo.utils.ShopConstant;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AppointPushMessageService Tester.
 *
 * @author zhangjuntao
 * @version 1.0
 * @since <pre>9,11, 2013</pre>
 */
public class AppointPushMessageServiceTest extends AbstractPushMessageTest {

  @Before
  public void before() throws Exception {
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: createShopAcceptAppointMessage(ShopAppointParameter parameter)
   */
  @Test
  public void testCreateShopAcceptAppointMessage() throws Exception {
    Long sourceId = 2l;
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    String userNo = "15851654273";
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    Long shopId = createShop();
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    appointPushMessageService.createShopAcceptAppointMessage(new ShopAppointParameter(shopId, userNo, "维修,钣金", sourceId, System.currentTimeMillis(),"苏E552UQ"));
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(shopId, PushMessageType.SHOP_ACCEPT_APPOINT);
    System.out.println("testCreateShopAcceptAppointMessage pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("testCreateShopAcceptAppointMessage receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("testCreateShopAcceptAppointMessage sourceList" + sourceList);
  }

  /**
   * Method: createSysAcceptAppointMessage(SysAppointParameter parameter)
   */
  @Test
  public void testCreateSysAcceptAppointMessage() throws Exception {
    Long sourceId = 21l;
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    String userNo = "15851654273";
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    Long shopId = createShop();
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    appointPushMessageService.createSysAcceptAppointMessage(new SysAppointParameter(userNo, "苏A23984", shopId, sourceId, System.currentTimeMillis(), "装潢,钣金", "localhost:8080/web"));
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(ShopConstant.BC_SHOP_ID, PushMessageType.SYS_ACCEPT_APPOINT);
    System.out.println("testCreateSysAcceptAppointMessage pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("testCreateSysAcceptAppointMessage receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("testCreateSysAcceptAppointMessage sourceList" + sourceList);
  }

  /**
   * Method: createShopRejectAppointMessage(ShopAppointParameter parameter)
   */
  @Test
  public void testCreateShopRejectAppointMessage() throws Exception {
    Long sourceId = 3l;
    String userNo = "15851654273";
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    Long shopId = createShop();
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    appointPushMessageService.createShopRejectAppointMessage(new ShopAppointParameter(shopId, userNo, "维修,钣金", sourceId, System.currentTimeMillis(),"苏E552UQ"));
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(shopId, PushMessageType.SHOP_REJECT_APPOINT);
    System.out.println("testCreateShopRejectAppointMessage pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("testCreateShopRejectAppointMessage receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("testCreateShopRejectAppointMessage sourceList" + sourceList);
  }

  /**
   * Method: createShopCancelAppointMessage(ShopAppointParameter parameter)
   */
  @Test
  public void testCreateShopCancelAppointMessage() throws Exception {
    Long sourceId = 4l;
    String userNo = "15851654273";
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    Long shopId = createShop();
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    appointPushMessageService.createShopCancelAppointMessage(new ShopAppointParameter(shopId, userNo, "维修,钣金", sourceId, System.currentTimeMillis(),"苏E552UQ"));
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(shopId, PushMessageType.SHOP_CANCEL_APPOINT);
    Assert.assertEquals(1, pushMessageList.size());
    System.out.println("testCreateShopCancelAppointMessage pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("testCreateShopCancelAppointMessage receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("testCreateShopCancelAppointMessage sourceList" + sourceList);
  }

  /**
   * Method: createShopChangeAppointMessage(ShopAppointParameter parameter)
   */
  @Test
  public void testCreateShopChangeAppointMessage() throws Exception {
    Long sourceId = 5l;
    String userNo = "15851654273";
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    Long shopId = createShop();
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    Long appointTime = System.currentTimeMillis();
    appointPushMessageService.createShopChangeAppointMessage(new ShopAppointParameter(shopId, userNo, "维修,钣金", "维修,钣金", sourceId, appointTime, appointTime,"苏E552UQ"));
    TxnWriter writer = txnDaoManager.getWriter();

    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(shopId, PushMessageType.SHOP_CHANGE_APPOINT);
    Assert.assertEquals(0, pushMessageList.size());

    appointPushMessageService.createShopChangeAppointMessage(new ShopAppointParameter(shopId, userNo, "喷漆,钣金", "装潢,钣金", sourceId, appointTime, appointTime + 1000 * 60 * 60 * 24l,"苏E552UQ"));

    pushMessageList = writer.getPushMessageByCreatorId(shopId, PushMessageType.SHOP_CHANGE_APPOINT);
    System.out.println("testCreateShopChangeAppointMessage pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("testCreateShopChangeAppointMessage receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("testCreateShopChangeAppointMessage sourceList" + sourceList);
  }


  /**
   * Method: createAppCancelAppointMessage(AppAppointParameter parameter)
   */
  @Test
  public void testCreateAppCancelAppointMessage() throws Exception {
    Long sourceId = 6l;
    String userNo = "15851654273";
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(userNo, null);
    Assert.assertNotNull(appUserDTO.getId());
    Long shopId = createShop();
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    TxnWriter writer = txnDaoManager.getWriter();

    appointPushMessageService.createAppCancelAppointMessage(new AppAppointParameter(userNo, "苏A23984", shopId, sourceId, System.currentTimeMillis(), "装潢,钣金", "localhost:8080/web"));
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(appUserDTO.getId(), PushMessageType.APP_CANCEL_APPOINT);
    Assert.assertEquals(1, pushMessageList.size());
    System.out.println("testCreateShopCancelAppointMessage pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("testCreateShopCancelAppointMessage receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("testCreateShopCancelAppointMessage sourceList" + sourceList);
  }

  /**
   * Method: createAppApplyAppointMessage(AppAppointParameter parameter)
   */
  @Test
  public void testCreateAppApplyAppointMessage() throws Exception {
    Long sourceId = 7l;
    String userNo = "15851654273";
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    registerAppUser(appUserService, new RegistrationDTO(userNo, "1", "15851654273", "hans"));
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(userNo, null);
    Assert.assertNotNull(appUserDTO.getId());
    Long shopId = createShop();
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    TxnWriter writer = txnDaoManager.getWriter();

    appointPushMessageService.createAppApplyAppointMessage(new AppAppointParameter(userNo, "苏A23984", shopId, sourceId, System.currentTimeMillis(), "装潢,钣金", "localhost:8080/web"));
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(appUserDTO.getId(), PushMessageType.APP_APPLY_APPOINT);
    Assert.assertEquals(1, pushMessageList.size());
    System.out.println("testCreateShopCancelAppointMessage pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("testCreateShopCancelAppointMessage receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("testCreateShopCancelAppointMessage sourceList" + sourceList);
  }

  /**
   * Method: createAppVehicleMaintainMileageMessage()
   * Method: createAppVehicleMaintainTimeMessage()
   * Method: createAppVehicleInsuranceTimeMessage()
   * Method: createAppVehicleExamineTimeMessage()
   */
  @Test
  public void testCreateAppVehicleMessage() throws Exception {
    String userNo = "15851654273";
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Set<String> userNos = createAppUserAndVehicle(1200);
    Map<String, AppUserDTO> userDTOMap = appUserService.getAppUserMapByUserNo(userNos);
    Assert.assertEquals(1200, userDTOMap.size());


    //begin  createAppVehicleMaintainMileageMessage
    appointPushMessageService.createAppVehicleMaintainMileageMessage(1000);
    AppUserDTO appUserDTO = userDTOMap.values().iterator().next();
    List<PushMessage> pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.APP_VEHICLE_MAINTAIN_MILEAGE);
    Assert.assertEquals(1, pushMessageList.size());
    //create again
    appointPushMessageService.createAppVehicleMaintainMileageMessage(1000);
    appUserDTO = userDTOMap.values().iterator().next();
    List<AppVehicleDTO> appVehicleDTOList = appUserService.getAppVehicleDTOByAppUserNo(appUserDTO.getUserNo());
    Assert.assertEquals(1, appVehicleDTOList.size());

    List<PushMessageSource> sourceList = txnWriter.getPushMessageSourceBySourceId(appVehicleDTOList.get(0).getVehicleId());
    pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.APP_VEHICLE_MAINTAIN_MILEAGE);
    List<PushMessageReceiver> receiverList = txnWriter.getPushMessageReceiverBySourceId(null, appVehicleDTOList.get(0).getVehicleId(), null, PushMessageSourceType.APP_VEHICLE_MAINTAIN_MILEAGE);
    Assert.assertEquals(1, receiverList.size());
    Assert.assertEquals(1, sourceList.size());
    Assert.assertEquals(1, pushMessageList.size());


    //begin check  createAppVehicleMaintainTimeMessage
    appointPushMessageService.createAppVehicleMaintainTimeMessage(1000);
    appUserDTO = userDTOMap.values().iterator().next();
    appVehicleDTOList = appUserService.getAppVehicleDTOByAppUserNo(appUserDTO.getUserNo());
    Assert.assertEquals(1, appVehicleDTOList.size());
    pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.APP_VEHICLE_MAINTAIN_TIME);
    Assert.assertEquals(1, pushMessageList.size());
    receiverList = txnWriter.getPushMessageReceiverBySourceId(null, appVehicleDTOList.get(0).getVehicleId(), null, PushMessageSourceType.APP_VEHICLE_MAINTAIN_TIME);
    Assert.assertEquals(1, receiverList.size());
    //create again
    appointPushMessageService.createAppVehicleMaintainTimeMessage(1000);
    pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.APP_VEHICLE_MAINTAIN_TIME);
    Assert.assertEquals(1, pushMessageList.size());


    //begin check  createAppVehicleInsuranceTimeMessage
    appointPushMessageService.createAppVehicleInsuranceTimeMessage(1000);
    appUserDTO = userDTOMap.values().iterator().next();
    appVehicleDTOList = appUserService.getAppVehicleDTOByAppUserNo(appUserDTO.getUserNo());
    Assert.assertEquals(1, appVehicleDTOList.size());
    pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.APP_VEHICLE_INSURANCE_TIME);
    Assert.assertEquals(1, pushMessageList.size());
    receiverList = txnWriter.getPushMessageReceiverBySourceId(null, appVehicleDTOList.get(0).getVehicleId(), null, PushMessageSourceType.APP_VEHICLE_INSURANCE_TIME);
    Assert.assertEquals(1, receiverList.size());
    //create again
    appointPushMessageService.createAppVehicleInsuranceTimeMessage(1000);
    pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.APP_VEHICLE_INSURANCE_TIME);
    Assert.assertEquals(1, pushMessageList.size());


    //begin check  createAppVehicleExamineTimeMessage
    appointPushMessageService.createAppVehicleExamineTimeMessage(1000);
    appUserDTO = userDTOMap.values().iterator().next();
    appVehicleDTOList = appUserService.getAppVehicleDTOByAppUserNo(appUserDTO.getUserNo());
    Assert.assertEquals(1, appVehicleDTOList.size());
    pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.APP_VEHICLE_EXAMINE_TIME);
    Assert.assertEquals(1, pushMessageList.size());
    receiverList = txnWriter.getPushMessageReceiverBySourceId(null, appVehicleDTOList.get(0).getVehicleId(), null, PushMessageSourceType.APP_VEHICLE_EXAMINE_TIME);
    Assert.assertEquals(1, receiverList.size());
    //create again
    appointPushMessageService.createAppVehicleExamineTimeMessage(1000);
    pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.APP_VEHICLE_EXAMINE_TIME);
    Assert.assertEquals(1, pushMessageList.size());
  }

  /**
   * createOverdueAppointRemindMessage
   */
  @Test
  public void testCreateAppointOrderMessage() throws Exception {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    int userNumbers = 3, orderNumbers = 10;
    Set<String> userNos = createAppUserAndVehicle(userNumbers);
    Set<Long> orderIds = new HashSet<Long>();
    Map<String, AppUserDTO> userDTOMap = appUserService.getAppUserMapByUserNo(userNos);
    Long shopId = createShop();
    Assert.assertEquals(userNumbers, userDTOMap.size());
    for (AppUserDTO appUserDTO : userDTOMap.values()) {
      orderIds.addAll(createAppointOrder(appUserDTO, orderNumbers, shopId));
    }
    appointPushMessageService.createOverdueAppointRemindMessage(5);
    AppUserDTO appUserDTO = userDTOMap.values().iterator().next();
    Long sourceId = orderIds.iterator().next();
    List<PushMessageSource> sourceList = txnWriter.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(2, sourceList.size());
    List<PushMessageReceiver> receiverList = txnWriter.getPushMessageReceiverBySourceId(null, sourceId, null, PushMessageSourceType.OVERDUE_APPOINT_TO_SHOP, PushMessageSourceType.SOON_EXPIRE_APPOINT_TO_SHOP);
    Assert.assertEquals(1, receiverList.size());
    receiverList = txnWriter.getPushMessageReceiverBySourceId(null, sourceId, null, PushMessageSourceType.OVERDUE_APPOINT_TO_APP, PushMessageSourceType.SOON_EXPIRE_APPOINT_TO_APP);
    Assert.assertEquals(1, receiverList.size());
    List<PushMessage> pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(appUserDTO.getId(), 100, PushMessageType.OVERDUE_APPOINT_TO_APP, PushMessageType.SOON_EXPIRE_APPOINT_TO_APP);
    Assert.assertEquals(orderNumbers, pushMessageList.size());
    pushMessageList = txnWriter.getUnReadPushMessageByReceiverId(shopId, 100, PushMessageType.OVERDUE_APPOINT_TO_SHOP, PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP);
    Assert.assertEquals(orderNumbers * userNumbers, pushMessageList.size());

    appointPushMessageService.createOverdueAppointRemindMessage(5);
    sourceList = txnWriter.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(4, sourceList.size());
    receiverList = txnWriter.getPushMessageReceiverBySourceId(null, sourceId, null,
        PushMessageSourceType.OVERDUE_APPOINT_TO_SHOP, PushMessageSourceType.SOON_EXPIRE_APPOINT_TO_SHOP);
    Assert.assertEquals(1, receiverList.size());

  }

  private Set<Long> createAppointOrder(AppUserDTO appUserDTO, int number, Long shopId) {
    Set<Long> orderIds = new HashSet<Long>();
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    List<AppVehicleDTO> appVehicleDTOList = appUserService.getAppVehicleDTOByAppUserNo(appUserDTO.getUserNo());
    Object status = writer.begin();
    try {
      while (number-- > 0) {
        //保存维修单
        AppointOrder appointOrder = new AppointOrder();
        appointOrder.setShopId(shopId);
        appointOrder.setVehicleNo(appVehicleDTOList.get(0).getVehicleNo());
        appointOrder.setAppUserNo(appUserDTO.getUserNo());
        if (number % 2 == 0) {
          appointOrder.setAppointTime(System.currentTimeMillis() + 3600000L);
          appointOrder.setStatus(AppointOrderStatus.PENDING);
        } else {
          appointOrder.setStatus(AppointOrderStatus.ACCEPTED);
          appointOrder.setAppointTime(System.currentTimeMillis() - 10800000l + 60000);
        }
        writer.save(appointOrder);

        AppointOrderServiceItem appointOrderServiceItem = new AppointOrderServiceItem();
        appointOrderServiceItem.setAppointOrderId(appointOrder.getId());
        appointOrderServiceItem.setServiceName("test");
        appointOrderServiceItem.setShopId(shopId);
        appointOrderServiceItem.setStatus(ObjectStatus.ENABLED);
        writer.save(appointOrderServiceItem);
        orderIds.add(appointOrder.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return orderIds;
  }

} 
