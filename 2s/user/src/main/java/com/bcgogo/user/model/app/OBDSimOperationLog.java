package com.bcgogo.user.model.app;

import com.bcgogo.api.OBDSimOperationLogDTO;
import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.enums.app.OBDSimOperationType;
import com.bcgogo.enums.user.ObdSimOperationTargetType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-6-16.
 */
@Entity
@Table(name = "obd_sim_operation_log")
public class OBDSimOperationLog extends LongIdentifier {
  private Long shopId;
  private Long userId;//操作人
  private String userName;//操作人姓名
  private Long operationDate;//操作时间
  private OBDSimOperationType operationType;//操作类型
  private Long obdId;
  private Long simId;
  private Long obdHistoryId;
  private Long simHistoryId;
  private String content;//操作内容
  private ObdSimOperationTargetType targetType;//操作对象类型
  private Long targetId;//操作对象id

  public void fromDTO(OBDSimOperationLogDTO dto){
    if(dto != null){
      setShopId(dto.getShopId());
      setUserId(dto.getUserId());
      setUserName(dto.getUserName());
      setOperationDate(dto.getOperationDate());
      setOperationType(dto.getOperationType());
      setObdId(dto.getObdId());
      setSimId(dto.getSimId());
      setObdHistoryId(dto.getObdHistoryId());
      setSimHistoryId(dto.getSimHistoryId());
      setContent(dto.getContent());
      setTargetId(dto.getTargetId());
      setTargetType(dto.getTargetType());
    }
  }

  public OBDSimOperationLogDTO toDTO() {
    OBDSimOperationLogDTO dto = new OBDSimOperationLogDTO();
    dto.setShopId(this.getShopId());
    dto.setUserId(this.getUserId());
    dto.setUserName(this.getUserName());
    dto.setOperationDate(this.getOperationDate());
    dto.setOperationType(this.getOperationType());
    dto.setObdId(this.getObdId());
    dto.setSimId(this.getSimId());
    dto.setObdHistoryId(this.getObdHistoryId());
    dto.setSimHistoryId(this.getSimHistoryId());
    dto.setContent(this.getContent());
    dto.setTargetId(this.getTargetId());
    dto.setTargetType(this.getTargetType());
    return dto;
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

  @Column(name = "user_name")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Column(name = "operation_date")
  public Long getOperationDate() {
    return operationDate;
  }

  public void setOperationDate(Long operationDate) {
    this.operationDate = operationDate;
  }

  @Column(name = "operation_type")
  @Enumerated(EnumType.STRING)
  public OBDSimOperationType getOperationType() {
    return operationType;
  }

  public void setOperationType(OBDSimOperationType operationType) {
    this.operationType = operationType;
  }

  @Column(name = "obd_id")
  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  @Column(name = "sim_id")
  public Long getSimId() {
    return simId;
  }

  public void setSimId(Long simId) {
    this.simId = simId;
  }

  @Column(name = "obd_history_id")
  public Long getObdHistoryId() {
    return obdHistoryId;
  }

  public void setObdHistoryId(Long obdHistoryId) {
    this.obdHistoryId = obdHistoryId;
  }

  @Column(name = "sim_history_id")
  public Long getSimHistoryId() {
    return simHistoryId;
  }

  public void setSimHistoryId(Long simHistoryId) {
    this.simHistoryId = simHistoryId;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "target_type")
  @Enumerated(EnumType.STRING)
  public ObdSimOperationTargetType getTargetType() {
    return targetType;
  }

  public void setTargetType(ObdSimOperationTargetType targetType) {
    this.targetType = targetType;
  }

  @Column(name = "target_id")
  public Long getTargetId() {
    return targetId;
  }

  public void setTargetId(Long targetId) {
    this.targetId = targetId;
  }



}
