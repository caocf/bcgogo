package com.bcgogo.api;

import com.bcgogo.enums.app.OBDSimOperationType;
import com.bcgogo.enums.user.ObdSimOperationTargetType;
import com.bcgogo.utils.DateUtil;

/**
 * Created by XinyuQiu on 14-6-16.
 */
public class OBDSimOperationLogDTO {
  private Long shopId;
  private Long userId;//操作人
  private String userName;//操作人姓名
  private Long operationDate;//操作时间
  private String operationDateStr;
  private OBDSimOperationType operationType;//操作类型
  private String operationTypeStr;
  private Long obdId;
  private Long simId;
  private Long obdHistoryId;
  private Long simHistoryId;
  private String content;//操作内容
  private ObdSimOperationTargetType targetType;//操作对象类型
  private Long targetId;//操作对象id

  public void fromObdSimBindDTO(ObdSimBindDTO obdSimBindDTO) {
    if(obdSimBindDTO != null){
      setShopId(obdSimBindDTO.getOperateShopId());
      setUserId(obdSimBindDTO.getOperateUserId());
      setUserName(obdSimBindDTO.getOperateUserName());
      setOperationDate(System.currentTimeMillis());
      setObdId(obdSimBindDTO.getObdId());
      setSimId(obdSimBindDTO.getSimId());
      setObdHistoryId(obdSimBindDTO.getObdHistoryId());
      setSimHistoryId(obdSimBindDTO.getSimHistoryId());
    }
  }

  public void fromObdSimOutStorageDTO(ObdSimOutStorageDTO outStorageDTO) {
    if(outStorageDTO != null){
      setShopId(outStorageDTO.getOperationShopId());
      setUserId(outStorageDTO.getOperationUserId());
      setUserName(outStorageDTO.getOperationName());
      setOperationDate(System.currentTimeMillis());
    }
  }

  public void fromObdSimReturnDTO(ObdSimReturnDTO obdSimReturnDTO) {
    if(obdSimReturnDTO != null){
      setShopId(obdSimReturnDTO.getOperationShopId());
      setUserId(obdSimReturnDTO.getOperationUserId());
      setUserName(obdSimReturnDTO.getOperationName());
      setOperationDate(System.currentTimeMillis());
    }
  }


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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Long getOperationDate() {
    return operationDate;
  }

  public void setOperationDate(Long operationDate) {
    this.operationDate = operationDate;
    if(operationDate != null){
      setOperationDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT,operationDate));
    }else {
      setOperationDateStr("");
    }
  }

  public OBDSimOperationType getOperationType() {
    return operationType;
  }

  public void setOperationType(OBDSimOperationType operationType) {
    this.operationType = operationType;
    if(operationType!=null){
      setOperationTypeStr(operationType.getName());
    }else {
      setOperationTypeStr("");
    }
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public Long getSimId() {
    return simId;
  }

  public void setSimId(Long simId) {
    this.simId = simId;
  }

  public Long getObdHistoryId() {
    return obdHistoryId;
  }

  public void setObdHistoryId(Long obdHistoryId) {
    this.obdHistoryId = obdHistoryId;
  }

  public Long getSimHistoryId() {
    return simHistoryId;
  }

  public void setSimHistoryId(Long simHistoryId) {
    this.simHistoryId = simHistoryId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ObdSimOperationTargetType getTargetType() {
    return targetType;
  }

  public void setTargetType(ObdSimOperationTargetType targetType) {
    this.targetType = targetType;
  }

  public Long getTargetId() {
    return targetId;
  }

  public void setTargetId(Long targetId) {
    this.targetId = targetId;
  }

  public String getOperationDateStr() {
    return operationDateStr;
  }

  public void setOperationDateStr(String operationDateStr) {
    this.operationDateStr = operationDateStr;
  }

  public String getOperationTypeStr() {
    return operationTypeStr;
  }

  public void setOperationTypeStr(String operationTypeStr) {
    this.operationTypeStr = operationTypeStr;
  }


}
