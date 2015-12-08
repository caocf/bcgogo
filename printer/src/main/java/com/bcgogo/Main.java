package com.bcgogo;

import com.bcgogo.listener.AbstractListener;
import com.bcgogo.listener.PrintListener;
import com.bcgogo.listener.UpdateListener;
import com.bcgogo.service.IClientService;
import com.bcgogo.service.impl.ClientService;
import com.bcgogo.util.ConfigUtil;
import com.bcgogo.util.PrintHelper;
import com.bcgogo.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-2-10
 * Time: 18:01
 */
public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    try {
      IClientService clientService = new ClientService();
      boolean result = clientService.validateSerialNo();
      if (!result) return;
      AbstractListener listener = new PrintListener();
      listener.start();
//      Thread updateThread=new Thread(new UpdateListener());
//      updateThread.start();
    } catch (Exception e) {
      try {
        logger.error("客户端出现异常,序列号:" + ConfigUtil.readSerialNo());
      } catch (Exception e1) {
        logger.error(e.getMessage(), e1);
      }
      logger.error(e.getMessage(), e);
    }
  }

}
