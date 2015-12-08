package com.bcgogo.txn.dto;

import com.bcgogo.enums.*;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.MoneyUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RfTxnConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-13
 * Time: 下午3:13
 * To change this template use File | Settings | File Templates.
 */
public class SalesReturnDTO extends BcgogoOrderDto implements Serializable {


  public SalesReturnDTO() {
  }

  private Long date;
  private String no;
  private String refNo;
  private Long salesOrderId;
  private String salesOrderNo;
  private Long repairOrderId;
  private String repairOrderNo;
  private Long deptId;
  private String dept;
  private Long customerId;
  private String customer;
  private String company;
  private String landline;
  private String memberNo;
  private String memberType;
  private MemberStatus memberStatus;
  private Long executorId;
  private String executor;
  private double total;
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long reviewerId;
  private String reviewer;
  private Long reviewDate;
  private Long creationDate;
  private Long invalidatorId;
  private String invalidator;
  private String invalidateDate;
  private SalesReturnItemDTO[] itemDTOs;
  private Long vestDate;
  private String vestDateStr;
  private OrderStatus status;
  private String purchaseReturnOrderMemo;
  private Long purchaseReturnOrderId;
  private PurchaseReturnDTO purchaseReturnDTO;
  private String receiptNo;
  private Long customerShopId;
  private String customerShopIdStr;
  private String contact;
  private Long contactId; // add by zhuj
  private String contactIdStr;
  private String mobile;
  private String address;
  private Double totalReturnAmount;
  private String print;
  private String refuseReason;

  //结算相关
  private double settledAmount;
  private double totalCostPrice;
  private double cashAmount;
  private double bankCheckAmount;
  private String bankCheckNo;
  private double bankAmount;
  private double customerDeposit; // add by zhuj

  private double strikeAmount;    //冲帐
  private double discountAmount;
  private String salesReturner;//退货人
  private Long salesReturnerId;//退货人id
  private InventoryLimitDTO inventoryLimitDTO;
  private String totalStr;
  private String returnType;
  private String draftOrderIdStr;
  private String editDateStr;

  private Long originOrderId;
  private OrderTypes originOrderType;
  private String originReceiptNo;
  private String customerStr;
  private Boolean readOnly;
  private Double accountDebtAmount;

  private String qq;
  private String email;
  private String customerIdStr;


  public String getCustomerShopIdStr() {
    return customerShopIdStr;
  }

  public void setCustomerShopIdStr(String customerShopIdStr) {
    this.customerShopIdStr = customerShopIdStr;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public double getCustomerDeposit() {
    return customerDeposit;
  }

  public void setCustomerDeposit(double customerDeposit) {
    this.customerDeposit = customerDeposit;
  }

  public Boolean getReadOnly() {
    return readOnly;
  }

  public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }

  public String getCustomerStr() {
    return customerStr;
  }

  public void setCustomerStr(String customerStr) {
    this.customerStr = customerStr;
  }

  public String getOriginReceiptNo() {
    return originReceiptNo;
  }

  public void setOriginReceiptNo(String originReceiptNo) {
    this.originReceiptNo = originReceiptNo;
  }

  public Long getOriginOrderId() {
    return originOrderId;
  }

  public void setOriginOrderId(Long originOrderId) {
    this.originOrderId = originOrderId;
  }

  public OrderTypes getOriginOrderType() {
    return originOrderType;
  }

