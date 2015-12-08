package com.bcgogo.txn.dto.secondary;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.RepairOrderOtherIncomeItemDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.MoneyUtil;
import org.apache.commons.collections.CollectionUtils;

public class RepairOrderSecondaryDTO {

  public RepairOrderSecondaryDTO() {
  }

  private Long id;
  private String idStr;
  private Long repairOrderId;
  private String repairOrderIdStr;
  private Long shopId;
  private String receipt;
  private Long customerId;
  private String customerIdStr;
  private String customerName;
  private String customerContact;
  private String customerMobile;
  private String customerLandline;
  private String customerAddress;
  private Long vehicleId;
  private String vehicleIdStr;
  private String vehicleLicense;
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleContact;
  private String vehicleMobile;
  private Long vehicleBuyDate;
  private String vehicleBuyDateStr;
  private String vehicleEngineNo;
  private String vehicleChassisNo;
  private String vehicleColor;
  private String vehicleHandover;
  private Double startMileage;
  private String fuelNumber;
  private String description;
  private String memo;
  private Long startDate;
  private String startDateStr;
  private Long endDate;
  private String endDateStr;
  private OrderSecondaryStatus status;
  private String statusStr;
  private String productSaler;

  private Double serviceTotal = 0.0;
  private Double salesTotal = 0.0;
  private Double otherIncomeTotal = 0.0;
  private Double total = 0.0;
  private String totalStr;
  private Double settledAmount = 0.0;
  private Double accountDebtAmount = 0.0;
  private Double accountDiscount = 0.0;
  private String salesName;

  private RepairOrderServiceSecondaryDTO[] serviceDTOs;
  private RepairOrderItemSecondaryDTO[] itemDTOs;
  private RepairOrderOtherIncomeItemSecondaryDTO[] otherIncomeItemDTOs;
  private RepairOrderSettlementSecondaryDTO[] repairOrderSettlementSecondaryDTOs;

  private Long[] deleteServiceDTOs;
  private Long[] deleteItemDTOs;
  private Long[] deleteOtherIncomeItemDTOs;

  private Boolean again = new Boolean(false);

