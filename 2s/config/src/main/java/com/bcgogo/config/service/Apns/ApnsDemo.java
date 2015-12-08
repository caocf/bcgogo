package com.bcgogo.config.service.Apns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by XinyuQiu on 14-4-10.
 */
public class ApnsDemo {

  private static final Logger LOG = LoggerFactory.getLogger(ApnsDemo.class);
  public static void main (String[] args)throws Exception{
    String tokenYGJ = "17be756e24288e96cfd4f2a60201fd5a7cf44472d8b0eb3d637493ee5d6afd6d";
    String tokenQXY = "62959d2a8937cbf199c2651fa7f5937be8ab6384b132b6ba59bcb75bbafe7784";
//    String pwd = "jamestonggou";
//    String p12Path = "E:\\技术专题\\APNS\\GSM-IOS\\gsm_development_push_java.p12";
    String message = "hello world";
//    ApnsService service =
//        APNS.newService()
//            .withCert(p12Path, pwd)
//            .withSandboxDestination()
//            .build();
//    Map<String,Object> customerField = new HashMap<String, Object>();
//    customerField.put("P","2345678901234567890,2345678901234567890,2345678901234567890");
//    customerField.put("T","1");
//
//    String payload = APNS.newPayload()
//        .alertBody(message)
//        .badge(3)
//        .sound("default")
//        .customFields(customerField)
//        .build();
//    service.push(tokenQXY, payload);
    Long start = System.currentTimeMillis();
    for(int i=0;i<1000;i++){

      GsmAPNSAdapter.sendPushMessage(message + i, tokenQXY);


//      Test2 test2 = new Test2();
//      test2.flag = " T"+i;
//      test2.run();
    }
    Long end = System.currentTimeMillis();
    System.out.println(end - start);

  }
  static class Test2 extends Thread{
    String flag ;
    public void run(){
      Long start = System.currentTimeMillis();
      GsmAPNSAdapter.sendPushMessage("hello world" + flag, "62959d2a8937cbf199c2651fa7f5937be8ab6384b132b6ba59bcb75bbafe7784");
      Long end = System.currentTimeMillis();
      System.out.println(end - start);
    }
  }
}
