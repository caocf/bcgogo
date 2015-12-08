package com.bcgogo.txn.dto.pushMessage.impact;

import com.bcgogo.common.Pager;
import com.bcgogo.etl.ImpactVideoExpDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午4:03
 */
public class ShopImpactInfoListResult {
  private long todayTotalRows = 0;
  private List<ImpactVideoExpDTO> todayShopImpactInfoList = new ArrayList<ImpactVideoExpDTO>();

  private long yesterdayTotalRows = 0;
  private List<ImpactVideoExpDTO> yesterdayShopImpactInfoList = new ArrayList<ImpactVideoExpDTO>();

  private long moreTotalRows = 0;
  private List<ImpactVideoExpDTO> moreShopImpactInfoList = new ArrayList<ImpactVideoExpDTO>();

  private Pager pager;

  public ShopImpactInfoListResult() {
  }

  public List<ImpactVideoExpDTO> getTodayShopImpactInfoList() {
    return todayShopImpactInfoList;
  }

  public void setTodayShopImpactInfoList(List<ImpactVideoExpDTO> todayShopImpactInfoList) {
    this.todayShopImpactInfoList = todayShopImpactInfoList;
  }

  public List<ImpactVideoExpDTO> getYesterdayShopImpactInfoList() {
    return yesterdayShopImpactInfoList;
  }

  public void setYesterdayShopImpactInfoList(List<ImpactVideoExpDTO> yesterdayShopImpactInfoList) {
    this.yesterdayShopImpactInfoList = yesterdayShopImpactInfoList;
  }

  public List<ImpactVideoExpDTO> getMoreShopImpactInfoList() {
    return moreShopImpactInfoList;
  }

  public void setMoreShopImpactInfoList(List<ImpactVideoExpDTO> moreShopImpactInfoList) {
    this.moreShopImpactInfoList = moreShopImpactInfoList;
  }

  public void computeMoreTotalRows(int totalRows) {
    moreTotalRows = totalRows - todayTotalRows - yesterdayTotalRows;
  }

  public ShopImpactInfoListResult(Pager pager) {
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
