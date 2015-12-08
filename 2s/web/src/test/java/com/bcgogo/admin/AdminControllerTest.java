package com.bcgogo.admin;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSendNecessaryType;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.notification.dto.MessageSwitchDTO;
import com.bcgogo.notification.dto.MessageTemplateDTO;
import com.bcgogo.notification.service.NotificationService;
import com.bcgogo.service.ServiceManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-31
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
public class AdminControllerTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
    adminController = new AdminController();
    notificationService = ServiceManager.getService(NotificationService.class);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testChangeMessageSwitch() throws Exception
  {
    request.getSession().setAttribute("shopId",12L);
    Long shopId = 12L;
    MessageTemplateDTO messageTemplateDTO = new MessageTemplateDTO();
    messageTemplateDTO.setContent("测试短信内容");
    messageTemplateDTO.setName("单元测试短信提醒");
    messageTemplateDTO.setType("测试短信");
    messageTemplateDTO.setScene(MessageScene.CUSTOMER_DEBT_MSG);
    createMsgTemplate(messageTemplateDTO);
    Assert.assertNotNull(messageTemplateDTO.getId());

    adminController.changeMessageSwitch(request,response,MessageScene.CUSTOMER_DEBT_MSG, MessageSwitchStatus.OFF);

    MessageSwitchDTO messageSwitchDTO = notificationService.getMessageSwitchDTOByShopIdAndScene(shopId,MessageScene.CUSTOMER_DEBT_MSG);

    Assert.assertEquals(MessageScene.CUSTOMER_DEBT_MSG,messageSwitchDTO.getScene());
    Assert.assertEquals(shopId,messageSwitchDTO.getShopId());
    Assert.assertEquals(MessageSwitchStatus.OFF,messageSwitchDTO.getStatus());

    Assert.assertEquals(MessageSwitchStatus.OFF, MemCacheAdapter.get(MemcachePrefix.messageSwitch.toString()+
        MessageScene.CUSTOMER_DEBT_MSG.toString()+shopId));

    adminController.changeMessageSwitch(request,response,MessageScene.CUSTOMER_DEBT_MSG, MessageSwitchStatus.ON);
    messageSwitchDTO = notificationService.getMessageSwitchDTOByShopIdAndScene(shopId,MessageScene.CUSTOMER_DEBT_MSG);

    Assert.assertEquals(MessageSwitchStatus.ON,messageSwitchDTO.getStatus());

    Assert.assertEquals(MessageSwitchStatus.ON, MemCacheAdapter.get(MemcachePrefix.messageSwitch.toString()+
        MessageScene.CUSTOMER_DEBT_MSG.toString()+shopId));
  }

  @Test
  public void testMessageSwitch() throws Exception
  {
    request.getSession().setAttribute("shopId",12L);
    Long shopId = 12L;
    MessageTemplateDTO messageTemplateDTO = new MessageTemplateDTO();
    messageTemplateDTO.setContent("测试短信内容1");
    messageTemplateDTO.setName("单元测试短信提醒");
    messageTemplateDTO.setType("测试短信1");
    messageTemplateDTO.setScene(MessageScene.CUSTOMER_DEBT_MSG);
    messageTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    createMsgTemplate(messageTemplateDTO);

    adminController.messageSwitch(request,response);

    List<MessageTemplateDTO> messageTemplateDTOs = (List<MessageTemplateDTO>)request.getAttribute("messageTemplateDTOs");

    Assert.assertEquals(1,messageTemplateDTOs.size());
    Assert.assertEquals(MessageScene.CUSTOMER_DEBT_MSG,messageTemplateDTOs.get(0).getScene());
    Assert.assertEquals("单元测试短信提醒",messageTemplateDTOs.get(0).getName());

    messageTemplateDTO = new MessageTemplateDTO();
    messageTemplateDTO.setContent("测试短信内容2");
    messageTemplateDTO.setName("单元测试短信提醒");
    messageTemplateDTO.setType("测试短信2");
    messageTemplateDTO.setScene(MessageScene.CUSTOMER_DEBT_MSG);
    messageTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    createMsgTemplate(messageTemplateDTO);
    adminController.changeMessageSwitch(request,response,MessageScene.CUSTOMER_DEBT_MSG, MessageSwitchStatus.OFF);
    adminController.messageSwitch(request,response);

    messageTemplateDTOs = (List<MessageTemplateDTO>)request.getAttribute("messageTemplateDTOs");

    Assert.assertEquals(1,messageTemplateDTOs.size());
    Assert.assertEquals(MessageScene.CUSTOMER_DEBT_MSG,messageTemplateDTOs.get(0).getScene());
    Assert.assertEquals("单元测试短信提醒",messageTemplateDTOs.get(0).getName());
    Assert.assertEquals(MessageSwitchStatus.OFF,messageTemplateDTOs.get(0).getStatus());


    messageTemplateDTO = new MessageTemplateDTO();
    messageTemplateDTO.setContent("测试短信内容3");
    messageTemplateDTO.setName("单元测试短信提醒2");
    messageTemplateDTO.setType("测试短信3");
    messageTemplateDTO.setScene(MessageScene.BOSS_DEBT_MSG);
    messageTemplateDTO.setNecessary(MessageSendNecessaryType.UNNECESSARY);
    createMsgTemplate(messageTemplateDTO);
    adminController.messageSwitch(request,response);

    messageTemplateDTOs = (List<MessageTemplateDTO>)request.getAttribute("messageTemplateDTOs");

    Assert.assertEquals(2,messageTemplateDTOs.size());

    for(MessageTemplateDTO newMessageTemplateDTO : messageTemplateDTOs)
    {
      if(MessageScene.BOSS_DEBT_MSG == newMessageTemplateDTO.getScene())
      {
        Assert.assertEquals("单元测试短信提醒2",newMessageTemplateDTO.getName());
      }
      else
      {
        Assert.assertEquals("单元测试短信提醒",newMessageTemplateDTO.getName());
        Assert.assertEquals(MessageScene.CUSTOMER_DEBT_MSG,newMessageTemplateDTO.getScene());
      }
    }
  }

}
