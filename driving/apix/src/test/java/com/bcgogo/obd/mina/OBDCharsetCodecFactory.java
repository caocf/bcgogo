package com.bcgogo.obd.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class OBDCharsetCodecFactory implements ProtocolCodecFactory {

  @Override
  public ProtocolDecoder getDecoder(IoSession session) throws Exception {
    return new OBDCharsetDecoder();
  }

  @Override
  public ProtocolEncoder getEncoder(IoSession session) throws Exception {
    return new OBDCharsetEncoder();
  }
}
