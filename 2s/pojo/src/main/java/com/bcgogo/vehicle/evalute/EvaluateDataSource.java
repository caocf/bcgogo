package com.bcgogo.vehicle.evalute;

/**
 * 评估数据接口来源
 * Author: ndong
 * Date: 14-11-17
 * Time: 下午5:36
 */
public enum EvaluateDataSource {
  CAR300("car300"),
  ;

  private String name;
  EvaluateDataSource(String name){
    this.name=name;
  }
}
