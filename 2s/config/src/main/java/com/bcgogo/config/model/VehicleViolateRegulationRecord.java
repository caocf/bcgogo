package com.bcgogo.config.model;

import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.enums.app.VRegulationRecordQueryType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

/**
 * 车辆违章查询记录
 * User: lw
 * Date: 14-4-8
 * Time: 下午2:37
 */
@Entity
@Table(name = "vehicle_violate_regulation_record")
public class VehicleViolateRegulationRecord extends LongIdentifier {

  private String city;
  private String vehicleNo;
  private Long recordDate;

  private String date;
  private String area;
  private String act;
  private String code;
  private String fen;
  private String money;
  private String handled;

  private VRegulationRecordQueryType queryType;

  public VehicleViolateRegulationRecordDTO toDTO() {
    VehicleViolateRegulationRecordDTO dto = new VehicleViolateRegulationRecordDTO();
    dto.setId(getId());
    dto.setCity(getCity());
    dto.setVehicleNo(getVehicleNo());
    dto.setRecordDate(getRecordDate());
    dto.setDate(getDate());
    dto.setArea(getArea());
    dto.setAct(getAct());
    dto.setCode(getCode());
    dto.setFen(getFen());
    dto.setMoney(getMoney());
    dto.setHandled(getHandled());
    dto.setQueryType(getQueryType());
    return dto;
  }

  public void fromDTO(VehicleViolateRegulationRecordDTO recordDTO) {
     try {
      String dateStr = recordDTO.getDate();
      String result = dateStr;
      if (StringUtil.isNotEmpty(dateStr)) {
        Long date = DateUtil.convertDateStringToDateLong("yyyy-MM-ddHH:mm:ss", dateStr);
        result = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, date);
      }
      if (StringUtil.isNotEmpty(result)) {
        this.setDate(result);
      } else {
        this.setDate(recordDTO.getDate());
      }

    } catch (Exception e) {
      this.setDate(recordDTO.getDate());
    }
     this.setId(recordDTO.getId());
    this.setCity(recordDTO.getCity());
    this.setVehicleNo(recordDTO.getVehicleNo());
    this.setRecordDate(recordDTO.getRecordDate());
    this.setArea(recordDTO.getArea());
    this.setAct(recordDTO.getAct());
    this.setCode(recordDTO.getCode());
    this.setFen(recordDTO.getFen());
    this.setMoney(recordDTO.getMoney());
    this.setHandled(recordDTO.getHandled());
    this.setQueryType(recordDTO.getQueryType());
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

  @Column(name = "record_date")
  public Long getRecordDate() {
    return recordDate;
  }

  public void setRecordDate(Long recordDate) {
    this.recordDate = recordDate;
  }

  @Column(name = "date")
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  @Column(name = "area")
  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  @Column(name = "act")
  public String getAct() {
    return act;
  }

  public void setAct(String act) {
    this.act = act;
  }

  @Column(name = "code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(name = "fen")
  public String getFen() {
    return fen;
  }

  public void setFen(String fen) {
    this.fen = fen;
  }

  @Column(name = "money")
  public String getMoney() {
    return money;
  }

  public void setMoney(String money) {
    this.money = money;
  }

  @Column(name = "handled")
  public String getHandled() {
    return handled;
  }

  public void setHandled(String handled) {
    this.handled = handled;
  }

  @Column(name = "query_type")
  @Enumerated(EnumType.STRING)
  public VRegulationRecordQueryType getQueryType() {
    return queryType;
  }

  public void setQueryType(VRegulationRecordQueryType queryType) {
    this.queryType = queryType;
  }



}
