package com.bcgogo.driving.socket.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;

import java.nio.charset.Charset;

public class XSocketCodecFactory implements ProtocolCodecFactory {

   private final static Charset charset = Charset.forName("UTF-8");
    //分隔符 7E
   private LineDelimiter enLineDelimiter = new LineDelimiter("7E");

  public XSocketCodecFactory(){}

  @Override
  public ProtocolDecoder getDecoder(IoSession session) throws Exception {
//    return new XSocketDecoder();
    return new XCumulativeProtocolDecoder();
//    return new TextLineDecoder(charset, enLineDelimiter);
  }

  @Override
  public ProtocolEncoder getEncoder(IoSession session) throws Exception {
    return new XSocketEncoder();
  }
}
