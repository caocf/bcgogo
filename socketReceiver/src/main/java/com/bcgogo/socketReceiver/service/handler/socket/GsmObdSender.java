package com.bcgogo.socketReceiver.service.handler.socket;

import com.bcgogo.common.Result;
import com.bcgogo.socketReceiver.service.handler.socket.SocketSessionManager;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * Rmi服务端，用于接收并执行Bcgogo等其他JVM发送来的OBD发送指令
 * User: Jimuchen
 * Date: 14-3-7
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
@Component
public class GsmObdSender implements IGsmObdSender {
  private static final Logger LOG = LoggerFactory.getLogger(GsmObdSender.class);

  @Override
  public Result sendCommand(String imei, String command) {
    if (StringUtils.isBlank(command)) {
      LOG.warn("command is empty!");
      return new Result(false);
    }
    IoSession session = SocketSessionManager.getSessionByImei(imei);
    if (session == null || !session.isConnected()) {
      LOG.warn("session not available!");
      return new Result(false, "未检测到有效的OBD连接！");
    }
    try {
      WriteFuture writeFuture = session.write(IoBuffer.wrap(command.getBytes(Charset.forName("UTF-8"))));
      return new Result(true);
    } catch (Exception e) {
      LOG.error("write command exception.", e);
      return new Result(false, "发送命令时出错！");
    }
  }
}
