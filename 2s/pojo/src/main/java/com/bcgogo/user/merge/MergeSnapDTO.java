package com.bcgogo.user.merge;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-15
 * Time: 上午1:49
 * To change this template use File | Settings | File Templates.
 */
public class MergeSnapDTO {
  private Long shopId;
  private Long childId;
  private String parentId;
  private Long mergeManId;
  private Long mergeMan;
  private Long mergeDate;
  private String txnInfo;
  private String userInfo;
  private String configInfo;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getChildId() {
    return childId;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }

  public Long getMergeManId() {
    return mergeManId;
  }

  public void setMergeManId(Long mergeManId) {
    this.mergeManId = mergeManId;
  }

  public Long getMergeMan() {
    return mergeMan;
  }

  public void setMergeMan(Long mergeMan) {
    this.mergeMan = mergeMan;
  }

  public Long getMergeDate() {
    return mergeDate;
  }

  public void setMergeDate(Long mergeDate) {
    this.mergeDate = mergeDate;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getTxnInfo() {
    return txnInfo;
  }

  public void setTxnInfo(String txnInfo) {
    this.txnInfo = txnInfo;
  }

  public String getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(String userInfo) {
    this.userInfo = userInfo;
  }

  public String getConfigInfo() {
    return configInfo;
  }

  public void setConfigInfo(String configInfo) {
    this.configInfo = configInfo;
  }
}
