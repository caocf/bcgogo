package com.bcgogo.activemq;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.thread.ThreadPool;
import com.bcgogo.utils.ShopConstant;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-5
 * Time: 13:36
 */
public class MQProductHelper {
  private static final Logger LOG = LoggerFactory.getLogger(MQProductHelper.class);

  private static PooledConnectionFactory poolFactory;


  /**
   * 产生队列消息
   * @param subject
   * @param msg
   */
  public static void produce(String subject, String msg) {
    LOG.info("produce queue msg:subject={},msg={}", subject, msg);
    Executor executor = ThreadPool.getInstance();
    executor.execute(new MQProductListener(subject, msg,"QUEUE"));
    LOG.info("MQProductHelper create thread finished");
  }

  /**
   * 产生订阅消息
   * @param subject
   * @param msg
   */
  public static void produceTopic(String subject, String msg) {
      LOG.info("produce queue msg:subject={},msg={}", subject, msg);
      Executor executor = ThreadPool.getInstance();
      executor.execute(new MQProductListener(subject, msg,"TOPIC"));
      LOG.info("MQProductHelper create thread finished");
    }

  public static synchronized PooledConnectionFactory getPooledConnectionFactory() {
    LOG.info("getPooledConnectionFactory");
    if (poolFactory != null) return poolFactory;
    LOG.info("getPooledConnectionFactory create new");
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String userName = configService.getConfig("MQ_USER_NAME", ShopConstant.BC_SHOP_ID);
    String password = configService.getConfig("MQ_USER_PASS", ShopConstant.BC_SHOP_ID);
    String url = configService.getConfig("MQ_BROKER_URL", ShopConstant.BC_SHOP_ID);
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
      userName, password, url);
    poolFactory = new PooledConnectionFactory(factory);
    // 池中借出的对象的最大数目
    poolFactory.setMaxConnections(100);
    poolFactory.setMaximumActiveSessionPerConnection(50);
    //后台对象清理时，休眠时间超过了3000毫秒的对象为过期
    poolFactory.setTimeBetweenExpirationCheckMillis(3000);
    LOG.info("getPooledConnectionFactory create success");
    return poolFactory;
  }


}
