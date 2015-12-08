package com.bcgogo.baidu.model.geocoder;

import com.bcgogo.enums.baidu.GeocoderStatus;

/**
 * Created by XinyuQiu on 14-3-28.
 *  0	正常
 *  1	服务器内部错误
 *  2	请求参数非法
 *  3	权限校验失败
 *  4	配额校验失败
 *  5	ak不存在或者非法
 *  101	服务禁用
 *  102	不通过白名单或者安全码不对
 *  2xx	无权限
 *  3xx	配额错误
 *
 */
public class GeocoderResponseV2 {
  private String status;
  private GeocoderResult result;

  public boolean isSuccess() {
    return "0".equals(getStatus()) && getResult() != null && getResult().getLocation() != null;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public GeocoderResult getResult() {
    return result;
  }

  public void setResult(GeocoderResult result) {
    this.result = result;
  }
}
