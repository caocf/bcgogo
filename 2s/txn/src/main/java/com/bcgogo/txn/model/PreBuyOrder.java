package com.bcgogo.txn.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PreBuyOrderDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "pre_buy_order")
public class PreBuyOrder extends LongIdentifier {
  private Long shopId;
  private String title;
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long vestDate;
  private Long endDate;
  private BusinessChanceType businessChanceType;
  private DeletedType deleted = DeletedType.FALSE;


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

  @Column(name="end_date")
  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  @Column(name="deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
  @Column(name="business_chance_type")
  @Enumerated(EnumType.STRING)
  public BusinessChanceType getBusinessChanceType() {
    return businessChanceType;
  }

  public void setBusinessChanceType(BusinessChanceType businessChanceType) {
    this.businessChanceType = businessChanceType;
  }

  @Column(name="title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public PreBuyOrderDTO toDTO(){
    PreBuyOrderDTO preBuyOrderDTO = new PreBuyOrderDTO();
    preBuyOrderDTO.setEditDate(this.getEditDate());
    preBuyOrderDTO.setEditor(this.getEditor());
    preBuyOrderDTO.setEditorId(this.getEditorId());
    preBuyOrderDTO.setId(this.getId());
    preBuyOrderDTO.setMemo(this.getMemo());
    preBuyOrderDTO.setVestDate(this.getVestDate());
    preBuyOrderDTO.setShopId(this.getShopId());
    preBuyOrderDTO.setEndDate(this.getEndDate());
    preBuyOrderDTO.setTitle(this.getTitle());
    preBuyOrderDTO.setCreationDate(this.getCreationDate());
    preBuyOrderDTO.setBusinessChanceType(this.getBusinessChanceType());
    return preBuyOrderDTO;
  }

  public PreBuyOrder fromDTO(PreBuyOrderDTO preBuyOrderDTO){
    this.setEditDate(preBuyOrderDTO.getEditDate());
    this.setEditor(preBuyOrderDTO.getEditor());
    this.setEditorId(preBuyOrderDTO.getEditorId());
    this.setMemo(preBuyOrderDTO.getMemo());
    this.setVestDate(preBuyOrderDTO.getVestDate());
    this.setShopId(preBuyOrderDTO.getShopId());
    this.setEndDate(preBuyOrderDTO.getEndDate());
    this.setTitle(preBuyOrderDTO.getTitle());
    this.setBusinessChanceType(preBuyOrderDTO.getBusinessChanceType());
    return this;
  }

}
