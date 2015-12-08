//package com.bcgogo.service.socket;
//
//import com.bcgogo.service.handler.MirrorServerIoHandler;
//import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
//import org.apache.mina.core.filterchain.IoFilterChainBuilder;
//import org.apache.mina.core.service.IoHandler;
//import org.apache.mina.transport.socket.SocketAcceptor;
//import org.apache.mina.transport.socket.SocketSessionConfig;
//import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.annotation.PostConstruct;
//import java.net.InetSocketAddress;
//
///**
// * Created by IntelliJ IDEA.
// * Author: ndong
// * Date: 2015-6-4
// * Time: 13:07
// */
//public class MirrorNioSocketAcceptor {
//  private static final Logger LOG = LoggerFactory.getLogger(MirrorNioSocketAcceptor.class);
//  // 服务器端口
//  private static final int SERVER_PORT = 8899;
//
//  private String defaultLocalAddress;
//  private IoHandler handler;
//  private IoFilterChainBuilder filterChainBuilder;
//  private int processorCount;
//
//
//  public MirrorNioSocketAcceptor(int processorCount){
//     this.processorCount=processorCount;
//  }
//
//  public void unbind() throws Exception {
//
//  }
//
//  public void bind() throws Exception {
//    //创建一个非阻塞的Server端Socket，用NIO
//    SocketAcceptor acceptor = new NioSocketAcceptor();
//    // 定义每次接收数据大小
//    SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
//    sessionConfig.setReadBufferSize(2048);
//    //创建接受数据的过滤器
//            DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
//    //设定这个过滤器将一行一行（/r/n）的读取数据
//    //        chain.addLast("myChain", new ProtocolCodecFilter(new TextLineCodecFactory()));
//    //设定服务器端的消息处理器: 一个 SimpleMinaServerHandler 对象
//    acceptor.setHandler(new MirrorServerIoHandler());
//    //绑定端口，启动服务器
//    acceptor.bind(new InetSocketAddress(SERVER_PORT));
//    LOG.info("Mina server is listing port:{}", SERVER_PORT);
//  }
//
//  public void setDefaultLocalAddress(String defaultLocalAddress) {
//    this.defaultLocalAddress = defaultLocalAddress;
//  }
//
//  public void setHandler(IoHandler handler) {
//    this.handler = handler;
//  }
//
//  public void setFilterChainBuilder(IoFilterChainBuilder filterChainBuilder) {
//    this.filterChainBuilder = filterChainBuilder;
//  }
//}