  public void fromRepairOrderDTO(RepairOrderDTO repairOrderDTO) {
    setRepairOrderId(repairOrderDTO.getId());
    setShopId(repairOrderDTO.getShopId());
    setReceipt(repairOrderDTO.getReceiptNo());
    setCustomerId(repairOrderDTO.getCustomerId());
    setCustomerName(repairOrderDTO.getCustomerName());
    setCustomerContact(repairOrderDTO.getContact());
    setCustomerMobile(repairOrderDTO.getMobile());
    setCustomerLandline(repairOrderDTO.getLandLine());
    setVehicleId(repairOrderDTO.getVechicleId());
    setVehicleLicense(repairOrderDTO.getLicenceNo());
    setVehicleContact(repairOrderDTO.getVehicleContact());
    setVehicleMobile(repairOrderDTO.getVehicleMobile());
    setVehicleBrand(repairOrderDTO.getBrand());
    setVehicleModel(repairOrderDTO.getModel());
    setVehicleHandover(repairOrderDTO.getVehicleHandover());
    setStartMileage(repairOrderDTO.getStartMileage());
    setFuelNumber(repairOrderDTO.getFuelNumber());
    setDescription(repairOrderDTO.getDescription());
    setMemo(repairOrderDTO.getMemo());
    setStartDate(repairOrderDTO.getStartDate());
    setEndDate(repairOrderDTO.getEndDate());
    setProductSaler(repairOrderDTO.getProductSaler());
    setServiceTotal(repairOrderDTO.getServiceTotal());
    setSalesTotal(repairOrderDTO.getSalesTotal());
    setOtherIncomeTotal(repairOrderDTO.getOtherIncomeTotal());
    setTotal(repairOrderDTO.getTotal());
    setCustomerAddress(repairOrderDTO.getAddress());
    setVehicleBuyDate(repairOrderDTO.getVehicleBuyDate());
    setVehicleEngineNo(repairOrderDTO.getVehicleEngineNo());
    setVehicleColor(repairOrderDTO.getVehicleColor());

    if (repairOrderDTO.getServiceDTOs() != null && repairOrderDTO.getServiceDTOs().length > 0) {
      serviceDTOs = new RepairOrderServiceSecondaryDTO[repairOrderDTO.getServiceDTOs().length];
      for (int i = 0; i < repairOrderDTO.getServiceDTOs().length; i++) {
        serviceDTOs[i] = new RepairOrderServiceSecondaryDTO();
        serviceDTOs[i].fromRepairOrderServiceDTO(repairOrderDTO.getServiceDTOs()[i]);
      }
    }

    if (repairOrderDTO.getItemDTOs() != null) {
      itemDTOs = new RepairOrderItemSecondaryDTO[repairOrderDTO.getItemDTOs().length];
      for (int i = 0; i < repairOrderDTO.getItemDTOs().length; i++) {
        itemDTOs[i] = new RepairOrderItemSecondaryDTO();
        itemDTOs[i].fromRepairOrderItemDTO(repairOrderDTO.getItemDTOs()[i]);
      }
    }

    if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
      otherIncomeItemDTOs = new RepairOrderOtherIncomeItemSecondaryDTO[repairOrderDTO.getOtherIncomeItemDTOList().size()];
      int i = 0;
      for (RepairOrderOtherIncomeItemDTO repairOrderOtherIncomeItemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
        otherIncomeItemDTOs[i] = new RepairOrderOtherIncomeItemSecondaryDTO();
        otherIncomeItemDTOs[i].fromRepairOrderOtherIncomeItemDTO(repairOrderOtherIncomeItemDTO);
        i++;
      }
    }
  }


  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getReceipt() {
    return receipt;
  }

  public void setReceipt(String receipt) {
    this.receipt = receipt;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerIdStr = customerId == null ? "" : customerId.toString();
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  public String getCustomerLandline() {
    return customerLandline;
  }

  public void setCustomerLandline(String customerLandline) {
    this.customerLandline = customerLandline;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleIdStr = vehicleId == null ? "" : vehicleId.toString();
    this.vehicleId = vehicleId;
  }

  public String getVehicleLicense() {
    return vehicleLicense;
  }

  public void setVehicleLicense(String vehicleLicense) {
    this.vehicleLicense = vehicleLicense;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  public String getFuelNumber() {
    return fuelNumber;
  }

  public void setFuelNumber(String fuelNumber) {
    this.fuelNumber = fuelNumber;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    if (startDate != null) {
      startDateStr = DateUtil.convertDateLongToString(startDate, "yyyy-MM-dd HH:mm");
    }
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    if (endDate != null) {
      this.endDateStr = DateUtil.convertDateLongToString(endDate, "yyyy-MM-dd HH:mm");
    }
    this.endDate = endDate;
  }

  public OrderSecondaryStatus getStatus() {
    return status;
  }

  public void setStatus(OrderSecondaryStatus status) {
    if (status != null) {
      statusStr = status.getName();
    }
    this.status = status;
  }

  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderIdStr = repairOrderId == null ? "" : repairOrderId.toString();
    this.repairOrderId = repairOrderId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.idStr = id == null ? "" : id.toString();
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getRepairOrderIdStr() {
    return repairOrderIdStr;
  }

  public void setRepairOrderIdStr(String repairOrderIdStr) {
    this.repairOrderIdStr = repairOrderIdStr;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getVehicleIdStr() {
    return vehicleIdStr;
  }

  public void setVehicleIdStr(String vehicleIdStr) {
    this.vehicleIdStr = vehicleIdStr;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public RepairOrderServiceSecondaryDTO[] getServiceDTOs() {
    return serviceDTOs;
  }

  public void setServiceDTOs(RepairOrderServiceSecondaryDTO[] serviceDTOs) {
    this.serviceDTOs = serviceDTOs;
  }

  public RepairOrderItemSecondaryDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(RepairOrderItemSecondaryDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public RepairOrderOtherIncomeItemSecondaryDTO[] getOtherIncomeItemDTOs() {
    return otherIncomeItemDTOs;
  }

  public void setOtherIncomeItemDTOs(RepairOrderOtherIncomeItemSecondaryDTO[] otherIncomeItemDTOs) {
    this.otherIncomeItemDTOs = otherIncomeItemDTOs;
  }

  public String getProductSaler() {
    return productSaler;
  }

  public void setProductSaler(String productSaler) {
    this.productSaler = productSaler;
  }

  public Double getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceTotal(Double serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public Double getSalesTotal() {
    return salesTotal;
  }

  public void setSalesTotal(Double salesTotal) {
    this.salesTotal = salesTotal;
  }

  public Double getOtherIncomeTotal() {
    return otherIncomeTotal;
  }

  public void setOtherIncomeTotal(Double otherIncomeTotal) {
    this.otherIncomeTotal = otherIncomeTotal;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    if (total != null) {
      totalStr = MoneyUtil.toBigType(total.toString());
    }
    this.total = total;
  }

  public Long[] getDeleteServiceDTOs() {
    return deleteServiceDTOs;
  }

  public void setDeleteServiceDTOs(Long[] deleteServiceDTOs) {
    this.deleteServiceDTOs = deleteServiceDTOs;
  }

  public Long[] getDeleteItemDTOs() {
    return deleteItemDTOs;
  }

  public void setDeleteItemDTOs(Long[] deleteItemDTOs) {
    this.deleteItemDTOs = deleteItemDTOs;
  }

  public Long[] getDeleteOtherIncomeItemDTOs() {
    return deleteOtherIncomeItemDTOs;
  }

  public void setDeleteOtherIncomeItemDTOs(Long[] deleteOtherIncomeItemDTOs) {
    this.deleteOtherIncomeItemDTOs = deleteOtherIncomeItemDTOs;
  }

  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public Double getAccountDebtAmount() {
    return accountDebtAmount;
  }

  public void setAccountDebtAmount(Double accountDebtAmount) {
    this.accountDebtAmount = accountDebtAmount;
  }

  public Double getAccountDiscount() {
    return accountDiscount;
  }

  public void setAccountDiscount(Double accountDiscount) {
    this.accountDiscount = accountDiscount;
  }

  public String getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  public Long getVehicleBuyDate() {
    return vehicleBuyDate;
  }

  public void setVehicleBuyDate(Long vehicleBuyDate) {
    if (vehicleBuyDate != null) {
      vehicleBuyDateStr = DateUtil.convertDateLongToString(vehicleBuyDate, "yyyy-MM-dd");
    }
    this.vehicleBuyDate = vehicleBuyDate;
  }

  public String getVehicleBuyDateStr() {
    return vehicleBuyDateStr;
  }

  public void setVehicleBuyDateStr(String vehicleBuyDateStr) {
    this.vehicleBuyDateStr = vehicleBuyDateStr;
  }

  public String getVehicleEngineNo() {
    return vehicleEngineNo;
  }

  public void setVehicleEngineNo(String vehicleEngineNo) {
    this.vehicleEngineNo = vehicleEngineNo;
  }

  public String getVehicleChassisNo() {
    return vehicleChassisNo;
  }

  public void setVehicleChassisNo(String vehicleChassisNo) {
    this.vehicleChassisNo = vehicleChassisNo;
  }

  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
  }

  public String getSalesName() {
    return salesName;
  }

  public void setSalesName(String salesName) {
    this.salesName = salesName;
  }

  public RepairOrderSettlementSecondaryDTO[] getRepairOrderSettlementSecondaryDTOs() {
    return repairOrderSettlementSecondaryDTOs;
  }

  public void setRepairOrderSettlementSecondaryDTOs(RepairOrderSettlementSecondaryDTO[] repairOrderSettlementSecondaryDTOs) {
    this.repairOrderSettlementSecondaryDTOs = repairOrderSettlementSecondaryDTOs;
  }

  public Boolean getAgain() {
    return again;
  }

  public void setAgain(Boolean again) {
    this.again = again;
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public String getCustomerContact() {
    return customerContact;
  }

  public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
  }

  public String getVehicleHandover() {
    return vehicleHandover;
  }

  public void setVehicleHandover(String vehicleHandover) {
    this.vehicleHandover = vehicleHandover;
  }

  public String getTotalStr() {
    return totalStr;
  }

  public void setTotalStr(String totalStr) {
    this.totalStr = totalStr;
  }
}

