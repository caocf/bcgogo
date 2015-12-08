package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.TransferTypeEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ReturnOrderDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * 借调单的归还单据
 * User: ndong
 * Date: 13-3-5
 * Time: 上午9:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "return_order")
public class ReturnOrder extends LongIdentifier {
  private Long borrowOrderId;
  private Long shopId;
  private String receiptNo;
  private OrderStatus status;
  private Long storehouseId;
  private String storehouseName;
  private Long returnerId;
  private String returner;  //归还人
  private String returnerType;
  private String operator;
  private Long operatorId;
  private Long vestDate;  //下单时间
  private TransferTypeEnum transferType;

  public ReturnOrderDTO toDTO(){
    ReturnOrderDTO orderDTO=new ReturnOrderDTO();
    orderDTO.setId(this.getId());
    orderDTO.setShopId(this.getShopId());
    orderDTO.setBorrowOrderId(String.valueOf(this.getBorrowOrderId()));
    orderDTO.setReceiptNo(this.getReceiptNo());
    orderDTO.setOperatorId(this.getOperatorId());
    orderDTO.setOperator(this.getOperator());
    orderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD,this.getVestDate()));
    orderDTO.setVestDate(this.getVestDate());
    orderDTO.setStatus(this.getStatus());
    orderDTO.setStorehouseId(this.getStorehouseId());
    orderDTO.setStorehouseName(this.getStorehouseName());
    return orderDTO;
  }


  public ReturnOrder fromDTO(ReturnOrderDTO returnOrderDTO){
    this.setId(NumberUtil.longValue(returnOrderDTO.getIdStr()));
    this.setShopId(returnOrderDTO.getShopId());
    this.setBorrowOrderId(NumberUtil.longValue(returnOrderDTO.getBorrowOrderId()));
    this.setReceiptNo(returnOrderDTO.getReceiptNo());
    this.setStatus(returnOrderDTO.getStatus());
    this.setStorehouseId(returnOrderDTO.getStorehouseId());
    this.setStorehouseName(returnOrderDTO.getStorehouseName());
    this.setVestDate(returnOrderDTO.getVestDate());
    this.setOperatorId(returnOrderDTO.getOperatorId());
    this.setOperator(returnOrderDTO.getOperator());
    this.setReturner(returnOrderDTO.getReturner());
    return this;
  }

  @Column(name = "borrow_order_id")
  public Long getBorrowOrderId() {
    return borrowOrderId;
  }

  public void setBorrowOrderId(Long borrowOrderId) {
    this.borrowOrderId = borrowOrderId;
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

  @Column(name = "returner_id")
  public Long getReturnerId() {
    return returnerId;
  }

  public void setReturnerId(Long returnerId) {
    this.returnerId = returnerId;
  }

  @Column(name = "returner")
  public String getReturner() {
    return returner;
  }

  public void setReturner(String returner) {
    this.returner = returner;
  }

  @Column(name = "transfer_type")
  @Enumerated(EnumType.STRING)
  public TransferTypeEnum getTransferType() {
    return transferType;
  }

  public void setTransferType(TransferTypeEnum transferType) {
    this.transferType = transferType;
  }

  @Column(name = "operator")
  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  @Column(name = "operator_id")
  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  @Column(name = "returner_type")
  public String getReturnerType() {
    return returnerType;
  }

  public void setReturnerType(String returnerType) {
    this.returnerType = returnerType;
  }

}
