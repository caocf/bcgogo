package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.AllocateRecordDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "allocate_record")
public class AllocateRecord extends LongIdentifier {
  private Long shopId;
  private Double totalAmount;
  private Double totalCostPrice;
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private String receiptNo;
  private Long vestDate;

  private Long outStorehouseId;
  private String outStorehouseName;
  private Long inStorehouseId;
  private String inStorehouseName;


  private Long originOrderId;
  private OrderTypes originOrderType;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "total_amount")
  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  @Column(name = "total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  @Column(name="editor_id")
  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  @Column(name="editor")
  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  @Column(name="edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name="receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name="vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  @Column(name="origin_order_id")
  public Long getOriginOrderId() {
    return originOrderId;
  }

  public void setOriginOrderId(Long originOrderId) {
    this.originOrderId = originOrderId;
  }

  @Column(name="origin_order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOriginOrderType() {
    return originOrderType;
  }

  public void setOriginOrderType(OrderTypes originOrderType) {
    this.originOrderType = originOrderType;
  }

  @Column(name="out_storehouse_id")
  public Long getOutStorehouseId() {
    return outStorehouseId;
  }

  public void setOutStorehouseId(Long outStorehouseId) {
    this.outStorehouseId = outStorehouseId;
  }

  @Column(name="out_storehouse_name")
  public String getOutStorehouseName() {
    return outStorehouseName;
  }

  public void setOutStorehouseName(String outStorehouseName) {
    this.outStorehouseName = outStorehouseName;
  }

  @Column(name="in_storehouse_id")
  public Long getInStorehouseId() {
    return inStorehouseId;
  }

  public void setInStorehouseId(Long inStorehouseId) {
    this.inStorehouseId = inStorehouseId;
  }

  @Column(name="in_storehouse_name")
  public String getInStorehouseName() {
    return inStorehouseName;
  }

  public void setInStorehouseName(String inStorehouseName) {
    this.inStorehouseName = inStorehouseName;
  }

  public AllocateRecordDTO toDTO(){
    AllocateRecordDTO allocateRecordDTO = new AllocateRecordDTO();
    allocateRecordDTO.setEditDate(this.getEditDate());
    allocateRecordDTO.setEditor(this.getEditor());
    allocateRecordDTO.setEditorId(this.getEditorId());
    allocateRecordDTO.setId(this.getId());
    allocateRecordDTO.setInStorehouseId(this.getInStorehouseId());
    allocateRecordDTO.setInStorehouseName(this.getInStorehouseName());
    allocateRecordDTO.setMemo(this.getMemo());
    allocateRecordDTO.setOriginOrderId(this.getOriginOrderId());
    allocateRecordDTO.setOriginOrderType(this.getOriginOrderType());
    allocateRecordDTO.setVestDate(this.getVestDate());
    allocateRecordDTO.setTotalAmount(this.getTotalAmount());
    allocateRecordDTO.setTotalCostPrice(this.getTotalCostPrice());
    allocateRecordDTO.setShopId(this.getShopId());
    allocateRecordDTO.setReceiptNo(this.getReceiptNo());
    allocateRecordDTO.setOutStorehouseName(this.getOutStorehouseName());
    allocateRecordDTO.setOutStorehouseId(this.getOutStorehouseId());
    return allocateRecordDTO;
  }

  public AllocateRecord fromDTO(AllocateRecordDTO allocateRecordDTO){
    this.setEditDate(allocateRecordDTO.getEditDate());
    this.setEditor(allocateRecordDTO.getEditor());
    this.setEditorId(allocateRecordDTO.getEditorId());
    this.setInStorehouseId(allocateRecordDTO.getInStorehouseId());
    this.setInStorehouseName(allocateRecordDTO.getInStorehouseName());
    this.setMemo(allocateRecordDTO.getMemo());
    this.setOriginOrderId(allocateRecordDTO.getOriginOrderId());
    this.setOriginOrderType(allocateRecordDTO.getOriginOrderType());
    this.setVestDate(allocateRecordDTO.getVestDate());
    this.setTotalAmount(allocateRecordDTO.getTotalAmount());
    this.setTotalCostPrice(allocateRecordDTO.getTotalCostPrice());
    this.setShopId(allocateRecordDTO.getShopId());
    this.setReceiptNo(allocateRecordDTO.getReceiptNo());
    this.setOutStorehouseName(allocateRecordDTO.getOutStorehouseName());
    this.setOutStorehouseId(allocateRecordDTO.getOutStorehouseId());
    return this;
  }
}
