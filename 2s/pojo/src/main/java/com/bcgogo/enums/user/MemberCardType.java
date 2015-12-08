package com.bcgogo.enums.user;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-9
 * Time: 下午4:36
 * To change this template use File | Settings | File Templates.
 */
public enum MemberCardType {
  TIMES_CARD("TIMES_CARD"),
  STORED_CARD("STORED_CARD");

  String type;

  MemberCardType(String type){
    this.type = type;
  }

  public String getType(){
    return this.type;
  }
}
