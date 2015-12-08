package testdata.test;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-21
 * Time: 上午11:13
 * To change this template use File | Settings | File Templates.
 */
public class Executor {
  private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
  //服务器端口号
  private static final int PORT = 60000;

  /**
   * 是否启用ssl
   */
  private static final boolean USE_SSL = false;

  public static void main(String[] args) throws Exception {
    //创建一个非阻塞的server端socket，NIO
    SocketAcceptor acceptor = new NioSocketAcceptor();

    DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
    //日志filter
    LoggingFilter loggingFilter = new LoggingFilter();
    chain.addLast("loggingFilter", loggingFilter);
    //实例化MdcInjectionFilter过滤器,针对日志输出做MDC操作,可以参考log4j的MDC、NDC的文档
//    MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
//    chain.addLast("mdc", mdcInjectionFilter);
    //实例化SSL chain
    if (USE_SSL) {
      //todo 需要实例化sslFilter 在这里添加实例代码
    }

    //设定这个过滤器将以对象为单位读取数据
//    ProtocolCodecFilter objectFilter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
//    chain.addLast("objectFilter",objectFilter);

     /* 添加TextLine编解码过滤器,将一行以换行符为结束符号的byte[]转换成String对象
         * TextLineCodecFactory有TextLineEncoder编码实现,TextLineDecoder解码实现
        */
//    ProtocolCodecFilter textFilter = new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8")));
//    chain.addLast("textFilter", textFilter);

    //ExecutorFilter  时候接收到消息后 分发给handler 处理
    chain.addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));

    //设定服务器消息处理器
    acceptor.setHandler(new MinaWebSocketIoHandler());

    //绑定端口，启动服务器
    try{
      acceptor.bind(new InetSocketAddress(PORT));
      LOGGER.warn("Mina Server run done! on port:" + PORT);
    }catch (Exception e){
      LOGGER.error(e.getMessage(),e);
    }



  }
}
