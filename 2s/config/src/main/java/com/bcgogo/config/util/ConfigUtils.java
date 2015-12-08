package com.bcgogo.config.util;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 上午10:43
 */
public class ConfigUtils {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigUtils.class);

  public static final Long ONE_MINUTE_MILLISECOND = 60000l;


  /**
   * app
   * 访问权限校验开关
   *
   * @return boolean
   */
  public static boolean appNeedPermissionValidate() {
    return StringUtil.isEqual(ServiceManager.getService(IConfigService.class).getConfig(ConfigConstant.API_PERMISSION_SWITCH, ShopConstant.BC_SHOP_ID), "on");
  }

  /**
   * app从obd读取数据的周期间隔，单位为毫秒
   */
  public static Long getAppObdReadInterval() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_OBD_READ_INTERVAL, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? (60000L) : Long.valueOf(time));
  }

  public static int getImpactVideoUploadLimit() {
    String limit = ServiceManager.getService(IConfigService.class).getConfig("IMPACT_VIDEO_UPLOAD_LIMIT", ShopConstant.BC_SHOP_ID);
    ;
    return NumberUtil.intValue(limit, 5);
  }


  /**
   * 车主剩余油量提醒配置,单位为%
   */
  public static String getAppRemainOilMassWarn() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_REMAIN_OIL_MASS_WARN, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? ("15_25") : time);
  }

  /**
   * app故障码提醒周期,单位小时
   */
  public static Double getAppVehicleErrorCodeWarnIntervals() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_VEHICLE_ERROR_CODE_WARN_INTERVALS, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? (24d) : Double.valueOf(time));
  }

  /**
   * app从服务端读取数据的周期间隔，单位为毫秒
   */
  public static Long getAppServerReadInterval() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_SERVER_READ_INTERVAL, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? (60000L) : Long.valueOf(time));
  }

  /**
   * app向服务端发送车辆里程数的公里数间隔，单位为公里
   */
  public static Double getAppMileageInformInterval() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String time = configService.getConfig(ConfigConstant.APP_MILEAGE_INFORM_INTERVAL, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? (100D) : Long.valueOf(time));
  }

  /**
   * 客服电话
   */
  public static String getCustomerServicePhone() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.CUSTOMER_SERVICE_PHONE, ShopConstant.BC_SHOP_ID);
  }

  public static boolean isCustomizerConfigOpen() {
    return "ON".equals(ServiceManager.getService(IConfigService.class)
      .getConfig("CUSTOMIZER_CONFIG", ShopConstant.BC_SHOP_ID));
  }

  public static boolean isPushMessageSwitchOn() {
    return "ON".equals(ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.PUSH_MESSAGE_SWITCH, ShopConstant.BC_SHOP_ID));
  }

  //毫秒
  public static Long getClientNextRequestTimeInterval() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String time = configService.getConfig("ClientNextRequestTimeInterval", ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? 10 : Integer.valueOf(time)) * ONE_MINUTE_MILLISECOND;
  }


  public static Long getClientNextRequestTime() {
    return System.currentTimeMillis() + getClientNextRequestTimeInterval();
  }

  public static Long getPushMessageLifeCycle(Long createdTime) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String time = configService.getConfig(ConfigConstant.PUSH_MESSAGE_LIFE_CYCLE, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? (432000000L) : Integer.valueOf(time)) + (createdTime == null ? System.currentTimeMillis() : createdTime);
  }

  public static boolean isWholesalerVersion(Long shopVersionId) {
    if (shopVersionId == null) return false;
    String wholesalerShopVersionIds = ServiceManager.getService(IConfigService.class)
      .getConfig("WholesalerShopVersions", ShopConstant.BC_SHOP_ID);
    return StringUtils.isNotBlank(wholesalerShopVersionIds) && wholesalerShopVersionIds.contains(shopVersionId.toString());
  }

  public static List<Long> getWholesalerVersion() {
    String wholesalerShopVersionIds = ServiceManager.getService(IConfigService.class)
      .getConfig("WholesalerShopVersions", ShopConstant.BC_SHOP_ID);
    return NumberUtil.parseLongValues(wholesalerShopVersionIds);
  }

  public static List<Long> getCommonShopVersion() {
    String commonShopVersionIds = ServiceManager.getService(IConfigService.class)
      .getConfig("CommonShopVersions", ShopConstant.BC_SHOP_ID);
    return NumberUtil.parseLongValues(commonShopVersionIds);
  }

  public static Integer getCancelRecommendAssociatedCountLimit() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String time = configService.getConfig("CancelRecommendAssociatedCountLimit", ShopConstant.BC_SHOP_ID);
    return StringUtils.isBlank(time) ? 10 : Integer.valueOf(time);
  }

  //客户端版本
  public static String getClientCurrentVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig("ClientCurrentVersion", ShopConstant.BC_SHOP_ID);
  }

  //客户端 更新请求链接
  public static String getClientUpdateUrl() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig("ClientUpdateUrl", ShopConstant.BC_SHOP_ID);
  }

  //ff 当前版本
  public static String getFirefoxCurrentVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig("FirefoxCurrentVersion", ShopConstant.BC_SHOP_ID);
  }

  //ff 更新请求链接
  public static String getFirefoxUpdateUrl() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig("FirefoxUpdateUrl", ShopConstant.BC_SHOP_ID);
  }

  /**
   * 得到本机IP地址，并与config表中的TOMCAT_IPS中的设置作比对，如果找到则返回配置中的IP，如果找不到，返回第一个本机地址
   *
   * @return
   */
  public static String getLocalIpWithConfigTomcats() {
    String[] configIps = ServiceManager.getService(IConfigService.class).getConfigTomcatIps();
    List<String> localIps = getLocalhostIps();
    if (CollectionUtils.isEmpty(localIps)) {
      return null;
    }
    if (ArrayUtils.isEmpty(configIps)) {
      return localIps.get(0);
    }
    for (String localIp : localIps) {
      if (ArrayUtils.contains(configIps, localIp)) {
        return localIp;
      }
    }
    return localIps.get(0);
  }

  /**
   * 得到本机的所有IP地址
   *
   * @return
   */
  public static List<String> getLocalhostIps() {
    List<String> ips = new ArrayList<String>();
    try {
      Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
      InetAddress ip = null;
      while (allNetInterfaces.hasMoreElements()) {
        NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
        Enumeration addresses = netInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          ip = (InetAddress) addresses.nextElement();

          if (ip != null && ip instanceof Inet4Address) {
            ips.add(ip.getHostAddress());
          }
        }
      }
    } catch (SocketException e) {
      LOG.error("ConfigUtils getLocalIps error", e);
    }

    if (CollectionUtils.isEmpty(ips)) {
      try {
        ips.add(InetAddress.getLocalHost().getHostAddress());
      } catch (UnknownHostException e) {
        LOG.error("ConfigUtils getLocalIps error", e);
      }
    }

    return ips;
  }

  public static String getUpYunBucket() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_BUCKET, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunUsername() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_USERNAME, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunPassword() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_PASSWORD, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunSecretKey() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_SECRET_KEY, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunDomainUrl() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_DOMAIN_URL, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunSeparator() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_SEPARATOR, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunNotFindImagePath() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_NOT_FIND_IMAGE_PATH, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunImagePath() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_IMAGE_PATH, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunAllowImageType() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_ALLOW_IMAGE_TYPE, ShopConstant.BC_SHOP_ID);
  }

  public static String getUpYunUploadDomainUrl() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_UPLOAD_DOMAIN_URL, ShopConstant.BC_SHOP_ID);
  }

  /**
   * 单位是min
   *
   * @return
   */
  public static int getUpYunExpiration() {
    return NumberUtil.intValue(ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.UP_YUN_EXPIRATION, ShopConstant.BC_SHOP_ID), 20);
  }


  public static int getRecentlyViewedProductNum() {
    return NumberUtil.intValue(ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.RECENTLY_VIEWED_PRODUCT_NUM, ShopConstant.BC_SHOP_ID), 5);
  }

  public static int getRecentlyUsedProductCategoryNum() {
    return NumberUtil.intValue(ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.RECENTLY_USED_PRODUCT_CATEGORY_NUM, ShopConstant.BC_SHOP_ID), 10);
  }

  public static int getRecentlyUsedSmsContactNum() {
    return NumberUtil.intValue(ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.RECENTLY_USED_SMS_CONTACT_NUM, ShopConstant.BC_SHOP_ID), 20);
  }

  //app 安卓最新版本
  public static String getAndroidAppVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_VERSION_ANDROID, ShopConstant.BC_SHOP_ID);
  }

  //IOS app最新版本
  public static String getIOSAppVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_VERSION_IOS, ShopConstant.BC_SHOP_ID);
  }

  //安卓 app 更新URL
  public static String getAndroidAppUpgradeURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_ANDROID_UPGRADE_URL, ShopConstant.BC_SHOP_ID);
  }

  //IOS app 更新URL
  public static String getISOAppUpgradeURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_ISO_UPGRADE_URL, ShopConstant.BC_SHOP_ID);
  }

  //自动打印客户端最新版本号
  public static String getYunPrintClientVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.YUN_PRINT_CLIENT_VERSION, ShopConstant.BC_SHOP_ID);
  }

  public static int getAppUserSendSMSLimits() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_USER_SEND_SMS_LIMITS, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? 3 : Integer.valueOf(time));
  }

  /**
   * 创建过期预约单提醒消息
   */
  public static Long[] getOverdueAppointRemindIntervals() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.OVERDUE_APPOINT_REMIND_INTERVALS, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? new Long[]{-10800000l, 3600000L} : NumberUtil.parseLongValueArray(time));
  }

  /**
   * 保养里程
   */
  public static Double[] getAppVehicleMaintainMileageIntervals() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_VEHICLE_MAINTAIN_MILEAGE_INTERVALS, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? new Double[]{-100d, 500D} : NumberUtil.parseDoubleValueArray(time));
  }

  /**
   * 保养时间
   */
  public static Long[] getAppVehicleMaintainTimeIntervals() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_VEHICLE_MAINTAIN_TIME_INTERVALS, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? new Long[]{-172800000L, 86400000L} : NumberUtil.parseLongValueArray(time));
    //2d        1d
  }

  /**
   * 保险时间
   */
  public static Long[] getAppVehicleInsuranceTimeIntervals() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_VEHICLE_INSURANCE_TIME_INTERVALS, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? new Long[]{-172800000L, 86400000L} : NumberUtil.parseLongValueArray(time));
  }

  /**
   * 验车时间
   */
  public static Long[] getAppVehicleExamineTimeIntervals() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_VEHICLE_EXAMINE_TIME_INTERVALS, ShopConstant.BC_SHOP_ID);
    return (StringUtils.isBlank(time) ? new Long[]{-172800000L, 86400000L} : NumberUtil.parseLongValueArray(time));
  }

  public static Integer getAppVehicleNextMaintainMileagePushMessageRemindTimesLimit() {
    String time = ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.APP_VEHICLE_NEXT_MAINTAIN_MILEAGE_PUSH_MESSAGE_REMIND_TIMES_LIMIT, ShopConstant.BC_SHOP_ID);
    return NumberUtil.intValue(time, 3);
  }

  public static Long getPushMessageKeepDay() {
    String days = ServiceManager.getService(IConfigService.class).getConfig(ConfigConstant.PUSH_MESSAGE_KEEP_DAY, ShopConstant.BC_SHOP_ID);
    return NumberUtil.intValue(days, 0) * 24 * 60 * 60 * 1000l;
  }

  public static String getBcgogoPhone() {
    return ServiceManager.getService(IConfigService.class).getConfig(ConfigConstant.BCGOGO_PHONE, ShopConstant.BC_SHOP_ID);
  }

  public static String getBcgogoQQ() {
    return ServiceManager.getService(IConfigService.class).getConfig(ConfigConstant.BCGOGO_QQ, ShopConstant.BC_SHOP_ID);
  }

  public static Double getBcgogoSoftAnnualPrice() {
    return NumberUtil.doubleValue(ServiceManager.getService(IConfigService.class).getConfig(ConfigConstant.BCGOGO_SOFT_ANNUAL_PRICE, ShopConstant.BC_SHOP_ID), 1000d);
  }

  //行业短信开关
  public static boolean isSmsIndustryTagOn() {
    return ServiceManager.getService(IConfigService.class).getConfig(ConfigConstant.SMS_INDUSTRY_TAG, ShopConstant.BC_SHOP_ID).equalsIgnoreCase("on");
  }

  public static String getJuheViolateRegulationKey() {
    String key = ServiceManager.getService(IConfigService.class).getConfig(ConfigConstant.JUHE_VIOLATE_REGULATION_KEY, ShopConstant.BC_SHOP_ID);
    return StringUtil.isEmpty(key) ? "60ad2a9b3c7bcda13b781dabe01fe843" : key;
  }

  public static Double getSearchShopLocationDistance() {
    String str = ServiceManager.getService(IConfigService.class).getConfig(ConfigConstant.SEARCH_SHOP_LOCATION_DISTANCE, ShopConstant.BC_SHOP_ID);
    return StringUtil.isEmpty(str) ? 200d : Double.valueOf(str);
  }

  public static String getHttpServerURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.HTTP_SERVER_URL, ShopConstant.BC_SHOP_ID);
  }

  public static String getSmsPreferentialPolicyImagePath() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.SMS_PREFERENTIAL_POLICY_PATH, ShopConstant.BC_SHOP_ID);
  }

  public static String getBcgogoOrderSmsPhoneNumber() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_ORDER_SMS_PHONE_NUMBER, ShopConstant.BC_SHOP_ID);
  }

  public static boolean isFourSShopVersion(Long shopVersionId) {
    if (shopVersionId == null) return false;
    String fourSShopVersionIds = ServiceManager.getService(IConfigService.class)
      .getConfig("fourSShopVersions", ShopConstant.BC_SHOP_ID);
    return StringUtils.isNotBlank(fourSShopVersionIds) && fourSShopVersionIds.contains(shopVersionId.toString());
  }

  public static String getFourSShopVersions() {
    String fourSShopVersionIds = ServiceManager.getService(IConfigService.class)
      .getConfig("fourSShopVersions", ShopConstant.BC_SHOP_ID);
    if (StringUtils.isEmpty(fourSShopVersionIds)) {
      fourSShopVersionIds = "100010010000000";
    }
    return fourSShopVersionIds;
  }

  //app gsm版安卓最新版本
  public static String getGsmAndroidAppVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.GSM_APP_VERSION_ANDROID, ShopConstant.BC_SHOP_ID);
  }

  //IOS app gsm版本最新版本
  public static String getGsmIOSAppVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.GSM_APP_VERSION_IOS, ShopConstant.BC_SHOP_ID);
  }

  //安卓 app gsm更新URL
  public static String getGsmAndroidAppUpgradeURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.GSM_APP_ANDROID_UPGRADE_URL, ShopConstant.BC_SHOP_ID);
  }

  //IOS app gsm更新URL
  public static String getGsmISOAppUpgradeURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.GSM_APP_ISO_UPGRADE_URL, ShopConstant.BC_SHOP_ID);
  }

  public static List<Long> getBlueBoothMatchShopVersion() {
    String commonShopVersionIds = ServiceManager.getService(IConfigService.class)
      .getConfig("BlueToothMatchShopVersion", ShopConstant.BC_SHOP_ID);
    return NumberUtil.parseLongValues(commonShopVersionIds);
  }

  public static Double getCouponDefaultAmount() {
    String couponAmount = ServiceManager.getService(IConfigService.class)
      .getConfig("COUPON_DEFAULT_AMOUNT", ShopConstant.BC_SHOP_ID);
    return NumberUtil.doubleValue(couponAmount,800D);
  }

  public static String getLocalIpWithConfigApiTomcats() {
    String configIp = ServiceManager.getService(IConfigService.class).getConfigApiTomcatIps();
    List<String> localIps = getLocalhostIps();
    if (CollectionUtils.isEmpty(localIps)) {
      return null;
    }
    if (StringUtils.isBlank(configIp)) {
      return localIps.get(0);
    }
    for (String localIp : localIps) {
      if (configIp.equals(localIp)) {
        return localIp;
      }
    }
    return localIps.get(0);
  }

  public static String getGsmApnsCertPath() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.GSM_APNS_CERT_PATH, ShopConstant.BC_SHOP_ID);
