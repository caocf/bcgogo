package com.bcgogo.txn.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-30
 * Time: 上午5:47
 * To change this template use File | Settings | File Templates.
 */
public class ImportedOrderDTO {
  private Long shopId;
  private Long userId;
  private String receipt;
  private String productCode;
  private String productName;
  private String brand;
  private String spec;
  private String model;
  private String vehicleBrand;
  private String vehicleModel;
  private String unit;
  private Double price;
  private Double amount;
  private String orderStatusStr;
  private String vestDate;   //消费日期

  private String vehicle;
  private String customerSupplierName;
  private String contact;
  private String mobile;
  private String memberType;
  private String memberCardNo;
  private String payPerProject;
  private String[] salesMan;
  private Double serviceTotal;
  private String inTime;
  private String outTime;
  private String payWay;
  private Double total;  //销售价
  private Double actuallyPaid; //最近入库价/最近采购价
  private Double debt;   //  入库价,采购价
  private OrderTypes orderType;
  private OrderStatus orderStatus;
  private String orderTypeStr;
  private String memo;

    //for search
  private Double amountLower;
  private Double amountUpper;
  private String[] payMethod;//支付方式
  private String orderTypes[];
  private String [] serviceWorkers;
  private String [] operator;
  private Long startDate;
  private Long endDate;
  private int startPageNo;
  private int pageSize;
  private Pager pager;
  public static final int INQUIRY_CENTER_PAGE_SIZE=15;

  public Double getAmountLower() {
    return amountLower;
  }

  public void setAmountLower(Double amountLower) {
    this.amountLower = amountLower;
  }

  public Double getAmountUpper() {
    return amountUpper;
  }

  public void setAmountUpper(Double amountUpper) {
    this.amountUpper = amountUpper;
  }

  public String[] getPayMethod() {
    return payMethod;
  }

  public void setPayMethod(String[] payMethod) {
    this.payMethod = payMethod;
  }

  public String[] getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String[] salesMan) {
    this.salesMan = salesMan;
  }

  public String[] getServiceWorkers() {
    return serviceWorkers;
  }

  public void setServiceWorkers(String[] serviceWorkers) {
    this.serviceWorkers = serviceWorkers;
  }

  public String[] getOperator() {
    return operator;
  }

  public void setOperator(String[] operator) {
    this.operator = operator;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public String[] getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(String[] orderTypes) {
    this.orderTypes = orderTypes;
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

  public String getReceipt() {
    return receipt;
  }

  public void setReceipt(String receipt) {
    this.receipt = receipt;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
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

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getOrderStatusStr() {
    return orderStatusStr;
  }

  public void setOrderStatusStr(String orderStatusStr) {
    this.orderStatusStr = orderStatusStr;
  }

  public String getVestDate() {
    return vestDate;
  }

  public void setVestDate(String vestDate) {
    this.vestDate = vestDate;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getCustomerSupplierName() {
    return customerSupplierName;
  }

  public void setCustomerSupplierName(String customerSupplierName) {
    this.customerSupplierName = customerSupplierName;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public String getMemberCardNo() {
    return memberCardNo;
  }

  public void setMemberCardNo(String memberCardNo) {
    this.memberCardNo = memberCardNo;
  }

  public String getPayPerProject() {
    return payPerProject;
  }

  public void setPayPerProject(String payPerProject) {
    this.payPerProject = payPerProject;
  }



  public Double getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceTotal(Double serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public String getInTime() {
    return inTime;
  }

  public void setInTime(String inTime) {
    this.inTime = inTime;
  }

  public String getOutTime() {
    return outTime;
  }

  public void setOutTime(String outTime) {
    this.outTime = outTime;
  }

  public String getPayWay() {
    return payWay;
  }

  public void setPayWay(String payWay) {
    this.payWay = payWay;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(Double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }
}
