package com.bcgogo.config.service.Apns;

import com.bcgogo.config.util.ConfigUtils;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by XinyuQiu on 14-4-10.
 */
public class GsmAPNSAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(GsmAPNSAdapter.class);
  private static final AtomicBoolean _initialize = new AtomicBoolean(true);
  private static CountDownLatch _initialized = new CountDownLatch(1);
  private static ApnsService _apnsService = null;
  private static final int MAX_CONN = 5;

  private static void init() {
    if (_initialize.compareAndSet(true, false)) {
      try {
        String path = ConfigUtils.getGsmApnsCertPath();
        String pwd = ConfigUtils.getGsmApnsCertPwd();
        boolean isProductionDestination = path != null && path.contains("Production");
        if (StringUtils.isNotBlank(path) && StringUtils.isNotBlank(pwd)) {
          _apnsService =
              APNS.newService()
                  .withCert(path, pwd)
                  .withAppleDestination(isProductionDestination)
                  .asPool(MAX_CONN)
//                  .withDelegate(new GsmAPNSFeedBack()) if apns need feedback add this
                  .build();
        }
      } finally {
        _initialized.countDown();
      }
      LOG.info("initialized GsmAPNSAdapter");
    } else {
      try {
        _initialized.await();
      } catch (InterruptedException ignore) {
        LOG.error(ignore.getMessage(),ignore);
      }
    }
  }

  public static boolean sendPushMessage(String message, String deviceToken) {
    try {
      init();
      if(StringUtils.isEmpty(message)){
        LOG.warn("message is empty!");
        return false;
      }
      if(StringUtils.isEmpty(deviceToken)){
        LOG.warn("token is empty!");
        return false;
      }
      if (_apnsService != null) {
        String payload = APNS.newPayload()
            .alertBody(message)
            .badge(1)
            .sound("default")
            .build();
        _apnsService.push(deviceToken, payload);
        return true;
      }
      return false;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return false;
    }

  }



}