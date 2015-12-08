package com.bcgogo.vehicle.evalute.car360;

import com.bcgogo.utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-13
 * Time: 下午2:08
 */
public class Car360EvaluateCondition {
  //车型标识
  private String modelId;
  //待估车辆的上牌时间（格式：yyyy-MM）
  private String regDate;
  //待估车辆的公里数，单位万公里。
  private String mile;
  //城市标识
  private String zone;
  //待估车辆的标题信息，可选参数。
  private String title;
  //待估车辆在贵网站上面的卖价（不是指导价，是用户标的价格），可选参数。
  private Double price;

  private String vehicleNo;

  public String genQueryParam() throws UnsupportedEncodingException {
    StringBuilder sb=new StringBuilder();
    sb.append("&modelId=").append(modelId);
    sb.append("&regDate=").append(regDate);
    sb.append("&mile=").append(mile);
    sb.append("&zone=").append(zone);
    if(StringUtil.isNotEmpty(title)){
      sb.append("&title=").append(URLEncoder.encode(title, "UTF-8"));
    }
    if(price!=null){
      sb.append("&price=").append(price);
    }
    return sb.toString();
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public String getRegDate() {
    return regDate;
  }

  public void setRegDate(String regDate) {
    this.regDate = regDate;
  }

  public String getMile() {
    return mile;
  }

  public void setMile(String mile) {
    this.mile = mile;
  }

  public String getZone() {
    return zone;
  }

  public void setZone(String zone) {
    this.zone = zone;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
}
