package com.bcgogo.driving.socket.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * 对输出数据进行无掩码转换
 */
public class XSocketEncoder implements ProtocolEncoder {
  private final static Logger LOG = LoggerFactory.getLogger(XSocketEncoder.class);
//  private final static Charset charset = Charset.forName("UTF-8");

  @Override
  public void dispose(IoSession session) throws Exception {
  }

  @Override
  public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

    try {
//      LOG.debug("XSocketEncoder encode,data:{}",String.valueOf(message));
      byte[] msgByte =String.valueOf(message).getBytes();
      IoBuffer buff = IoBuffer.allocate(msgByte.length);
      buff.put(msgByte);
      buff.flip();
      out.write(buff);

//      IoBuffer buff = IoBuffer.allocate(100).setAutoExpand(true);
//      buff.putString(message.toString(), charset.newEncoder());
//      buff.putString(LineDelimiter.DEFAULT.getValue(), charset.newEncoder());
//      buff.flip();
//      out.write(buff);


    } catch (Exception e) {
      LOG.error("SocketEncoder 过滤器发生异常!");
      LOG.error(e.getMessage(), e);
    }


  }


}