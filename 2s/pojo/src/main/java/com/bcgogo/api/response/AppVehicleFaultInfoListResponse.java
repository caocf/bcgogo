package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.common.Pager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-28
 * Time: 下午2:34
 */
public class AppVehicleFaultInfoListResponse extends ApiResponse {
  private List<AppVehicleFaultInfoDTO> result;
  private Pager pager;

  public AppVehicleFaultInfoListResponse() {
     super();
   }

  public AppVehicleFaultInfoListResponse(ApiResponse response) {
    super(response);
  }

  public List<AppVehicleFaultInfoDTO> getResult() {
    return result;
  }

  public void setResult(List<AppVehicleFaultInfoDTO> result) {
    this.result = result;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }
}
