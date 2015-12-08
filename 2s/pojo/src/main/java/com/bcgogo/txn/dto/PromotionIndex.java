package com.bcgogo.txn.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.PromotionsEnum;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-8-23
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public class PromotionIndex extends PromotionsDTO{
  private Long [] promotionsIdList;
  private PromotionsEnum.PromotionStatus[] promotionStatusList; //查询用
  private String sortFiled;
  private String currentSort;
  private Pager pager;
  private int startPageNo;

  private boolean addPromotionsProductFlag;

  public Long[] getPromotionsIdList() {
    return promotionsIdList;
  }

  public void setPromotionsIdList(Long[] promotionsIdList) {
    this.promotionsIdList = promotionsIdList;
  }

  public String getSortFiled() {
    return sortFiled;
  }

  public void setSortFiled(String sortFiled) {
    this.sortFiled = sortFiled;
  }

  public PromotionsEnum.PromotionStatus[] getPromotionStatusList() {
    return promotionStatusList;
  }

  public void setPromotionStatusList(PromotionsEnum.PromotionStatus[] promotionStatusList) {
    this.promotionStatusList = promotionStatusList;
  }

  public boolean isAddPromotionsProductFlag() {
    return addPromotionsProductFlag;
  }

  public void setAddPromotionsProductFlag(boolean addPromotionsProductFlag) {
    this.addPromotionsProductFlag = addPromotionsProductFlag;
  }
  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public String getCurrentSort() {
    return currentSort;
  }

  public void setCurrentSort(String currentSort) {
    this.currentSort = currentSort;
  }


}
