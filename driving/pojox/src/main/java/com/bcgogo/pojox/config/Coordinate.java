package com.bcgogo.pojox.config;

/**
 * User: ZhangJuntao
 * Date: 13-8-2
 * Time: 上午10:57
 */
public class Coordinate {
  private String lat;
  private String lng;   // lat: 纬度：数值，lng: 经度：数值

  private String x;//和lng一样
  private String y;//和lat一样


  public Coordinate() {
    this.setX(getX());
  }

  public Coordinate(String lat, String lng) {
    this.lat = lat;
    this.lng = lng;
    this.x =lng;
    this.y =lat;
  }

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getLng() {
    return lng;
  }

  public void setLng(String lng) {
    this.lng = lng;
  }

  public String getX() {
    return x;
  }

  public void setX(String x) {
    this.x = x;
    this.lng = x;
  }

  public String getY() {
    return y;
  }

  public void setY(String y) {
    this.y = y;
    this.lat = y;
  }

  @Override
  public String toString() {
    return "Coordinate{" +
        "lat='" + lat + '\'' +
        ", lng='" + lng + '\'' +
        ", x='" + x + '\'' +
        ", y='" + y + '\'' +
        '}';
  }
}
