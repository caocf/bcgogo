package com.bcgogo.user.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.VehicleStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Xiao Jian
 * Date: 11-10-24
 */

@Entity
@Table(name = "customer_vehicle")
public class CustomerVehicle extends LongIdentifier implements Cloneable{

  public CustomerVehicle() {
  }

  public CustomerVehicle(CustomerVehicleDTO customerVehicleDTO) {
    this.setId(customerVehicleDTO.getId());
    this.setCustomerId(customerVehicleDTO.getCustomerId());
    this.setVehicleId(customerVehicleDTO.getVehicleId());
    this.setMaintainTime(customerVehicleDTO.getMaintainTime());
    this.setInsureTime(customerVehicleDTO.getInsureTime());
    this.setExamineTime(customerVehicleDTO.getExamineTime());
    this.setMaintainMileage(customerVehicleDTO.getMaintainMileage());
    this.setTotalConsume(NumberUtil.toReserve(customerVehicleDTO.getTotalConsume(), NumberUtil.MONEY_PRECISION));
    this.setConsumeTimes(NumberUtil.longValue(customerVehicleDTO.getConsumeTimes()));

    this.setLastExpenditureDate(customerVehicleDTO.getLastExpenditureDate());
    this.setLastOrderType(customerVehicleDTO.getLastOrderType());
    this.setLastOrderId(customerVehicleDTO.getLastOrderId());

    this.setLastMaintainMileage(customerVehicleDTO.getLastMaintainMileage());
    this.setLastMaintainTime(customerVehicleDTO.getLastMaintainTime());
    this.setMaintainTimePeriod(customerVehicleDTO.getMaintainTimePeriod());
    this.setMaintainMileagePeriod(customerVehicleDTO.getMaintainMileagePeriod());
    this.setNextMaintainMileageAccess(customerVehicleDTO.getNextMaintainMileageAccess());

  }

  public CustomerVehicle fromDTO(CustomerVehicleDTO customerVehicleDTO) {
    this.setId(customerVehicleDTO.getId());
    this.setCustomerId(customerVehicleDTO.getCustomerId());
    this.setVehicleId(customerVehicleDTO.getVehicleId());
    this.setMaintainTime(customerVehicleDTO.getMaintainTime());
    this.setInsureTime(customerVehicleDTO.getInsureTime());
    this.setExamineTime(customerVehicleDTO.getExamineTime());
    this.setMaintainMileage(customerVehicleDTO.getMaintainMileage());
    this.setTotalConsume(NumberUtil.toReserve(customerVehicleDTO.getTotalConsume(), NumberUtil.MONEY_PRECISION));
    this.setConsumeTimes(NumberUtil.longValue(customerVehicleDTO.getConsumeTimes()));

    this.setLastExpenditureDate(customerVehicleDTO.getLastExpenditureDate());
    this.setLastOrderType(customerVehicleDTO.getLastOrderType());
    this.setLastOrderId(customerVehicleDTO.getLastOrderId());

    this.setLastMaintainMileage(customerVehicleDTO.getLastMaintainMileage());
    this.setLastMaintainTime(customerVehicleDTO.getLastMaintainTime());
    this.setMaintainTimePeriod(customerVehicleDTO.getMaintainTimePeriod());
    this.setMaintainMileagePeriod(customerVehicleDTO.getMaintainMileagePeriod());
    this.setNextMaintainMileageAccess(customerVehicleDTO.getNextMaintainMileageAccess());
    return this;
  }

