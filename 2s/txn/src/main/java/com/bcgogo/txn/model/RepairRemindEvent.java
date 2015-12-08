package com.bcgogo.txn.model;

import com.bcgogo.enums.RepairRemindEventTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.LackMaterialDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.RepairOrderItemDTO;
import com.bcgogo.txn.dto.RepairRemindEventDTO;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "repair_remind_event")
public class RepairRemindEvent extends LongIdentifier {
  public RepairRemindEvent() {
  }

  public RepairRemindEvent fromDTO(RepairRemindEventDTO repairRemindEventDTO) {
    if(repairRemindEventDTO == null)
      return this;
    setId(repairRemindEventDTO.getId());
    this.shopId = repairRemindEventDTO.getShopId();
    this.repairOrderId = repairRemindEventDTO.getRepairOrderId();
    this.eventTypeEnum = repairRemindEventDTO.getEventType();
    this.eventContent = repairRemindEventDTO.getEventContent();
    this.amount = repairRemindEventDTO.getAmount();
    this.vehicleBrand = repairRemindEventDTO.getVehicleBrand();
    this.vehicleModel = repairRemindEventDTO.getVehicleModel();
    this.vehicleYear = repairRemindEventDTO.getVehicleYear();
    this.vehicleEngine = repairRemindEventDTO.getVehicleEngine();
    this.productName = repairRemindEventDTO.getProductName();
    this.productBrand = repairRemindEventDTO.getProductBrand();
    this.productSpec = repairRemindEventDTO.getProductSpec();
    this.productModel = repairRemindEventDTO.getProductModel();
    this.productId = repairRemindEventDTO.getProductId();
    this.vehicle = repairRemindEventDTO.getVehicle();
    this.customer = repairRemindEventDTO.getCustomer();
    this.mobile = repairRemindEventDTO.getMobile();
    this.service = repairRemindEventDTO.getService();
    this.finishTime = repairRemindEventDTO.getFinishTime();
    this.unit = repairRemindEventDTO.getUnit();
    return this;
  }

  public RepairRemindEventDTO toDTO() {
    RepairRemindEventDTO repairRemindEventDTO = new RepairRemindEventDTO();
    repairRemindEventDTO.setId(getId());
    repairRemindEventDTO.setShopId(getShopId());
    repairRemindEventDTO.setRepairOrderId(getRepairOrderId());
    repairRemindEventDTO.setEventType(getEventTypeEnum());
    repairRemindEventDTO.setEventContent(getEventContent());
    repairRemindEventDTO.setAmount(getAmount());
    repairRemindEventDTO.setVehicleBrand(getVehicleBrand());
    repairRemindEventDTO.setVehicleModel(getVehicleModel());
    repairRemindEventDTO.setVehicleYear(getVehicleYear());
    repairRemindEventDTO.setVehicleEngine(getVehicleEngine());
    repairRemindEventDTO.setProductName(getProductName());
    repairRemindEventDTO.setProductBrand(getProductBrand());
    repairRemindEventDTO.setProductSpec(getProductSpec());
    repairRemindEventDTO.setProductModel(getProductModel());
    repairRemindEventDTO.setProductId(getProductId());
    repairRemindEventDTO.setVehicle(getVehicle());
    repairRemindEventDTO.setCustomer(getCustomer());
    repairRemindEventDTO.setMobile(getMobile());
    repairRemindEventDTO.setService(getService());
    repairRemindEventDTO.setFinishTime(getFinishTime());
    repairRemindEventDTO.setUnit(getUnit());
    return repairRemindEventDTO;
  }

  public LackMaterialDTO toLackMaterialDTO() {
    LackMaterialDTO lackMaterialDTO = new LackMaterialDTO();
    lackMaterialDTO.setId(repairOrderId);
    if (amount != null) {
      lackMaterialDTO.setAmount(amount);
    }else{
      lackMaterialDTO.setAmount(0);
    }
    lackMaterialDTO.setCustomer(customer);
    lackMaterialDTO.setMobile(mobile);
    lackMaterialDTO.setProductId(productId);
    lackMaterialDTO.setProductModel(productModel);
    lackMaterialDTO.setProductName(productName);
    lackMaterialDTO.setProductSpec(productSpec);
    lackMaterialDTO.setService(service);
    lackMaterialDTO.setVechicle(vehicle);
    lackMaterialDTO.setVehicleBrand(vehicleBrand);
    lackMaterialDTO.setVehicleEngine(vehicleEngine);
    lackMaterialDTO.setVehicleModel(vehicleModel);
    lackMaterialDTO.setVehicleYear(vehicleYear);
    lackMaterialDTO.setStartDateStr(startDateStr);
    lackMaterialDTO.setEndDateStr(endDateStr);
    lackMaterialDTO.setUnit(unit);
    return lackMaterialDTO;
  }

