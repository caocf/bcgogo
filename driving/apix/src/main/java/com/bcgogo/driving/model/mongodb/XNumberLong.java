package com.bcgogo.driving.model.mongodb;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午2:35
 */
public class XNumberLong {
  private String $numberLong;

  public XNumberLong(Long nLong){
    if(nLong!=null){
      this.$numberLong= nLong.toString();
    }
  }

  public String get$numberLong() {
    return $numberLong;
  }

  public void set$numberLong(String $numberLong) {
    this.$numberLong = $numberLong;
  }
}
