package com.bcgogo.mq.service;

import com.bcgogo.mq.SocketSessionManager;
import com.bcgogo.mq.service.filter.SocketCodecFactory;
import com.bcgogo.mq.service.handler.MinaClientIoHandler;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-9
 * Time: 上午11:10
 */
public class MQPushHelper {
  public static final Logger LOG = LoggerFactory.getLogger(MQPushHelper.class);
  private static final String SERVER_IP = "192.168.1.100";
  private static final int SERVER_PORT = 60100;
  public static NioSocketConnector connector;

  public static void createNioSocketConnector(String id) {
    //Create TCP/IP connection
    connector = new NioSocketConnector();
    //创建接受数据的过滤器
    DefaultIoFilterChainBuilder chain = connector.getFilterChain();
    //      chain.addLast("myChain", new ProtocolCodecFilter(new JMessageProtocolCodecFactory(Charset.forName("UTF-8"))));
    chain.addLast("myChain", new ProtocolCodecFilter(new SocketCodecFactory()));
    //服务器的消息处理器：一个 SimpleMinaClientHandler 对象
    connector.setHandler(new MinaClientIoHandler());
    //set connect timeout
    connector.setConnectTimeoutMillis(30 * 1000);
    //连接到服务器：
    ConnectFuture future = connector.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
    // 等待连接创建完成
    future.awaitUninterruptibly();
    IoSession session = future.getSession();
    SocketSessionManager.addSession(id, session);
  }


}
