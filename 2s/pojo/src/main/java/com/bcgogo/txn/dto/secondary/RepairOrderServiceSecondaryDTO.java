package com.bcgogo.txn.dto.secondary;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.txn.dto.RepairOrderServiceDTO;
import org.apache.commons.lang.StringUtils;

public class RepairOrderServiceSecondaryDTO {
  public RepairOrderServiceSecondaryDTO() {
  }

  private Long id;
  private String idStr;
  private Long shopId;
  private Long repairOrderSecondaryId;
  private String repairOrderSecondaryIdStr;
  private String service;               //施工内容
  private Double total;                 //金额
  private String workers;               //施工人
  private String memo;                  //备注
  private Double standardHours;         //标准工时
  private Double standardUnitPrice;     //标准工时单价
  private Double actualHours;           //实际工时

  public boolean isValidator() {
    return StringUtils.isNotEmpty(service);
  }

  public void fromRepairOrderServiceDTO(RepairOrderServiceDTO repairOrderServiceDTO) {
    setService(repairOrderServiceDTO.getService());
    setTotal(repairOrderServiceDTO.getTotal());
    setWorkers(repairOrderServiceDTO.getWorkers());
    setMemo(repairOrderServiceDTO.getMemo());
    setShopId(repairOrderServiceDTO.getShopId());
    setStandardHours(repairOrderServiceDTO.getStandardHours());
    setStandardUnitPrice(repairOrderServiceDTO.getStandardUnitPrice());
    setActualHours(repairOrderServiceDTO.getActualHours());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = id == null ? "" : id.toString();
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getRepairOrderSecondaryId() {
    return repairOrderSecondaryId;
  }

  public void setRepairOrderSecondaryId(Long repairOrderSecondaryId) {
    this.repairOrderSecondaryId = repairOrderSecondaryId;
    this.repairOrderSecondaryIdStr = repairOrderSecondaryId == null ? "" : repairOrderSecondaryId.toString();
  }


  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }


  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public String getWorkers() {
    return workers;
  }

  public void setWorkers(String workers) {
    this.workers = workers;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  public Double getActualHours() {
    return actualHours;
  }

  public void setActualHours(Double actualHours) {
    this.actualHours = actualHours;
  }

  public String getRepairOrderSecondaryIdStr() {
    return repairOrderSecondaryIdStr;
  }

  public void setRepairOrderSecondaryIdStr(String repairOrderSecondaryIdStr) {
    this.repairOrderSecondaryIdStr = repairOrderSecondaryIdStr;
  }
}
