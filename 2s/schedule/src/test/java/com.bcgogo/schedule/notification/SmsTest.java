//package com.bcgogo.schedule.notification;
//
//import com.bcgogo.AbstractTest;
//import com.bcgogo.config.model.ConfigDaoManager;
//import com.bcgogo.config.model.ConfigWriter;
//import com.bcgogo.config.model.ShopBalance;
//import com.bcgogo.config.service.IConfigService;
//import com.bcgogo.enums.notification.SmsChannel;
//import com.bcgogo.enums.sms.SenderType;
//import com.bcgogo.notification.dto.SmsJobDTO;
//import com.bcgogo.notification.model.NotificationWriter;
//import com.bcgogo.notification.model.OutBox;
//import com.bcgogo.notification.model.SmsJob;
//import com.bcgogo.notification.service.INotificationService;
//import com.bcgogo.notification.smsSend.SmsYiMeiSenderMock;
//import com.bcgogo.schedule.bean.SmsSendSchedule;
//import com.bcgogo.service.ServiceManager;
//import com.bcgogo.user.model.Customer;
//import com.bcgogo.user.model.ShopPlan;
//import com.bcgogo.user.model.UserDaoManager;
//import com.bcgogo.user.model.UserWriter;
//import com.bcgogo.utils.ShopConstant;
//import com.bcgogo.utils.SmsConstant;
//import junit.framework.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.List;
//
///**
//* Created by IntelliJ IDEA.
//* User: caiweili
//* Date: 3/4/12
//* Time: 5:53 PM
//* To change this template use File | Settings | File Templates.
//*/
//public class SmsTest extends AbstractTest {
//  @Before
//  public void setUp() throws Exception {
//    notificationService = ServiceManager.getService(INotificationService.class);
//  }
//
//  @Test
//  public void testSendSmsAsync() throws Exception {
//    Long shopId = createShop();
//    ServiceManager.getService(IConfigService.class).setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
//    ShopBalance shopBalance = new ShopBalance();
//    shopBalance.setSmsBalance(100d);
//    shopBalance.setShopId(shopId);
//    shopBalance.setRechargeTotal(100d);
//    ConfigWriter writer = configDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      writer.save(shopBalance);
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//    for (int i = 0; i < 100; i++) {
//      SmsJobDTO smsJobDTO = new SmsJobDTO();
//      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//      smsJobDTO.setShopId(shopId);
//      smsJobDTO.setContent("test");
//      smsJobDTO.setSender(SenderType.Shop);
//      smsJobDTO.setReceiveMobile("15851654173");
//      smsJobDTO.setType(SmsConstant.SMS_TYPE_MANUAL);
//      smsJobDTO.setStartTime(System.currentTimeMillis() - 100);
//      notificationService.sendSmsAsync(smsJobDTO);
//    }
//    List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() - 100);
//    Assert.assertEquals(10, jobs.size());
//
//    SmsSendSchedule smsSendSchedule = new SmsSendSchedule();
//    smsSendSchedule.processSmsJobs();
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    List<OutBox> outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(100, outBoxs.size());
//    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() - 100);
//    Assert.assertEquals(0, jobs.size());
//    shopBalance = writer.getById(ShopBalance.class, shopBalance.getId());
//    Assert.assertEquals(90.0, shopBalance.getSmsBalance(), 0.001);
//  }
//
//  @Test
//  public void testSendSmsAsyncFails() throws Exception {
//    Long shopId = createShop();
//    ServiceManager.getService(IConfigService.class).setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
//    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
//
//    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    SmsYiMeiSenderMock.setCode(SmsConstant.SmsYiMeiConstant.SMS_RESPONSE_ERROR);
//    ShopBalance shopBalance = new ShopBalance();
//    shopBalance.setSmsBalance(100d);
//    shopBalance.setShopId(shopId);
//    shopBalance.setRechargeTotal(100d);
//    ConfigWriter writer = configDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      writer.save(shopBalance);
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//    for (int i = 0; i < 10; i++) {
//      SmsJobDTO smsJobDTO = new SmsJobDTO();
//      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//      smsJobDTO.setShopId(shopId);
//      smsJobDTO.setContent("test");
//      smsJobDTO.setSender(SenderType.Shop);
//      smsJobDTO.setReceiveMobile("15851654173");
//      smsJobDTO.setType(SmsConstant.SMS_TYPE_MANUAL);
//      smsJobDTO.setStartTime(System.currentTimeMillis() - 100);
//      notificationService.sendSmsAsync(smsJobDTO);
//    }
//    List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis());
//    Assert.assertEquals(10, jobs.size());
//
//    SmsSendSchedule smsSendSchedule = new SmsSendSchedule();
//    smsSendSchedule.processSmsJobs();
//
//    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis());
//    Assert.assertEquals(0, jobs.size());
//
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    List<OutBox> outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(0, outBoxs.size());
//    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() + SmsConstant.SMS_FAILED_DELAY + 100);
//    Assert.assertEquals(10, jobs.size());
//    Assert.assertEquals(1, jobs.get(0).getSendTimes().intValue());
//    shopBalance = writer.getById(ShopBalance.class, shopBalance.getId());
//    Assert.assertEquals(100, shopBalance.getSmsBalance(), 0.001);
//  }
//
//  @Test
//  public void testSendSmsAsyncFailsAndRetry() throws Exception {
//    Long shopId = createShop();
//    ServiceManager.getService(IConfigService.class).setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
//    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
//
//    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    SmsYiMeiSenderMock.setCode(-1);
//    ShopBalance shopBalance = new ShopBalance();
//    shopBalance.setSmsBalance(100d);
//    shopBalance.setShopId(shopId);
//    shopBalance.setRechargeTotal(100d);
//    ConfigWriter writer = configDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      writer.save(shopBalance);
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//    for (int i = 0; i < 10; i++) {
//      SmsJobDTO smsJobDTO = new SmsJobDTO();
//      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//      smsJobDTO.setShopId(shopId);
//      smsJobDTO.setContent("test");
//      smsJobDTO.setSender(SenderType.Shop);
//      smsJobDTO.setReceiveMobile("15851654173");
//      smsJobDTO.setType(SmsConstant.SMS_TYPE_MANUAL);
//      smsJobDTO.setStartTime(System.currentTimeMillis() - 100);
//      notificationService.sendSmsAsync(smsJobDTO);
//    }
//    List<SmsJobDTO> jobs =  notificationService.getSmsJobsByShopId(shopId, 0, 100);
//    Assert.assertEquals(10, jobs.size());
//
//    SmsSendSchedule smsSendSchedule = new SmsSendSchedule();
//    smsSendSchedule.processSmsJobs();
//
//    List<SmsJob>  smsJobs =  notificationService.getSmsJobsByStartTime(System.currentTimeMillis());
//    Assert.assertEquals(0, smsJobs.size());
//
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    List<OutBox> outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(0, outBoxs.size());
//    jobs =  notificationService.getSmsJobsByShopId(shopId, 0, 100);
//    Assert.assertEquals(10, jobs.size());
//    Assert.assertEquals(1, jobs.get(0).getSendTimes());
//    shopBalance = writer.getById(ShopBalance.class, shopBalance.getId());
//    Assert.assertEquals(100.0, shopBalance.getSmsBalance(), 0.001);
//
//    smsSendSchedule.setCurrentTime(System.currentTimeMillis() + SmsConstant.SMS_FAILED_DELAY);
//    smsSendSchedule.processSmsJobs();
//    outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(0, outBoxs.size());
//    jobs = notificationService.getSmsJobsByShopId(shopId, 0, 100);
//    Assert.assertEquals(2, jobs.get(0).getSendTimes());
//    smsSendSchedule.setCurrentTime(System.currentTimeMillis() + SmsConstant.SMS_FAILED_DELAY );
//    smsSendSchedule.processSmsJobs();
//    outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(0, outBoxs.size());
//    jobs =  notificationService.getSmsJobsByShopId(shopId, 0, 100);
//    Assert.assertEquals(3, jobs.get(0).getSendTimes());
//
//    smsSendSchedule.setCurrentTime(System.currentTimeMillis() + SmsConstant.SMS_FAILED_DELAY );
//    smsSendSchedule.processSmsJobs();
//    outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(0, outBoxs.size());
//    jobs =  notificationService.getSmsJobsByShopId(shopId, 0, 100);
//    Assert.assertEquals(0, jobs.size());
//  }
//
//  @Test
//  public void testSendSmsSync() throws Exception {
//    Long shopId = createShop();
//    ServiceManager.getService(IConfigService.class).setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
//    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    SmsYiMeiSenderMock.setCode(0);
//    ShopBalance shopBalance = new ShopBalance();
//    shopBalance.setSmsBalance(100d);
//    shopBalance.setShopId(shopId);
//    shopBalance.setRechargeTotal(100d);
//    ConfigWriter writer = configDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      writer.save(shopBalance);
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//    for (int i = 0; i < 100; i++) {
//      SmsJobDTO jobDTO = new SmsJobDTO();
//      jobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//      jobDTO.setContent("test");
//      jobDTO.setName("testshop");
//      jobDTO.setReceiveMobile("15851654173");
//      jobDTO.setShopId(shopId);
//      jobDTO.setSender(SenderType.Bcgogo);
//      notificationService.sendSmsSync(jobDTO);
//    }
//    List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() - 100);
//    Assert.assertEquals(0, jobs.size());
//    SmsSendSchedule smsSendSchedule = new SmsSendSchedule();
//    smsSendSchedule.processSmsJobs();
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    List<OutBox> outBoxes = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(100, outBoxes.size());
//    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() - 100);
//    Assert.assertEquals(0, jobs.size());
//    shopBalance = writer.getById(ShopBalance.class, shopBalance.getId());
//    //bcgogo发送短信不要 扣费
//    Assert.assertEquals(100.0, shopBalance.getSmsBalance(), 0.001);
//  }
//
//  @Test
//  public void testSendSmsSyncFail() throws Exception {
//    Long shopId = createShop();
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
//    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    SmsYiMeiSenderMock.setCode(-1);
//    ShopBalance shopBalance = new ShopBalance();
//    shopBalance.setSmsBalance(100d);
//    shopBalance.setShopId(shopId);
//    shopBalance.setRechargeTotal(100d);
//    ConfigWriter writer = configDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      writer.save(shopBalance);
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//    for (int i = 0; i < 10; i++) {
//      SmsJobDTO jobDTO = new SmsJobDTO();
//      jobDTO.setShopId(shopId);
//      jobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//      jobDTO.setContent("test");
//      jobDTO.setName("test");
//      jobDTO.setReceiveMobile("15851654173");
//      jobDTO.setSender(SenderType.Shop);
//      notificationService.sendSmsSync(jobDTO);
//    }
//    List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() - 100);
//    Assert.assertEquals(0, jobs.size());
//    SmsSendSchedule smsSendSchedule = new SmsSendSchedule();
//    smsSendSchedule.processSmsJobs();
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    List<OutBox> outBoxs = notificationWriter.getShopOutBoxs(ShopConstant.BC_SHOP_ID, 0, 1000);
//    //Assert.assertEquals(0, outBoxs.size());
//    jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() - 100);
//    Assert.assertEquals(0, jobs.size());
//    shopBalance = writer.getById(ShopBalance.class, shopBalance.getId());
//    Assert.assertEquals(100.0, shopBalance.getSmsBalance(), 0.001);
//  }
//
//  @Test
//  public void testSmsSendAllShopCustomers() throws Exception {
//    Long shopId = createShop();
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
//    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
//    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//    configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    SmsYiMeiSenderMock.setCode(-1);
//    //给店面充值
//    ShopBalance shopBalance = new ShopBalance();
//    shopBalance.setSmsBalance(100d);
//    shopBalance.setShopId(shopId);
//    shopBalance.setRechargeTotal(100d);
//    ConfigWriter writer = configDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      writer.save(shopBalance);
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//    //保存 客户
//    UserWriter userWriter = userDaoManager.getWriter();
//    for (int i = 0; i < 98; i++) {
//      Object userStatus = userWriter.begin();
//      try {
//        Customer customer = new Customer();
//        customer.setShopId(shopId);
//        customer.setMobile("15851654173");
//        userWriter.save(customer);
//        userWriter.commit(userStatus);
//      } finally {
//        userWriter.rollback(userStatus);
//      }
//    }
//
//    ShopPlan shopPlan = new ShopPlan();
//    shopPlan.setShopId(shopId);
//    shopPlan.setCustomerType("all");
//    shopPlan.setContent("张峻滔测试");
//    Object statusSP = userWriter.begin();
//    try {
//      userWriter.save(shopPlan);
//      userWriter.commit(statusSP);
//    } finally {
//      userWriter.rollback(statusSP);
//    }
//  }
//
//}
