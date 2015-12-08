package com.bcgogo.config.dto;

import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.AppointOrderDTO;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午2:37
 * To change this template use File | Settings | File Templates.
 */
public class OperationLogDTO implements Serializable {
  private Long id;
  private Long shopId;
  private Long creationDate;
  private String creationDateStr;
  private Long userId;
  private String userName;
  private Long objectId;
  private ObjectTypes objectType;
  private String content;
  private OperationTypes operationType;

  public OperationLogDTO() {
  }

  public OperationLogDTO(Long shopId, Long userId, Long objectId, ObjectTypes objectType, OperationTypes operationType) {
    this.shopId = shopId;
    this.userId = userId;
    this.objectId = objectId;
    this.objectType = objectType;
    this.operationType = operationType;
  }

  public void setAppointOrderOperation(AppointOrderDTO appointOrderDTO) {
    if(appointOrderDTO != null){
      setObjectId(appointOrderDTO.getId());
      setObjectType(ObjectTypes.APPOINT_ORDER);
      setShopId(appointOrderDTO.getShopId());
      setUserId(appointOrderDTO.getUserId());
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
    if(creationDate!=null){
      this.creationDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,creationDate);
    }
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  public ObjectTypes getObjectType() {
    return objectType;
  }

  public void setObjectType(ObjectTypes objectType) {
    this.objectType = objectType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public OperationTypes getOperationType() {
    return operationType;
  }

  public void setOperationType(OperationTypes operationType) {
    this.operationType = operationType;
  }

  public String getCreationDateStr() {
    return creationDateStr;
  }

  public void setCreationDateStr(String creationDateStr) {
    this.creationDateStr = creationDateStr;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

}
