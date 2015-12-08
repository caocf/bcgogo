package com.bcgogo.config;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.dto.ConfigDTO;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSendNecessaryType;
import com.bcgogo.notification.cache.MessageTemplateCacheManager;
import com.bcgogo.notification.dto.MessageTemplateDTO;
import com.bcgogo.notification.model.MessageTemplate;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

/**
 * Created by IntelliJ IDEA.
 * User: dongnan
 * Date: 12-7-12
 * Time: 下午8:21
 * To change this template use File | Settings | File Templates.
 */
public class DataMaintenanceControllerTest extends AbstractTest {

   public ConfigDTO configDTO;
  public MessageTemplateDTO msgTemplateDTO;
  public ModelMap model;
  public int pageNo;

  @Before
  public void setUp() throws Exception {
    configDTO=new ConfigDTO();
    msgTemplateDTO=new MessageTemplateDTO();
    model = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testSearchConfig() throws Exception {

    Long shopId=-1l;
    pageNo=1;
    String value="808080580006213";
    //name为空
    String name="";
    dataMaintenanceController.searchConfig(model,request,response,name,value,shopId,pageNo);
    name="MerId";
    dataMaintenanceController.searchConfig(model,request,response,name,value,shopId,pageNo);
    //value为空
    value="";
    dataMaintenanceController.searchConfig(model,request,response,name,value,shopId,pageNo);
  }
    @Test
  public void testSaveOrUpdateConfig() throws Exception {
    configDTO.setShopId(-1l);
    configDTO.setName("ChinaPayPubKeyPath");
    configDTO.setValue("/usr/local/tomcat/key/PgPubk.key");
    configDTO.setDescription("description");
    //update
    dataMaintenanceController.saveOrUpdateConfig(model,request,response,configDTO);
    ConfigDTO configDTO= (ConfigDTO)model.get("configDTO");
    Assert.assertNotNull(configDTO);
    Assert.assertEquals(-1l,configDTO.getShopId(),0.0001);
    Assert.assertEquals("ChinaPayPubKeyPath",configDTO.getName());
    Assert.assertEquals("/usr/local/tomcat/key/PgPubk.key",configDTO.getValue());
    Assert.assertEquals("description",configDTO.getDescription());
    //savle
    configDTO.setShopId(-1l);
    configDTO.setName("key10");
    configDTO.setValue("value10");
    configDTO.setDescription("ds10");
    dataMaintenanceController.saveOrUpdateConfig(model,request,response,configDTO);
    configDTO= (ConfigDTO)model.get("configDTO");
    Assert.assertNotNull(configDTO);
    Assert.assertEquals(-1l,configDTO.getShopId(),0.0001);
    Assert.assertEquals("key10",configDTO.getName());
    Assert.assertEquals("value10",configDTO.getValue());
    Assert.assertEquals("ds10",configDTO.getDescription());

  }



  //测试缓存
  @Test
  public void testMsgTemplateCacheManager() throws Exception {
    MessageTemplate msgTemplate;
    String type="m1";
    Long shopId=-1L;
    //第一次获取将在本地缓存保存一条数据
    msgTemplate= MessageTemplateCacheManager.getMessageTemplate(type,shopId) ;
    Assert.assertNotNull(msgTemplate);
    Assert.assertEquals(-1l,msgTemplate.getShopId(),0.0001);
    Assert.assertEquals("m1",msgTemplate.getType());
    Assert.assertEquals("m1_content", msgTemplate.getContent());
    //更新模板，在缓存server中会做标记
    msgTemplateDTO.setShopId(-1l);
    msgTemplateDTO.setType("m1");
    msgTemplateDTO.setContent("m1_content");
    dataMaintenanceController.updateMsgTemplate(model,request,response,msgTemplateDTO);
    //测试本地缓存中是否做了标记
    Assert.assertNotNull(MemCacheAdapter.get("msgTemplate_m1_-1"));
//     测试缓存超时
    MessageTemplateCacheManager.SYNC_INTERVAL=1000L;
    Thread.sleep(1000L);
    msgTemplate= MessageTemplateCacheManager.getMessageTemplate(type,shopId) ;
    Assert.assertNotNull(msgTemplate);
    Assert.assertEquals(-1l,msgTemplate.getShopId(),0.0001);
    Assert.assertEquals("m1",msgTemplate.getType());
    Assert.assertEquals("m1_content", msgTemplate.getContent());
  }

  /**
   *
   * @throws Exception
   */
  @Test
  public void testsearchMessageTemplate() throws Exception {
    Long shopId=-1l;
    pageNo=1;
    //测短信模板类型为空
    String type="";
    dataMaintenanceController.searchMessageTemplate(model,request,response,type,shopId,pageNo);
    //测短信模板类型不为空
    type="m1";
    dataMaintenanceController.searchMessageTemplate(model,request,response,type,shopId,pageNo);
    //测分页
    pageNo=2;
    dataMaintenanceController.searchMessageTemplate(model,request,response,type,shopId,pageNo);

  }




  //根据type获取单条短信模板,未使用本地缓存
  @Test
  public void testgetMessageTemplate() throws Exception {

    String type="";
    Long shopId=-1l;
    type="m1";
    msgTemplateDTO=dataMaintenanceController.getMessageTemplate(type,shopId);
    Assert.assertNotNull(msgTemplateDTO);
    Assert.assertEquals(-1l,msgTemplateDTO.getShopId(),0.0001);
    Assert.assertEquals("m1",msgTemplateDTO.getType());
    Assert.assertEquals("m1_content", msgTemplateDTO.getContent());
    //测试cachedConfig中已经保存了的情况
    msgTemplateDTO=dataMaintenanceController.getMessageTemplate(type,shopId);
    Assert.assertNotNull(msgTemplateDTO);
    Assert.assertEquals(-1l,msgTemplateDTO.getShopId(),0.0001);
    Assert.assertEquals("m1",msgTemplateDTO.getType());
    Assert.assertEquals("m1_content", msgTemplateDTO.getContent());

  }

  @Test
  public void testsaveMsgTemplate() throws Exception {
     request.getSession().setAttribute("shopId",-1l);
    msgTemplateDTO.setShopId(-1l);
    msgTemplateDTO.setType("ms");
    msgTemplateDTO.setScene(MessageScene.CUSTOMER_DEBT_MSG);
    msgTemplateDTO.setNecessary(MessageSendNecessaryType.NECESSARY);
    msgTemplateDTO.setName("msg_name");
    msgTemplateDTO.setContent("ms_content");

    dataMaintenanceController.saveMsgTemplate(model,request,response,msgTemplateDTO);
    msgTemplateDTO= (MessageTemplateDTO)model.get("msgTemplateDTO");
    Assert.assertNotNull(msgTemplateDTO);
    Assert.assertEquals(-1l,msgTemplateDTO.getShopId(),0.0001);
    Assert.assertEquals("ms",msgTemplateDTO.getType());
    Assert.assertEquals("ms_content",msgTemplateDTO.getContent());
  }

   @Test
  public void testupdateMsgTemplate() throws Exception {
    msgTemplateDTO.setShopId(-1l);
    msgTemplateDTO.setType("m1");
    msgTemplateDTO.setContent("m1_content");

    dataMaintenanceController.updateMsgTemplate(model,request,response,msgTemplateDTO);
    msgTemplateDTO= (MessageTemplateDTO)model.get("msgTemplateDTO");
    Assert.assertNotNull(msgTemplateDTO);
    Assert.assertEquals(-1l,msgTemplateDTO.getShopId(),0.0001);
    Assert.assertEquals("m1",msgTemplateDTO.getType());
    Assert.assertEquals("m1_content",msgTemplateDTO.getContent());

  }

}
