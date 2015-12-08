package com.bcgogo.config.model;

import com.bcgogo.config.dto.juhe.VehicleViolateRegulationQueryRecordDTO;
import com.bcgogo.enums.app.VRegulationRecordQueryType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * 车辆违章查询记录
 * User: lw
 * Date: 14-4-8
 * Time: 下午2:37
 */
@Entity
@Table(name = "vehicle_violate_regulation_query_record")
public class VehicleViolateRegulationQueryRecord extends LongIdentifier {

  private String city;
  private String vehicleNo;
  private Long queryDate;

  private String resultCode;
  private String reason;

  private VRegulationRecordQueryType queryType;

  @Column(name = "query_type")
  @Enumerated(EnumType.STRING)
  public VRegulationRecordQueryType getQueryType() {
    return queryType;
  }

  public void setQueryType(VRegulationRecordQueryType queryType) {
    this.queryType = queryType;
  }

  @Column(name = "city")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  @Column(name = "query_date")
  public Long getQueryDate() {
    return queryDate;
  }

  public void setQueryDate(Long queryDate) {
    this.queryDate = queryDate;
  }

  @Column(name = "reason")
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  @Column(name = "result_record")
  public String getResultCode() {
    return resultCode;
  }

  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public VehicleViolateRegulationQueryRecordDTO toDTO() {
    VehicleViolateRegulationQueryRecordDTO dto = new VehicleViolateRegulationQueryRecordDTO();
    dto.setId(getId());
    dto.setCity(getCity());
    dto.setVehicleNo(getVehicleNo());
    dto.setQueryDate(getQueryDate());
    dto.setReason(getReason());
    dto.setResultCode(getResultCode());
    dto.setQueryType(getQueryType());
    return dto;
  }


  public VehicleViolateRegulationQueryRecord(VehicleViolateRegulationQueryRecordDTO dto) {
    setId(dto.getId());
    setCity(dto.getCity());
    setVehicleNo(dto.getVehicleNo());
    setQueryDate(dto.getQueryDate());
    setReason(dto.getReason());
    setResultCode(dto.getResultCode());
    setQueryType(dto.getQueryType());
  }

  public VehicleViolateRegulationQueryRecord(){

  }

}
