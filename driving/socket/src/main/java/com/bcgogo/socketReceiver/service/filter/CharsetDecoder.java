package com.bcgogo.socketReceiver.service.filter;

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
  // 可变的IoBuffer数据缓冲区
  private IoBuffer buff = IoBuffer.allocate(1024).setAutoExpand(true);

  @Override
  public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
    byte[] b = new byte[in.limit()];
    in.get(b);
    String message = new String(b, charset);
    out.write(message);

//    while (in.hasRemaining()) {
//      // 判断消息是否是结束符，不同平台的结束符也不一样；
//      // windows换行符（\r\n）就认为是一个完整消息的结束符了； UNIX 是\n；MAC 是\r
//      byte b = in.get();
//      if (b == '\n') {
//        buff.flip();
//        byte[] bytes = new byte[buff.limit()];
//        buff.get(bytes);
//        String message = new String(bytes, charset);
//        buff = IoBuffer.allocate(100).setAutoExpand(true);
//        out.write(message);
//      } else {
//        buff.put(b);
//      }
//    }
  }

  @Override
  public void dispose(IoSession session) throws Exception {
  }

  @Override
  public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
  }
}