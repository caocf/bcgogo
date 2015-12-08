package com.bcgogo.wx;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-28
 * Time: 上午11:36
 * To change this template use File | Settings | File Templates.
 */
public class ErrCode {
  public static final String SUCCESS="0";
  public static final String ERROR="40018";


  private String errcode;
  private String errmsg;

  public String getErrcode() {
    return errcode;
  }

  public void setErrcode(String errcode) {
    this.errcode = errcode;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }
}
