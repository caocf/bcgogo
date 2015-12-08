package com.bcgogo.driving.model;


import com.bcgogo.driving.model.base.LongIdentifier;
import com.bcgogo.pojox.enums.config.JuheStatus;
import com.bcgogo.pojox.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.pojox.juhe.ViolateRegulationCitySearchConditionCity;
import com.bcgogo.pojox.juhe.ViolateRegulationCitySearchConditionDTO;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-10-22
 * Time: 下午5:55
 */
@Entity
@Table(name = "juhe_violate_regulation_city_search_condition")
public class JuheViolateRegulationCitySearchCondition extends LongIdentifier {
  private String provinceName; //省份名称
  private String provinceCode; //省份代码
  private String cityName; //城市名称
  private String cityCode;//城市代码
  private int engine;  //是否需要发动机号0,不需要 1,需要
  private int engineNo; //需要几位发动机号0,全部 1-9 ,需要发动机号后N位
  private int classa;  //是否需要车架号0,不需要 1,需要
  private int classNo; //需要几位车架号0,全部 1-9 需要车架号后N位
  private int regist;  // 是否需要登记证书号0,不需要 1,需要
  private int registNo; //需要几位登记证书0,全部 1-9 需要登记证书后N位
  private JuheStatus status = JuheStatus.ACTIVE;

  public JuheViolateRegulationCitySearchCondition() {
  }

  public JuheViolateRegulationCitySearchCondition(ViolateRegulationCitySearchConditionCity city, String provinceCode, String provinceName) {
    this.provinceName = provinceName;
    this.provinceCode = provinceCode;
    this.setCityCode(city.getCity_code());
    this.setCityName(city.getCity_name());
    this.setEngine(city.getEngine());
    this.setEngineNo(city.getEngineno());
    this.setClassa(city.getClassa());
    this.setClassNo(city.getClassno());
    this.setRegist(city.getRegist());
    this.setRegistNo(city.getRegistno());
  }

  public JuheViolateRegulationCitySearchConditionDTO toDTO() {
    JuheViolateRegulationCitySearchConditionDTO dto = new JuheViolateRegulationCitySearchConditionDTO();
    dto.setProvinceCode(getProvinceCode());
    dto.setProvinceName(getProvinceName());
    dto.setCityCode(getCityCode());
    dto.setCityName(getCityName());
    dto.setStatus(getStatus());
    dto.setEngine(getEngine());
    dto.setEngineNo(getEngineNo());
    dto.setClassa(getClassa());
    dto.setClassNo(getClassNo());
    dto.setRegist(getRegist());
    dto.setRegistNo(getRegistNo());
    return dto;
  }

  public ViolateRegulationCitySearchConditionDTO toViolateRegulationCitySearchConditionDTO() {
    ViolateRegulationCitySearchConditionDTO dto = new ViolateRegulationCitySearchConditionDTO();
    dto.setProvinceCode(getProvinceCode());
    dto.setProvinceName(getProvinceName());
    dto.setCityCode(getCityCode());
    dto.setCityName(getCityName());
    dto.setStatus(getStatus());
    dto.getEngine().setValueNum(getEngineNo());
    dto.getClassa().buildValueNum(getClassNo()).buildNecessary(getClassa());
    dto.getEngine().buildValueNum(getEngineNo()).buildNecessary(getEngine());
    dto.getRegist().buildValueNum(getRegistNo()).buildNecessary(getRegist());
    return dto;
  }

  @Column(name = "province_name")
  public String getProvinceName() {
    return provinceName;
  }

  public void setProvinceName(String provinceName) {
    this.provinceName = provinceName;
  }

  @Column(name = "province_code")
  public String getProvinceCode() {
    return provinceCode;
  }

  public void setProvinceCode(String provinceCode) {
    this.provinceCode = provinceCode;
  }

  @Column(name = "city_name")
  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  @Column(name = "city_code")
  public String getCityCode() {
    return cityCode;
  }

  public void setCityCode(String cityCode) {
    this.cityCode = cityCode;
  }

  @Column(name = "engine")
  public int getEngine() {
    return engine;
  }

  public void setEngine(int engine) {
    this.engine = engine;
  }

  @Column(name = "engine_no")
  public int getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(int engineNo) {
    this.engineNo = engineNo;
  }

  @Column(name = "classa")
  public int getClassa() {
    return classa;
  }

  public void setClassa(int classa) {
    this.classa = classa;
  }

  @Column(name = "class_no")
  public int getClassNo() {
    return classNo;
  }

  public void setClassNo(int classNo) {
    this.classNo = classNo;
  }

  @Column(name = "regist")
  public int getRegist() {
    return regist;
  }

  public void setRegist(int regist) {
    this.regist = regist;
  }

  @Column(name = "regist_no")
  public int getRegistNo() {
    return registNo;
  }

  public void setRegistNo(int registNo) {
    this.registNo = registNo;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public JuheStatus getStatus() {
    return status;
  }

  public void setStatus(JuheStatus status) {
    this.status = status;
  }
}
