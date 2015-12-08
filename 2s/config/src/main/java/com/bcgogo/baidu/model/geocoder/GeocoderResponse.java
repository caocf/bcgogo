package com.bcgogo.baidu.model.geocoder;

import com.bcgogo.enums.baidu.GeocoderStatus;

/**
 * User: ZhangJuntao
 * Date: 13-8-5
 * Time: 下午3:47
 */
public class GeocoderResponse {
  private GeocoderStatus status;
  private GeocoderResult result;

  public boolean isSuccess() {
    return status == GeocoderStatus.OK && getResult() != null && getResult().getLocation() != null;
  }

  public GeocoderResponse() {
  }

  public GeocoderResponse(GeocoderStatus status) {
    this.status = status;
  }

  public GeocoderStatus getStatus() {
    return status;
  }

  public void setStatus(GeocoderStatus status) {
    this.status = status;
  }

  public GeocoderResult getResult() {
    return result;
  }

  public void setResult(GeocoderResult result) {
    this.result = result;
  }

  @Override
  public String toString() {
    return "GeocoderResponse{" +
        "status=" + status +
        ", result=" + result +
        '}';
  }


}
