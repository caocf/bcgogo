package com.bcgogo.obd.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * <b>function:</b> 字符编码
 */
public class OBDCharsetEncoder implements ProtocolEncoder {
  public static final Logger LOG = LoggerFactory.getLogger(OBDCharsetEncoder.class);
  private final static Charset charset = Charset.forName("UTF-8");

  @Override
  public void dispose(IoSession session) throws Exception {
  }

  @Override
  public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
      byte[] msgByte =(byte[])message;
    LOG.debug("TestPCharsetEncoder encode,data:{}", new String(msgByte,"UTF-8"));
    IoBuffer buff = IoBuffer.allocate(msgByte.length);
    buff.put(msgByte);
    buff.flip();
    out.write(buff);

  }
}