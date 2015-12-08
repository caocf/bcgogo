package com.bcgogo.config.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.merge.MergeChangeLogDTO;

import javax.persistence.*;


/**
 * 客户合并的记录
 * User: ndong
 * Date: 12-11-3
 * Time: 下午12:19
 */
@Entity
@Table(name = "merge_change_log")
public class MergeChangeLog extends LongIdentifier {

  private Long shopId;
  private Long userId;
  private String modifiedTable;
  private Long recordId;
  private String field;
  private String oldValue;
  private String newValue;
  private MergeChangeLogDTO.ModifyType description;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "record_id")
  public Long getRecordId() {
    return recordId;
  }

  public void setRecordId(Long recordId) {
    this.recordId = recordId;
  }

  @Column(name = "modified_table")
  public String getModifiedTable() {
    return modifiedTable;
  }

  public void setModifiedTable(String modifiedTable) {
    this.modifiedTable = modifiedTable;
  }

  @Column(name = "field")
  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  @Column(name = "old_value")
  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  @Column(name = "new_value")
  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  @Column(name = "description")
  @Enumerated(EnumType.STRING)
  public MergeChangeLogDTO.ModifyType getDescription() {
    return description;
  }

  public void setDescription(MergeChangeLogDTO.ModifyType description) {
    this.description = description;
  }

  public MergeChangeLog fromDTO(MergeChangeLogDTO logDTO){
    this.setShopId(logDTO.getShopId());
    this.setUserId(logDTO.getUserId());
    this.setModifiedTable(logDTO.getModifiedTable());
    this.setOldValue(logDTO.getOldValue());
    this.setNewValue(logDTO.getNewValue());
    this.setField(logDTO.getField());
    this.setRecordId(logDTO.getRecordId());
    this.setDescription(logDTO.getDescription());
    return this;
  }

}
