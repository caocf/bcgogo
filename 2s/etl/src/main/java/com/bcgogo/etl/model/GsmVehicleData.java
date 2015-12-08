package com.bcgogo.etl.model;

import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.etl.model.mongodb.XLongIdentifier;
import com.bcgogo.etl.model.mongodb.XNumberLong;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;

/**
 * obd二代，后视镜上报的原始数据
 * Author: ndong
 * Date: 2015-4-23
 * Time: 17:31
 */
public class GsmVehicleData extends XLongIdentifier implements Serializable {
  private String uuid; //行程识别码
  private String imei;
  private String appUserNo;
  private AppUserType userType;
  private String curMil;  //仪表盘当前里程数(单位：km)
  private String mile;
  private String gpsMile;
  //车门状态，5位数字，分别代表顺序为左前门右前门左后门右后门后备箱。状态值为0，1，2，0是关闭，1是打开，2是未知。 例如，01012：
  private String door;
  //设备上传的采集数据时间。格式150422212030，15年04月22日21时20分30秒
  private XNumberLong uploadTime;
  //数据上传时服务器时间
  private XNumberLong uploadServerTime;
  //数据实时上报状态，0实时，1补报，当数据产生时无网络连接，本地存储下来，下次有网络时上报数据数据标识为1
  private int dttpe;
  //0:点火；1：熄火；2：行驶中 3:熄火后的车况 4碰撞时的车况
  private String vehicleStatus;
  //故障代码 例如：U0021,B0090,C0032,P0006,P0007,P1233,P0008,P0009,U0022
  private String rdtc;

  //定时钟数据处理状态（生成行车轨迹判定状态）
  private GsmVehicleStatus gsmVehicleStatus;

  //定时钟数据处理状态（gps生成城市code判定状态）
  private GsmVehicleStatus gpsCityStatus;

    private String rpf;  //发动机负荷

  /**
   * **********************  车况信息   *************************************
   */
  private String voltageForOxygenSensor;  //进气系统   氧传感器电压 0.255V
  private String throttlePosition;  //排放系统          节气门相对位置 20%

  /**
   * **********************  OBD标准数据流   *************************************
   */
  private String spwr;  //电瓶电压                 12.53V    //电源系统
  private String rpm;  //发动机转速                2000
  private String vss;  //车辆速度                  90km/h
  private String wbtm; //水箱温度                            //冷却系统

  /**
   * **********************  gsp信息   *************************************
   */
  private String lon; //经度
  private String lonDir; //经度标识：E标识东经，W标识西经
  private String lat; //纬度
  private String latDir; //纬度标识,N表示北纬，S南纬
  private String gpsSpeed; //gps速度
  private String gpsHeading; //gps航向
  //定位数据有效值，A：实时定位，V：无效定位，取上一个有效定位数据
  private String gpsDataValidity;

  /**
   * **********************  油耗信息   *************************************
   */
  //本次行程平均油耗。 9L/100KM，参考套线协议，EPID数据。本次采用后台根据行车记录油耗求平均
  private String aOilWear;
  //剩余油量。 10%或者30L 不同车型表示结果不同，或者参考鹏奥达编码规则
  private String rOilMass;
  //剩余油量类别（L或者%）
  private String rOilMassType;
    private Integer bOilWear;  //百公里油耗
  private Integer mOilWear;  //每小时油耗

  //新增 鹏奥达编码规则
  private String gx;  //gsensor x方向值
  private String gy;  //gsensor y方向值
  /**
   * **********************  辅助gps数据   *************************************
   */
  private String country;  //国家代码
  private String supplier; //运营商代码
  private String base_station;//基站号
  private String dist_id;  //小区ID


