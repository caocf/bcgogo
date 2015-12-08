package com.bcgogo.pojox.api;

import com.bcgogo.pojox.constant.XConstant;
import com.bcgogo.pojox.enums.app.AppUserType;
import com.bcgogo.pojox.enums.etl.GsmVehicleStatus;
import com.bcgogo.pojox.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后视镜车况信息采集
 * Author: ndong
 * Date: 2015-4-24
 * Time: 11:30
 */
public class GsmVehicleDataDTO {
  private static final Logger LOG = LoggerFactory.getLogger(GsmVehicleDataDTO.class);
  private String id;
  private String idStr;
  private String uuid; //行程识别码
  private String impactUUid; //行程识别码
  private String imei;
  private String appUserNo;
  private AppUserType userType;
  private String curMil;  //仪表盘当前里程数(单位：km)
  private String gpsMile;
  //车门状态，5位数字，分别代表顺序为左前门右前门左后门右后门后备箱。状态值为0，1，2，0是关闭，1是打开，2是未知。 例如，01012：
  private String door;
  private Long uploadTime;  //设备上传的采集数据时间。格式150422212030，15年04月22日21时20分30秒
  private String upLoadTimeStr;
  private Long uploadServerTime; //数据上传时服务器时间
  private int dttpe; //数据实时上报状态，0实时，1补报，当数据产生时无网络连接，本地存储下来，下次有网络时上报数据数据标识为1
  private String rdtc;  //故障代码   U0021,B0090,C0032,P0006,P0007,P1233,P0008,P0009,U0022
  //0:点火；1：熄火；2：行驶中
  private String vehicleStatus;
  private String rpf;  //发动机负荷


  /**
   * **********************  OBD标准数据流   *************************************
   */
  private String spwr;  //电瓶电压                 12.53V      //电源系统
  private String rpm;  //发动机转速                2000
  private String vss;  //车辆速度                  90km/h
  private String wbtm; //水箱温度                             //冷却系统

  /**
   * **********************  gsp信息   *************************************
   */
  private String lon; //经度
  private String lonDir; //经度标识：E标识东经，W标识西经
  private String lat; //纬度
  private String latDir; //纬度标识,N表示北纬，S南纬
  private String gpsSpeed; //gps速度
  private String gpsHeading; //gps航向

  /**
   * **********************  车况信息   *************************************
   */
  private String voltageForOxygenSensor;  //进气系统   氧传感器电压 0.255V
  private String throttlePosition;  //排放系统          节气门相对位置 20%

  //定位数据有效值，A：实时定位，V：无效定位，取上一个有效定位数据
  private String gpsDataValidity;

  /**
   * **********************  油耗信息   *************************************
   */
  //本次行程平均油耗。 9L/100KM，参考套线协议，EPID数据  todo瞬时油耗？
  private String aOilWear;
  //剩余油量。 10%或者30L 不同车型表示结果不同，或者参考鹏奥达编码规则
  private String rOilMass;
  private String rOilMassType;

  private Integer bOilWear;  //百公里油耗
  private Integer mOilWear;  //每小时油耗

  //新增 鹏奥达编码规则
  private String gx;  //gsensor x方向值
  private String gy;  //gsensor y方向值
  private String gz;  //gsensor z方向值

  //彭奥迪上传数据
  private String mile;//本次行程里程(鹏奥达)
  private Long travelTime;//本次行程时间 （单位:秒）
  private Integer oilWear;//本次行程油耗(鹏奥达)

  /**
   * **********************  辅助gps数据   *************************************
   */
  private String country;  //国家代码
  private String supplier; //运营商代码
  private String base_station;//基站号
  private String dist_id;  //小区ID

 /**
   * 对上传的错误时间进行纠错
   */
  public void correctUploadTime() {
    if (this.getUploadTime() != null) {
      Long diffTime = System.currentTimeMillis() - this.getUploadTime();
      if (diffTime > XConstant.ERROR_DELAY_UPLOAD_TIME || diffTime < XConstant.ERROR_EARLIER_UPLOAD_TIME) {
        this.setUploadTime(System.currentTimeMillis());
      }
    } else {
      this.setUploadTime(System.currentTimeMillis());
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getRpf() {
    return rpf;
  }

  public void setRpf(String rpf) {
    this.rpf = rpf;
  }

  public GsmVehicleStatus getGsmVehicleStatus() {
    return gsmVehicleStatus;
  }

  public void setGsmVehicleStatus(GsmVehicleStatus gsmVehicleStatus) {
    this.gsmVehicleStatus = gsmVehicleStatus;
  }

  //定时钟数据处理状态
  private GsmVehicleStatus gsmVehicleStatus = GsmVehicleStatus.UN_HANDLE;

  public String getVehicleStatus() {
    return vehicleStatus;
  }

  public void setVehicleStatus(String vehicleStatus) {
    this.vehicleStatus = vehicleStatus;
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

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getImpactUUid() {
    return impactUUid;
  }

  public void setImpactUUid(String impactUUid) {
    this.impactUUid = impactUUid;
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

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getCurMil() {
    return curMil;
  }

  public void setCurMil(String curMil) {
    this.curMil = curMil;
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

  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
    try {
      this.upLoadTimeStr = DateUtil.convertDateLongToDateString(DateUtil.ALL, uploadTime);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public Long getUploadServerTime() {
    return uploadServerTime;
  }

  public void setUploadServerTime(Long uploadServerTime) {
    this.uploadServerTime = uploadServerTime;
  }

  public int getDttpe() {
    return dttpe;
  }

  public void setDttpe(int dttpe) {
    this.dttpe = dttpe;
  }

  public String getRdtc() {
    return rdtc;
  }

  public void setRdtc(String rdtc) {
    this.rdtc = rdtc;
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


  public String getWbtm() {
    return wbtm;
  }

  public void setWbtm(String wbtm) {
    this.wbtm = wbtm;
  }


  public String getUpLoadTimeStr() {
    return upLoadTimeStr;
  }

  public void setUpLoadTimeStr(String upLoadTimeStr) {
    this.upLoadTimeStr = upLoadTimeStr;
  }

  public String getGz() {
    return gz;
  }

  public void setGz(String gz) {
    this.gz = gz;
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

  public String getrOilMassType() {
    return rOilMassType;
  }

  public void setrOilMassType(String rOilMassType) {
    this.rOilMassType = rOilMassType;
  }

  public String getMile() {
    return mile;
  }

  public void setMile(String mile) {
    this.mile = mile;
  }

  public Integer getOilWear() {
    return oilWear;
  }

  public void setOilWear(Integer oilWear) {
    this.oilWear = oilWear;
  }

  public Long getTravelTime() {
    return travelTime;
  }

  public void setTravelTime(Long travelTime) {
    this.travelTime = travelTime;
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
}
