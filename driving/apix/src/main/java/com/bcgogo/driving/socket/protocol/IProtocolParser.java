package com.bcgogo.driving.socket.protocol;

import org.apache.mina.core.session.IoSession;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-18
 * Time: 上午11:10
 */
public interface IProtocolParser {

  void doParse(IoSession session, String hexString) throws Exception;

}
