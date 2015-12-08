package com.bcgogo;

import com.bcgogo.pojo.util.ConfigUtil;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-4
 * Time: 11:36
 */
public class Temp {

  public static void main(String[] args) {
    try {
      File path = new File(".");
      String[] list = path.list();
      for (int i = 0; i < list.length; i++) {
        System.out.println(list[i]);
      }
      System.out.println(ConfigUtil.readOutPropertyFile("CAMERA.CLIENT.UUID"));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
