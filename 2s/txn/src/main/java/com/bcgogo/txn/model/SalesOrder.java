package com.bcgogo.txn.model;

import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SalesOrderDTO;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-13
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "sales_order")
public class SalesOrder extends LongIdentifier {
  public SalesOrder() {
  }

  public SalesOrder fromDTO(SalesOrderDTO salesOrderDTO) {
    if(salesOrderDTO==null)
      return this;
	  setPurchaseOrderId(salesOrderDTO.getPurchaseOrderId());
    setId(salesOrderDTO.getId());
    setShopId(salesOrderDTO.getShopId());
    setDate(salesOrderDTO.getDate());
    setNo(salesOrderDTO.getNo());
    setRefNo(salesOrderDTO.getRefNo());
    setDeptId(salesOrderDTO.getDeptId());
    setDept(salesOrderDTO.getDept());
    setCustomerId(salesOrderDTO.getCustomerId());
    setCustomer(salesOrderDTO.getCustomer());
    setCustomerCompany(salesOrderDTO.getCompany());
    if (StringUtils.isNotBlank(salesOrderDTO.getContactIdStr())&&!"null".equals(salesOrderDTO.getContactIdStr())) { // add by zhuj
      setCustomerContactId(Long.parseLong(salesOrderDTO.getContactIdStr()));
    } else {
      setCustomerContactId(salesOrderDTO.getContactId());
    }
    setCustomerContact(salesOrderDTO.getContact());
    setCustomerMobile(salesOrderDTO.getMobile());
    setCustomerLandline(salesOrderDTO.getLandline());
    setCustomerAddress(salesOrderDTO.getAddress());
    setMemberNo(salesOrderDTO.getMemberNo());
    setMemberType(salesOrderDTO.getMemberType());
    setMemberStatus(salesOrderDTO.getMemberStatus());
    setExecutorId(salesOrderDTO.getExecutorId());
    setExecutor(salesOrderDTO.getExecutor());
    setTotal(salesOrderDTO.getTotal());
    setEditorId(salesOrderDTO.getEditorId());
    setEditor(salesOrderDTO.getEditor());
    setEditDate(salesOrderDTO.getEditDate());
    setReviewerId(salesOrderDTO.getReviewerId());
    setReviewer(salesOrderDTO.getReviewer());
    setReviewDate(salesOrderDTO.getReviewDate());
    setInvalidatorId(salesOrderDTO.getInvalidatorId());
    setInvalidator(salesOrderDTO.getInvalidator());
    setInvalidateDate(salesOrderDTO.getInvalidateDate());
    setGoodsSaler(salesOrderDTO.getGoodsSaler());
    setGoodsSalerId(salesOrderDTO.getGoodsSalerId());
    setMemo(salesOrderDTO.getMemo());
    setStatusEnum(salesOrderDTO.getStatus());
    setTotalCostPrice(salesOrderDTO.getTotalCostPrice());
    setVestDate(salesOrderDTO.getVestDate());
    setReceiptNo(salesOrderDTO.getReceiptNo());
    if (null == salesOrderDTO.getAfterMemberDiscountTotal()) {
      setAfterMemberDiscountTotal(salesOrderDTO.getTotal());
    } else {
      setAfterMemberDiscountTotal(salesOrderDTO.getAfterMemberDiscountTotal());
    }
    setRefuseMsg(salesOrderDTO.getRefuseMsg());
	  setRepealMsg(salesOrderDTO.getRepealMsg());
	  setPurchaseMemo(salesOrderDTO.getPurchaseMemo());
    this.setStorehouseId(salesOrderDTO.getStorehouseId());
    this.setStorehouseName(salesOrderDTO.getStorehouseName());
    this.setStatementAccountOrderId(getStatementAccountOrderId());
    this.setCustomerShopId(salesOrderDTO.getCustomerShopId());
    this.setOtherTotalCostPrice(salesOrderDTO.getOtherTotalCostPrice());
    this.setOtherIncomeTotal(salesOrderDTO.getOtherIncomeTotal());
    setVehicleId(salesOrderDTO.getVehicleId());
    setVehicleMobile(salesOrderDTO.getVehicleMobile());
    setVehicleContact(salesOrderDTO.getVehicleContact());
    setVehicleLicenceNo(salesOrderDTO.getLicenceNo());
    return this;
  }

