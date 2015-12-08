package com.bcgogo.user.model.task;


import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.RelatedShopUpdateLogDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-8
 * Time: 下午2:53
 */
@Entity
@Table(name = "relation_shop_update_log")
public class RelatedShopUpdateLog extends LongIdentifier {
  private Long taskId;
  private Long shopId;   //被改动的资料的shopId
  private Long objectId; //被改动的资料的Id
  private String modifiedTable;
  private String field;
  private String oldValue;
  private String newValue;
  private Long finishTime;

  @Column(name = "task_id")
  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "object_id")
  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
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

  @Column(name = "finish_time")
  public Long getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Long finishTime) {
    this.finishTime = finishTime;
  }

  public void fromDTO(RelatedShopUpdateLogDTO relatedShopUpdateLogDTO) {
    if(relatedShopUpdateLogDTO != null){
      setId(relatedShopUpdateLogDTO.getId());
      setShopId(relatedShopUpdateLogDTO.getShopId());
      setTaskId(relatedShopUpdateLogDTO.getTaskId());
      setFinishTime(relatedShopUpdateLogDTO.getFinishTime());
      setField(relatedShopUpdateLogDTO.getField());
      setModifiedTable(relatedShopUpdateLogDTO.getModifiedTable());
      setObjectId(relatedShopUpdateLogDTO.getObjectId());
      setOldValue(relatedShopUpdateLogDTO.getOldValue());
      setNewValue(relatedShopUpdateLogDTO.getNewValue());
    }

  }
}
