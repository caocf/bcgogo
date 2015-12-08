package com.bcgogo.driving.socket.codec;

import com.bcgogo.pojox.util.BinaryUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>function:</b> 字符解码
 */
public class XSocketDecoder implements ProtocolDecoder {

  private static final Logger LOG = LoggerFactory.getLogger(XSocketDecoder.class);

  private final static Charset charset = Charset.forName("UTF-8");

  @Override
  public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
    try {
      IoBuffer buff = IoBuffer.allocate(in.limit());
      while (in.hasRemaining()) {
        buff.put(in.get());
      }
      buff.flip();
      byte[] byteData = buff.array();
      LOG.debug("server receiver hex:{}", BinaryUtil.byte2HexString(byteData));
      out.write(byteData);
    } catch (Exception e) {
      LOG.error("SocketDecoder 过滤器发生异常!");
      LOG.error(e.getMessage(), e);
    }
  }


  @Override
  public void dispose
    (IoSession
       session) throws Exception {
  }

  @Override
  public void finishDecode
    (IoSession
       session, ProtocolDecoderOutput
      out) throws Exception {
  }

  public String getSecWebSocketAccept(String key) {
    String secKey = getSecWebSocketKey(key);

    String guid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    secKey += guid;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update(secKey.getBytes("iso-8859-1"), 0, secKey.length());
      byte[] sha1Hash = md.digest();
      secKey = base64Encode(sha1Hash);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String rtn = "HTTP/1.1 101 Switching Protocols\r\n" +
      "Upgrade: websocket\r\n" +
      "Connection: Upgrade\r\n" +
      "Sec-WebSocket-Accept: " + secKey + "\r\n\r\n";
    return rtn;
  }

  public static String getSecWebSocketKey(String req) {
    Pattern p = Pattern.compile("^(Sec-WebSocket-Key:).+",
      Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    Matcher m = p.matcher(req);
    if (m.find()) {
      String foundstring = m.group();
      return foundstring.split(":")[1].trim();
    } else {
      return null;
    }

  }

  public static String base64Encode(byte[] input) {
    return new String(org.apache.mina.util.Base64.encodeBase64(input));
  }

}