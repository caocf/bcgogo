package com.bcgogo.txn.dto;


import com.bcgogo.config.dto.ShopUnitDTO;
import com.bcgogo.enums.*;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-16
 * To change this template use File | Settings | File Templates.
 */
public class SalesOrderDTO extends BcgogoOrderDto implements Cloneable{
  private String shopName;
  private String shopAddress;
  private String shopLandLine;
  private Long date;
  private String no;
  private String refNo;
  private Integer deptId;
  private String dept;
  private Long customerId;
  private String customerIdStr;
  private String customer;
  private CustomerStatus customerStatus;
  private Long vehicleId;
  private String vehicleContact;//车主
  private String vehicleMobile;//车主电话
  private String vehicleColor;// 车辆颜色
  private String vehicleBrand;//车辆名牌
  private String vehicleModel;//车型
  private String memberType;
  private MemberStatus memberStatus;
  private Long executorId;
  private String executor;
  private Double total;
  private String totalStr;
  private double totalHid;
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private String editDateStr;
  private Long reviewerId;
  private String reviewer;
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;
  private Long invalidateDate;
  private SalesOrderItemDTO[] itemDTOs;
  private String goodsSaler;
  private Long goodsSalerId;
  private Long creationDate;
  private String mobile;
  private String landline;
  private String address;
  private String fax;
  private String memberNo;
  private String licenceNo;//车牌号
  private String brand;
  private Long brandId;
  private String model;
  private Long modelId;
  private String year;
  private Long yearId;
  private Long engineId;
  private String engine;
  private Long receivableId;
  private Double settledAmount;    //实收
  private String settledAmountStr;
  private Double settledAmountHid;
  private Double debt;              //欠款
  private Double totalDebt;
  private Long paymentTime;//欠款的还款时间 add by liuWei
  private OrderStatus status;
  private String returnType;
  private String returnIndex;
  private ShopUnitDTO[] shopUnits;    //单据页面用到的单位
  private String totalMoneyStr;
  private String settledAmountMoneyStr;
  private String draftOrderIdStr;

  private String payee;   //结算人
  private Double memberAmount;  //储值支付
  private Double cashAmount;        //支付方式:现金
  private Double bankAmount;      //支付方式 银行卡
  private Double bankCheckAmount;       //支付方式 支票
  private String bankCheckNo;          //支付方式 支票号码
  private Double customerDeposit; //支付方式 客户预存款 add by zhuj



  private Long memberId;    //会员id
  private Double memberBalance;//会员卡余额
  private double strikeAmount;

  private String accountMemberNo; //结算时填的会员号码
  private String accountMemberPassword;  //结算时填的会员密码
  private Long accountMemberId;//结算时会员id


    // add by liuWei 营业统计页面显示table
  private String orderContent; //销售单内容
  private String orderContentStr;
  private double orderPurchaseCost; //销售单成本
  private double orderProfit; //销售单毛利
  private String orderProfitPercent; //销售单毛利率
  private Long vestDate; //归属时间
  private String vestDateStr;
  private String accountDateStr; //结算时间，区别于vestDate

  private double orderDiscount; //销售单折扣
  private double discount;
  private String print;
  private String paymentStr;
  private Long purchaseOrderId;     //关联的采购单Id
  private String refuseMsg;//拒绝理由

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;

  private boolean isShortage = false; //本单据是否有缺料商品

  private String huankuanTime;//销售单还款时间
  private String purchaseVestDate;//采购单要求送货时间

	private Long preDispatchDate;//预计发货时间
	private String preDispatchDateStr;//预计发货时间 yyyy-MM-dd
	private String saleMemo;//接受销售单的时候填写的备注
	private Long expressId;//快递信息
	private String waybills; //快递号
	private String company;  //快递公司名
	private String dispatchMemo;  //快递备注
	private String repealMsg;//作废理由
	private String purchaseMemo;//采购备注
  private String acceptMemo;


  private Double amountTotal;
  private Double itemTotal;
  private Double salesTotal;
  private Double otherIncomeTotal;

  private String qq;
  private String email;
  private String promotionsInfoJson;
  private PromotionsInfoDTO promotionsInfoDTO;
  private Long customerShopId;
  private List<SalesOrderOtherIncomeItemDTO> otherIncomeItemDTOList;
  private Double otherTotalCostPrice; //施工单其他费用成本总和


  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  public double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public String getHuankuanTime() {
    return huankuanTime;
  }

