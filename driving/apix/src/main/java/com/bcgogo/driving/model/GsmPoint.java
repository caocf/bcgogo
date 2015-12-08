package com.bcgogo.driving.model;

import com.bcgogo.driving.model.base.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: Jimuchen
 * Date: 14-2-25
 * Time: 下午6:58
 */
@Entity
@Table(name = "gsm_point")
public class GsmPoint extends LongIdentifier {
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
  private String orgInfo;   //原始信息
  private Long uploadTime;//设备发给我们的时间
  private Long uploadServerTime;//数据上传时服务器时间
  private Integer impactStrength;//碰撞强度

  @Column(name = "emi")
  public String getEmi() {
    return emi;
  }

  public void setEmi(String emi) {
    this.emi = emi;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "state")
  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  @Column(name = "app_password")
  public String getAppPassword() {
    return appPassword;
  }

  public void setAppPassword(String appPassword) {
    this.appPassword = appPassword;
  }

  @Column(name = "gp_group")
  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  @Column(name = "cell_pos")
  public String getCellPos() {
    return cellPos;
  }

  public void setCellPos(String cellPos) {
    this.cellPos = cellPos;
  }

  @Column(name = "lon")
  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  @Column(name = "lon_dir")
  public String getLonDir() {
    return lonDir;
  }

  public void setLonDir(String lonDir) {
    this.lonDir = lonDir;
  }

  @Column(name = "lat")
  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  @Column(name = "lat_dir")
  public String getLatDir() {
    return latDir;
  }

  public void setLatDir(String latDir) {
    this.latDir = latDir;
  }

  @Column(name = "velocity")
  public String getVelocity() {
    return velocity;
  }

  public void setVelocity(String velocity) {
    this.velocity = velocity;
  }

  @Column(name = "heading")
  public String getHeading() {
    return heading;
  }

  public void setHeading(String heading) {
    this.heading = heading;
  }

  @Column(name = "gp_date")
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  @Column(name = "gp_time")
  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  @Column(name = "org_info", length = 1000)
  public String getOrgInfo() {
    return orgInfo;
  }

  public void setOrgInfo(String orgInfo) {
    this.orgInfo = orgInfo;
  }

  @Column(name = "upload_time")
  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
  }

  @Column(name = "upload_server_time")
  public Long getUploadServerTime() {
    return uploadServerTime;
  }

  public void setUploadServerTime(Long uploadServerTime) {
    this.uploadServerTime = uploadServerTime;
  }


  @Column(name = "impact_strength")
  public Integer getImpactStrength() {
    return impactStrength;
  }

  public void setImpactStrength(Integer impactStrength) {
    this.impactStrength = impactStrength;
  }
}
