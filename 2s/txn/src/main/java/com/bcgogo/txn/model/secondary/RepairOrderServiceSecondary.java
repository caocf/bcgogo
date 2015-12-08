package com.bcgogo.txn.model.secondary;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.secondary.RepairOrderServiceSecondaryDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "repair_order_service_secondary")
public class RepairOrderServiceSecondary extends LongIdentifier {
  private Long shopId;
  private Long repairOrderSecondaryId;
  private String service;               //施工内容
  private Double total;                 //金额
  private String workers;               //施工人
  private String memo;                  //备注
  private Double standardHours;         //标准工时
  private Double standardUnitPrice;     //标准工时单价
  private Double actualHours;           //实际工时

  public void fromDTO(RepairOrderServiceSecondaryDTO repairOrderServiceSecondaryDTO) {
    setService(repairOrderServiceSecondaryDTO.getService());
    setTotal(repairOrderServiceSecondaryDTO.getTotal());
    setWorkers(repairOrderServiceSecondaryDTO.getWorkers());
    setMemo(repairOrderServiceSecondaryDTO.getMemo());
    setStandardHours(repairOrderServiceSecondaryDTO.getStandardHours());
    setStandardUnitPrice(repairOrderServiceSecondaryDTO.getStandardUnitPrice());
    setActualHours(repairOrderServiceSecondaryDTO.getActualHours());
  }

  public RepairOrderServiceSecondaryDTO toDTO() {
    RepairOrderServiceSecondaryDTO repairOrderServiceSecondaryDTO = new RepairOrderServiceSecondaryDTO();
    repairOrderServiceSecondaryDTO.setId(getId());
    repairOrderServiceSecondaryDTO.setShopId(shopId);
    repairOrderServiceSecondaryDTO.setRepairOrderSecondaryId(repairOrderSecondaryId);
    repairOrderServiceSecondaryDTO.setService(service);
    repairOrderServiceSecondaryDTO.setTotal(total);
    repairOrderServiceSecondaryDTO.setWorkers(workers);
    repairOrderServiceSecondaryDTO.setMemo(memo);
    repairOrderServiceSecondaryDTO.setStandardHours(standardHours);
    repairOrderServiceSecondaryDTO.setStandardUnitPrice(standardUnitPrice);
    repairOrderServiceSecondaryDTO.setActualHours(actualHours);
    return repairOrderServiceSecondaryDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "repair_order_secondary_id")
  public Long getRepairOrderSecondaryId() {
    return repairOrderSecondaryId;
  }

  public void setRepairOrderSecondaryId(Long repairOrderSecondaryId) {
    this.repairOrderSecondaryId = repairOrderSecondaryId;
  }

  @Column(name = "service")
  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "workers")
  public String getWorkers() {
    return workers;
  }

  public void setWorkers(String workers) {
    this.workers = workers;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "standard_hours")
  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  @Column(name = "standard_unit_price")
  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  @Column(name = "actual_hours")
  public Double getActualHours() {
    return actualHours;
  }

  public void setActualHours(Double actualHours) {
    this.actualHours = actualHours;
  }
}
