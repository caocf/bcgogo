package com.bcgogo.listener;

/**
 * 监测ActiveＭＱ服务器的连接状态
 * Author: ndong
 * Date: 2015-3-3
 * Time: 15:23
 */

import java.io.IOException;

import com.bcgogo.service.IConnector;
import com.bcgogo.service.impl.Connector;
import org.apache.activemq.transport.TransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTransportListener implements TransportListener {

  protected final Logger logger = LoggerFactory.getLogger(ClientTransportListener.class);

  /**
   * 对消息传输命令进行监控
   */
  @Override
  public void onCommand(Object o) {
    logger.debug("onCommand检测到服务端命令:{}", o);
  }

  /**
   * 与服务器连接发生错误
   *
   * @param error
   */
  @Override
  public void onException(IOException error) {
    logger.error("onException,与服务器连接发生错误......");
  }

  /**
   * 消息服务器连接发生中断
   */
  @Override
  public void transportInterupted() {
    logger.error("transportInterupted,与服务器连接发生中断......");
    IConnector connector = new Connector();
    connector.reConnect();
  }

  /**
   * 恢复与服务器的连接
   */
  @Override
  public void transportResumed() {
    logger.info("transportResumed,恢复与服务器连接....");
  }

}
