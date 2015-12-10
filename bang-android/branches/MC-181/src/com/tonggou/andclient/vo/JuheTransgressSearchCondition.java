package com.tonggou.andclient.vo;

import java.io.Serializable;

public class JuheTransgressSearchCondition implements Serializable {

	private static final long serialVersionUID = 6354739877014307709L;

	private String provinceName;	// 省
	private String provinceCode;	// 省编号
	private String cityName; 		// 城市名称
	private String cityCode; 		// 城市编码
	private int engine; 			// 是否需要发动机号 0 不需要
	private int engineno; 			// 发动机号从后数的位数
	private int classa; 			// 是否需要车架号 0 不需要
	private int classno; 			// 车架号从后数的位数
	private int regist; 			// 是否需要登记证书号 0 不需要
	private int registno;			// 登记证书从后数的位数
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
