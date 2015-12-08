package com.bcgogo.txn.service;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.enquiry.VehicleFaultParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.model.pushMessage.PushMessageSource;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;
import com.bcgogo.txn.service.pushMessage.IVehicleFaultPushMessage;
import com.bcgogo.user.service.app.IAppUserService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class VehicleFaultMessageServiceTest extends AbstractPushMessageTest {
  @Test
  public void testCreateVehicleFaultMessage2Shop() throws Exception {
    Long shopId = createShop();
    Long sourceId = shopId + 1l;
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    Set<String> userNos = createAppUserAndVehicle(1);
    String userNo =userNos.iterator().next();
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(userNo, null);
    AppVehicleDTO  appVehicleDTO = appUserService.getAppVehicleDTOByAppUserNo(userNo).iterator().next();
    Assert.assertNotNull(appUserDTO.getId());
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    IVehicleFaultPushMessage vehicleFaultPushMessage =ServiceManager.getService(IVehicleFaultPushMessage.class);
    Long faultInfoToShopId = 1L;
    vehicleFaultPushMessage.createVehicleFaultMessage2Shop(
        new VehicleFaultParameter(appUserDTO.getUserNo(), appUserDTO.getName(), appVehicleDTO.getVehicleNo(),
            "2SJDF28", "", shopId, sourceId, "15851654544",faultInfoToShopId)
    );
    TxnWriter writer = txnDaoManager.getWriter();
    List<PushMessage> pushMessageList = writer.getPushMessageByCreatorId(appUserDTO.getId(), PushMessageType.VEHICLE_FAULT_2_SHOP);
    System.out.println("createVehicleFaultMessage2Shop pushMessageList" + pushMessageList);
    List<PushMessageReceiver> receiverList = writer.getPushMessageReceiverByMessageId(null, PushMessageReceiverStatus.UNREAD, pushMessageList.get(0).getId());
    Assert.assertEquals(1, receiverList.size());
    System.out.println("createVehicleFaultMessage2Shop receiverList" + receiverList);
    List<PushMessageSource> sourceList = writer.getPushMessageSourceBySourceId(sourceId);
    Assert.assertEquals(1, sourceList.size());
    System.out.println("createVehicleFaultMessage2Shop sourceList" + sourceList);
  }
}