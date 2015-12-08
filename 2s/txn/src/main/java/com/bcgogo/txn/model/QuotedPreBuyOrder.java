package com.bcgogo.txn.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.txn.preBuyOrder.PreBuyOrderValidDate;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "quoted_pre_buy_order")
public class QuotedPreBuyOrder extends LongIdentifier {
  private Long shopId;
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long vestDate;
  private Long preBuyOrderId;
  private Long customerShopId;
  private String customerShopName;


  private DeletedType deleted = DeletedType.FALSE;

  @Column(name = "customer_shop_id")
  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  @Column(name = "customer_shop_name")
  public String getCustomerShopName() {
    return customerShopName;
  }

  public void setCustomerShopName(String customerShopName) {
    this.customerShopName = customerShopName;
  }

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

  @Column(name="vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }


  @Column(name="deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
  @Column(name="pre_buy_order_id")
  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
  }

  public QuotedPreBuyOrderDTO toDTO(){
    QuotedPreBuyOrderDTO quotedPreBuyOrderDTO = new QuotedPreBuyOrderDTO();
    quotedPreBuyOrderDTO.setEditDate(this.getEditDate());
    quotedPreBuyOrderDTO.setEditor(this.getEditor());
    quotedPreBuyOrderDTO.setEditorId(this.getEditorId());
    quotedPreBuyOrderDTO.setId(this.getId());
    quotedPreBuyOrderDTO.setMemo(this.getMemo());
    quotedPreBuyOrderDTO.setVestDate(this.getVestDate());
    quotedPreBuyOrderDTO.setShopId(this.getShopId());
    quotedPreBuyOrderDTO.setPreBuyOrderId(this.getPreBuyOrderId());
    quotedPreBuyOrderDTO.setCreationDate(this.getCreationDate());
    quotedPreBuyOrderDTO.setCustomerShopId(this.getCustomerShopId());
    quotedPreBuyOrderDTO.setCustomerShopName(this.getCustomerShopName());
    return quotedPreBuyOrderDTO;
  }

  public QuotedPreBuyOrder fromDTO(QuotedPreBuyOrderDTO quotedPreBuyOrderDTO){
    this.setEditDate(quotedPreBuyOrderDTO.getEditDate());
    this.setEditor(quotedPreBuyOrderDTO.getEditor());
    this.setEditorId(quotedPreBuyOrderDTO.getEditorId());
    this.setMemo(quotedPreBuyOrderDTO.getMemo());
    this.setVestDate(quotedPreBuyOrderDTO.getVestDate());
    this.setShopId(quotedPreBuyOrderDTO.getShopId());
    this.setPreBuyOrderId(quotedPreBuyOrderDTO.getPreBuyOrderId());
    this.setCustomerShopId(quotedPreBuyOrderDTO.getCustomerShopId());
    this.setCustomerShopName(quotedPreBuyOrderDTO.getCustomerShopName());
    return this;
  }

}
