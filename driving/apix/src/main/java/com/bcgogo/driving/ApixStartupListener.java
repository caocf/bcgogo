package com.bcgogo.driving;

import com.bcgogo.driving.socket.XSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created with IntelliJ IDEA.
 * User: ndong
 * Date: 15-06-12
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
public class ApixStartupListener extends ContextLoaderListener implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(ApixStartupListener.class);

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    Integer socket_port = null;

    try {
      LOG.info("XSocketAcceptor start...");
      socket_port = 60113;   //todo 改成可配置的
      String local_mq_ip = "0.0.0.0";
      XSocketAcceptor socketAcceptor = new XSocketAcceptor(local_mq_ip, socket_port);
      socketAcceptor.bind();
      LOG.info("bind XSocketAcceptor finished,mq_port:{}", socket_port);
    } catch (Exception e) {
      LOG.error("bind WebSocketAcceptor failed,mq_port:{} ", socket_port);
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {

  }
}