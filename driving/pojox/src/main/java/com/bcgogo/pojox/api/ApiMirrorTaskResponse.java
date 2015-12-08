package com.bcgogo.pojox.api;

import com.bcgogo.pojox.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-30
 * Time: 下午2:21
 * To change this template use File | Settings | File Templates.
 */
public class ApiMirrorTaskResponse {
  private static final Logger LOG = LoggerFactory.getLogger(ApiMirrorTaskResponse.class);
  private int code;
  private String msg;
  private Object data=null;

  public ApiMirrorTaskResponse(ApiMirrorTaskResponse response) {
    setApiMirrorTaskResponse(response);
  }

  public ApiMirrorTaskResponse() {
    super();
  }

  public ApiMirrorTaskResponse(int code, String msg, Object data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public void setApiMirrorTaskResponse(ApiMirrorTaskResponse response){
    this.code=response.code;
    this.msg=response.msg;
    this.data=response.data;
  }

  @Override
  public String toString() {
    return "ApiMirrorTaskResponse{" +
        "code=" + code +
        ", msg='" + msg + '\'' +
        ", data=" + data +
        '}';
  }

//  public static void main(String[]args){
//    int code=0;
//    String msg="msg";
//    XMirrorTaskForDevDTO[] data=new XMirrorTaskForDevDTO[2];
//    data[0].setId("0");
//    data[1].setId("1");
//    data[0].setTid("00");
//    data[1].setTid("01");
//    data[0].setParam("asdf");
//    data[1].setParam("asdf");
//    ApiMirrorTaskResponse response=new ApiMirrorTaskResponse(code,msg,data);
//    System.out.println(response.toString());
//  }
}