//    return "E:\\技术专题\\APNS\\GSM-IOS\\gsm_development_push_java.p12";

  }

  public static String getGsmApnsCertPwd() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.GSM_APNS_CERT_PWD, ShopConstant.BC_SHOP_ID);
//    return  "jamestonggou";

  }

  public static String getGSMUMAppkey() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.GSM_UM_APP_KEY, ShopConstant.BC_SHOP_ID);
//    return "533b7b6a56240b29f80bc58f";
  }

  public static String getGSMUMAppMasterSeret() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.GSM_UM_APP_MASTER_SECRET, ShopConstant.BC_SHOP_ID);
//    return "2yaa3scwibfvfaikfe7ypwrzlspkpg2f";
  }

  public static int getTotalNumPerExcel() {
    return NumberUtil.intValue(ServiceManager.getService(IConfigService.class)
      .getConfig("TotalNumPerExcel", ShopConstant.BC_SHOP_ID), 1000);
  }

  public static String getYFApnsCertPath() {
//    return ServiceManager.getService(IConfigService.class)
//        .getConfig(ConfigConstant.GSM_APNS_CERT_PATH, ShopConstant.BC_SHOP_ID);
    return null;//todo 一发app ios 版本暂未申请下来。

  }

  public static String getYFApnsCertPwd() {
//    return ServiceManager.getService(IConfigService.class)
//        .getConfig(ConfigConstant.GSM_APNS_CERT_PWD, ShopConstant.BC_SHOP_ID);
    return "jamestonggou";//todo 一发app ios 版本暂未申请下来。

  }

  public static String getYFUMAppkey() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.YF_UM_APP_KEY, ShopConstant.BC_SHOP_ID);
