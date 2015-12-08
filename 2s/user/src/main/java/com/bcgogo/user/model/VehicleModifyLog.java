package com.bcgogo.user.model;

import com.bcgogo.enums.StatProcessStatus;
import com.bcgogo.enums.VehicleModifyFields;
import com.bcgogo.enums.VehicleModifyOperations;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.VehicleModifyLogDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-11-13
 * Time: 上午9:00
 */
@Entity
@Table(name="vehicle_modify_log")
public class VehicleModifyLog extends LongIdentifier {
  private Long vehicleId;
	private Long shopId;
  private Long userId;
  private Long operationId;
	private VehicleModifyOperations operationType;
  private VehicleModifyFields fieldName;
  private String oldValue;
  private String newValue;
  private StatProcessStatus statProcessStatus;

  public VehicleModifyLog(){}

  public VehicleModifyLog(VehicleModifyLogDTO vehicleModifyLogDTO, boolean setId){
    if(vehicleModifyLogDTO == null){
      return;
    }
    if(setId){
      setId(vehicleModifyLogDTO.getId());
    }
    setVehicleId(vehicleModifyLogDTO.getVehicleId());
    setShopId(vehicleModifyLogDTO.getShopId());
    setUserId(vehicleModifyLogDTO.getUserId());
    setOperationId(vehicleModifyLogDTO.getOperationId());
    setOperationType(vehicleModifyLogDTO.getOperationType());
    setFieldName(vehicleModifyLogDTO.getFieldName());
    setOldValue(vehicleModifyLogDTO.getOldValue());
    setNewValue(vehicleModifyLogDTO.getNewValue());
    setStatProcessStatus(vehicleModifyLogDTO.getStatProcessStatus());
  }

  public VehicleModifyLogDTO toDTO(){
    VehicleModifyLogDTO dto = new VehicleModifyLogDTO();
    dto.setId(getId());
    dto.setVehicleId(getVehicleId());
    dto.setShopId(getShopId());
    dto.setUserId(getUserId());
    dto.setOperationId(getOperationId());
    dto.setOperationType(getOperationType());
    dto.setFieldName(getFieldName());
    dto.setOldValue(getOldValue());
    dto.setNewValue(getNewValue());
    dto.setStatProcessStatus(getStatProcessStatus());
    dto.setCreationDate(getCreationDate());
    return dto;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
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

  @Column(name = "operation_id")
  public Long getOperationId() {
    return operationId;
  }

  public void setOperationId(Long operationId) {
    this.operationId = operationId;
  }

  @Column(name = "operation_type")
  @Enumerated(EnumType.STRING)
  public VehicleModifyOperations getOperationType() {
    return operationType;
  }

  public void setOperationType(VehicleModifyOperations operationType) {
    this.operationType = operationType;
  }

  @Column(name = "field_name")
  @Enumerated(EnumType.STRING)
  public VehicleModifyFields getFieldName() {
    return fieldName;
  }

  public void setFieldName(VehicleModifyFields fieldName) {
    this.fieldName = fieldName;
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

  @Column(name = "stat_process_status")
  @Enumerated(EnumType.STRING)
  public StatProcessStatus getStatProcessStatus() {
    return statProcessStatus;
  }

  public void setStatProcessStatus(StatProcessStatus statProcessStatus) {
    this.statProcessStatus = statProcessStatus;
  }
}
