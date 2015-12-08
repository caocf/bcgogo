package com.bcgogo.admin.model;

import com.bcgogo.etl.model.mongodb.XLongIdentifier;
import com.bcgogo.admin.model.XMirrorTaskDTO;
import com.bcgogo.admin.model.XMirrorTaskForDevDTO;

import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-29
 * Time: 上午10:57
 * 后视镜任务
 */
public class XMirrorTask extends XLongIdentifier {
  private String tid;   //任务类型
  private String param;    //参数
  private String imei;     //设备id (IMEI)
  private String status;  //任务状态
  private String filePath;//文件路径

  public String tidStr(){
    if("1".equals(tid)){
      return "标定OBD";
    }
    if("2".equals(tid)){
      return "Log上传";
    }
    return "";
  }

  public String statusStr(){
    if("0".equals(status)){
      return "等待";
    }
    if("-1".equals(status)){
      return "失败";
    }
    if("1".equals(status)){
      return "成功";
    }
    return "";
  }

  @Override
  public String toString() {
    return "XMirrorTask{" +
        "tid='" + tid + '\'' +
        ", param='" + param + '\'' +
        ", imei='" + imei + '\'' +
        ", status='" + status + '\'' +
        ", filePath='" + filePath + '\'' +
        '}';
  }

  public XMirrorTask() {
    super();
  }

  /**
   * 将XMirrorTaskDTO转换为XMirrorTask
   * @param xMirrorTaskDTO
   */
  public void fromDTO(XMirrorTaskDTO xMirrorTaskDTO){
    this.set_id(xMirrorTaskDTO.getId());
    this.setTid(xMirrorTaskDTO.getTid());
    this.setParam(xMirrorTaskDTO.getParam());
    this.setImei(xMirrorTaskDTO.getImei());
    this.setStatus(xMirrorTaskDTO.getStatus());
  }

  /**
   * 将XMirrorTask转换为XMirrorTaskDTO
   * @return
   */
  public XMirrorTaskDTO toDTO(){
    XMirrorTaskDTO dto=new XMirrorTaskDTO();
    dto.setId(get_id().get$oid());
    dto.setTid(getTid());
    dto.setParam(getParam());
    dto.setImei(getImei());
    dto.setStatus(getStatus());
    return dto;
  }

  /**
   * 将XMirrorTask转换为专门传输给设备的类型XMirrorTaskForDevDTO
   * @return
   */
  public XMirrorTaskForDevDTO toDevDTO(){
    XMirrorTaskForDevDTO devDTO=new XMirrorTaskForDevDTO();
    devDTO.setId(get_id().get$oid());
    devDTO.setTid(Integer.parseInt(getTid()));
    devDTO.setParam(getParam());
    return devDTO;
  }

  /**
   * 将XMirrorTask转换为XMirrorTaskForWebDTO
   * @return
   */
  public XMirrorTaskForWebDTO toWebDTO(){
    XMirrorTaskForWebDTO dto=new XMirrorTaskForWebDTO();
    dto.setId(get_id().get$oid());
    dto.setTid(tidStr());
    dto.setParam(getParam());
    dto.setImei(getImei());
    dto.setStatus(statusStr());
    dto.setFilePath(getFilePath());
    dto.setCreatedTime(new Long(getCreated().get$numberLong()));
    dto.setLastUpdateTime(new Long(getLastUpdate().get$numberLong()));
    return dto;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
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

  public String getTid() {

    return tid;
  }

  public void setTid(String tid) {
    this.tid = tid;
  }
}
