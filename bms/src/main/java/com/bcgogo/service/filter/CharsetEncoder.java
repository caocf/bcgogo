package com.bcgogo.service.filter;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;

import java.nio.charset.Charset;

/**
 * <b>function:</b> 字符编码
 */
public class CharsetEncoder implements ProtocolEncoder {
  private final static Logger log = Logger.getLogger(CharsetEncoder.class);
  private final static Charset charset = Charset.forName("UTF-8");

  @Override
  public void dispose(IoSession session) throws Exception {
  }

  @Override
  public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
    byte bt[] = (byte[]) message;
    IoBuffer buff = IoBuffer.allocate(bt.length);
    buff.put(bt, 0, bt.length);
//    buff.put(LineDelimiter.DEFAULT.getValue(), charset.newEncoder());
    buff.flip();
    out.write(buff);
  }
}