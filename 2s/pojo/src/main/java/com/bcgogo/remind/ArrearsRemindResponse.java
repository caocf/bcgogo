package com.bcgogo.remind;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-14
 * Time: 上午11:18
 * To change this template use File | Settings | File Templates.
 */
public class ArrearsRemindResponse {

  private String licenceNo;
  private String clientName;
  private String contact;
  private Long contactId;
  private String contactIdStr;
  private String mobile;
  private String brand;
  private String model;
  private String lastBill;
  private String lastTime;
  private double lastAmount;
  private double totalArrears;
  private double total;
  private Long repayDate;
  private String repayDateStr;
  private Long customerId;
  private String customerIdStr;
  private Long debtId;
  private String debtIdStr;
  private String remindStatus;
  private double totalReturnDebt;
  private Long supplierId;
  private String supplierIdStr;

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
    if(contactId!=null) this.contactIdStr = contactId.toString();
  }

  public String getSupplierIdStr() {
    return supplierIdStr;
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    if(supplierId != null) {
      this.supplierIdStr = supplierId.toString();
    }
    this.supplierId = supplierId;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    if (customerId != null) {
      customerIdStr = customerId.toString();
    }
    this.customerId = customerId;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
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

  public String getLastBill() {
    return lastBill;
  }

  public void setLastBill(String lastBill) {
    this.lastBill = lastBill;
  }

  public String getLastTime() {
    return lastTime;
  }

  public void setLastTime(String lastTime) {
    this.lastTime = lastTime;
  }

  public double getLastAmount() {
    return lastAmount;
  }

  public void setLastAmount(double lastAmount) {
    this.lastAmount = lastAmount;
  }

  public double getTotalArrears() {
    return totalArrears;
  }

  public void setTotalArrears(double totalArrears) {
    this.totalArrears = totalArrears;
  }

  public Long getRepayDate() {
    return repayDate;
  }

  public void setRepayDate(Long repayDate) {
    this.repayDate = repayDate;
  }

  public String getRepayDateStr() {
    return repayDateStr;
  }

  public void setRepayDateStr(String repayDateStr) {
    this.repayDateStr = repayDateStr;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public Long getDebtId() {
    return debtId;
  }

  public void setDebtId(Long debtId) {
    this.debtId = debtId;
    if(debtId!=null){
      this.debtIdStr = debtId + "";
    }
  }

  public String getDebtIdStr() {
    return debtIdStr;
  }

  public void setDebtIdStr(String debtIdStr) {
    this.debtIdStr = debtIdStr;
  }

  public String getRemindStatus() {
    return remindStatus;
  }

  public void setRemindStatus(String remindStatus) {
    this.remindStatus = remindStatus;
  }

  public double getTotalReturnDebt() {
    return totalReturnDebt;
  }

  public void setTotalReturnDebt(double totalReturnDebt) {
    this.totalReturnDebt = totalReturnDebt;
  }
}
