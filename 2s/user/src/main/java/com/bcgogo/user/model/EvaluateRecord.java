package com.bcgogo.user.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.vehicle.evalute.EvaluateDataSource;
import com.bcgogo.vehicle.evalute.EvaluateRecordDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-17
 * Time: 下午3:35
 */
@Entity
@Table(name = "evaluate_record")
public class EvaluateRecord extends LongIdentifier {
  private String openId;
  /************************评估信息**********************/
  private String vehicleNo;
  private Double evalPrice;
  private Long evalDate;
  private Double lowPrice; //车况一般的估值
  private Double goodPrice; //车况良好的估值
  private Double highPrice; //车况优秀的估值
  private EvaluateDataSource source;
  /************************评估车辆信息**********************/
  private String modelId;//车型
  private String model;
  private Long regDate; //待估车辆的上牌时间（格式：yyyy-MM）
  private String mile;  //待估车辆的公里数，单位万公里。
  private String areaId;//城市标识
  private String area;
  private DeletedType deleted=DeletedType.FALSE;

  public EvaluateRecordDTO toDTO(){
    EvaluateRecordDTO recordDTO=new EvaluateRecordDTO();
    recordDTO.setId(getId());
    recordDTO.setVehicleNo(getVehicleNo());
    recordDTO.setEvalPrice(getEvalPrice());
    recordDTO.setEvalDate(getEvalDate());
    recordDTO.setLowPrice(getLowPrice());
    recordDTO.setGoodPrice(getGoodPrice());
    recordDTO.setHighPrice(getHighPrice());
    recordDTO.setSource(getSource());
    recordDTO.setModelId(getModelId());
    recordDTO.setModel(getModel());
    recordDTO.setRegDate(getRegDate());
    recordDTO.setMile(getMile());
    recordDTO.setAreaId(getAreaId());
    recordDTO.setArea(getArea());
    recordDTO.setDeleted(getDeleted());
    return recordDTO;
  }

  public void fromDTO(EvaluateRecordDTO recordDTO){
    this.setId(recordDTO.getId());
    this.setOpenId(recordDTO.getOpenId());
    this.setVehicleNo(recordDTO.getVehicleNo());
    this.setEvalPrice(recordDTO.getEvalPrice());
    this.setEvalDate(recordDTO.getEvalDate());
    this.setLowPrice(recordDTO.getLowPrice());
    this.setGoodPrice(recordDTO.getGoodPrice());
    this.setHighPrice(recordDTO.getHighPrice());
    this.setSource(recordDTO.getSource());
    this.setModelId(recordDTO.getModelId());
    this.setModel(recordDTO.getModel());
    this.setRegDate(recordDTO.getRegDate());
    this.setMile(recordDTO.getMile());
    this.setAreaId(recordDTO.getAreaId());
    this.setArea(recordDTO.getArea());
    this.setDeleted(recordDTO.getDeleted());
  }

  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }


  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  @Column(name = "eval_date")
  public Long getEvalDate() {
    return evalDate;
  }

  public void setEvalDate(Long evalDate) {
    this.evalDate = evalDate;
  }

  @Column(name = "eval_price")
  public Double getEvalPrice() {
    return evalPrice;
  }

  public void setEvalPrice(Double evalPrice) {
    this.evalPrice = evalPrice;
  }

  @Column(name = "low_price")
  public Double getLowPrice() {
    return lowPrice;
  }

  public void setLowPrice(Double lowPrice) {
    this.lowPrice = lowPrice;
  }

  @Column(name = "good_price")
  public Double getGoodPrice() {
    return goodPrice;
  }

  public void setGoodPrice(Double goodPrice) {
    this.goodPrice = goodPrice;
  }

  @Column(name = "high_price")
  public Double getHighPrice() {
    return highPrice;
  }

  public void setHighPrice(Double highPrice) {
    this.highPrice = highPrice;
  }

  @Column(name = "source")
  @Enumerated(EnumType.STRING)
  public EvaluateDataSource getSource() {
    return source;
  }

  public void setSource(EvaluateDataSource source) {
    this.source = source;
  }

  @Column(name = "model_id")
  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "reg_date")
  public Long getRegDate() {
    return regDate;
  }

  public void setRegDate(Long regDate) {
    this.regDate = regDate;
  }

  @Column(name = "mile")
  public String getMile() {
    return mile;
  }

  public void setMile(String mile) {
    this.mile = mile;
  }

  @Column(name = "area_id")
  public String getAreaId() {
    return areaId;
  }

  public void setAreaId(String areaId) {
    this.areaId = areaId;
  }

  @Column(name = "area")
  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
