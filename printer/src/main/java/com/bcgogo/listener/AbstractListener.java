package com.bcgogo.listener;

import com.bcgogo.util.ConfigUtil;
import com.bcgogo.pojo.Constants;
import com.bcgogo.util.StringUtil;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-2-13
 * Time: 16:50
 */
public abstract class AbstractListener implements MessageListener {
  private static final Logger logger = LoggerFactory.getLogger(AbstractListener.class);

  private String producerType;

  public AbstractListener(String producerType) {
    this.producerType = producerType;
  }

  public void start() throws Exception {
    logger.info("startup listening...,producerType:{}", producerType);
    String userName = ConfigUtil.read("MQ.USER.NAME");
    String password = ConfigUtil.read("MQ.USER.PASS");
    String url = ConfigUtil.read("MQ.BROKER.URL");
    if (StringUtil.isEmpty(url)) {
      logger.error("can't read BROKER.URL in property file");
      throw new Exception("请在配置文件中，添加服务地址。");
    }
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
      userName, password, url);
    Connection connection = connectionFactory.createConnection();
    ((ActiveMQConnection) connection).addTransportListener(new ClientTransportListener());
    //false 参数表示 为非事务型消息，后面的参数表示消息的确认类型（见4.消息发出去后的确认模式）
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    String subject = Constants.PREFIX_SUBJECT_PRINT + ConfigUtil.readSerialNo();
    MessageConsumer consumer = null;
    if ("QUEUE".equals(producerType)) {
      Destination destination = session.createQueue(subject);
      consumer = session.createConsumer(destination);
    } else {
      Topic topic = session.createTopic(subject);
      consumer = session.createConsumer(topic);
    }
    consumer.setMessageListener(this);
    connection.start();
    logger.info("startup success");
  }

}
