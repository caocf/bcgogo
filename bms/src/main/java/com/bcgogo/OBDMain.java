package com.bcgogo;

import com.bcgogo.pojo.MinaClientMocker;
import com.bcgogo.pojo.OBDMocker;
import com.bcgogo.pojo.constants.Constant;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-18
 * Time: 下午4:05
 */
public class OBDMain {

  public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
    OBDMocker mocker = new OBDMocker();
    mocker.driveLogTest();
  }
}
