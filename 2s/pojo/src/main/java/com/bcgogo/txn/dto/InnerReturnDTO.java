package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-4
 * Time: 上午11:29
 * To change this template use File | Settings | File Templates.
 */
public class InnerReturnDTO extends BcgogoOrderDto {
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final int DEFAULT_PAGE_INDEX = 1;

  private String receiptNo;
  private Long vestDate;  //领料时间
  private String vestDateStr;  //领料时间
  private OrderStatus status;  //领料单状态，是否作废(待处理，结算，作废)
  private String pickingMan;      //退料人
  private Long pickingManId;
  private String operationMan;
  private Long operationManId;
  private Double total;
  private InnerReturnItemDTO[] itemDTOs;

  private boolean isHaveStoreHouse = false;

  private Double totalAmount;   //每个item的数量之和
  private Double totalInventoryAmount;     //每个item库存总量之和

  //分页条件
  private Integer pageSize = DEFAULT_PAGE_SIZE;
  private Integer pageNo = DEFAULT_PAGE_INDEX;

  //查询条件
  private String startTimeStr;
  private String endTimeStr;
  private Long startTime;
  private Long endTime;
  private String sortStatus;

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

  public String getPickingMan() {
    return pickingMan;
  }

  public void setPickingMan(String pickingMan) {
    this.pickingMan = pickingMan;
  }

  public Long getPickingManId() {
    return pickingManId;
  }

  public void setPickingManId(Long pickingManId) {
    this.pickingManId = pickingManId;
  }

  public String getOperationMan() {
    return operationMan;
  }

  public void setOperationMan(String operationMan) {
    this.operationMan = operationMan;
  }

  public Long getOperationManId() {
    return operationManId;
  }

  public void setOperationManId(Long operationManId) {
    this.operationManId = operationManId;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public InnerReturnItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(InnerReturnItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
    if (vestDate != null) {
      this.setVestDateStr(DateUtil.convertDateLongToString(vestDate, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    } else {
      this.setVestDateStr(null);
    }
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public boolean getIsHaveStoreHouse() {
    return isHaveStoreHouse;
  }

  public void setIsHaveStoreHouse(boolean haveStoreHouse) {
    isHaveStoreHouse = haveStoreHouse;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Double getTotalInventoryAmount() {
    return totalInventoryAmount;
  }

  public void setTotalInventoryAmount(Double totalInventoryAmount) {
    this.totalInventoryAmount = totalInventoryAmount;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getSortStatus() {
    return sortStatus;
  }

  public void setSortStatus(String sortStatus) {
    this.sortStatus = sortStatus;
  }

  public void initSearchTime() {
    Long startTime, endTime;
    try {
      startTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, this.getStartTimeStr());
    } catch (Exception e) {
      startTime = null;
      this.setStartTimeStr(null);
    }
    try {
      endTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, this.getEndTimeStr());
    } catch (Exception e) {
      endTime = null;
      this.setEndTimeStr(null);
    }
    if (startTime != null && endTime != null) {
      if (startTime > endTime) {
        Long temp = endTime;
        endTime = startTime;
        startTime = temp;
      }
    }
    if (endTime != null) {
      endTime = DateUtil.getInnerDayTime(endTime, 1);
    }
    this.setStartTime(startTime);
    this.setEndTime(endTime);
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.INNER_RETURN);
    orderIndexDTO.setCreationDate(this.getVestDate());
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());

    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (InnerReturnItemDTO innerReturnItemDTO : this.getItemDTOs()) {
        if (innerReturnItemDTO == null) continue;
        //添加每个单据的产品信息
         innerReturnItemDTO.setItemCostPrice(NumberUtil.doubleVal(innerReturnItemDTO.getInventoryAveragePrice()) * innerReturnItemDTO.getAmount());
        inOutRecordDTOList.addAll(innerReturnItemDTO.toInOutRecordDTO(this));
      }
      orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    }

    return orderIndexDTO;
  }
}
