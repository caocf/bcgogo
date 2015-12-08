//package com.bcgogo.notification.client.yimei;
//
//import cn.emay.sdk.client.api.Client;
//import com.bcgogo.config.service.IConfigService;
//import com.bcgogo.enums.notification.SmsChannel;
//import com.bcgogo.notification.client.SmsClient;
//import com.bcgogo.notification.client.SmsParam;
//import com.bcgogo.service.ServiceManager;
//import com.bcgogo.utils.ShopConstant;
//import com.bcgogo.utils.SmsConstant;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
///**
// * User: ZhangJuntao
// * Date: 12-9-7
// * Time: 下午1:22
// * comment zjt: client jar 中存在 线程泄露
// */
//@Component
//public class YimeiEmbeddedSmsClient implements SmsClient {
//  private static final Logger LOG = LoggerFactory.getLogger(YimeiEmbeddedSmsClient.class);
//  private static Client industryClient = YimeiEmbeddedSmsClient.getIndustryInstance(); //行业
//  private static Client marketingClient = YimeiEmbeddedSmsClient.getMarketingInstance();//营销
//
//  public synchronized static Client getIndustryInstance() {
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    if (industryClient == null) {
//      try {
//        String industrySerialNumber = configService.getConfig("YI_MEI_INDUSTRY_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID);
//        String industryPassword = configService.getConfig("YI_MEI_INDUSTRY_PASSWORD", ShopConstant.BC_SHOP_ID);
//        industryClient = new Client(industrySerialNumber, industryPassword);
//      } catch (Exception e) {
//        LOG.error("行业短信通道实例获得失败！");
//        LOG.error(e.getMessage(), e);
//      }
//    }
//    return industryClient;
//  }
//
//  public synchronized static Client getMarketingInstance() {
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    if (marketingClient == null) {
//      try {
//        String marketingSerialNumber = configService.getConfig("YI_MEI_MARKETING_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID);
//        String marketingPassword = configService.getConfig("YI_MEI_MARKETING_PASSWORD", ShopConstant.BC_SHOP_ID);
//        marketingClient = new Client(marketingSerialNumber, marketingPassword);
//      } catch (Exception e) {
//        LOG.error("营销短信通道实例获得失败！");
//        LOG.error(e.getMessage(), e);
//      }
//    }
//    return marketingClient;
//  }
//
//  @Override
//  public String sendSMS(SmsParam smsSendParam) {
//    YimeiSmsSendParam param = (YimeiSmsSendParam) smsSendParam;
//    int code = 0;
//    if (param.getSeqid() == null) {
//      param.setSeqid(String.valueOf(System.nanoTime()));
//    }
//    try {
//      if (param.getSmsChannel().equals(SmsChannel.MARKETING)) {
//        // 营销
//        code = marketingClient.sendSMSEx(param.getPhone().split(","), param.getMessage(), param.getAddserial(),
//            "GBK", param.getSmspriority(), Long.valueOf(param.getSeqid()));
//      } else if (param.getSmsChannel().equals(SmsChannel.INDUSTRY)) {
//        // 行业
//        code = industryClient.sendSMSEx(param.getPhone().split(","), param.getMessage(), param.getAddserial(),
//            "GBK", param.getSmspriority(), Long.valueOf(param.getSeqid()));
//      } else {
//        throw new Exception("there is not this " + param.getSmsChannel() + " in YiMei.");
//      }
//    } catch (Exception e) {
//      LOG.error(e.getMessage(), e);
//      LOG.error("查询失败！");
//      code = -1;
//    }
//    return String.valueOf(code);
//  }
//
//  public int sendSMS(String[] mobiles, String smsContent, String addSerial, String srcCharset, int smsPriority, Long smsId, SmsChannel channel) {
//    int code = 0;
//    if (smsId == null) {
//      smsId = System.currentTimeMillis();
//    }
//    try {
//      if (channel.equals(SmsChannel.MARKETING)) {
//        // 营销
//        code = marketingClient.sendSMSEx(mobiles, smsContent, "", srcCharset, smsPriority, smsId);
//      } else if (channel.equals(SmsChannel.INDUSTRY)) {
//        // 行业
//        code = industryClient.sendSMSEx(mobiles, smsContent, "", srcCharset, smsPriority, smsId);
//      } else {
//        throw new Exception("there is not this " + channel + " in YiMei.");
//      }
//    } catch (Exception e) {
//      LOG.error(e.getMessage(), e);
//      LOG.error("查询失败！");
//      code = -1;
//    }
//    return code;
//  }
//
//  // 注册
//  public void register(SmsParam yimeiSmsParam) throws Exception {
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    YimeiSmsParam param = (YimeiSmsParam) yimeiSmsParam;
//    LOG.info("======亿美-{}-注册序列号======", param.getSmsChannel());
//    int code = 0;
//    String key;
//    // 行业
//    if (param.getSmsChannel().equals(SmsChannel.INDUSTRY)) {
//      key = configService.getConfig("YI_MEI_INDUSTRY_KEY", ShopConstant.BC_SHOP_ID);
//      code = industryClient.registEx(key);
//      LOG.warn("行业-注册结果[{}:{}]:", code, SmsConstant.SmsYiMeiConstant.getSmsResponseCodeMap(code));
//    } else if (param.getSmsChannel().equals(SmsChannel.MARKETING)) {
//      // 注册营销
//      key = configService.getConfig("YI_MEI_MARKETING_KEY", ShopConstant.BC_SHOP_ID);
//      code = marketingClient.registEx(key);
//      LOG.warn("营销-注册结果:", code, SmsConstant.SmsYiMeiConstant.getSmsResponseCodeMap(code));
//    }
//    if (code != SmsConstant.SmsYiMeiConstant.SMS_STATUS_SUCCESS) {
//      throw new Exception("亿美注册操作失败" + code + SmsConstant.SmsYiMeiConstant.getSmsResponseCodeMap(code));
//    }
//  }
//
//  //余额查询
//  public String queryBalance(SmsParam yimeiSmsParam) throws Exception {
//    YimeiSmsParam param = (YimeiSmsParam) yimeiSmsParam;
//    LOG.info("======亿美-{}-查询余额======", param.getSmsChannel());
//    Double balance = null;
//    // 行业
//    if (param.getSmsChannel().equals(SmsChannel.INDUSTRY)) {
//      balance = Double.valueOf(industryClient.getBalance());
//      LOG.info("行业-查询余额:{}", balance);
//    } else if (param.getSmsChannel().equals(SmsChannel.MARKETING)) {
//      // 营销
//      balance = Double.valueOf(marketingClient.getBalance());
//      LOG.info("营销-查询余额:{}", balance);
//    } else {
//      throw new Exception("there is not this " + param.getSmsChannel() + " in YiMei.");
//    }
//    return String.valueOf(balance);
//  }
//
////  @Override
////  public Long getSurplusNumber() {
////    try {
////      return Math.round(industryClient.getBalance() * 10L) + Math.round(marketingClient.getBalance() * 10L);
////    } catch (Exception e) {
////      LOG.error(e.getMessage());
////      return 0l;
////    }
////  }
//
//
//  public void logout(SmsParam yimeiSmsParam) throws Exception {
//    YimeiSmsParam param = (YimeiSmsParam) yimeiSmsParam;
//    LOG.info("======亿美-{}-注销======", param.getSmsChannel());
//    int code;
//    // 行业
//    if (param.getSmsChannel().equals(SmsChannel.INDUSTRY)) {
//      code = industryClient.logout();
//      LOG.warn("行业-注销结果:[{}:{}]", code, SmsConstant.SmsYiMeiConstant.getSmsResponseCodeMap(code));
//    } else if (param.getSmsChannel().equals(SmsChannel.MARKETING)) {
//      //营销
//      code = marketingClient.logout();
//      LOG.warn("营销-注销结果:[{}:{}]", code, SmsConstant.SmsYiMeiConstant.getSmsResponseCodeMap(code));
//    } else {
//      throw new Exception("there is not this " + param.getSmsChannel() + " in YiMei.");
//    }
//    if (code != SmsConstant.SmsYiMeiConstant.SMS_STATUS_SUCCESS) {
//      throw new Exception("亿美注销操作失败:" + code + SmsConstant.SmsYiMeiConstant.getSmsResponseCodeMap(code));
//    }
//  }
//
////  public void getReport(SmsParam yimeiSmsParam) {
////    YimeiSmsParam param = (YimeiSmsParam) yimeiSmsParam;
////    try {
////      List reportList;
////      if (param.getSmsChannel().equals(SmsChannel.INDUSTRY)) {
////        reportList = industryClient.getReport();
////      } else if (param.getSmsChannel().equals(SmsChannel.MARKETING)) {
////        //营销
////        reportList = marketingClient.getReport();
////      } else {
////        throw new Exception("there is not this " + param.getSmsChannel() + " in YiMei.");
////      }
////      if (CollectionUtils.isEmpty(reportList)) return;
////      for (Object report : reportList) {
////        StatusReport statusReport = (StatusReport) report;
////        LOG.info("通道[" + param.getSmsChannel() + "]mobile:" + statusReport.getMobile() + ",sqId:" + statusReport.getSeqID() + ",errorCode:" + statusReport.getErrorCode() + ",reportStatus:" + statusReport.getErrorCode());
////      }
////    } catch (Exception e) {
////      LOG.error(e.getMessage(), e);
////      LOG.error("查询Report失败！");
////    }
////  }
//
//
//}
