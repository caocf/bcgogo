package com.bcgogo.pojo.protocol;

/**
* Created by IntelliJ IDEA.
* Author: ndong
* Date: 2015-6-4
* Time: 11:13
*/

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

/**
* JMessageProtocol编码
* @author Simple
*
*/
public class JMessageProtocolEncoder extends ProtocolEncoderAdapter {

  private Charset charset;

  public JMessageProtocolEncoder(Charset charset) {
    this.charset=charset;
  }

  /**
   * 编码
   */
  public void encode(IoSession session, Object object, ProtocolEncoderOutput out) throws Exception {
    // new buf
    IoBuffer buf= IoBuffer.allocate(2048).setAutoExpand(true);
    // object --> AbsMP
    MMsgProtocol protocol=(MMsgProtocol)object;
//    buf.put(absMp.getTag());
//    buf.putInt(absMp.getLength());
    if(object instanceof MMsgProtocol) {// 请求协议
//      JMessageProtocolReq mpReq=(JMessageProtocolReq)object;
      buf.put(protocol.getData());
//      buf.putShort(mpReq.getFunctionCode());
//      buf.putString(mpReq.getContent(), charset.newEncoder());
    }
// else if(object instanceof JMessageProtocolRes) {// 响应协议
//      JMessageProtocolRes mpRes=(JMessageProtocolRes)object;
//      buf.put(mpRes.getResultCode());
//      buf.putString(mpRes.getContent(), charset.newEncoder());
//    }
    buf.flip();
    out.write(buf);
  }
}
