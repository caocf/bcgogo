package com.bcgogo.pojox.juhe;

/**
 * User: ZhangJuntao
 * Date: 13-10-23
 * Time: 下午1:53
 */
public class ViolateRegulationCitySearchConditionCity {
  private String city_name; //城市名称
  private String city_code;//城市代码
  private int engine;  //是否需要发动机号0,不需要 1,需要
  private int engineno; //需要几位发动机号0,全部 1-9 ,需要发动机号后N位
  private int classa;  //是否需要车架号0,不需要 1,需要
  private int classno; //需要几位车架号0,全部 1-9 需要车架号后N位
  private int regist;  // 是否需要登记证书号0,不需要 1,需要
  private int registno; //需要几位登记证书0,全部 1-9 需要登记证书后N位

  public String getCity_name() {
    return city_name;
  }

  public void setCity_name(String city_name) {
    this.city_name = city_name;
  }

  public String getCity_code() {
    return city_code;
  }

  public void setCity_code(String city_code) {
    this.city_code = city_code;
  }

  public int getEngine() {
    return engine;
  }

  public void setEngine(int engine) {
    this.engine = engine;
  }

  public int getEngineno() {
    return engineno;
  }

  public void setEngineno(int engineno) {
    this.engineno = engineno;
  }

  public int getClassa() {
    return classa;
  }

  public void setClassa(int classa) {
    this.classa = classa;
  }

  public int getClassno() {
    return classno;
  }

  public void setClassno(int classno) {
    this.classno = classno;
  }

  public int getRegist() {
    return regist;
  }

  public void setRegist(int regist) {
    this.regist = regist;
  }

  public int getRegistno() {
    return registno;
  }

  public void setRegistno(int registno) {
    this.registno = registno;
  }
}