  public void setHuankuanTime(String huankuanTime) {
    this.huankuanTime = huankuanTime;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public Double getMemberAmount() {
    return memberAmount;
  }

  public void setMemberAmount(Double memberAmount) {
    this.memberAmount = memberAmount;
  }

  public String getPurchaseVestDate() {
    return purchaseVestDate;
  }

  public void setPurchaseVestDate(String purchaseVestDate) {
    this.purchaseVestDate = purchaseVestDate;
  }

  public Double getBankAmount() {
    return bankAmount;

  }

  public void setBankAmount(Double bankAmount) {
    this.bankAmount = bankAmount;
  }

  public Double getCashAmount() {
    return cashAmount;
  }

  public void setCashAmount(Double cashAmount) {
    this.cashAmount = cashAmount;
  }

  public Double getBankCheckAmount() {
    return bankCheckAmount;
  }

  public void setBankCheckAmount(Double bankCheckAmount) {
    this.bankCheckAmount = bankCheckAmount;
  }

  public String getBankCheckNo() {
    return bankCheckNo;
  }

  public void setBankCheckNo(String bankCheckNo) {
    this.bankCheckNo = bankCheckNo;
  }

  public String getAccountMemberNo() {
    return accountMemberNo;
  }

  public void setAccountMemberNo(String accountMemberNo) {
    this.accountMemberNo = accountMemberNo;
  }

  public String getAccountMemberPassword() {
    return accountMemberPassword;
  }

  public void setAccountMemberPassword(String accountMemberPassword) {
    this.accountMemberPassword = accountMemberPassword;
  }

	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Long purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

  public String getRefuseMsg() {
    return refuseMsg;
  }

  public void setRefuseMsg(String refuseMsg) {
    this.refuseMsg = refuseMsg;
  }

	public String getPurchaseMemo() {
		return purchaseMemo;
	}

	public void setPurchaseMemo(String purchaseMemo) {
		this.purchaseMemo = purchaseMemo;
	}

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  private String receiptNo;

  public SalesOrderDTO() {
  }

  public SalesOrderDTO(RepairOrderDTO repairOrderDTO) {
    //this.shopId = repairOrderDTO.getShopId();
    this.setShopId(repairOrderDTO.getShopId());
    this.date = repairOrderDTO.getDate();
    this.no = repairOrderDTO.getNo();
    this.dept = repairOrderDTO.getDept();
    this.customerId = repairOrderDTO.getCustomerId();
    this.customer = repairOrderDTO.getCustomerName();
    this.executorId = repairOrderDTO.getExecutorId();
    this.executor = repairOrderDTO.getExecutor();
    this.total = repairOrderDTO.getTotal();
    this.memo = repairOrderDTO.getMemo();
    this.editorId = repairOrderDTO.getEditorId();
    this.editor = repairOrderDTO.getEditor();
    this.editDate = repairOrderDTO.getEditDate();
    this.reviewerId = repairOrderDTO.getReviewerId();
    this.reviewer = repairOrderDTO.getReviewer();
    this.reviewDate = repairOrderDTO.getReviewDate();
    this.invalidatorId = repairOrderDTO.getInvalidatorId();
    this.invalidator = repairOrderDTO.getInvalidator();
    this.editDate = repairOrderDTO.getStartDate();
    this.editDateStr = repairOrderDTO.getStartDateStr();
    this.licenceNo = repairOrderDTO.getLicenceNo();
    this.vestDate = repairOrderDTO.getVestDate();
    this.vestDateStr = repairOrderDTO.getVestDateStr();
    this.setItemDTOsFromRepairOrder(repairOrderDTO.getItemDTOs());
    this.setStorehouseId(repairOrderDTO.getStorehouseId());
    this.setStorehouseName(repairOrderDTO.getStorehouseName());
  }

public SalesOrderDTO setPurchaseOrderDTO(PurchaseOrderDTO purchaseOrderDTO) {
	if (purchaseOrderDTO != null) {
		setShopId(purchaseOrderDTO.getSupplierShopId());
		setDate(purchaseOrderDTO.getDate());
		setTotal(purchaseOrderDTO.getTotal());
		setPurchaseOrderId(purchaseOrderDTO.getId());
    setCustomerShopId(purchaseOrderDTO.getShopId());
		//todo
		setTotalCostPrice(null);

		setPurchaseMemo(purchaseOrderDTO.getMemo());
		 List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
		if(!ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())){
			 for(PurchaseOrderItemDTO purchaseOrderItemDTO :purchaseOrderDTO.getItemDTOs()){
				 salesOrderItemDTOs.add(new SalesOrderItemDTO(purchaseOrderItemDTO));
			 }
		}
		if(CollectionUtils.isNotEmpty(salesOrderItemDTOs)){
			this.itemDTOs = salesOrderItemDTOs.toArray(new SalesOrderItemDTO[salesOrderItemDTOs.size()]);
		}
	}
	return this;
}


