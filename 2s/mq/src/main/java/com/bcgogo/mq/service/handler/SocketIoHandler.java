package com.bcgogo.mq.service.handler;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.constant.MQConstant;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.mq.SocketSessionManager;
import com.bcgogo.mq.enums.MProtocolType;
import com.bcgogo.mq.message.MQAckMessageDTO;
import com.bcgogo.mq.message.MQLoginMessageDTO;
import com.bcgogo.mq.message.MQMessageDTO;
import com.bcgogo.mq.message.MQTalkMessageDTO;
import com.bcgogo.mq.protocol.MMsgProtocol;
import com.bcgogo.mq.protocol.MProtocol;
import com.bcgogo.mq.service.MQHelper;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.ByteUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-12
 * Time: 下午2:16
 */
public class SocketIoHandler extends IoHandlerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(SocketIoHandler.class);


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
    LOG.info("client disconnection : " + session.getId() + " is Disconnection");
    SocketSessionManager.removeSession(session.getId());
    session.close(true);
  }


  private void handleMirror(IoSession session, byte[] data) throws Exception {
    MMsgProtocol msgProtocol = null;
    int len = ByteUtil.reverseByteToInt(ByteUtil.subBytes(data, 0, 4));
    byte[] bData = ByteUtil.subBytes(data, 5, len);
    byte type = data[4];
    if (type != MQConstant.MIRROR_MSG_LOGIN && SocketSessionManager.getSessionById(session.getId()) == null) {
      LOG.info("user info exception,sessionId={}", session.getId());
      SocketSessionManager.removeSession(session.getId());
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.LOGIN_USER_NO_EMPTY);
      msgProtocol = new MMsgProtocol(MQConstant.MIRROR_MSG_ERROR, ByteUtil.complementZero(JsonUtil.objectToJson(apiResponse).getBytes("UTF-8")));
      MProtocol protocol = new MProtocol(MProtocolType.MIRROR, msgProtocol.toProtocol());
      session.write(protocol);
      return;
    }
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    MQAckMessageDTO ackMessageDTO = null;
    switch (type) {
      case MQConstant.MIRROR_MSG_TYPE_HEART_BEAT:
        LOG.info("heart_beat");
        break;
      case MQConstant.MIRROR_MSG_LOGIN:  //客户端登录
        MQLoginMessageDTO loginMessageDTO = JsonUtil.fromJson(new String(bData, "UTF-8"), MQLoginMessageDTO.class);
        LOG.info("mq:MIRROR_MSG_LOGIN,imei={}", loginMessageDTO.getName());
        loginMessageDTO.setMqSessionId(StringUtil.valueOf(session.getId()));
        ApiResponse apiResponse = appUserService.messageCenterLogin(loginMessageDTO);
        if ("SUCCESS".equals(apiResponse.getStatus())) {
          LOG.info("mq:messageCenterLogin success,sessionId:{}",session.getId());
          session.setAttribute("ptl_type", MProtocolType.MIRROR);
          session.setAttribute("fromUserName", loginMessageDTO.getName());
          SocketSessionManager.addSession(session.getId(), session);
        } else {
           LOG.warn("mq:messageCenterLogin failed,err msg is:{}",apiResponse.getMessage());
          session.close(true);
        }
        msgProtocol = new MMsgProtocol(MQConstant.MIRROR_MSG_LOGIN_ACK, ByteUtil.complementZero(JsonUtil.objectToJson(apiResponse).getBytes("UTF-8")));
        MProtocol protocol = new MProtocol(MProtocolType.MIRROR, msgProtocol.toProtocol());
        session.write(protocol);
        break;
      case MQConstant.MIRROR_MSG_DATA: //从客户端发来的消息
        LOG.info("mq:MIRROR_MSG_DATA");
        MQMessageDTO messageDTO = JsonUtil.fromJson(new String(bData, "UTF-8"), MQMessageDTO.class);
        MQHelper.receiveMirrorMsg(messageDTO);
        break;
      case MQConstant.MIRROR_MSG_DATA_ACK:   //从客户端发来的确认消息
         LOG.info("mq:MIRROR_MSG_DATA_ACK");
        ackMessageDTO = JsonUtil.fromJson(new String(bData, "UTF-8"), MQAckMessageDTO.class);
        ServiceManager.getService(IPushMessageService.class).handleMsgDateAck(ackMessageDTO);
        break;
      default:
        break;
    }

  }


  /**
   * 当接收到客户端的信息
   *
   * @param session
   * @param message
   * @throws Exception
   */
  @Override
  public void messageReceived(IoSession session, Object message) {
    try {
      byte[] data = (byte[]) message;
      if (ArrayUtil.isEmpty(data)) return;
      LOG.info("server receiver data={}", new String(data, "UTF-8"));
      MProtocolType protocolType = (MProtocolType) session.getAttribute("ptl_type");
      if (MProtocolType.WEB_SOCKET.equals(protocolType)) {
        LOG.info("receiver web socket msg");
        String msg = new String(data, "UTF-8");
        MQTalkMessageDTO talkMessageDTO = null;
        try {
          talkMessageDTO = JsonUtil.fromJson(msg, MQTalkMessageDTO.class);
        } catch (Exception e) {  //todo 非法字符串
        }
        if (talkMessageDTO == null) return;
        MQHelper.receiveWebSocketMsg(session, talkMessageDTO);
      } else {
        LOG.info("receiver mirror msg");
        handleMirror(session, data);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }


}
