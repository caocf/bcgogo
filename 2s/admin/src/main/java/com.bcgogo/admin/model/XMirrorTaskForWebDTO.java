package com.bcgogo.admin.model;

import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-28
 * Time: 下午6:45
 * 传递给web端的后视镜任务DTO.
 */
public class XMirrorTaskForWebDTO implements Serializable {
  private String id;         //id
  private String tid;    //任务类型
  private String param;     //参数
  private String imei;      //设备id (IMEI)
  private String status;    //任务状态
  private String filePath;  //文件路径
  private Long createdTime;//创建时间
  private Long lastUpdateTime;//最后修改时间
  private String createdTimeString;//格式化的创建时间
  private String lastUpdateTimeString;//格式化的最后修改时间

  public XMirrorTaskForWebDTO() {
    super();
  }

  public XMirrorTaskForWebDTO(String id, String tid, String param, String imei, String status, String filePath, Long createdTime, Long lastUpdateTime) {
    this.id = id;
    this.tid = tid;
    this.param = param;
    this.imei = imei;
    this.status = status;
    this.filePath = filePath;
    this.createdTime = createdTime;
    this.lastUpdateTime = lastUpdateTime;
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

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
    this.setCreatedTimeString(DateUtil.convertDateLongToDateString(DateUtil.ALL, createdTime));
    //this.setSubmitReviewTimeStr(DateUtil.convertDateLongToDateString(DateUtil.ALL, submitReviewTime));
  }

  public Long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
    this.setLastUpdateTimeString(DateUtil.convertDateLongToDateString(DateUtil.ALL, lastUpdateTime));
    //this.setSubmitReviewTimeStr(DateUtil.convertDateLongToDateString(DateUtil.ALL, submitReviewTime));
  }

  public String getCreatedTimeString() {
    return createdTimeString;
  }

  public void setCreatedTimeString(String createdTimeString) {
    this.createdTimeString = createdTimeString;
  }

  public String getLastUpdateTimeString() {
    return lastUpdateTimeString;
  }

  public void setLastUpdateTimeString(String lastUpdateTimeString) {
    this.lastUpdateTimeString = lastUpdateTimeString;
  }
}
