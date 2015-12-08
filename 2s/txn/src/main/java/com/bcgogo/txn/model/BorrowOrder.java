package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.ReturnStatus;
import com.bcgogo.enums.TransferTypeEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.BorrowOrderDTO;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-5
 * Time: 上午8:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "borrow_order")
public class BorrowOrder extends LongIdentifier {
  private Long shopId;
  private String receiptNo;
  private OrderStatus status;
  private Long storehouseId;
  private String storehouseName;
  private Long borrowerId;  //customer or supplier id
  private String borrower;
  private String borrowerType;
  private String operator;
  private Long operatorId;
  private Double total;
  private Long vestDate;  //下单时间
  private Long returnDate;//还调时间
  private ReturnStatus returnStatus;
  private String memo;
  private TransferTypeEnum transferType;

  public void fromDTO(BorrowOrderDTO borrowOrderDTO) throws ParseException {
    if(borrowOrderDTO == null){
      return;
    }
    this.setId(borrowOrderDTO.getId());
    this.setShopId(borrowOrderDTO.getShopId());
    this.setStorehouseName(borrowOrderDTO.getStorehouseName());
    this.setStorehouseId(borrowOrderDTO.getStorehouseId());
    this.setOperator(borrowOrderDTO.getOperator());
    this.setOperatorId(borrowOrderDTO.getOperatorId());
    this.setBorrower(borrowOrderDTO.getBorrower());
    this.setBorrowerId(borrowOrderDTO.getBorrowerId());
    this.setBorrowerType(borrowOrderDTO.getBorrowerType());
    this.setReceiptNo(borrowOrderDTO.getReceiptNo());
    this.setStatus(borrowOrderDTO.getStatus());
    this.setTransferType(borrowOrderDTO.getTransferType());
    this.setReturnStatus(borrowOrderDTO.getReturnStatus());
    this.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,borrowOrderDTO.getVestDateStr()));
    this.setReturnDate(DateUtil.convertDateStringToDateLong(DateUtil.DEFAULT,borrowOrderDTO.getReturnDateStr()));
    this.setMemo(borrowOrderDTO.getMemo());
    this.setTotal(borrowOrderDTO.getTotal());
  }

  public BorrowOrderDTO toDTO(){
    BorrowOrderDTO borrowOrderDTO = new BorrowOrderDTO();
    borrowOrderDTO.setId(this.getId());
    borrowOrderDTO.setShopId(this.getShopId());
    borrowOrderDTO.setStorehouseName(this.getStorehouseName());
    borrowOrderDTO.setStorehouseId(this.getStorehouseId());
    borrowOrderDTO.setOperator(this.getOperator());
    borrowOrderDTO.setOperatorId(this.getOperatorId());
    borrowOrderDTO.setTotal(this.getTotal());
    borrowOrderDTO.setBorrower(this.getBorrower());
    borrowOrderDTO.setBorrowerId(this.getBorrowerId());
    borrowOrderDTO.setBorrowerType(this.getBorrowerType());
    borrowOrderDTO.setReturnStatusStr(this.getReturnStatus().getStatus());
    borrowOrderDTO.setReceiptNo(this.getReceiptNo());
    borrowOrderDTO.setStatus(this.getStatus());
    borrowOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD,this.getVestDate()));
    borrowOrderDTO.setVestDate(this.getVestDate());
    borrowOrderDTO.setReturnDateStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT,this.getVestDate()));
    borrowOrderDTO.setMemo(this.getMemo());
    return borrowOrderDTO;
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

  @Column(name = "return_date")
  public Long getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(Long returnDate) {
    this.returnDate = returnDate;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  @Column(name = "transfer_type")
  @Enumerated(EnumType.STRING)
  public TransferTypeEnum getTransferType() {
    return transferType;
  }

  public void setTransferType(TransferTypeEnum transferType) {
    this.transferType = transferType;
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

  @Column(name = "borrower_id")
  public Long getBorrowerId() {
    return borrowerId;
  }

  public void setBorrowerId(Long borrowerId) {
    this.borrowerId = borrowerId;
  }

  @Column(name = "borrower")
  public String getBorrower() {
    return borrower;
  }

  public void setBorrower(String borrower) {
    this.borrower = borrower;
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

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "borrower_type")
  public String getBorrowerType() {
    return borrowerType;
  }

  public void setBorrowerType(String borrowerType) {
    this.borrowerType = borrowerType;
  }

  @Column(name = "return_status")
  @Enumerated(EnumType.STRING)
  public ReturnStatus getReturnStatus() {
    return returnStatus;
  }

  public void setReturnStatus(ReturnStatus returnStatus) {
    this.returnStatus = returnStatus;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

}
