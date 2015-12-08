package com.bcgogo.pojox.api;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-11-1
 * Time: 下午2:14
 * 从设备端接收到的param参数
 */
public class ParamDTO {
  private int tid;
  private int result;
  private String msg;
  public static final int RESULT_SUCCESS=0;
  public static final int RESULT_FAILED=1;

  public ParamDTO() {
    super();
  }

  public ParamDTO(int tid, int result, String msg) {
    this.tid = tid;
    this.result = result;
    this.msg = msg;
  }

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }

  public int getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = result;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
