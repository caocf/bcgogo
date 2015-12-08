package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-5-24
 * Time: 上午11:58
 */
public enum MemberOrderType {
  NEW("购卡"),
  RENEW("续卡");

  private final String name;

  private MemberOrderType(String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
