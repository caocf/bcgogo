package com.bcgogo.mq.service.filter;

import com.bcgogo.mq.enums.MProtocolType;
import com.bcgogo.mq.protocol.MProtocol;
import com.bcgogo.mq.protocol.ProtocolHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 对输出数据进行无掩码转换
 */
public class SocketEncoder implements ProtocolEncoder {
  private final static Logger LOG = LoggerFactory.getLogger(SocketEncoder.class);

  @Override
  public void dispose(IoSession session) throws Exception {
  }

  @Override
  public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

    try {
      byte[] msgByte = null;
      MProtocol protocol = (MProtocol) message;
      msgByte = protocol.getBtProtocol();
      LOG.info("SocketEncoder encode,data length is {}",msgByte.length);
      MProtocolType protocolType = (MProtocolType) session.getAttribute("ptl_type");
      if (MProtocolType.WEB_SOCKET.equals(protocolType)) {
        LOG.info("encode webSocket");
        msgByte = ProtocolHelper.webSocketEncode(msgByte);
      }
      IoBuffer buff = IoBuffer.allocate(msgByte.length);
      buff.put(msgByte);
      buff.flip();
      out.write(buff);
    } catch (Exception e) {
      LOG.error("SocketEncoder 过滤器发生异常!");
      LOG.error(e.getMessage(), e);
    }


  }


}