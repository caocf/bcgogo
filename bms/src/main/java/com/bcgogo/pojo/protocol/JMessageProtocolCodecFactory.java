package com.bcgogo.pojo.protocol;

/**
* Created by IntelliJ IDEA.
* Author: ndong
* Date: 2015-6-4
* Time: 11:12
*/

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

/**
* JMessageProtocal解码编码工厂
*
* @author Simple
*/
public class JMessageProtocolCodecFactory implements ProtocolCodecFactory {

  private final JMessageProtocolDecoder decoder;

  private final JMessageProtocolEncoder encoder;

  public JMessageProtocolCodecFactory() {
    Charset charset = Charset.forName("UTF-8");
    this.decoder = new JMessageProtocolDecoder(charset);
    this.encoder = new JMessageProtocolEncoder(charset);
  }

  public JMessageProtocolCodecFactory(Charset charset) {
    this.decoder = new JMessageProtocolDecoder(charset);
    this.encoder = new JMessageProtocolEncoder(charset);
  }

  public ProtocolDecoder getDecoder(IoSession paramIoSession) throws Exception {
    return decoder;
  }

  public ProtocolEncoder getEncoder(IoSession paramIoSession) throws Exception {
    return encoder;
  }
}