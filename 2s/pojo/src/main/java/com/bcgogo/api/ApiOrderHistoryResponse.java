package com.bcgogo.api;

import com.bcgogo.utils.NumberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 获得单据历史信息返回封装
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
public class ApiOrderHistoryResponse extends ApiResponse {

  private Integer unFinishedServiceCount = 0;//未完成服务数量    int
  private Integer finishedServiceCount = 0;//已完成服务数量      int
  private List<AppOrderDTO> unFinishedServiceList = new ArrayList<AppOrderDTO>();//未完成的服务
  private List<AppOrderDTO> finishedServiceList = new ArrayList<AppOrderDTO>();//已完成的服务
  private Double finishedServiceTotal = 0D;//已完成服务的总额

  public Integer getUnFinishedServiceCount() {
    return unFinishedServiceCount;
  }

  public void setUnFinishedServiceCount(Integer unFinishedServiceCount) {
    this.unFinishedServiceCount = unFinishedServiceCount;
  }

  public Integer getFinishedServiceCount() {
    return finishedServiceCount;
  }

  public void setFinishedServiceCount(Integer finishedServiceCount) {
    this.finishedServiceCount = finishedServiceCount;
  }

  public List<AppOrderDTO> getUnFinishedServiceList() {
    return unFinishedServiceList;
  }

  public void setUnFinishedServiceList(List<AppOrderDTO> unFinishedServiceList) {
    this.unFinishedServiceList = unFinishedServiceList;
  }

  public List<AppOrderDTO> getFinishedServiceList() {
    return finishedServiceList;
  }

  public void setFinishedServiceList(List<AppOrderDTO> finishedServiceList) {
    this.finishedServiceList = finishedServiceList;
  }

  public ApiOrderHistoryResponse() {
    super();
  }

  public ApiOrderHistoryResponse(ApiResponse response) {
    super(response);
  }

  public Double getFinishedServiceTotal() {
    return finishedServiceTotal;
  }

  public void setFinishedServiceTotal(Double finishedServiceTotal) {
    this.finishedServiceTotal = NumberUtil.round(finishedServiceTotal);
  }
}
