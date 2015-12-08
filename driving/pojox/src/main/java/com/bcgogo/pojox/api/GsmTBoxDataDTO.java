package com.bcgogo.pojox.api;

import com.bcgogo.pojox.constant.XConstant;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-11-17
 * Time: 上午9:42
 */
public class GsmTBoxDataDTO {
  private String id;
  private String idStr;
  private String uuid; //行程识别码
  private Long uploadTime;
  private Long uploadServerTime;
  private String appUserNo;
  //0:点火；1：熄火；2：行驶中
  private String vehicleStatus;
  //故障代码   U0021,B0090,C0032,P0006,P0007,P1233,P0008,P0009,U0022
  private String rdtc;
     //数据实时上报状态，0实时，1补报，当数据产生时无网络连接，本地存储下来，下次有网络时上报数据数据标识为1
  private int dttpe;
  /**
   * *****************************************车辆实时数据流*******************************************
   */
  private String spwr;  //电瓶电压
  private String rpm;  //发动机转速
  private String vss;  //行驶车速
  private String rpf;   //发动机负荷
  private String throttle;  //节气门开度
  private String cTemp;   //冷却液温度 ℃
  private Double trOil; //瞬时油耗    怠速）：L/h （行驶）：L/100km 通过车速判断当前是怠速状态或行驶状态
  private Double aOilWear; //平均油耗
  private Double tMile;  //总里程  km
  private String tOil; //累计耗油量 单位:L

  /**
   * *****************************************驾驶习惯数据流*******************************************
   */
  private Integer tFire; //总点火次数
  private Long tDsTime;//累计怠速时间  单位:s
  private Long tTime;//累计行驶时间 单位:秒
  private Double aSpeed;//平均车速  单位:km/h
  private Double hMaxSpeed;//历史最高车速  单位:km/h
  private Double hMaxRpm;//历史最高转速  rpm
  /**
   * *****************************************本次行程统计数据流*******************************************
   */
  private Long rcTime;//本次热车时长  单位:s
  private Long dsTime;  //本次怠速时长  min
  private Long travelTime;  //本次行驶时长  min
  private Double mile;  //本次行驶里程  km
  private Double dsOil; //本次怠速油耗 单位:L
  private Double oilWear; //本次行驶油耗 单位:L
  private Double maxRpm;//本次最高转速  rpm
  private Double maxSpeed;//本次最高车速  单位:km/h
  private Integer rapAcc; //本次急加速次数
  private Integer rapDec; //本次急减速次数

  /**
   * **********************  gsp信息   *************************************
   */
  private String lon; //经度
  private String lonDir; //经度标识：E标识东经，W标识西经
  private String lat; //纬度
  private String latDir; //纬度标识,N表示北纬，S南纬
  /**
   * **********************  gsensor信息   *************************************
   */

  private String gx;  //gsensor x方向值
  private String gy;  //gsensor y方向值
  private String gz;  //gsensor z方向值

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

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
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

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
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

  public String getRpf() {
    return rpf;
  }

  public void setRpf(String rpf) {
    this.rpf = rpf;
  }

  public String getThrottle() {
    return throttle;
  }

  public void setThrottle(String throttle) {
    this.throttle = throttle;
  }

  public String getcTemp() {
    return cTemp;
  }

  public void setcTemp(String cTemp) {
    this.cTemp = cTemp;
  }

  public Double getTrOil() {
    return trOil;
  }

  public void setTrOil(Double trOil) {
    this.trOil = trOil;
  }

  public Double getaOilWear() {
    return aOilWear;
  }

  public void setaOilWear(Double aOilWear) {
    this.aOilWear = aOilWear;
  }

  public Double gettMile() {
    return tMile;
  }

  public void settMile(Double tMile) {
    this.tMile = tMile;
  }

  public String gettOil() {
    return tOil;
  }

  public void settOil(String tOil) {
    this.tOil = tOil;
  }

  public Integer gettFire() {
    return tFire;
  }

  public void settFire(Integer tFire) {
    this.tFire = tFire;
  }

  public Long gettDsTime() {
    return tDsTime;
  }

  public void settDsTime(Long tDsTime) {
    this.tDsTime = tDsTime;
  }

  public Long gettTime() {
    return tTime;
  }

  public void settTime(Long tTime) {
    this.tTime = tTime;
  }

  public Double getaSpeed() {
    return aSpeed;
  }

  public void setaSpeed(Double aSpeed) {
    this.aSpeed = aSpeed;
  }

  public Double gethMaxSpeed() {
    return hMaxSpeed;
  }

  public void sethMaxSpeed(Double hMaxSpeed) {
    this.hMaxSpeed = hMaxSpeed;
  }

  public Double gethMaxRpm() {
    return hMaxRpm;
  }

  public void sethMaxRpm(Double hMaxRpm) {
    this.hMaxRpm = hMaxRpm;
  }

  public Long getRcTime() {
    return rcTime;
  }

  public void setRcTime(Long rcTime) {
    this.rcTime = rcTime;
  }

  public Long getDsTime() {
    return dsTime;
  }

  public void setDsTime(Long dsTime) {
    this.dsTime = dsTime;
  }

  public Long getTravelTime() {
    return travelTime;
  }

  public void setTravelTime(Long travelTime) {
    this.travelTime = travelTime;
  }

  public Double getMile() {
    return mile;
  }

  public void setMile(Double mile) {
    this.mile = mile;
  }

  public Double getDsOil() {
    return dsOil;
  }

  public void setDsOil(Double dsOil) {
    this.dsOil = dsOil;
  }

  public Double getOilWear() {
    return oilWear;
  }

  public void setOilWear(Double oilWear) {
    this.oilWear = oilWear;
  }

  public Double getMaxRpm() {
    return maxRpm;
  }

  public void setMaxRpm(Double maxRpm) {
    this.maxRpm = maxRpm;
  }

  public Double getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(Double maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public Integer getRapAcc() {
    return rapAcc;
  }

  public void setRapAcc(Integer rapAcc) {
    this.rapAcc = rapAcc;
  }

  public Integer getRapDec() {
    return rapDec;
  }

  public void setRapDec(Integer rapDec) {
    this.rapDec = rapDec;
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
}
