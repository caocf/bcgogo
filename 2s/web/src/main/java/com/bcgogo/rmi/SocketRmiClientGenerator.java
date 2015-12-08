package com.bcgogo.rmi;

import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-8-13
 * Time: 上午10:24
 */

public class SocketRmiClientGenerator extends RmiProxyFactoryBean {
  private static final Logger LOG = LoggerFactory.getLogger(SocketRmiClientGenerator.class);
  private static final String SOCKET_RMI_SERVER_PORT = "19010";
  private static final String SOCKET_RMI_SERVER_NAME = "gsmObdSender";

  private static final int SOCKET_TIMEOUT = 5000;

  public IGsmObdSender getRmiSolrService() {
    String ip = ServiceManager.getService(IConfigService.class).getConfigSocketReceiverIp();
    return getClient(IGsmObdSender.class, ip+":"+ SOCKET_RMI_SERVER_PORT, SOCKET_RMI_SERVER_NAME);
  }

  public <T> T getClient(Class<T> cls, String ipAndPort, String serviceName){
    return getClientService(cls, ipAndPort, serviceName);
  }

  public <T> T getClientService(Class<T> cls, String ipAndPort, String serviceName){
    T clientService = null;
    RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
    rmiProxyFactoryBean.setServiceUrl("rmi://" + ipAndPort + "/" + serviceName);
    rmiProxyFactoryBean.setServiceInterface(cls);
    rmiProxyFactoryBean.setLookupStubOnStartup(false);    //不在容器启动的时候创建与Server端的连接
    rmiProxyFactoryBean.setRefreshStubOnConnectFailure(true);     //这个属性是表示是否连接出错时自动重连
    RMICustomClientSocketFactory socketFactory = new RMICustomClientSocketFactory();
    socketFactory.setTimeout(SOCKET_TIMEOUT);
    rmiProxyFactoryBean.setRegistryClientSocketFactory(socketFactory);
    try {
      rmiProxyFactoryBean.afterPropertiesSet();
      clientService = (T) rmiProxyFactoryBean.getObject();
      if (clientService != null) {
        return clientService;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  public static void main(String[] args) {
    RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
    rmiProxyFactoryBean.setServiceUrl("rmi://61.147.80.131:19010/gsmObdSender");
    rmiProxyFactoryBean.setServiceInterface(IGsmObdSender.class);
    try{
      rmiProxyFactoryBean.afterPropertiesSet();
      IGsmObdSender serverSample = (IGsmObdSender)rmiProxyFactoryBean.getObject();
//      Result result = serverSample.sendCommand("111", "adm123456,18662210060");
//      Result result = serverSample.sendCommand("111", "#356824206007887#DTU#BT+DATA.SPWR\r\n##");
      Result result = serverSample.sendCommand("356824200008122", "vib0,3");
      System.out.println(result);
    }catch(Exception e){
      LOG.error(e.getMessage(), e);
    }

//    try{
//      Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
//      InetAddress ip = null;
//      while (allNetInterfaces.hasMoreElements()) {
//        NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//        Enumeration addresses = netInterface.getInetAddresses();
//        while (addresses.hasMoreElements()) {
//          ip = (InetAddress) addresses.nextElement();
//          if (ip != null && ip instanceof InetAddress) {
//            System.out.println("本机的IP = " + ip.getHostAddress());
//          }
//        }
//      }
//    }catch(Exception e){
//      e.printStackTrace();
//    }
  }
}
