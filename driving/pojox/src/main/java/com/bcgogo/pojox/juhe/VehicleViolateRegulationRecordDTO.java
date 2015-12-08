package com.bcgogo.pojox.juhe;

import com.bcgogo.pojox.enums.VRegulationRecordQueryType;
import com.bcgogo.pojox.util.DateUtil;
import com.bcgogo.pojox.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: lw
 * Date: 14-4-8
 * Time: 下午2:53
 */
public class VehicleViolateRegulationRecordDTO {

  private static final Logger LOG = LoggerFactory.getLogger(VehicleViolateRegulationRecordDTO.class);


  private Long id;
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

  public void formatDate() {
    try {
      if (StringUtil.isNotEmpty(getDate())) {
        Long date = DateUtil.convertDateStringToDateLong("yyyy-MM-ddHH:mm", getDate());
        String result = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, date);
        if (StringUtil.isNotEmpty(result)) {
          this.setDate(result);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public VRegulationRecordQueryType getQueryType() {
    return queryType;
  }

  public void setQueryType(VRegulationRecordQueryType queryType) {
    this.queryType = queryType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Long getRecordDate() {
    return recordDate;
  }

  public void setRecordDate(Long recordDate) {
    this.recordDate = recordDate;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getAct() {
    return act;
  }

  public void setAct(String act) {
    this.act = act;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getFen() {
    return fen;
  }

  public void setFen(String fen) {
    this.fen = fen;
  }

  public String getMoney() {
    return money;
  }

  public void setMoney(String money) {
    this.money = money;
  }

  public String getHandled() {
    return handled;
  }

  public void setHandled(String handled) {
    this.handled = handled;
  }
}
