package com.bcgogo.config.model;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "operation_log")
public class OperationLog extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private Long objectId;
  private ObjectTypes objectType;
  private String content;
  private OperationTypes operationType;
  private String userName;

  public OperationLog() {
  }

  public OperationLog(OperationLogDTO operationLogDTO) {
    this.setContent(operationLogDTO.getContent());
    this.setObjectId(operationLogDTO.getObjectId());
    this.setObjectType(operationLogDTO.getObjectType());
    this.setOperationType(operationLogDTO.getOperationType());
    this.setShopId(operationLogDTO.getShopId());
    this.setUserId(operationLogDTO.getUserId());
    this.setUserName(operationLogDTO.getUserName());
  }

  public OperationLogDTO toDTO(){
    OperationLogDTO operationLogDTO = new OperationLogDTO();
    operationLogDTO.setId(this.getId());
    operationLogDTO.setCreationDate(this.getCreationDate());
    operationLogDTO.setContent(this.getContent());
    operationLogDTO.setObjectId(this.getObjectId());
    operationLogDTO.setObjectType(this.getObjectType());
    operationLogDTO.setOperationType(this.getOperationType());
    operationLogDTO.setShopId(this.getShopId());
    operationLogDTO.setUserId(this.getUserId());
    operationLogDTO.setUserName(this.getUserName());
    return operationLogDTO;
  }

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
  @Column(name="object_id")
  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  @Column(name = "object_type")
  @Enumerated(EnumType.STRING)
  public ObjectTypes getObjectType() {
    return objectType;
  }

  public void setObjectType(ObjectTypes objectType) {
    this.objectType = objectType;
  }

  @Column(name = "content", length = 500)
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "operation_type")
  @Enumerated(EnumType.STRING)
  public OperationTypes getOperationType() {
    return operationType;
  }

  public void setOperationType(OperationTypes operationType) {
    this.operationType = operationType;
  }

  @Column(name = "user_name", length = 500)
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
