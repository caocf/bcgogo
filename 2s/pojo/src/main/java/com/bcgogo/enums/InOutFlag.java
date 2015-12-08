package com.bcgogo.enums;

/**
 * 资金出入标示位
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-14
 * Time: 上午9:47
 * To change this template use File | Settings | File Templates.
 */
public enum InOutFlag {

  INOUT(0L, "INOUT"),
  IN_FLAG(1l, "IN"),
  OUT_FLAG(2L, "OUT");

  public Long getCode() {
    return code;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  InOutFlag(Long code, String name) {
    this.code = code;
    this.name = name;
  }

  // 0入1出
  private Long code;
  private String name;

  public static InOutFlag getInOutFlagEnumByCode(Long code) {
    for (InOutFlag inOutFlag : InOutFlag.values()) {
      if (inOutFlag.getCode().equals(code)) {
        return inOutFlag;
      }
    }
    return null;
  }

}
