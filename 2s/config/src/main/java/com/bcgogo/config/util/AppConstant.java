package com.bcgogo.config.util;

import com.bcgogo.common.Assert;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;

/**
 * User: ZhangJuntao
 * Date: 13-8-30
 * Time: 下午2:34
 */
public class AppConstant {
  public static final Long MEM_CACHE_DATE = 60 * 60 * 24 * 30 * 1000L;//30天

  public static final String VIDEO_FORMAT = ".mp4";
  public static final String IMAGE_FORMAT = ".jpg";

  /**
   * ***********************************  apix ************************************************
   */

  public static String DOMAIN_APIX = "";

  static {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    DOMAIN_APIX = configService.getConfig("DOMAIN_APIX", ShopConstant.BC_SHOP_ID);
    Assert.notEmpty(DOMAIN_APIX);
  }

  //同步登录信息到apix
  public static final String URL_APIX_PLAT_LOGIN = DOMAIN_APIX + "/apix/plat/login";
  //保存车况信息
  public static final String URL_APIX_GSM_VEHICLE_SAVE_DATA = DOMAIN_APIX + "/apix/gsm/data/save";

  public static final String URL_APIS_GET_ILLEGAL_CITY = DOMAIN_APIX + "/apix/gsm/vehicle/getIllegalCityByAppUserNo/{appUserNo}";
}
