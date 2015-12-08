package com.bcgogo.pojox.constant;

import com.bcgogo.pojox.common.Assert;
import com.bcgogo.pojox.util.CommonUtil;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午5:28
 */
public class XConstant {
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(XConstant.class);

  /**
   * ***********************************  open  ************************************************
   */
  public static String DOMAIN_OPEN = "";

  static {

    try {
      Properties properties = new Properties();
      File file = ResourceUtils.getFile("classpath:cfg.properties");
      properties.load(new FileInputStream(file));
      DOMAIN_OPEN = properties.getProperty("DOMAIN_OPEN");
      if (CommonUtil.isDevMode()) {
        DOMAIN_OPEN = "http://192.168.1.248:8080";
      }
      Assert.notEmpty(DOMAIN_OPEN);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }

  //发送故障码
  public static final String URL_OPEN_SEND_FAULT_CODE = DOMAIN_OPEN + "/api/guest/vehicle/sendFaultCode";

  //对上传的错误时间进行纠错
  public static final Long ERROR_DELAY_UPLOAD_TIME = 7 * 24 * 60 * 60 * 1000L;

  public static final Long ERROR_EARLIER_UPLOAD_TIME = -1 * 60 * 60 * 1000L;

  //命令头--HQ
  public static final String CMD_HEADER_HQ = "2A4851";
  //命令头--定时上报
  public static final String CMD_HEADER_RQ = "24";


}
