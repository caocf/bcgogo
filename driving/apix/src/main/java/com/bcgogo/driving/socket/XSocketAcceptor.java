package com.bcgogo.driving.socket;

import com.bcgogo.driving.socket.codec.XSocketCodecFactory;
import com.bcgogo.driving.socket.handler.XSocketIoHandler;
import com.bcgogo.pojox.util.StringUtil;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-12
 * Time: 下午2:04
 */
public class XSocketAcceptor {

  private static final Logger LOG = LoggerFactory.getLogger(XSocketAcceptor.class);

  private int port; // 服务器端口
  private String ip;



  public XSocketAcceptor(String ip, int port) {
    this.port = port;
    this.ip=ip;
  }

  public void bind() throws Exception {
    //创建一个非阻塞的Server端Socket，用NIO
    SocketAcceptor acceptor = new NioSocketAcceptor();
    // 定义每次接收数据大小
    SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
    //缓冲区大小
    sessionConfig.setReceiveBufferSize(1024);
    sessionConfig.setMinReadBufferSize(1024);
    sessionConfig.setReadBufferSize(1024);
    //创建接受数据的过滤器
    DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
    chain.addFirst("socket-codec", new ProtocolCodecFilter(new XSocketCodecFactory()));
    //设定服务器端的消息处理器: 一个 SimpleMinaServerHandler 对象
    acceptor.setHandler(new XSocketIoHandler());
    //绑定端口，启动服务器
    ip= StringUtil.isEmpty(ip)?InetAddress.getLocalHost().getHostAddress():ip;
    //避免重启时报错 Address already in use
    acceptor.setReuseAddress(true);
    acceptor.bind(new InetSocketAddress(ip,port));
    LOG.info(" web-socket server is listing ip:{} port:{}",ip,port);
  }



}

