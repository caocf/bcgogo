package com.bcgogo.txn.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-19
 * Time: 下午5:00
 * To change this template use File | Settings | File Templates.
 */
public class InventoryCheckDTO extends BcgogoOrderDto {

  public static final int CHECK_ALL=0;
  public static final int CHECK_WIN=1;
  public static final int CHECK_LOSE=2;
  public static final int CHECK_BALANCE=3;

  private Long editDate;
  private String editDateStr;
  private String editor;
  private Long editorId;
  private String memo;

  private String receiptNo;
  private Double adjustPriceTotal;
  private int checkResultFlag;
  private String startTimeStr;
  private String endTimeStr;
  private Long startTime;
  private Long endTime;

  private String startPageNo;
  private Pager pager;

  private String checkResult;
  private InventoryCheckItemDTO[] itemDTOs;
  private Boolean mergeInOutRecordFlag = true;

  private String amountTotalStr;
  private Double amountTotal;

  private Double inventoryAmount;
  private Double modifyInventoryAmount;

  public Double getModifyInventoryAmount() {
    return modifyInventoryAmount;
  }

  public void setModifyInventoryAmount(Double modifyInventoryAmount) {
    this.modifyInventoryAmount = modifyInventoryAmount;
  }

  public Long getVestDate() {
    return null;
  }


  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }


  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Double getAdjustPriceTotal() {
    return adjustPriceTotal;
  }

  public void setAdjustPriceTotal(Double adjustPriceTotal) {
    this.adjustPriceTotal = adjustPriceTotal;
  }


  public String getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(String startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getCheckResultFlag() {
    return checkResultFlag;
  }

  public void setCheckResultFlag(int checkResultFlag) {
    this.checkResultFlag = checkResultFlag;
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

  public String getAmountTotalStr() {
    return amountTotalStr;
  }

  public void setAmountTotalStr(String amountTotalStr) {
    this.amountTotalStr = amountTotalStr;
  }

  public Double getAmountTotal() {
    return amountTotal;
  }

  public void setAmountTotal(Double amountTotal) {
    this.amountTotal = amountTotal;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public String getCheckResult() {
    return checkResult;
  }

  public void setCheckResult(String checkResult) {
    this.checkResult = checkResult;
  }

  public InventoryCheckItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(InventoryCheckItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public void convertSearchCondition() throws ParseException {
    if(StringUtil.isNotEmpty(startTimeStr)){
      this.startTime= DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, startTimeStr);
    }
    if(StringUtil.isNotEmpty(endTimeStr)){
      this.endTime= DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,endTimeStr);
    }
    if(editor!=null){
      editor=editor.trim();
    }
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.INVENTORY_CHECK);
    orderIndexDTO.setCreationDate(this.getEditDate());
    orderIndexDTO.setOrderStatus(OrderStatus.SETTLED);//盘点单没有状态 所以用结算代替
    orderIndexDTO.setVestDate(this.getEditDate());

    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    if (!ArrayUtil.isEmpty(getItemDTOs())) {
      for (InventoryCheckItemDTO inventoryCheckItemDTO : this.getItemDTOs()) {
        if (inventoryCheckItemDTO == null) continue;
        //添加每个单据的产品信息
        inOutRecordDTOList.addAll(inventoryCheckItemDTO.toInOutRecordDTO(this));
      }
      orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    }
    return orderIndexDTO;
  }

  public Double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(Double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
  }

  public Boolean getMergeInOutRecordFlag() {
    return mergeInOutRecordFlag;
  }

  public void setMergeInOutRecordFlag(Boolean mergeInOutRecordFlag) {
    this.mergeInOutRecordFlag = mergeInOutRecordFlag;
  }

  @Override
  public String toString() {
    return "InventoryCheckDTO{" +
        "editDate=" + editDate +
        ", editDateStr='" + editDateStr + '\'' +
        ", editor='" + editor + '\'' +
        ", editorId=" + editorId +
        ", memo='" + memo + '\'' +
        ", receiptNo='" + receiptNo + '\'' +
        ", adjustPriceTotal=" + adjustPriceTotal +
        ", checkResultFlag=" + checkResultFlag +
        ", startTimeStr='" + startTimeStr + '\'' +
        ", endTimeStr='" + endTimeStr + '\'' +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", startPageNo='" + startPageNo + '\'' +
        ", pager=" + pager +
        ", checkResult='" + checkResult + '\'' +
        ", itemDTOs=" + (itemDTOs == null ? null : Arrays.asList(itemDTOs)) +
        ", mergeInOutRecordFlag=" + mergeInOutRecordFlag +
        '}';
  }
}
