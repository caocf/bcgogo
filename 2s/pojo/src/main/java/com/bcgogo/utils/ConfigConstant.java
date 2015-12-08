package com.bcgogo.utils;

/**
 * Created by IntelliJ IDEA.
 * User: qiuxinyu
 * Date: 12-5-16
 * Time: 下午4:44
 */
public class ConfigConstant {
  public static final Long CONFIG_SHOP_ID = -1L;   //shopId，为-1时，在shop_unit中存储初始化单位信息

  public static final String MEMCACHED_SERVERS = "MemcachedServers";

  public static final String SHOPPING_CART_MAX_CAPACITY = "SHOPPING_CART_MAX_CAPACITY";
  public static final String SHOPPING_CART_WARN_CAPACITY = "SHOPPING_CART_WARN_CAPACITY";

  public static final String PUSH_MESSAGE_POP_HIDE_INTERVAL = "PushMessagePopHideInterval"; // 求购资讯右下角弹出框持续时间 单位ms
  public static final String PUSH_MESSAGE_POP_REQUEST_INTERVAL = "PushMessagePopRequestInterval"; //求购资讯右下角弹出框间隔时间 单位ms
  public static final String PUSH_MESSAGE_LIFE_CYCLE = "PushMessageLifeCycle";//推送消息的有效时间  单位ms
  public static final String PRE_BUY_HARD_MATCH_ACCESSORY_PUSH_MESSAGE_COUNT = "PreBuyHardMatchAccessoryPushMessageCount";//推送消息的有效时间  单位ms
  public static final String PRE_BUY_SOFT_MATCH_ACCESSORY_PUSH_MESSAGE_COUNT = "PreBuySoftMatchAccessoryPushMessageCount";//推送消息的有效时间  单位ms


  public static final String PUSH_MESSAGE_SWITCH = "PushMessageSwitch";//消息开关

  public static final String ACCESSORY_SEED_COUNT = "AccessorySeedCount";//配件匹配种子数量
  public static final String ACCESSORY_RECOMMEND_COUNT = "AccessoryRecommendCount";//精品配件推荐数
  public static final String ACCESSORY_RECOMMEND_BY_PRE_BUY_ORDER_COUNT = "AccessoryRecommendByPreBuyOrderCount";//根据求购推荐配件推荐数
  public static final String PRE_BUY_RECOMMEND_COUNT = "PreBuyRecommendCount";//求购资讯推荐数

  public static final String SHOP_RECOMMEND_COUNT = "ShopRecommendCount";//shop推荐数

  public static final String PRODUCT_MATCH_SCALE = "ProductMatchScale";//商品匹配度系数（a*商品匹配度+b*价格比例+c*区域得分）
  public static final String PRODUCT_PRICE_SCALE = "ProductPriceScale";//商品价格系数（a*商品匹配度+b*价格比例+c*区域得分）
  public static final String PRODUCT_AREA_SCALE = "ProductAreaScale";//商品区域系数（a*商品匹配度+b*价格比例+c*区域得分）


  public static final String PRE_BUY_MATCH_SCALE = "PreBuyMatchScale";//求购商品区域系数（a*商品匹配度+b*区域得分）
  public static final String PRE_BUY_AREA_SCALE = "PreBuyAreaScale";//求购区域系数（a*商品匹配度+b*区域得分）


  public static final String CONFIG_RMI_SERVER_HOST = "java.rmi.server.hostname"; //RMI服务用到的系统环境变量设置
  //upYun
  public static final String UP_YUN_BUCKET = "upYun_Bucket";//空间名称
  public static final String UP_YUN_USERNAME = "upYun_Username";//
  public static final String UP_YUN_PASSWORD = "upYun_Password";//
  public static final String UP_YUN_SECRET_KEY = "upYun_SecretKey";//
  public static final String UP_YUN_DOMAIN_URL = "upYun_Domain_Url";//
  public static final String UP_YUN_SEPARATOR = "upYun_Separator";//
  public static final String UP_YUN_NOT_FIND_IMAGE_PATH = "upYun_NotFindImagePath";//
  public static final String UP_YUN_IMAGE_PATH = "upYun_ImagePath";//
  public static final String UP_YUN_ALLOW_IMAGE_TYPE = "upYun_AllowImageType";//
  public static final String UP_YUN_EXPIRATION = "upYun_Expiration";//
  public static final String UP_YUN_UPLOAD_DOMAIN_URL = "upYun_Upload_Domain_Url";//


  public static final String RECENTLY_VIEWED_PRODUCT_NUM = "RecentlyViewedProductNum";//
  public static final String RECENTLY_USED_PRODUCT_CATEGORY_NUM = "RecentlyUsedProductCategoryNum";//
  public static final String RECENTLY_USED_SMS_CONTACT_NUM = "RecentlyUsedSmsContactNum";//


  public static final String YUN_PRINT_CLIENT_VERSION = "yunPrintClientVersion";

  public static final String APP_VERSION_ANDROID = "AppVersionAndroid";
  public static final String APP_ANDROID_UPGRADE_URL = "AppAndroidAppUpgradeURL";

