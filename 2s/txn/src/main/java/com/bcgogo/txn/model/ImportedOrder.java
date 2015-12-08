package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.txn.dto.ImportedOrderDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-30
 * Time: 上午4:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "imported_order")
public class ImportedOrder extends LongIdentifier {
//  private Long id;
  private Long shopId;
  private Long userId;
  private String receipt;

  private String orderStatusStr;
  private Long vestDate;   //消费日期
//  private String vestDateStr;

  private String vehicle;
  private String customerSupplierName;
  private String contact;
  private String mobile;
  private String memberType;
  private String memberCardNo;
  private String payPerProject;
  private String salesMan;

  private Long inTime;
  private Long outTime;
  private String payWay;
  private Double total;  //销售价
  private Double actuallyPaid; //实收
  private Double debt;   //  入库价,采购价
  private OrderTypes orderType;
  private OrderStatus orderStatus;
  private String orderTypeStr;
  private String memo;
  private List<ImportedOrderItem> orderItems= new ArrayList<ImportedOrderItem>();

//    @Column(name = "id")
//  public Long getId() {
//    return id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }

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

  @Transient
  public List<ImportedOrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<ImportedOrderItem> orderItems) {
    this.orderItems = orderItems;
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
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
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



  @Column(name = "in_time")
  public Long getInTime() {
    return inTime;
  }

  public void setInTime(Long inTime) {
    this.inTime = inTime;
  }

  @Column(name = "out_time")
  public Long getOutTime() {
    return outTime;
  }

  public void setOutTime(Long outTime) {
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

  public ImportedOrderDTO fromOrderSearchConditionDTO(OrderSearchConditionDTO searchConditionDTO){
    ImportedOrderDTO importedOrderDTO = new ImportedOrderDTO();
    importedOrderDTO.setShopId(searchConditionDTO.getShopId());
    importedOrderDTO.setAmountLower(searchConditionDTO.getAmountLower());
    importedOrderDTO.setAmountUpper(searchConditionDTO.getAmountUpper());
    importedOrderDTO.setVehicle(searchConditionDTO.getVehicle());
    importedOrderDTO.setCustomerSupplierName(searchConditionDTO.getCustomerOrSupplierName());
    importedOrderDTO.setMemberType(searchConditionDTO.getMemberType());
    importedOrderDTO.setMemberCardNo(searchConditionDTO.getAccountMemberNo());
    importedOrderDTO.setPayPerProject(searchConditionDTO.getPayPerProject());
    importedOrderDTO.setMobile(searchConditionDTO.getMobile());
    importedOrderDTO.setContact(searchConditionDTO.getContact());
    importedOrderDTO.setBrand(searchConditionDTO.getProductBrand());
    importedOrderDTO.setSpec(searchConditionDTO.getProductSpec());
    importedOrderDTO.setModel(searchConditionDTO.getProductModel());
    importedOrderDTO.setProductName(searchConditionDTO.getProductName());
    importedOrderDTO.setProductCode(searchConditionDTO.getCommodityCode());
    importedOrderDTO.setVehicleBrand(searchConditionDTO.getProductVehicleBrand());
    importedOrderDTO.setVehicleModel(searchConditionDTO.getProductVehicleModel());
    importedOrderDTO.setPayMethod(searchConditionDTO.getPayMethod());
    importedOrderDTO.setStartDate(searchConditionDTO.getStartTime());
    importedOrderDTO.setEndDate(searchConditionDTO.getEndTime());
    importedOrderDTO.setOrderTypes(searchConditionDTO.getOrderType());
    importedOrderDTO.setServiceWorkers(searchConditionDTO.getServiceWorker());
    importedOrderDTO.setSalesMan(searchConditionDTO.getSalesman());
    importedOrderDTO.setReceipt(searchConditionDTO.getReceiptNo());
    return importedOrderDTO;
  }

  public List<OrderSearchResultDTO>  toOrderSearchConditionDTO(List<ImportedOrder> importedOrders){
    if(CollectionUtils.isEmpty(importedOrders)){
      return null;
    }
    List<OrderSearchResultDTO> searchResultDTOs = new ArrayList<OrderSearchResultDTO>();
    OrderSearchResultDTO searchResultDTO=null;
    for (ImportedOrder importedOrder:importedOrders){
      if(importedOrder==null){
        continue;
      }
      searchResultDTO=new OrderSearchResultDTO();
      searchResultDTO.setOrderId(importedOrder.getId());
      searchResultDTO.setOrderIdStr(String.valueOf(importedOrder.getId()));
      searchResultDTO.setOrderType(importedOrder.getOrderTypeStr());
      searchResultDTO.setContact(importedOrder.getContact());
      searchResultDTO.setCustomerOrSupplierName(importedOrder.getCustomerSupplierName());

      searchResultDTO.setMemberNo(importedOrder.getMemberCardNo());
      searchResultDTO.setMemo(importedOrder.getMemo());
      searchResultDTO.setMemberType(importedOrder.getMemberType());
      searchResultDTO.setVehicle(importedOrder.getVehicle());
      searchResultDTO.setShopId(importedOrder.getShopId());
      searchResultDTO.setReceiptNo(importedOrder.getReceipt());
      searchResultDTO.setSalesMans(importedOrder.getSalesMan());
      searchResultDTO.setOrderStatusValue(importedOrder.getOrderStatusStr());
      searchResultDTO.setDebt(importedOrder.getDebt());
      searchResultDTO.setSettled(importedOrder.getActuallyPaid());
      searchResultDTO.setAmount(importedOrder.getTotal());
      searchResultDTO.setInTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY2,importedOrder.getInTime()));
      searchResultDTO.setOutTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY2,importedOrder.getOutTime()));
      searchResultDTO.setCreatedTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY2,importedOrder.getVestDate()));
      searchResultDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY2,importedOrder.getVestDate()));
      searchResultDTOs.add(searchResultDTO);
    }
    return searchResultDTOs;
  }

  public ImportedOrderDTO toDTO() {
    ImportedOrderDTO importedOrderDTO = new ImportedOrderDTO();

    return importedOrderDTO;
  }

}
