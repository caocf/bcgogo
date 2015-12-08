package com.bcgogo.enums;

/**
 * 定时钟执行状态
 * User: ndong
 * Date: 12-11-15
 * Time: 上午6:55
 * To change this template use File | Settings | File Templates.
 */
public enum ExeStatus{
    FINISHED("完成"),
    READY("等待"),
    EXCEPTION("异常"),
    START("开始");

    String status;
    ExeStatus(String status){
      this.status = status;
    }
    public String getStatus(){
      return this.status;
    }
  }