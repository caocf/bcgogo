package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.StatementAccountOrderDTO;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;

/**
 * 客户或者供应商对账单实体类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午1:28
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "statement_account_order")
public class StatementAccountOrder extends LongIdentifier {

  private Long shopId;     //店铺id
  private Long startDate; //对账日期 开始时间
  private Long endDate;   //对账日期 结束时间
  private String receiptNo; //对账单据号
  private Long vestDate; //结算时间 默认为系统时间
  private Long salesManId; //结算人id
  private String salesMan; //结算人名字
  private Long customerOrSupplierId;//客户或者供应商id
  private String customerOrSupplier; //客户或者供应商名字
  private double total;   //应收 应付总和
  private OrderTypes orderType;//单据类型 默认为客户对账单 或者供应商对账单
  private OrderStatus orderStatus;//单据状态

  private Long  statementAccountOrderId;//对账单id

  //客户或者供应商信息
  private String contact;
  private String mobile;
  private String address;

  private double totalReceivable;
  private double totalPayable;

  public StatementAccountOrder() {

  }

  public StatementAccountOrder(StatementAccountOrderDTO statementAccountOrderDTO) {
    setId(statementAccountOrderDTO.getId());
    setShopId(statementAccountOrderDTO.getShopId());
    setStartDate(statementAccountOrderDTO.getStartDate());
    setEndDate(statementAccountOrderDTO.getEndDate());
    setReceiptNo(statementAccountOrderDTO.getReceiptNo());
    setVestDate(statementAccountOrderDTO.getVestDate());
    setSalesManId(statementAccountOrderDTO.getSalesManId());
    setSalesMan(statementAccountOrderDTO.getSalesMan());
    setCustomerOrSupplierId(statementAccountOrderDTO.getCustomerOrSupplierId());
    setCustomerOrSupplier(statementAccountOrderDTO.getCustomerOrSupplier());
    setTotal(statementAccountOrderDTO.getTotal());
    setOrderType(statementAccountOrderDTO.getOrderType());
    setOrderStatus(statementAccountOrderDTO.getOrderStatus());
    setStatementAccountOrderId(statementAccountOrderDTO.getStatementAccountOrderId());
    setContact(statementAccountOrderDTO.getContact());
    setMobile(statementAccountOrderDTO.getMobile());
    setAddress(statementAccountOrderDTO.getAddress());
    setTotalPayable(statementAccountOrderDTO.getTotalPayable());
    setTotalReceivable(statementAccountOrderDTO.getTotalReceivable());
  }


  public StatementAccountOrder fromDTO(StatementAccountOrderDTO statementAccountOrderDTO, boolean setId) {
    if (statementAccountOrderDTO == null) {
      return this;
    }
    if (setId) {
      setId(statementAccountOrderDTO.getId());
    }
    setShopId(statementAccountOrderDTO.getShopId());
    setStartDate(statementAccountOrderDTO.getStartDate());
    setEndDate(statementAccountOrderDTO.getEndDate());
    setReceiptNo(statementAccountOrderDTO.getReceiptNo());
    setVestDate(statementAccountOrderDTO.getVestDate());
    setSalesManId(statementAccountOrderDTO.getSalesManId());
    setSalesMan(statementAccountOrderDTO.getSalesMan());
    setCustomerOrSupplierId(statementAccountOrderDTO.getCustomerOrSupplierId());
    setCustomerOrSupplier(statementAccountOrderDTO.getCustomerOrSupplier());
    setTotal(statementAccountOrderDTO.getTotal());
    setOrderType(statementAccountOrderDTO.getOrderType());
    setOrderStatus(statementAccountOrderDTO.getOrderStatus());
    setStatementAccountOrderId(getStatementAccountOrderId());
    setContact(statementAccountOrderDTO.getContact());
    setMobile(statementAccountOrderDTO.getMobile());
    setAddress(statementAccountOrderDTO.getAddress());
    setTotalPayable(statementAccountOrderDTO.getTotalPayable());
    setTotalReceivable(statementAccountOrderDTO.getTotalReceivable());

    return this;
  }

  public StatementAccountOrderDTO toDTO() {
    StatementAccountOrderDTO statementAccountOrderDTO = new StatementAccountOrderDTO();
    statementAccountOrderDTO.setId(getId());
    statementAccountOrderDTO.setShopId(getShopId());
    statementAccountOrderDTO.setStartDate(getStartDate());
    statementAccountOrderDTO.setEndDate(getEndDate());
    statementAccountOrderDTO.setReceiptNo(getReceiptNo());
    statementAccountOrderDTO.setVestDate(getVestDate());
    statementAccountOrderDTO.setSalesManId(getSalesManId());
    statementAccountOrderDTO.setSalesMan(getSalesMan());
    statementAccountOrderDTO.setCustomerOrSupplierId(getCustomerOrSupplierId());
    statementAccountOrderDTO.setCustomerOrSupplier(getCustomerOrSupplier());
    statementAccountOrderDTO.setTotal(getTotal());
    statementAccountOrderDTO.setOrderType(getOrderType());
    statementAccountOrderDTO.setVestDateStr(DateUtil.dateLongToStr(getVestDate(),DateUtil.DATE_STRING_FORMAT_DAY));
    statementAccountOrderDTO.setOrderTypeStr(getOrderType().getName());
    statementAccountOrderDTO.setOrderStatus(getOrderStatus());
    statementAccountOrderDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    statementAccountOrderDTO.setContact(getContact());
    statementAccountOrderDTO.setMobile(getMobile());
    statementAccountOrderDTO.setAddress(getAddress());
    statementAccountOrderDTO.setTotalPayable(getTotalPayable());
    statementAccountOrderDTO.setTotalReceivable(getTotalReceivable());
    statementAccountOrderDTO.setCreationDate(getCreationDate());
    return statementAccountOrderDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  @Column(name = "sales_man_id")
  public Long getSalesManId() {
    return salesManId;
  }

  public void setSalesManId(Long salesManId) {
    this.salesManId = salesManId;
  }

  @Column(name = "sales_man")
  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  @Column(name = "customer_or_supplier_id")
  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
  }

  @Column(name = "customer_or_supplier")
  public String getCustomerOrSupplier() {
    return customerOrSupplier;
  }

  public void setCustomerOrSupplier(String customerOrSupplier) {
    this.customerOrSupplier = customerOrSupplier;
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  @Column(name = "order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  @Column(name = "statement_account_order_id")
  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
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

  @Column(name = "address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Column(name = "total_receivable")
  public double getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(double totalReceivable) {
    this.totalReceivable = totalReceivable;
  }

  @Column(name = "total_payable")
  public double getTotalPayable() {
    return totalPayable;
  }

  public void setTotalPayable(double totalPayable) {
    this.totalPayable = totalPayable;
  }
}
