package com.bcgogo.config.model;

import com.bcgogo.camera.CameraConfigDTO;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjie
 * Date: 14-12-25
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "camera_config")
public class CameraConfig extends LongIdentifier {

  private Long interval_time_warn;
  private String white_vehicle_nos;
  private String order_type;
  private Long camera_id;
  private Long shop_id;
  private String member_card;
  private String construction_project_value;
  private String construction_project_text;
  //客户端打印机序列号
  private String printer_serial_no;

  public CameraConfigDTO toCameraConfigDTO(){
    CameraConfigDTO cameraConfigDTO = new CameraConfigDTO();
    cameraConfigDTO.setId(this.getId().toString());
    cameraConfigDTO.setInterval_time_warn(this.getInterval_time_warn().toString());
    cameraConfigDTO.setWhite_vehicle_nos(this.getWhite_vehicle_nos());
    cameraConfigDTO.setOrder_type(this.getOrder_type());
    cameraConfigDTO.setMember_card(this.getMember_card());
    cameraConfigDTO.setCamera_id(this.getCamera_id().toString());
    cameraConfigDTO.setShop_id(this.getShop_id().toString());
    cameraConfigDTO.setConstruction_project_value(this.getConstruction_project_value());
    cameraConfigDTO.setConstruction_project_text(this.getConstruction_project_text());
    cameraConfigDTO.setPrinter_serial_no(this.getPrinter_serial_no());
    return cameraConfigDTO;
  }

  public CameraConfig fromCameraConfigDTO(CameraConfigDTO cameraConfigDTO) throws ParseException {
    this.setId(NumberUtil.longValue(cameraConfigDTO.getId()));
    this.setInterval_time_warn(NumberUtil.longValue(cameraConfigDTO.getInterval_time_warn()));
    this.setWhite_vehicle_nos(cameraConfigDTO.getWhite_vehicle_nos());
    this.setOrder_type(cameraConfigDTO.getOrder_type());
    this.setMember_card(cameraConfigDTO.getMember_card());
    this.setCamera_id(NumberUtil.longValue(cameraConfigDTO.getCamera_id()));
    this.setConstruction_project_text(cameraConfigDTO.getConstruction_project_text());
    this.setConstruction_project_value(cameraConfigDTO.getConstruction_project_value());
    this.setPrinter_serial_no(cameraConfigDTO.getPrinter_serial_no());
    this.setShop_id(-1L);
    return this;
  }

  @Column(name = "construction_project_value")
  public String getConstruction_project_value() {
    return construction_project_value;
  }

  public void setConstruction_project_value(String construction_project_value) {
    this.construction_project_value = construction_project_value;
  }

  @Column(name = "construction_project_text")
  public String getConstruction_project_text() {
    return construction_project_text;
  }

  public void setConstruction_project_text(String construction_project_text) {
    this.construction_project_text = construction_project_text;
  }

  @Column(name = "interval_time_warn")
  public Long getInterval_time_warn() {
    return interval_time_warn;
  }

  public void setInterval_time_warn(Long interval_time_warn) {
    this.interval_time_warn = interval_time_warn;
  }

  @Column(name = "white_vehicle_nos")
  public String getWhite_vehicle_nos() {
    return white_vehicle_nos;
  }

  public void setWhite_vehicle_nos(String white_vehicle_nos) {
    this.white_vehicle_nos = white_vehicle_nos;
  }

  @Column(name = "order_type")
  public String getOrder_type() {
    return order_type;
  }

  public void setOrder_type(String order_type) {
    this.order_type = order_type;
  }

  @Column(name = "member_card")
  public String getMember_card() {
    return member_card;
  }

  public void setMember_card(String member_card) {
    this.member_card = member_card;
  }

  @Column(name = "camera_id")
  public Long getCamera_id() {
    return camera_id;
  }

  public void setCamera_id(Long camera_id) {
    this.camera_id = camera_id;
  }

  @Column(name = "shop_id")
  public Long getShop_id() {
    return shop_id;
  }

  public void setShop_id(Long shop_id) {
    this.shop_id = shop_id;
  }

  @Column(name = "printer_serial_no")
  public String getPrinter_serial_no() {
    return printer_serial_no;
  }

  public void setPrinter_serial_no(String printer_serial_no) {
    this.printer_serial_no = printer_serial_no;
  }

  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}

