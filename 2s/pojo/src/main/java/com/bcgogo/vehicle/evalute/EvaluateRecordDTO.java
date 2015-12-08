package com.bcgogo.vehicle.evalute;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.vehicle.evalute.car360.Car360EvaluateCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-17
 * Time: 下午5:58
 */
public class EvaluateRecordDTO {
  private static final Logger LOG = LoggerFactory.getLogger(EvaluateRecordDTO.class);

  private Long id;
  private String openId;
   /************************评估信息**********************/
  private String vehicleNo;
  private Long evalDate;
  private Double evalPrice;
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


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Double getEvalPrice() {
    return evalPrice;
  }

  public void setEvalPrice(Double evalPrice) {
    this.evalPrice = evalPrice;
  }

  public Double getLowPrice() {
    return lowPrice;
  }

  public void setLowPrice(Double lowPrice) {
    this.lowPrice = lowPrice;
  }

  public Double getGoodPrice() {
    return goodPrice;
  }

  public void setGoodPrice(Double goodPrice) {
    this.goodPrice = goodPrice;
  }

  public Double getHighPrice() {
    return highPrice;
  }

  public void setHighPrice(Double highPrice) {
    this.highPrice = highPrice;
  }

  public Long getEvalDate() {
    return evalDate;
  }

  public void setEvalDate(Long evalDate) {
    this.evalDate = evalDate;
  }

  public EvaluateDataSource getSource() {
    return source;
  }

  public void setSource(EvaluateDataSource source) {
    this.source = source;
  }

  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Long getRegDate() {
    return regDate;
  }

  public void setRegDate(Long regDate) {
    this.regDate = regDate;
  }

  public String getMile() {
    return mile;
  }

  public void setMile(String mile) {
    this.mile = mile;
  }

  public String getAreaId() {
    return areaId;
  }

  public void setAreaId(String areaId) {
    this.areaId = areaId;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
