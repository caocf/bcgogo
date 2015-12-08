package com.bcgogo.txn.model.secondary;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.secondary.OrderSecondaryStatus;
import com.bcgogo.txn.dto.secondary.RepairOrderSecondaryDTO;

import javax.persistence.*;

@Entity
@Table(name = "repair_order_secondary")
public class RepairOrderSecondary extends LongIdentifier {
  public RepairOrderSecondary() {
  }

  private Long repairOrderId;
  private Long shopId;
  private String receipt;
  private Long customerId;
  private String customerName;
  private String customerContact;
  private String customerMobile;
  private String customerLandline;
  private String customerAddress;
  private Long vehicleId;
  private String vehicleLicense;
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleContact;
  private String vehicleMobile;
  private Long vehicleBuyDate;
  private String vehicleEngineNo;
  private String vehicleChassisNo;
  private String vehicleColor;
  private String vehicleHandover;
  private Double startMileage;
  private String fuelNumber;
  private String description;
  private String memo;
  private Long startDate;
  private Long endDate;
  private OrderSecondaryStatus status;
  private String productSaler;
  private Double serviceTotal;
  private Double salesTotal;
  private Double otherIncomeTotal;
  private Double total;
  private Double settledAmount;
  private Double accountDebtAmount;
  private Double accountDiscount;

  public RepairOrderSecondaryDTO toDTO() {
    RepairOrderSecondaryDTO repairOrderSecondaryDTO = new RepairOrderSecondaryDTO();
    repairOrderSecondaryDTO.setId(getId());
    repairOrderSecondaryDTO.setRepairOrderId(repairOrderId);
    repairOrderSecondaryDTO.setShopId(shopId);
    repairOrderSecondaryDTO.setReceipt(receipt);
    repairOrderSecondaryDTO.setCustomerId(customerId);
    repairOrderSecondaryDTO.setCustomerName(customerName);
    repairOrderSecondaryDTO.setCustomerContact(customerContact);
    repairOrderSecondaryDTO.setCustomerMobile(customerMobile);
    repairOrderSecondaryDTO.setCustomerLandline(customerLandline);
    repairOrderSecondaryDTO.setVehicleId(vehicleId);
    repairOrderSecondaryDTO.setVehicleLicense(vehicleLicense);
    repairOrderSecondaryDTO.setVehicleBrand(vehicleBrand);
    repairOrderSecondaryDTO.setVehicleMobile(vehicleMobile);
    repairOrderSecondaryDTO.setVehicleContact(vehicleContact);
    repairOrderSecondaryDTO.setVehicleModel(vehicleModel);
    repairOrderSecondaryDTO.setVehicleHandover(vehicleHandover);
    repairOrderSecondaryDTO.setStartMileage(startMileage);
    repairOrderSecondaryDTO.setFuelNumber(fuelNumber);
    repairOrderSecondaryDTO.setDescription(description);
    repairOrderSecondaryDTO.setMemo(memo);
    repairOrderSecondaryDTO.setStartDate(startDate);
    repairOrderSecondaryDTO.setEndDate(endDate);
    repairOrderSecondaryDTO.setStatus(status);
    repairOrderSecondaryDTO.setProductSaler(productSaler);
    repairOrderSecondaryDTO.setServiceTotal(serviceTotal);
    repairOrderSecondaryDTO.setSalesTotal(salesTotal);
    repairOrderSecondaryDTO.setOtherIncomeTotal(otherIncomeTotal);
    repairOrderSecondaryDTO.setTotal(total);
    repairOrderSecondaryDTO.setSettledAmount(settledAmount);
    repairOrderSecondaryDTO.setAccountDebtAmount(accountDebtAmount);
    repairOrderSecondaryDTO.setAccountDiscount(accountDiscount);
    repairOrderSecondaryDTO.setCustomerAddress(customerAddress);
    repairOrderSecondaryDTO.setVehicleBuyDate(vehicleBuyDate);
    repairOrderSecondaryDTO.setVehicleEngineNo(vehicleEngineNo);
    repairOrderSecondaryDTO.setVehicleChassisNo(vehicleChassisNo);
    repairOrderSecondaryDTO.setVehicleColor(vehicleColor);
    return repairOrderSecondaryDTO;
  }