  public void fromDTO(GsmVehicleDataDTO gsmVehicleDataDTO) {

    this.set_id(gsmVehicleDataDTO.getId());
    this.setUuid(gsmVehicleDataDTO.getUuid());
    this.setImei(gsmVehicleDataDTO.getImei());
    this.setAppUserNo(gsmVehicleDataDTO.getAppUserNo());
    this.setUserType(gsmVehicleDataDTO.getUserType());
    this.setDoor(gsmVehicleDataDTO.getDoor());
    this.setCurMil(gsmVehicleDataDTO.getCurMil());
    this.setMile(gsmVehicleDataDTO.getMile());
    this.setUploadTime(new XNumberLong(gsmVehicleDataDTO.getUploadTime()));
    this.setUploadServerTime(new XNumberLong(gsmVehicleDataDTO.getUploadServerTime()));
    this.setDttpe(gsmVehicleDataDTO.getDttpe());
    this.setSpwr(gsmVehicleDataDTO.getSpwr());
    this.setRpf(gsmVehicleDataDTO.getRpf());
    this.setRpm(gsmVehicleDataDTO.getRpm());
    this.setVss(gsmVehicleDataDTO.getVss());
    this.setLon(gsmVehicleDataDTO.getLon());
    this.setLonDir(gsmVehicleDataDTO.getLonDir());
    this.setLat(gsmVehicleDataDTO.getLat());
    this.setLatDir(gsmVehicleDataDTO.getLatDir());
    this.setGpsSpeed(gsmVehicleDataDTO.getGpsSpeed());
    this.setGpsHeading(gsmVehicleDataDTO.getGpsHeading());
    this.setGpsDataValidity(gsmVehicleDataDTO.getGpsDataValidity());
    this.setaOilWear(gsmVehicleDataDTO.getaOilWear());
    this.setbOilWear(gsmVehicleDataDTO.getbOilWear());
    this.setmOilWear(gsmVehicleDataDTO.getmOilWear());
    this.setrOilMass(gsmVehicleDataDTO.getrOilMass());
    this.setVehicleStatus(gsmVehicleDataDTO.getVehicleStatus());
    this.setRdtc(gsmVehicleDataDTO.getRdtc());
    this.setGsmVehicleStatus(gsmVehicleDataDTO.getGsmVehicleStatus());
    this.setVoltageForOxygenSensor(gsmVehicleDataDTO.getVoltageForOxygenSensor());
    this.setThrottlePosition(gsmVehicleDataDTO.getThrottlePosition());
    this.setGx(gsmVehicleDataDTO.getGx());
    this.setGy(gsmVehicleDataDTO.getGy());
    this.setGz(gsmVehicleDataDTO.getGz());
    this.setWbtm(gsmVehicleDataDTO.getWbtm());
    this.setGpsCityStatus(GsmVehicleStatus.UN_HANDLE);
    this.setrOilMassType(gsmVehicleDataDTO.getrOilMassType());
    this.setCountry(gsmVehicleDataDTO.getCountry());
    this.setSupplier(gsmVehicleDataDTO.getSupplier());
    this.setBase_station(gsmVehicleDataDTO.getBase_station());
    this.setDist_id(gsmVehicleDataDTO.getDist_id());
  }

