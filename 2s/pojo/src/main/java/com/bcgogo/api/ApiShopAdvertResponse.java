package com.bcgogo.api;

import com.bcgogo.common.Pager;
import com.bcgogo.txn.dto.AdvertDTO;

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
public class ApiShopAdvertResponse extends ApiResponse {
  private List<AdvertDTO> advertDTOList = new ArrayList<AdvertDTO>();

  private Pager pager = new Pager();

  public ApiShopAdvertResponse() {
    super();
  }

  public ApiShopAdvertResponse(ApiResponse response) {
    super(response);
  }

  public List<AdvertDTO> getAdvertDTOList() {
    return advertDTOList;
  }

  public void setAdvertDTOList(List<AdvertDTO> advertDTOList) {
    this.advertDTOList = advertDTOList;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  @Override
  public String toString() {
    return "ApiShopAdvertResponse{" +
        "advertDTOList=" + advertDTOList +
        '}';
  }
}
