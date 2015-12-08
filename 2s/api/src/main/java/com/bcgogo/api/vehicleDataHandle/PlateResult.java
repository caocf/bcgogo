package com.bcgogo.api.vehicleDataHandle;

/**
 * Created by XinyuQiu on 14-10-23.
 */
public class PlateResult {
  private String license;
  private Integer colorValue;
  private Integer colorType;
  private Integer type;
  private Integer confidence;
  private Integer bright;
  private Integer direction;
  private Integer timeUsed;
  private Integer carBright;
  private Integer carColor;
  private TimeStamp timeStamp;
  private Location location;
  private Integer triggerType;

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public Integer getColorValue() {
    return colorValue;
  }

  public void setColorValue(Integer colorValue) {
    this.colorValue = colorValue;
  }

  public Integer getColorType() {
    return colorType;
  }

  public void setColorType(Integer colorType) {
    this.colorType = colorType;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Integer getConfidence() {
    return confidence;
  }

  public void setConfidence(Integer confidence) {
    this.confidence = confidence;
  }

  public Integer getBright() {
    return bright;
  }

  public void setBright(Integer bright) {
    this.bright = bright;
  }

  public Integer getDirection() {
    return direction;
  }

  public void setDirection(Integer direction) {
    this.direction = direction;
  }

  public Integer getTimeUsed() {
    return timeUsed;
  }

  public void setTimeUsed(Integer timeUsed) {
    this.timeUsed = timeUsed;
  }

  public Integer getCarBright() {
    return carBright;
  }

  public void setCarBright(Integer carBright) {
    this.carBright = carBright;
  }

  public Integer getCarColor() {
    return carColor;
  }

  public void setCarColor(Integer carColor) {
    this.carColor = carColor;
  }

  public TimeStamp getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(TimeStamp timeStamp) {
    this.timeStamp = timeStamp;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Integer getTriggerType() {
    return triggerType;
  }

  public void setTriggerType(Integer triggerType) {
    this.triggerType = triggerType;
  }
}
