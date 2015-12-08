package com.bcgogo.txn.dto.pushMessage.faultCode;

import com.bcgogo.common.Pager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午4:03
 */
public class ShopFaultInfoListResult {
  private long todayTotalRows = 0;
  private List<FaultInfoToShopDTO> todayShopFaultInfoList = new ArrayList<FaultInfoToShopDTO>();

  private long yesterdayTotalRows = 0;
  private List<FaultInfoToShopDTO> yesterdayShopFaultInfoList = new ArrayList<FaultInfoToShopDTO>();

  private long moreTotalRows = 0;
  private List<FaultInfoToShopDTO> moreShopFaultInfoList = new ArrayList<FaultInfoToShopDTO>();

  private Pager pager;

  public ShopFaultInfoListResult() {
  }

  public void computeMoreTotalRows(int totalRows) {
    moreTotalRows = totalRows - todayTotalRows - yesterdayTotalRows;
  }

  public ShopFaultInfoListResult(Pager pager) {
    this.pager = pager;
  }

  public long getTodayTotalRows() {
    return todayTotalRows;
  }

  public void setTodayTotalRows(long todayTotalRows) {
    this.todayTotalRows = todayTotalRows;
  }

  public List<FaultInfoToShopDTO> getTodayShopFaultInfoList() {
    return todayShopFaultInfoList;
  }

  public void setTodayShopFaultInfoList(List<FaultInfoToShopDTO> todayShopFaultInfoList) {
    this.todayShopFaultInfoList = todayShopFaultInfoList;
  }

  public long getYesterdayTotalRows() {
    return yesterdayTotalRows;
  }

  public void setYesterdayTotalRows(long yesterdayTotalRows) {
    this.yesterdayTotalRows = yesterdayTotalRows;
  }

  public List<FaultInfoToShopDTO> getYesterdayShopFaultInfoList() {
    return yesterdayShopFaultInfoList;
  }

  public void setYesterdayShopFaultInfoList(List<FaultInfoToShopDTO> yesterdayShopFaultInfoList) {
    this.yesterdayShopFaultInfoList = yesterdayShopFaultInfoList;
  }

  public long getMoreTotalRows() {
    return moreTotalRows;
  }

  public void setMoreTotalRows(long moreTotalRows) {
    this.moreTotalRows = moreTotalRows;
  }

  public List<FaultInfoToShopDTO> getMoreShopFaultInfoList() {
    return moreShopFaultInfoList;
  }

  public void setMoreShopFaultInfoList(List<FaultInfoToShopDTO> moreShopFaultInfoList) {
    this.moreShopFaultInfoList = moreShopFaultInfoList;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

}
