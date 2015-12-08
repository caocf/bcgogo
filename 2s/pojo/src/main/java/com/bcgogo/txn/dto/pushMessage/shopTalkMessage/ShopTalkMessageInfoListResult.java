package com.bcgogo.txn.dto.pushMessage.shopTalkMessage;

import com.bcgogo.common.Pager;
import com.bcgogo.txn.dto.pushMessage.ShopTalkMessageDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午4:03
 */
public class ShopTalkMessageInfoListResult {
  private long todayTotalRows = 0;
  private List<ShopTalkMessageDTO> todayShopTalkMessageInfoList = new ArrayList<ShopTalkMessageDTO>();

  private long yesterdayTotalRows = 0;
  private List<ShopTalkMessageDTO> yesterdayShopTalkMessageInfoList = new ArrayList<ShopTalkMessageDTO>();

  private long moreTotalRows = 0;
  private List<ShopTalkMessageDTO> moreShopTalkMessageInfoList = new ArrayList<ShopTalkMessageDTO>();

  private Pager pager;

  public ShopTalkMessageInfoListResult() {
  }

  public List<ShopTalkMessageDTO> getTodayShopTalkMessageInfoList() {
    return todayShopTalkMessageInfoList;
  }

  public void setTodayShopTalkMessageInfoList(List<ShopTalkMessageDTO> todayShopTalkMessageInfoList) {
    this.todayShopTalkMessageInfoList = todayShopTalkMessageInfoList;
  }

  public List<ShopTalkMessageDTO> getYesterdayShopTalkMessageInfoList() {
    return yesterdayShopTalkMessageInfoList;
  }

  public void setYesterdayShopTalkMessageInfoList(List<ShopTalkMessageDTO> yesterdayShopTalkMessageInfoList) {
    this.yesterdayShopTalkMessageInfoList = yesterdayShopTalkMessageInfoList;
  }

  public List<ShopTalkMessageDTO> getMoreShopTalkMessageInfoList() {
    return moreShopTalkMessageInfoList;
  }

  public void setMoreShopTalkMessageInfoList(List<ShopTalkMessageDTO> moreShopTalkMessageInfoList) {
    this.moreShopTalkMessageInfoList = moreShopTalkMessageInfoList;
  }

  public void computeMoreTotalRows(int totalRows) {
    moreTotalRows = totalRows - todayTotalRows - yesterdayTotalRows;
  }

  public ShopTalkMessageInfoListResult(Pager pager) {
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
