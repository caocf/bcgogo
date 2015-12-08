package com.bcgogo.pojo.common;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-3-7
 * Time: 下午3:55
 * To change this template use File | Settings | File Templates.
 */
public class Result implements Serializable {

  private static final long serialVersionUID = 6920105849582836361L;

  private String title;
  private String msg;
  private boolean success = true;
  private Integer total;
  private Object data;
  private String dataStr; //传输id用object的data，值会发生变化
  private Object[] dataList;
  private String operation;

  public Result(){}

  public Result(boolean success) {
    this.success = success;
  }

  public Result(boolean success, String msg) {
    this.msg = msg;
    this.success = success;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public String getDataStr() {
    return dataStr;
  }

  public void setDataStr(String dataStr) {
    this.dataStr = dataStr;
  }

  public Object[] getDataList() {
    return dataList;
  }

  public void setDataList(Object[] dataList) {
    this.dataList = dataList;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }
}