  public void fromDTO(RepairOrderSecondaryDTO repairOrderSecondaryDTO) {
    setRepairOrderId(repairOrderSecondaryDTO.getRepairOrderId());
    setShopId(repairOrderSecondaryDTO.getShopId());
    setReceipt(repairOrderSecondaryDTO.getReceipt());
    setCustomerId(repairOrderSecondaryDTO.getCustomerId());
    setCustomerName(repairOrderSecondaryDTO.getCustomerName());
    setCustomerContact(repairOrderSecondaryDTO.getCustomerContact());
    setCustomerMobile(repairOrderSecondaryDTO.getCustomerMobile());
    setCustomerLandline(repairOrderSecondaryDTO.getCustomerLandline());
    setVehicleId(repairOrderSecondaryDTO.getVehicleId());
    setVehicleLicense(repairOrderSecondaryDTO.getVehicleLicense());
    setVehicleBrand(repairOrderSecondaryDTO.getVehicleBrand());
    setVehicleMobile(repairOrderSecondaryDTO.getVehicleMobile());
    setVehicleContact(repairOrderSecondaryDTO.getVehicleContact());
    setVehicleModel(repairOrderSecondaryDTO.getVehicleModel());
    setVehicleHandover(repairOrderSecondaryDTO.getVehicleHandover());
    setStartMileage(repairOrderSecondaryDTO.getStartMileage());
    setFuelNumber(repairOrderSecondaryDTO.getFuelNumber());
    setDescription(repairOrderSecondaryDTO.getDescription());
    setMemo(repairOrderSecondaryDTO.getMemo());
    setStartDate(repairOrderSecondaryDTO.getStartDate());
    setEndDate(repairOrderSecondaryDTO.getEndDate());
    setStatus(repairOrderSecondaryDTO.getStatus());
    setProductSaler(repairOrderSecondaryDTO.getProductSaler());
    setServiceTotal(repairOrderSecondaryDTO.getServiceTotal());
    setSalesTotal(repairOrderSecondaryDTO.getSalesTotal());
    setOtherIncomeTotal(repairOrderSecondaryDTO.getOtherIncomeTotal());
    setTotal(repairOrderSecondaryDTO.getTotal());
    setSettledAmount(repairOrderSecondaryDTO.getSettledAmount());
    setAccountDebtAmount(repairOrderSecondaryDTO.getAccountDebtAmount());
    setAccountDiscount(repairOrderSecondaryDTO.getAccountDiscount());
    setVehicleBuyDate(repairOrderSecondaryDTO.getVehicleBuyDate());
    setVehicleEngineNo(repairOrderSecondaryDTO.getVehicleEngineNo());
    setVehicleChassisNo(repairOrderSecondaryDTO.getVehicleChassisNo());
    setVehicleColor(repairOrderSecondaryDTO.getVehicleColor());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "customer_name")
  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  @Column(name = "customer_mobile")
  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  @Column(name = "customer_landline")
  public String getCustomerLandline() {
    return customerLandline;
  }

  public void setCustomerLandline(String customerLandline) {
    this.customerLandline = customerLandline;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Column(name = "vehicle_license")
  public String getVehicleLicense() {
    return vehicleLicense;
  }

  public void setVehicleLicense(String vehicleLicense) {
    this.vehicleLicense = vehicleLicense;
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

  @Column(name = "vehicle_contact")
  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  @Column(name = "vehicle_mobile")
  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  @Column(name = "start_mileage")
  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  @Column(name = "fuel_number")
  public String getFuelNumber() {
    return fuelNumber;
  }

  public void setFuelNumber(String fuelNumber) {
    this.fuelNumber = fuelNumber;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "start_date")
  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  @Column(name = "end_date")
  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public OrderSecondaryStatus getStatus() {
    return status;

  }

  public void setStatus(OrderSecondaryStatus status) {
    this.status = status;
  }

  @Column(name = "receipt")
  public String getReceipt() {
    return receipt;
  }


  public void setReceipt(String receipt) {
    this.receipt = receipt;
  }

  @Column(name = "repair_order_id")
  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  @Column(name = "product_saler")
  public String getProductSaler() {
    return productSaler;
  }

  public void setProductSaler(String productSaler) {
    this.productSaler = productSaler;
  }

  @Column(name = "service_total")
  public Double getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceTotal(Double serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  @Column(name = "sales_total")
  public Double getSalesTotal() {
    return salesTotal;
  }

  public void setSalesTotal(Double salesTotal) {
    this.salesTotal = salesTotal;
  }

  @Column(name = "other_income_total")
  public Double getOtherIncomeTotal() {
    return otherIncomeTotal;
  }

  public void setOtherIncomeTotal(Double otherIncomeTotal) {
    this.otherIncomeTotal = otherIncomeTotal;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "settled_amount")
  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  @Column(name = "account_debt_amount")
  public Double getAccountDebtAmount() {
    return accountDebtAmount;
  }

  public void setAccountDebtAmount(Double accountDebtAmount) {
    this.accountDebtAmount = accountDebtAmount;
  }

  @Column(name = "account_discount")
  public Double getAccountDiscount() {
    return accountDiscount;
  }

  public void setAccountDiscount(Double accountDiscount) {
    this.accountDiscount = accountDiscount;
  }

  @Column(name = "customer_address")
  public String getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  @Column(name = "vehicle_buy_date")
  public Long getVehicleBuyDate() {
    return vehicleBuyDate;
  }

  public void setVehicleBuyDate(Long vehicleBuyDate) {
    this.vehicleBuyDate = vehicleBuyDate;
  }

  @Column(name = "vehicle_engine_no")
  public String getVehicleEngineNo() {
    return vehicleEngineNo;
  }

  public void setVehicleEngineNo(String vehicleEngineNo) {
    this.vehicleEngineNo = vehicleEngineNo;
  }

  @Column(name = "vehicle_chassis_no")
  public String getVehicleChassisNo() {
    return vehicleChassisNo;
  }

  public void setVehicleChassisNo(String vehicleChassisNo) {
    this.vehicleChassisNo = vehicleChassisNo;
  }

  @Column(name = "vehicle_color")
  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
  }

  @Column(name = "customer_contact")
  public String getCustomerContact() {
    return customerContact;
  }

  public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
  }

  @Column(name = "vehicle_handover")
  public String getVehicleHandover() {
    return vehicleHandover;
  }

  public void setVehicleHandover(String vehicleHandover) {
    this.vehicleHandover = vehicleHandover;
  }

}
