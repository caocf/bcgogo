package com.bcgogo.pojox.api;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-28
 * Time: 下午6:45
 * 后视镜任务DTO.
 */
public class XMirrorTaskDTO implements Serializable {
  private String id;         //id
  private String tid;    //任务类型
  private String param;     //参数
  private String imei;      //设备id (IMEI)
  private String status;    //任务状态
  private String filePath;  //文件路径

  public XMirrorTaskDTO() {
    super();
  }

  public XMirrorTaskDTO(String id, String tid, String param, String imei, String status, String filePath) {
    this.id = id;
    this.tid = tid;
    this.param = param;
    this.imei = imei;
    this.status = status;
    this.filePath = filePath;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTid() {
    return tid;
  }

  public void setTid(String tid) {
    this.tid = tid;
  }

  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
