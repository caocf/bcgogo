package com.bcgogo.etl.common;

import com.bcgogo.common.Assert;
import com.bcgogo.common.CommonUtil;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.etl.dao.XConfigDao;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午5:28
 */
public class XConstant {

  /**
    * ***********************************  open  ************************************************
    */
    public static String DOMAIN_OPEN = "";

   static {
     XConfigDao configDao = ServiceManager.getService(XConfigDao.class);
     DOMAIN_OPEN = configDao.getConfig("DOMAIN_OPEN");
     if(CommonUtil.isDevMode()){
         DOMAIN_OPEN="http://127.0.0.1:8080";
     }
     Assert.notEmpty(DOMAIN_OPEN);
   }
   //保存车况
   public static final String URL_OPEN_SAVE_DRIVE_LOG = DOMAIN_OPEN + "/api/guest/vehicle/saveDriveLog";
  //getAppUserNoByImei
   public static final String URL_OPEN_GET_APP_USER_NO = DOMAIN_OPEN + "/api/guest/user/getAppUserNoByImei/{IMEI}";
  //发送故障码
   public static final String URL_OPEN_SEND_FAULT_CODE = DOMAIN_OPEN + "/api/guest/vehicle/sendFaultCode";

 //对上传的错误时间进行纠错
  public static final Long ERROR_DELAY_UPLOAD_TIME = 7 * 24 * 60 * 60 * 1000L;

  public static final Long ERROR_EARLIER_UPLOAD_TIME = -1 *60 * 60 * 1000L;

   //命令头--HQ
  public static final String CMD_HEADER_HQ ="2A4851";
   //命令头--定时上报
  public static final String CMD_HEADER_RQ ="24";





}
