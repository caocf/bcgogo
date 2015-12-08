package com.bcgogo.vehicle.evalute.car360;

import com.bcgogo.common.Result;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.vehicle.evalute.EvaluateResult;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-13
 * Time: 下午2:36
 */
public class GetCar360UsedCarPriceResult {
  //1 表示成功/0 表示失败
  private String status;
  ////估值结果
  private String eval_price;
  //车况一般的估值
  private String low_price;
  //车况良好的估值
  private String good_price;
  //车况优秀的估值
  private String high_price;
  ////直达该车型在车三百网站详细估值结果页面
  private String url;
  //车型指导价
  private String price;
  //车型名称
  private String title;
  //品牌的图片地址
  private String car_logo_url;

  public EvaluateResult toEvaluateResult(){
    EvaluateResult result=new EvaluateResult();
    result.setEvalPrice(getEval_price());
    result.setLowPrice(getLow_price());
    result.setGoodPrice(getGood_price());
    result.setHighPrice(getHigh_price());
    result.setPrice(getPrice());
    result.setCarLogoUrl(getCar_logo_url());
    return result;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getEval_price() {
    return eval_price;
  }

  public void setEval_price(String eval_price) {
    this.eval_price = eval_price;
  }

  public String getLow_price() {
    return low_price;
  }

  public void setLow_price(String low_price) {
    this.low_price = low_price;
  }

  public String getGood_price() {
    return good_price;
  }

  public void setGood_price(String good_price) {
    this.good_price = good_price;
  }

  public String getHigh_price() {
    return high_price;
  }

  public void setHigh_price(String high_price) {
    this.high_price = high_price;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCar_logo_url() {
    return car_logo_url;
  }

  public void setCar_logo_url(String car_logo_url) {
    this.car_logo_url = car_logo_url;
  }
}
