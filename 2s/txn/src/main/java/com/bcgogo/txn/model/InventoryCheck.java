package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InventoryCheckDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-19
 * Time: 下午3:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inventory_check")
public class InventoryCheck extends LongIdentifier {

  private Long shopId;
  private Long editDate;
  private String editor;
  private Long editorId;
  private String memo;
  private Long storehouseId;
  private String storehouseName;
  private String receiptNo;
  private Double adjustPriceTotal;

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

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "editor")
  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  @Column(name = "editor_id")
  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
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

  @Column(name="receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name="adjust_price_total")
  public Double getAdjustPriceTotal() {
    return adjustPriceTotal;
  }

  public void setAdjustPriceTotal(Double adjustPriceTotal) {
    this.adjustPriceTotal = adjustPriceTotal;
  }


  public InventoryCheck fromDTO(InventoryCheckDTO inventoryCheckOrderDTO) {
    if (inventoryCheckOrderDTO == null) {
      return this;
    }
    this.setEditDate(inventoryCheckOrderDTO.getEditDate());
    this.setEditor(inventoryCheckOrderDTO.getEditor());
    this.setEditorId(inventoryCheckOrderDTO.getEditorId());
    this.setMemo(inventoryCheckOrderDTO.getMemo());
    this.setShopId(inventoryCheckOrderDTO.getShopId());
    this.setStorehouseId(inventoryCheckOrderDTO.getStorehouseId());
    this.setStorehouseName(inventoryCheckOrderDTO.getStorehouseName());
    this.setReceiptNo(inventoryCheckOrderDTO.getReceiptNo());
    this.setAdjustPriceTotal(inventoryCheckOrderDTO.getAdjustPriceTotal());
    return this;
  }

  public InventoryCheckDTO toDTO() {
    InventoryCheckDTO inventoryCheckDTO = new InventoryCheckDTO();
    inventoryCheckDTO.setId(this.getId());
    inventoryCheckDTO.setShopId(this.getShopId());
    inventoryCheckDTO.setEditor(this.getEditor());
    inventoryCheckDTO.setEditDate(this.getEditDate());
    inventoryCheckDTO.setStorehouseName(this.getStorehouseName());
    inventoryCheckDTO.setStorehouseId(this.getStorehouseId());

    inventoryCheckDTO.setEditDateStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD, this.getEditDate()));
    inventoryCheckDTO.setReceiptNo(this.getReceiptNo());
    inventoryCheckDTO.setAdjustPriceTotal(this.getAdjustPriceTotal());
    if (NumberUtil.numberValue(inventoryCheckDTO.getAdjustPriceTotal(), 0D) > 0) {
      inventoryCheckDTO.setCheckResult("盘盈" + inventoryCheckDTO.getAdjustPriceTotal().toString());
    } else if (NumberUtil.numberValue(inventoryCheckDTO.getAdjustPriceTotal(), 0D) < 0) {
      inventoryCheckDTO.setCheckResult("盘亏" + String.valueOf(Math.abs(inventoryCheckDTO.getAdjustPriceTotal())));
    } else {
      inventoryCheckDTO.setCheckResult("0.0");
    }
    inventoryCheckDTO.setMemo(this.getMemo());
    return inventoryCheckDTO;
  }


}
