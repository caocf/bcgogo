package com.bcgogo.base;

/**
 * 程序执行结果，返回值，作为基础父类
 * User: ndong
 * Date: 12-11-22
 * Time: 上午1:35
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class BaseResult {

  private Long shopId;
  private Long userId;
  private boolean isSuccess;
  private String msg;

  public BaseResult(){
    this.setSuccess(true);
  }

  public BaseResult(boolean isSuccess,String msg){
    this.isSuccess=isSuccess;
    this.msg=msg;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public boolean isFailed(){
        return !isSuccess;
  }

  public void setSuccess(boolean success) {
    isSuccess = success;
  }

  public BaseResult LogErrorMsg(String msg){
    this.isSuccess=false;
    this.msg=msg;
    return this;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public BaseResult setMsg(boolean isSuccess,String msg) {
    this.isSuccess=isSuccess;
    this.msg = msg;
    return this;
  }

}