package com.bcgogo.sms;

import com.bcgogo.AbstractTest;
import com.bcgogo.admin.AdminController;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSendNecessaryType;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.notification.dto.MessageTemplateDTO;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.utils.SmsConstant;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hans
 * Date: 12-11-26
 * Time: 下午5:13
 * 短信模板测试
 */
public class SmsServiceTest extends AbstractTest {
  AdminController adminController = new AdminController();

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    deleteJobs();
  }

  @After
  public void tearDown() throws Exception {
    request = null;
    response = null;
  }

  @Test
  public void salesAcceptedSMSSuccessTest() throws Exception {
    Long senderId = createShop();
    Long receiverId = createShop();
    //创建模板
    createMsmTemplate();
    //开启开关
    messageSwitch(MessageScene.SALES_ACCEPTED, MessageSwitchStatus.ON);
    messageSwitch(MessageScene.SALES_REFUSE, MessageSwitchStatus.OFF);
    messageSwitch(MessageScene.STOCKING_CANCEL, MessageSwitchStatus.ON);
    messageSwitch(MessageScene.SHIPPED_CANCEL, MessageSwitchStatus.OFF);
    messageSwitch(MessageScene.RETURNS_REFUSE, MessageSwitchStatus.ON);
    messageSwitch(MessageScene.RETURNS_ACCEPTED, MessageSwitchStatus.OFF);
    String orderNumber = "JTSGHRZ-20121126";
    String mobile = "15851654173";
    smsService.salesAcceptedSMS(receiverId, senderId, orderNumber, mobile);
    List<SmsJobDTO> jobs = notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(1, jobs.size());
    Assert.assertEquals(mobile, jobs.get(0).getReceiveMobile());
    Assert.assertEquals(receiverId, jobs.get(0).getShopId());

    smsService.salesRefuseSMS(receiverId, senderId, orderNumber, mobile);
    jobs = notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(1, jobs.size());

    smsService.stockingCancelSMS(receiverId, senderId, orderNumber, mobile);
    jobs = notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(2, jobs.size());

    smsService.shippedCancelSMS(receiverId, senderId, orderNumber, mobile);
    jobs = notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(2, jobs.size());

    smsService.returnsRefuseSMS(receiverId, senderId, orderNumber, mobile);
    jobs = notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(3, jobs.size());

    smsService.returnsAcceptedSMS(receiverId, senderId, orderNumber, mobile);
    jobs = notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(3, jobs.size());

  }

  @Test
  public void salesAcceptedSMSFailedTest() throws Exception {
    Long senderId = createShop();
    Long receiverId = createShop();
    //创建模板
    createMsmTemplate();
    //关闭开关
    messageSwitch(MessageScene.SALES_ACCEPTED, MessageSwitchStatus.OFF);
    String orderNumber = "JTSGHRZ-20121126";
    String mobile = "15851654173";
    smsService.salesAcceptedSMS(receiverId, senderId, orderNumber, mobile);
    List<SmsJobDTO> jobs = notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(0, jobs.size());

    //没有单据号
    try {
      smsService.salesAcceptedSMS(receiverId, senderId, "", mobile);
    } catch (SmsException e) {
      Assert.assertFalse(e.getMessage(), false);
    }
    notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(0, jobs.size());

    //手机号不正确
    messageSwitch(MessageScene.SALES_ACCEPTED, MessageSwitchStatus.ON);
    smsService.salesAcceptedSMS(receiverId, senderId, orderNumber, "1111");
    notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(0, jobs.size());

    //接受者shopId为空
    try {
      smsService.salesAcceptedSMS(null, senderId, orderNumber, mobile);
    } catch (SmsException e) {
      Assert.assertFalse(e.getMessage(), false);
    }
    jobs = notificationService.getSmsJobsByShopId(receiverId, 0, 100);
    Assert.assertEquals(0, jobs.size());

  }

  //打开模板
  public void messageSwitch(MessageScene scene, MessageSwitchStatus status) throws Exception {
    adminController.changeMessageSwitch(request, response, scene, status);
  }

  //创建模板
  public void createMsmTemplate() {
    MessageTemplateDTO msgTemplateDTO = new MessageTemplateDTO();
    msgTemplateDTO.setName("销售单接受短信");
    msgTemplateDTO.setScene(MessageScene.SALES_ACCEPTED);
    msgTemplateDTO.setType(SmsConstant.MsgTemplateTypeConstant.salesAccepted);
    msgTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    msgTemplateDTO.setContent("您的采购订单{orderNumber}已经被供应商接受，正在备货中！");
    smsService.saveMsgTemplate(msgTemplateDTO);

    msgTemplateDTO = new MessageTemplateDTO();
    msgTemplateDTO.setName("销售单拒绝短信");
    msgTemplateDTO.setScene(MessageScene.SALES_REFUSE);
    msgTemplateDTO.setType(SmsConstant.MsgTemplateTypeConstant.salesRefuse);
    msgTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    msgTemplateDTO.setContent("您的采购订单{orderNumber}已经被供应商拒绝，如有问题请与供应商联系！");
    smsService.saveMsgTemplate(msgTemplateDTO);

    msgTemplateDTO = new MessageTemplateDTO();
    msgTemplateDTO.setName("备货中作废短信");
    msgTemplateDTO.setScene(MessageScene.STOCKING_CANCEL);
    msgTemplateDTO.setType(SmsConstant.MsgTemplateTypeConstant.stockingCancel);
    msgTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    msgTemplateDTO.setContent("您的采购订单{orderNumber}已经被供应商销售终止，如有问题请与供应商联系！");
    smsService.saveMsgTemplate(msgTemplateDTO);

    msgTemplateDTO = new MessageTemplateDTO();
    msgTemplateDTO.setName("已发货作废短信");
    msgTemplateDTO.setScene(MessageScene.SHIPPED_CANCEL);
    msgTemplateDTO.setType(SmsConstant.MsgTemplateTypeConstant.shippedCancel);
    msgTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    msgTemplateDTO.setContent("您的采购订单{orderNumber}已经被供应商销售终止，如有问题请与供应商联系！");
    smsService.saveMsgTemplate(msgTemplateDTO);

    msgTemplateDTO = new MessageTemplateDTO();
    msgTemplateDTO.setName("退货单接受短信");
    msgTemplateDTO.setScene(MessageScene.RETURNS_ACCEPTED);
    msgTemplateDTO.setType(SmsConstant.MsgTemplateTypeConstant.returnsAccepted);
    msgTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    msgTemplateDTO.setContent("您的入库退货单{orderNumber}已经被供应商接受，可以继续做结算操作！");
    smsService.saveMsgTemplate(msgTemplateDTO);

    msgTemplateDTO = new MessageTemplateDTO();
    msgTemplateDTO.setName("退货单接受短信");
    msgTemplateDTO.setScene(MessageScene.RETURNS_REFUSE);
    msgTemplateDTO.setType(SmsConstant.MsgTemplateTypeConstant.returnsRefuse);
    msgTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    msgTemplateDTO.setContent("您的入库退货单{orderNumber}已经被供应商拒绝，如有问题请与供应商联系！");
    smsService.saveMsgTemplate(msgTemplateDTO);
  }

}
