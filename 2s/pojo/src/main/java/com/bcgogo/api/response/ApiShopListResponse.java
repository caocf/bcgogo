package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppShopDTO;
import com.bcgogo.common.Pager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午4:08
 */
public class ApiShopListResponse extends ApiResponse {
  private List<AppShopDTO> shopList = new ArrayList<AppShopDTO>();
  private Pager pager;
  private AppShopDTO shop;
  private Long obdSellerShopId = null;

  public ApiShopListResponse() {
    super();
  }

  public ApiShopListResponse(ApiResponse response) {
    super(response);
  }

  public ApiShopListResponse(ApiResponse response, Long obdSellerShopId) {
    super(response);
    this.obdSellerShopId = obdSellerShopId;
  }

  public List<AppShopDTO> getShopList() {
    return shopList;
  }

  public void setShopList(List<AppShopDTO> shopList) {
    this.shopList = shopList;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public AppShopDTO getShop() {
    return shop;
  }

  public void setShop(AppShopDTO shop) {
    this.shop = shop;
  }

  public Long getObdSellerShopId() {
    return obdSellerShopId;
  }

  public void setObdSellerShopId(Long obdSellerShopId) {
    this.obdSellerShopId = obdSellerShopId;
  }
}
