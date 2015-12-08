package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ReturnStatus;
import com.bcgogo.enums.TransferTypeEnum;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.CustomerOrSupplierDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-5
 * Time: 下午3:39
 * To change this template use File | Settings | File Templates.
 */
public class BorrowOrderDTO extends BcgogoOrderDto{

  private String receiptNo;
  private OrderStatus status;
  private Long borrowerId;
  private String borrowerIdStr;
  private String borrower;
  private String name;
  private String label;
  private String borrowerType;
  private String phone;
  private String operator;
  private Long operatorId;
  private String storehouseName;
  private Long vestDate;
  private String vestDateStr;
  private String returnDateStr;
  private boolean isHaveStoreHouse;
  private TransferTypeEnum transferType;
  private String returnStatusStr;  //
  private ReturnStatus returnStatus;
  private BorrowOrderItemDTO[] itemDTOs;
  private CustomerOrSupplierDTO customerOrSupplierDTO;
  private Double total;
  private String startTimeStr;
  private Long startTime;
  private String endTimeStr;
  private Long endTime;
  private int startPageNo;
  private int pageSize;
  private String sortStatus;
  private String memo;

  public void initParams() throws ParseException {
    if(ReturnStatus.RETURN_ALL.toString().equals(this.getReturnStatusStr())){
      this.setReturnStatus(ReturnStatus.RETURN_ALL);
    }else if(ReturnStatus.RETURN_PARTLY.toString().equals(this.getReturnStatusStr())){
      this.setReturnStatus(ReturnStatus.RETURN_PARTLY);
    }else if(ReturnStatus.RETURN_NONE.toString().equals(this.getReturnStatusStr())){
      this.setReturnStatus(ReturnStatus.RETURN_NONE);
    }else {
      this.setReturnStatus(null);
    }
    this.setBorrower(StringUtil.toTrim(this.getBorrower()));
    this.setOperator(StringUtil.toTrim(this.getOperator()));
    if(StringUtil.isNotEmpty(this.getStartTimeStr())){
      this.setStartTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,this.getStartTimeStr()));
    }
     if(StringUtil.isNotEmpty(this.getEndTimeStr())){
      this.setEndTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,this.getEndTimeStr()));
    }
  }

  public BorrowOrderDTO(){

  }

  public ReturnStatus getReturnStatus() {
    return returnStatus;
  }

  public void setReturnStatus(ReturnStatus returnStatus) {
    this.returnStatus = returnStatus;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Long getBorrowerId() {
    return borrowerId;
  }

  public void setBorrowerId(Long borrowerId) {
    this.setBorrowerIdStr(String.valueOf(borrowerId));
    this.borrowerId = borrowerId;
  }

  public String getBorrowerIdStr() {
    return borrowerIdStr;
  }

  public void setBorrowerIdStr(String borrowerIdStr) {
    this.borrowerIdStr = borrowerIdStr;
  }

  public String getBorrower() {
    return borrower;
  }

  public void setBorrower(String borrower) {
    this.borrower = borrower;
    this.setName(borrower);
    this.setLabel(borrower);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBorrowerType() {
    return borrowerType;
  }

  public void setBorrowerType(String borrowerType) {
    this.borrowerType = borrowerType;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getReturnDateStr() {
    return returnDateStr;
  }

  public void setReturnDateStr(String returnDateStr) {
    this.returnDateStr = returnDateStr;
  }

  public boolean setIsHaveStoreHouse(boolean isHaveStoreHouse) {
    return this.isHaveStoreHouse=isHaveStoreHouse;
  }

  public boolean getIsHaveStoreHouse() {
    return isHaveStoreHouse;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public TransferTypeEnum getTransferType() {
    return transferType;
  }

  public void setTransferType(TransferTypeEnum transferType) {
    this.transferType = transferType;
  }

  public String getReturnStatusStr() {
    return returnStatusStr;
  }

  public void setReturnStatusStr(String returnStatusStr) {
    this.returnStatusStr = returnStatusStr;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public BorrowOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(BorrowOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public boolean isHaveStoreHouse() {
    return isHaveStoreHouse;
  }

  public void setHaveStoreHouse(boolean haveStoreHouse) {
    isHaveStoreHouse = haveStoreHouse;
  }

  public CustomerOrSupplierDTO getCustomerOrSupplierDTO() {
    return customerOrSupplierDTO;
  }

  public void setCustomerOrSupplierDTO(CustomerOrSupplierDTO customerOrSupplierDTO) {
    this.customerOrSupplierDTO = customerOrSupplierDTO;
  }


  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public String getSortStatus() {
    return sortStatus;
  }

  public void setSortStatus(String sortStatus) {
    this.sortStatus = sortStatus;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.BORROW_ORDER);
    orderIndexDTO.setCreationDate(this.getVestDate());
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());

    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (BorrowOrderItemDTO borrowOrderItemDTO : this.getItemDTOs()) {
        if (borrowOrderItemDTO == null) continue;
        borrowOrderItemDTO.setItemCostPrice(NumberUtil.doubleVal(borrowOrderItemDTO.getInventoryAveragePrice()) * borrowOrderItemDTO.getAmount());
        //添加每个单据的产品信息
        inOutRecordDTOList.addAll(borrowOrderItemDTO.toInOutRecordDTO(this));
      }
      orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    }

    return orderIndexDTO;

  }
}
