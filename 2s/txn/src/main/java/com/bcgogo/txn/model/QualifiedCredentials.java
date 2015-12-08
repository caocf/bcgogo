package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.QualifiedCredentialsDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-1-15
 * Time: 下午2:20
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="qualified_credentials")
public class QualifiedCredentials extends LongIdentifier{

  private Long shopId;
  private String customer;  //托修方
  private Long customerId;
  private String licenseNo;  //车牌号
  private String engineNo;   //发动机号
  private String chassisNumber; //车架号
  private String repairType; //维修类型
  private String repairContractNo;  //维修合同编号
  private String producedMileage;  //出产里程
  private String brand;  //车辆品牌
  private String model;  //车型
  private String qualityInspectors;  //质量检测员
  private String shopName;  //承修单位
  private String startDateStr;  //进厂日期
  private Long startDate;
  private String endDateStr;   //出产日期
  private Long endDate;
  private Long orderId;   //施工单id;
  private String no;  //合格证编号
  private String repairInvoiceNumber;   //维修发票号
  private String travelDate;    //质量保证行驶日期
  private String travelLength;  //质量保证行驶距离

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name="customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name="license_no")
  public String getLicenseNo() {
    return licenseNo;
  }

  public void setLicenseNo(String licenseNo) {
    this.licenseNo = licenseNo;
  }

  @Column(name="engine_no")
  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  @Column(name="chassis_number")
  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  @Column(name="repair_type")
  public String getRepairType() {
    return repairType;
  }

  public void setRepairType(String repairType) {
    this.repairType = repairType;
  }

  @Column(name="repair_contract_no")
  public String getRepairContractNo() {
    return repairContractNo;
  }

  public void setRepairContractNo(String repairContractNo) {
    this.repairContractNo = repairContractNo;
  }

  @Column(name="produced_mileage")
  public String getProducedMileage() {
    return producedMileage;
  }

  public void setProducedMileage(String producedMileage) {
    this.producedMileage = producedMileage;
  }

  @Column(name="brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name="model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name="quality_inspectors")
  public String getQualityInspectors() {
    return qualityInspectors;
  }

  public void setQualityInspectors(String qualityInspectors) {
    this.qualityInspectors = qualityInspectors;
  }

