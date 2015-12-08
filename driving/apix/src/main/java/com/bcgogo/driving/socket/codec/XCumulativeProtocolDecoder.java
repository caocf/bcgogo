package com.bcgogo.driving.socket.codec;

import com.bcgogo.driving.socket.SocketHelper;
import com.bcgogo.pojox.util.BinaryUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-22
 * Time: 上午11:33
 */
public class XCumulativeProtocolDecoder extends CumulativeProtocolDecoder {

  private static final Logger LOG = LoggerFactory.getLogger(XCumulativeProtocolDecoder.class);


  /**
   * 每次读取一个包,
   * 返回true可以清掉缓存已读的数据，继续读取下一个
   * 段包时，返回false可以重复读取缓存数据
   *
   * @param session
   * @param in
   * @param out
   * @return
   * @throws Exception
   */
  public boolean doDecode(IoSession session, IoBuffer in,
                          ProtocolDecoderOutput out) throws Exception {
    if (in.remaining() <= 0) {
      LOG.error("tmp server receiver empty hex!");
      return false;
    }
    String hexString = SocketHelper.getIoBufferHexString(in, in.remaining());
//    LOG.debug("server receiver data packet,remain len:{},hexString:{},", in.remaining(), hexString);
    int packet_size = hexString.length() / 2;
    if (!hexString.startsWith("7E") || !hexString.endsWith("7E")) {
      //处理不是7E开头的数据
      boolean trunk = trunkIllegalHead(in);
      if (trunk) {
        LOG.debug("trunk illegal str");
        return true;
      }
      //计算包size
      packet_size = SocketHelper.calPacketSize(in);
      if (packet_size == 0) {
        LOG.error("无结束标志,等待重复读取缓存数据。");
        return false;
      }
    }
    //每次从缓存读一个包的长度
    IoBuffer buff = IoBuffer.allocate(packet_size);
    for (int i = 0; i < packet_size; i++) {
      buff.put(in.get());
    }
    LOG.debug("allocate success, packet_size:{}", packet_size);
    out.write(buff.array());
    return true;
  }

  /**
   * 处理不是7E开头的数据
   *
   * @param in
   * @throws UnsupportedEncodingException
   */
  private static boolean trunkIllegalHead(IoBuffer in) throws UnsupportedEncodingException {
    boolean trunk = false;
    String hexString = SocketHelper.getIoBufferHexString(in, in.remaining());
    for (int i = 0; i < in.remaining(); i += 2) {
      if ("7E".equals(hexString.substring(i, i + 2)) && !"7E".equals(hexString.substring(i + 2, i + 4))) {
        String tmp_hex = hexString.substring(0, hexString.indexOf("7E", 2) + 2);
        if (tmp_hex.length() % 2 == 0) {
          return false;
        } else {
          in.get(tmp_hex.length());
          return true;
        }
      } else {
        in.get();
        trunk = true;
      }
    }
    return trunk;
  }

}
