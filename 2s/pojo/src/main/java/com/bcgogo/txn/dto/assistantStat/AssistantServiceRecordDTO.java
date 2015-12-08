package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.*;
import com.bcgogo.utils.NumberUtil;

/**
 * 员工服务记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 下午2:02
 * To change this template use File | Settings | File Templates.
 */
public class AssistantServiceRecordDTO extends AssistantRecordDTO {

  private Long itemId;

  private Long serviceId;
  private String serviceName;

  private String vehicle;

  private Double standardHours;
  private Double standardService;
  private Double actualHours;
  private Double actualService;

  private Long serviceAchievementHistoryId;

  public AssistantServiceRecordDTO() {
  }

  public AssistantServiceRecordDTO(WashBeautyOrderDTO washBeautyOrderDTO, WashBeautyOrderItemDTO washBeautyOrderItemDTO) {
    this.setShopId(washBeautyOrderDTO.getShopId());
    this.setOrderId(washBeautyOrderDTO.getId());
    this.setOrderType(OrderTypes.WASH_BEAUTY);

    this.setReceiptNo(washBeautyOrderDTO.getReceiptNo());
    this.setItemId(washBeautyOrderItemDTO.getId());
    this.setVestDate(washBeautyOrderDTO.getVestDate());
    this.setServiceId(washBeautyOrderItemDTO.getServiceId());

    this.setVehicle(washBeautyOrderDTO.getVechicle());

    this.setCustomer(washBeautyOrderDTO.getCustomer());
    this.setCustomerId(washBeautyOrderDTO.getCustomerId());
  }

  public AssistantServiceRecordDTO(RepairOrderDTO repairOrderDTO, RepairOrderServiceDTO repairOrderServiceDTO ) {
    this.setShopId(repairOrderDTO.getShopId());
    this.setOrderType(OrderTypes.REPAIR);
    this.setOrderId(repairOrderDTO.getId());
    this.setServiceId(repairOrderServiceDTO.getServiceId());

    this.setReceiptNo(repairOrderDTO.getReceiptNo());
    this.setItemId(repairOrderServiceDTO.getId());
    this.setVestDate(repairOrderDTO.getVestDate());

    this.setVehicle(repairOrderDTO.getVechicle());

    this.setCustomer(repairOrderDTO.getCustomerName());
    this.setCustomerId(repairOrderDTO.getCustomerId());
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = NumberUtil.toReserve(standardHours, NumberUtil.PRECISION);
  }

  public Double getStandardService() {
    return standardService;
  }

  public void setStandardService(Double standardService) {
    this.standardService = NumberUtil.toReserve(standardService,NumberUtil.PRECISION);
  }

  public Double getActualHours() {
    return actualHours;
  }

  public void setActualHours(Double actualHours) {
    this.actualHours = NumberUtil.toReserve(actualHours,NumberUtil.PRECISION);
  }

  public Double getActualService() {
    return actualService;
  }

  public void setActualService(Double actualService) {
    this.actualService = NumberUtil.toReserve(actualService,NumberUtil.PRECISION);
  }

  public Long getServiceAchievementHistoryId() {
    return serviceAchievementHistoryId;
  }

  public void setServiceAchievementHistoryId(Long serviceAchievementHistoryId) {
    this.serviceAchievementHistoryId = serviceAchievementHistoryId;
  }
}
