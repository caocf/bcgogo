package com.bcgogo.service.impl;

import com.bcgogo.listener.PrintListener;
import com.bcgogo.service.IConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-3
 * Time: 16:32
 */
public class Connector implements IConnector {
  private static final Logger logger = LoggerFactory.getLogger(Connector.class);

  /**
   * @throws InterruptedException
   */
  @Override
  public void reConnect() {
    logger.info("start reConnect...");
    try {
      PrintListener listener = new PrintListener();
      listener.start();
      logger.info("successfully reConnect");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }


}
