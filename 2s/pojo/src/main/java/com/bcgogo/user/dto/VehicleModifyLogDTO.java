package com.bcgogo.user.dto;

import com.bcgogo.enums.StatProcessStatus;
import com.bcgogo.enums.VehicleModifyFields;
import com.bcgogo.enums.VehicleModifyOperations;
import com.bcgogo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-11-13
 * Time: 上午9:05
 */
public class VehicleModifyLogDTO {
  private Long id;
  private Long creationDate;
  private Long vehicleId;
	private Long shopId;
  private Long userId;
  private Long operationId;
	private VehicleModifyOperations operationType;
  private VehicleModifyFields fieldName;
  private String oldValue;
  private String newValue;
  private StatProcessStatus statProcessStatus;

  private String brand;
  private String model;

  //车型变动监听时使用
  private String oldBrand;
  private String newBrand;
  private String oldModel;
  private String newModel;

  public static List<VehicleModifyLogDTO> compare(VehicleModifyLogDTO oldLog, VehicleModifyLogDTO newLog){
    List<VehicleModifyLogDTO> list = new ArrayList<VehicleModifyLogDTO>();
    if (!StringUtil.compareSame(oldLog.getBrand(), newLog.getBrand()) || !StringUtil.compareSame(oldLog.getModel(), newLog.getModel())) {
      //车型统计需获取每次改动的新旧值
      VehicleModifyLogDTO dto = new VehicleModifyLogDTO();
      dto.setFieldName(VehicleModifyFields.brand);
      dto.setOldValue(oldLog.getBrand());
      dto.setNewValue(newLog.getBrand());
      list.add(dto);
      dto = new VehicleModifyLogDTO();
      dto.setFieldName(VehicleModifyFields.model);
      dto.setOldValue(oldLog.getModel());
      dto.setNewValue(newLog.getModel());
      list.add(dto);
    }
    return list;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
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

  public Long getOperationId() {
    return operationId;
  }

  public void setOperationId(Long operationId) {
    this.operationId = operationId;
  }

  public VehicleModifyOperations getOperationType() {
    return operationType;
  }

  public void setOperationType(VehicleModifyOperations operationType) {
    this.operationType = operationType;
  }

  public VehicleModifyFields getFieldName() {
    return fieldName;
  }

  public void setFieldName(VehicleModifyFields fieldName) {
    this.fieldName = fieldName;
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

  public StatProcessStatus getStatProcessStatus() {
    return statProcessStatus;
  }

  public void setStatProcessStatus(StatProcessStatus statProcessStatus) {
    this.statProcessStatus = statProcessStatus;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getOldBrand() {
    return oldBrand;
  }

  public void setOldBrand(String oldBrand) {
    this.oldBrand = oldBrand;
  }

  public String getNewBrand() {
    return newBrand;
  }

  public void setNewBrand(String newBrand) {
    this.newBrand = newBrand;
  }

  public String getOldModel() {
    return oldModel;
  }

  public void setOldModel(String oldModel) {
    this.oldModel = oldModel;
  }

  public String getNewModel() {
    return newModel;
  }

  public void setNewModel(String newModel) {
    this.newModel = newModel;
  }
}
