package com.bcgogo.mq.service.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class SocketCodecFactory implements ProtocolCodecFactory {

  @Override
  public ProtocolDecoder getDecoder(IoSession session) throws Exception {
    return new SocketDecoder();
  }

  @Override
  public ProtocolEncoder getEncoder(IoSession session) throws Exception {
    return new SocketEncoder();
  }
}
