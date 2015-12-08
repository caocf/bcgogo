package com.bcgogo.api.vehicleDataHandle;

/**
 * Created by XinyuQiu on 14-10-23.
 */
public class AlarmInfoPlate {
  private String serialno;
  private Integer channel;
  private AlarmInfoPlateResult result;

  public String getSerialno() {
    return serialno;
  }

  public void setSerialno(String serialno) {
    this.serialno = serialno;
  }

  public Integer getChannel() {
    return channel;
  }

  public void setChannel(Integer channel) {
    this.channel = channel;
  }

  public AlarmInfoPlateResult getResult() {
    return result;
  }

  public void setResult(AlarmInfoPlateResult result) {
    this.result = result;
  }
}
