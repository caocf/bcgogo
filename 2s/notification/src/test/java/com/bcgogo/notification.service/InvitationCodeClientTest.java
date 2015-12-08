package com.bcgogo.notification.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.notification.InvitationCodeStatus;
import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.notification.dto.InvitationCodeDTO;
import com.bcgogo.notification.invitationCode.InvitationCodeGeneratorClient;
import com.bcgogo.notification.model.InvitationCode;
import com.bcgogo.notification.model.InvitationCodeRecycle;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.service.ServiceManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-17
 * Time: 下午9:56
 */
public class InvitationCodeClientTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
    NotificationDaoManager notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<InvitationCode> codeList = writer.getInvitationCode(0, 200);
    List<InvitationCodeRecycle> recycleList = writer.getInvitationCodeRecycle(0, 200);
    Object status = writer.begin();
    try {
      for (InvitationCode code : codeList) {
        writer.delete(InvitationCode.class, code.getId());
      }
      for (InvitationCodeRecycle recycle : recycleList) {
        writer.delete(InvitationCodeRecycle.class, recycle.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
      codeList.clear();
      recycleList.clear();
    }
  }

  @Test
  public void recycleInvitationCodeTest() throws Exception {
    InvitationCodeGeneratorClient invitationCodeService = ServiceManager.getService(InvitationCodeGeneratorClient.class);
    NotificationDaoManager notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
    NotificationWriter writer = notificationDaoManager.getWriter();
    Long time;
    for (int i = 0; i < 10; i++) {
      if (i <= 3) {
        time = System.currentTimeMillis();
      } else if (3 < i && i <= 6) {
        time = System.currentTimeMillis() - 24 * 60 * 60 * 1000 * 60l;
      } else {
        time = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 10l;
      }
      invitationCodeService.createInvitationCode(InvitationCodeType.SHOP, OperatorType.SHOP, 1l, OperatorType.SHOP, 2l, time);
    }
    List<InvitationCode> codeList = writer.getInvitationCode(0, 200);
    List<InvitationCodeRecycle> recycleList = writer.getInvitationCodeRecycle(0, 200);
    Assert.assertEquals(10, codeList.size());
    Assert.assertEquals(0, recycleList.size());

    invitationCodeService.recycleInvitationCode(2);

    codeList = writer.getInvitationCode(0, 200);
    recycleList = writer.getInvitationCodeRecycle(0, 200);
    Assert.assertEquals(3, recycleList.size());
    int overdue=0;
    int effective=0;
    for(InvitationCode code: codeList){
      if(code.getStatus()== InvitationCodeStatus.OVERDUE)overdue++;
      if(code.getStatus()== InvitationCodeStatus.EFFECTIVE)effective++;
    }
    Assert.assertEquals(4,effective);
    Assert.assertEquals(3,overdue);
  }


  @Test
  public void createInvitationCodeTest() throws Exception {
    InvitationCodeGeneratorClient invitationCodeService = ServiceManager.getService(InvitationCodeGeneratorClient.class);
    String code = invitationCodeService.createInvitationCode(InvitationCodeType.SHOP, OperatorType.SHOP, 1l, OperatorType.SHOP, 2l, null);
    InvitationCodeDTO dto = invitationCodeService.findEffectiveInvitationCodeByCode(code);
    Assert.assertNotNull(dto);
  }

  @Test
  public void updateInvitationCodeToUsedTest() throws Exception {
    InvitationCodeGeneratorClient invitationCodeService = ServiceManager.getService(InvitationCodeGeneratorClient.class);
    String code = invitationCodeService.createInvitationCode(InvitationCodeType.SHOP, OperatorType.SHOP, 1l, OperatorType.SHOP, 2l, null);
    invitationCodeService.updateInvitationCodeToUsed(code);
    InvitationCodeDTO dto = invitationCodeService.findEffectiveInvitationCodeByCode(code);
    Assert.assertNull(dto);
  }

}