  public GsmVehicleDataDTO toDTO() {
    GsmVehicleDataDTO gsmVehicleDataDTO = new GsmVehicleDataDTO();
    gsmVehicleDataDTO.setId(this.get_id().get$oid().toString());
    gsmVehicleDataDTO.setUuid(this.getUuid());
    gsmVehicleDataDTO.setImei(this.getImei());
    gsmVehicleDataDTO.setAppUserNo(this.getAppUserNo());
    gsmVehicleDataDTO.setUserType(getUserType());
    gsmVehicleDataDTO.setDoor(this.getDoor());
    gsmVehicleDataDTO.setCurMil(this.getCurMil());
    gsmVehicleDataDTO.setMile(this.getMile());
    gsmVehicleDataDTO.setUploadTime(NumberUtil.longValue((this.getUploadTime().get$numberLong())));
    gsmVehicleDataDTO.setUploadServerTime(NumberUtil.longValue(this.getUploadServerTime().get$numberLong()));
    gsmVehicleDataDTO.setDttpe(this.getDttpe());
    gsmVehicleDataDTO.setSpwr(String.valueOf(NumberUtil.round(this.getSpwr())));
    gsmVehicleDataDTO.setVoltageForOxygenSensor(String.valueOf(NumberUtil.round(this.getVoltageForOxygenSensor())));
    gsmVehicleDataDTO.setRpf(getRpf());
    gsmVehicleDataDTO.setRpm(this.getRpm());
    gsmVehicleDataDTO.setVss(this.getVss());
    gsmVehicleDataDTO.setLon(this.getLon());
    gsmVehicleDataDTO.setLonDir(this.getLonDir());
    gsmVehicleDataDTO.setLat(this.getLat());
    gsmVehicleDataDTO.setLatDir(this.getLatDir());
    gsmVehicleDataDTO.setGpsSpeed(this.getGpsSpeed());
    gsmVehicleDataDTO.setGpsHeading(this.getGpsHeading());
    gsmVehicleDataDTO.setGpsDataValidity(this.getGpsDataValidity());
    gsmVehicleDataDTO.setaOilWear(this.getaOilWear());
    gsmVehicleDataDTO.setrOilMass(this.getrOilMass());
    gsmVehicleDataDTO.setbOilWear(this.getbOilWear());
    gsmVehicleDataDTO.setmOilWear(this.getmOilWear());
    gsmVehicleDataDTO.setVehicleStatus(this.getVehicleStatus());
    gsmVehicleDataDTO.setRdtc(this.getRdtc());
    gsmVehicleDataDTO.setGsmVehicleStatus(this.getGsmVehicleStatus());
    gsmVehicleDataDTO.setThrottlePosition(this.getThrottlePosition());
    gsmVehicleDataDTO.setGx(this.getGx());
    gsmVehicleDataDTO.setGy(this.getGy());
    gsmVehicleDataDTO.setGz(this.getGz());
    gsmVehicleDataDTO.setWbtm(this.getWbtm());
    gsmVehicleDataDTO.setrOilMassType(this.getrOilMassType());
    gsmVehicleDataDTO.setCountry(this.getCountry());
    gsmVehicleDataDTO.setSupplier(this.getSupplier());
    gsmVehicleDataDTO.setBase_station(this.getBase_station());
    gsmVehicleDataDTO.setDist_id(this.getDist_id());
    return gsmVehicleDataDTO;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public AppUserType getUserType() {
    return userType;
  }

  public void setUserType(AppUserType userType) {
    this.userType = userType;
  }

  public String getCurMil() {
    return curMil;
  }

  public void setCurMil(String curMil) {
    this.curMil = curMil;
  }

  public String getMile() {
    return mile;
  }

  public void setMile(String mile) {
    this.mile = mile;
  }

  public String getGpsMile() {
    return gpsMile;
  }

  public void setGpsMile(String gpsMile) {
    this.gpsMile = gpsMile;
  }

  public String getDoor() {
    return door;
  }

  public void setDoor(String door) {
    this.door = door;
  }

  public XNumberLong getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(XNumberLong uploadTime) {
    this.uploadTime = uploadTime;
  }

  public XNumberLong getUploadServerTime() {
    return uploadServerTime;
  }

  public void setUploadServerTime(XNumberLong uploadServerTime) {
    this.uploadServerTime = uploadServerTime;
  }

  public int getDttpe() {
    return dttpe;
  }

  public void setDttpe(int dttpe) {
    this.dttpe = dttpe;
  }

  public String getVehicleStatus() {
    return vehicleStatus;
  }

  public void setVehicleStatus(String vehicleStatus) {
    this.vehicleStatus = vehicleStatus;
  }

  public String getRdtc() {
    return rdtc;
  }

  public void setRdtc(String rdtc) {
    this.rdtc = rdtc;
  }

  public GsmVehicleStatus getGsmVehicleStatus() {
    return gsmVehicleStatus;
  }

  public void setGsmVehicleStatus(GsmVehicleStatus gsmVehicleStatus) {
    this.gsmVehicleStatus = gsmVehicleStatus;
  }

  public GsmVehicleStatus getGpsCityStatus() {
    return gpsCityStatus;
  }

  public void setGpsCityStatus(GsmVehicleStatus gpsCityStatus) {
    this.gpsCityStatus = gpsCityStatus;
  }

  public String getVoltageForOxygenSensor() {
    return voltageForOxygenSensor;
  }

  public void setVoltageForOxygenSensor(String voltageForOxygenSensor) {
    this.voltageForOxygenSensor = voltageForOxygenSensor;
  }

  public String getThrottlePosition() {
    return throttlePosition;
  }

  public void setThrottlePosition(String throttlePosition) {
    this.throttlePosition = throttlePosition;
  }

  public String getSpwr() {
    return spwr;
  }

  public void setSpwr(String spwr) {
    this.spwr = spwr;
  }

  public String getRpm() {
    return rpm;
  }

  public void setRpm(String rpm) {
    this.rpm = rpm;
  }

  public String getVss() {
    return vss;
  }

  public void setVss(String vss) {
    this.vss = vss;
  }

  public String getWbtm() {
    return wbtm;
  }

  public void setWbtm(String wbtm) {
    this.wbtm = wbtm;
  }

  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  public String getLonDir() {
    return lonDir;
  }

  public void setLonDir(String lonDir) {
    this.lonDir = lonDir;
  }

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getLatDir() {
    return latDir;
  }

  public void setLatDir(String latDir) {
    this.latDir = latDir;
  }

  public String getGpsSpeed() {
    return gpsSpeed;
  }

  public void setGpsSpeed(String gpsSpeed) {
    this.gpsSpeed = gpsSpeed;
  }

  public String getGpsHeading() {
    return gpsHeading;
  }

  public void setGpsHeading(String gpsHeading) {
    this.gpsHeading = gpsHeading;
  }

  public String getGpsDataValidity() {
    return gpsDataValidity;
  }

  public void setGpsDataValidity(String gpsDataValidity) {
    this.gpsDataValidity = gpsDataValidity;
  }

  public String getaOilWear() {
    return aOilWear;
  }

  public void setaOilWear(String aOilWear) {
    this.aOilWear = aOilWear;
  }

  public String getrOilMass() {
    return rOilMass;
  }

  public void setrOilMass(String rOilMass) {
    this.rOilMass = rOilMass;
  }

  public String getGx() {
    return gx;
  }

  public void setGx(String gx) {
    this.gx = gx;
  }

  public String getGy() {
    return gy;
  }

  public void setGy(String gy) {
    this.gy = gy;
  }

  public String getGz() {
    return gz;
  }

  public void setGz(String gz) {
    this.gz = gz;
  }

  private String gz;  //gsensor z方向值

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getrOilMassType() {
    return rOilMassType;
  }

  public void setrOilMassType(String rOilMassType) {
    this.rOilMassType = rOilMassType;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public String getBase_station() {
    return base_station;
  }

  public void setBase_station(String base_station) {
    this.base_station = base_station;
  }

  public String getDist_id() {
    return dist_id;
  }

  public void setDist_id(String dist_id) {
    this.dist_id = dist_id;
  }

  public String getRpf() {
    return rpf;
  }

  public void setRpf(String rpf) {
    this.rpf = rpf;
  }

  public Integer getbOilWear() {
    return bOilWear;
  }

  public void setbOilWear(Integer bOilWear) {
    this.bOilWear = bOilWear;
  }

  public Integer getmOilWear() {
    return mOilWear;
  }

  public void setmOilWear(Integer mOilWear) {
    this.mOilWear = mOilWear;
  }
}