  private Long shopId;
  private Long repairOrderId;
  /**
   * 1:待交付
   * 2:缺料待修
   * 5:来料待修
   */
  private Long eventType;
  private RepairRemindEventTypes eventTypeEnum;
  /**
   * 当eventType=1 为空
   * 当eventType=2 缺料产品的product_local_info
   * 5:来料待修 来料产品的product_local_info
   */
  private Long eventContent;
  private Double amount;
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleYear;
  private String vehicleEngine;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private Long productId;
  private String vehicle;
  private String customer;
  private String mobile;
  private String service;
  private Long finishTime;
  private String startDateStr;
  private String endDateStr;
  private String unit;

  @Column(name = "start_date")
  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }
  @Column(name = "end_date")
  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }


  @Column(name = "finish_time")
  public Long getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Long finishTime) {
    this.finishTime = finishTime;
  }

  @Column(name = "service")
  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = StringUtils.substring(service, 0, 200);
  }

  @Column(name = "vehicle")
  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  @Column(name = "customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "vehicle_brand")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name = "vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name = "vehicle_year")
  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }

  @Column(name = "vehicle_engine")
  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = StringUtils.substring(productName, 0, 200);
  }

  @Column(name = "product_spec")
  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  @Column(name = "product_model")
  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  @Column(name = "product_brand")
  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "repair_order_id")
  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  @Column(name = "event_type")
  public Long getEventType() {
    return eventType;
  }

  public void setEventType(Long eventType) {
    this.eventType = eventType;
  }

  @Column(name = "event_type_enum")
  @Enumerated(EnumType.STRING)
  public RepairRemindEventTypes getEventTypeEnum() {
    return eventTypeEnum;
  }

  public void setEventTypeEnum(RepairRemindEventTypes eventTypeEnum) {
    this.eventTypeEnum = eventTypeEnum;
  }

  @Column(name = "event_content")
  public Long getEventContent() {
    return eventContent;
  }

  public void setEventContent(Long eventContent) {
    this.eventContent = eventContent;
  }

  @Column(name = "unit", length = 20)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void createWaitOutStorageRemindEvent(RepairOrderDTO repairOrderDTO, RepairOrderItemDTO repairOrderItemDTO) {
    if (repairOrderDTO != null) {
      this.setShopId(repairOrderDTO.getShopId());
      this.setRepairOrderId(repairOrderDTO.getId());
      this.setVehicleBrand(repairOrderDTO.getBrand());
      this.setVehicleModel(repairOrderDTO.getModel());
      this.setVehicleEngine(repairOrderDTO.getEngine());
      this.setVehicleYear(repairOrderDTO.getYear());
      this.setAmount(repairOrderDTO.getSettledAmount());
      this.setCustomer(repairOrderDTO.getCustomerName());
      this.setMobile(repairOrderDTO.getMobile());
      this.setVehicle(repairOrderDTO.getVechicle());
      this.setStartDateStr(repairOrderDTO.getStartDateStr());
      this.setEndDateStr(repairOrderDTO.getEndDateStr());
      this.setFinishTime(repairOrderDTO.getEndDate());
      this.setService(StringUtils.substring(repairOrderDTO.getServiceStr(), 0, 200));
    }
    if (repairOrderItemDTO != null) {
      this.setProductId(repairOrderItemDTO.getProductId());
      this.setEventContent(repairOrderItemDTO.getProductId());
      this.setProductBrand(repairOrderItemDTO.getBrand());
      this.setProductName(repairOrderItemDTO.getProductName());
      this.setProductModel(repairOrderItemDTO.getModel());
      this.setProductSpec(repairOrderItemDTO.getSpec());
      this.setUnit(repairOrderItemDTO.getUnit());
      this.setAmount(repairOrderItemDTO.getAmount());
    }
  }

     //带交付提醒
  public void createPendingRemindEvent(RepairOrderDTO repairOrderDTO) {
    if (repairOrderDTO != null) {
      this.setShopId(repairOrderDTO.getShopId());
      this.setRepairOrderId(repairOrderDTO.getId());
      this.setVehicleBrand(repairOrderDTO.getBrand());
      this.setVehicleModel(repairOrderDTO.getModel());
      this.setVehicleEngine(repairOrderDTO.getEngine());
      this.setVehicleYear(repairOrderDTO.getYear());
      this.setAmount(repairOrderDTO.getSettledAmount());
      this.setCustomer(repairOrderDTO.getCustomerName());
      this.setMobile(repairOrderDTO.getMobile());
      this.setVehicle(repairOrderDTO.getVechicle());
      this.setStartDateStr(repairOrderDTO.getStartDateStr());
      this.setEndDateStr(repairOrderDTO.getEndDateStr());
      this.setFinishTime(repairOrderDTO.getEndDate());
      this.setService(StringUtils.substring(repairOrderDTO.getServiceStr(), 0, 200));
    }
  }
}
