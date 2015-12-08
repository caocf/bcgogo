package com.bcgogo.driving.socket.handler;

import com.bcgogo.driving.socket.XSocketSessionManager;
import com.bcgogo.driving.socket.protocol.IProtocolParser;
import com.bcgogo.driving.socket.protocol.ProtocolFactory;
import com.bcgogo.pojox.util.BinaryUtil;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-12
 * Time: 下午2:16
 */
public class XSocketIoHandler extends IoHandlerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(XSocketIoHandler.class);


  /**
   * 当一个客户端建立连接时
   */
  @Override
  public void sessionOpened(IoSession session) throws Exception {
    LOG.info("mina:receive client connection : {}", session.getRemoteAddress());
  }

  /**
   * 当一个客户端关闭时
   */
  @Override
  public void sessionClosed(IoSession session) throws Exception {
    LOG.info("mina:client disconnection : " + session.getId() + " is Disconnection");
    XSocketSessionManager.removeSession((String) session.getAttribute("imei"));
    session.close(true);
  }


  /**
   * 当接收到客户端的信息
   *
   * @param session
   * @param message
   * @throws Exception
   */
  @Override
  public void messageReceived(IoSession session, Object message) {
    try {
      String hexString = BinaryUtil.byte2HexString((byte[]) message);
      IProtocolParser parser= ProtocolFactory.getHandler(hexString);
      if(parser==null){
        LOG.error("can't parser hexString:{}",hexString);
        return;
      }
      parser.doParse(session,hexString);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }


}
