package com.bcgogo.txn.model;

import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SalesReturnDTO;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-13
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "sales_return")
public class SalesReturn  extends LongIdentifier {
  public SalesReturn(){
  }
  
  public SalesReturn fromDTO(SalesReturnDTO salesReturnDTO) {
    if(salesReturnDTO == null)
      return this;
    this.shopId = salesReturnDTO.getShopId();
    this.date = salesReturnDTO.getDate();
    this.no = salesReturnDTO.getNo();
    this.refNo = salesReturnDTO.getRefNo();
    this.purchaseReturnOrderId = salesReturnDTO.getPurchaseReturnOrderId();
    this.purchaseReturnOrderMemo = salesReturnDTO.getPurchaseReturnOrderMemo();
    this.deptId = salesReturnDTO.getDeptId();
    this.dept = salesReturnDTO.getDept();
    this.customerId = salesReturnDTO.getCustomerId();
    this.customer = salesReturnDTO.getCustomer();
    this.customerCompany = salesReturnDTO.getCompany();
    this.customerContact = salesReturnDTO.getContact();
    if (StringUtils.isNotBlank(salesReturnDTO.getContactIdStr())){
      this.customerContactId = Long.parseLong(salesReturnDTO.getContactIdStr());
    }else{
      this.customerContactId = salesReturnDTO.getContactId();
    }
    this.customerMobile = salesReturnDTO.getMobile();
    this.customerLandline = salesReturnDTO.getLandline();
    this.customerAddress = salesReturnDTO.getAddress();
    this.memberNo = salesReturnDTO.getMemberNo();
    this.executorId = salesReturnDTO.getExecutorId();
    this.executor = salesReturnDTO.getExecutor();
    this.total = salesReturnDTO.getTotal();
    this.status = salesReturnDTO.getStatus();
    this.memo = salesReturnDTO.getMemo();
    this.editorId = salesReturnDTO.getEditorId();
    this.editor = salesReturnDTO.getEditor();
    this.editDate = salesReturnDTO.getEditDate();
    this.reviewerId = salesReturnDTO.getReviewerId();
    this.reviewer = salesReturnDTO.getReviewer();
    this.reviewDate = salesReturnDTO.getReviewDate();
    this.invalidatorId = salesReturnDTO.getInvalidatorId();
    this.invalidator = salesReturnDTO.getInvalidator();
    this.invalidateDate = salesReturnDTO.getInvalidateDate();
    this.vestDate=salesReturnDTO.getVestDate();
    this.receiptNo = salesReturnDTO.getReceiptNo();
    this.refuseReason = salesReturnDTO.getRefuseReason();
    this.totalCostPrice = salesReturnDTO.getTotalCostPrice();
    this.salesReturner = salesReturnDTO.getSalesReturner();
    this.salesReturnerId = salesReturnDTO.getSalesReturnerId();
    this.originOrderId = salesReturnDTO.getOriginOrderId();
    this.originOrderType = salesReturnDTO.getOriginOrderType();
    this.setStorehouseId(salesReturnDTO.getStorehouseId());
    this.setStorehouseName(salesReturnDTO.getStorehouseName());
    this.setStatementAccountOrderId(getStatementAccountOrderId());
    this.setCustomerShopId(salesReturnDTO.getCustomerShopId());
    return this;
  }
  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "date")
  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  @Column(name = "no", length = 20)
  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  @Column(name = "ref_no",length = 20)
  public String getRefNo() {
    return refNo;
  }

  public void setRefNo(String refNo) {
    this.refNo = refNo;
  }

  @Column(name = "sales_order_id")
  public Long getSalesOrderId() {
    return salesOrderId;
  }

  public void setSalesOrderId(Long salesOrderId) {
    this.salesOrderId = salesOrderId;
  }

  @Column(name = "sales_order_no", length = 20)
  public String getSalesOrderNo() {
    return salesOrderNo;
  }

  public void setSalesOrderNo(String salesOrderNo) {
    this.salesOrderNo = salesOrderNo;
  }

  @Column(name = "repair_order_id")
  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  @Column(name = "repair_order_no", length = 20)
  public String getRepairOrderNo() {
    return repairOrderNo;
  }

  public void setRepairOrderNo(String repairOrderNo) {
    this.repairOrderNo = repairOrderNo;
  }

  @Column(name = "dept_id")
  public Long getDeptId() {
    return deptId;
  }

  public void setDeptId(Long deptId) {
    this.deptId = deptId;
  }

  @Transient
  public String getDept() {
    return dept;
  }

  private void setDept(String dept) {
    this.dept = dept;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name="customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name = "executor_id")
  public Long getExecutorId() {
    return executorId;
  }

  public void setExecutorId(Long executorId) {
    this.executorId = executorId;
  }

  @Transient
  public String getExecutor() {
    return executor;
  }

  private void setExecutor(String executor) {
    this.executor = executor;
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "editor_id")
  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  @Column(name = "editor")
  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "reviewer_id")
  public Long getReviewerId() {
    return reviewerId;
  }

  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  @Column(name = "reviewer")
  public String getReviewer() {
    return reviewer;
  }

  public void setReviewer(String reviewer) {
    this.reviewer = reviewer;
  }

  @Column(name = "review_date")
  public Long getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  @Column(name = "invalidator_id")
  public Long getInvalidatorId() {
    return invalidatorId;
  }

  public void setInvalidatorId(Long invalidatorId) {
    this.invalidatorId = invalidatorId;
  }

  @Transient
  public String getInvalidator() {
    return invalidator;
  }

  private void setInvalidator(String invalidator) {
    this.invalidator = invalidator;
  }

  @Column(name = "invalidate_date", length = 20)
  public String getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(String invalidateDate) {
    this.invalidateDate = invalidateDate;
  }
  @Column(name = "purchase_return_order_id")
  public Long getPurchaseReturnOrderId() {
    return purchaseReturnOrderId;
  }

  public void setPurchaseReturnOrderId(Long purchaseReturnOrderId) {
    this.purchaseReturnOrderId = purchaseReturnOrderId;
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
    return this.vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }
  @Column(name = "purchase_return_order_memo")
  public String getPurchaseReturnOrderMemo() {
    return purchaseReturnOrderMemo;
  }

  public void setPurchaseReturnOrderMemo(String purchaseReturnOrderMemo) {
    this.purchaseReturnOrderMemo = purchaseReturnOrderMemo;
  }

  @Column(name = "refuse_reason")
  public String getRefuseReason() {
    return refuseReason;
  }

  public void setRefuseReason(String refuseReason) {
    this.refuseReason = refuseReason;
  }

  @Column(name="total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  @Column(name="sales_returner")
  public String getSalesReturner() {
    return salesReturner;
  }

  public void setSalesReturner(String salesReturner) {
    this.salesReturner = salesReturner;
  }

  @Column(name="sales_returner_id")
  public Long getSalesReturnerId() {
    return salesReturnerId;
  }

  public void setSalesReturnerId(Long salesReturnerId) {
    this.salesReturnerId = salesReturnerId;
  }

  @Column(name="origin_order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOriginOrderType() {
    return originOrderType;
  }

  public void setOriginOrderType(OrderTypes originOrderType) {
    this.originOrderType = originOrderType;
  }

  @Column(name="origin_order_id")
  public Long getOriginOrderId() {
    return originOrderId;
  }

  public void setOriginOrderId(Long originOrderId) {
    this.originOrderId = originOrderId;
  }

  @Column(name="customer_company")
  public String getCustomerCompany() {
    return customerCompany;
  }

  public void setCustomerCompany(String customerCompany) {
    this.customerCompany = customerCompany;
  }

  @Column(name="customer_mobile")
  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  @Column(name="customer_landline")
  public String getCustomerLandline() {
    return customerLandline;
  }

  public void setCustomerLandline(String customerLandline) {
    this.customerLandline = customerLandline;
  }

  @Column(name="customer_address")
  public String getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  @Column(name="member_no")
  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  @Column(name="member_type")
  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  @Column(name="member_status")
  @Enumerated(EnumType.STRING)
  public MemberStatus getMemberStatus() {
    return memberStatus;
  }

  public void setMemberStatus(MemberStatus memberStatus) {
    this.memberStatus = memberStatus;
  }

  @Column(name="customer_contact")
  public String getCustomerContact() {
    return customerContact;
  }

  public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
  }

  @Column (name = "customer_contact_id")
  public Long getCustomerContactId() {
    return customerContactId;
  }

  public void setCustomerContactId(Long customerContactId) {
    this.customerContactId = customerContactId;
  }

  @Column(name="storehouse_id")
  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  @Column(name="storehouse_name")
  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  @Column(name="statement_account_order_id")
  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }

  @Column(name = "customer_shop_id")
  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }


  private Long shopId;
  private Long date;
  private String no;
  private String refNo;
  private Long purchaseReturnOrderId;
  private Long salesOrderId;
  private String salesOrderNo;
  private Long repairOrderId;
  private String repairOrderNo;
  private Long deptId;
  private String dept;                      //瞬态字段
  private Long customerId;
  private String customer;
  private String customerCompany;
  private String customerContact;
  private Long customerContactId;
  private String customerMobile;
  private String customerLandline;
  private String customerAddress;
  private String memberNo;
  private String memberType;
  private MemberStatus memberStatus;
  private Long executorId;
  private String executor;                 //瞬态字段
  private Double total;
  private String memo;
  private Long editorId;
  private String editor;                   //瞬态字段
  private Long editDate;
  private Long reviewerId;
  private String reviewer;                 //瞬态字段
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;             //瞬态字段
  private String invalidateDate;
  private String receiptNo;
  private Long vestDate;
  private OrderStatus status;
  private String purchaseReturnOrderMemo;
  private String refuseReason;
  private Double totalCostPrice;
  private String salesReturner;//退货人
  private Long salesReturnerId;//退货人id
  private OrderTypes originOrderType;//原来的单据类型
  private Long originOrderId;//原来的单据id

  private Long storehouseId;
  private String storehouseName;
  private Long statementAccountOrderId;//对账单id
  private Long customerShopId; // 对应purchaseReturnOrder 的shopId  add by zhuj
  /**
   * 根据XXId找到相关信息并设置到XX字段.
   */
  @Override
  public void onLoad(Session s, Serializable id) {
//    if(editorId!=null){
//      setEditor(ServiceManager.getService(IUserService.class).getNameByUserId(editorId));
//    }
  }


  public SalesReturnDTO toDTO() {
    SalesReturnDTO salesReturnDTO = new SalesReturnDTO();
    salesReturnDTO.setId(getId());
    salesReturnDTO.setShopId(getShopId());
    salesReturnDTO.setDate(getDate());
    salesReturnDTO.setNo(getNo());
    salesReturnDTO.setRefNo(getRefNo());
    salesReturnDTO.setPurchaseReturnOrderId(getPurchaseReturnOrderId());
    salesReturnDTO.setPurchaseReturnOrderMemo(getPurchaseReturnOrderMemo());
    salesReturnDTO.setDeptId(getDeptId());
    salesReturnDTO.setDept(getDept());
    salesReturnDTO.setCustomerId(getCustomerId());
    salesReturnDTO.setCustomer(getCustomer());
    salesReturnDTO.setExecutorId(getExecutorId());
    salesReturnDTO.setExecutor(getExecutor());
    salesReturnDTO.setTotal(getTotal());
    salesReturnDTO.setStatus(getStatus());
    salesReturnDTO.setMemo(getMemo());
    salesReturnDTO.setEditorId(getEditorId());
    salesReturnDTO.setEditor(getEditor());
    salesReturnDTO.setEditDate(getEditDate());
    salesReturnDTO.setReviewerId(getReviewerId());
    salesReturnDTO.setReviewer(getReviewer());
    salesReturnDTO.setReviewDate(getReviewDate());
    salesReturnDTO.setInvalidatorId(getInvalidatorId());
    salesReturnDTO.setInvalidator(getInvalidator());
    salesReturnDTO.setInvalidateDate(getInvalidateDate());
    salesReturnDTO.setCreationDate(getCreationDate());
    salesReturnDTO.setVestDate(getVestDate());
    salesReturnDTO.setReceiptNo(getReceiptNo());
    salesReturnDTO.setRefuseReason(getRefuseReason());
    salesReturnDTO.setTotalCostPrice(getTotalCostPrice() == null ? 0d : getTotalCostPrice());
    salesReturnDTO.setSalesReturner(getSalesReturner());
    salesReturnDTO.setSalesReturnerId(getSalesReturnerId());
    salesReturnDTO.setOriginOrderId(getOriginOrderId());
    salesReturnDTO.setOriginOrderType(getOriginOrderType());
    salesReturnDTO.setCompany(getCustomerCompany());
    salesReturnDTO.setMobile(getCustomerMobile());
    salesReturnDTO.setLandline(getCustomerLandline());
    salesReturnDTO.setAddress(getCustomerAddress());
    salesReturnDTO.setMemberNo(getMemberNo());
    salesReturnDTO.setMemberStatus(getMemberStatus());
    salesReturnDTO.setMemberType(getMemberType());
    salesReturnDTO.setContact(getCustomerContact());
    salesReturnDTO.setContactId(getCustomerContactId());
    if (getCustomerContactId() != null) {
      salesReturnDTO.setContactIdStr(String.valueOf(getCustomerContactId()));
    }
    salesReturnDTO.setStorehouseId(this.getStorehouseId());
    salesReturnDTO.setStorehouseName(this.getStorehouseName());
    salesReturnDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    salesReturnDTO.setCustomerShopId(getCustomerShopId());
    return salesReturnDTO;
  }
}