	public void setCustomerDTO(CustomerDTO customerDTO) {
    if(customerDTO==null)return;
		this.setCustomer(customerDTO.getName());
		this.setCustomerId(customerDTO.getId());
    this.setLandline(customerDTO.getLandLine());
		this.setAddress(customerDTO.getAddress());
    this.setCustomerStatus(customerDTO.getStatus());
    this.setCompany(customerDTO.getCompany());

    boolean isHaveSameContact = false;
    if (this.getContactId() != null && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
      for (ContactDTO contactDTO : customerDTO.getContacts()) {
        if (contactDTO != null && getContactId().equals(contactDTO.getId())) {
          isHaveSameContact = true;
          setContact(contactDTO.getName());
          setMobile(contactDTO.getMobile());
          setContactId(contactDTO.getId());
          setContactIdStr(com.bcgogo.utils.StringUtil.valueOf(contactDTO.getId()));
          setQq(contactDTO.getQq());
          setEmail(contactDTO.getEmail());
          break;
        }
      }
    }
    if (!isHaveSameContact && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
      ContactDTO contactDTO = customerDTO.getContacts()[0];
      if (contactDTO != null) {
        setContact(contactDTO.getName());
        setMobile(contactDTO.getMobile());
        setContactId(contactDTO.getId());
        setContactIdStr(contactDTO.getId().toString());
        setQq(contactDTO.getQq());
        setEmail(contactDTO.getEmail());
      }
    }
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.SALE);
    orderIndexDTO.setCreationDate(this.getCreationDate());
    //orderIndex中的状态和salesOrderDTO的状态保持一致。
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setCustomerOrSupplierId(this.getCustomerId());
    orderIndexDTO.setCustomerOrSupplierName(this.getCustomer());
    orderIndexDTO.setCustomerStatus(this.getCustomerStatus());
    orderIndexDTO.setContactNum(this.getMobile());
    orderIndexDTO.setAddress(this.getAddress());
    orderIndexDTO.setContact(this.getContact());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setCustomerOrSupplierShopId(this.getCustomerShopId());
    if (this.getLicenceNo() != null && !this.getLicenceNo().equals("")) {
      orderIndexDTO.setVehicle(this.getLicenceNo());
    }
    orderIndexDTO.setVehicleContact(this.getVehicleContact());
    orderIndexDTO.setVehicleBrand(this.getVehicleBrand());
    orderIndexDTO.setVehicleModel(this.getVehicleModel());
    orderIndexDTO.setVehicleColor(this.getVehicleColor());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setOrderTotalCostPrice(this.getTotalCostPrice());
    orderIndexDTO.setArrears(this.getDebt());
    orderIndexDTO.setOrderDebt(this.getDebt());
    orderIndexDTO.setOrderSettled(this.getSettledAmount());
    orderIndexDTO.setDiscount(this.getOrderDiscount());
    orderIndexDTO.setMemberBalancePay(this.getMemberAmount());
    orderIndexDTO.setAccountMemberId(this.getAccountMemberId());
    orderIndexDTO.setAccountMemberNo(this.getAccountMemberNo());
    if (StringUtils.isBlank(this.getGoodsSaler())) {
      orderIndexDTO.setSalesMans(RfTxnConstant.ASSISTANT_NAME);
    } else {
      orderIndexDTO.setSalesMans(this.getGoodsSaler());
    }
    orderIndexDTO.setStorehouseName(this.getStorehouseName());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    StringBuffer str = new StringBuffer();
    str.append("销售内容:");
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    if (this.getItemDTOs() != null && this.getItemDTOs().length > 0) {
      for (SalesOrderItemDTO salesOrderItemDTO : this.getItemDTOs()) {
        if (salesOrderItemDTO == null) continue;
        //添加每个单据的产品信息
        itemIndexDTOList.add(salesOrderItemDTO.toItemIndexDTO(this));
        inOutRecordDTOList.addAll(salesOrderItemDTO.toInOutRecordDTO(this));

        str.append("(品名:").append(salesOrderItemDTO.getProductName());
        if (!StringUtils.isBlank(salesOrderItemDTO.getBrand())) {
          str.append(",品牌:").append(salesOrderItemDTO.getBrand()).append(",单价:").append(salesOrderItemDTO.getPrice())
            .append("数量:").append(salesOrderItemDTO.getAmount()).append(");");
        } else {
          str.append("单价:").append(salesOrderItemDTO.getPrice()).append("数量:").append(salesOrderItemDTO.getAmount())
            .append(");");
        }
      }
      orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
      orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    }

    if (CollectionUtils.isNotEmpty(this.getOtherIncomeItemDTOList())) {
      str.append("其他费用:");
      for (SalesOrderOtherIncomeItemDTO otherIncomeItemDTO : this.getOtherIncomeItemDTOList()) {
        if (otherIncomeItemDTO == null) continue;
        if (StringUtils.isBlank(otherIncomeItemDTO.getName())) {
          continue;
        }
        str.append("(").append(otherIncomeItemDTO.getName()).append(":").append(NumberUtil.doubleVal(otherIncomeItemDTO.getPrice()));
        str.append("元);");
      }
    }


    if(CollectionUtils.isNotEmpty(this.getOtherIncomeItemDTOList())) {
      for (SalesOrderOtherIncomeItemDTO itemDTO : this.getOtherIncomeItemDTOList()) {
        if (itemDTO == null) continue;
        itemIndexDTOList.add(itemDTO.toItemIndexDTO(this));
      }

    }

    String orderContent = str.substring(0, str.length() - 1);
    if (orderContent.length() > 450) {
      orderContent = orderContent.substring(0, 450);
      orderContent = orderContent + "等";
    }
    orderIndexDTO.setOrderContent(orderContent);
    orderIndexDTO.setMemberDiscountRatio(this.getMemberDiscountRatio());
    if (null != this.getAfterMemberDiscountTotal()) {
      orderIndexDTO.setAfterMemberDiscountTotal(this.getAfterMemberDiscountTotal());
    } else {
      orderIndexDTO.setAfterMemberDiscountTotal(this.getTotal());
    }
    if (this.getPaymentTime()!=null && this.getPaymentTime() != 0) {
      orderIndexDTO.setPaymentTime(this.getPaymentTime());
    }

