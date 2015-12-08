package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-13
 * Time: 上午9:33
 * To change this template use File | Settings | File Templates.
 */
public class CustomerOrSupplierDTO {

  private Long shopId;
  private Long customerOrSupplierId;
  private String customerOrSupplierIdStr;
  private String name;
  private String contact;
  private Long contactId;
  private String mobile;
  private String address;
  private String bank;
  private String account;
  private String businessScope;
  private String accountName;
  private Long category;
  private String abbr;
  private Long settlementType;
  private String landline;
  private String fax;
  private String qq;
  private Long invoiceCategory;
  private String email;
  private String csType;
  private Long agent;
  private Long areaId;
  private String area;
  private String birthdayString;
  private String company;

  public void fromSupplierDTO(SupplierDTO supplierDTO) {
    if (supplierDTO == null) return;
    this.setCustomerOrSupplierId(supplierDTO.getId());
    this.contact = supplierDTO.getContact();
    this.name = supplierDTO.getName();
    this.mobile = supplierDTO.getMobile();
  }

  public void fromCustomerDTO(CustomerDTO customerDTO) {
    if (customerDTO == null) return;
    this.setCustomerOrSupplierId(customerDTO.getId());
    this.contact = customerDTO.getContact();
    this.name = customerDTO.getName();
    this.mobile = customerDTO.getMobile();
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
    this.customerOrSupplierIdStr = String.valueOf(customerOrSupplierId);
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getCustomerOrSupplierIdStr() {
    return customerOrSupplierIdStr;
  }

  public void setCustomerOrSupplierIdStr(String customerOrSupplierIdStr) {
    this.customerOrSupplierIdStr = customerOrSupplierIdStr;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public Long getCategory() {
    return category;
  }

  public void setCategory(Long category) {
    this.category = category;
  }

  public String getAbbr() {
    return abbr;
  }

  public void setAbbr(String abbr) {
    this.abbr = abbr;
  }

  public Long getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(Long settlementType) {
    this.settlementType = settlementType;
  }

  public String getLandline() {
    return landline;
  }

  public void setLandline(String landline) {
    this.landline = landline;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public Long getInvoiceCategory() {
    return invoiceCategory;
  }

  public void setInvoiceCategory(Long invoiceCategory) {
    this.invoiceCategory = invoiceCategory;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCsType() {
    return csType;
  }

  public void setCsType(String csType) {
    this.csType = csType;
  }

  public Long getAgent() {
    return agent;
  }

  public void setAgent(Long agent) {
    this.agent = agent;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

  public String getBirthdayString() {
    return birthdayString;
  }

  public void setBirthdayString(String birthdayString) {
    this.birthdayString = birthdayString;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }
}
