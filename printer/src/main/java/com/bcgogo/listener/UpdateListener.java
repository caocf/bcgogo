package com.bcgogo.listener;

import com.bcgogo.pojo.Constants;
import com.bcgogo.pojo.response.HttpResponse;
import com.bcgogo.service.IClientService;
import com.bcgogo.service.impl.ClientService;
import com.bcgogo.util.ConfigUtil;
import com.bcgogo.util.HttpUtils;
import com.bcgogo.util.NumberUtil;
import com.bcgogo.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-18
 * Time: 17:39
 */
public class UpdateListener implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(UpdateListener.class);
  //检测更新间隔 一小时
  private Long time_interval = 60 * 60 * 1000L;

  @Override
  public void run() {
    String path = null;
    try {
      String url = Constants.URL_GET_YUN_PRINT_CLIENT_VERSION;
      HttpResponse response = HttpUtils.sendPost(url);
      String lastVersion = response.getContent();
      String currentVersion = ConfigUtil.read("VERSION");
      if (!StringUtil.compareVersion(currentVersion, lastVersion)) {
        return;
      }
      IClientService clientService = new ClientService();
      String fileUrl = ConfigUtil.read("FILE.SERVICE.DOMAIN") + "printer-" + lastVersion + ".jar";
      path = ConfigUtil.read("SETUP.PATH") + "printer-" + lastVersion + ".jar";
      clientService.download(fileUrl, path);
//      File file = new File(path);
//      if (file.exists()) {
//
//      }
      Thread.sleep(time_interval);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

}
