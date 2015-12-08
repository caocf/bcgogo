package com.bcgogo.notification.service;

import com.bcgogo.utils.StringUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TricomSMS implements ITricomSMS {

  String tricomUrl = "http://3tong.cn:8080/ema_new/";
  String account = "838095";
  String normail_pass = "bcgogo66733331";
  String password = "afa9f3904d5f775bb82a513ae32dc9c0";

  String postData;

  private static final int SMS_UNIT_LENGTH = 70;
  private static final float SMS_UNIT_PRICE = (float) 0.1;

  public TricomSMS() {
    this.postData = "Account=" + this.account + "&Password="
        + this.password;
  }

  @Override
  public String send(String mobile, String content) throws Exception {
    return this.post(this.tricomUrl + "http/SendSms", this.postData
        + "&Phone=" + mobile + "&Content=" + java.net.URLEncoder.encode(content, "UTF-8"));
  }

  @Override
  public String receive() throws Exception {
    return this.post(this.tricomUrl + "http/GetSms", this.postData);
  }

  @Override
  public String query(String smsId) throws Exception {
    return this.post(this.tricomUrl + "http/GetReport", this.postData
        + "&SmsID=" + smsId);
  }

  private String post(String url, String postData) throws Exception {
    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

    httpURLConnection.setRequestMethod("POST");
    httpURLConnection.setDoOutput(true);
    OutputStream out = httpURLConnection.getOutputStream();
    out.write(postData.getBytes("GBK"));
    out.flush();
    out.close();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "GBK"));
    StringBuffer stringBuffer = new StringBuffer();
    int ch;
    while ((ch = bufferedReader.read()) > -1) {
      stringBuffer.append((char) ch);
    }
    bufferedReader.close();

    return stringBuffer.toString();
  }

  @Override
  public void setResponseCode(Integer code) {
    return;
  }

    public float caculateDeductAmount(String content){
        if(StringUtil.isEmpty(content)){
            return 0;
        }
        int length = content.length();
        int smsCount = length%SMS_UNIT_LENGTH == 0?length/SMS_UNIT_LENGTH:length/SMS_UNIT_LENGTH + 1;
        return smsCount*SMS_UNIT_PRICE;
    }
}
