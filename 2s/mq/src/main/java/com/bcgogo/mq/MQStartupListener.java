package com.bcgogo.mq;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.mq.service.socket.WebSocketAcceptor;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: ndong
 * Date: 15-06-12
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
public class MQStartupListener extends ContextLoaderListener implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(MQStartupListener.class);

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    Integer mq_port = null;
    try {
       LOG.info("MQStartupListener start...");
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      mq_port = NumberUtil.intValue(configService.getConfig("MQ_LISTENER_PORT", ShopConstant.BC_SHOP_ID));
//      mq_port = 60112;
      String local_mq_ip = "0.0.0.0";
      WebSocketAcceptor socketAcceptor = new WebSocketAcceptor(local_mq_ip, mq_port);
      socketAcceptor.bind();
      LOG.info("MQStartupListener finished,mq_port:{}",mq_port);
    } catch (Exception e) {
      LOG.error("bind WebSocketAcceptor failed,mq_port:{} ", mq_port);
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {

  }
}