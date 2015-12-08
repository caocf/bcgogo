package com.bcgogo.pojox.api;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-11-6
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class VersionDTO {
  String lyx;
  String app;
  String mcu;
  String os;
  String dvr;

  public VersionDTO() {
    super();
  }

  public VersionDTO(String lyx, String app, String mcu, String os, String dvr) {
    this.lyx = lyx;
    this.app = app;
    this.mcu = mcu;
    this.os = os;
    this.dvr = dvr;
  }

  public String getLyx() {
    return lyx;
  }

  public void setLyx(String lyx) {
    this.lyx = lyx;
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public String getMcu() {
    return mcu;
  }

  public void setMcu(String mcu) {
    this.mcu = mcu;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getDvr() {
    return dvr;
  }

  public void setDvr(String dvr) {
    this.dvr = dvr;
  }
}
