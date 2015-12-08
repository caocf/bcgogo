package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RepairPickingDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-12
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "repair_picking")
public class RepairPicking extends LongIdentifier {

  private Long shopId;
  private Long repairOrderId;
  private String repairOrderReceiptNo;
  private String receiptNo;
  private Long vestDate;  //下单时间，对应采购单的settleDate
  private String productSeller;
  private OrderStatus status;  //领料单状态，是否作废(待处理，结算，作废)
  private Long storehouseId;//仓库id
  private String storehouseName;

  public RepairPickingDTO toDTO() {
    RepairPickingDTO repairPickingDTO = new RepairPickingDTO();
    repairPickingDTO.setId(this.getId());
    repairPickingDTO.setShopId(this.getShopId());
    repairPickingDTO.setRepairOrderId(this.getRepairOrderId());
    repairPickingDTO.setRepairOrderReceiptNo(this.getRepairOrderReceiptNo());
    repairPickingDTO.setReceiptNo(this.getReceiptNo());
    repairPickingDTO.setVestDate(this.getVestDate());
    repairPickingDTO.setProductSeller(this.getProductSeller());
    repairPickingDTO.setStatus(this.getStatus());
    repairPickingDTO.setStorehouseId(getStorehouseId());
    repairPickingDTO.setStorehouseName(getStorehouseName());
    return repairPickingDTO;
  }

  public void fromDTO(RepairPickingDTO repairPickingDTO) {
    this.setId(repairPickingDTO.getId());
    this.setShopId(repairPickingDTO.getShopId());
    this.setRepairOrderId(repairPickingDTO.getRepairOrderId());
    this.setRepairOrderReceiptNo(repairPickingDTO.getRepairOrderReceiptNo());
    this.setReceiptNo(repairPickingDTO.getReceiptNo());
    this.setVestDate(repairPickingDTO.getVestDate());
    this.setProductSeller(repairPickingDTO.getProductSeller());
    this.setStatus(repairPickingDTO.getStatus());
    this.setStorehouseId(repairPickingDTO.getStorehouseId());
    this.setStorehouseName(repairPickingDTO.getStorehouseName());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "repair_order_id")
  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  @Column(name = "repair_order_receipt_No")
  public String getRepairOrderReceiptNo() {
    return repairOrderReceiptNo;
  }

  public void setRepairOrderReceiptNo(String repairOrderReceiptNo) {
    this.repairOrderReceiptNo = repairOrderReceiptNo;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }
  @Column(name = "product_seller")
  public String getProductSeller() {
    return productSeller;
  }

  public void setProductSeller(String productSeller) {
    this.productSeller = productSeller;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  @Column(name = "storehouse_id")
  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  @Column(name = "storehouse_name")
  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }
}
