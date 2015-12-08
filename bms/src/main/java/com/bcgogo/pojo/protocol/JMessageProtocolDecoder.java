package com.bcgogo.pojo.protocol;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-4
 * Time: 11:06
 */

import com.bcgogo.pojo.constants.Constant;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * JMessageProtocal解码
 *
 * @author Simple
 */
public class JMessageProtocolDecoder extends ProtocolDecoderAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(JMessageProtocolDecoder.class);

  private Charset charset;

  public JMessageProtocolDecoder(Charset charset) {
    this.charset = charset;
  }

  /**
   * 解码
   */
  public void decode(IoSession session, IoBuffer buf, ProtocolDecoderOutput out) throws Exception {
    IoBuffer allBuf = IoBuffer.allocate(100).setAutoExpand(true);
    while (buf.hasRemaining()) {
      byte b = buf.get();
          System.out.println("b:" + b);
          allBuf.put(b);
    }


    MinaProtocol protocol = null;
    // 获取协议tag
//    byte tag=buf.get();
    // 获取协议体长度
    int length = buf.getInt();
    // 取出协议体
    byte[] bodyData = new byte[length];
    buf.get(bodyData);
    // 为解析数据做准备
    // 检测协议
    IoBuffer tempBuf = IoBuffer.allocate(100).setAutoExpand(true);
//    tempBuf.put(tag);
    tempBuf.putInt(length);
    tempBuf.put(bodyData);
    tempBuf.flip();
    if (!canDecode(tempBuf)) {
      return;
    }
    // 协议体buf
    IoBuffer bodyBuf = IoBuffer.allocate(100).setAutoExpand(true);
    bodyBuf.put(bodyData);
    bodyBuf.flip();
    // 整个协议buf
//    allBuf.put(tag);
    allBuf.putInt(length);
    allBuf.put(bodyData);
    allBuf.flip();
    //
//    if(tag == Constant.REQ) {
//      JMessageProtocolReq req=new JMessageProtocolReq();
//      short functionCode=bodyBuf.getShort();
//      String content=bodyBuf.getString(charset.newDecoder());
//      req.setFunctionCode(functionCode);
//      req.setContent(content);
//      protocol=req;
//    } else if(tag == Constant.RES) {
//      JMessageProtocolRes res=new JMessageProtocolRes();
//      byte resultCode=bodyBuf.get();
//      String content=bodyBuf.getString(charset.newDecoder());
//      res.setResultCode(resultCode);
//      res.setContent(content);
//      protocol=res;
//    } else {
//      LOG.error("未定义的Tag");
//    }
    out.write(protocol);
  }

  // 是否可以解码
  private boolean canDecode(IoBuffer buf) {
    int protocalHeadLength = 5;// 协议头长度
    int remaining = buf.remaining();
    if (remaining < protocalHeadLength) {
      LOG.error("错误，协议不完整，协议头长度小于" + protocalHeadLength);
      return false;
    } else {
      LOG.debug("协议完整");
      // 获取协议tag
      byte tag = buf.get();
//      if (tag == Constant.REQ || tag == Constant.RES) {
//        LOG.debug("Tag=" + tag);
//      } else {
//        LOG.error("错误，未定义的Tag类型");
//        return false;
//      }
      // 获取协议体长度
      int length = buf.getInt();
      if (buf.remaining() < length) {
        LOG.error("错误，真实协议体长度小于消息头中取得的值");
        return false;
      } else {
        LOG.debug("真实协议体长度:" + buf.remaining() + " = 消息头中取得的值:" + length);
      }
    }
    return true;
  }
}