  public static final String APP_VERSION_IOS = "AppVersionIOS";
  public static final String APP_ISO_UPGRADE_URL = "AppISOAppUpgradeURL";
  public static final String API_PERMISSION_SWITCH = "ApiPermissionSwitch";
  public static final String APP_OBD_READ_INTERVAL = "AppObdReadInterval";
  public static final String APP_SERVER_READ_INTERVAL = "AppServerReadInterval";
  public static final String APP_MILEAGE_INFORM_INTERVAL = "AppMileageInformInterval";
  public static final String APP_USER_SEND_SMS_LIMITS = "AppUserSendSMSLimits";
  public static final String APP_REMAIN_OIL_MASS_WARN = "AppRemainOilMassWarn";
  public static final String APP_VEHICLE_ERROR_CODE_WARN_INTERVALS = "AppVehicleErrorCodeWarnIntervals";

  public static final String OVERDUE_APPOINT_REMIND_INTERVALS = "OverdueAppointRemindIntervals";
  public static final String APP_VEHICLE_MAINTAIN_MILEAGE_INTERVALS = "AppVehicleMaintainMileageIntervals";
  public static final String APP_VEHICLE_MAINTAIN_TIME_INTERVALS = "AppVehicleMaintainTimeIntervals";
  public static final String APP_VEHICLE_INSURANCE_TIME_INTERVALS = "AppVehicleInsuranceTimeIntervals";
  public static final String APP_VEHICLE_EXAMINE_TIME_INTERVALS = "AppVehicleExamineTimeIntervals";
  public static final String APP_VEHICLE_NEXT_MAINTAIN_MILEAGE_PUSH_MESSAGE_REMIND_TIMES_LIMIT = "AppVehicleNextMaintainMileagePushMessageRemindTimesLimit";
  public static final String HTTP_SERVER_URL = "HTTP_SERVER_URL";

  public static final String BCGOGO_ORDER_SMS_PHONE_NUMBER = "BcgogoOrderSmsPhoneNumber";

  public static final String CUSTOMER_SERVICE_PHONE = "CustomerServicePhone";


  public static final String PUSH_MESSAGE_KEEP_DAY = "PushMessageKeepDay";

  public static final String BCGOGO_PHONE = "BcgogoPhone";
  public static final String BCGOGO_QQ = "BcgogoQQ";
  public static final String BCGOGO_SOFT_ANNUAL_PRICE = "BcgogoSoftAnnualPrice";

  public static final String SMS_INDUSTRY_TAG = "SmsIndustryTag";

  public static final String JUHE_VIOLATE_REGULATION_KEY = "JUHE_VIOLATE_REGULATION_KEY";
  public static final String SEARCH_SHOP_LOCATION_DISTANCE = "SEARCH_SHOP_LOCATION_DISTANCE";
  public static final String SMS_PREFERENTIAL_POLICY_PATH = "smsPreferentialPolicyPath";

  public static final String GSM_APP_VERSION_ANDROID = "GsmAppVersionAndroid";
  public static final String GSM_APP_ANDROID_UPGRADE_URL = "GsmAppAndroidAppUpgradeURL";

  public static final String GSM_APP_VERSION_IOS = "GsmAppVersionIOS";
  public static final String GSM_APP_ISO_UPGRADE_URL = "GsmAppISOAppUpgradeURL";


  public static final String GSM_APNS_CERT_PATH = "GsmAPNSCertPath";
  public static final String GSM_APNS_CERT_PWD = "gsmAPNSCertPwd";

  public static final String GSM_UM_APP_KEY = "gsmUMAppKey";
  public static final String GSM_UM_APP_MASTER_SECRET = "gsmUMAppMasterSecret";

  public static final String BCGOGO_APP_VERSION_ANDROID = "BcgogoAppVersionAndroid";
  public static final String BCGOGO_APP_ANDROID_UPGRADE_URL = "BcgogoAppAndroidAppUpgradeURL";

  public static final String BCGOGO_APP_VERSION_WINCE = "BcgogoAppVersionWinCE";
  public static final String BCGOGO_OBD_VERSION = "BcgogoOBDVersion";
  public static final String BCGOGO_APP_WINCE_UPGRADE_URL = "BcgogoAppWinCEAppUpgradeURL";
    public static final String BCGOGO_OBD_UPGRADE_URL = "BcgogoOBDUpgradeURL";

  public static final String BCGOGO_APP_VERSION_IOS = "BcgogoAppVersionIOS";
  public static final String BCGOGO_APP_ISO_UPGRADE_URL = "BcgogoAppISOAppUpgradeURL";

  public static final String YF_APNS_CERT_PATH = "YFAPNSCertPath";
  public static final String YF_APNS_CERT_PWD = "YFAPNSCertPwd";

  public static final String YF_UM_APP_KEY = "YFUMAppKey";
  public static final String YF_UM_APP_MASTER_SECRET = "YFUMAppMasterSecret";

}
