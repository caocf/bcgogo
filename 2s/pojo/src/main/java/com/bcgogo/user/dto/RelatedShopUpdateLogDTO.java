package com.bcgogo.user.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-8
 * Time: 下午3:16
 */
public class RelatedShopUpdateLogDTO implements Serializable {
  private Long Id;
  private Long taskId;
  private Long shopId;   //被改动的资料的shopId
  private Long objectId; //被改动的资料的Id
  private String modifiedTable;
  private String field;
  private String oldValue;
  private String newValue;
  private Long finishTime;

  public  RelatedShopUpdateLogDTO(){
  }

  public RelatedShopUpdateLogDTO(Object object, String field, String oldValue, String newValue) {
    if(object!=null){
      if (object instanceof CustomerDTO) {
        CustomerDTO customerDTO =(CustomerDTO)object;
        this.shopId = customerDTO.getShopId();
        this.objectId = customerDTO.getId();
        this.modifiedTable = "Customer";
      } else if (object instanceof CustomerRecordDTO) {
        CustomerRecordDTO customerRecordDTO = (CustomerRecordDTO) object;
        this.shopId = customerRecordDTO.getShopId();
        this.objectId = customerRecordDTO.getId();
        this.modifiedTable = "CustomerRecord";
      } else if (object instanceof SupplierDTO) {
        SupplierDTO supplierDTO = (SupplierDTO) object;
        this.shopId = supplierDTO.getShopId();
        this.objectId = supplierDTO.getId();
        this.modifiedTable = "Supplier";
      }
    }
    this.field = field;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  public String getModifiedTable() {
    return modifiedTable;
  }

  public void setModifiedTable(String modifiedTable) {
    this.modifiedTable = modifiedTable;
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

  public Long getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Long finishTime) {
    this.finishTime = finishTime;
  }
}
