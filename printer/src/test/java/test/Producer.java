package test;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-2-27
 * Time: 17:54
 */

import com.bcgogo.pojo.Constants;
import com.bcgogo.pojo.Result;
import com.bcgogo.util.ConfigUtil;
import com.bcgogo.util.StringUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-2-15
 * Time: 10:26
 */
public class Producer {
  private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

  /**
   * 初始化
   *
   * @throws Exception
   */
  public static synchronized Session createSession() throws JMSException, IOException {
    String userName = "user";
    String password = "pass1";
    String url = ConfigUtil.read("MQ.BROKER.URL");
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
      userName, password, url);
    PooledConnectionFactory poolFactory = new PooledConnectionFactory(factory);
    poolFactory.setMaxConnections(100);
    poolFactory.setMaximumActiveSessionPerConnection(50);
    //后台对象清理时，休眠时间超过了3000毫秒的对象为过期
    poolFactory.setTimeBetweenExpirationCheckMillis(3000);
    PooledConnection pooledConnection = (PooledConnection) poolFactory.createConnection();
    //false 参数表示 为非事务型消息，后面的参数表示消息的确认类型（见4.消息发出去后的确认模式）
    return pooledConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  /**
   * @param msg
   * @return
   * @throws Exception
   */
  public Result sendTextMsg(String msg) throws Exception {
    LOG.info("producer send msg: {} ", msg);
    if (StringUtil.isEmpty(msg)) {
      return new Result(false, "发送消息不能为空。");
    }
    //false 参数表示 为非事务型消息，后面的参数表示消息的确认类型（见4.消息发出去后的确认模式）
    Session session = createSession();
    TextMessage textMessage = session.createTextMessage(msg);
    String subject = Constants.PREFIX_SUBJECT_PRINT + ConfigUtil.readSerialNo();
    Destination destination = session.createQueue(subject);
    MessageProducer producer = session.createProducer(destination);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    producer.send(textMessage);
    return new Result();
  }

//  /**
//   * @param msg
//   * @return
//   * @throws Exception
//   */
//  public Result sendObjectMsg(ClientMessage msg) throws Exception {
//    LOG.info("producer send msg: {} ", msg);
//    if (msg == null) {
//      return new Result(false, "发送消息不能为空。");
//    }
//    //false 参数表示 为非事务型消息，后面的参数表示消息的确认类型（见4.消息发出去后的确认模式）
//    Session session = createSession();
//    ObjectMessage message = session.createObjectMessage(msg);
//    String subject = Constants.PREFIX_SUBJECT_PRINT + ConfigUtil.readSerialNo();
//    Destination destination = session.createQueue(subject);
//    MessageProducer producer = session.createProducer(destination);
//    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//    producer.send(message);
//    return new Result();
//  }

  public static void main(String[] args) {
    try {
      String msg = "print#/web/print.do?method=printWashBeautyOrder&shopId=10000010001040004&orderId=10000010028032709";
      Producer producer = new Producer();
      producer.sendTextMsg(msg);
      LOG.info("send success!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