    List<PayMethod> payMethods = new ArrayList<PayMethod>();
    if (NumberUtil.doubleVal(this.getCashAmount()) > 0) { //现金
      payMethods.add(PayMethod.CASH);
    }
    if (NumberUtil.doubleVal(this.getBankAmount()) > 0) { //银行卡
      payMethods.add(PayMethod.BANK_CARD);
    }
    if (NumberUtil.doubleVal(this.getBankCheckAmount()) > 0) {// 支票
      payMethods.add(PayMethod.CHEQUE);
    }
    if (this.getAccountMemberId() != null && this.getAccountMemberId() != 0) {   //会员支付
      payMethods.add(PayMethod.MEMBER_BALANCE_PAY);
    }
    if (NumberUtil.doubleVal(this.getCustomerDeposit()) > 0) { //预收款 add by zhuj
      payMethods.add(PayMethod.CUSTOMER_DEPOSIT);
    }
    if(this.getStatementAccountOrderId() != null){//对账支付
      payMethods.add(PayMethod.STATEMENT_ACCOUNT);
    }

    orderIndexDTO.setPayMethods(payMethods);
    return orderIndexDTO;
  }

  private void setItemDTOsFromRepairOrder(RepairOrderItemDTO[] itemDTOs) {
    this.itemDTOs = new SalesOrderItemDTO[itemDTOs.length];
    for (int i = 0; i < itemDTOs.length; i++) {
      RepairOrderItemDTO repairOrderItemDTO = itemDTOs[i];
      SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO();
      salesOrderItemDTO.setSalesOrderId(this.getId());
      salesOrderItemDTO.setProductId(repairOrderItemDTO.getProductId());
      salesOrderItemDTO.setProductName(repairOrderItemDTO.getProductName());
      salesOrderItemDTO.setBrand(repairOrderItemDTO.getBrand());
      salesOrderItemDTO.setSpec(repairOrderItemDTO.getSpec());
      salesOrderItemDTO.setModel(repairOrderItemDTO.getModel());
      salesOrderItemDTO.setAmount(repairOrderItemDTO.getAmount());
      salesOrderItemDTO.setPrice(repairOrderItemDTO.getPrice());
      salesOrderItemDTO.setTotal(repairOrderItemDTO.getTotal());
      salesOrderItemDTO.setMemo(repairOrderItemDTO.getMemo());
      salesOrderItemDTO.setCostPrice(repairOrderItemDTO.getCostPrice());
      salesOrderItemDTO.setTotalCostPrice(repairOrderItemDTO.getTotalCostPrice());
      this.itemDTOs[i] = salesOrderItemDTO;
    }
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
    this.setPtOrderStatus(status);
  }

  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public String getRefNo() {
    return refNo;
  }

  public void setRefNo(String refNo) {
    this.refNo = refNo;
  }

  public Integer getDeptId() {
    return deptId;
  }

  public void setDeptId(Integer deptId) {
    this.deptId = deptId;
  }

  public String getDept() {
    return dept;
  }

  public void setDept(String dept) {
    this.dept = dept;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
    if(customerId != null) {
       setCustomerIdStr(String.valueOf(customerId));
    }
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public Long getExecutorId() {
    return executorId;
  }

  public void setExecutorId(Long executorId) {
    this.executorId = executorId;
  }

  public String getExecutor() {
    return executor;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
  }

  public Double getTotal() {
    if(total==null){
      return 0D;
    }
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
    if(total >= 0){
      this.totalStr = MoneyUtil.toBigType(String.valueOf(total));
    }else{
      this.totalStr = "负" + MoneyUtil.toBigType(String.valueOf(0 -total));
    }

    this.totalMoneyStr = NumberUtil.roundToString(total,2);
  }

  public String getTotalStr() {
    return MoneyUtil.toBigType(String.valueOf(total));
  }

  public void setTotalStr(String totalStr) {
    this.totalStr = totalStr;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public Long getReviewerId() {
    return reviewerId;
  }

  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  public String getReviewer() {
    return reviewer;
  }

  public void setReviewer(String reviewer) {
    this.reviewer = reviewer;
  }

  public Long getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  public Long getInvalidatorId() {
    return invalidatorId;
  }

  public void setInvalidatorId(Long invalidatorId) {
    this.invalidatorId = invalidatorId;
  }

  public String getInvalidator() {
    return invalidator;
  }

  public void setInvalidator(String invalidator) {
    this.invalidator = invalidator;
  }

  public Long getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(Long invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  public SalesOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(SalesOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

//  public Long getId() {
//    return this.id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Double getSettledAmount() {
    if(settledAmount==null){
      return 0D;
    }
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
    this.settledAmountStr = MoneyUtil.toBigType(String.valueOf(settledAmount));
    this.settledAmountMoneyStr = NumberUtil.roundToString(settledAmount,2);
  }

  public String getSettledAmountStr() {
	  if(settledAmount !=null){
		  return MoneyUtil.toBigType(String.valueOf(settledAmount));
	  } else {
		  return "";
	  }


  }

  public void setSettledAmountStr(String settledAmountStr) {
    this.settledAmountStr = settledAmountStr;
  }

  public Double getSettledAmountHid() {
      if(settledAmountHid==null){
        return 0D;
      }
    return settledAmountHid;
  }

  public void setSettledAmountHid(Double settledAmountHid) {
    this.settledAmountHid = settledAmountHid;
  }

  public Double getDebt() {
    if(debt==null){
      return 0D;
    }
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  public Double getTotalDebt() {
    return totalDebt;
  }

  public void setTotalDebt(Double totalDebt) {
    this.totalDebt = totalDebt;
  }

  public Long getReceivableId() {
    return receivableId;
  }

  public void setReceivableId(Long receivableId) {
    this.receivableId = receivableId;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public Long getYearId() {
    return yearId;
  }

  public void setYearId(Long yearId) {
    this.yearId = yearId;
  }

  public Long getEngineId() {
    return engineId;
  }

  public void setEngineId(Long engineId) {
    this.engineId = engineId;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public double getTotalHid() {
    return totalHid;
  }

  public void setTotalHid(double totalHid) {
    this.totalHid = totalHid;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopAddress() {
    return shopAddress;
  }

  public void setShopAddress(String shopAddress) {
    this.shopAddress = shopAddress;
  }

  public String getShopLandLine() {
    return shopLandLine;
  }

  public void setShopLandLine(String shopLandLine) {
    this.shopLandLine = shopLandLine;
  }

  public String getGoodsSaler() {
    return goodsSaler;
  }

  public void setGoodsSaler(String goodsSaler) {
    this.goodsSaler = goodsSaler;
  }

  public String getAcceptMemo() {
    return acceptMemo;
  }

  public void setAcceptMemo(String acceptMemo) {
    this.acceptMemo = acceptMemo;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public String getReturnIndex() {
    return returnIndex;
  }

  public void setReturnIndex(String returnIndex) {
    this.returnIndex = returnIndex;
  }



  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }

  private InventoryLimitDTO inventoryLimitDTO;
  public ShopUnitDTO[] getShopUnits() {
    return shopUnits;
  }

  public void setShopUnits(ShopUnitDTO[] shopUnits) {
    this.shopUnits = shopUnits;
  }

  public double getOrderDiscount() {
    return orderDiscount;
  }

  public void setOrderDiscount(double orderDiscount) {
    this.orderDiscount = orderDiscount;
    this.discount=orderDiscount;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  public String getOrderContentStr() {
    return orderContentStr;
  }

  public void setOrderContentStr(String orderContentStr) {
    this.orderContentStr = orderContentStr;
  }

  public String getOrderContent() {
    return orderContent;
  }

  public void setOrderContent(String orderContent) {
    this.orderContent = orderContent;
  }

  public double getOrderPurchaseCost() {
    return orderPurchaseCost;
  }

  public void setOrderPurchaseCost(double orderPurchaseCost) {
    BigDecimal bigDecimal = new BigDecimal(orderPurchaseCost);
    this.orderPurchaseCost = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  public double getOrderProfit() {
    return orderProfit;
  }

  public void setOrderProfit(double orderProfit) {
    BigDecimal bigDecimal = new BigDecimal(orderProfit);
    this.orderProfit = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  public String getOrderProfitPercent() {
    return orderProfitPercent;
  }

  public void setOrderProfitPercent(String orderProfitPercent) {
    this.orderProfitPercent = orderProfitPercent;
  }


  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  private String contact;

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
    setContactIdStr(StringUtil.longToString(contactId, ""));
  }

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

  private Long contactId; // add by zhuj
  private String contactIdStr;
  private Double totalCostPrice;

  public Double getTotalCostPrice() {
	  if(totalCostPrice == null){
		  return 0D;
	  }
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  public InventoryLimitDTO getInventoryLimitDTO() {
    return inventoryLimitDTO;
  }

  public void setInventoryLimitDTO(InventoryLimitDTO inventoryLimitDTO) {
    this.inventoryLimitDTO = inventoryLimitDTO;
  }

  public Long getVestDate(){
      return this.vestDate;
  }

  public void setVestDate(Long vestDate){
      this.vestDateStr= DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, vestDate);
      this.vestDate =vestDate;
  }

  public String getVestDateStr()
  {
      return this.vestDateStr;
  }

  public void setVestDateStr(String vestDateStr)
  {
      this.vestDateStr =vestDateStr;
  }

  public String getAccountDateStr() {
    return accountDateStr;
  }

  public void setAccountDateStr(String accountDateStr) {
    this.accountDateStr = accountDateStr;
  }

  public Long getGoodsSalerId() {
    return goodsSalerId;
  }

  public CustomerStatus getCustomerStatus() {
    return customerStatus;
  }

  public void setCustomerStatus(CustomerStatus customerStatus) {
    this.customerStatus = customerStatus;
  }

  public void setGoodsSalerId(Long goodsSalerId) {
    this.goodsSalerId = goodsSalerId;
  }


  public String getTotalMoneyStr() {
    return totalMoneyStr;
  }

  public void setTotalMoneyStr(String totalMoneyStr) {
    this.totalMoneyStr = totalMoneyStr;
  }

  public String getSettledAmountMoneyStr() {
    return settledAmountMoneyStr;
  }

  public void setSettledAmountMoneyStr(String settledAmountMoneyStr) {
    this.settledAmountMoneyStr = settledAmountMoneyStr;
  }

  public String getDraftOrderIdStr() {
    return draftOrderIdStr;
  }

  public void setDraftOrderIdStr(String draftOrderIdStr) {
    this.draftOrderIdStr = draftOrderIdStr;
  }

  public String getPrint() {
    return print;
  }

  public void setPrint(String print) {
    this.print = print;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getPaymentStr() {
    return paymentStr;
  }

  public void setPaymentStr(String paymentStr) {
    this.paymentStr = paymentStr;
  }

  public Long getAccountMemberId() {
    return accountMemberId;
  }

  public void setAccountMemberId(Long accountMemberId) {
    this.accountMemberId = accountMemberId;
  }

  public Double getMemberDiscountRatio() {
    return memberDiscountRatio;
  }

  public void setMemberDiscountRatio(Double memberDiscountRatio) {
    this.memberDiscountRatio = memberDiscountRatio;
  }

  public Double getAfterMemberDiscountTotal() {
    return afterMemberDiscountTotal;
  }

  public void setAfterMemberDiscountTotal(Double afterMemberDiscountTotal) {
    this.afterMemberDiscountTotal = afterMemberDiscountTotal;
  }

  public boolean isShortage() {
    return isShortage;
  }

  public void setShortage(boolean shortage) {
    isShortage = shortage;
  }

	public Long getPreDispatchDate() {
		return preDispatchDate;
	}

	public void setPreDispatchDate(Long preDispatchDate) {
		this.preDispatchDate = preDispatchDate;
		if(preDispatchDate != null){
			setPreDispatchDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,preDispatchDate));
		}
	}

	public String getPreDispatchDateStr() {
		return preDispatchDateStr;
	}

	public void setPreDispatchDateStr(String preDispatchDateStr) {
		this.preDispatchDateStr = preDispatchDateStr;
	}

  public Double getItemTotal() {
    return itemTotal;
  }

  public void setItemTotal(Double itemTotal) {
    this.itemTotal = itemTotal;
  }

  public Double getSalesTotal() {
    return salesTotal;
  }

  public void setSalesTotal(Double salesTotal) {
    this.salesTotal = salesTotal;
  }

  public Double getOtherIncomeTotal() {
    return otherIncomeTotal;
  }

  public void setOtherIncomeTotal(Double otherIncomeTotal) {
    this.otherIncomeTotal = otherIncomeTotal;
  }

	public void setPreDispatchDateFromPage(String preDispatchDateStr) {
		if (StringUtil.isNotEmpty(preDispatchDateStr)) {
			if ("today".equals(preDispatchDateStr)) {
				this.preDispatchDate = DateUtil.getInnerDayTime(0);
			} else if ("tomorrow".equals(preDispatchDateStr)) {
				this.preDispatchDate = DateUtil.getInnerDayTime(1);
			} else if ("innerThreeDays".equals(preDispatchDateStr)) {
				this.preDispatchDate = DateUtil.getInnerDayTime(3);
			} else {
				try {
					this.preDispatchDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, preDispatchDateStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getSaleMemo() {
		return saleMemo;
	}

	public void setSaleMemo(String saleMemo) {
		this.saleMemo = saleMemo;
	}

	public Long getExpressId() {
		return expressId;
	}

	public void setExpressId(Long expressId) {
		this.expressId = expressId;
	}

	public String getWaybills() {
		return waybills;
	}

	public void setWaybills(String waybills) {
		this.waybills = waybills;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDispatchMemo() {
		return dispatchMemo;
	}

	public void setDispatchMemo(String dispatchMemo) {
		this.dispatchMemo = dispatchMemo;
	}

	public String getRepealMsg() {
		return repealMsg;
	}

	public void setRepealMsg(String repealMsg) {
		this.repealMsg = repealMsg;
	}

  public List<SalesOrderOtherIncomeItemDTO> getOtherIncomeItemDTOList() {
    return otherIncomeItemDTOList;
  }

  public void setOtherIncomeItemDTOList(List<SalesOrderOtherIncomeItemDTO> otherIncomeItemDTOList) {
    this.otherIncomeItemDTOList = otherIncomeItemDTOList;
  }

  public String getLandline() {
    return landline;
  }

  public void setLandline(String landline) {
    this.landline = landline;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public MemberStatus getMemberStatus() {
    return memberStatus;
  }

  public void setMemberStatus(MemberStatus memberStatus) {
    this.memberStatus = memberStatus;
  }

  public Double getCustomerDeposit() {
    return customerDeposit;
  }

  public void setCustomerDeposit(Double customerDeposit) {
    this.customerDeposit = customerDeposit;
  }

  public Double getAmountTotal() {
    return amountTotal;
  }

  public void setAmountTotal(Double amountTotal) {
    this.amountTotal = amountTotal;
  }

  public Double getOtherTotalCostPrice() {
    return otherTotalCostPrice;
  }

  public void setOtherTotalCostPrice(Double otherTotalCostPrice) {
    this.otherTotalCostPrice = otherTotalCostPrice;
  }

  public String toString() {

    final StringBuilder sb = new StringBuilder();
    sb.append("SalesOrderDTO");
    if (this == null) {
      sb.append("{salesOrder is null}");
      return sb.toString();
    }
    sb.append("{id=").append(this.getId()==null?"":this.getId());
    sb.append(", shopId=").append(this.getShopId()==null?"":this.getShopId());
    sb.append(", shopName='").append(StringUtil.truncValue(shopName)).append('\'');
    sb.append(", shopAddress='").append(StringUtil.truncValue(shopAddress)).append('\'');
    sb.append(", shopLandLine='").append(StringUtil.truncValue(shopLandLine)).append('\'');
    sb.append(", date=").append(date == null ? "" : date);
    sb.append(", no='").append(StringUtil.truncValue(no)).append('\'');
    sb.append(", refNo='").append(StringUtil.truncValue(refNo)).append('\'');
    sb.append(", deptId=").append(deptId == null ? "" : deptId);
    sb.append(", dept='").append(StringUtil.truncValue(dept)).append('\'');
    sb.append(", customerId=").append(customerId == null ? "" : customerId);
    sb.append(", customer='").append(StringUtil.truncValue(customer)).append('\'');
    sb.append(", executorId=").append(executorId == null ? "" : executorId);
    sb.append(", executor='").append(StringUtil.truncValue(executor)).append('\'');
    sb.append(", total=").append(total);
    sb.append(", totalHid=").append(totalHid);
    sb.append(", memo='").append(StringUtil.truncValue(memo)).append('\'');
    sb.append(", editorId=").append(editorId == null ? "" : editorId);
    sb.append(", editor='").append(StringUtil.truncValue(editor)).append('\'');
    sb.append(", editDate=").append(editDate == null ? "" : editDate);
    sb.append(", editDateStr='").append(StringUtil.truncValue(editDateStr)).append('\'');
    sb.append(", reviewerId=").append(reviewerId == null ? "" : reviewerId);
    sb.append(", reviewer='").append(StringUtil.truncValue(reviewer)).append('\'');
    sb.append(", reviewDate=").append(reviewDate == null ? "" : reviewDate);
    sb.append(", invalidatorId=").append(invalidatorId == null ? "" : invalidatorId);
    sb.append(", invalidator='").append(StringUtil.truncValue(invalidator)).append('\'');
    sb.append(", invalidateDate=").append(invalidateDate == null ? "" : invalidateDate);
    if (itemDTOs != null) {
      for (SalesOrderItemDTO itemDTO : itemDTOs) {
        if(itemDTO!=null){
          sb.append(itemDTO.toString());
        }else {
          continue;
        }
      }
    }
    sb.append(", goodsSaler='").append(StringUtil.truncValue(goodsSaler)).append('\'');
    sb.append(", creationDate=").append(creationDate == null ? "" : creationDate);
    sb.append(", mobile='").append(StringUtil.truncValue(mobile)).append('\'');
    sb.append(", address='").append(StringUtil.truncValue(address)).append('\'');
    sb.append(", licenceNo='").append(StringUtil.truncValue(licenceNo)).append('\'');
    sb.append(", brand='").append(StringUtil.truncValue(brand)).append('\'');
    sb.append(", brandId=").append(brandId == null ? "" : brandId);
    sb.append(", model='").append(StringUtil.truncValue(model)).append('\'');
    sb.append(", modelId=").append(modelId == null ? "" : modelId);
    sb.append(", year='").append(StringUtil.truncValue(year)).append('\'');
    sb.append(", yearId=").append(yearId == null ? "" : yearId);
    sb.append(", engineId=").append(engineId == null ? "" : engineId);
    sb.append(", engine='").append(StringUtil.truncValue(engine)).append('\'');
    sb.append(", receivableId=").append(receivableId == null ? "" : receivableId);
    sb.append(", settledAmount=").append(settledAmount);
    sb.append(", settledAmountHid=").append(settledAmountHid);
    sb.append(", debt=").append(debt);
    sb.append(", accountMemberNo=").append(accountMemberNo);
    sb.append(", accountMemberId=").append(accountMemberId);
    sb.append(", accountMemberPassword=").append(accountMemberPassword);
    sb.append(", contact='").append(StringUtil.truncValue(contact)).append('\'');
    sb.append(", contactId=").append(contactId == null ? "" : contactId);
    sb.append(", contactIdStr='").append(StringUtil.truncValue(contactIdStr)).append('\'');
    sb.append(", otherTotalCostPrice=").append(otherTotalCostPrice == null ? "" : otherTotalCostPrice);
    sb.append('}');
    return sb.toString();
  }

	public void setExpressDTO(ExpressDTO expressDTO) {
		if(expressDTO == null){
			return;
		}
		this.setCompany(expressDTO.getCompany());
		this.setWaybills(expressDTO.getWaybills());
		this.setDispatchMemo(expressDTO.getMemo());
	}

	//缺料为true
	public boolean getIsShortage(){
		isShortage = false;
		if(ArrayUtils.isEmpty(getItemDTOs())){
			isShortage = true;
			return isShortage;
		}
		for(SalesOrderItemDTO salesOrderItemDTO :getItemDTOs()){
			if(NumberUtil.doubleVal(salesOrderItemDTO.getAmount())>NumberUtil.doubleVal(salesOrderItemDTO.getReserved())){
				isShortage = true;
				break;
			}
		}
		return isShortage;
	}

  public CustomerDTO generateCustomerDTO() {
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setId(getCustomerId());
    customerDTO.setName(getCustomer());
    customerDTO.setAddress(getAddress());
    customerDTO.setCompany(getCompany());
    customerDTO.setContact(getContact());
    customerDTO.setContactId(getContactId());
    customerDTO.setStatus(getCustomerStatus());
    customerDTO.setLandLine(getLandline());
    customerDTO.setMobile(getMobile());
    return customerDTO;
  }

  public void clearCustomerInfo() {
    setId(null);
    setCustomer(null);
    setAddress(null);
    setCompany(null);
    setContact(null);
    setContactId(null);
    setContactIdStr(null);
    setStatus(null);
    setLandline(null);
    setMobile(null);
  }

  public SalesOrderDTO clone() throws CloneNotSupportedException{
    return (SalesOrderDTO)super.clone();
  }

  public Set<String> getCategoryNames() {
    Set<String> categoryNames = new HashSet<String>();
    if(!ArrayUtils.isEmpty(getItemDTOs())) {
      for (SalesOrderItemDTO salesOrderItemDTO : this.getItemDTOs()) {
        if (salesOrderItemDTO == null || salesOrderItemDTO.getProductId() == null) {
          continue;
        }
        if (StringUtils.isNotBlank(salesOrderItemDTO.getBusinessCategoryName())) {
          categoryNames.add(salesOrderItemDTO.getBusinessCategoryName());
        }
      }
    }
    return categoryNames;
  }

  public Set<Long> getBusinessCategoryIds (){
    Set<Long> businessCategoryIds = new HashSet<Long>();
    if(!ArrayUtils.isEmpty(getItemDTOs())) {
      for (SalesOrderItemDTO salesOrderItemDTO : this.getItemDTOs()) {
        if (salesOrderItemDTO == null || salesOrderItemDTO.getBusinessCategoryId() == null) {
          continue;
        }
        businessCategoryIds.add(salesOrderItemDTO.getBusinessCategoryId());
      }
    }
       return businessCategoryIds;
  }

  public Set<String> getOtherIncomeNames() {
    Set<String> otherIncomeNames = new HashSet<String>();
    if (CollectionUtils.isNotEmpty(this.getOtherIncomeItemDTOList())) {
      for (SalesOrderOtherIncomeItemDTO salesOrderOtherIncomeItemDTO : this.getOtherIncomeItemDTOList()) {
        if (StringUtils.isNotBlank(salesOrderOtherIncomeItemDTO.getName())) {
          otherIncomeNames.add(salesOrderOtherIncomeItemDTO.getName());
        }
      }
    }
    return otherIncomeNames;
  }

  public void setPromotionsInfoJson(String promotionsInfoJson) {
    this.promotionsInfoJson = promotionsInfoJson;
    if(StringUtils.isNotBlank(promotionsInfoJson)){
      PromotionsInfoDTO infoDTO = (PromotionsInfoDTO)JsonUtil.jsonToObject(promotionsInfoJson, PromotionsInfoDTO.class);
      setPromotionsInfoDTO(infoDTO);
    }
  }

  public String getPromotionsInfoJson() {
    return promotionsInfoJson;
  }

  public PromotionsInfoDTO getPromotionsInfoDTO() {
    return promotionsInfoDTO;
  }

  public void setPromotionsInfoDTO(PromotionsInfoDTO promotionsInfoDTO) {
    this.promotionsInfoDTO = promotionsInfoDTO;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
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

  public Double getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

   public void calculateTotal() {
    double result = 0;
    double sales = 0;
    double otherIncome = 0;
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (SalesOrderItemDTO itemDTO : this.getItemDTOs()) {
        result += NumberUtil.doubleVal(itemDTO.getTotal());
        sales += NumberUtil.doubleVal(itemDTO.getTotal());
      }
    }
    if(CollectionUtils.isNotEmpty(getOtherIncomeItemDTOList())){
      for(SalesOrderOtherIncomeItemDTO orderOtherIncomeItemDTO : getOtherIncomeItemDTOList()){
		    result += NumberUtil.doubleVal(orderOtherIncomeItemDTO.getPrice());
        otherIncome += NumberUtil.doubleVal(orderOtherIncomeItemDTO.getPrice());
      }
    }
    this.setTotal(NumberUtil.toReserve(result, NumberUtil.PRECISION));
    this.setSalesTotal(NumberUtil.toReserve(sales, NumberUtil.PRECISION));
    this.setOtherIncomeTotal(NumberUtil.toReserve(otherIncome, NumberUtil.PRECISION));
  }

   public void setReceivableDTO(ReceivableDTO receivableDTO) {
    setSettledAmount(receivableDTO.getSettledAmount());
    setOrderDiscount(receivableDTO.getDiscount());
    setDebt(receivableDTO.getDebt());
    setBankAmount(receivableDTO.getBankCard());
    setBankCheckNo(StringUtil.valueOf(receivableDTO.getCheque()));
    setBankCheckAmount(receivableDTO.getCheque());
    setCashAmount(receivableDTO.getCash());
    setMemberAmount(receivableDTO.getMemberBalancePay());
  }

}