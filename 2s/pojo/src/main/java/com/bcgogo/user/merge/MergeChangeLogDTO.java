package com.bcgogo.user.merge;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-10
 * Time: 上午7:20
 * To change this template use File | Settings | File Templates.
 */
public class MergeChangeLogDTO {
  private Long shopId;
  private Long userId;
  private String modifiedTable;
  private Long recordId;
  private String field;
  private String oldValue;
  private String newValue;
  private ModifyType description;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getModifiedTable() {
    return modifiedTable;
  }

  public void setModifiedTable(String modifiedTable) {
    this.modifiedTable = modifiedTable;
  }

  public Long getRecordId() {
    return recordId;
  }

  public void setRecordId(Long recordId) {
    this.recordId = recordId;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  public ModifyType getDescription() {
    return description;
  }

  public void setDescription(ModifyType description) {
    this.description = description;
  }

  public enum ModifyType{
    DELETE_RECORD("逻辑删除记录"),
    MODIFY_CUSTOMER_ID("修改客户ID"),
    MODIFY_SUPPLIER_ID("修改供应商ID"),
    MODIFY_CUSTOMER_NAME("修改客户名"),
    MODIFY_CUSTOMER_DEPOSIT_AMOUNT("修改预收款值"),
    MODIFY_DEPOSIT_AMOUNT("修改预付款值");

    String status;
    ModifyType(String status){
      this.status = status;
    }
    public String getStatus(){
      return this.status;
    }
  }
}
