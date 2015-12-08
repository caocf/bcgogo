package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RepairPickingItemDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-12
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "repair_picking_item")
public class RepairPickingItem extends LongIdentifier {

  private Long repairPickingId;
  private Long productId;
  private Double amount;
  private String unit;
  private String operationMan;   //操作人
  private String pickingMan;     //领料人
  private Long operationDate;    //领料退料时间
  private OrderStatus status;
  private Long operationManId;//操作人Id
  private Long pickingManId;  //领料人Id

  public void fromDTO(RepairPickingItemDTO repairPickingItemDTO) {
    if (repairPickingItemDTO == null) {
      return;
    }
    this.setId(repairPickingItemDTO.getId());
    this.setRepairPickingId(repairPickingItemDTO.getRepairPickingId());
    this.setProductId(repairPickingItemDTO.getProductId());
    this.setAmount(repairPickingItemDTO.getAmount());
    this.setUnit(repairPickingItemDTO.getUnit());
    this.setOperationMan(repairPickingItemDTO.getOperationMan());
    this.setPickingMan(repairPickingItemDTO.getPickingMan());
    this.setOperationDate(repairPickingItemDTO.getOperationDate());
    this.setStatus(repairPickingItemDTO.getStatus());
  }

  public RepairPickingItem() {
  }

  public RepairPickingItem(Long repairPickingId, Long productId, Double amount, String unit,OrderStatus status) {
    this.repairPickingId = repairPickingId;
    this.productId = productId;
    this.amount = amount;
    this.unit = unit;
    this.status = status;
  }

  public RepairPickingItemDTO toDTO() {
    RepairPickingItemDTO repairPickingItemDTO = new RepairPickingItemDTO();
    repairPickingItemDTO.setId(this.getId());
    repairPickingItemDTO.setRepairPickingId(this.getRepairPickingId());
    repairPickingItemDTO.setProductId(this.getProductId());
    repairPickingItemDTO.setAmount(this.getAmount());
    repairPickingItemDTO.setUnit(this.getUnit());
    repairPickingItemDTO.setOperationMan(this.getOperationMan());
    repairPickingItemDTO.setOperationManId(this.getOperationManId());
    repairPickingItemDTO.setPickingMan(this.getPickingMan());
    repairPickingItemDTO.setOperationDate(this.getOperationDate());
    repairPickingItemDTO.setStatus(this.getStatus());
    return repairPickingItemDTO;
  }

  @Column(name = "repair_picking_id")
  public Long getRepairPickingId() {
    return repairPickingId;
  }

  public void setRepairPickingId(Long repairPickingId) {
    this.repairPickingId = repairPickingId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "operation_man")
  public String getOperationMan() {
    return operationMan;
  }

  public void setOperationMan(String operationMan) {
    this.operationMan = operationMan;
  }

  @Column(name = "picking_man")
  public String getPickingMan() {
    return pickingMan;
  }

  public void setPickingMan(String pickingMan) {
    this.pickingMan = pickingMan;
  }

  @Column(name = "operation_date")
  public Long getOperationDate() {
    return operationDate;
  }

  public void setOperationDate(Long operationDate) {
    this.operationDate = operationDate;
  }

  @Column(name = "status")

  @Enumerated(EnumType.STRING)
  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  @Column(name = "operation_man_id")
  public Long getOperationManId() {
    return operationManId;
  }

  public void setOperationManId(Long operationManId) {
    this.operationManId = operationManId;
  }

  @Column(name = "picking_man_id")
  public Long getPickingManId() {
    return pickingManId;
  }

  public void setPickingManId(Long pickingManId) {
    this.pickingManId = pickingManId;
  }
}