  public void setOriginOrderType(OrderTypes originOrderType) {
    this.originOrderType = originOrderType;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public String getDraftOrderIdStr() {
    return draftOrderIdStr;
  }

  public void setDraftOrderIdStr(String draftOrderIdStr) {
    this.draftOrderIdStr = draftOrderIdStr;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public Long getSalesReturnerId() {
    return salesReturnerId;
  }

  public void setSalesReturnerId(Long salesReturnerId) {
    this.salesReturnerId = salesReturnerId;
  }

  public String getSalesReturner() {
    return salesReturner;
  }

  public void setSalesReturner(String salesReturner) {
    this.salesReturner = salesReturner;
  }

  public String getRefuseReason() {
    return refuseReason;
  }

  public void setRefuseReason(String refuseReason) {
    this.refuseReason = refuseReason;
  }

  public PurchaseReturnDTO getPurchaseReturnDTO() {
    return purchaseReturnDTO;
  }

  public void setPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO) {
    this.purchaseReturnDTO = purchaseReturnDTO;
  }

  public String getPrint() {
    return print;
  }

  public void setPrint(String print) {
    this.print = print;
  }

  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
    if(customerShopId!=null) this.customerShopIdStr=customerShopId.toString();
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
    if(contactId!=null){
      this.contactIdStr = contactId.toString();
    }
  }

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public String getPurchaseReturnOrderMemo() {
    return purchaseReturnOrderMemo;
  }