//    return "53758ca756240bab770101d3";
  }

  public static String getYFUMAppMasterSeret() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.YF_UM_APP_MASTER_SECRET, ShopConstant.BC_SHOP_ID);
//    return "8a511db6220b2a726994676a3f52a0fb";
  }

  //app 安卓最新版本
  public static String getBcgogoAndroidAppVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_APP_VERSION_ANDROID, ShopConstant.BC_SHOP_ID);
  }

  //winCE 安卓最新版本
  public static String getBcgogoWinCEAppVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_APP_VERSION_WINCE, ShopConstant.BC_SHOP_ID);
  }

  //OBD 最新版本
  public static String getBcgogoOBDVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_OBD_VERSION, ShopConstant.BC_SHOP_ID);
  }

  //IOS app最新版本
  public static String getBcgogoIOSAppVersion() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_APP_VERSION_IOS, ShopConstant.BC_SHOP_ID);
  }

  //WINCE app 更新URL
  public static String getBcgogoWinCEUpgradeURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_APP_WINCE_UPGRADE_URL, ShopConstant.BC_SHOP_ID);
  }

  //OBD 更新URL
  public static String getBcgogoOBDUpgradeURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_OBD_UPGRADE_URL, ShopConstant.BC_SHOP_ID);
  }

  //安卓 app 更新URL
  public static String getBcgogoAndroidAppUpgradeURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_APP_ANDROID_UPGRADE_URL, ShopConstant.BC_SHOP_ID);
  }

  //IOS app 更新URL
  public static String getBcgogoISOAppUpgradeURL() {
    return ServiceManager.getService(IConfigService.class)
      .getConfig(ConfigConstant.BCGOGO_APP_ISO_UPGRADE_URL, ShopConstant.BC_SHOP_ID);
  }

  //webSocket url
  public static String getWSUrl() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    int mq_port = NumberUtil.intValue(configService.getConfig("MQ_LISTENER_PORT", ShopConstant.BC_SHOP_ID));
    String mq_ip = ServiceManager.getService(IConfigService.class).getConfig("MQ_IP_INTERNET", ShopConstant.BC_SHOP_ID);
    mq_ip = StringUtil.isEmpty(mq_ip) ? "192.168.1.100" : mq_ip;
    return "ws://" + mq_ip + ":" + mq_port;
  }

  public static Set<Long> getAgentDepartmentIdStr() {
    String agentDepartmentIdsStr = ServiceManager.getService(IConfigService.class)
      .getConfig("agentDepartmentIds", ShopConstant.BC_SHOP_ID);
    Set<Long> agentDepartmentIdSet = new HashSet<Long>();
    if (StringUtils.isNotEmpty(agentDepartmentIdsStr)) {
      String[] agentDepartmentIdArr = agentDepartmentIdsStr.split(",");
      for (String agentDepartmentIdStr : agentDepartmentIdArr) {
        if (StringUtils.isNotBlank(agentDepartmentIdStr)
          && StringUtils.isNumeric(agentDepartmentIdStr)) {
          agentDepartmentIdSet.add(NumberUtil.longValue(agentDepartmentIdStr));
        }
      }
    }
    return agentDepartmentIdSet;
  }

  /**
   * 读工程项目配置文件
   *
   * @param name
   * @return
   * @throws java.io.IOException
   */
  public static String read(String name) throws IOException {
    return readPropertyFile(name, "/prop.properties");
  }

  public static String readPropertyFile(String name, String file) throws IOException {
    if (StringUtil.isEmpty(name)) return null;
    InputStream is = null;
    try {
      is = Object.class.getResourceAsStream(file);
      Properties p = new Properties();
      p.load(is);
      return p.getProperty(name);
    } finally {
      if (is != null)
        is.close();
    }

  }

  /**
   * 读取jar包以外的工程配置
   *
   * @param name
   * @return
   * @throws IOException
   */
  public static String readOutPropertyFile(String name) throws IOException {
    if (StringUtil.isEmpty(name)) return null;
    InputStream is = null;
    try {
      is = new FileInputStream("camera.properties");
      Properties p = new Properties();
      p.load(is);
      return p.getProperty(name);
    } finally {
      if (is != null)
        is.close();
    }
  }

}
