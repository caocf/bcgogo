package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InnerReturnDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-4
 * Time: 上午11:27
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inner_return")
public class InnerReturn extends LongIdentifier {
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

  public InnerReturnDTO toDTO() {
    InnerReturnDTO innerReturnDTO = new InnerReturnDTO();
    innerReturnDTO.setId(this.getId());
    innerReturnDTO.setShopId(this.getShopId());
    innerReturnDTO.setStorehouseName(this.getStorehouseName());
    innerReturnDTO.setStorehouseId(this.getStorehouseId());
    innerReturnDTO.setOperationMan(this.getOperationMan());
    innerReturnDTO.setOperationManId(this.getOperationManId());
    innerReturnDTO.setPickingMan(this.getPickingMan());
    innerReturnDTO.setPickingManId(this.getPickingManId());
    innerReturnDTO.setReceiptNo(this.getReceiptNo());
    innerReturnDTO.setStatus(this.getStatus());
    innerReturnDTO.setTotal(NumberUtil.round(this.getTotal(), 2));
    innerReturnDTO.setVestDate(this.getVestDate());
    return innerReturnDTO;
  }

  public void fromDTO(InnerReturnDTO innerReturnDTO) {
    if (innerReturnDTO == null) {
      return;
    }
    this.setId(innerReturnDTO.getId());
    this.setShopId(innerReturnDTO.getShopId());
    this.setStorehouseName(innerReturnDTO.getStorehouseName());
    this.setStorehouseId(innerReturnDTO.getStorehouseId());
    this.setOperationMan(innerReturnDTO.getOperationMan());
    this.setOperationManId(innerReturnDTO.getOperationManId());
    this.setPickingMan(innerReturnDTO.getPickingMan());
    this.setPickingManId(innerReturnDTO.getPickingManId());
    this.setReceiptNo(innerReturnDTO.getReceiptNo());
    this.setStatus(innerReturnDTO.getStatus());
    this.setTotal(innerReturnDTO.getTotal());
    this.setVestDate(innerReturnDTO.getVestDate());
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
