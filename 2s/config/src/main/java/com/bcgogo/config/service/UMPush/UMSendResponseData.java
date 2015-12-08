package com.bcgogo.config.service.UMPush;

/**
 * Created by XinyuQiu on 14-4-29.
 */
public class UMSendResponseData {
  private String msg_id;
  private String  task_id;
  private Integer error_code;

  public String getMsg_id() {
    return msg_id;
  }

  public void setMsg_id(String msg_id) {
    this.msg_id = msg_id;
  }

  public String getTask_id() {
    return task_id;
  }

  public void setTask_id(String task_id) {
    this.task_id = task_id;
  }

  public Integer getError_code() {
    return error_code;
  }

  public void setError_code(Integer error_code) {
    this.error_code = error_code;
  }
}
