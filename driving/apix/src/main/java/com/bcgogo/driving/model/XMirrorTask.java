package com.bcgogo.driving.model;

import com.bcgogo.driving.model.mongodb.XLongIdentifier;
import com.bcgogo.pojox.api.XMirrorTaskDTO;
import com.bcgogo.pojox.api.XMirrorTaskForDevDTO;

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
