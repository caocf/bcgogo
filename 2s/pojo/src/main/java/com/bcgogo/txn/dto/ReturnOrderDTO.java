package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.TransferTypeEnum;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.CustomerOrSupplierDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-8
 * Time: 上午7:03
 * To change this template use File | Settings | File Templates.
 */
public class ReturnOrderDTO extends BcgogoOrderDto{
  private String borrowOrderId;
  private String borrowOrderReceiptNo;
  private Long shopId;
  private String receiptNo;
  private OrderStatus status;
  private Long returnId;
  private String returner;
  private Long storehouseId;
  private String storehouseName;
  private String operator;
  private Long operatorId;
  private String returnerType;
  private Long vestDate;
  private String vestDateStr;
  private boolean isHaveStoreHouse;
  private TransferTypeEnum transferType;
  private ReturnOrderItemDTO[] itemDTOs;
  private CustomerOrSupplierDTO customerOrSupplierDTO;
  private String startTimeStr;
  private Long startTime;
  private String endTimeStr;
  private Long endTime;
  private int pageNo;
  private int pageSize;

  public void initAndVerifyByRequest() throws ParseException {
    if(this.getCustomerOrSupplierDTO()!=null){
      this.getCustomerOrSupplierDTO().setCustomerOrSupplierId(NumberUtil.longValue(this.getCustomerOrSupplierDTO().getCustomerOrSupplierIdStr()));
    }
    this.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, this.getVestDateStr()));
  }

  public String getBorrowOrderId() {
    return borrowOrderId;
  }

  public void setBorrowOrderId(String borrowOrderId) {
    this.borrowOrderId = borrowOrderId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public boolean isHaveStoreHouse() {
    return isHaveStoreHouse;
  }

  public void setHaveStoreHouse(boolean haveStoreHouse) {
    isHaveStoreHouse = haveStoreHouse;
  }

  public TransferTypeEnum getTransferType() {
    return transferType;
  }

  public void setTransferType(TransferTypeEnum transferType) {
    this.transferType = transferType;
  }

  public Long getReturnId() {
    return returnId;
  }

  public void setReturnId(Long returnId) {
    this.returnId = returnId;
  }

  public String getReturner() {
    return returner;
  }

  public void setReturner(String returner) {
    this.returner = returner;
  }

  public String getReturnerType() {
    return returnerType;
  }

  public void setReturnerType(String returnerType) {
    this.returnerType = returnerType;
  }

  public ReturnOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(ReturnOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
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

  public int getPageNo() {
    return pageNo;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public OrderIndexDTO toOrderIndexDTO() {

    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.RETURN_ORDER);
    orderIndexDTO.setCreationDate(this.getVestDate());
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());

    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (ReturnOrderItemDTO returnOrderItemDTO : this.getItemDTOs()) {
        if (returnOrderItemDTO == null) continue;
        //添加每个单据的产品信息
        inOutRecordDTOList.addAll(returnOrderItemDTO.toInOutRecordDTO(this));
      }
      orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    }

    return orderIndexDTO;
  }

  public String getBorrowOrderReceiptNo() {
    return borrowOrderReceiptNo;
  }

  public void setBorrowOrderReceiptNo(String borrowOrderReceiptNo) {
    this.borrowOrderReceiptNo = borrowOrderReceiptNo;
  }
}
