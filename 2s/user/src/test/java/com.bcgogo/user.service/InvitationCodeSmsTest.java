package com.bcgogo.user.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSendNecessaryType;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.notification.dto.InvitationCodeSendDTO;
import com.bcgogo.notification.dto.MessageTemplateDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.invitationCode.InvitationCodeGenerator;
import com.bcgogo.notification.model.*;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.notification.service.NotificationService;
import com.bcgogo.notification.service.SmsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.SmsConstant;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-21
 * Time: 下午3:03
 */
public class InvitationCodeSmsTest extends AbstractTest {
  @Before
  public void setUpTest() throws Exception {
    ServiceManager.getService(NotificationService.class)
        .setMessageTemplate(SmsConstant.MsgTemplateTypeConstant.invitationCodeToCustomer,
            "我在用一发软件,这是汽车行业最好的管理软件,了解视频:XXX.bcgogo.com注册网址:{regUrl};邀请码(有效期10天):{invitationCode}（{shopName}推荐）"
            , ShopConstant.BC_SHOP_ID);
    ServiceManager.getService(NotificationService.class)
        .setMessageTemplate(SmsConstant.MsgTemplateTypeConstant.invitationCodeToSupplier,
            "我在用一发软件,这是汽车行业最好的管理软件,了解视频:XXX.bcgogo.com注册网址:{regUrl};邀请码(有效期10天):{invitationCode}（{shopName}推荐）"
            , ShopConstant.BC_SHOP_ID);
  }

  @Test
  public void sendInvitationCodeSmsForCustomersTest() throws Exception {
    Long shopId = createShop();
    createMsmTemplate();
    for (int i = 0; i < 11; i++) {
      createCustomer(shopId, "Customer-U-" + i, RelationTypes.UNRELATED);
      createCustomer(shopId, "Customer-S-" + i, RelationTypes.REGISTER_RELATED);
    }
    InvitationCodeSendDTO invitationCodeSendDTO = new InvitationCodeSendDTO();
    invitationCodeSendDTO.setSender(SenderType.Shop);
    invitationCodeSendDTO.setInvitationCodeType(InvitationCodeType.SHOP);
    invitationCodeSendDTO.setShopId(shopId);
    invitationCodeSendDTO.setPageSize(2);
    invitationCodeSendDTO.setSendTime(System.currentTimeMillis());
    InvitationCodeSmsService invitationCodeSmsService = ServiceManager.getService(InvitationCodeSmsService.class);
    NotificationDaoManager notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
    NotificationService notificationService = ServiceManager.getService(NotificationService.class);
    invitationCodeSmsService.sendInvitationCodeSmsForCustomers(null,invitationCodeSendDTO);
    List<SmsJobDTO> jobs = notificationService.getSmsJobsByShopId(shopId, 0, 100);
    Assert.assertEquals(11, jobs.size());
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<InvitationCode> codeList = writer.getInvitationCode(0, 100);
    Assert.assertEquals(11, codeList.size());
    List<CustomerDTO> customerDTOList =ServiceManager.getService(ICustomerService.class).getCustomerByShopId(shopId,0,100);
    Assert.assertEquals(1, customerDTOList.get(0).getInvitationCodeSendTimes(),0.0001);
  }

  //创建模板
  public void createMsmTemplate() {
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    MessageTemplateDTO msgTemplateDTO = new MessageTemplateDTO();
    msgTemplateDTO.setName("验证码提醒");
    msgTemplateDTO.setScene(MessageScene.INVITATION_CODE);
    msgTemplateDTO.setType(SmsConstant.MsgTemplateTypeConstant.verificationCode);
    msgTemplateDTO.setNecessary(MessageSendNecessaryType.NECESSARY);
    msgTemplateDTO.setContent("您好!您的验证码为:<{vercode}>。祝您使用愉快!如有任何问题请随时拨打客服电话:0512-66733331与我们联系。");
    smsService.saveMsgTemplate(msgTemplateDTO);
  }

}
