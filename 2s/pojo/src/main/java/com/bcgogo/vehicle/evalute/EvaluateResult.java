package com.bcgogo.vehicle.evalute;

import com.bcgogo.common.Result;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-17
 * Time: 下午4:15
 */
public class EvaluateResult extends Result{
  private String evalPrice;
  //车况一般的估值
  private String lowPrice;
  //车况良好的估值
  private String goodPrice;
  //车况优秀的估值
  private String highPrice;
  //详细估值结果页面
  private String url;
  //车型指导价
  private String price;
  //车型名称
  private String model;
  //品牌的图片地址
  private String carLogoUrl;

  public EvaluateResult(){}

  public EvaluateResult(String msg, boolean success){
    super(msg,success);
  }

  public String getEvalPrice() {
    return evalPrice;
  }

  public void setEvalPrice(String evalPrice) {
    this.evalPrice = evalPrice;
  }

  public String getLowPrice() {
    return lowPrice;
  }

  public void setLowPrice(String lowPrice) {
    this.lowPrice = lowPrice;
  }

  public String getGoodPrice() {
    return goodPrice;
  }

  public void setGoodPrice(String goodPrice) {
    this.goodPrice = goodPrice;
  }

  public String getHighPrice() {
    return highPrice;
  }

  public void setHighPrice(String highPrice) {
    this.highPrice = highPrice;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getCarLogoUrl() {
    return carLogoUrl;
  }

  public void setCarLogoUrl(String carLogoUrl) {
    this.carLogoUrl = carLogoUrl;
  }
}
