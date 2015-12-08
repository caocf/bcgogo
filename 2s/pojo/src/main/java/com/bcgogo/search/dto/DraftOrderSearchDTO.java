package com.bcgogo.search.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-15
 * Time: 上午1:59
 * To change this template use File | Settings | File Templates.
 */
public class DraftOrderSearchDTO {
  private String draftOrderIdStr;
  private Long shopId;
  private Long userId;
  private String startTime;
  private String endTime;
  private String orderTypes[];
  private Pager pager;
  private String startPageNo;

  private Long vehicleId;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String[] getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(String[] orderTypes) {
    this.orderTypes = orderTypes;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(String startPageNo) {
    this.startPageNo = startPageNo;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public String getDraftOrderIdStr() {
    return draftOrderIdStr;
    }

  public void setDraftOrderIdStr(String draftOrderIdStr) {
    this.draftOrderIdStr = draftOrderIdStr;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public void convertOrderType(String[] orderTypes){
    if(StringUtil.isAllEmpty(this.getOrderTypes())){
      this.setOrderTypes(new String[]{"NONE"});
    }else if(this.getOrderTypes().length==1&&"ALL".equals(this.getOrderTypes()[0])){
      this.setOrderTypes(new String[]{"PURCHASE","INVENTORY","SALE", "RETURN","REPAIR","SALE_RETURN"});
    }
  }
}
