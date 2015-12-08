package com.bcgogo.service.broker;

import com.bcgogo.pojo.util.ConfigUtil;
import com.bcgogo.pojo.util.StringUtil;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

import java.net.URI;

/**
 * 打印broker
 * Author: ndong
 * Date: 2015-3-2
 * Time: 13:43
 */
public class PrintBroker implements IBcgogoBroker {

  /**
   * 如果启动多个Broker时，必须为Broker设置一个名称
   * @throws Exception
   */
  public void start() throws Exception {
//    BrokerService broker = BrokerFactory.createBroker(new URI("file:activemq.xml"));
    BrokerService broker = BrokerFactory.createBroker(new URI("xbean:activemq.xml"));
    broker.setBrokerName("print-broker");
    String url= ConfigUtil.read("MQ.BROKER.URL");
    if(StringUtil.isEmpty(url)) throw new Exception("请在配置文件中，添加服务地址。");
    broker.addConnector(url);
    broker.start();
  }
}
