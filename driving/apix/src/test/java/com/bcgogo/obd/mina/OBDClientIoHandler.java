package com.bcgogo.obd.mina;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-3
 * Time: 16:33
 */

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OBDClientIoHandler extends IoHandlerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(OBDClientIoHandler.class);

  private static String imei = "356824200008005";

  /**
   * 当客户端接收到消息时
   */
  @Override
  public void messageReceived(IoSession session, Object message) throws Exception {
    byte[] msg = (byte[]) message;
    LOG.info("messageReceived:{}", new String(msg, "UTF-8"));
  }

  /**
   * 当一个客户端被关闭时
   */
  @Override
  public void sessionClosed(IoSession session) throws Exception {
    LOG.info("client disconnect");
  }

  /**
   * 当一个客户端连接进入时
   */
  @Override
  public void sessionOpened(IoSession session) throws Exception {
    LOG.info("create connection to server :" + session.getRemoteAddress());
  }

}
