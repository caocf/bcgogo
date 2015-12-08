package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayMethod;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RfTxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class AllocateRecordDTO extends BcgogoOrderDto {
  private Double totalAmount;
  private Double totalCostPrice;
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private String receiptNo;
  private Long vestDate;
  private String vestDateStr;
  private Long originOrderId;
  private OrderTypes originOrderType;
  private Long outStorehouseId;
  private String outStorehouseName;
  private Long inStorehouseId;
  private String inStorehouseName;
  private AllocateRecordItemDTO[] itemDTOs;
  private OrderTypes updateLackOrderType;
  private Long updateLackOrderId;
  private String returnType;
  private String print;

  public AllocateRecordDTO() {
    if(itemDTOs == null){
      itemDTOs = new AllocateRecordItemDTO[]{new AllocateRecordItemDTO()};
    }
    this.totalAmount = 0d;
    this.totalCostPrice = 0d;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public AllocateRecordItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(AllocateRecordItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    if(vestDate!=null){
      this.vestDateStr= DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, vestDate);
    }
    this.vestDate = vestDate;
  }

  public Long getOriginOrderId() {
    return originOrderId;
  }

  public void setOriginOrderId(Long originOrderId) {
    this.originOrderId = originOrderId;
  }

  public OrderTypes getOriginOrderType() {
    return originOrderType;
  }

  public void setOriginOrderType(OrderTypes originOrderType) {
    this.originOrderType = originOrderType;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public Long getOutStorehouseId() {
    return outStorehouseId;
  }

  public void setOutStorehouseId(Long outStorehouseId) {
    this.outStorehouseId = outStorehouseId;
  }

  public String getOutStorehouseName() {
    return outStorehouseName;
  }

  public void setOutStorehouseName(String outStorehouseName) {
    this.outStorehouseName = outStorehouseName;
  }

  public Long getInStorehouseId() {
    return inStorehouseId;
  }

  public void setInStorehouseId(Long inStorehouseId) {
    this.inStorehouseId = inStorehouseId;
  }

  public String getInStorehouseName() {
    return inStorehouseName;
  }

  public void setInStorehouseName(String inStorehouseName) {
    this.inStorehouseName = inStorehouseName;
  }

  public String getPrint() {
    return print;
  }

  public void setPrint(String print) {
    this.print = print;
  }

  public OrderTypes getUpdateLackOrderType() {
    return updateLackOrderType;
  }

  public void setUpdateLackOrderType(OrderTypes updateLackOrderType) {
    this.updateLackOrderType = updateLackOrderType;
  }

  public Long getUpdateLackOrderId() {
    return updateLackOrderId;
  }

  public void setUpdateLackOrderId(Long updateLackOrderId) {
    this.updateLackOrderId = updateLackOrderId;
  }

  public void fromRepairPickingDTO(RepairPickingDTO repairPickingDTO,List<RepairPickingItemDTO> repairPickingItemDTOs) {
    if(repairPickingDTO!=null){
      this.setOutStorehouseId(repairPickingDTO.getStorehouseId());
      this.setInStorehouseId(repairPickingDTO.getToStorehouseId());
      this.setOriginOrderId(repairPickingDTO.getId());
      this.setOriginOrderType(OrderTypes.REPAIR_PICKING);
      this.setEditDate(System.currentTimeMillis());
      this.setVestDate(System.currentTimeMillis());
      this.setShopId(repairPickingDTO.getShopId());
    }
    if (CollectionUtils.isNotEmpty(repairPickingItemDTOs)) {
      this.setEditor(repairPickingItemDTOs.get(0).getOperationMan());
      this.setEditorId(repairPickingItemDTOs.get(0).getOperationManId());

      List<AllocateRecordItemDTO> allocateRecordItemDTOList = new ArrayList<AllocateRecordItemDTO>();
      AllocateRecordItemDTO allocateRecordItemDTO = null;
      Double totalCostPrice = 0d, totalAmount = 0d;
      for (RepairPickingItemDTO repairPickingItemDTO : repairPickingItemDTOs) {
        allocateRecordItemDTO = new AllocateRecordItemDTO();
        allocateRecordItemDTO.setAmount(repairPickingItemDTO.getAmount());
        allocateRecordItemDTO.setCostPrice(repairPickingItemDTO.getCostPrice());
        allocateRecordItemDTO.setTotalCostPrice(allocateRecordItemDTO.getAmount() * NumberUtil.doubleVal(allocateRecordItemDTO.getCostPrice()));
        allocateRecordItemDTO.setTotalCostPrice(NumberUtil.round(allocateRecordItemDTO.getTotalCostPrice(),NumberUtil.MONEY_PRECISION));
        allocateRecordItemDTO.setProductHistoryId(repairPickingItemDTO.getProductHistoryId());
        allocateRecordItemDTO.setProductId(repairPickingItemDTO.getProductId());
        allocateRecordItemDTO.setUnit(repairPickingItemDTO.getUnit());
        allocateRecordItemDTO.setStorageBin(repairPickingItemDTO.getStorageBin());
        totalCostPrice += NumberUtil.doubleVal(allocateRecordItemDTO.getTotalCostPrice());
        totalAmount += NumberUtil.doubleVal(allocateRecordItemDTO.getAmount());
        allocateRecordItemDTOList.add(allocateRecordItemDTO);
      }
      this.setItemDTOs(allocateRecordItemDTOList.toArray(new AllocateRecordItemDTO[allocateRecordItemDTOList.size()]));
      this.setTotalCostPrice(totalCostPrice);
      this.setTotalAmount(totalAmount);
    }
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.ALLOCATE_RECORD);
    orderIndexDTO.setCreationDate(this.getEditDate());
    orderIndexDTO.setOrderStatus(OrderStatus.SETTLED);
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());

    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (AllocateRecordItemDTO allocateRecordItemDTO : this.getItemDTOs()) {
        if (allocateRecordItemDTO == null) continue;
        //添加每个单据的产品信息
        inOutRecordDTOList.addAll(allocateRecordItemDTO.toInOutRecordDTO(this));
      }
      orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    }

    return orderIndexDTO;


  }
}
