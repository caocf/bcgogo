package com.bcgogo.txn.dto.pushMessage.faultCode;

import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.enums.YesNo;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午12:00
 */
public class FaultInfoSearchConditionDTO {
  private List<Long> ids = new ArrayList<Long>();
  private Long shopId;          //查询店铺的id
  private int maxRows = 10;
  private int startPageNo = 1;
  private String vehicleNo;
  private List<String> vehicleNoList = new ArrayList<String>();
  private String mobile;
  private List<String> mobiles;
  private YesNo isSendMessage;
  private YesNo isCreateAppointOrder;
  private YesNo isUntreated;
  private YesNo isDeleted;

  private Long timeStart;
  private Long timeEnd;
  private FaultAlertType faultAlertType;
  private String code;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public boolean hasState() {
    return isUntreated != null || isCreateAppointOrder != null || isSendMessage != null || isDeleted != null;
  }

  public Long getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(Long timeStart) {
    this.timeStart = timeStart;
  }

  public Long getTimeEnd() {
    return timeEnd;
  }

  public void setTimeEnd(Long timeEnd) {
    this.timeEnd = timeEnd;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public YesNo getIsSendMessage() {
    return isSendMessage;
  }

  public void setIsSendMessage(YesNo isSendMessage) {
    this.isSendMessage = isSendMessage;
  }

  public YesNo getIsCreateAppointOrder() {
    return isCreateAppointOrder;
  }

  public void setIsCreateAppointOrder(YesNo isCreateAppointOrder) {
    this.isCreateAppointOrder = isCreateAppointOrder;
  }

  public List<String> getVehicleNoList() {
    return vehicleNoList;
  }

  public void setVehicleNoList(List<String> vehicleNoList) {
    this.vehicleNoList = vehicleNoList;
  }

  public YesNo getIsUntreated() {
    return isUntreated;
  }

  public void setIsUntreated(YesNo isUntreated) {
    this.isUntreated = isUntreated;
  }

  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

  public List<String> getMobiles() {
    return mobiles;
  }

  public void setMobiles(List<String> mobiles) {
    this.mobiles = mobiles;
  }

    public FaultAlertType getFaultAlertType() {
        return faultAlertType;
    }

    public void setFaultAlertType(FaultAlertType faultAlertType) {
        this.faultAlertType = faultAlertType;
    }

  public YesNo getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(YesNo isDeleted) {
    this.isDeleted = isDeleted;
  }
}
