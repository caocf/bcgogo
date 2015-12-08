package com.bcgogo.txn.dto.pushMessage.mileage;

import com.bcgogo.enums.user.MileageType;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午12:00
 */
public class MileageInfoSearchConditionDTO {
  private List<Long> ids = new ArrayList<Long>();
  private Long shopId;          //查询店铺的id
  private int maxRows = 10;
  private int startPageNo = 1;
  private String vehicleNo;
  private String status;
  private Long timeStart;
  private Long timeEnd;
  private MileageType mileageType;

  public MileageType getMileageType() {
    return mileageType;
  }

  public void setMileageType(MileageType mileageType) {
    this.mileageType = mileageType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }

}
