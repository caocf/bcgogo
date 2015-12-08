package com.bcgogo.notification.service;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 3/4/12
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TricomSmsMock implements ITricomSMS{

  @Override
  public String send(String mobile, String content) throws Exception {
    String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<result>\n" +
        "<response>"+code+"</response><sms><phone>130982922</phone><smsID>1000000000000</smsID></sms></result>";
    return response;
  }

  @Override
  public String receive() throws Exception {
    return null;
  }

  @Override
  public String query(String smsId) throws Exception {
   return null;
  }

  @Override
  public void setResponseCode(Integer code){
    this.code = code;
  }

    @Override
    public float caculateDeductAmount(String content) {
        return (float) 0.1;
    }

    private int code = 1;

}
