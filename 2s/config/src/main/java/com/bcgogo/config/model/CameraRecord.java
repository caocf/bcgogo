package com.bcgogo.config.model;

import com.bcgogo.camera.CameraRecordDTO;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.DateUtil;
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
@Table(name = "camera_record")
public class CameraRecord extends LongIdentifier {
  private Long camera_id;
  private String vehicle_no;
  private Long arrive_date;
  private String ref_order_type;
  private Long order_id;
  private String name;
  private Long shop_id;

  @Column(name = "shop_id")
  public Long getShop_id() {
    return shop_id;
  }

  public void setShop_id(Long shop_id) {
    this.shop_id = shop_id;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "camera_id")
  public Long getCamera_id() {
    return camera_id;
  }

  public void setCamera_id(Long camera_id) {
    this.camera_id = camera_id;
  }

  @Column(name = "vehicle_no")
  public String getVehicle_no() {
    return vehicle_no;
  }

  public void setVehicle_no(String vehicle_no) {
    this.vehicle_no = vehicle_no;
  }

  @Column(name = "arrive_date")
  public Long getArrive_date() {
    return arrive_date;
  }

  public void setArrive_date(Long arrive_date) {
    this.arrive_date = arrive_date;
  }

  @Column(name = "ref_order_type")
  public String getRef_order_type() {
    return ref_order_type;
  }

  public void setRef_order_type(String ref_order_type) {
    this.ref_order_type = ref_order_type;
  }

  @Column(name = "order_id")
  public Long getOrder_id() {
    return order_id;
  }

  public void setOrder_id(Long order_id) {
    this.order_id = order_id;
  }

  public CameraRecord fromCameraRecordDTO(CameraRecordDTO cameraRecordDTO) throws ParseException {
    this.setId(NumberUtil.longValue(cameraRecordDTO.getId()));
    this.setVehicle_no(cameraRecordDTO.getVehicle_no());
    this.setArrive_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, cameraRecordDTO.getArrive_date()));
    this.setCamera_id(NumberUtil.longValue(cameraRecordDTO.getCamera_id()));
    this.setName(cameraRecordDTO.getName());
    this.setOrder_id(NumberUtil.longValue(cameraRecordDTO.getOrder_id()));
    this.setRef_order_type(cameraRecordDTO.getRef_order_type());
    this.setOrder_id(NumberUtil.longValue(cameraRecordDTO.getOrder_id()));
    this.setShop_id(NumberUtil.longValue(cameraRecordDTO.getShop_id()));
    return this;
  }

  public CameraRecordDTO toCameraRecordDTO(){
    CameraRecordDTO cameraRecordDTO = new CameraRecordDTO();
    cameraRecordDTO.setId(this.getId().toString());
    cameraRecordDTO.setName(this.getName());
    cameraRecordDTO.setShop_id(this.getShop_id().toString());
    cameraRecordDTO.setOrder_id(this.getOrder_id());
    cameraRecordDTO.setCamera_id(this.getCamera_id().toString());
    cameraRecordDTO.setVehicle_no(this.getVehicle_no());
    cameraRecordDTO.setArrive_date(formatter.format(this.getArrive_date()));
    cameraRecordDTO.setRef_order_type(this.getRef_order_type());
    if(this.getOrder_id()!=null){
      cameraRecordDTO.setOrder_idStr(this.getOrder_id().toString());
    }else{
      cameraRecordDTO.setOrder_idStr("0");
    }
    return cameraRecordDTO;
  }

  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

}

