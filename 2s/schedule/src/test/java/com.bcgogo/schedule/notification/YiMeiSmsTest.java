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
//import com.bcgogo.notification.service.INotificationService;
//import com.bcgogo.schedule.bean.SmsSendSchedule;
//import com.bcgogo.service.ServiceManager;
//import com.bcgogo.utils.ShopConstant;
//import com.bcgogo.utils.SmsConstant;
//import junit.framework.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
///**
// * Created by IntelliJ IDEA.
// * User: ZhangJuntao
// * Date: 12-9-7
// * Time: 下午4:18
// */
//public class YiMeiSmsTest extends AbstractTest {
//  private static final Logger LOG = LoggerFactory.getLogger(YiMeiSmsTest.class);
//
//  @Before
//  public void setUp() throws Exception {
//    notificationService = ServiceManager.getService(INotificationService.class);
//    configService = ServiceManager.getService(IConfigService.class);
//    configService = ServiceManager.getService(IConfigService.class);
//  }
//
//   @Test
//  public void yiMeiSmsSendTest() throws Exception {
//    deleteJobs();
//    SmsSendSchedule smsSendSchedule = new SmsSendSchedule();
//    //创建 shop
//    Long shopId = createShop();
//    configService.deleteConfig("SmsSenderStrategy", ShopConstant.BC_SHOP_ID);
////    configService.setConfig("SmsSenderStrategy", "YiMei", ShopConstant.BC_SHOP_ID);
//     configService.setConfig("SmsIndustrySenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
//    String smsSenderStrategyStr = configService.getConfig("SmsIndustrySenderStrategy", ShopConstant.BC_SHOP_ID);
//    LOG.info("smsSenderStrategies:" + smsSenderStrategyStr);
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
//    ShopBalance shopBalance = new ShopBalance();
//    //创建充值账号
//    shopBalance.setSmsBalance(10d);
//    shopBalance.setShopId(shopId);
//    shopBalance.setRechargeTotal(10d);
//    ConfigWriter writer = configDaoManager.getWriter();
//    Object status = writer.begin();
//    try {
//      writer.save(shopBalance);
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//    //scene 1:正常发送短信
//    //保存1条job
//    SmsJobDTO smsJobDTO = new SmsJobDTO();
//    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//    smsJobDTO.setShopId(shopId);
//    smsJobDTO.setContent("test");
//    smsJobDTO.setReceiveMobile("15851654173,15851654173,15851654173,15851654173");
//    smsJobDTO.setType(SmsConstant.SMS_TYPE_MANUAL);
//    smsJobDTO.setStartTime(System.currentTimeMillis());
//    smsJobDTO.setSender(SenderType.Shop);
//    notificationService.sendSmsAsync(smsJobDTO);
//    List<SmsJobDTO> jobs = notificationService.getSmsJobsByShopId(shopId,0,100);
//    Assert.assertEquals(1, jobs.size());
//
//    smsSendSchedule.processSmsJobs();
//    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
//    List<OutBox> outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(1, outBoxs.size());
//    jobs = notificationService.getSmsJobsByShopId(shopId,0,100);
//    Assert.assertEquals(0, jobs.size());
//
//    smsJobDTO = new SmsJobDTO();
//    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//    smsJobDTO.setShopId(shopId);
//    smsJobDTO.setContent("test");
//    smsJobDTO.setType(SmsConstant.SMS_TYPE_MANUAL);
//    smsJobDTO.setStartTime(System.currentTimeMillis());
//    smsJobDTO.setSender(SenderType.Shop);
//    smsJobDTO.setReceiveMobile("13580806060");
//    notificationService.sendSmsAsync(smsJobDTO);
//    jobs = notificationService.getSmsJobsByShopId(shopId,0,100);
//    Assert.assertEquals(1, jobs.size());
//    shopBalance = writer.getById(ShopBalance.class, shopBalance.getId());
//    Assert.assertEquals(10.0 - 0.1 * 4, shopBalance.getSmsBalance(), 0.001);
//
//    //scene 2:手机号为空  发送失败
//
//    //scene 3:通道为空 发送失败
//    shopId = createShop();
//    configService.setConfig("SmsSenderStrategy", "YiMei", ShopConstant.BC_SHOP_ID);
//    shopBalance = new ShopBalance();
//    //创建充值账号
//    shopBalance.setSmsBalance(10d);
//    shopBalance.setShopId(shopId);
//    shopBalance.setRechargeTotal(10d);
//    writer = configDaoManager.getWriter();
//    status = writer.begin();
//    try {
//      writer.save(shopBalance);
//      writer.commit(status);
//    } finally {
//      writer.rollback(status);
//    }
//    smsJobDTO = new SmsJobDTO();
//    smsJobDTO.setShopId(shopId);
//    smsJobDTO.setContent("test");
//    smsJobDTO.setReceiveMobile("15851654173,15851654173");
//    smsJobDTO.setType(SmsConstant.SMS_TYPE_MANUAL);
//    smsJobDTO.setStartTime(System.currentTimeMillis());
//     smsJobDTO.setSender(SenderType.bcgogo);
//    notificationService.sendSmsAsync(smsJobDTO);
//    jobs = notificationService.getSmsJobsByShopId(shopId,0,100);
//    Assert.assertEquals(1, jobs.size());
//
//    smsSendSchedule.processSmsJobs();
//    outBoxs = notificationWriter.getShopOutBoxs(shopId, 0, 1000);
//    Assert.assertEquals(0, outBoxs.size());
//    jobs = notificationService.getSmsJobsByShopId(shopId,0,100);
//    Assert.assertEquals(1, jobs.size());
//    Assert.assertEquals(10.0, shopBalance.getSmsBalance(), 0.001);
//    deleteJobs();
//  }
//
//
//
//
////  @Test(expected=SmsException.class)
////  public void sendSMSTest() throws SmsException {
////    YimeiSmsSendParam param = new YimeiSmsSendParam();
////    param.setSeqid("1");
////    param.setSmsChannel(SmsChannel.INDUSTRY);
////    param.setPhone("15851654173");
////    param.setMessage("test");
////    configService.setConfig("YI_MEI_INDUSTRY_SERIAL_NUMBER", "test", ShopConstant.BC_SHOP_ID);
////    configService.setConfig("YI_MEI_INDUSTRY_PASSWORD", "test_password", ShopConstant.BC_SHOP_ID);
////    configService.setConfig("YI_MEI_INDUSTRY_SEND_SMS_URL","http://test.bcgogotest.com", ShopConstant.BC_SHOP_ID);
////    ClientFactory.getYimeiHttpSmsClient().sendSMS(param);
////  }
//
//  @Test
//  public void yiMeiSmsReportTest() throws Exception {
////     SmsYiMeiService smsYiMeiService = new SmsYiMeiService();
////    smsYiMeiService.getReport(SmsChannel.INDUSTRY);
//  }
//
//  public static void main(String[] args) throws Exception {
//
//  //发送
////    SmsYiMeiSender smsYiMeiSender = new SmsYiMeiSender();
////    SmsSendDTO smsSendDTO = new SmsSendDTO();
////    smsSendDTO.setReceiveMobile("15962141710");
////
////    smsSendDTO.setContent("欠款备忘：2012年03月21日，车辆京B00000消费项目：、；材料：三角警示牌1.0个、，应收款800000.0元，实收0.0元，欠款800000.0元，预计还款日：2012-03-21。");
////    smsSendDTO.setContent("折扣备忘：2012年03月21日，车辆京B00000消费项目：呵呵、；材料：机油4.0个、，应收款3280.0元，实收3000.0元，折扣80.0元。");
////
////    smsSendDTO.setDeductMoney(false);
////
////    smsSendDTO.setSmsChannel(SmsChannel.INDUSTRY);
////    smsSendDTO.setSmsChannel(SmsChannel.MARKETING);
////
////    smsSendDTO.setId(System.currentTimeMillis());
////    SmsSendResult smsSendResult = smsYiMeiSender.sendSms(smsSendDTO);
////    System.out.println(smsSendResult.getSmsResponse() + " " + smsSendResult.getSmsResponseReason());
//
//  //查询
////    YimeiEmbeddedSmsClient smsYiMeiService = new YimeiEmbeddedSmsClient();
////    smsYiMeiService.register(SmsChannel.INDUSTRY);
////    smsYiMeiService.register(SmsChannel.MARKETING);
//
////    smsYiMeiService.getBalance(SmsChannel.INDUSTRY);
////    smsYiMeiService.getBalance(SmsChannel.MARKETING);
//
////    smsYiMeiService.logout(SmsChannel.MARKETING);
////    smsYiMeiService.logout(SmsChannel.INDUSTRY);
////
////    smsYiMeiService.getReport(SmsChannel.INDUSTRY);
////    smsYiMeiService.getReport(SmsChannel.MARKETING);
//  }
//
//}
