package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InnerPickingDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-28
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inner_picking")
public class InnerPicking extends LongIdentifier {
  private Long shopId;
  private String receiptNo;
  private Long vestDate;  //下单时间，
  private OrderStatus status;
  private Long storehouseId;//仓库id
  private String storehouseName;
  private String pickingMan;
  private Long pickingManId;
  private String operationMan;
  private Long operationManId;
  private Double total;

  public InnerPickingDTO toDTO(){
    InnerPickingDTO innerPickingDTO = new InnerPickingDTO();
    innerPickingDTO.setId(this.getId());
    innerPickingDTO.setShopId(this.getShopId());
    innerPickingDTO.setStorehouseName(this.getStorehouseName());
    innerPickingDTO.setStorehouseId(this.getStorehouseId());
    innerPickingDTO.setOperationMan(this.getOperationMan());
    innerPickingDTO.setOperationManId(this.getOperationManId());
    innerPickingDTO.setPickingMan(this.getPickingMan());
    innerPickingDTO.setPickingManId(this.getPickingManId());
    innerPickingDTO.setReceiptNo(this.getReceiptNo());
    innerPickingDTO.setStatus(this.getStatus());
    innerPickingDTO.setTotal(NumberUtil.round(this.getTotal(),2));
    innerPickingDTO.setVestDate(this.getVestDate());
    return innerPickingDTO;
  }

  public void fromDTO(InnerPickingDTO innerPickingDTO){
    if(innerPickingDTO == null){
      return;
    }
    this.setId(innerPickingDTO.getId());
    this.setShopId(innerPickingDTO.getShopId());
    this.setStorehouseName(innerPickingDTO.getStorehouseName());
    this.setStorehouseId(innerPickingDTO.getStorehouseId());
    this.setOperationMan(innerPickingDTO.getOperationMan());
    this.setOperationManId(innerPickingDTO.getOperationManId());
    this.setPickingMan(innerPickingDTO.getPickingMan());
    this.setPickingManId(innerPickingDTO.getPickingManId());
    this.setReceiptNo(innerPickingDTO.getReceiptNo());
    this.setStatus(innerPickingDTO.getStatus());
    this.setTotal(innerPickingDTO.getTotal());
    this.setVestDate(innerPickingDTO.getVestDate());
  }
  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  @Column(name = "picking_man")
  public String getPickingMan() {
    return pickingMan;
  }

  public void setPickingMan(String pickingMan) {
    this.pickingMan = pickingMan;
  }

  @Column(name = "picking_man_id")
  public Long getPickingManId() {
    return pickingManId;
  }

  public void setPickingManId(Long pickingManId) {
    this.pickingManId = pickingManId;
  }

  @Column(name = "operation_man")
  public String getOperationMan() {
    return operationMan;
  }

  public void setOperationMan(String operationMan) {
    this.operationMan = operationMan;
  }

  @Column(name = "operation_man_id")
  public Long getOperationManId() {
    return operationManId;
  }

  public void setOperationManId(Long operationManId) {
    this.operationManId = operationManId;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }
}
