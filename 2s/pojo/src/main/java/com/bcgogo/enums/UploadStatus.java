package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-21
 * Time: 17:52
 */
public enum UploadStatus {
  UPLOADING("上传中"),//
  UPLOAD_SWITCH_OFF("视频上传开关已关闭"),
  EXCEPTION("上传出现异常"),
  SUCCESS("上传成功"),
  ;

  UploadStatus(String status){
    this.status=status;
  }

  private String status;

  public String getStatus() {
    return status;
  }
}
