package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopUnitDTO;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayMethod;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.MoneyUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RfTxnConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PurchaseReturnDTO extends BcgogoOrderDto implements Serializable,Cloneable{
  private Long date;
  private String no;
  private String refNo;
  private Long purchaseOrderId;
  private String purchaseOrderIdStr;
  private String purchaseOrderNo;
  private Long purchaseInventoryId;
  private Integer deptId;
  private String dept;
  private Long supplierId;
  private String supplierIdStr;
  private Long supplierShopId;
  private String supplierShopIdStr; // add by zhuj
  private String supplier;
  private Long executorId;
  private String executor;
  private double total;
  private OrderStatus status;
  private String memo;
  private Long editorId;
  private String editDateStr;
  private String editor;
  private Long editDate;
  private Long reviewerId;
  private String reviewer;
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;
  private String invalidateDate;
  private String contact;
  private Long contactId; // add by zhuj
  private String contactIdStr;
  private String mobile;
  private String address;
  private String bank;
  private String account;
  private String businessScope;
  private String accountName;
  private Long category;
  private String abbr;
  private Long settlementType;
  private String landline;
  private String fax;
  private String qq;
  private Long invoiceCategory;
  private String email;
  private PurchaseReturnItemDTO[] itemDTOs;
  private String shopName;
  private String shopAddress;
  private String shopLandLine;
  private String totalStr;
  private Long vestDate;   //归属时间
  private String vestDateStr;
  private InventoryLimitDTO inventoryLimitDTO;
  private Long creationDate;
  private Double totalReturnAmount;
  private String returnPayableType;
  private String draftOrderIdStr;
  private String receiptNo;
  private String print;

  private String refuseReason;
  //现金
  private Double cash;
  //转定金
  private Double depositAmount;
  //银联
  private Double bankAmount;

  //支票
  private Double bankCheckAmount;
  //支票号
  private String bankCheckNo;
  //冲账
  private Double strikeAmount;

  private String payee;

  private Double accountDiscount;   //优惠
  private Double accountDebtAmount;//欠款
  private Double settledAmount;//实收

  private String saleReturnReceiptNo;
  private Long province;
  private Long city;
  private Long region;

  private Long originOrderId;
  private String originOrderIdStr;
  private String originReceiptNo;
  private Boolean readOnly;
  private String huankuanTime;

  public String getSupplierShopIdStr() {
    return supplierShopIdStr;
  }

  public void setSupplierShopIdStr(String supplierShopIdStr) {
    this.supplierShopIdStr = supplierShopIdStr;
  }

  public String getHuankuanTime() {
    return huankuanTime;
  }

  public void setHuankuanTime(String huankuanTime) {
    this.huankuanTime = huankuanTime;
  }

  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  public Long getCity() {

    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  public Long getProvince() {

    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  public String getRefuseReason() {
    return refuseReason;
  }

  public void setRefuseReason(String refuseReason) {
    this.refuseReason = refuseReason;
  }

  public String getDraftOrderIdStr() {
    return draftOrderIdStr;
  }

  public void setDraftOrderIdStr(String draftOrderIdStr) {
    this.draftOrderIdStr = draftOrderIdStr;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    if(StringUtils.isBlank(vestDateStr)){
      this.vestDateStr=DateUtil.convertDateLongToDateString("yyyy-MM-dd",vestDate);
    }
    this.vestDate = vestDate;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getReturnPayableType() {
    return returnPayableType;
  }

  public void setReturnPayableType(String returnPayableType) {
    this.returnPayableType = returnPayableType;
  }

  public Double getTotalReturnAmount() {
    return totalReturnAmount;
  }

  public void setTotalReturnAmount(Double totalReturnAmount) {
    this.totalReturnAmount = totalReturnAmount;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public ShopUnitDTO[] getShopUnits() {
    return shopUnits;
  }

  public void setShopUnits(ShopUnitDTO[] shopUnits) {
    this.shopUnits = shopUnits;
  }

  private ShopUnitDTO[] shopUnits;    //单据页面用到的单位

  public String getTotalStr() {
    return MoneyUtil.toBigType(String.valueOf(total));
  }

  public void setTotalStr(String totalStr) {
    this.totalStr = totalStr;
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

  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
  }

  public String getPurchaseOrderNo() {
    return purchaseOrderNo;
  }

  public void setPurchaseOrderNo(String purchaseOrderNo) {
    this.purchaseOrderNo = purchaseOrderNo;
  }

  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
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

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
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
    this.totalStr= MoneyUtil.toBigType(String.valueOf(total));
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
    this.setPtOrderStatus(status);
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

  public PurchaseReturnItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(PurchaseReturnItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
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

  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public Long getCategory() {
    return category;
  }

  public void setCategory(Long category) {
    this.category = category;
  }

  public String getAbbr() {
    return abbr;
  }

  public void setAbbr(String abbr) {
    this.abbr = abbr;
  }

  public Long getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(Long settlementType) {
    this.settlementType = settlementType;
  }

  public String getLandline() {
    return landline;
  }

  public void setLandline(String landline) {
    this.landline = landline;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public Long getInvoiceCategory() {
    return invoiceCategory;
  }

  public void setInvoiceCategory(Long invoiceCategory) {
    this.invoiceCategory = invoiceCategory;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public InventoryLimitDTO getInventoryLimitDTO() {
    return inventoryLimitDTO;
  }

  public void setInventoryLimitDTO(InventoryLimitDTO inventoryLimitDTO) {
    this.inventoryLimitDTO = inventoryLimitDTO;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getPrint() {
    return print;
  }

  public void setPrint(String print) {
    this.print = print;
  }

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public Double getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(Double depositAmount) {
    this.depositAmount = depositAmount;
  }

  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
    if(supplierShopId!=null) this.supplierShopIdStr = supplierShopId.toString();
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public Double getAccountDiscount() {
    return accountDiscount;
  }

  public void setAccountDiscount(Double accountDiscount) {
    this.accountDiscount = accountDiscount;
  }

  public Double getAccountDebtAmount() {
    return accountDebtAmount;
  }

  public void setAccountDebtAmount(Double accountDebtAmount) {
    this.accountDebtAmount = accountDebtAmount;
  }

  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public Double getBankAmount() {
    return bankAmount;
  }

  public void setBankAmount(Double bankAmount) {
    this.bankAmount = bankAmount;
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

  public String getSaleReturnReceiptNo() {
    return saleReturnReceiptNo;
  }

  public void setSaleReturnReceiptNo(String saleReturnReceiptNo) {
    this.saleReturnReceiptNo = saleReturnReceiptNo;
  }

  public Long getOriginOrderId() {
    return originOrderId;
  }

  public void setOriginOrderId(Long originOrderId) {
    this.originOrderId = originOrderId;
    this.setOriginOrderIdStr(com.bcgogo.utils.StringUtil.valueOf(originOrderId));
  }

  public String getOriginOrderIdStr() {
    return originOrderIdStr;
  }

  public void setOriginOrderIdStr(String originOrderIdStr) {
    this.originOrderIdStr = originOrderIdStr;
  }

  public String getOriginReceiptNo() {
    return originReceiptNo;
  }

  public void setOriginReceiptNo(String originReceiptNo) {
    this.originReceiptNo = originReceiptNo;
  }

  public Boolean getReadOnly() {
    return readOnly;
  }

  public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }

  public void setSupplierDTO(SupplierDTO supplierDTO) {
    if (supplierDTO == null) {
      return;
    }
    this.setSupplierId(supplierDTO.getId());
    this.setSupplierShopId(supplierDTO.getSupplierShopId());
    this.setSupplier(supplierDTO.getName());
    this.setMobile(supplierDTO.getMobile());
    this.setAddress(supplierDTO.getAddress());
    this.setAccount(supplierDTO.getAccount());
    this.setBank(supplierDTO.getBank());
    this.setAccountName(supplierDTO.getAccountName());
    this.setCategory(supplierDTO.getCategory());
    this.setAbbr(supplierDTO.getAbbr());
    this.setLandline(supplierDTO.getLandLine());
    this.setFax(supplierDTO.getFax());
    this.setQq(supplierDTO.getQq());
    this.setEmail(supplierDTO.getEmail());
    this.setInvoiceCategory(supplierDTO.getInvoiceCategoryId());
    this.setSettlementType(supplierDTO.getSettlementTypeId());
    this.setProvince(supplierDTO.getProvince());
    this.setCity(supplierDTO.getCity());
    this.setRegion(supplierDTO.getRegion());

    boolean isHaveSameContact = false;
    if (this.getContactId() != null && !ArrayUtils.isEmpty(supplierDTO.getContacts())) {
       for(ContactDTO contactDTO : supplierDTO.getContacts()){
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
    if (!isHaveSameContact &&!ArrayUtils.isEmpty(supplierDTO.getContacts())) {
      ContactDTO contactDTO = supplierDTO.getContacts()[0];
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

  public void setShopDTO(ShopDTO shopDTO) {
    if (shopDTO == null) {
      return;
    }
    this.setShopName(shopDTO.getName());
    this.setShopAddress(shopDTO.getAddress());
    this.setShopLandLine(shopDTO.getLandline());

  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.RETURN);
    orderIndexDTO.setCreationDate(this.getCreationDate() == null ? System.currentTimeMillis() : this.getCreationDate());
    //退货单状态
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setCreationDate(this.getCreationDate());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setCustomerOrSupplierId(this.getSupplierId());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setCustomerOrSupplierName(this.getSupplier());
    orderIndexDTO.setContactNum(this.getMobile());
    orderIndexDTO.setAddress(this.getAddress());
    orderIndexDTO.setContact(this.getContact());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    orderIndexDTO.setArrears(this.getAccountDebtAmount());
    orderIndexDTO.setDiscount(this.getAccountDiscount());
    orderIndexDTO.setOrderTotalCostPrice(this.getTotalReturnAmount());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    //退货结算方式
    List<PayMethod> payMethods = new ArrayList<PayMethod>();
    if (NumberUtil.doubleVal(this.getCash()) > 0) { //现金
      payMethods.add(PayMethod.CASH);
    }

    if (NumberUtil.doubleVal(this.getBankAmount()) > 0) {   // 银联
      payMethods.add(PayMethod.BANK_CARD);
    }

    if (NumberUtil.doubleVal(this.getBankCheckAmount()) > 0) {   //支票
      payMethods.add(PayMethod.CHEQUE);
    }


    if (NumberUtil.doubleVal(this.getDepositAmount()) > 0) {   //定金
      payMethods.add(PayMethod.DEPOSIT);
    }

    if(this.getStatementAccountOrderId() != null){//对账支付
      payMethods.add(PayMethod.STATEMENT_ACCOUNT);
    }

    orderIndexDTO.setPayMethods(payMethods);
    orderIndexDTO.setStrikeAmount(null==this.getStrikeAmount()?0D:this.getStrikeAmount());

    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    StringBuffer str = new StringBuffer();
    if (this.getItemDTOs() != null && this.getItemDTOs().length > 0) {
      str.append("退货商品:");
      for (PurchaseReturnItemDTO itemDTO : this.getItemDTOs()) {
        if (itemDTO == null) continue;
        //添加每个单据的产品信息
        itemDTO.setItemCostPrice(NumberUtil.doubleVal(itemDTO.getInventoryAveragePrice())*itemDTO.getAmount());
        itemIndexDTOList.add(itemDTO.toItemIndexDTO(this));
        inOutRecordDTOList.add(itemDTO.toInOutRecordDTO(this));
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

    orderIndexDTO.setOrderDebt(this.getAccountDebtAmount());
    orderIndexDTO.setOrderSettled(this.getSettledAmount());
    orderIndexDTO.setDiscount(this.getAccountDiscount());
    List<Long> customerOrSupplierSreaIdList = new ArrayList<Long>();
    if(this.getProvince() != null) {
      customerOrSupplierSreaIdList.add(this.getProvince());
    }
    if(this.getCity() != null) {
      customerOrSupplierSreaIdList.add(this.getCity());
    }
    if(this.getRegion() != null) {
      customerOrSupplierSreaIdList.add(this.getRegion());
    }
    orderIndexDTO.setCustomerOrSupplierAreaIdList(customerOrSupplierSreaIdList);
    orderIndexDTO.setCustomerOrSupplierShopId(this.getSupplierShopId());   // add by zhuj

    return orderIndexDTO;
  }

  @Override
  public String toString() {
    return "PurchaseReturnDTO{" +
        "date=" + date +
        ", no='" + no + '\'' +
        ", refNo='" + refNo + '\'' +
        ", purchaseOrderId=" + purchaseOrderId +
        ", purchaseOrderNo='" + purchaseOrderNo + '\'' +
        ", purchaseInventoryId=" + purchaseInventoryId +
        ", deptId=" + deptId +
        ", dept='" + dept + '\'' +
        ", supplierId=" + supplierId +
        ", supplier='" + supplier + '\'' +
        ", executorId=" + executorId +
        ", executor='" + executor + '\'' +
        ", total=" + total +
        ", status=" + status +
        ", memo='" + memo + '\'' +
        ", editorId=" + editorId +
        ", editDateStr='" + editDateStr + '\'' +
        ", editor='" + editor + '\'' +
        ", editDate=" + editDate +
        ", reviewerId=" + reviewerId +
        ", reviewer='" + reviewer + '\'' +
        ", reviewDate=" + reviewDate +
        ", invalidatorId=" + invalidatorId +
        ", invalidator='" + invalidator + '\'' +
        ", invalidateDate='" + invalidateDate + '\'' +
        ", contact='" + contact + '\'' +
        ", contactId='" + contactId + '\'' +
        ", contactIdStr='" + contactIdStr + '\'' +
        ", mobile='" + mobile + '\'' +
        ", address='" + address + '\'' +
        ", bank='" + bank + '\'' +
        ", account='" + account + '\'' +
        ", businessScope='" + businessScope + '\'' +
        ", accountName='" + accountName + '\'' +
        ", category=" + category +
        ", abbr='" + abbr + '\'' +
        ", settlementType=" + settlementType +
        ", landline='" + landline + '\'' +
        ", fax='" + fax + '\'' +
        ", qq='" + qq + '\'' +
        ", invoiceCategory=" + invoiceCategory +
        ", email='" + email + '\'' +
        ", itemDTOs=" + (itemDTOs == null ? null : Arrays.asList(itemDTOs)) +
        ", shopName='" + shopName + '\'' +
        ", shopAddress='" + shopAddress + '\'' +
        ", shopLandLine='" + shopLandLine + '\'' +
        ", totalStr='" + totalStr + '\'' +
        ", vestDate=" + vestDate +
        ", vestDateStr='" + vestDateStr + '\'' +
        ", inventoryLimitDTO=" + inventoryLimitDTO +
        ", creationDate=" + creationDate +
        ", totalReturnAmount=" + totalReturnAmount +
        ", returnPayableType='" + returnPayableType + '\'' +
        ", draftOrderIdStr='" + draftOrderIdStr + '\'' +
        ", receiptNo='" + receiptNo + '\'' +
        ", print='" + print + '\'' +
        ", cash=" + cash +
        ", depositAmount=" + depositAmount +
        ", strikeAmount=" + strikeAmount +
        ", shopUnits=" + (shopUnits == null ? null : Arrays.asList(shopUnits)) +
        '}';
  }

  public SupplierDTO generateSupplierDTO() {
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO.setId(getSupplierId());
    supplierDTO.setSupplierShopId(getSupplierShopId());
    supplierDTO.setName(getSupplier());
    supplierDTO.setContact(getContact());
    supplierDTO.setContactId(getContactId());// add by zhuj
    supplierDTO.setContactIdStr(getContactIdStr());
    supplierDTO.setMobile(getMobile());
    supplierDTO.setAddress(getAddress());
    supplierDTO.setAccount(getAccount());
    supplierDTO.setBank(getBank());
    supplierDTO.setAccountName(getAccountName());
    supplierDTO.setCategory(getCategory());
    supplierDTO.setAbbr(getAbbr());
    supplierDTO.setLandLine(getLandline());
    supplierDTO.setFax(getFax());
    supplierDTO.setQq(getQq());
    supplierDTO.setEmail(getEmail());
    return supplierDTO;
  }

  public void clearSupplierDTO() {
    this.setSupplierId(null);
    this.setSupplierShopId(null);
    this.setSupplier(null);
    this.setContact(null);
    this.setContactId(null);
    this.setContactIdStr(null);
    this.setMobile(null);
    this.setAddress(null);
    this.setAccount(null);
    this.setBank(null);
    this.setAccountName(null);
    this.setCategory(null);
    this.setAbbr(null);
    this.setLandline(null);
    this.setFax(null);
    this.setQq(null);
    this.setBusinessScope(null);
    this.setEmail(null);
  }

  public PurchaseReturnDTO clone() throws CloneNotSupportedException{
    return (PurchaseReturnDTO)super.clone();
  }

  public PayableHistoryDTO toPayableHistoryDTO() {
    PayableHistoryDTO payableHistoryDTO = new PayableHistoryDTO();

    payableHistoryDTO.setShopId(getShopId());
    payableHistoryDTO.setDeduction(-NumberUtil.doubleVal(getAccountDiscount()));
    payableHistoryDTO.setCreditAmount(-NumberUtil.doubleVal(getAccountDebtAmount()));
    payableHistoryDTO.setCash(-NumberUtil.doubleVal(getCash()));
    payableHistoryDTO.setBankCardAmount(-NumberUtil.doubleVal(getBankAmount()));
    payableHistoryDTO.setCheckAmount(-NumberUtil.doubleVal(getBankCheckAmount()));
    payableHistoryDTO.setCheckNo(getBankCheckNo());
    payableHistoryDTO.setDepositAmount(-NumberUtil.doubleVal(getDepositAmount()));
    payableHistoryDTO.setActuallyPaid(-NumberUtil.doubleVal(getSettledAmount()));
    payableHistoryDTO.setSupplierId(getSupplierId());
    payableHistoryDTO.setStrikeAmount(-NumberUtil.doubleVal(getStrikeAmount()));
    payableHistoryDTO.setPayer(getUserName());
    payableHistoryDTO.setPayerId(getUserId());
    payableHistoryDTO.setPayTime(getVestDate());

    return payableHistoryDTO;
  }

  public void setPayableDTO(PayableDTO payableDTO) {
    setCash(Math.abs(payableDTO.getCash()));
    setBankAmount(Math.abs(payableDTO.getBankCard()));
    setAccountDiscount(Math.abs(payableDTO.getDeduction()));
    setStrikeAmount(Math.abs(payableDTO.getStrikeAmount()));
    setDepositAmount(Math.abs(payableDTO.getDeposit()));
    setBankCheckAmount(Math.abs(payableDTO.getCheque()));
  }

  public String getSupplierIdStr() {
    return supplierIdStr;
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
  }

  public String getPurchaseOrderIdStr() {
    return purchaseOrderIdStr;
  }

  public void setPurchaseOrderIdStr(String purchaseOrderIdStr) {
    this.purchaseOrderIdStr = purchaseOrderIdStr;
  }


}
