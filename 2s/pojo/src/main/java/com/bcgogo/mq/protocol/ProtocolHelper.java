package com.bcgogo.mq.protocol;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.constant.MQConstant;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.mq.enums.MProtocolType;
import com.bcgogo.mq.message.*;
import com.bcgogo.utils.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-12
 * Time: 下午4:48
 */
public class ProtocolHelper {


  public static byte[] webSocketDecode(byte[] byteData) throws UnsupportedEncodingException {
    // 计算非空位置
    int lastStation = byteData.length - 1;
    // 利用掩码对org-data进行异或
    int frame_masking_key = 1;
    for (int i = 6; i <= lastStation; i++) {
      frame_masking_key = i % 4;
      frame_masking_key = frame_masking_key == 0 ? 4 : frame_masking_key;
      frame_masking_key = frame_masking_key == 1 ? 5 : frame_masking_key;
      byteData[i] = (byte) (byteData[i] ^ byteData[frame_masking_key]);
    }
    return new String(byteData, 6, lastStation - 5, "UTF-8").getBytes();
  }

  /**
   * 对传入数据进行无掩码转换
   *
   * @param msgByte
   * @return
   * @throws UnsupportedEncodingException
   */
  public static byte[] webSocketEncode(byte[] msgByte) throws UnsupportedEncodingException {
    // 掩码开始位置
    int masking_key_startIndex = 2;
    // 计算掩码开始位置
    if (msgByte.length <= 125) {
      masking_key_startIndex = 2;
    } else if (msgByte.length > 65536) {
      masking_key_startIndex = 10;
    } else if (msgByte.length > 125) {
      masking_key_startIndex = 4;
    }
    // 创建返回数据
    byte[] result = new byte[msgByte.length + masking_key_startIndex];

    // 开始计算ws-frame
    // frame-fin + frame-rsv1 + frame-rsv2 + frame-rsv3 + frame-opcode
    result[0] = (byte) 0x81; // 129

    // frame-masked+frame-payload-length
    // 从第9个字节开始是 1111101=125,掩码是第3-第6个数据
    // 从第9个字节开始是 1111110>=126,掩码是第5-第8个数据
    if (msgByte.length <= 125) {
      result[1] = (byte) (msgByte.length);
    } else if (msgByte.length > 65536) {
      result[1] = 0x7F; // 127
    } else if (msgByte.length > 125) {
      result[1] = 0x7E; // 126
      result[2] = (byte) (msgByte.length >> 8);
      result[3] = (byte) (msgByte.length % 256);
    }

    // 将数据编码放到最后
    for (int i = 0; i < msgByte.length; i++) {
      result[i + masking_key_startIndex] = msgByte[i];
    }
    return result;
  }

  public static MProtocol getTalkResponse(MQTalkMessageDTO messageDTO) throws UnsupportedEncodingException {
//    MQMessageItemDTO itemDTO = new MQMessageItemDTO();
//    itemDTO.setContent(message);
//    itemDTO.setTitle("对话消息");
    MQTalkMessageDTO replyMessageDTO=new MQTalkMessageDTO();
    replyMessageDTO.setFromUserName(messageDTO.getToUserName());
    replyMessageDTO.setToUserName(messageDTO.getFromUserName());
//    talkMessageDTO.setTitle("对话消息");
    replyMessageDTO.setContent("hi");
    MProtocol protocol = new MProtocol(MProtocolType.WEB_SOCKET,JsonUtil.objectToJson(replyMessageDTO).getBytes());
    return protocol;
  }

}
