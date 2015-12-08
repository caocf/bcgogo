package com.bcgogo.txn.model;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RepairOrderServiceDTO;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-14
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "repair_order_service")
public class RepairOrderService extends LongIdentifier {
  public RepairOrderService() {
  }

  public RepairOrderService fromDTO(RepairOrderServiceDTO serviceDTO) {
    if(serviceDTO == null)
      return this;
    this.setId(serviceDTO.getId());
    this.setShopId(serviceDTO.getShopId());
    this.setRepairOrderId(serviceDTO.getRepairOrderId());
    this.setService(serviceDTO.getService());
    this.setServiceId(serviceDTO.getServiceId());
    this.setTotal(serviceDTO.getTotal());
    this.setWorkers(serviceDTO.getWorkers());
    this.setMemo(serviceDTO.getMemo());
    this.setCostPrice(serviceDTO.getCostPrice());
    this.setBusinessCategoryId(serviceDTO.getBusinessCategoryId());
    this.setBusinessCategoryName(serviceDTO.getBusinessCategoryName());
    this.setServiceHistoryId(serviceDTO.getServiceHistoryId());
    this.setStandardHours(serviceDTO.getStandardHours());
    this.setStandardUnitPrice(serviceDTO.getStandardUnitPrice());
    this.setActualHours(serviceDTO.getActualHours());
    return this;
  }

  @Column(name = "repair_order_id")
  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Transient
  public String getService() {
    return service;
  }

  private void setService(String service) {
    this.service = service;
  }

  @Column(name = "service_history_id")
  public Long getServiceHistoryId() {
    return serviceHistoryId;
  }

  public void setServiceHistoryId(Long serviceHistoryId) {
    this.serviceHistoryId = serviceHistoryId;
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "workers", length = 50)
  public String getWorkers() {
    return workers;
  }

  public void setWorkers(String workers) {
    this.workers = workers;
  }

  @Column(name = "memo", length = 100)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  private Long repairOrderId;
  private Long serviceId;
  private String service;
  private Long serviceHistoryId;
  private double total;
  private String workers;
  private String memo;
  private Long shopId;
  private ConsumeType consumeType;
  private String workerIds;
  private Long businessCategoryId;
  private String businessCategoryName;

  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时单价
  private Double actualHours;//实际工时


  @Column(name = "worker_ids")
  public String getWorkerIds() {
    return workerIds;
  }

  public void setWorkerIds(String workerIds) {
    this.workerIds = workerIds;
  }

  @Column(name = "consume_type")
  @Enumerated(EnumType.STRING)
  public ConsumeType getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(ConsumeType consumeType) {
    this.consumeType = consumeType;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  private Double costPrice;

  @Column(name = "cost_price")
  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  @Column(name="business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name="business_category_name")
  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  @Column(name="standard_hours")
  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  @Column(name="standard_unit_price")
  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  @Column(name="actual_hours")
  public Double getActualHours() {
    return actualHours;
  }

  public void setActualHours(Double actualHours) {
    this.actualHours = actualHours;
  }

  public RepairOrderServiceDTO toDTO() {
    RepairOrderServiceDTO repairOrderServiceDTO = new RepairOrderServiceDTO();

    repairOrderServiceDTO.setId(this.getId());
    repairOrderServiceDTO.setShopId(this.getShopId());

    repairOrderServiceDTO.setRepairOrderId(this.getRepairOrderId());
    repairOrderServiceDTO.setService(this.getService());
    repairOrderServiceDTO.setServiceId(this.getServiceId());

    repairOrderServiceDTO.setTotal(this.getTotal());
    repairOrderServiceDTO.setWorkers(this.getWorkers());
    repairOrderServiceDTO.setMemo(this.getMemo());
    repairOrderServiceDTO.setCostPrice(this.getCostPrice());
    repairOrderServiceDTO.setConsumeType(this.getConsumeType());
    repairOrderServiceDTO.setWorkerIds(this.getWorkerIds());
    repairOrderServiceDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    repairOrderServiceDTO.setBusinessCategoryName(this.getBusinessCategoryName());
    repairOrderServiceDTO.setServiceHistoryId(this.getServiceHistoryId());
    repairOrderServiceDTO.setStandardHours(getStandardHours());
    repairOrderServiceDTO.setStandardUnitPrice(getStandardUnitPrice());
    repairOrderServiceDTO.setActualHours(getActualHours());
    return repairOrderServiceDTO;
  }

  public RepairOrderService fromDTO(RepairOrderServiceDTO repairOrderServiceDTO, boolean setId) {
    if (setId) {
      this.setId(repairOrderServiceDTO.getId());
    }

    this.setShopId(repairOrderServiceDTO.getShopId());
    this.setRepairOrderId(repairOrderServiceDTO.getRepairOrderId());
    this.setService(repairOrderServiceDTO.getService());
    this.setServiceId(repairOrderServiceDTO.getServiceId());
    this.setTotal(repairOrderServiceDTO.getTotal());
    this.setWorkers(StringUtil.isEmpty(repairOrderServiceDTO.getWorkers()) ? null : repairOrderServiceDTO.getWorkers());
    this.setMemo(repairOrderServiceDTO.getMemo());
    this.setConsumeType(repairOrderServiceDTO.getConsumeType());
    this.setWorkerIds(StringUtil.isEmpty(repairOrderServiceDTO.getWorkerIds()) ? null : repairOrderServiceDTO.getWorkerIds());
    this.setBusinessCategoryId(repairOrderServiceDTO.getBusinessCategoryId());
    this.setBusinessCategoryName(repairOrderServiceDTO.getBusinessCategoryName());
    this.setServiceHistoryId(repairOrderServiceDTO.getServiceHistoryId());
    this.setStandardHours(repairOrderServiceDTO.getStandardHours());
    this.setStandardUnitPrice(repairOrderServiceDTO.getStandardUnitPrice());
    this.setActualHours(repairOrderServiceDTO.getActualHours());
    return this;
  }
}