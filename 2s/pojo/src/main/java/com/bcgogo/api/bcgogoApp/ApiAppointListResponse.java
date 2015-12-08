package com.bcgogo.api.bcgogoApp;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.txn.dto.AppointOrderDTO;
import com.bcgogo.user.dto.CustomerServiceJobDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 获得单据信息返回封装
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
public class ApiAppointListResponse extends ApiResponse {
  private List<AppointOrderDTO> appointOrderDTOList = new ArrayList<AppointOrderDTO>();

  private Pager pager = new Pager();

  public ApiAppointListResponse() {
    super();
  }

  public ApiAppointListResponse(ApiResponse response) {
    super(response);
  }

  public List<AppointOrderDTO> getAppointOrderDTOList() {
    return appointOrderDTOList;
  }

  public void setAppointOrderDTOList(List<AppointOrderDTO> appointOrderDTOList) {
    this.appointOrderDTOList = appointOrderDTOList;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  @Override
  public String toString() {
    return "ApiAppointListResponse{" +
        "appointOrderDTOList=" + appointOrderDTOList +
        ", pager=" + pager +
        '}';
  }
}