  @Column(name="shop_name")
  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  @Column(name="start_date_str")
  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  @Column(name="start_date")
  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  @Column(name="end_date_str")
  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  @Column(name="end_date")
  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  @Column(name="order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name="no")
  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  @Column(name="repair_invoice_number")
  public String getRepairInvoiceNumber() {
    return repairInvoiceNumber;
  }

  public void setRepairInvoiceNumber(String repairInvoiceNumber) {
    this.repairInvoiceNumber = repairInvoiceNumber;
  }

  @Column(name="travel_date")
  public String getTravelDate() {
    return travelDate;
  }

  public void setTravelDate(String travelDate) {
    this.travelDate = travelDate;
  }

  @Column(name="travel_length")
  public String getTravelLength() {
    return travelLength;
  }

  public void setTravelLength(String travelLength) {
    this.travelLength = travelLength;
  }

  public QualifiedCredentialsDTO toDTO()
  {
    QualifiedCredentialsDTO qualifiedCredentialsDTO = new QualifiedCredentialsDTO();
    qualifiedCredentialsDTO.setId(this.getId());
    qualifiedCredentialsDTO.setShopId(this.getShopId());
    qualifiedCredentialsDTO.setCustomer(this.getCustomer());
    qualifiedCredentialsDTO.setCustomerId(this.getCustomerId());
    qualifiedCredentialsDTO.setLicenseNo(this.getLicenseNo());
    qualifiedCredentialsDTO.setEngineNo(this.getEngineNo());
    qualifiedCredentialsDTO.setChassisNumber(this.getChassisNumber());
    qualifiedCredentialsDTO.setRepairType(this.getRepairType());
    qualifiedCredentialsDTO.setRepairContractNo(this.getRepairContractNo());
    qualifiedCredentialsDTO.setProducedMileage(this.getProducedMileage());
    qualifiedCredentialsDTO.setBrand(this.getBrand());
    qualifiedCredentialsDTO.setModel(this.getModel());
    qualifiedCredentialsDTO.setQualityInspectors(this.getQualityInspectors());
    qualifiedCredentialsDTO.setShopName(this.getShopName());
    qualifiedCredentialsDTO.setStartDateStr(this.getStartDateStr());
    qualifiedCredentialsDTO.setStartDate(this.getStartDate());
    qualifiedCredentialsDTO.setEndDate(this.getEndDate());
    qualifiedCredentialsDTO.setEndDateStr(this.getEndDateStr());
    qualifiedCredentialsDTO.setOrderId(this.getOrderId());
    qualifiedCredentialsDTO.setNo(this.getNo());
    qualifiedCredentialsDTO.setRepairInvoiceNumber(this.getRepairInvoiceNumber());
    qualifiedCredentialsDTO.setTravelDate(this.getTravelDate());
    qualifiedCredentialsDTO.setTravelLength(this.getTravelLength());

    return qualifiedCredentialsDTO;
  }

  public QualifiedCredentials(){
  }

  public QualifiedCredentials(QualifiedCredentialsDTO qualifiedCredentialsDTO)
  {
    if(null != qualifiedCredentialsDTO)
    {
      this.setId(qualifiedCredentialsDTO.getId());
      this.setShopId(qualifiedCredentialsDTO.getShopId());
      this.setCustomer(qualifiedCredentialsDTO.getCustomer());
      this.setCustomerId(qualifiedCredentialsDTO.getCustomerId());
      this.setLicenseNo(qualifiedCredentialsDTO.getLicenseNo());
      this.setEngineNo(qualifiedCredentialsDTO.getEngineNo());
      this.setChassisNumber(qualifiedCredentialsDTO.getChassisNumber());
      this.setRepairType(qualifiedCredentialsDTO.getRepairType());
      this.setRepairContractNo(qualifiedCredentialsDTO.getRepairContractNo());
      this.setProducedMileage(qualifiedCredentialsDTO.getProducedMileage());
      this.setBrand(qualifiedCredentialsDTO.getBrand());
      this.setModel(qualifiedCredentialsDTO.getModel());
      this.setQualityInspectors(qualifiedCredentialsDTO.getQualityInspectors());
      this.setShopName(qualifiedCredentialsDTO.getShopName());
      this.setStartDateStr(qualifiedCredentialsDTO.getStartDateStr());
      this.setStartDate(qualifiedCredentialsDTO.getStartDate());
      this.setEndDate(qualifiedCredentialsDTO.getEndDate());
      this.setEndDateStr(qualifiedCredentialsDTO.getEndDateStr());
      this.setOrderId(qualifiedCredentialsDTO.getOrderId());
      this.setNo(qualifiedCredentialsDTO.getNo());
      this.setRepairInvoiceNumber(qualifiedCredentialsDTO.getRepairInvoiceNumber());
      this.setTravelDate(qualifiedCredentialsDTO.getTravelDate());
      this.setTravelLength(qualifiedCredentialsDTO.getTravelLength());
    }
  }

  public void fromDTO(QualifiedCredentialsDTO qualifiedCredentialsDTO,boolean flag)
  {
    if(null == qualifiedCredentialsDTO)
    {
      return;
    }
    if(flag)
    {
      this.setId(qualifiedCredentialsDTO.getId());
    }

    this.setShopId(qualifiedCredentialsDTO.getShopId());
    this.setCustomer(qualifiedCredentialsDTO.getCustomer());
    this.setCustomerId(qualifiedCredentialsDTO.getCustomerId());
    this.setLicenseNo(qualifiedCredentialsDTO.getLicenseNo());
    this.setEngineNo(qualifiedCredentialsDTO.getEngineNo());
    this.setChassisNumber(qualifiedCredentialsDTO.getChassisNumber());
    this.setRepairType(qualifiedCredentialsDTO.getRepairType());
    this.setRepairContractNo(qualifiedCredentialsDTO.getRepairContractNo());
    this.setProducedMileage(qualifiedCredentialsDTO.getProducedMileage());
    this.setBrand(qualifiedCredentialsDTO.getBrand());
    this.setModel(qualifiedCredentialsDTO.getModel());
    this.setQualityInspectors(qualifiedCredentialsDTO.getQualityInspectors());
    this.setShopName(qualifiedCredentialsDTO.getShopName());
    this.setStartDateStr(qualifiedCredentialsDTO.getStartDateStr());
    this.setStartDate(qualifiedCredentialsDTO.getStartDate());
    this.setEndDate(qualifiedCredentialsDTO.getEndDate());
    this.setEndDateStr(qualifiedCredentialsDTO.getEndDateStr());
    this.setOrderId(qualifiedCredentialsDTO.getOrderId());
    this.setNo(qualifiedCredentialsDTO.getNo());
    this.setRepairInvoiceNumber(qualifiedCredentialsDTO.getRepairInvoiceNumber());
    this.setTravelDate(qualifiedCredentialsDTO.getTravelDate());
    this.setTravelLength(qualifiedCredentialsDTO.getTravelLength());
  }
}