  public SalesOrderDTO toDTO() {
    SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
    salesOrderDTO.setId(getId());
    salesOrderDTO.setShopId(getShopId());
    salesOrderDTO.setDate(getDate());
    salesOrderDTO.setNo(getNo());
    salesOrderDTO.setRefNo(getRefNo());
    salesOrderDTO.setDeptId(getDeptId());
    salesOrderDTO.setDept(getDept());
    salesOrderDTO.setCustomerId(getCustomerId());
    salesOrderDTO.setCustomer(getCustomer());
    salesOrderDTO.setExecutorId(getExecutorId());
    salesOrderDTO.setExecutor(getExecutor());
    salesOrderDTO.setTotal(getTotal());
    salesOrderDTO.setEditorId(getEditorId());
    salesOrderDTO.setEditor(getEditor());
    salesOrderDTO.setEditDate(getEditDate());
    salesOrderDTO.setReviewerId(getReviewerId());
    salesOrderDTO.setReviewer(getReviewer());
    salesOrderDTO.setReviewDate(getReviewDate());
    salesOrderDTO.setInvalidatorId(getInvalidatorId());
    salesOrderDTO.setInvalidator(getInvalidator());
    salesOrderDTO.setInvalidateDate(getInvalidateDate());
    salesOrderDTO.setGoodsSaler(getGoodsSaler());
    salesOrderDTO.setMemo(getMemo());
    salesOrderDTO.setStatus(getStatusEnum());
    salesOrderDTO.setTotalCostPrice(getTotalCostPrice());
    salesOrderDTO.setVestDate(getVestDate());
	  salesOrderDTO.setCreationDate(getCreationDate());
    salesOrderDTO.setReceiptNo(getReceiptNo());
    if (null == getAfterMemberDiscountTotal()) {
      salesOrderDTO.setAfterMemberDiscountTotal(getTotal());
    } else {
      salesOrderDTO.setAfterMemberDiscountTotal(getAfterMemberDiscountTotal());
    }

    salesOrderDTO.setPurchaseOrderId(getPurchaseOrderId());
    salesOrderDTO.setRefuseMsg(getRefuseMsg());
	  salesOrderDTO.setPreDispatchDate(getPreDispatchDate());
	  salesOrderDTO.setSaleMemo(getSaleMemo());
	  salesOrderDTO.setExpressId(getExpressId());
	  salesOrderDTO.setRepealMsg(getRepealMsg());
	  salesOrderDTO.setPurchaseMemo(getPurchaseMemo());
    salesOrderDTO.setCompany(getCustomerCompany());
    salesOrderDTO.setMobile(getCustomerMobile());
    salesOrderDTO.setLandline(getCustomerLandline());
    salesOrderDTO.setAddress(getCustomerAddress());
    salesOrderDTO.setMemberNo(getMemberNo());
    salesOrderDTO.setMemberStatus(getMemberStatus());
    salesOrderDTO.setMemberType(getMemberType());
    salesOrderDTO.setContact(getCustomerContact());
    salesOrderDTO.setContactId(getCustomerContactId()); // add by zhuj
    salesOrderDTO.setStorehouseName(this.getStorehouseName());
    salesOrderDTO.setStorehouseId(this.getStorehouseId());
    salesOrderDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    salesOrderDTO.setCustomerShopId(getCustomerShopId());
    salesOrderDTO.setOtherTotalCostPrice(this.getOtherTotalCostPrice());
    salesOrderDTO.setOtherIncomeTotal(this.getOtherIncomeTotal());
    salesOrderDTO.setVehicleContact(getVehicleContact());
    salesOrderDTO.setVehicleId(getVehicleId());
    salesOrderDTO.setVehicleMobile(getVehicleMobile());
    salesOrderDTO.setLicenceNo(getVehicleLicenceNo());
    return salesOrderDTO;
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

  @Column(name = "invalidate_date", length = 20)
  public Long getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(Long invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  @Column(name = "goods_saler", length = 50)
  public String getGoodsSaler() {
    return goodsSaler;
  }

  public void setGoodsSaler(String goodsSaler) {
    this.goodsSaler = goodsSaler;
  }

   @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
  @Column(name="total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
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

  @Column(name="vest_date")
  public Long getVestDate()
  {
    return this.vestDate;
  }

  public void setVestDate(Long vestDate)
  {
      this.vestDate = vestDate;
  }

  @Column(name = "goods_saler_id")
  public Long getGoodsSalerId() {
    return goodsSalerId;
  }

  public void setGoodsSalerId(Long goodsSalerId) {
    this.goodsSalerId = goodsSalerId;
  }

  @Column(name="receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name="after_member_discount_total")
  public Double getAfterMemberDiscountTotal() {
    return afterMemberDiscountTotal;
  }

  public void setAfterMemberDiscountTotal(Double afterMemberDiscountTotal) {
    this.afterMemberDiscountTotal = afterMemberDiscountTotal;
  }

	@Column(name = "purchase_order_id")
	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Long purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
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

	@Column(name = "pre_dispatch_date")
	public Long getPreDispatchDate() {
		return preDispatchDate;
	}

	public void setPreDispatchDate(Long preDispatchDate) {
		this.preDispatchDate = preDispatchDate;
	}

	@Column(name = "sale_memo")
	public String getSaleMemo() {
		return saleMemo;
	}

	public void setSaleMemo(String saleMemo) {
		this.saleMemo = saleMemo;
	}

	@Column(name = "express_id")
	public Long getExpressId() {
		return expressId;
	}

	public void setExpressId(Long expressId) {
		this.expressId = expressId;
	}

	 @Column(name = "repeal_msg")
	public String getRepealMsg() {
		return repealMsg;
	}

	public void setRepealMsg(String repealMsg) {
		if (repealMsg != null && repealMsg.length() > 500) {
      repealMsg = repealMsg.substring(0, 495);
			repealMsg += "...";
		}
		this.repealMsg = repealMsg;
	}

	@Column(name = "purchase_memo")
	public String getPurchaseMemo() {
		return purchaseMemo;
	}

	public void setPurchaseMemo(String purchaseMemo) {
			if (purchaseMemo != null && purchaseMemo.length() > 500) {
        purchaseMemo = purchaseMemo.substring(0, 495);
			purchaseMemo += "...";
		}
		this.purchaseMemo = purchaseMemo;
	}
  @Column(name="storehouse_id")
  public Long getStorehouseId() {
    return storehouseId;
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

  @Column(name = "customer_contact_id")
  public Long getCustomerContactId() {
    return customerContactId;
  }

  public void setCustomerContactId(Long customerContactId) {
    this.customerContactId = customerContactId;
  }

  @Column(name="customer_contact")
  public String getCustomerContact() {
    return customerContact;
  }

  public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
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

  @Column(name = "other_total_cost_price")
  public Double getOtherTotalCostPrice() {
    return otherTotalCostPrice;
  }

  public void setOtherTotalCostPrice(Double otherTotalCostPrice) {
    this.otherTotalCostPrice = otherTotalCostPrice;
  }

  @Column(name = "other_income_total")
  public Double getOtherIncomeTotal() {
    return otherIncomeTotal;
  }

  public void setOtherIncomeTotal(Double otherIncomeTotal) {
    this.otherIncomeTotal = otherIncomeTotal;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Column(name = "vehicle_contact")
  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  @Column(name = "vehicle_mobile")
  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  @Column(name = "vehicle_licence_no")
  public String getVehicleLicenceNo() {
    return vehicleLicenceNo;
  }

  public void setVehicleLicenceNo(String vehicleLicenceNo) {
    this.vehicleLicenceNo = vehicleLicenceNo;
  }

  private Long statementAccountOrderId;//对账单id
  private Long shopId;
  private Long date;
  private String no;
  private String refNo;
  private Integer deptId;
  private String dept;                 //瞬态字段
  private Long customerId;
  private String customer;
  private Long customerContactId;
  private String customerContact;
  private String customerCompany;
  private String customerMobile;
  private String customerLandline;
  private String customerAddress;
  private String memberNo;
  private String memberType;
  private MemberStatus memberStatus;
  private Long executorId;
  private String executor;             //瞬态字段
  private double total;
  private Long editorId;
  private String editor;               //瞬态字段
  private Long editDate;
  private Long reviewerId;
  private String reviewer;             //瞬态字段
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;          //瞬态字段
  private Long invalidateDate;
  private String goodsSaler;
  private String memo;
  private Long status;
  private OrderStatus statusEnum;
  private Double totalCostPrice;
  private Long vestDate;
  private Long goodsSalerId;
  private String receiptNo; //单据号
  private Double afterMemberDiscountTotal;
	private Long purchaseOrderId;//关联的采购单Id
	private Double lackAmount;
  private String refuseMsg;    //拒绝理由
	private Long preDispatchDate;//预计发货时间
	private String saleMemo;//接受销售单的时候填写的备注
	private Long expressId;//快递信息
	private String repealMsg;//作废理由
	private String purchaseMemo;//采购备注

  private Long storehouseId;
  private String storehouseName;
  private Long customerShopId; //在线单据的客户店面ID

  private Double otherTotalCostPrice; //施工单其他费用成本总和
  private Double otherIncomeTotal;//其他费用总和

  private Long vehicleId;
  private String vehicleContact;//车主
  private String vehicleMobile;//车主电话
  private String vehicleLicenceNo;//车牌号

}