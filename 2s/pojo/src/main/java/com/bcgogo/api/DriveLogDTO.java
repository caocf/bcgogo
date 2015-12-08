package com.bcgogo.api;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.DriveLogStatus;
import com.bcgogo.enums.app.DriveStatStatus;
import com.bcgogo.user.Coordinate;
import com.bcgogo.utils.*;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-9
 * Time: 上午9:51
 */
public class DriveLogDTO implements Serializable {
  private Long id;
  private String idStr;
  private String appUserNo;
  private String imei;
  private String appDriveLogId;//app里记录的行车日志的Id
  private Long lastUpdateTime;//最后更新时间
  private String vehicleNo;//当前日志的车牌号
  private Long startTime; //开始时间
  private String startLat;//开始维度
  private String startLon; //开始经度
  private String startPlace;//开始地址
  private Long endTime; //结束时间
  private String endLat;//结束维度
  private String endLon; //结束经度
  private String endPlace;//结束地址
  private Long travelTime;//行程时间 （秒为单位）
  private Double distance;//路程  （千米）
  private Double oilWear;//平均油耗  L/100KM
  private Double oilPrice;//油价
  private String oilKind;//油品
  private Double totalOilMoney;//油钱
  private String placeNotes;//踩点信息
  private DriveLogStatus status;//日志的状态
  private AppPlatform appPlatform;//系统平台
  private Double oilCost;//耗油量 单位L
  private DriveStatStatus driveStatStatus;//统计状态

  //行车日志
  private String startTimeStr;// 开始时间
  private String endTimeStr;//  结束时间
  private String travelTimeStr;//行驶时间
  private String detailStartPlace; //具体到门牌号的开始地址
  private String detailEndPlace; //具体到门牌号的结束地址
  private Coordinate[] coordinates; //gps坐标
  private String averageSpeed;//平均速度
  private List<Coordinate> baiDuCoordinate;//百度坐标

  //微信行车轨迹显示所需的城市名和具体街道地址
  private String startCity;
  private String startAddressDesc;
  private String endCity;
  private String endAddressDesc;

  public String getStartCity() {
    return startCity;
  }

  public void setStartCity(String startCity) {
    this.startCity = startCity;
  }

  public String getStartAddressDesc() {
    return startAddressDesc;
  }

  public void setStartAddressDesc(String startAddressDesc) {
    this.startAddressDesc = startAddressDesc;
  }

  public String getEndCity() {
    return endCity;
  }

  public void setEndCity(String endCity) {
    this.endCity = endCity;
  }

  public String getEndAddressDesc() {
    return endAddressDesc;
  }

  public void setEndAddressDesc(String endAddressDesc) {
    this.endAddressDesc = endAddressDesc;
  }

  public String generateParamString() throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("startPlace=").append(URLEncoder.encode(getStartPlace(), "UTF-8"));
    sb.append("&endPlace=").append(URLEncoder.encode(getEndPlace(), "UTF-8"));

    sb.append("&averageSpeed=").append(getAverageSpeed());

    sb.append("&vehicleNo=").append(URLEncoder.encode(getVehicleNo(), "UTF-8"));

