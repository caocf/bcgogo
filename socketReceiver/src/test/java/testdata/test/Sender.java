package testdata.test;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.ConnectException;
import java.net.InetSocketAddress;

public class Sender {
  public SocketConnector socketConnector;

  /**
   * 缺省连接超时时间
   */
  public static final int DEFAULT_CONNECT_TIMEOUT = 5;

  public static final String HOST = "localhost";

  public static final int PORT = 60000;

  public Sender() {
    init();
  }

  public void init() {
    socketConnector = new NioSocketConnector();

    // 长连接
    socketConnector.getSessionConfig().setKeepAlive(true);

    socketConnector.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);

//    socketConnector.setReaderIdleTime(DEFAULT_CONNECT_TIMEOUT);
//    socketConnector.setWriterIdleTime(DEFAULT_CONNECT_TIMEOUT);
//    socketConnector.setBothIdleTime(DEFAULT_CONNECT_TIMEOUT);

    socketConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));

    ClientIoHandler ioHandler = new ClientIoHandler();
    socketConnector.setHandler(ioHandler);
  }

  public void sendMessage(final String msg) {
    InetSocketAddress addr = new InetSocketAddress(HOST, PORT);
    ConnectFuture cf = socketConnector.connect(addr);
    try {
      cf.awaitUninterruptibly();
      cf.getSession().write(msg);
      System.out.println("send message " + msg);
      cf.getSession().getCloseFuture().awaitUninterruptibly();
    } catch (RuntimeIoException e) {
      if (e.getCause() instanceof ConnectException) {
        try {
          if (cf.isConnected()) {
            cf.getSession().close();
          }
        } catch (RuntimeIoException e1) {
        }
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Sender client = new Sender();
    for (int i = 0; i < 1; i++) {
      long time = System.currentTimeMillis();
      client.sendMessage("#356823033341020#56205280774#0#0000#DTU#<Load: 2.0%,ECT: -25逤,SHRTFT1: 76.6% ,LONGFT1: 76.6%,MAP: 2270.0kPa,RPM: 7510,VSS: 5km/h,Spark Adv: 50.0,IAT: 188逤,MAF: 724.41g/s,TPS: 2.4%,MIL_dist: 58809km,Fuel Lvl: 90.2%,BARO: 230kPa,VPWR: 59.07V,IFE: 52.880ml/s,RunTime:56s,AD_Mil:0.0km,AD_FEH:5066.515l/100km,SPWR:12.87V,RDTC:\n\t009,&P3002&P3102&P3401&C163B&B24A1&P020C&P1400&U3C00&P0000>## " + time);
    }
    client.getSocketConnector().dispose();
    System.exit(-1);
  }

  public SocketConnector getSocketConnector() {
    return socketConnector;
  }

  public void setSocketConnector(SocketConnector socketConnector) {
    this.socketConnector = socketConnector;
  }


}

class ClientIoHandler extends IoHandlerAdapter {

  private void releaseSession(IoSession session) throws Exception {
    System.out.println("releaseSession");
    if (session.isConnected()) {
      session.close(true);
    }
  }

  @Override
  public void sessionOpened(IoSession session) throws Exception {
    System.out.println("sessionOpened");
  }

  @Override
  public void sessionClosed(IoSession session) throws Exception {
    System.out.println("sessionClosed");
  }

  @Override
  public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    System.out.println("sessionIdle");
    try {
      releaseSession(session);
    } catch (RuntimeIoException e) {
    }
  }

  @Override
  public void messageReceived(IoSession session, Object message) throws Exception {
    System.out.println("Receive Server message " + message);

    super.messageReceived(session, message);

    releaseSession(session);
  }

  @Override
  public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    System.out.println("exceptionCaught");
    cause.printStackTrace();
    releaseSession(session);
  }

  @Override
  public void messageSent(IoSession session, Object message) throws Exception {
    System.out.println("messageSent");
    super.messageSent(session, message);
  }

}
