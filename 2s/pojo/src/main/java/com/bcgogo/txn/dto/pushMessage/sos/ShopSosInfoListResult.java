package com.bcgogo.txn.dto.pushMessage.sos;

import com.bcgogo.api.RescueDTO;
import com.bcgogo.common.Pager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午4:03
 */
public class ShopSosInfoListResult {
  private long todayTotalRows = 0;
  private List<RescueDTO> todayShopSosInfoList = new ArrayList<RescueDTO>();

  private long yesterdayTotalRows = 0;
  private List<RescueDTO> yesterdayShopSosInfoList = new ArrayList<RescueDTO>();

  private long moreTotalRows = 0;
  private List<RescueDTO> moreShopSosInfoList = new ArrayList<RescueDTO>();

  private Pager pager;

  public ShopSosInfoListResult() {
  }

  public List<RescueDTO> getTodayShopSosInfoList() {
    return todayShopSosInfoList;
  }

  public void setTodayShopSosInfoList(List<RescueDTO> todayShopSosInfoList) {
    this.todayShopSosInfoList = todayShopSosInfoList;
  }

  public List<RescueDTO> getYesterdayShopSosInfoList() {
    return yesterdayShopSosInfoList;
  }

  public void setYesterdayShopSosInfoList(List<RescueDTO> yesterdayShopSosInfoList) {
    this.yesterdayShopSosInfoList = yesterdayShopSosInfoList;
  }

  public List<RescueDTO> getMoreShopSosInfoList() {
    return moreShopSosInfoList;
  }

  public void setMoreShopSosInfoList(List<RescueDTO> moreShopSosInfoList) {
    this.moreShopSosInfoList = moreShopSosInfoList;
  }

  public void computeMoreTotalRows(int totalRows) {
    moreTotalRows = totalRows - todayTotalRows - yesterdayTotalRows;
  }

  public ShopSosInfoListResult(Pager pager) {
    this.pager = pager;
  }

  public long getTodayTotalRows() {
    return todayTotalRows;
  }

  public void setTodayTotalRows(long todayTotalRows) {
    this.todayTotalRows = todayTotalRows;
  }



  public long getYesterdayTotalRows() {
    return yesterdayTotalRows;
  }

  public void setYesterdayTotalRows(long yesterdayTotalRows) {
    this.yesterdayTotalRows = yesterdayTotalRows;
  }



  public long getMoreTotalRows() {
    return moreTotalRows;
  }

  public void setMoreTotalRows(long moreTotalRows) {
    this.moreTotalRows = moreTotalRows;
  }



  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

}