    sb.append("&coordinate=");
    if (CollectionUtil.isNotEmpty(getBaiDuCoordinate())) {
      int size = getBaiDuCoordinate().size() / 100;
      if (getBaiDuCoordinate().size() <= 100) {
        size = 1;
      }
      for (double index = 0; index < getBaiDuCoordinate().size(); index = index + size) {
        Coordinate coordinate = getBaiDuCoordinate().get(Double.valueOf(index).intValue());
        sb.append(coordinate.getLat()).append(",").append(coordinate.getLng());
        sb.append("-");
      }

      return sb.substring(0, sb.length() - 2);
    }
    return sb.toString();
  }


  public void calculateAverageSpeed() {
    if (travelTime != null && travelTime>0 && distance != null & distance>0&&
        (this.getTravelTime() / (DateUtil.MINUTE_MILLION_SECONDS / DateUtil.MILLOIN_SECONDS))>0) {
      this.setAverageSpeed(String.valueOf(
          NumberUtil.toReserve(this.getDistance() /(this.getTravelTime() / (DateUtil.MINUTE_MILLION_SECONDS / DateUtil.MILLOIN_SECONDS)),
          NumberUtil.MONEY_PRECISION)));
    }
  }

  public void generateCoordinate() {
    if (StringUtil.isEmpty(getPlaceNotes())) {
      return;
    }
    String[] strings = getPlaceNotes().split("\\|");
    if (ArrayUtil.isEmpty(strings)) {
      return;
    }
    List<Coordinate> coordinateList = new ArrayList<Coordinate>();
    for (String str : strings) {
      if (StringUtil.isEmpty(str)) {
        continue;
      }
      String[] array = str.split(",");
      if (ArrayUtil.isEmpty(array) || array.length != 3) {
        continue;
      }
      Coordinate coordinate = new Coordinate();
      coordinate.setLat(array[0]);
      coordinate.setLng(array[1]);

      coordinateList.add(coordinate);
    }
    setCoordinates(coordinateList.toArray(new Coordinate[coordinateList.size()]));
  }


  public void setPlaceNoteDTO(DriveLogPlaceNoteDTO driveLogPlaceNoteDTO) {
    if (driveLogPlaceNoteDTO != null) {
      setPlaceNotes(driveLogPlaceNoteDTO.getPlaceNotes());
      setAppPlatform(driveLogPlaceNoteDTO.getAppPlatform());
    }
  }

  public void setAppUserVehicleInfo(AppUserDTO appUserDTO, AppVehicleDTO appVehicleDTO) {
    if(appUserDTO != null){
      setAppUserNo(appUserDTO.getUserNo());
    }
    if(appVehicleDTO!=null){
      setVehicleNo(appVehicleDTO.getVehicleNo());
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if (id != null) {
      this.setIdStr(id.toString());
    }
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

  public String getAppDriveLogId() {
    return appDriveLogId;
  }

  public void setAppDriveLogId(String appDriveLogId) {
    this.appDriveLogId = appDriveLogId;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
    if(startTime != null){
      this.setStartTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL,startTime));
    }
  }

  public String getStartLat() {
    return startLat;
  }

  public void setStartLat(String startLat) {
    this.startLat = startLat;
  }

  public String getStartLon() {
    return startLon;
  }

  public void setStartLon(String startLon) {
    this.startLon = startLon;
  }

  public String getStartPlace() {
    return startPlace;
  }

  public void setStartPlace(String startPlace) {
    this.startPlace = startPlace;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
    if (endTime != null) {
      this.setEndTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, endTime));
    }
  }

  public String getEndLat() {
    return endLat;
  }

  public void setEndLat(String endLat) {
    this.endLat = endLat;
  }

  public String getEndLon() {
    return endLon;
  }

  public void setEndLon(String endLon) {
    this.endLon = endLon;
  }

  public String getEndPlace() {
    return endPlace;
  }

  public void setEndPlace(String endPlace) {
    this.endPlace = endPlace;
  }

  public Long getTravelTime() {
    return travelTime;
  }

  public void setTravelTime(Long travelTime) {
    this.travelTime = travelTime;

    if (travelTime != null) {
      Long hours = travelTime / 3600;
      Long min = (travelTime % 3600) / 60;

      String str = (hours > 0 ? (hours + "小时") : "") + (min > 0 ? (min + "分") : "");
      this.setTravelTimeStr(str);
    }

  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public Double getOilWear() {
    return oilWear;
  }

  public void setOilWear(Double oilWear) {
    this.oilWear = oilWear;
  }

  public Double getOilPrice() {
    return oilPrice;
  }

  public void setOilPrice(Double oilPrice) {
    this.oilPrice = oilPrice;
  }

  public String getOilKind() {
    return oilKind;
  }

  public void setOilKind(String oilKind) {
    this.oilKind = oilKind;
  }

  public Double getTotalOilMoney() {
    return totalOilMoney;
  }

  public void setTotalOilMoney(Double totalOilMoney) {
    this.totalOilMoney = totalOilMoney;
  }

  public String getPlaceNotes() {
    return placeNotes;
  }

  public void setPlaceNotes(String placeNotes) {
    this.placeNotes = placeNotes;
  }

  public DriveLogStatus getStatus() {
    return status;
  }

  public void setStatus(DriveLogStatus status) {
    this.status = status;
  }

  public Long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public AppPlatform getAppPlatform() {
    return appPlatform;
  }

  public void setAppPlatform(AppPlatform appPlatform) {
    this.appPlatform = appPlatform;
  }

  public Double getOilCost() {
    return oilCost;
  }

  public void setOilCost(Double oilCost) {
    this.oilCost = oilCost;
  }

  public DriveStatStatus getDriveStatStatus() {
    return driveStatStatus;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setDriveStatStatus(DriveStatStatus driveStatStatus) {
    this.driveStatStatus = driveStatStatus;
  }
  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public String getTravelTimeStr() {
    return travelTimeStr;
  }

  public void setTravelTimeStr(String travelTimeStr) {
    this.travelTimeStr = travelTimeStr;
  }

  public String getDetailStartPlace() {
    return detailStartPlace;
  }

  public void setDetailStartPlace(String detailStartPlace) {
    this.detailStartPlace = detailStartPlace;
  }

  public String getDetailEndPlace() {
    return detailEndPlace;
  }

  public void setDetailEndPlace(String detailEndPlace) {
    this.detailEndPlace = detailEndPlace;
  }

  public Coordinate[] getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Coordinate[] coordinates) {
    this.coordinates = coordinates;
  }

  public String getAverageSpeed() {
    return averageSpeed;
  }

  public void setAverageSpeed(String averageSpeed) {
    this.averageSpeed = averageSpeed;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public List<Coordinate> getBaiDuCoordinate() {
    return baiDuCoordinate;
  }

  public void setBaiDuCoordinate(List<Coordinate> baiDuCoordinate) {
    this.baiDuCoordinate = baiDuCoordinate;
  }
}
