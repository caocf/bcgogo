package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.enums.app.MessageCode;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-5-18
 * Time: 17:54
 */
public class ApiVideoInitResponse extends ApiResponse {
   private Long impactId;
  private String uploadFlag; //是否上传标志
  private String msg;


  public ApiVideoInitResponse(String uploadFlag, String msg,Long impactId) {
    setMessageCode(MessageCode.SUCCESS);
    this.impactId=impactId;
    this.uploadFlag = uploadFlag;
    this.msg = msg;
  }

  public Long getImpactId() {
    return impactId;
  }

  public void setImpactId(Long impactId) {
    this.impactId = impactId;
  }

  public String getUploadFlag() {
    return uploadFlag;
  }

  public void setUploadFlag(String uploadFlag) {
    this.uploadFlag = uploadFlag;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
