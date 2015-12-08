package com.bcgogo.api;

/**
 * Created by XinyuQiu on 14-7-7.
 */
public class OBDSimOperationLogDTOSearchCondition {
  private int limit = 10;
  private int start = 0;
  private Long obdId;
  private Long simId;

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public Long getSimId() {
    return simId;
  }

  public void setSimId(Long simId) {
    this.simId = simId;
  }
}
