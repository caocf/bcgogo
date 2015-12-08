package com.bcgogo.notification.service;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 3/4/12
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ITricomSMS {
  public String send(String mobile, String content) throws Exception;

  public String receive() throws Exception;

  public String query(String smsId) throws Exception;

   public void setResponseCode(Integer code);

    public float caculateDeductAmount(String content);
}
