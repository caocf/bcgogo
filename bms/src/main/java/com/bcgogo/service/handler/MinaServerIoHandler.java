package com.bcgogo.service.handler;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-3
 * Time: 14:19
 */

import com.bcgogo.pojo.constants.Constant;
import com.bcgogo.pojo.message.MQLoginMessageDTO;
import com.bcgogo.pojo.protocol.MMsgProtocol;
import com.bcgogo.pojo.response.ApiResponse;
import com.bcgogo.pojo.response.HttpResponse;
import com.bcgogo.pojo.util.ByteUtil;
import com.bcgogo.pojo.util.HttpUtils;
import com.bcgogo.pojo.util.JsonUtil;
import com.bcgogo.service.SocketSessionManager;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-3
 * Time: 09:54
 */
@Service
public class MinaServerIoHandler extends IoHandlerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(MinaServerIoHandler.class);


  /**
   * 当一个客户端建立连接时
   */
  @Override
  public void sessionOpened(IoSession session) throws Exception {
    LOG.info("mina:receive client connection : {}", session.getRemoteAddress());
  }

  /**
   * 当一个客户端关闭时
   */
  @Override
  public void sessionClosed(IoSession session) throws Exception {
    System.out.println("client disconnection : " + session.getId() + " is Disconnection");
  }

  /**
   * 当接收到客户端的信息
   *
   * @param session
   * @param message
   * @throws Exception
   */
  @Override
  public void messageReceived(IoSession session, Object message) throws Exception {
    MMsgProtocol protocol = null;
    byte[] data = (byte[]) message;
    int len = ByteUtil.reverseByteToInt(ByteUtil.subBytes(data, 0, 4));
    byte[] bData = ByteUtil.subBytes(data, 5, len);
    byte type = data[4];
    switch (type) {
      case Constant.MIRROR_MSG_TYPE_HEART_BEAT:
        System.out.println("heart_beat");
        break;
      case Constant.MIRROR_MSG_LOGIN:
        LOG.info("MIRROR_MSG_LOGIN");
        MQLoginMessageDTO loginMessageDTO = JsonUtil.fromJson(new String(bData), MQLoginMessageDTO.class);
        LOG.info("parse dataJson,imei={}", loginMessageDTO.getName());
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("appUserNo", loginMessageDTO.getName());
        parameters.put("pass", loginMessageDTO.getPass());
        HttpResponse response = HttpUtils.sendPUT(Constant.URL_MSG_MIRROR_LOGIN, parameters);
        ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
        protocol = new MMsgProtocol(Constant.MIRROR_MSG_LOGIN_ACK, ByteUtil.complementZero(apiResponse.getMessage().getBytes()));
        session.write(protocol.toProtocol());
        if ("SUCCESS".equals(apiResponse.getStatus())) {
          SocketSessionManager.addSession(loginMessageDTO.getName(), session);
        } else {
          session.close(true);
        }
        break;

      default:
        System.out.println("default");
    }

  }

}

