package com.bcgogo.config.model;

import com.bcgogo.camera.CameraDTO;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjie
 * Date: 14-12-25
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "camera")
public class Camera extends LongIdentifier {
  private String serial_no;
  private Long last_heart_date;
  private String lan_ip;
  private String lan_port;
  private String username;
  private String password;
  private String domain_username;
  private String domain_password;
  private String status;
  private String remark;
  private String external_address;


  public Camera fromCameraDTO(CameraDTO cameraDTO) throws ParseException {
    this.setId(NumberUtil.longValue(cameraDTO.getId()));
    this.setSerial_no(cameraDTO.getSerial_no().equals("")?null:cameraDTO.getSerial_no());
    if(StringUtil.isEmpty(cameraDTO.getLast_heart_date())){
      this.setLast_heart_date(0L);
    }else{
      this.setLast_heart_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL,cameraDTO.getLast_heart_date()));
    }
    this.setLan_ip(cameraDTO.getLan_ip().equals("")?null:cameraDTO.getLan_ip());
    this.setLan_port(cameraDTO.getLan_port().equals("")?null:cameraDTO.getLan_port());
    this.setUsername(cameraDTO.getUsername().equals("")?null:cameraDTO.getUsername());
    this.setPassword(cameraDTO.getPassword().equals("")?null:cameraDTO.getPassword());
    this.setDomain_username(cameraDTO.getDomain_username().equals("")?null:cameraDTO.getDomain_username());
    this.setDomain_password(cameraDTO.getDomain_password().equals("")?null:cameraDTO.getDomain_password());
    if("已绑定".equals(cameraDTO.getStatus())){
      this.setStatus("binding");
    } else{
      this.setStatus("nobinding");
    }
    this.setRemark(cameraDTO.getRemark().equals("")?null:cameraDTO.getRemark());
    this.setExternal_address(cameraDTO.getExternal_address().equals("")?null:cameraDTO.getExternal_address());
    return this;
  }

  public Camera fromCameraDTOVLPR(CameraDTO cameraDTO) throws ParseException {
    this.setId(NumberUtil.longValue(cameraDTO.getId()));
    this.setSerial_no(cameraDTO.getSerial_no());
    this.setLast_heart_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL,cameraDTO.getLast_heart_date()));
    this.setLan_ip(cameraDTO.getLan_ip());
    this.setLan_port(cameraDTO.getLan_port());
    this.setUsername(cameraDTO.getUsername());
    this.setPassword(cameraDTO.getPassword());
    return this;
  }

  public CameraDTO toCameraDTO(){
    CameraDTO cameraDTO = new CameraDTO();
    cameraDTO.setId(this.getId().toString());
    cameraDTO.setSerial_no(this.getSerial_no());
    cameraDTO.setLast_heart_date(formatter.format(new Date(this.getLast_heart_date())));
    cameraDTO.setLan_ip(this.getLan_ip());
    cameraDTO.setLan_port(this.getLan_port());
    cameraDTO.setUsername(this.getUsername());
    cameraDTO.setPassword(this.getPassword());
    return cameraDTO;
  }



  @Column(name = "serial_no")
  public String getSerial_no() {
    return serial_no;
  }

  public void setSerial_no(String serial_no) {
    this.serial_no = serial_no;
  }

  @Column(name = "last_heart_date")
  public Long getLast_heart_date() {
    return last_heart_date;
  }

  public void setLast_heart_date(Long last_heart_date) {
    this.last_heart_date = last_heart_date;
  }

  @Column(name = "lan_ip")
  public String getLan_ip() {
    return lan_ip;
  }

  public void setLan_ip(String lan_ip) {
    this.lan_ip = lan_ip;
  }

  @Column(name = "lan_port")
  public String getLan_port() {
    return lan_port;
  }

  public void setLan_port(String lan_port) {
    this.lan_port = lan_port;
  }

  @Column(name = "username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Column(name = "password")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Column(name = "domain_username")
  public String getDomain_username() {
    return domain_username;
  }

  public void setDomain_username(String domain_username) {
    this.domain_username = domain_username;
  }

  @Column(name = "domain_password")
  public String getDomain_password() {
    return domain_password;
  }

  public void setDomain_password(String domain_password) {
    this.domain_password = domain_password;
  }

  @Column(name = "status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Column(name = "remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Column(name = "external_address")
  public String getExternal_address() {
    return external_address;
  }

  public void setExternal_address(String external_address) {
    this.external_address = external_address;
  }


  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}

