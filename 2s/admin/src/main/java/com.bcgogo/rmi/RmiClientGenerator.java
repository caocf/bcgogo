package com.bcgogo.rmi;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.rmi.service.IRmiSolrService;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-8-13
 * Time: 上午10:24
 */

public class RmiClientGenerator extends RmiProxyFactoryBean {
  private static final Logger LOG = LoggerFactory.getLogger(RmiClientGenerator.class);
  private static final String RMI_SOLR_SERVICE_PORT = "19001";
  private static final String RMI_SOLR_SERVICE_NAME = "rmiSolrService";

  private static final int SOCKET_TIMEOUT = 5000;

  public List<IRmiSolrService> getRmiSolrService() {
    return getClients(IRmiSolrService.class, RMI_SOLR_SERVICE_PORT, RMI_SOLR_SERVICE_NAME);
  }

  public <T> List<T> getClients(Class<T> cls, String rmiSolrServicePort, String serviceName){
    String[] ips = ServiceManager.getService(IConfigService.class).getConfigTomcatIps();
    List<T> result = new ArrayList<T>();
    if (ips == null) {
      return result;
    }
    for (String ip : ips) {
      T client = getClientService(cls, ip, rmiSolrServicePort, serviceName);
      if (client != null) {
        result.add(client);
      }
    }
    return result;
  }

  public <T> T getClientService(Class<T> cls, String ipAddr, String rmiSolrServicePort, String serviceName){
    T clientService = null;
    RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
    rmiProxyFactoryBean.setServiceUrl("rmi://" + ipAddr + ":" + rmiSolrServicePort + "/" + serviceName);
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
//    RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
//    rmiProxyFactoryBean.setServiceUrl("rmi://192.168.3.36:19876/orderSolrReindex");
//    rmiProxyFactoryBean.setServiceInterface(IOrderSolrWriterService.class);
//    try{
//      rmiProxyFactoryBean.afterPropertiesSet();
//      IOrderSolrWriterService serverSample = (IOrderSolrWriterService)rmiProxyFactoryBean.getObject();
//      ShopDTO shopDTO = new ShopDTO();
//      shopDTO.setId(1111111111111111L);
//      List<ShopDTO> list = new ArrayList<ShopDTO>();
//      list.add(shopDTO);
//      serverSample.batchReCreateOrderSolrIndex(list, null, 1L, 1000);
//    }catch(Exception e){
//      LOG.error(e.getMessage(), e);
//    }
    try{
      Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
      InetAddress ip = null;
      while (allNetInterfaces.hasMoreElements()) {
        NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
        Enumeration addresses = netInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          ip = (InetAddress) addresses.nextElement();
          if (ip != null && ip instanceof InetAddress) {
            System.out.println("本机的IP = " + ip.getHostAddress());
          }
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
