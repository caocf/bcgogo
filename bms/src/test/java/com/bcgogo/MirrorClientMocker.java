package com.bcgogo;

import com.bcgogo.mina.TestMinaClientHelper;
import com.bcgogo.pojo.constants.Constant;
import com.bcgogo.pojo.protocol.MMsgProtocol;
import com.bcgogo.service.SocketSessionManager;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-4
 * Time: 16:38
 */
public class MirrorClientMocker {
  public static final Logger LOG = LoggerFactory.getLogger(MirrorClientMocker.class);


  private static NioSocketConnector connector;

  private static final String SERVER_IP = "192.168.1.100";
  private static final int SERVER_PORT = 60100;

  private static String imei = "356824200008005";
  private static String sessionId;

  @BeforeClass
  public static void setUp() throws IOException {
    sessionId = TestMinaClientHelper.getSessionId(imei);
    TestMinaClientHelper.createNioSocketConnector(imei);
    connector = TestMinaClientHelper.connector;
  }

  @AfterClass
  public static void after() {
    IoSession session = SocketSessionManager.getSessionById(imei);
    session.close(true);
//    if (session.isConnected()) {
//      future.getSession().getCloseFuture().awaitUninterruptibly();
//    }
    connector.dispose(true);
  }

  @Test
  public void testHeartBeat() {
    IoSession session = SocketSessionManager.getSessionById(imei);
    MMsgProtocol protocol = new MMsgProtocol(Constant.MIRROR_MSG_TYPE_HEART_BEAT, null);
    session.write(protocol.toProtocol());
  }


  @Test
  public void testLogin() {
    TestMinaClientHelper.testLogin(imei, sessionId);
  }


}
