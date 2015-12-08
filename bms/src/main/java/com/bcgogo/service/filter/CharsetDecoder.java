package com.bcgogo.service.filter;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

/**
 * <b>function:</b> 字符解码
 */
public class CharsetDecoder implements ProtocolDecoder {

  private final static Logger LOG = Logger.getLogger(CharsetDecoder.class);

  private final static Charset charset = Charset.forName("UTF-8");

  public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
    // 可变的IoBuffer数据缓冲区
    IoBuffer buff = IoBuffer.allocate(in.limit());
    while (in.hasRemaining()) {
      buff.put(in.get());
    }
    buff.flip();
    out.write(buff.array());
  }

  public void dispose(IoSession session) throws Exception {
  }

  public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
  }
}