package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PurchaseReturnDTO;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-9
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "purchase_return")
public class PurchaseReturn extends LongIdentifier {
  public PurchaseReturn() {
  }

  public PurchaseReturn fromDTO(PurchaseReturnDTO purchaseReturnDTO) {
    if (purchaseReturnDTO == null)
      return this;
    setId(purchaseReturnDTO.getId());
    this.shopId = purchaseReturnDTO.getShopId();
    this.date = purchaseReturnDTO.getDate();
    this.no = purchaseReturnDTO.getNo();
    this.refNo = purchaseReturnDTO.getRefNo();
    this.purchaseOrderId = purchaseReturnDTO.getPurchaseOrderId();
    this.purchaseOrderNo = purchaseReturnDTO.getPurchaseOrderNo();
    this.purchaseInventoryId = purchaseReturnDTO.getPurchaseInventoryId();
    this.deptId = purchaseReturnDTO.getDeptId();
    this.dept = purchaseReturnDTO.getDept();
    this.supplierId = purchaseReturnDTO.getSupplierId();
    this.supplier = purchaseReturnDTO.getSupplier();
    this.executorId = purchaseReturnDTO.getExecutorId();
    this.executor = purchaseReturnDTO.getExecutor();
    this.total = purchaseReturnDTO.getTotal();
    this.status = purchaseReturnDTO.getStatus();
    this.memo = purchaseReturnDTO.getMemo();
    this.editorId = purchaseReturnDTO.getEditorId();
    this.editor = purchaseReturnDTO.getEditor();
    this.editDate = purchaseReturnDTO.getEditDate();
    this.reviewerId = purchaseReturnDTO.getReviewerId();
    this.reviewer = purchaseReturnDTO.getReviewer();
    this.reviewDate = purchaseReturnDTO.getReviewDate();
    this.invalidatorId = purchaseReturnDTO.getInvalidatorId();
    this.invalidator = purchaseReturnDTO.getInvalidator();
    this.invalidateDate = purchaseReturnDTO.getInvalidateDate();
    this.vestDate = purchaseReturnDTO.getVestDate();
    this.receiptNo = purchaseReturnDTO.getReceiptNo();
    this.refuseReason = purchaseReturnDTO.getRefuseReason();
    this.supplierContact = purchaseReturnDTO.getContact();
    if (StringUtils.isNotBlank(purchaseReturnDTO.getContactIdStr())){
      this.supplierContactId = Long.parseLong(purchaseReturnDTO.getContactIdStr()) ;
    }else {
      this.supplierContactId = purchaseReturnDTO.getContactId();
    }
    this.supplierMobile = purchaseReturnDTO.getMobile();
    this.supplierLandline = purchaseReturnDTO.getLandline();
    this.supplierAddress = purchaseReturnDTO.getAddress();
    this.setStorehouseId(purchaseReturnDTO.getStorehouseId());
    this.setStorehouseName(purchaseReturnDTO.getStorehouseName());
    this.setStatementAccountOrderId(purchaseReturnDTO.getStatementAccountOrderId());
    this.setSupplierShopId(purchaseReturnDTO.getSupplierShopId());
    this.setOriginOrderId(purchaseReturnDTO.getOriginOrderId());
    return this;
  }

  public PurchaseReturnDTO toDTO() {
    PurchaseReturnDTO purchaseReturnDTO = new PurchaseReturnDTO();
    purchaseReturnDTO.setId(getId());
    purchaseReturnDTO.setShopId(getShopId());
    purchaseReturnDTO.setDate(getDate());
    purchaseReturnDTO.setNo(getNo());
    purchaseReturnDTO.setRefNo(getRefNo());
    purchaseReturnDTO.setPurchaseOrderId(getPurchaseOrderId());
    purchaseReturnDTO.setPurchaseOrderNo(getPurchaseOrderNo());
    purchaseReturnDTO.setPurchaseInventoryId(getPurchaseInventoryId());
    purchaseReturnDTO.setDeptId(getDeptId());
    purchaseReturnDTO.setDept(getDept());
    purchaseReturnDTO.setSupplierId(getSupplierId());
    purchaseReturnDTO.setSupplier(getSupplier());
    purchaseReturnDTO.setExecutorId(getExecutorId());
    purchaseReturnDTO.setExecutor(getExecutor());
    purchaseReturnDTO.setTotal(getTotal());
    purchaseReturnDTO.setStatus(getStatus());
    purchaseReturnDTO.setMemo(getMemo());
    purchaseReturnDTO.setEditorId(getEditorId());
    purchaseReturnDTO.setEditor(getEditor());
    purchaseReturnDTO.setEditDate(getEditDate());
    purchaseReturnDTO.setReviewerId(getReviewerId());
    purchaseReturnDTO.setReviewer(getReviewer());
    purchaseReturnDTO.setReviewDate(getReviewDate());
    purchaseReturnDTO.setInvalidatorId(getInvalidatorId());
    purchaseReturnDTO.setInvalidator(getInvalidator());
    purchaseReturnDTO.setInvalidateDate(getInvalidateDate());
    purchaseReturnDTO.setCreationDate(getCreationDate());
    purchaseReturnDTO.setVestDate(getVestDate());
    purchaseReturnDTO.setReceiptNo(getReceiptNo());
    purchaseReturnDTO.setRefuseReason(getRefuseReason());
    purchaseReturnDTO.setContact(getSupplierContact());
    purchaseReturnDTO.setContactId(getSupplierContactId());//add by zhuj
    purchaseReturnDTO.setContactIdStr(getSupplierContactId() != null ? String.valueOf(getSupplierContactId()) : null);
    purchaseReturnDTO.setMobile(getSupplierMobile());
    purchaseReturnDTO.setLandline(getSupplierLandline());
    purchaseReturnDTO.setAddress(getSupplierAddress());
    purchaseReturnDTO.setStorehouseId(this.getStorehouseId());
    purchaseReturnDTO.setStorehouseName(this.getStorehouseName());
    purchaseReturnDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    purchaseReturnDTO.setSupplierShopId(this.getSupplierShopId());
    purchaseReturnDTO.setOriginOrderId(this.getOriginOrderId());
    return purchaseReturnDTO;
  }

