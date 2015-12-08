package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-19
 * Time: 下午8:04
 */
public class ApiDriveLogsResponse extends ApiResponse {
  private List<DriveLogDTO> driveLogDTOs;
  private Double worstOilWear;
  private Double bestOilWear;
  private Long subtotalTravelTime;//小计行程时间 （秒为单位）
  private Double subtotalDistance;//小计路程  （千米）
  private Double subtotalOilMoney;//小计油钱
  private Double subtotalOilWear;//小计平均油耗
  private Double totalOilWear;//总平均油耗
  private Double subtotalOilCost;//小计耗油量

  public ApiDriveLogsResponse() {
    super();
  }

  public ApiDriveLogsResponse(ApiResponse response) {
    super(response);
  }

  public ApiDriveLogsResponse(ApiResponse response, List<DriveLogDTO> driveLogDTOs) {
    super(response);
    setDriveLogDTOs(driveLogDTOs);
    subtotalTravelTime = 0l;
    subtotalDistance = 0d;
    subtotalOilMoney = 0d;
    subtotalOilCost = 0d;
    subtotalOilWear = 0d;
    if (CollectionUtils.isNotEmpty(driveLogDTOs)) {
      for (DriveLogDTO driveLogDTO : driveLogDTOs) {
        if (driveLogDTO != null) {
          subtotalTravelTime += NumberUtil.longValue(driveLogDTO.getTravelTime());
          subtotalDistance += NumberUtil.doubleVal(driveLogDTO.getDistance());
          subtotalOilMoney += NumberUtil.doubleVal(driveLogDTO.getTotalOilMoney());
          subtotalOilCost += NumberUtil.doubleVal(driveLogDTO.getOilCost());
        }
      }
    }
    if (subtotalDistance > 0) {
      subtotalOilWear = subtotalOilCost / subtotalDistance * 100;
    }
  }

  public List<DriveLogDTO> getDriveLogDTOs() {
    return driveLogDTOs;
  }

  public void setDriveLogDTOs(List<DriveLogDTO> driveLogDTOs) {
    this.driveLogDTOs = driveLogDTOs;
  }

  public Double getWorstOilWear() {
    return worstOilWear;
  }

  public void setWorstOilWear(Double worstOilWear) {
    this.worstOilWear = worstOilWear;
  }

  public Double getBestOilWear() {
    return bestOilWear;
  }

  public void setBestOilWear(Double bestOilWear) {
    this.bestOilWear = bestOilWear;
  }

  public Long getSubtotalTravelTime() {
    return subtotalTravelTime;
  }

  public void setSubtotalTravelTime(Long subtotalTravelTime) {
    this.subtotalTravelTime = subtotalTravelTime;
  }

  public Double getSubtotalDistance() {
    return subtotalDistance;
  }

  public void setSubtotalDistance(Double subtotalDistance) {
    this.subtotalDistance = subtotalDistance;
  }

  public Double getSubtotalOilMoney() {
    return subtotalOilMoney;
  }

  public void setSubtotalOilMoney(Double subtotalOilMoney) {
    this.subtotalOilMoney = subtotalOilMoney;
  }

  public Double getTotalOilWear() {
    return totalOilWear;
  }

  public void setTotalOilWear(Double totalOilWear) {
    this.totalOilWear = totalOilWear;
  }

  public Double getSubtotalOilCost() {
    return subtotalOilCost;
  }

  public void setSubtotalOilCost(Double subtotalOilCost) {
    this.subtotalOilCost = subtotalOilCost;
  }

  public Double getSubtotalOilWear() {
    return subtotalOilWear;
  }

  public void setSubtotalOilWear(Double subtotalOilWear) {
    this.subtotalOilWear = subtotalOilWear;
  }
}
