package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder extends LongIdentifier {
  public PurchaseOrder() {
  }

  public PurchaseOrder fromDTO(PurchaseOrderDTO purchaseOrderDTO) {
    if(purchaseOrderDTO==null)
      return this;
    setId(purchaseOrderDTO.getId());
    this.shopId = purchaseOrderDTO.getShopId();
    this.date = purchaseOrderDTO.getDate();
    this.no = purchaseOrderDTO.getNo();
    this.refNo = purchaseOrderDTO.getRefNo();
    this.deptId = purchaseOrderDTO.getDeptId();
    this.dept = purchaseOrderDTO.getDept();
    this.supplierId = purchaseOrderDTO.getSupplierId();
    this.supplier = purchaseOrderDTO.getSupplier();
    this.executorId = purchaseOrderDTO.getExecutorId();
    this.executor = purchaseOrderDTO.getExecutor();
    this.total = purchaseOrderDTO.getTotal();
    this.deliveryDate = purchaseOrderDTO.getDeliveryDate();
    this.statusEnum = purchaseOrderDTO.getStatus();
    this.editorId = purchaseOrderDTO.getEditorId();
    this.editor = purchaseOrderDTO.getEditor();
    this.editDate = purchaseOrderDTO.getEditDate();
    this.reviewerId = purchaseOrderDTO.getReviewerId();
    this.reviewer = purchaseOrderDTO.getReviewer();
    this.reviewDate = purchaseOrderDTO.getReviewDate();
    this.invalidatorId = purchaseOrderDTO.getInvalidatorId();
    this.invalidator = purchaseOrderDTO.getInvalidator();
    this.invalidateDate = purchaseOrderDTO.getInvalidateDate();
    this.billProducer = purchaseOrderDTO.getBillProducer();
    this.billProducerId = purchaseOrderDTO.getBillProducerId();
    this.memo = purchaseOrderDTO.getMemo();
    this.vestDate = purchaseOrderDTO.getVestDate();
    this.receiptNo = purchaseOrderDTO.getReceiptNo();
    setRefuseMsg(purchaseOrderDTO.getRefuseMsg());
    this.supplierContact = purchaseOrderDTO.getContact();
    if (StringUtils.isNotBlank(purchaseOrderDTO.getContactIdStr())){
      this.supplierContactId = Long.parseLong(purchaseOrderDTO.getContactIdStr());
    }else{
      this.supplierContactId = purchaseOrderDTO.getContactId();
    }
    this.supplierMobile = purchaseOrderDTO.getMobile();
    this.supplierLandline = purchaseOrderDTO.getLandline();
    this.supplierAddress = purchaseOrderDTO.getAddress();
    this.setSupplierShopId(purchaseOrderDTO.getSupplierShopId());
    this.inventoryVestDate = purchaseOrderDTO.getInventoryVestDate();
    this.promotionsInfoJson=purchaseOrderDTO.getPromotionsInfoJson();
    return this;
  }

  public PurchaseOrderDTO toDTO() {
    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
    purchaseOrderDTO.setId(getId());
    purchaseOrderDTO.setShopId(getShopId());
    purchaseOrderDTO.setDate(getDate());
    purchaseOrderDTO.setNo(getNo());
    purchaseOrderDTO.setRefNo(getRefNo());
    purchaseOrderDTO.setDeptId(getDeptId());
    purchaseOrderDTO.setDept(getDept());
    purchaseOrderDTO.setSupplierId(getSupplierId());
    purchaseOrderDTO.setSupplier(getSupplier());
    purchaseOrderDTO.setExecutorId(getExecutorId());
    purchaseOrderDTO.setExecutor(getExecutor());
    purchaseOrderDTO.setTotal(getTotal());
    purchaseOrderDTO.setDeliveryDate(getDeliveryDate());
    if(getDeliveryDate() != null) {
      purchaseOrderDTO.setDeliveryDateStr(DateUtil.dateLongToStr(getDeliveryDate(), DateUtil.DATE_STRING_FORMAT_DAY));
    }
    purchaseOrderDTO.setStatus(getStatusEnum());
    purchaseOrderDTO.setEditorId(getEditorId());
    purchaseOrderDTO.setEditor(getEditor());
    purchaseOrderDTO.setEditDate(getEditDate());
    purchaseOrderDTO.setReviewerId(getReviewerId());
    purchaseOrderDTO.setReviewer(getReviewer());
    purchaseOrderDTO.setReviewDate(getReviewDate());
    purchaseOrderDTO.setInvalidatorId(getInvalidatorId());
    purchaseOrderDTO.setInvalidator(getInvalidator());
    purchaseOrderDTO.setInvalidateDate(getInvalidateDate());
    purchaseOrderDTO.setBillProducer(getBillProducer());
    purchaseOrderDTO.setMemo(getMemo());
    purchaseOrderDTO.setVestDate(getVestDate());
    purchaseOrderDTO.setCreationDate(getCreationDate());
    purchaseOrderDTO.setReceiptNo(getReceiptNo());
    purchaseOrderDTO.setRefuseMsg(getRefuseMsg());
	  purchaseOrderDTO.setExpressId(getExpressId());
	  purchaseOrderDTO.setPreDispatchDate(getPreDispatchDate());
    purchaseOrderDTO.setContact(getSupplierContact());
    purchaseOrderDTO.setContactId(getSupplierContactId()); // add by zhuj
    if (getSupplierContactId() != null)
      purchaseOrderDTO.setContactIdStr(String.valueOf(getSupplierContactId()));
    purchaseOrderDTO.setMobile(getSupplierMobile());
    purchaseOrderDTO.setLandline(getSupplierLandline());
    purchaseOrderDTO.setAddress(getSupplierAddress());
    purchaseOrderDTO.setSupplierShopId(getSupplierShopId());
    purchaseOrderDTO.setInventoryVestDate(getInventoryVestDate());
    purchaseOrderDTO.setPromotionsInfoJson(getPromotionsInfoJson());
    return purchaseOrderDTO;
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

  @Column(name = "dept_id")
  public Integer getDeptId() {
    return deptId;
  }

  public void setDeptId(Integer deptId) {
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

  @Column(name = "status")
  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  @Column(name = "status_enum")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatusEnum() {
    return statusEnum;
  }

  public void setStatusEnum(OrderStatus statusEnum) {
    this.statusEnum = statusEnum;
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

  @Column(name="reviewer")
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

  @Column(name = "invalidateor_id")
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

  @Column(name = "bill_producer")
  public String getBillProducer() {
    return billProducer;
  }

  public void setBillProducer(String billProducer) {
    this.billProducer = billProducer;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
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

  @Column(name="bill_producer_id")
  public Long getBillProducerId() {
    return billProducerId;
  }

  public void setBillProducerId(Long billProducerId) {
    this.billProducerId = billProducerId;
  }

  @Column(name="receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "refuse_msg")
  public String getRefuseMsg() {
    return refuseMsg;
  }

  public void setRefuseMsg(String refuseMsg) {
    if(refuseMsg!=null && refuseMsg.length()>500){
      refuseMsg = refuseMsg.substring(0,495);
      refuseMsg += "...";
    }
    this.refuseMsg = refuseMsg;
  }

	@Column(name = "express_id")
	public Long getExpressId() {
		return expressId;
	}

	public void setExpressId(Long expressId) {
		this.expressId = expressId;
	}

	@Column(name = "pre_dispatch_date")
	public Long getPreDispatchDate() {
		return preDispatchDate;
	}

	public void setPreDispatchDate(Long preDispatchDate) {
		this.preDispatchDate = preDispatchDate;
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

  @Column(name="supplier_shop_id")
  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }

  @Column(name = "inventory_vest_date")
  public Long getInventoryVestDate() {
    return inventoryVestDate;
  }

  public void setInventoryVestDate(Long inventoryVestDate) {
    this.inventoryVestDate = inventoryVestDate;
  }

  @Column(name = "supplier_contact_id")
  public Long getSupplierContactId() {
    return supplierContactId;
  }

  public void setSupplierContactId(Long supplierContactId) {
    this.supplierContactId = supplierContactId;
  }

   @Column(name = "promotions_info_json")
  public String getPromotionsInfoJson() {
    return promotionsInfoJson;
  }

  public void setPromotionsInfoJson(String promotionsInfoJson) {
    this.promotionsInfoJson = promotionsInfoJson;
  }

  private Long shopId;
  private Long date;
  private String no;
  private String refNo;
  private Integer deptId;
  private String dept;
  private Long supplierId;
  private String supplier;
  private String supplierContact;
  private Long supplierContactId; // add by zhuj
  private String supplierMobile;
  private String supplierLandline;
  private String supplierAddress;
  private Long executorId;
  private String executor;
  private double total;
  private Long deliveryDate;
  private Long status;
  private OrderStatus statusEnum;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long reviewerId;
  private String reviewer;
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;
  private Long invalidateDate;
  private String billProducer;
  private String memo;
  private Long vestDate;
  private Long billProducerId;
  private String receiptNo;
  private String refuseMsg;    //拒绝理由
	private Long expressId;//快递信息
	private Long preDispatchDate;//卖家预计发货时间
	private Long supplierShopId;//卖家预计发货时间
  private Long inventoryVestDate;
  private String promotionsInfoJson;
}


