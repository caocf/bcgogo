package com.bcgogo.txn.model;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-6
 * To change this template use File | Settings | File Templates.
 */

import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.user.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "purchase_inventory")
public class PurchaseInventory extends LongIdentifier {
  public PurchaseInventory() {
  }

  /**
   * 将DTO转为Entity
   * @param purchaseInventoryDTO
   * @param setId, 是否将DTO中的ID覆盖到Entity中
   * @return
   */
  public PurchaseInventory fromDTO(PurchaseInventoryDTO purchaseInventoryDTO, boolean setId) {
    if(purchaseInventoryDTO==null)
      return this;
    if(setId){
      setId(purchaseInventoryDTO.getId());
    }
    this.shopId = purchaseInventoryDTO.getShopId();
    this.date = purchaseInventoryDTO.getDate();
    this.no = purchaseInventoryDTO.getNo();
    this.refNo = purchaseInventoryDTO.getRefNo();
    this.purchaseOrderId = purchaseInventoryDTO.getPurchaseOrderId();
    this.purchaseOrderNo = purchaseInventoryDTO.getPurchaseOrderNo();
    this.deptId = purchaseInventoryDTO.getDeptId();
    this.dept = purchaseInventoryDTO.getDept();
    this.supplierId = purchaseInventoryDTO.getSupplierId();
    this.supplier = purchaseInventoryDTO.getSupplier();
    this.executorId = purchaseInventoryDTO.getExecutorId();
    this.executor = purchaseInventoryDTO.getExecutor();
    this.total = purchaseInventoryDTO.getTotal();
    this.deliveryDate = purchaseInventoryDTO.getDeliveryDate();
    this.editorId = purchaseInventoryDTO.getEditorId();
    this.editor = purchaseInventoryDTO.getEditor();
    this.editDate = purchaseInventoryDTO.getEditDate();
    this.reviewerId = purchaseInventoryDTO.getReviewerId();
    this.reviewer = purchaseInventoryDTO.getReviewer();
    this.reviewDate = purchaseInventoryDTO.getReviewDate();
    this.invalidatorId = purchaseInventoryDTO.getInvalidatorId();
    this.invalidator = purchaseInventoryDTO.getInvalidator();
    this.invalidateDate = purchaseInventoryDTO.getInvalidateDate();
    this.acceptor = purchaseInventoryDTO.getAcceptor();
    this.acceptorId = purchaseInventoryDTO.getAcceptorId();
    this.memo = purchaseInventoryDTO.getMemo();
    this.statusEnum = purchaseInventoryDTO.getStatus();
    this.vestDate = purchaseInventoryDTO.getVestDate();
    this.receiptNo = purchaseInventoryDTO.getReceiptNo();
    this.supplierContact = purchaseInventoryDTO.getContact();
    if (StringUtils.isNotBlank(purchaseInventoryDTO.getContactIdStr())){
      this.supplierContactId = Long.parseLong(purchaseInventoryDTO.getContactIdStr());
    }else{
      this.supplierContactId = purchaseInventoryDTO.getContactId();
    }
    this.supplierMobile = purchaseInventoryDTO.getMobile();
    this.supplierLandline = purchaseInventoryDTO.getLandline();
    this.supplierAddress = purchaseInventoryDTO.getAddress();
    this.setStorehouseId(purchaseInventoryDTO.getStorehouseId());
    this.setStorehouseName(purchaseInventoryDTO.getStorehouseName());
    this.setStatementAccountOrderId(purchaseInventoryDTO.getStatementAccountOrderId());
    return this;
  }