  public CustomerVehicleDTO toDTO() {
    CustomerVehicleDTO customerVehicleDTO = new CustomerVehicleDTO();

    customerVehicleDTO.setId(this.getId());
    customerVehicleDTO.setCustomerId(this.getCustomerId());
    customerVehicleDTO.setVehicleId(this.getVehicleId());
    customerVehicleDTO.setExamineTime(this.getExamineTime());
    customerVehicleDTO.setInsureTime(this.getInsureTime());
    customerVehicleDTO.setMaintainTime(this.getMaintainTime());
    customerVehicleDTO.setExamineTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,this.getExamineTime()));
    customerVehicleDTO.setMaintainTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,this.getMaintainTime()));
    customerVehicleDTO.setInsureTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,this.getInsureTime()));
    customerVehicleDTO.setStatus(this.getStatus());
    customerVehicleDTO.setMaintainMileage(this.getMaintainMileage());
    customerVehicleDTO.setTotalConsume(NumberUtil.doubleVal(this.getTotalConsume()));
    customerVehicleDTO.setConsumeTimes(NumberUtil.longValue(this.getConsumeTimes()));

    customerVehicleDTO.setLastExpenditureDate(this.getLastExpenditureDate());
    customerVehicleDTO.setLastOrderType(this.getLastOrderType());
    customerVehicleDTO.setLastOrderId(this.getLastOrderId());

    customerVehicleDTO.setLastMaintainMileage(this.getLastMaintainMileage());
    customerVehicleDTO.setLastMaintainTime(this.getLastMaintainTime());
    customerVehicleDTO.setMaintainTimePeriod(this.getMaintainTimePeriod());
    customerVehicleDTO.setMaintainMileagePeriod(this.getMaintainMileagePeriod());
    customerVehicleDTO.setNextMaintainMileageAccess(this.getNextMaintainMileageAccess());

    return customerVehicleDTO;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return this.customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Column(name = "maintain_time")
  public Long getMaintainTime() {
    return maintainTime;
  }

  public void setMaintainTime(Long maintainTime) {
    this.maintainTime = maintainTime;
  }

  @Column(name = "insure_time")
  public Long getInsureTime() {
    return insureTime;
  }

  public void setInsureTime(Long insureTime) {
    this.insureTime = insureTime;
  }

  @Column(name = "examine_time")
  public Long getExamineTime() {
    return examineTime;
  }

  public void setExamineTime(Long examineTime) {
    this.examineTime = examineTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public VehicleStatus getStatus() {
    return status;
  }

  public void setStatus(VehicleStatus status) {
    this.status = status;
  }

  @Column(name="maintain_mileage")
  public Long getMaintainMileage() {
    return maintainMileage;
  }

  public void setMaintainMileage(Long maintainMileage) {
    this.maintainMileage = maintainMileage;
  }

  public CustomerVehicle clone() throws CloneNotSupportedException{
    CustomerVehicle newVehicle=(CustomerVehicle)super.clone();
    newVehicle.setId(null);
    return newVehicle;
  }

  private Long customerId;
  private Long vehicleId;
  private Long maintainTime;   //预约保养时间
  private Long maintainMileage;//保养里程
  private Long insureTime;      //预约保险时间
  private Long examineTime;    //预约验车时间
  private VehicleStatus status;
  private Double totalConsume; //累计消费 实收+欠款
  private Long consumeTimes;  //消费次数

  private Long lastExpenditureDate;//车辆的上次消费时间
  private OrderTypes lastOrderType; //车辆的上次消费单据类型
  private Long lastOrderId;//车辆的上次消费单据id

  private Double lastMaintainMileage;//上次保养里程
  private Long lastMaintainTime;//上次保养时间
  private Double maintainMileagePeriod;//保养里程周期
  private Long maintainTimePeriod;//保养时间周期
  private Double nextMaintainMileageAccess;//距下次保养里程


  public static Map<Long,CustomerVehicle> listToMap(List<CustomerVehicle> customerVehicles){
    Map<Long,CustomerVehicle> customerVehicleMap = new HashMap<Long, CustomerVehicle>();
    if(CollectionUtils.isNotEmpty(customerVehicles)){
      for(CustomerVehicle customerVehicle : customerVehicles){
        if(null != customerVehicle.getVehicleId()){
          customerVehicleMap.put(customerVehicle.getVehicleId(),customerVehicle);
        }
      }
    }
    return customerVehicleMap;
  }

  @Column(name="total_consume")
  public Double getTotalConsume() {
    return totalConsume;
  }

  public void setTotalConsume(Double totalConsume) {
    this.totalConsume = totalConsume;
  }

  @Column(name="consume_times")
  public Long getConsumeTimes() {
    return consumeTimes;
  }

  public void setConsumeTimes(Long consumeTimes) {
    this.consumeTimes = consumeTimes;
  }

  @Column(name="last_expenditure_date")
  public Long getLastExpenditureDate() {
    return lastExpenditureDate;
  }

  public void setLastExpenditureDate(Long lastExpenditureDate) {
    this.lastExpenditureDate = lastExpenditureDate;
  }

  @Column(name="last_order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getLastOrderType() {
    return lastOrderType;
  }

  public void setLastOrderType(OrderTypes lastOrderType) {
    this.lastOrderType = lastOrderType;
  }

  @Column(name="last_order_id")
  public Long getLastOrderId() {
    return lastOrderId;
  }

  public void setLastOrderId(Long lastOrderId) {
    this.lastOrderId = lastOrderId;
  }


  @Column(name="last_maintain_mileage")
  public Double getLastMaintainMileage() {
    return lastMaintainMileage;
  }

  public void setLastMaintainMileage(Double lastMaintainMileage) {
    this.lastMaintainMileage = lastMaintainMileage;
  }

  @Column(name="last_maintain_time")
  public Long getLastMaintainTime() {
    return lastMaintainTime;
  }

  public void setLastMaintainTime(Long lastMaintainTime) {
    this.lastMaintainTime = lastMaintainTime;
  }

  @Column(name="maintain_mileage_period")
  public Double getMaintainMileagePeriod() {
    return maintainMileagePeriod;
  }

  public void setMaintainMileagePeriod(Double maintainMileagePeriod) {
    this.maintainMileagePeriod = maintainMileagePeriod;
  }

  @Column(name="maintain_time_period")
  public Long getMaintainTimePeriod() {
    return maintainTimePeriod;
  }

  public void setMaintainTimePeriod(Long maintainTimePeriod) {
    this.maintainTimePeriod = maintainTimePeriod;
  }

  @Column(name="next_maintain_mileage_access")
  public Double getNextMaintainMileageAccess() {
    return nextMaintainMileageAccess;
  }

  public void setNextMaintainMileageAccess(Double nextMaintainMileageAccess) {
    this.nextMaintainMileageAccess = nextMaintainMileageAccess;
  }
}
