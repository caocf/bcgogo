package com.bcgogo.user.dto;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.VehicleStatus;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-10-28
 * Time: 上午10:36
 * To change this template use File | Settings | File Templates.
 */
public class CustomerVehicleDTO implements Serializable {

  private Long id;
  private Long customerId;
  private Long vehicleId;
  private Long maintainTime;   //预约保养时间
  private String maintainTimeStr;
  private Long insureTime;      //预约保险时间
  private String insureTimeStr;
  private Long examineTime;    //预约验车时间
  private String examineTimeStr;
   private VehicleStatus status;
  private VehicleDTO vehicleDTO;
  private Long maintainMileage;//保养里程
  private Double totalConsume; //累计消费 实收+欠款
  private Long consumeTimes;  //消费次数

  private Long lastExpenditureDate;//车辆的上次消费时间
  private OrderTypes lastOrderType; //车辆的上次消费单据类型
  private Long lastOrderId;//车辆的上次消费单据id

  private Double lastMaintainMileage;//上次保养里程
  private Long lastMaintainTime;//上次保养时间
  private String lastMaintainTimeStr;//上次保养时间

  private Double maintainMileagePeriod;//保养里程周期
  private Long maintainTimePeriod;//保养时间周期
  private long maintainTimePeriodStr;//保养时间里周期
  private Double nextMaintainMileageAccess;//距下次保养里程


  public String getLastMaintainTimeStr() {
    return lastMaintainTimeStr;
  }

  public void setLastMaintainTimeStr(String lastMaintainTimeStr) {
    this.lastMaintainTimeStr = lastMaintainTimeStr;
  }

  public long getMaintainTimePeriodStr() {
    return maintainTimePeriodStr;
  }

  public void setMaintainTimePeriodStr(long maintainTimePeriodStr) {
    this.maintainTimePeriodStr = maintainTimePeriodStr;
  }

  public Long getMaintainTime() {
    return maintainTime;
  }

  public void setMaintainTime(Long maintainTime) {
    this.maintainTime = maintainTime;
  }

  public Long getInsureTime() {
    return insureTime;
  }

  public void setInsureTime(Long insureTime) {
    this.insureTime = insureTime;
  }

  public Long getExamineTime() {
    return examineTime;
  }

  public void setExamineTime(Long examineTime) {
    this.examineTime = examineTime;
  }

  public CustomerVehicleDTO() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCustomerId() {
    return this.customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public VehicleStatus getStatus() {
    return status;
  }

  public void setStatus(VehicleStatus status) {
    this.status = status;
  }

  public VehicleDTO getVehicleDTO() {
    return vehicleDTO;
  }

  public void setVehicleDTO(VehicleDTO vehicleDTO) {
    this.vehicleDTO = vehicleDTO;
  }

  public String getMaintainTimeStr() {
    return maintainTimeStr;
  }

  public void setMaintainTimeStr(String maintainTimeStr) {
    this.maintainTimeStr = maintainTimeStr;
  }

  public String getExamineTimeStr() {
    return examineTimeStr;
  }

  public void setExamineTimeStr(String examineTimeStr) {
    this.examineTimeStr = examineTimeStr;
  }

  public String getInsureTimeStr() {
    return insureTimeStr;
  }

  public void setInsureTimeStr(String insureTimeStr) {
    this.insureTimeStr = insureTimeStr;
  }

  public Long getMaintainMileage() {
    return maintainMileage;
  }

  public void setMaintainMileage(Long maintainMileage) {
    this.maintainMileage = maintainMileage;
  }

  public Double getTotalConsume() {
    return totalConsume;
  }

  public void setTotalConsume(Double totalConsume) {
    this.totalConsume = totalConsume;
  }


  public Long getConsumeTimes() {
    return consumeTimes;
  }

  public void setConsumeTimes(Long consumeTimes) {
    this.consumeTimes = consumeTimes;
  }

  public void calculateVehicleConsume(ReceivableDTO receivableDTO,boolean isRepeal) {
    this.setLastExpenditureDate(receivableDTO.getVestDate());
    this.setLastOrderType(receivableDTO.getOrderType());
    this.setLastOrderId(receivableDTO.getOrderId());
    if (isRepeal) {
      this.setTotalConsume(NumberUtil.doubleVal(this.getTotalConsume()) - receivableDTO.getSettledAmount() - receivableDTO.getDebt());
      this.setConsumeTimes(NumberUtil.longValue(this.getConsumeTimes()) - 1);
      return;
    }
    this.setConsumeTimes(NumberUtil.longValue(this.getConsumeTimes()) + 1);
    this.setTotalConsume(NumberUtil.doubleVal(this.getTotalConsume()) + receivableDTO.getSettledAmount() + receivableDTO.getDebt());
  }

  public Long getLastExpenditureDate() {
    return lastExpenditureDate;
  }

  public void setLastExpenditureDate(Long lastExpenditureDate) {
    this.lastExpenditureDate = lastExpenditureDate;
  }

  public OrderTypes getLastOrderType() {
    return lastOrderType;
  }

  public void setLastOrderType(OrderTypes lastOrderType) {
    this.lastOrderType = lastOrderType;
  }

  public Long getLastOrderId() {
    return lastOrderId;
  }

  public void setLastOrderId(Long lastOrderId) {
    this.lastOrderId = lastOrderId;
  }

  public Double getLastMaintainMileage() {
    return lastMaintainMileage;
  }

  public void setLastMaintainMileage(Double lastMaintainMileage) {
    this.lastMaintainMileage = lastMaintainMileage;
  }

  public Double getMaintainMileagePeriod() {
    return maintainMileagePeriod;
  }

  public void setMaintainMileagePeriod(Double maintainMileagePeriod) {
    this.maintainMileagePeriod = maintainMileagePeriod;
  }

  public Long getLastMaintainTime() {
    return lastMaintainTime;
  }

  public void setLastMaintainTime(Long lastMaintainTime) {
    this.lastMaintainTime = lastMaintainTime;
    if(lastMaintainTime != null){
      this.setLastMaintainTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY,lastMaintainTime));
    }
  }

  public Long getMaintainTimePeriod() {
    return maintainTimePeriod;
  }

  public void setMaintainTimePeriod(Long maintainTimePeriod) {
    this.maintainTimePeriod = maintainTimePeriod;
    if (maintainTimePeriod != null) {
      this.setMaintainTimePeriodStr(maintainTimePeriod);
    }
  }

  public Double getNextMaintainMileageAccess() {
    return nextMaintainMileageAccess;
  }

  public void setNextMaintainMileageAccess(Double nextMaintainMileageAccess) {
    this.nextMaintainMileageAccess = nextMaintainMileageAccess;
  }
}
