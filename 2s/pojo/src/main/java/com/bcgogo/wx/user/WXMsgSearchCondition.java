package com.bcgogo.wx.user;

import com.bcgogo.common.Pager;
import com.bcgogo.wx.WXMsgStatus;
import com.bcgogo.wx.message.WXMCategory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-17
 * Time: 下午2:48
 */
public class WXMsgSearchCondition {
  private Long shopId;
  private String title;
  private String description;
  private Long startSendTime;
  private Long endSendTime;
  private WXMCategory[] categoryList;
  private WXMsgStatus[] statusList;
  private Pager pager;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public WXMCategory[] getCategoryList() {
    return categoryList;
  }

  public void setCategoryList(WXMCategory[] categoryList) {
    this.categoryList = categoryList;
  }

  public WXMsgStatus[] getStatusList() {
    return statusList;
  }

  public void setStatusList(WXMsgStatus[] statusList) {
    this.statusList = statusList;
  }

  public Long getStartSendTime() {
    return startSendTime;
  }

  public void setStartSendTime(Long startSendTime) {
    this.startSendTime = startSendTime;
  }

  public Long getEndSendTime() {
    return endSendTime;
  }

  public void setEndSendTime(Long endSendTime) {
    this.endSendTime = endSendTime;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }
}
