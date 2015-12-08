package com.bcgogo;

import com.bcgogo.pojo.MinaClientMocker;
import com.bcgogo.pojo.constants.Constant;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-9
 * Time: 下午4:37
 */
public class MirrorMain {

  public static void main(String[] args) throws IOException, InterruptedException {
    Constant.init(null);
    MinaClientMocker mocker = new MinaClientMocker(Constant.imei, Constant.SOCKET_SERVER_IP, Constant.SOCKET_SERVER_PORT);
    mocker.login();
    while (true) {
//      mocker.sendTalk();
      Thread.sleep(30000L);
    }
//    mocker.quit();
  }

}
