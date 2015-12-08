package com.bcgogo.txn.model;

/**
 * order和item作为一条数据存储到临时表
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-11-6
 * Time: 下午1:23
 * To change this template use File | Settings | File Templates.
 */

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;
import java.text.ParseException;


@Entity
@Table(name = "imported_order_temp")
public class ImportedOrderTemp extends LongIdentifier {
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
  private String salesMan;
  private Double serviceTotal;
  private String serviceWorker;
  private String serviceContent;
  private String inTime;
  private String outTime;
  private String payWay;
  private Double total;  //销售价
  private Double actuallyPaid; //最近入库价/最近采购价
  private Double debt;   //  入库价,采购价
  private OrderTypes orderType;
  private OrderStatus orderStatus;
  private String orderTypeStr;
  private String itemType; //service or not
  private String memo;

  public static final String timeStr=" 12:00:00";  //由于导入数据没有时间，这里是默认时间
  public static final String SERVICE="service";


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



  @Column(name = "receipt")
  public String getReceipt() {
    return receipt;
  }

  public void setReceipt(String receipt) {
    this.receipt = receipt;
  }

  @Column(name = "vehicle")
  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "spec")
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "debt")
  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }



  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "vest_date")
  public String getVestDate() {
    return vestDate;
  }

  public void setVestDate(String vestDate) {
    this.vestDate = vestDate;
  }



  @Column(name = "order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  @Column(name = "order_status_str")
  public String getOrderStatusStr() {
    return orderStatusStr;
  }

  public void setOrderStatusStr(String orderStatusStr) {
    this.orderStatusStr = orderStatusStr;
  }

  @Column(name = "order_type_str")
  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }

  @Column(name = "product_code")
  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
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

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "customer_supplier_name")
  public String getCustomerSupplierName() {
    return customerSupplierName;
  }

  public void setCustomerSupplierName(String customerSupplierName) {
    this.customerSupplierName = customerSupplierName;
  }

  @Column(name = "contact")
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "member_type")
  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  @Column(name = "member_card_no")
  public String getMemberCardNo() {
    return memberCardNo;
  }

  public void setMemberCardNo(String memberCardNo) {
    this.memberCardNo = memberCardNo;
  }

  @Column(name = "pay_per_project")
  public String getPayPerProject() {
    return payPerProject;
  }

  public void setPayPerProject(String payPerProject) {
    this.payPerProject = payPerProject;
  }

  @Column(name = "sales_man")
  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  @Column(name = "service_total")
  public Double getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceTotal(Double serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  @Column(name = "service_worker")
  public String getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String serviceWorker) {
    this.serviceWorker = serviceWorker;
  }

  @Column(name = "service_content")
  public String getServiceContent() {
    return serviceContent;
  }

  public void setServiceContent(String serviceContent) {
    this.serviceContent = serviceContent;
  }

  @Column(name = "in_time")
  public String getInTime() {
    return inTime;
  }

  public void setInTime(String inTime) {
    this.inTime = inTime;
  }

  @Column(name = "out_time")
  public String getOutTime() {
    return outTime;
  }

  public void setOutTime(String outTime) {
    this.outTime = outTime;
  }

  @Column(name = "pay_way")
  public String getPayWay() {
    return payWay;
  }

  public void setPayWay(String payWay) {
    this.payWay = payWay;
  }

  @Column(name = "actually_paid")
  public Double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(Double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  @Column(name = "item_type")
  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public ImportedOrderItem toImportedOrderItem(){
    ImportedOrderItem orderItem=new ImportedOrderItem();
    orderItem.setProductName(this.getProductName());
    orderItem.setProductCode(this.getProductCode());
    orderItem.setBrand(this.getBrand());
    orderItem.setSpec(this.getSpec());
    orderItem.setModel(this.getModel());
    orderItem.setVehicleBrand(this.getVehicleBrand());
    orderItem.setVehicleModel(this.getVehicleModel());
    orderItem.setPrice(this.getPrice());
    orderItem.setAmount(this.getAmount());
    orderItem.setUnit(this.getUnit());
    orderItem.setItemType(this.getItemType());
    orderItem.setServiceTotal(this.getServiceTotal());
    orderItem.setServiceWorker(this.getServiceWorker());
    orderItem.setServiceContent(this.getServiceContent());
    return orderItem;
  }

  public ImportedOrder toImportedOrder() throws ParseException {
    ImportedOrder importedOrder=new ImportedOrder();
    importedOrder.setShopId(this.getShopId());
    importedOrder.setUserId(this.getUserId());
    importedOrder.setReceipt(this.getReceipt());
    importedOrder.setOrderStatusStr(this.getOrderStatusStr());
    importedOrder.setVehicle(this.getVehicle());
    importedOrder.setCustomerSupplierName(this.getCustomerSupplierName());
    importedOrder.setContact(this.getContact());
    importedOrder.setMobile(this.getMobile());
    importedOrder.setMemberType(this.getMemberType());
    importedOrder.setMemberCardNo(this.getMemberCardNo());
    importedOrder.setPayPerProject(this.getPayPerProject());
    importedOrder.setSalesMan(this.getSalesMan());
    if(DateUtil.isStandardDateFormat(this.getInTime()))
    importedOrder.setInTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL,this.getInTime()+timeStr));
    if(DateUtil.isStandardDateFormat(this.getOutTime()))
    importedOrder.setOutTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL,this.getOutTime()+timeStr));
    importedOrder.setPayWay(this.getPayWay());
    importedOrder.setDebt(this.getDebt());
    importedOrder.setActuallyPaid(this.getActuallyPaid());
    importedOrder.setTotal(this.getTotal());
    importedOrder.setOrderStatusStr(this.getOrderStatusStr());
    importedOrder.setOrderTypeStr(this.getOrderTypeStr());
    if(DateUtil.isStandardDateFormat(this.getVestDate()))
    importedOrder.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, this.getVestDate()+timeStr));
    importedOrder.setMemo(this.getMemo());
    return importedOrder;
  }

}
