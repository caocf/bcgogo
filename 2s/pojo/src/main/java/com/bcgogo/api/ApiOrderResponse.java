package com.bcgogo.api;

/**
 * 获得单据信息返回封装
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
public class ApiOrderResponse extends ApiResponse {
  private AppOrderDTO serviceDetail;

  public ApiOrderResponse() {
    super();
  }

  public ApiOrderResponse(ApiResponse response) {
    super(response);
  }

  public AppOrderDTO getServiceDetail() {
    return serviceDetail;
  }

  public void setServiceDetail(AppOrderDTO serviceDetail) {
    this.serviceDetail = serviceDetail;
  }

  @Override
  public String toString() {
    return "ApiOrderResponse{" +
        "serviceDetail=" + serviceDetail == null?"":serviceDetail.toString() +
        '}';
  }
}
