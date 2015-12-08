package com.bcgogo.enums.common;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午3:08
 * To change this template use File | Settings | File Templates.
 */
public enum Frequency {
  DAY("每天"),
  HOUR("每小时"),
  HALF_AN_HOUR("每半小时");

  String frequency;

  Frequency(String frequency){
    this.frequency = frequency;
  }

  public String getFrequency(){
    return this.frequency;
  }

}