  @Column(name = "vest_date")
  public Long getVestDate() {
    return this.vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
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

  @Column(name = "ref_no", length = 20)
  public String getRefNo() {
    return refNo;
  }

  public void setRefNo(String refNo) {
    this.refNo = refNo;
  }

  @Column(name = "purchase_order_id")
  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
  }

  @Column(name = "purchase_order_no", length = 20)
  public String getPurchaseOrderNo() {
    return purchaseOrderNo;
  }

  public void setPurchaseOrderNo(String purchaseOrderNo) {
    this.purchaseOrderNo = purchaseOrderNo;
  }

  @Column(name = "purchase_inventory_id")
  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  @Column(name = "dept_id")
  public Integer getDeptId() {
    return deptId;
  }

  public void setDeptId(Integer deptId) {
    this.deptId = deptId;
  }

  @Transient
  public String getDept() {
    return dept;
  }

  private void setDept(String dept) {
    this.dept = dept;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "supplier")
  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
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

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
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

  private void setEditor(String editor) {
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

  @Transient
  public String getReviewer() {
    return reviewer;
  }

  private void setReviewer(String reviewer) {
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

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "refuse_reason")
  public String getRefuseReason() {
    return refuseReason;
  }

  public void setRefuseReason(String refuseReason) {
    this.refuseReason = refuseReason;
  }
  @Column(name="storehouse_id")
  public Long getStorehouseId() {
    return storehouseId;
  }

  @Column(name="supplier_contact")
  public String getSupplierContact() {
    return supplierContact;
  }

  public void setSupplierContact(String supplierContact) {
    this.supplierContact = supplierContact;
  }

  @Column(name="supplier_mobile")
  public String getSupplierMobile() {
    return supplierMobile;
  }

  public void setSupplierMobile(String supplierMobile) {
    this.supplierMobile = supplierMobile;
  }

  @Column(name="supplier_landline")
  public String getSupplierLandline() {
    return supplierLandline;
  }

  public void setSupplierLandline(String supplierLandline) {
    this.supplierLandline = supplierLandline;
  }

  @Column(name="supplier_address")
  public String getSupplierAddress() {
    return supplierAddress;
  }

  public void setSupplierAddress(String supplierAddress) {
    this.supplierAddress = supplierAddress;
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

  @Column(name="supplier_shop_id")
  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }

  @Column(name="origin_order_id")
  public Long getOriginOrderId() {
    return originOrderId;
  }

  public void setOriginOrderId(Long originOrderId) {
    this.originOrderId = originOrderId;
  }

  @Column(name="supplier_contact_id")
  public Long getSupplierContactId() {
    return supplierContactId;
  }

  public void setSupplierContactId(Long supplierContactId) {
    this.supplierContactId = supplierContactId;
  }

  private Long shopId;
  private Long date;
  private String no;
  private String refNo;
  private Long purchaseOrderId;
  private String purchaseOrderNo;
  private Long purchaseInventoryId;
  private Integer deptId;
  private String dept;               //瞬态字段
  private Long supplierId;
  private String supplier;
  private String supplierContact;
  private Long supplierContactId; // add by zhuj
  private String supplierMobile;
  private String supplierLandline;
  private String supplierAddress;
  private Long executorId;
  private String executor;          //瞬态字段
  private double total;
  private OrderStatus status;
  private String memo;
  private Long editorId;
  private String editor;            //瞬态字段
  private Long editDate;
  private Long reviewerId;
  private String reviewer;          //瞬态字段
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;       //瞬态字段
  private String invalidateDate;
  private Long vestDate;//归属时间
  private Long originOrderId;

  private String receiptNo;
  private String refuseReason;

  private Long storehouseId;
  private String storehouseName;

  private Long statementAccountOrderId;//对账单id
  private Long supplierShopId;

  /**
   * 根据XXId找到相关信息并设置到XX字段.
   */
  @Override
  public void onLoad(Session s, Serializable id) {
//    if(editorId!=null){
//      setEditor(ServiceManager.getService(IUserService.class).getNameByUserId(editorId));
//    }
  }
}