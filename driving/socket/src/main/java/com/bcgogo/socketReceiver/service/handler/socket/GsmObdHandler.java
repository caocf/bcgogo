package com.bcgogo.socketReceiver.service.handler.socket;

import com.bcgogo.pojox.util.StringUtil;
import com.bcgogo.socketReceiver.service.IGsmObdStatusService;
import com.bcgogo.socketReceiver.service.handler.GsmMessageReceivedParser;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: Jimuchen
 * Date: 14-2-21
 * Time: 上午11:15
 */
@Service
public class GsmObdHandler extends IoHandlerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(GsmObdHandler.class);
  @Autowired
  private GsmMessageReceivedParser gsmMessageReceivedParser;

  @Autowired
  private IGsmObdStatusService gsmObdStatusService;

  //    session.write("#751#500#5#22.5442N#113.91E#0000##");
//    session.write(IoBuffer.wrap("#751#500#5#22.5442N#113.91E#0000##".getBytes(Charset.forName("UTF-8"))));
  @Override
  public void messageReceived(IoSession session, Object message) throws Exception {
    String sessionIMei = null;
    String sessionId = null;
    if (session != null) {
      sessionIMei = (String) session.getAttribute("iMei");
      sessionId = StringUtil.valueOf(session.getId()) ;
    }
    LOGGER.warn("SessionCount:[{}]收到session id :【{}】中imei为：【{}】数据：【{}】,", new String[]{StringUtil.valueOf(session.getService().getManagedSessionCount()) ,sessionId,sessionIMei,(String)message});
    String iMei = gsmMessageReceivedParser.parser((String) message);
    if (session != null && StringUtils.isNotEmpty(iMei)) {
      SocketSessionManager.addSession(iMei, session);
      session.setAttribute("iMei", iMei);
    }
    if(!"test".equals(StringUtil.valueOf(session.getAttribute("test")))){
      IoSession testSession = SocketSessionManager.getSessionByImei("test");
      if(testSession != null){
        testSession.write(message);
      }

    }
    if("test".equals(StringUtil.valueOf(message))){
      session.setAttribute("test","test");
      session.setAttribute("iMei","test");
      SocketSessionManager.addSession("test", session);
    }



  }

  @Override
  public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    super.exceptionCaught(session, cause);
    LOGGER.error(cause.getMessage(), cause);
  }

  public void messageSent(IoSession session, Object message) throws Exception {
    System.out.println("hello world!");
  }

  @Override
  public void sessionClosed(IoSession session) throws Exception {
    if (session != null && session.getAttribute("iMei") != null) {
      SocketSessionManager.removeSession((String) session.getAttribute("iMei"));
    }
    super.sessionClosed(session);
  }
}
