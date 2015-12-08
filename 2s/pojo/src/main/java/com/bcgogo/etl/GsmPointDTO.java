package com.bcgogo.etl;

import com.bcgogo.enums.app.GsmPointType;
import com.bcgogo.enums.etl.GsmVehicleStatus;

import java.io.Serializable;

/**
 * User: lw
 * Date: 14-5-4
 * Time: 下午5:39
 */
public class GsmPointDTO implements Serializable {

 private Long id;
  private String emi;        //EMI
  private String appUserNo; //用户名
  private Integer state;     //状态   0 设置成功   1 密码错误  2 不识别的指令
  private String appPassword; //2468
  private String group;    //组数
  private String cellPos; //基站信息
  private String lon; //经度
  private String lonDir; //经度方向
  private String lat; //纬度
  private String latDir; //纬度方向
  private String velocity; //速度
  private String heading; //航向
  private String date; //日期
  private String time; //时间
  private GsmVehicleStatus gsmVehicleStatus = GsmVehicleStatus.UN_HANDLE;
  private String orgInfo;   //原始信息
  private Long groupId;   //组Id
  private Long uploadTime;//设备发给我们的时间
  private Long uploadServerTime;//数据上传时服务器时间
  private GsmPointType gsmPointType;//报警类型
  private Integer impactStrength;//碰撞强度
  private String uploadServerTimeStr;  //数据上传时服务器时间

  private String gpsLat;//gsp坐标：纬度
  private String gpsLon;//gso坐标:经度

  private String baiDuLat;//gso坐标:纬度
  private String baiDuLon;//gso坐标:经度

  private String address;
  private String vehicleNo;//车牌号


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmi() {
    return emi;
  }

  public void setEmi(String emi) {
    this.emi = emi;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getAppPassword() {
    return appPassword;
  }

  public void setAppPassword(String appPassword) {
    this.appPassword = appPassword;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getCellPos() {
    return cellPos;
  }

  public void setCellPos(String cellPos) {
    this.cellPos = cellPos;
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

  public String getVelocity() {
    return velocity;
  }

  public void setVelocity(String velocity) {
    this.velocity = velocity;
  }

  public String getHeading() {
    return heading;
  }

  public void setHeading(String heading) {
    this.heading = heading;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public GsmVehicleStatus getGsmVehicleStatus() {
    return gsmVehicleStatus;
  }

  public void setGsmVehicleStatus(GsmVehicleStatus gsmVehicleStatus) {
    this.gsmVehicleStatus = gsmVehicleStatus;
  }

  public String getOrgInfo() {
    return orgInfo;
  }

  public void setOrgInfo(String orgInfo) {
    this.orgInfo = orgInfo;
  }

  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
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

  public GsmPointType getGsmPointType() {
    return gsmPointType;
  }

  public void setGsmPointType(GsmPointType gsmPointType) {
    this.gsmPointType = gsmPointType;
  }

  public Integer getImpactStrength() {
    return impactStrength;
  }

  public void setImpactStrength(Integer impactStrength) {
    this.impactStrength = impactStrength;
  }

  public String getUploadServerTimeStr() {
    return uploadServerTimeStr;
  }

  public void setUploadServerTimeStr(String uploadServerTimeStr) {
    this.uploadServerTimeStr = uploadServerTimeStr;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getGpsLat() {
    return gpsLat;
  }

  public void setGpsLat(String gpsLat) {
    this.gpsLat = gpsLat;
  }

  public String getGpsLon() {
    return gpsLon;
  }

  public void setGpsLon(String gpsLon) {
    this.gpsLon = gpsLon;
  }

  public String getBaiDuLat() {
    return baiDuLat;
  }

  public void setBaiDuLat(String baiDuLat) {
    this.baiDuLat = baiDuLat;
  }

  public String getBaiDuLon() {
    return baiDuLon;
  }

  public void setBaiDuLon(String baiDuLon) {
    this.baiDuLon = baiDuLon;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }
}
