package com.bcgogo.pojox.juhe;


import com.bcgogo.pojox.enums.VRegulationRecordQueryType;

/**
 * User: lw
 * Date: 14-4-8
 * Time: 下午2:54
 */
public class VehicleViolateRegulationQueryRecordDTO {

  private Long id;
  private String city;
  private String vehicleNo;
  private Long queryDate;

  private String resultCode;
  private String reason;
  private VRegulationRecordQueryType queryType;


  public VehicleViolateRegulationQueryRecordDTO(){}

  public VehicleViolateRegulationQueryRecordDTO(VehicleViolateRegulationCityQueryResponse response, String city, String vehicleNo, Long queryDate,VRegulationRecordQueryType queryType) {
    this.setResultCode(response.getResultcode());
    this.setReason(response.getReason());

    this.setQueryDate(queryDate);
    this.setCity(city);
    this.setVehicleNo(vehicleNo);
    this.setQueryType(queryType);
  }


  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Long getQueryDate() {
    return queryDate;
  }

  public void setQueryDate(Long queryDate) {
    this.queryDate = queryDate;
  }

  public String getResultCode() {
    return resultCode;
  }

  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public VRegulationRecordQueryType getQueryType() {
    return queryType;
  }

  public void setQueryType(VRegulationRecordQueryType queryType) {
    this.queryType = queryType;
  }
}
