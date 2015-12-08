package com.bcgogo.api.bcgogoApp;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.ShopFaultInfoListResult;

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
public class ApiVFaultInfoListResponse extends ApiResponse {
  private List<FaultInfoToShopDTO> faultInfoToShopDTOList = new ArrayList<FaultInfoToShopDTO>();

  private Pager pager = new Pager();

  public ApiVFaultInfoListResponse() {
    super();
  }

  public ApiVFaultInfoListResponse(ApiResponse response) {
    super(response);
  }

  public List<FaultInfoToShopDTO> getFaultInfoToShopDTOList() {
    return faultInfoToShopDTOList;
  }

  public void setFaultInfoToShopDTOList(List<FaultInfoToShopDTO> faultInfoToShopDTOList) {
    this.faultInfoToShopDTOList = faultInfoToShopDTOList;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }


  @Override
  public String toString() {
    return "ApiVFaultInfoListResponse{" +
        "faultInfoToShopDTOList=" + faultInfoToShopDTOList +
        ", pager=" + pager +
        '}';
  }
}
