package com.bcgogo.txn.dto.pushMessage.mileage;

import com.bcgogo.api.MileageDTO;
import com.bcgogo.common.Pager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午4:03
 */
public class ShopMileageInfoListResult {
  private long todayTotalRows = 0;
  private List<MileageDTO> todayShopMileageInfoList = new ArrayList<MileageDTO>();

  private long yesterdayTotalRows = 0;
  private List<MileageDTO> yesterdayShopMileageInfoList = new ArrayList<MileageDTO>();

  private long moreTotalRows = 0;
  private List<MileageDTO> moreShopMileageInfoList = new ArrayList<MileageDTO>();

  private Pager pager;

  public ShopMileageInfoListResult() {
  }

  public List<MileageDTO> getTodayShopMileageInfoList() {
    return todayShopMileageInfoList;
  }

  public void setTodayShopMileageInfoList(List<MileageDTO> todayShopMileageInfoList) {
    this.todayShopMileageInfoList = todayShopMileageInfoList;
  }

  public List<MileageDTO> getYesterdayShopMileageInfoList() {
    return yesterdayShopMileageInfoList;
  }

  public void setYesterdayShopMileageInfoList(List<MileageDTO> yesterdayShopMileageInfoList) {
    this.yesterdayShopMileageInfoList = yesterdayShopMileageInfoList;
  }

  public List<MileageDTO> getMoreShopMileageInfoList() {
    return moreShopMileageInfoList;
  }

  public void setMoreShopMileageInfoList(List<MileageDTO> moreShopMileageInfoList) {
    this.moreShopMileageInfoList = moreShopMileageInfoList;
  }

  public void computeMoreTotalRows(int totalRows) {
    moreTotalRows = totalRows - todayTotalRows - yesterdayTotalRows;
  }

  public ShopMileageInfoListResult(Pager pager) {
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
