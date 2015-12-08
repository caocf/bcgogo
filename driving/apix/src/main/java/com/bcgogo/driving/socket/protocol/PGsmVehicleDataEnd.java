package com.bcgogo.driving.socket.protocol;

import com.bcgogo.pojox.api.GsmVehicleDataDTO;
import com.bcgogo.pojox.constant.GSMConstant;
import com.bcgogo.pojox.constant.XConstant;
import com.bcgogo.pojox.util.BinaryUtil;
import com.bcgogo.pojox.util.DateUtil;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/**
 * 鹏奥达--里程结束包
 * <p/>
 * Author: ndong
 * Date: 15-9-17
 * Time: 下午3:45
 */
public class PGsmVehicleDataEnd {
  private String appUserNo;
  private String uuid;
  private String imei;  //设备id
  private String i_id; //信息标识
  private String timestamp;//时间戳
  private String gpsDataValidity;//数据有效位
  private String lat; //纬度
  private String latDir; //纬度标识,N表示北纬，S南纬
  private String lon;   //经度
  private String lonDir; //经度标识：E标识东经，W标识西经
  private String vss;   //速度
  private String gpsHeading;
  private String date;
  private String v_data;
  private String alert_flag; //用户报警标识
  private String alert_status;//报警设置
  private String country;
  private String supplier;
  private String base_station;
  private String dist_id;
  private String battery_flag;
  private Integer mile;  //本次行程的里程  (单位:米)
  private Integer oilWear; //本次行程油耗 单位:L
  private Integer max_speed;//本次行程最高速度  单位:km/h
  private Integer a_speed;//本次行程平均速度  单位:km/h
  private Integer ds_time;//本次行程怠速时间  单位:s
  private Long travel_time;//本次行程的行驶时间 单位:秒

  public PGsmVehicleDataEnd(String hexString) throws UnsupportedEncodingException {
    hexString = hexString.substring(0, hexString.length() - 2);
    String[] strArr = BinaryUtil.hexString2String(hexString).split(",");
    this.imei = strArr[1];
    this.i_id = strArr[2];
    this.timestamp = strArr[3];
    this.gpsDataValidity = strArr[4];
    String tmp = strArr[5];
    this.lat = String.valueOf(Double.valueOf(tmp.substring(0, 2)) + Double.valueOf(tmp.substring(2, tmp.length())) / 60);
    this.latDir = strArr[6];
    tmp = strArr[7];
    this.lon = String.valueOf(Double.valueOf(tmp.substring(0, 3)) + Double.valueOf(tmp.substring(3, tmp.length()))/ 60);
    this.lonDir = strArr[8];
    this.vss = strArr[9];
    this.gpsHeading = strArr[10];
    this.date = strArr[11];
    this.v_data = strArr[12];
    this.alert_flag = strArr[13];
    this.alert_status = strArr[14];
    this.country = strArr[15];
    this.supplier = strArr[16];
    this.base_station = strArr[17];
    this.dist_id = strArr[18];
    this.battery_flag = strArr[19];
    this.mile = Integer.parseInt(strArr[20], 16);
    this.oilWear = Integer.parseInt(strArr[21], 16);
    this.max_speed = Integer.parseInt(strArr[22], 16);
    this.a_speed = Integer.parseInt(strArr[23], 16);
    this.ds_time = Integer.parseInt(strArr[24], 16);
    this.travel_time = Long.parseLong(strArr[25], 16);
  }

  public GsmVehicleDataDTO toGVDataDTO() throws ParseException {
    GsmVehicleDataDTO dataDTO = new GsmVehicleDataDTO();
    dataDTO.setUuid(getUuid());
    dataDTO.setMile(String.valueOf(getMile()));
    dataDTO.setOilWear(getOilWear());
    dataDTO.setImei(getImei());
    dataDTO.setAppUserNo(getAppUserNo());
    dataDTO.setVehicleStatus(GSMConstant.CUTOFF);
    dataDTO.setLat(getLat());
    dataDTO.setLatDir(getLatDir());
    dataDTO.setLon(getLon());
    dataDTO.setLonDir(getLonDir());
    dataDTO.setVss(getVss());
    dataDTO.setGpsDataValidity(gpsDataValidity);
    dataDTO.setTravelTime(getTravel_time());
    try {
      Long uploadTime = DateUtil.convertDateStringToDateLong("ddMMyyHHmmss", getDate() + getTimestamp());
//      if (uploadTime != null) {
//        Long diffTime = System.currentTimeMillis() - uploadTime;
//        if (diffTime > XConstant.ERROR_DELAY_UPLOAD_TIME || diffTime < XConstant.ERROR_EARLIER_UPLOAD_TIME) {
//          uploadTime = System.currentTimeMillis();
//        }
//      } else {
        uploadTime = System.currentTimeMillis();
//      }
      dataDTO.setUploadTime(uploadTime);
      dataDTO.setUploadServerTime(System.currentTimeMillis());
    } catch (Exception e) {
    }
    return dataDTO;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }


  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getI_id() {
    return i_id;
  }

  public void setI_id(String i_id) {
    this.i_id = i_id;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getGpsDataValidity() {
    return gpsDataValidity;
  }

  public void setGpsDataValidity(String gpsDataValidity) {
    this.gpsDataValidity = gpsDataValidity;
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

  public String getVss() {
    return vss;
  }

  public void setVss(String vss) {
    this.vss = vss;
  }

  public String getGpsHeading() {
    return gpsHeading;
  }

  public void setGpsHeading(String gpsHeading) {
    this.gpsHeading = gpsHeading;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getV_data() {
    return v_data;
  }

  public void setV_data(String v_data) {
    this.v_data = v_data;
  }

  public String getAlert_flag() {
    return alert_flag;
  }

  public void setAlert_flag(String alert_flag) {
    this.alert_flag = alert_flag;
  }

  public String getAlert_status() {
    return alert_status;
  }

  public void setAlert_status(String alert_status) {
    this.alert_status = alert_status;
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

  public String getBattery_flag() {
    return battery_flag;
  }

  public void setBattery_flag(String battery_flag) {
    this.battery_flag = battery_flag;
  }

  public Integer getMile() {
    return mile;
  }

  public void setMile(Integer mile) {
    this.mile = mile;
  }

  public Integer getOilWear() {
    return oilWear;
  }

  public void setOilWear(Integer oilWear) {
    this.oilWear = oilWear;
  }

  public Integer getMax_speed() {
    return max_speed;
  }

  public void setMax_speed(Integer max_speed) {
    this.max_speed = max_speed;
  }

  public Integer getA_speed() {
    return a_speed;
  }

  public void setA_speed(Integer a_speed) {
    this.a_speed = a_speed;
  }

  public Integer getDs_time() {
    return ds_time;
  }

  public void setDs_time(Integer ds_time) {
    this.ds_time = ds_time;
  }

  public Long getTravel_time() {
    return travel_time;
  }

  public void setTravel_time(Long travel_time) {
    this.travel_time = travel_time;
  }
}
