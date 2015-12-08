package com.bcgogo.listener;

import com.bcgogo.pojo.Constants;
import com.bcgogo.pojo.Result;
import com.bcgogo.util.ConfigUtil;
import com.bcgogo.util.PrintHelper;
import com.bcgogo.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-2-11
 * Time: 09:54
 */
public class PrintListener extends AbstractListener {

  private static final Logger logger = LoggerFactory.getLogger(PrintListener.class);

  public PrintListener() {
    super("QUEUE");
  }


  /**
   * 消息处理函数
   *
   * @param message
   */
  public void onMessage(Message message) {
    try {
      if (message instanceof TextMessage) {
        TextMessage txtMsg = (TextMessage) message;
        String msg = txtMsg.getText();
        logger.info("received msg{}" , msg);
        msg=null;
        if (StringUtil.isEmpty(msg)) {
          logger.error("print msg is empty!");
          return;
        }
        String command = msg.split("#")[0];
        String val = msg.split("#")[1];
        if (StringUtil.isEmpty(msg)) {
          logger.error("print msg is empty!");
          return;
        }
        if ("TEST".equals(ConfigUtil.read("ENV.MODE"))) {
          return;
        }
        String url = Constants.URL_SERVER_DOMAIN + val;
        Result result = PrintHelper.printHtml(url);
        logger.info("print finished,result msg : {}", result.getMsg());
      } else {
        logger.info("consumer received: " + message);
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

}
