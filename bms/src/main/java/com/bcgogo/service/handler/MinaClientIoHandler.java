package com.bcgogo.service.handler;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-3
 * Time: 16:33
 */

import com.bcgogo.pojo.constants.Constant;
import com.bcgogo.pojo.message.MQAckMessageDTO;
import com.bcgogo.pojo.message.MQAckMessageItemDTO;
import com.bcgogo.pojo.message.MQMessageDTO;
import com.bcgogo.pojo.message.MQMessageItemDTO;
import com.bcgogo.pojo.protocol.MMsgProtocol;
import com.bcgogo.pojo.util.ByteUtil;
import com.bcgogo.pojo.util.CollectionUtil;
import com.bcgogo.pojo.util.JsonUtil;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MinaClientIoHandler extends IoHandlerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(MinaClientIoHandler.class);

  private static String imei = "356824200008005";

  /**
   * 当客户端接受到消息时
   */
  @Override
  public void messageReceived(IoSession session, Object message) throws Exception {
    MMsgProtocol protocol = null;
    byte[] data = (byte[]) message;
    int len = ByteUtil.reverseByteToInt(ByteUtil.subBytes(data, 0, 4));
    byte[] bData = ByteUtil.subBytes(data, 5, len - 1);
    String msg = new String(bData, "UTF-8");
    LOG.info("client:receive data,type={}, bData={}", data[4], msg);
    byte type = data[4];
    switch (type) {
      case Constant.MIRROR_MSG_DATA:
        MQMessageDTO messageDTO = JsonUtil.jsonToObj(msg, MQMessageDTO.class);
        List<MQMessageItemDTO> messageItemDTOs = messageDTO.getItemDTOs();
        List<MQAckMessageItemDTO> ackMessageItemDTOs = new ArrayList<MQAckMessageItemDTO>();
        if (CollectionUtil.isNotEmpty(messageItemDTOs)) {
          for (MQMessageItemDTO itemDTO : messageItemDTOs) {
            MQAckMessageItemDTO ackMessageItemDTO = new MQAckMessageItemDTO();
            ackMessageItemDTO.setId(itemDTO.getMsgId());
            ackMessageItemDTOs.add(ackMessageItemDTO);
          }
        }
        MQAckMessageDTO ackMessageDTO = new MQAckMessageDTO();
        ackMessageDTO.setSendTime(System.currentTimeMillis());
        ackMessageDTO.setItemDTOs(ackMessageItemDTOs);
        protocol = new MMsgProtocol(Constant.MIRROR_MSG_DATA_ACK, JsonUtil.objectToJson(ackMessageDTO).getBytes("UTF-8"));
        session.write(protocol.toProtocol());
        break;
      case Constant.MIRROR_MSG_LOGIN_ACK:
        break;
      case Constant.WEB_SOCKET_TALK_FROM_WX:
        break;
      default:
        System.out.println("default");
    }
  }

  /**
   * 当一个客户端被关闭时
   */
  @Override
  public void sessionClosed(IoSession session) throws Exception {
    LOG.info("client disconnect");
  }

  /**
   * 当一个客户端连接进入时
   */
  @Override
  public void sessionOpened(IoSession session) throws Exception {
    LOG.info("create connection to server :" + session.getRemoteAddress());
  }

}
