package com.bcgogo.api.response;

import com.bcgogo.api.ApiArea;
import com.bcgogo.api.ApiResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午4:08
 */
public class AreaResponse extends ApiResponse {
  private List<ApiArea> areaList = new ArrayList<ApiArea>();

  public AreaResponse() {
    super();
  }

  public AreaResponse(ApiResponse response) {
    super(response);
  }

  public List<ApiArea> getAreaList() {
    return areaList;
  }

  public void setAreaList(List<ApiArea> areaList) {
    this.areaList = areaList;
  }
}
