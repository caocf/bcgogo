package com.bcgogo.driving.socket.protocol;


import com.bcgogo.driving.SpringUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-18
 * Time: 上午11:08
 */
public class ProtocolFactory {

  public static IProtocolParser getHandler(String hexString) {
    if (hexString.startsWith("7E") && hexString.endsWith("7E")) {
      return  (POBDParser) SpringUtil.getObject("POBDParser");
    }
    return null;
  }

}
