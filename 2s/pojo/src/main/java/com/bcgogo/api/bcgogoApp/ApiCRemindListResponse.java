package com.bcgogo.api.bcgogoApp;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
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
public class ApiCRemindListResponse extends ApiResponse {
  private List<CustomerServiceJobDTO> serviceJobDTOList = new ArrayList<CustomerServiceJobDTO>();

  private Pager pager = new Pager();

  public ApiCRemindListResponse() {
    super();
  }

  public ApiCRemindListResponse(ApiResponse response) {
    super(response);
  }

  public List<CustomerServiceJobDTO> getServiceJobDTOList() {
    return serviceJobDTOList;
  }

  public void setServiceJobDTOList(List<CustomerServiceJobDTO> serviceJobDTOList) {
    this.serviceJobDTOList = serviceJobDTOList;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  @Override
  public String toString() {
    return "ApiCRemindListResponse{" +
        "serviceJobDTOList=" + serviceJobDTOList +
        ", pager=" + pager +
        '}';
  }
}
