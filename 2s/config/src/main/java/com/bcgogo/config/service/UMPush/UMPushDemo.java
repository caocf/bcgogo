package com.bcgogo.config.service.UMPush;

import com.bcgogo.config.service.UMPush.UMPushConstant.UMAfterOpen;
import com.bcgogo.config.service.UMPush.UMPushConstant.UMPushDisplayType;
import com.bcgogo.config.service.UMPush.UMPushConstant.UMPushType;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.RandomUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by XinyuQiu on 14-4-28.
 */
public class UMPushDemo {

  public static void f1(String[]args) throws Exception{
    String appkey = "533b7b6a56240b29f80bc58f";
    String timestamp = String.valueOf(System.currentTimeMillis());
    String appMasterSecret = "2yaa3scwibfvfaikfe7ypwrzlspkpg2f";
    String validateToken = DigestUtils.md5Hex(appkey.toLowerCase()+appMasterSecret.toLowerCase()+timestamp);
    String device_tokens = "AjBYIKh29BMyEl94supjsyM6GRx1MBn5MPzUJsE0obnI";
    UnicastUMNotification notification = new UnicastUMNotification();
    notification.setAppkey(appkey);
    notification.setTimestamp(timestamp);
    notification.setDevice_tokens(device_tokens);
    notification.setProduction_mode(true);
    notification.setType(UMPushType.unicast);
    notification.setValidation_token(validateToken);
    UMPayLoad payLoad = new UMPayLoad();
    notification.setPayload(payLoad);
    UMPayLoadBody body = new UMPayLoadBody();
    payLoad.setBody(body);
    payLoad.setDisplay_type(UMPushDisplayType.notification);
    body.setTicker("消息描述" + RandomUtils.randomNumeric(10));
    body.setTitle(" 通知标题" + RandomUtils.randomNumeric(10));
    body.setText(" 通知文字描述" + RandomUtils.randomNumeric(10));
    body.setAfter_open(UMAfterOpen.go_app);
    System.out.println("payload size :" + JsonUtil.objectToJson(payLoad).length() );
    String sendUrl = "http://msg.umeng.com/api/send";
    URL url = new URL(sendUrl);
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    urlConnection.setRequestMethod("POST");
    urlConnection.setDoOutput(true);
    urlConnection.setDoInput(true);
    urlConnection.setUseCaches(false);
    urlConnection.setConnectTimeout(30000);
    urlConnection.setReadTimeout(30000);
    urlConnection.setRequestProperty("Content-Type","application/json");
    urlConnection.getOutputStream().write(JsonUtil.objectToJson(notification).getBytes());
    urlConnection.getOutputStream().flush();
    urlConnection.getOutputStream().close();

    InputStream in = urlConnection.getInputStream();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
    StringBuilder temp = new StringBuilder();
    String line = bufferedReader.readLine();
    while (line != null) {
      if(temp.length()>0){
        temp.append("\r\n");
      }
      temp.append(line);
      line = bufferedReader.readLine();
    }
    bufferedReader.close();
    UMSendResponse umPushResponse = null;
    if(StringUtils.isNotBlank(temp.toString())){
      umPushResponse = JsonUtil.fromJson(temp.toString(),UMSendResponse.class);
    }
    if(umPushResponse != null){
      System.out.println(umPushResponse);
      System.out.println(JsonUtil.objectToJson(umPushResponse));
    }

    System.out.println(temp.toString());





  }

  public static void f2(String[]args) throws Exception{
    String device_tokens = "AjBYIKh29BMyEl94supjsyM6GRx1MBn5MPzUJsE0obnI";
    for(int i=0;i<3;i++){
      Long start = System.currentTimeMillis();
      GSMUMPushAdapter.sendPushMessage("服务提醒","您预约车辆苏E0430205月01日 12时34分洗车已修改为05月01日 12时34分保险理赔，详询。一发软件",device_tokens);
      Long end = System.currentTimeMillis();
      System.out.println(end-start);

    }

  }

  public static void main(String[]args)throws Exception{
    UMPushDemo.f2(args);
  }
}
