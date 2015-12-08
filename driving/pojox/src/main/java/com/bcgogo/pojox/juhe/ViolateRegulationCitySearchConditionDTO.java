package com.bcgogo.pojox.juhe;

import com.bcgogo.pojox.ValidateMsg;
import com.bcgogo.pojox.api.AppVehicleDTO;
import com.bcgogo.pojox.enums.config.JuheStatus;
import com.bcgogo.pojox.util.StringUtil;

/**
 * User: ZhangJuntao
 * Date: 13-11-8
 * Time: 上午10:10
 */
public class ViolateRegulationCitySearchConditionDTO {
  private String provinceName; //省份名称
  private String provinceCode; //省份代码
  private String cityName;      //城市名称
  private String cityCode;      //城市代码
  private boolean success = true;
  private String message = "";
  private JuheStatus status;
  private ViolateRegulationCitySearchConditionElement engine = new ViolateRegulationCitySearchConditionElement("engine");
  private ViolateRegulationCitySearchConditionElement classa = new ViolateRegulationCitySearchConditionElement("class");
  private ViolateRegulationCitySearchConditionElement regist = new ViolateRegulationCitySearchConditionElement("regist");

  //需要几位发动机号0,全部
  //1-9 需要发动机号后N位
  public void setEngineValue(String engine) {
    if (getEngine().isNecessary() && StringUtil.isEmpty(engine)) {
      getEngine().setSuccess(false);
      getEngine().setMessage(ValidateMsg.ENGINE_EMPTY.getValue());
      setSuccess(false);
    }
    getEngine().buildOrgValue(engine);
  }

  //需要几位登记证书0,全部
  //1-9 需要登记证书后N位
  public void setRegistValue(String regist) {
    if (getRegist().isNecessary() && StringUtil.isEmpty(regist)) {
      getRegist().setSuccess(false);
      getRegist().setMessage(ValidateMsg.REGIST_EMPTY.getValue());
      setSuccess(false);
    }
    getRegist().buildOrgValue(regist);
  }

  //需要几位车架号0,全部
  //1-9 需要车架号后N位
  public void setClassValue(String classa) {
    if (getClassa().isNecessary() && StringUtil.isEmpty(classa)) {
      getClassa().setSuccess(false);
      getClassa().setMessage(ValidateMsg.CLASS_EMPTY.getValue());
      setSuccess(false);
    }
    getClassa().buildOrgValue(classa);
  }

  public void setAppVehicleInfo(AppVehicleDTO dto) {
    setEngineValue(dto.getEngineNo());
    setClassValue(dto.getVehicleVin());
    setRegistValue(dto.getRegistNo());
  }

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

  public ViolateRegulationCitySearchConditionElement getEngine() {
    return engine;
  }

  public void setEngine(ViolateRegulationCitySearchConditionElement engine) {
    this.engine = engine;
  }

  public ViolateRegulationCitySearchConditionElement getClassa() {
    return classa;
  }

  public void setClassa(ViolateRegulationCitySearchConditionElement classa) {
    this.classa = classa;
  }

  public ViolateRegulationCitySearchConditionElement getRegist() {
    return regist;
  }

  public void setRegist(ViolateRegulationCitySearchConditionElement regist) {
    this.regist = regist;
  }


  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public JuheStatus getStatus() {
    return status;
  }

  public void setStatus(JuheStatus status) {
    this.status = status;
  }

}