  public void setPurchaseReturnOrderMemo(String purchaseReturnOrderMemo) {
    this.purchaseReturnOrderMemo = purchaseReturnOrderMemo;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public SalesReturnItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(SalesReturnItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
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

  public Long getSalesOrderId() {
    return salesOrderId;
  }

  public void setSalesOrderId(Long salesOrderId) {
    this.salesOrderId = salesOrderId;
  }

  public String getSalesOrderNo() {
    return salesOrderNo;
  }

  public void setSalesOrderNo(String salesOrderNo) {
    this.salesOrderNo = salesOrderNo;
  }

  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  public String getRepairOrderNo() {
    return repairOrderNo;
  }

  public void setRepairOrderNo(String repairOrderNo) {
    this.repairOrderNo = repairOrderNo;
  }

  public Long getDeptId() {
    return deptId;
  }

  public void setDeptId(Long deptId) {
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

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
    if(total >= 0){
      this.totalStr = MoneyUtil.toBigType(String.valueOf(total));
    }else{
      this.totalStr = "负" + MoneyUtil.toBigType(String.valueOf(0 -total));
    }
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

  public String getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(String invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate){
    if(vestDate!=null){
      this.vestDateStr= DateUtil.convertDateLongToDateString("yyyy-MM-dd", vestDate);
    }
    this.vestDate =vestDate;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getPurchaseReturnOrderId() {
    return purchaseReturnOrderId;
  }

  public void setPurchaseReturnOrderId(Long purchaseReturnOrderId) {
    this.purchaseReturnOrderId = purchaseReturnOrderId;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    if (customerDTO == null) {
      return;
    }
    this.setCustomerId(customerDTO.getId());
    this.setCustomerShopId(customerDTO.getCustomerShopId());
    this.setCustomer(customerDTO.getName());
    this.setContact(customerDTO.getContact());
    this.setMobile(customerDTO.getMobile());
    this.setLandline(customerDTO.getLandLine());
    this.setCompany(customerDTO.getCompany());
    this.setAddress(customerDTO.getAddress());

    boolean isHaveSameContact = false;
      if (this.getContactId() != null && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
         for(ContactDTO contactDTO : customerDTO.getContacts()){
           if (contactDTO != null && getContactId().equals(contactDTO.getId())) {
             isHaveSameContact = true ;
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
      if (!isHaveSameContact &&!ArrayUtils.isEmpty(customerDTO.getContacts())) {
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

  public Double getTotalReturnAmount() {
    return totalReturnAmount;
  }

  public void setTotalReturnAmount(Double totalReturnAmount) {
    this.totalReturnAmount = totalReturnAmount;
  }

  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  public double getCashAmount() {
    return cashAmount;
  }

  public void setCashAmount(double cashAmount) {
    this.cashAmount = cashAmount;
  }

  public double getBankCheckAmount() {
    return bankCheckAmount;
  }

  public void setBankCheckAmount(double bankCheckAmount) {
    this.bankCheckAmount = bankCheckAmount;
  }

  public String getBankCheckNo() {
    return bankCheckNo;
  }

  public void setBankCheckNo(String bankCheckNo) {
    this.bankCheckNo = bankCheckNo;
  }

  public double getBankAmount() {
    return bankAmount;
  }

  public void setBankAmount(double bankAmount) {
    this.bankAmount = bankAmount;
  }

  public double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public InventoryLimitDTO getInventoryLimitDTO() {
    return inventoryLimitDTO;
  }

  public void setInventoryLimitDTO(InventoryLimitDTO inventoryLimitDTO) {
    this.inventoryLimitDTO = inventoryLimitDTO;
  }

  public double getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(double discountAmount) {
    this.discountAmount = discountAmount;
  }

  public String getTotalStr() {
    return totalStr;
  }

  public void setTotalStr(String totalStr) {
    this.totalStr = totalStr;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
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

  public Double getAccountDebtAmount() {
    return accountDebtAmount;
  }

  public void setAccountDebtAmount(Double accountDebtAmount) {
    this.accountDebtAmount = accountDebtAmount;
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

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.SALE_RETURN);
    orderIndexDTO.setCreationDate(this.getCreationDate() == null ? System.currentTimeMillis() : this.getCreationDate());
    //退货单状态
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setCreationDate(this.getCreationDate());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setCustomerOrSupplierId(this.getCustomerId());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setCustomerOrSupplierName(this.getCustomer());
    orderIndexDTO.setContactNum(this.getMobile());
    orderIndexDTO.setAddress(this.getAddress());
    orderIndexDTO.setContact(this.getContact());
    /*orderIndexDTO.setOrderTotalCostPrice(this.getTotalCostPrice());*/
    //退货结算方式
    List<PayMethod> payMethods = new ArrayList<PayMethod>();
    if (NumberUtil.doubleVal(this.getCashAmount()) > 0) { //现金
      payMethods.add(PayMethod.CASH);
    }
    if (NumberUtil.doubleVal(this.getBankAmount()) > 0) {
      payMethods.add(PayMethod.BANK_CARD);
    }
    if(NumberUtil.doubleVal(this.getBankCheckAmount())>0){
      payMethods.add(PayMethod.CHEQUE);
    }
    if(NumberUtil.doubleVal(this.getCustomerDeposit())>0){    // add by zhuj
      payMethods.add(PayMethod.CUSTOMER_DEPOSIT);
    }
    if(this.getStatementAccountOrderId() != null){//对账支付
      payMethods.add(PayMethod.STATEMENT_ACCOUNT);
    }
    orderIndexDTO.setPayMethods(payMethods);
    orderIndexDTO.setStrikeAmount(this.getStrikeAmount());
    orderIndexDTO.setDiscount(this.getDiscountAmount());
    orderIndexDTO.setOrderDebt(this.getAccountDebtAmount());
    orderIndexDTO.setOrderSettled(getSettledAmount());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    orderIndexDTO.setArrears(this.getAccountDebtAmount());
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    StringBuffer str = new StringBuffer();
    if (this.getItemDTOs() != null && this.getItemDTOs().length > 0) {
      str.append("退货商品:");
      for (SalesReturnItemDTO itemDTO : this.getItemDTOs()) {
        if (itemDTO == null) continue;
        //添加每个单据的产品信息
        itemIndexDTOList.add(itemDTO.toItemIndexDTO(this));
         itemDTO.setItemCostPrice(NumberUtil.doubleVal(itemDTO.getInventoryAveragePrice())*NumberUtil.doubleVal(itemDTO.getAmount()));
        inOutRecordDTOList.addAll(itemDTO.toInOutRecordDTO(this));

        str.append("(品名:").append(itemDTO.getProductName());
        if (!StringUtils.isEmpty(itemDTO.getBrand())) {
          str.append(",品牌:").append(itemDTO.getBrand()).append(",单价:").append(itemDTO.getPrice())
              .append("数量:").append(itemDTO.getAmount()).append(");");
        } else {
          str.append("单价:").append(itemDTO.getPrice()).append("数量:").append(itemDTO.getAmount())
              .append(");");
        }
      }
    }

    String orderContent = "";
    if (!"".equals(str.toString())) {
      orderContent = str.substring(0, str.length() - 1);
    }

    if (orderContent.length() > 450) {
      orderContent = orderContent.substring(0, 450);
      orderContent = orderContent + "等";
    }
    orderIndexDTO.setOrderContent(orderContent);
    orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
    orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    orderIndexDTO.setCustomerOrSupplierShopId(this.getCustomerShopId());
    return orderIndexDTO;
  }

  @Override
  public String toString() {
    return "SalesReturnDTO{" +
        "id=" + getId() +
        ", shopId=" + getShopId() +
        ", userId=" + getUserId() +
        ", date=" + date +
        ", no='" + no + '\'' +
        ", refNo='" + refNo + '\'' +
        ", salesOrderId=" + salesOrderId +
        ", salesOrderNo='" + salesOrderNo + '\'' +
        ", repairOrderId=" + repairOrderId +
        ", repairOrderNo='" + repairOrderNo + '\'' +
        ", deptId=" + deptId +
        ", dept='" + dept + '\'' +
        ", customerId=" + customerId +
        ", customer='" + customer + '\'' +
        ", executorId=" + executorId +
        ", executor='" + executor + '\'' +
        ", total=" + total +
        ", memo='" + memo + '\'' +
        ", editorId=" + editorId +
        ", editor='" + editor + '\'' +
        ", editDate=" + editDate +
        ", reviewerId=" + reviewerId +
        ", reviewer='" + reviewer + '\'' +
        ", reviewDate=" + reviewDate +
        ", creationDate=" + creationDate +
        ", invalidatorId=" + invalidatorId +
        ", invalidator='" + invalidator + '\'' +
        ", invalidateDate='" + invalidateDate + '\'' +
        ", itemDTOs=" + (itemDTOs == null ? null : Arrays.asList(itemDTOs)) +
        ", vestDate=" + vestDate +
        ", vestDateStr='" + vestDateStr + '\'' +
        ", status=" + status +
        ", purchaseReturnOrderMemo='" + purchaseReturnOrderMemo + '\'' +
        ", purchaseReturnOrderId=" + purchaseReturnOrderId +
        ", purchaseReturnDTO=" + purchaseReturnDTO +
        ", receiptNo='" + receiptNo + '\'' +
        ", customerShopId=" + customerShopId +
        ", contact='" + contact + '\'' +
        ", contactId='" + contactId + '\'' +
        ", contactIdStr='" + contactIdStr + '\'' +
        ", mobile='" + mobile + '\'' +
        ", address='" + address + '\'' +
        ", totalReturnAmount=" + totalReturnAmount +
        ", print='" + print + '\'' +
        ", refuseReason='" + refuseReason + '\'' +
        ", settledAmount=" + settledAmount +
        ", totalCostPrice=" + totalCostPrice +
        ", cashAmount=" + cashAmount +
        ", bankCheckAmount=" + bankCheckAmount +
        ", bankCheckNo='" + bankCheckNo + '\'' +
        ", bankAmount=" + bankAmount +
        ", strikeAmount=" + strikeAmount +
        ", discountAmount=" + discountAmount +
        ", inventoryLimitDTO=" + inventoryLimitDTO +
        ",salesReturner:" + salesReturner +
        ",salesReturnerId:" + salesReturnerId +
        '}';
  }

  public void setReceivableDTO(ReceivableDTO receivableDTO){
    setCashAmount(Math.abs(receivableDTO.getCash()));
    setBankAmount(Math.abs(receivableDTO.getBankCard()));
    setBankCheckNo(receivableDTO.getBankCheckNo());
    setBankCheckAmount(Math.abs(receivableDTO.getCheque()));
    setCustomerDeposit(Math.abs(receivableDTO.getDeposit()));
    setDiscountAmount(Math.abs(receivableDTO.getDiscount()));
    setAccountDebtAmount(Math.abs(receivableDTO.getDebt()));
    setSettledAmount(Math.abs(receivableDTO.getSettledAmount()));
  }
}
