package com.bcgogo.pojox.config;

import com.bcgogo.pojox.util.CollectionUtil;

import java.util.List;

/**
 * 坐标转换
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 14-5-19
 * Time: 上午10:06
 * To change this template use File | Settings | File Templates.
 */
public class CoordinateConvertResponse {
  private String status;
  private List<Coordinate> result;

  public boolean isSuccess() {
    return "0".equals(getStatus()) && getResult() != null && CollectionUtil.isNotEmpty(result);
  }

  public List<Coordinate> getResult() {
    return result;
  }

  public void setResult(List<Coordinate> result) {
    this.result = result;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
