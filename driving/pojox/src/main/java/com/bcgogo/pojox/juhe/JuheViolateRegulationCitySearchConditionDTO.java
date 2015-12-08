package com.bcgogo.pojox.juhe;

import com.bcgogo.pojox.enums.config.JuheStatus;

/**
 * User: ZhangJuntao
 * Date: 13-12-16
 * Time: 下午1:49
 */
public class JuheViolateRegulationCitySearchConditionDTO {
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

  public String getProvinceName() {
    return provinceName;
  }

  public void setProvinceName(String provinceName) {
    this.provinceName = provinceName;
  }

  public String getProvinceCode() {
    return provinceCode;
  }

  public void setProvinceCode(String provinceCode) {
    this.provinceCode = provinceCode;
  }

  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public String getCityCode() {
    return cityCode;
  }

  public void setCityCode(String cityCode) {
    this.cityCode = cityCode;
  }

  public int getEngine() {
    return engine;
  }

  public void setEngine(int engine) {
    this.engine = engine;
  }

  public int getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(int engineNo) {
    this.engineNo = engineNo;
  }

  public int getClassa() {
    return classa;
  }

  public void setClassa(int classa) {
    this.classa = classa;
  }

  public int getClassNo() {
    return classNo;
  }

  public void setClassNo(int classNo) {
    this.classNo = classNo;
  }

  public int getRegist() {
    return regist;
  }

  public void setRegist(int regist) {
    this.regist = regist;
  }

  public int getRegistNo() {
    return registNo;
  }

  public void setRegistNo(int registNo) {
    this.registNo = registNo;
  }

  public JuheStatus getStatus() {
    return status;
  }

  public void setStatus(JuheStatus status) {
    this.status = status;
  }
}
