package com.bcgogo.config.service.UMPush;

import com.bcgogo.config.service.UMPush.UMPushConstant.UMAfterOpen;
import com.bcgogo.config.service.UMPush.UMPushConstant.UMPushDisplayType;
import com.bcgogo.config.service.UMPush.UMPushConstant.UMPushType;
import com.bcgogo.config.util.ConfigUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by XinyuQiu on 14-6-3.
 */
public class YFUMPushAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(YFUMPushAdapter.class);
  private static final AtomicBoolean _initialize = new AtomicBoolean(true);
  private static CountDownLatch _initialized = new CountDownLatch(1);
  private static UMPushService _umPushService = null;
  private static String appkey = null;
  private static String appMasterSecret = null;
  private static String timestamp = null;
  private static String validateToken = null;
  private static int MAX_POOL_SIZE = 10;

  private static void init() {
    if (_initialize.compareAndSet(true, false)) {
      try {
        appkey = ConfigUtils.getYFUMAppkey();
        appMasterSecret = ConfigUtils.getYFUMAppMasterSeret();
        if (StringUtils.isNotBlank(appkey) && StringUtils.isNotBlank(appMasterSecret)) {
          timestamp = String.valueOf(System.currentTimeMillis());
          validateToken = DigestUtils.md5Hex(appkey.toLowerCase() + appMasterSecret.toLowerCase() + timestamp);
          _umPushService = UMPush.newService()
              .setMaxPoolSize(MAX_POOL_SIZE)
              .setDelegate(new UMPushDelegateAdapter())
              .buildUnicastService();
        }else {
          LOG.error("UM 消息推送初始化失败 ：appkey:[{}],appMasterSecret:[{}]有空项",appkey,appMasterSecret);
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

  public static boolean sendPushMessage(String title,String text, String deviceToken) {
    try {
      init();
      if(StringUtils.isEmpty(title)){
        LOG.warn("title is empty!");
        return false;
      }
      if(StringUtils.isEmpty(text)){
        LOG.warn("text is empty!");
        return false;
      }
      if(StringUtils.isEmpty(deviceToken)){
        LOG.warn("deviceToken is empty!");
        return false;
      }
      if (_umPushService != null) {
        UnicastUMNotification notification = new UnicastUMNotification();
        notification.setAppkey(appkey);
        notification.setTimestamp(timestamp);
        notification.setDevice_tokens(deviceToken);
        notification.setProduction_mode(true);
        notification.setType(UMPushType.unicast);
        notification.setValidation_token(validateToken);
        UMPayLoad payLoad = new UMPayLoad();
        notification.setPayload(payLoad);
        UMPayLoadBody body = new UMPayLoadBody();
        payLoad.setBody(body);
        payLoad.setDisplay_type(UMPushDisplayType.notification);
        body.setTicker(title);
        body.setTitle(title);
        body.setText(text);
        body.setAfter_open(UMAfterOpen.go_app);
        _umPushService.send(notification);
        return true;
      }
      return false;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return false;
    }

  }


}