  public PurchaseInventoryDTO toDTO() {
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.setId(getId());
    purchaseInventoryDTO.setShopId(getShopId());
    purchaseInventoryDTO.setDate(getDate());
    purchaseInventoryDTO.setCreationDate(this.getCreationDate());
    purchaseInventoryDTO.setNo(getNo());
    purchaseInventoryDTO.setRefNo(getRefNo());
    purchaseInventoryDTO.setPurchaseOrderId(getPurchaseOrderId());
    purchaseInventoryDTO.setPurchaseOrderNo(getPurchaseOrderNo());
    purchaseInventoryDTO.setDeptId(getDeptId());
    purchaseInventoryDTO.setDept(getDept());
    purchaseInventoryDTO.setSupplierId(getSupplierId());
    purchaseInventoryDTO.setSupplier(getSupplier());
    purchaseInventoryDTO.setExecutorId(getExecutorId());
    purchaseInventoryDTO.setExecutor(getExecutor());
    purchaseInventoryDTO.setTotal(getTotal());
    purchaseInventoryDTO.setDeliveryDate(getDeliveryDate());
    purchaseInventoryDTO.setEditorId(getEditorId());
    purchaseInventoryDTO.setEditor(getEditor());
    purchaseInventoryDTO.setEditDate(getEditDate());
    purchaseInventoryDTO.setReviewerId(getReviewerId());
    purchaseInventoryDTO.setReviewer(getReviewer());
    purchaseInventoryDTO.setReviewDate(getReviewDate());
    purchaseInventoryDTO.setInvalidatorId(getInvalidatorId());
    purchaseInventoryDTO.setInvalidator(getInvalidator());
    purchaseInventoryDTO.setInvalidateDate(getInvalidateDate());
    purchaseInventoryDTO.setAcceptor(getAcceptor());
    purchaseInventoryDTO.setMemo(getMemo());
    purchaseInventoryDTO.setStatus(getStatusEnum());
    purchaseInventoryDTO.setVestDate(getVestDate());
	  purchaseInventoryDTO.setCreationDate(getCreationDate());
    purchaseInventoryDTO.setReceiptNo(getReceiptNo());
    purchaseInventoryDTO.setContact(getSupplierContact());
    purchaseInventoryDTO.setContactId(getSupplierContactId());
    purchaseInventoryDTO.setContactIdStr(getSupplierContactId() != null ? String.valueOf(getSupplierContactId()) : null);
    purchaseInventoryDTO.setMobile(getSupplierMobile());
    purchaseInventoryDTO.setLandline(getSupplierLandline());
    purchaseInventoryDTO.setAddress(getSupplierAddress());
    purchaseInventoryDTO.setStorehouseId(this.getStorehouseId());
    purchaseInventoryDTO.setStorehouseName(this.getStorehouseName());
    purchaseInventoryDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    return purchaseInventoryDTO;
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

  @Column(name = "dept_id")
  public Long getDeptId() {
    return deptId;
  }

  public void setDeptId(Long deptId) {
    this.deptId = deptId;
  }

  @Column(name = "dept")
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

  @Column(name = "executor")
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

  @Column(name = "delivery_date")
  public Long getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Long deliveryDate) {
    this.deliveryDate = deliveryDate;
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

  @Column(name = "reviewer")
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

  @Column(name = "invalidator")
  public String getInvalidator() {
    return invalidator;
  }

  private void setInvalidator(String invalidator) {
    this.invalidator = invalidator;
  }

  @Column(name = "invalidate_date")
  public Long getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(Long invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  @Column(name = "acceptor")
  public String getAcceptor() {
    return acceptor;
  }

  public void setAcceptor(String acceptor) {
    this.acceptor = acceptor;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "status")
   public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  @Column(name="status_enum")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatusEnum() {
    return statusEnum;
  }

  public void setStatusEnum(OrderStatus statusEnum) {
    this.statusEnum = statusEnum;
  }

  @Column(name="vest_date")
  public Long getVestDate()
  {
      return this.vestDate;
  }

  public void setVestDate(Long vestDate)
  {
      this.vestDate = vestDate;
  }

  @Column(name="acceptor_id")
  public Long getAcceptorId() {
    return acceptorId;
  }

  public void setAcceptorId(Long acceptorId) {
    this.acceptorId = acceptorId;
  }

  @Column(name="receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
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

   @Column(name="promotions_info_json")
  public String getPromotionsInfoJson() {
    return promotionsInfoJson;
  }

  public void setPromotionsInfoJson(String promotionsInfoJson) {
    this.promotionsInfoJson = promotionsInfoJson;
  }

  @Column(name = "supplier_contact_id")
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
  private Long deptId;
  private String dept;                //瞬态字段
  private Long supplierId;
  private String supplier;
  private String supplierContact;
  private Long supplierContactId; // add by zhuj
  private String supplierMobile;
  private String supplierLandline;
  private String supplierAddress;
  private Long executorId;
  private String executor;           //瞬态字段
  private double total;
  private Long deliveryDate;
  private Long editorId;
  private String editor;             //瞬态字段
  private Long editDate;
  private Long reviewerId;
  private String reviewer;           //瞬态字段
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;        //瞬态字段
  private Long invalidateDate;
  private String acceptor;
  private String memo;
  private Long status;
  private OrderStatus statusEnum;
  private Long vestDate;
  private Long acceptorId;
  private String receiptNo;

  private Long storehouseId;
  private String storehouseName;

  private Long statementAccountOrderId;//对账单id
    private String promotionsInfoJson;;

}

