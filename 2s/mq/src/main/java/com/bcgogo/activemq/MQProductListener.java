package com.bcgogo.activemq;

import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.utils.StringUtil;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * activeMQ product
 * Author: ndong
 * Date: 2015-3-11
 * Time: 14:17
 */
public class MQProductListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(MQProductListener.class);

  private String subject;
  private String msg;
  private String producerType;

  public MQProductListener(String subject, String msg, String producerType) {
    this.subject = subject;
    this.msg = msg;
    this.producerType = producerType;
  }

  /**
   * 1.对象池管理connection和session,包括创建和关闭等
   * 2.PooledConnectionFactory缺省设置MaxIdle为1，
   * 官方解释Set max idle (not max active) since our connections always idle in the pool.
   * todo 参数读属性文件，待优化
   *
   * @return
   * @throws javax.jms.JMSException
   */
  private Session createSession() throws JMSException {
    PooledConnectionFactory poolFactory = MQProductHelper.getPooledConnectionFactory();
    PooledConnection pooledConnection = (PooledConnection) poolFactory.createConnection();
    //false 参数表示 为非事务型消息，后面的参数表示消息的确认类型（见4.消息发出去后的确认模式）
    return pooledConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  @Override
  public void run() {
    LOG.info("producer send msg: {} ", msg);
    if (StringUtil.isEmpty(msg)) {
      LOG.warn("发送消息不能为空。");
      return;
    }
    try {
      LOG.info("begin create session");
      Session session = createSession();
      MessageProducer producer = null;
      if ("QUEUE".equals(producerType)) {
        Destination destination = session.createQueue(subject);
        producer = session.createProducer(destination);
      } else {
        Topic topic = session.createTopic(subject);
        producer = session.createProducer(topic);
      }
      TextMessage textMessage = session.createTextMessage(msg);
      producer.send(textMessage);
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      LOG.info("create session success");
    } catch (JMSException e) {
      LOG.error(e.getMessage(), e);
    }

  }
}
