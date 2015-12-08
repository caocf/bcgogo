package com.bcgogo;

import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.notification.NotificationServiceFactory;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.payment.PaymentServiceFactory;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.schedule.ScheduleServiceFactory;
import com.bcgogo.schedule.bean.SmsSendSchedule;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.TxnServiceFactory;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.service.ISmsRechargeService;
import com.bcgogo.user.UserServiceFactory;
import com.bcgogo.util.h2.H2EventListener;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.SmsConstant;
import org.apache.commons.collections.CollectionUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractTest {

  protected INotificationService notificationService;
  protected NotificationDaoManager notificationDaoManager;
  protected ISmsRechargeService smsRechargeService;
  protected IShopBalanceService shopBalanceService;
  protected IConfigService configService;
  protected IChinapayService chinapayService;

  @BeforeClass
  public static void init() throws Exception {
    H2EventListener.startServer.set(false);

    Map<String, String> jpaProperties = new HashMap<String, String>();
    System.setProperty("unit.test", "true");
    jpaProperties.put("bcgogo.dataSource.url",
        "jdbc:h2:mem:test_bcgogo;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2" +
            ";DATABASE_EVENT_LISTENER='com.bcgogo.util.h2.H2EventListener'");
     System.setProperty("solr.solr.home", "../search/src/test/resources/solr");


    SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    new ScheduleServiceFactory(jpaProperties);
    new ConfigServiceFactory(jpaProperties);
    new NotificationServiceFactory(jpaProperties);
    new TxnServiceFactory(jpaProperties);
    new UserServiceFactory(jpaProperties);
    new PaymentServiceFactory(jpaProperties);
    ServiceManager.getService(ConfigService.class).setConfig("RecentChangedProductExpirationTime", "10", ShopConstant.BC_SHOP_ID);
  }

  public Long createShop() throws Exception {
    notificationService = ServiceManager.getService(INotificationService.class);
    shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
    configService = ServiceManager.getService(IConfigService.class);
    smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    configService.setConfig("PaymentQueryTag", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("MOCK_PAYMENT", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("MerId", "808080060692183", ShopConstant.BC_SHOP_ID);
    configService.setConfig("Version", "20100401", ShopConstant.BC_SHOP_ID);
    configService.setConfig("CurId", "156", ShopConstant.BC_SHOP_ID);
    configService.setConfig("ReturnMerBgUrl", "http://122.193.109.138:8000/", ShopConstant.BC_SHOP_ID);
    configService.setConfig("ReturnMerPgUrl", "http://122.193.109.138:8000/", ShopConstant.BC_SHOP_ID);
    configService.setConfig("GateId", "0001", ShopConstant.BC_SHOP_ID);
    configService.setConfig("ShareType", "0001", ShopConstant.BC_SHOP_ID);
    configService.setConfig("ShareA", "00055916^", ShopConstant.BC_SHOP_ID);
    configService.setConfig("MerPriKeyPath", "C:\\Tomcat\\Tomcat6.0\\lib\\MerPrK_808080060692212_20120606105157.key", ShopConstant.BC_SHOP_ID);

    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_3TONG_TRICOM_URL", "http://3tong.cn:8080/ema_new/http/SendSms", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_3TONG_ACCOUNT", "dh6763", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_3TONG_PASSWORD", "4ac74ac5bd65f4526820746c28dd7b", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_TRICOM_URL", "http://www.smswe.com:50000/sms/services/sms/", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_KEY", "richarapi", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_SECRET", "560e4c1e9d609840497ef45e38ec9f70f35427", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_USERNAME", "richar_ji", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SMS_SWE_PASSWORD", "hfps860621_", ShopConstant.BC_SHOP_ID);
    //清空job
    SmsSendSchedule smsSendSchedule = new SmsSendSchedule();
//    configService.setConfig("SmsSenderStrategy", "YiMei", ShopConstant.BC_SHOP_ID);
    configService.setConfig("SmsMarketingSenderStrategy", SmsConstant.SmsLianYuConstant.name, ShopConstant.BC_SHOP_ID);
    configService.setConfig("SmsIndustrySenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
    smsSendSchedule.processSmsJobs();
//    configService.deleteConfig("SmsSenderStrategy", ShopConstant.BC_SHOP_ID);
    configService.deleteConfig("SmsMarketingSenderStrategy", ShopConstant.BC_SHOP_ID);
    configService.deleteConfig("SmsIndustrySenderStrategy", ShopConstant.BC_SHOP_ID);
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setAccount("test");
    shopDTO.setName("test");
    ServiceManager.getService(IShopService.class).createShop(shopDTO);
    return shopDTO.getId();
  }

  public void deleteJobs() {
    notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
    notificationService = ServiceManager.getService(INotificationService.class);
    List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(System.currentTimeMillis() + SmsConstant.SMS_FAILED_DELAY);
    if (CollectionUtils.isNotEmpty(jobs)) {
      NotificationWriter notificationWriter = notificationDaoManager.getWriter();
      Object notificationStatus = notificationWriter.begin();
      try {
        for (SmsJob smsJob : jobs)
          notificationWriter.delete(SmsJob.class, smsJob.getId());
        notificationWriter.commit(notificationStatus);
      } finally {
        notificationWriter.rollback(notificationStatus);
      }
    }
  }

  public SmsRechargeDTO createRecharge(Long shopId) {
    SmsRechargeDTO smsRechargeDTO = new SmsRechargeDTO();
    smsRechargeDTO.setSmsBalance(0d);
    smsRechargeDTO.setRechargeAmount(100d);
    smsRechargeDTO.setShopId(shopId);
    smsRechargeDTO.setRechargeNumber("0000010001240029");
    smsRechargeDTO.setRechargeTime(System.currentTimeMillis());
    smsRechargeDTO.setState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_INIT);
    smsRechargeDTO = smsRechargeService.createSmsRecharge(smsRechargeDTO);
    return smsRechargeDTO;
  }

  @AfterClass
  public static void terminate() {

  }


}
