//package com.bcgogo.mina;
//
//import com.bcgogo.api.ApiResponse;
//import com.bcgogo.api.response.HttpResponse;
//import com.bcgogo.constant.MQConstant;
//import com.bcgogo.mq.packet.LoginPacket;
//import com.bcgogo.mq.protocol.MMsgProtocol;
//import com.bcgogo.mq.service.SocketSessionManager;
//import com.bcgogo.mq.service.filter.CharsetCodecFactory;
//import com.bcgogo.utils.HttpUtils;
//import com.bcgogo.utils.JsonUtil;
//import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
//import org.apache.mina.core.future.ConnectFuture;
//import org.apache.mina.core.session.IoSession;
//import org.apache.mina.filter.codec.ProtocolCodecFilter;
//import org.apache.mina.transport.socket.nio.NioSocketConnector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//
///**
// * Created by IntelliJ IDEA.
// * Author: ndong
// * Date: 2015-6-5
// * Time: 13:19
// */
//public class TestMinaClientHelper {
//  public static final Logger LOG = LoggerFactory.getLogger(TestMinaClientHelper.class);
//
//  private static final String SERVER_IP = "192.168.1.100";
//  private static final int SERVER_PORT = 60101;
//  public static NioSocketConnector connector;
//  private static String imei = "356824200008005";
//  private static String sessionId;
//
//  static {
//    try {
//      createNioSocketConnector(imei);
//      sessionId = getSessionId(imei);
//    } catch (IOException e) {
//      LOG.error(e.getMessage(), e);
//    }
//  }
//
//  public static String getSessionId(String imei) throws IOException {
//    LOG.info("mirrorLogin...");
//    String loginUrl = MQConstant.URL_MIRROR_LOGIN.replace("{IMEI}", imei);
//    HttpResponse response = HttpUtils.sendGet(loginUrl);
//    LOG.info("登录mirrorLogin result:{}", response.getContent());
//    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
//    sessionId = response.getCookie();
//    return (sessionId.split(";")[0]).replace("JSESSIONID=", "");
//  }
//
//  public static void createNioSocketConnector(String id) {
//    //Create TCP/IP connection
//    connector = new NioSocketConnector();
//    //创建接受数据的过滤器
//    DefaultIoFilterChainBuilder chain = connector.getFilterChain();
//    //      chain.addLast("myChain", new ProtocolCodecFilter(new JMessageProtocolCodecFactory(Charset.forName("UTF-8"))));
//    chain.addLast("myChain", new ProtocolCodecFilter(new CharsetCodecFactory()));
//    //服务器的消息处理器：一个 SimpleMinaClientHandler 对象
//    connector.setHandler(new TestMinaClientHandler());
//    //set connect timeout
//    connector.setConnectTimeoutMillis(30 * 1000);
//    //连接到服务器：
//    ConnectFuture future = connector.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
//    // 等待连接创建完成
//    future.awaitUninterruptibly();
//    IoSession session = future.getSession();
//    SocketSessionManager.addSession(id, session);
//  }
//
//  public static void testLogin(String imei, String sessionId) {
//    LoginPacket packet = new LoginPacket();
//    packet.setName(imei);
//    packet.setPass(sessionId);
//    String msg = JsonUtil.objectToJson(packet);
//    MMsgProtocol protocol = new MMsgProtocol(MQConstant.MIRROR_MSG_LOGIN, msg.getBytes());
//    IoSession session = SocketSessionManager.getSessionById(imei);
//    session.write(protocol.toProtocol());
//    LOG.info("testLogin success");
//  }
//
//  public static void main(String[] args) throws IOException {
//    testLogin(imei, sessionId);
//    LOG.info("finish");
//  }
//
//}
