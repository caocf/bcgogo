package com.bcgogo.txn.dto;

import com.bcgogo.common.DataValidation;
import com.bcgogo.config.dto.ShopUnitDTO;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayMethod;
import com.bcgogo.product.dto.KindDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-9
 * Time: 下午3:34
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseInventoryDTO extends BcgogoOrderDto {
  public void parseVehicleBrand() {
//    if (itemDTOs != null) {
//      for (PurchaseInventoryItemDTO itemDTO : itemDTOs) {
//        if (StringUtils.isBlank(itemDTO.getVehicleBrand())
//            && StringUtils.isBlank(itemDTO.getVehicleModel())
//            && StringUtils.isBlank(itemDTO.getVehicleEngine())
//            && StringUtils.isBlank(itemDTO.getVehicleYear())) {
//          itemDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
//        } else {
//          itemDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
//        }
//      }
//    }
    if (itemDTOs != null) {
      for (PurchaseInventoryItemDTO itemDTO : itemDTOs) {
        if (itemDTO.getProductVehicleStatus() == null) {
          itemDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
        }
      }
    }
  }


  public PurchaseInventoryDTO() {
  }

  //  private Long id;
//  private Long shopId;
  private String shopName;
  private String shopAddress;
  private String shopLandLine;
  private Long date;
  private String no;
  private String refNo;
  private Long purchaseOrderId;
  private String purchaseOrderNo;
  private Long deptId;
  private String dept;
  private Long supplierId;
  private String supplier;
  private Long executorId;
  private String executor;
  private Double total;
  private Long deliveryDate;
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
  private String acceptor;
  private Long acceptorId;
  private PurchaseInventoryItemDTO[] itemDTOs;
  private List<PurchaseInventoryItemDTO> itemList;

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
  private String totalStr;
  private Long vestDate;     //归属时间
  private String vestDateStr;
  private String accountDateStr; //结算时间，区别于vestDate

  private String draftOrderIdStr;
  private String receiptNo;

  private Long supplierShopId;
  //入库后判断缺料待修逻辑与页面跳转逻辑使用
  private String repairOrderId = null;//从缺料入单过来的入库
  private String productAmount = null;//维修单材料使用量

  private OrderStatus status;//订单状态
  private InventoryLimitDTO inventoryLimitDTO;

  private ShopUnitDTO[] shopUnits;    //单据页面用到的单位

  /*付款历史信息*/
  /* 扣款 优惠金额*/
  private Double deduction;
  /*欠款挂账*/
  private Double creditAmount;
  /*现金*/
  private Double cash;
  /*银行卡*/
  private Double bankCardAmount;
  /*支票*/
  private Double checkAmount;
  /*支票号码*/
  private String checkNo;
  /*定金*/
  private Double depositAmount;
  /*实付*/
  private Double actuallyPaid;

  /*入库单界面*/
  /*实付*/
  private Double stroageActuallyPaid;
  /*挂账*/
  private Double stroageCreditAmount;
  /*优惠金额*/
  private Double stroageSupplierDeduction;

  private String paidtype;
  /*入库单作废后：供应商还款类型：（1）退现金 （2）退定金*/
  private String returnMoneyType;

  private Long creationDate;

  private String print;

  private Double strikeAmount;

  private String payer;

  private List<Long> returnProductIds = new ArrayList<Long>();//来料分配productIds

  private String supplierIdStr;

  private String promotionsInfoJson;

  private List<Long> notInSaleProductIds = new ArrayList<Long>();   //未上架商品Id

  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }


  //供应商评价
  private String qualityScoreStr ="0";
  //供应商关联的采购单 是否是在线的采购单
  private Long purchaseSupplierShopId;
  private String purchaseReceiptNo;//采购单单据号
  private String purchaseOrderIdStr;

  private Long province;
  private Long city;
  private Long region;

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

  public String getPurchaseOrderIdStr() {
    return purchaseOrderIdStr;
  }

  public void setPurchaseOrderIdStr(String purchaseOrderIdStr) {
    this.purchaseOrderIdStr = purchaseOrderIdStr;
  }

  public String getPurchaseReceiptNo() {
    return purchaseReceiptNo;
  }

  public void setPurchaseReceiptNo(String purchaseReceiptNo) {
    this.purchaseReceiptNo = purchaseReceiptNo;
  }

  public Long getPurchaseSupplierShopId() {
    return purchaseSupplierShopId;
  }

  public void setPurchaseSupplierShopId(Long purchaseSupplierShopId) {
    this.purchaseSupplierShopId = purchaseSupplierShopId;
  }

  public String getQualityScoreStr() {
    return qualityScoreStr;
  }

  public void setQualityScoreStr(String qualityScoreStr) {
    this.qualityScoreStr = qualityScoreStr;
  }

  public String getReturnMoneyType() {
    return returnMoneyType;
  }

  public void setReturnMoneyType(String returnMoneyType) {
    this.returnMoneyType = returnMoneyType;
  }

  public String getPaidtype() {
    return paidtype;
  }

  public void setPaidtype(String paidtype) {
    this.paidtype = paidtype;
  }

  public Double getStroageActuallyPaid() {
    return stroageActuallyPaid;
  }

  public void setStroageActuallyPaid(Double stroageActuallyPaid) {
    this.stroageActuallyPaid = stroageActuallyPaid;
  }

  public Double getStroageCreditAmount() {
    return stroageCreditAmount;
  }

  public void setStroageCreditAmount(Double stroageCreditAmount) {
    this.stroageCreditAmount = stroageCreditAmount;
  }

  public Double getStroageSupplierDeduction() {
    return stroageSupplierDeduction;
  }

  public void setStroageSupplierDeduction(Double stroageSupplierDeduction) {
    this.stroageSupplierDeduction = stroageSupplierDeduction;
  }

  public Double getDeduction() {
    return deduction;
  }

  public void setDeduction(Double deduction) {
    this.deduction = deduction;
  }

  public Double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = creditAmount;
  }

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public Double getBankCardAmount() {
    return bankCardAmount;
  }

  public void setBankCardAmount(Double bankCardAmount) {
    this.bankCardAmount = bankCardAmount;
  }

  public String getCheckNo() {
    return checkNo;
  }

  public void setCheckNo(String checkNo) {
    this.checkNo = checkNo;
  }

  public Double getCheckAmount() {
    return checkAmount;
  }

  public void setCheckAmount(Double checkAmount) {
    this.checkAmount = checkAmount;
  }

  public Double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(Double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  public Double getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(Double depositAmount) {
    this.depositAmount = depositAmount;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getLastModified() {
    return lastModified;
  }

  public void setLastModified(Long lastModified) {
    this.lastModified = lastModified;
  }

  public String getDraftOrderIdStr() {
    return draftOrderIdStr;
  }

  public void setDraftOrderIdStr(String draftOrderIdStr) {
    this.draftOrderIdStr = draftOrderIdStr;
  }

  private Long lastModified;

  public List<PurchaseInventoryItemDTO> getItemList() {
    return itemList;
  }

  public void setItemList(List<PurchaseInventoryItemDTO> itemList) {
    this.itemList = itemList;
  }

  /**
   * 判断入库完后的逻辑
   * <p/>
   * 1:跳转到指定的维修单
   * 2:不跳转，提示缺料信息
   * 3:出现来料待修提示页面
   */
  private String returnType = null;
  private String returnIndex = null;

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

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public String getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(String repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  public String getProductAmount() {
    return productAmount;
  }

  public void setProductAmount(String productAmount) {
    this.productAmount = productAmount;
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

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    if(StringUtils.isNotBlank(editDateStr)) {
      this.editDateStr = editDateStr;
    }
  }


//  public Long getId() {
//    return this.id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }
//
//  public Long getShopId() {
//    return shopId;
//  }
//
//  public void setShopId(Long shopId) {
//    this.shopId = shopId;
//  }

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

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
    this.supplierIdStr = null==this.supplierId?"":this.supplierId.toString();
  }

  public String getPromotionsInfoJson() {
    return promotionsInfoJson;
  }

  public void setPromotionsInfoJson(String promotionsInfoJson) {
    this.promotionsInfoJson = promotionsInfoJson;
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

  public Double getTotal() {
    if (total == null) {
      total = 0D;
    }
    return total;
  }

  public void setTotal(Double total) {
    if(total==null){
      total= 0D;
    }
    this.total = total;
    this.totalStr = MoneyUtil.toBigType(String.valueOf(total));
  }

  public String getTotalStr() {
    if(total==null){
      total= 0D;
    }
    return MoneyUtil.toBigType(String.valueOf(total));
  }

  public void setTotalStr(String totalStr) {
    this.totalStr = totalStr;
  }

  public Long getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Long deliveryDate) {
    this.deliveryDate = deliveryDate;
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

  public PurchaseInventoryItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(PurchaseInventoryItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
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

  public String getAcceptor() {
    return acceptor;
  }

  public void setAcceptor(String acceptor) {
    this.acceptor = acceptor;
  }

  public String getReturnIndex() {
    return returnIndex;
  }

  public void setReturnIndex(String returnIndex) {
    this.returnIndex = returnIndex;
  }

  public InventoryLimitDTO getInventoryLimitDTO() {
    return inventoryLimitDTO;
  }

  public void setInventoryLimitDTO(InventoryLimitDTO inventoryLimitDTO) {
    this.inventoryLimitDTO = inventoryLimitDTO;
  }

  public Long getVestDate() {
    return this.vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public String getVestDateStr() {
    return this.vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getAccountDateStr() {
    return accountDateStr;
  }

  public void setAccountDateStr(String accountDateStr) {
    this.accountDateStr = accountDateStr;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }


  public Long getAcceptorId() {
    return acceptorId;
  }

  public void setAcceptorId(Long acceptorId) {
    this.acceptorId = acceptorId;
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

  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public void setSupplierDTO(SupplierDTO supplierDTO) {
    if (supplierDTO == null) {
      return;
    }
    this.setSupplierId(supplierDTO.getId());
    this.setSupplier(supplierDTO.getName());
    this.setSupplier(supplierDTO.getName());
    this.setMobile(StringUtils.isBlank(supplierDTO.getMobile()) ? "" : supplierDTO.getMobile());
    this.setLandline(StringUtils.isBlank(supplierDTO.getLandLine()) ? "" : supplierDTO.getLandLine());
    this.setAddress(supplierDTO.getAddress());
    this.setInvoiceCategory(supplierDTO.getInvoiceCategoryId());
    this.setAccount(supplierDTO.getAccount());
    this.setBank(supplierDTO.getBank());
    this.setAccountName(supplierDTO.getAccountName());
    this.setCategory(supplierDTO.getCategory());
    this.setAbbr(supplierDTO.getAbbr());
    this.setSettlementType(supplierDTO.getSettlementTypeId());
    this.setLandline(supplierDTO.getLandLine());
    this.setFax(supplierDTO.getFax());
    this.setQq(supplierDTO.getQq());
    this.setInvoiceCategory(supplierDTO.getInvoiceCategoryId());
    this.setEmail(supplierDTO.getEmail());
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
          setContactIdStr(StringUtil.valueOf(contactDTO.getId()));
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

  public List<Long> getReturnProductIds() {
    return returnProductIds;
  }

  public void setReturnProductIds(List<Long> returnProductIds) {
    this.returnProductIds = returnProductIds;
  }

  public List<Long> getNotInSaleProductIds() {
    return notInSaleProductIds;
  }

  public void setNotInSaleProductIds(List<Long> notInSaleProductIds) {
    this.notInSaleProductIds = notInSaleProductIds;
  }

  public String getPayer() {
    return payer;
  }

  public void setPayer(String payer) {
    this.payer = payer;
  }

  public String getSupplierIdStr() {
    return supplierIdStr;
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("PurchaseInventoryDTO");
    sb.append("{id=").append(this.getId() == null ? "" : this.getId());
    sb.append(", shopId=").append(this.getId() == null ? "" : this.getShopId());
    sb.append(", shopName='").append(StringUtil.truncValue(shopName)).append('\'');
    sb.append(", shopAddress='").append(StringUtil.truncValue(shopAddress)).append('\'');
    sb.append(", shopLandLine='").append(StringUtil.truncValue(shopLandLine)).append('\'');
    sb.append(", date=").append(date == null ? "" : date);
    sb.append(", no='").append(StringUtil.truncValue(no)).append('\'');
    sb.append(", refNo='").append(StringUtil.truncValue(refNo)).append('\'');
    sb.append(", purchaseOrderId=").append(purchaseOrderId == null ? "" : purchaseOrderId);
    sb.append(", purchaseOrderNo='").append(StringUtil.truncValue(purchaseOrderNo)).append('\'');
    sb.append(", deptId=").append(deptId == null ? "" : deptId);
    sb.append(", dept='").append(StringUtil.truncValue(dept)).append('\'');
    sb.append(", supplierId=").append(supplierId == null ? "" : supplierId);
    sb.append(", supplier='").append(StringUtil.truncValue(supplier)).append('\'');
    sb.append(", executorId=").append(executorId == null ? "" : editorId);
    sb.append(", executor='").append(StringUtil.truncValue(executor)).append('\'');
    sb.append(", total=").append(total);
    sb.append(", deliveryDate=").append(deliveryDate == null ? "" : deliveryDate);
    sb.append(", memo='").append(StringUtil.truncValue(memo)).append('\'');
    sb.append(", editorId=").append(editorId == null ? "" : editorId);
    sb.append(", editor='").append(StringUtil.truncValue(editor)).append('\'');
    sb.append(", editDate=").append(editDate == null ? "" : editDate);
    sb.append(", editDateStr='").append(StringUtil.truncValue(editDateStr)).append('\'');
    sb.append(", reviewerId=").append(reviewerId);
    sb.append(", reviewer='").append(StringUtil.truncValue(reviewer)).append('\'');
    sb.append(", reviewDate=").append(reviewDate == null ? "" : reviewDate);
    sb.append(", invalidatorId=").append(invalidatorId == null ? "" : invalidatorId);
    sb.append(", invalidator='").append(StringUtil.truncValue(invalidator)).append('\'');
    sb.append(", invalidateDate=").append(invalidateDate == null ? "" : invalidateDate);
    sb.append(", acceptor='").append(StringUtil.truncValue(acceptor)).append('\'');
    if (ArrayUtil.isNotEmpty(itemDTOs)) {
      for (PurchaseInventoryItemDTO itemDTO : itemDTOs) {
        sb.append(itemDTO.toString());
      }
    }
    sb.append(", itemList=").append(itemList);
    sb.append(", contact='").append(StringUtil.truncValue(contact)).append('\'');
    sb.append(", contactId='").append(contactId).append('\'');
    sb.append(", contactIdStr='").append(StringUtil.truncValue(contactIdStr)).append('\'');
    sb.append(", mobile='").append(StringUtil.truncValue(mobile)).append('\'');
    sb.append(", address='").append(StringUtil.truncValue(address)).append('\'');
    sb.append(", bank='").append(StringUtil.truncValue(bank)).append('\'');
    sb.append(", account='").append(StringUtil.truncValue(account)).append('\'');
    sb.append(", businessScope='").append(StringUtil.truncValue(businessScope)).append('\'');
    sb.append(", accountName='").append(StringUtil.truncValue(accountName)).append('\'');
    sb.append(", category=").append(category == null ? "" : category);
    sb.append(", abbr='").append(StringUtil.truncValue(abbr)).append('\'');
    sb.append(", settlementType=").append(settlementType == null ? "" : settlementType);
    sb.append(", landline='").append(StringUtil.truncValue(landline)).append('\'');
    sb.append(", email='").append(StringUtil.truncValue(email)).append('\'');
    sb.append(", fax='").append(StringUtil.truncValue(fax)).append('\'');
    sb.append(", qq='").append(StringUtil.truncValue(qq)).append('\'');
    sb.append(", invoiceCategory=").append(invoiceCategory == null ? "" : invoiceCategory);
    sb.append(", repairOrderId='").append(StringUtil.truncValue(repairOrderId)).append('\'');
    sb.append(", productAmount='").append(StringUtil.truncValue(productAmount)).append('\'');
    sb.append(", lastModified=").append(lastModified == null ? "" : lastModified);
    sb.append(", returnType='").append(StringUtil.truncValue(returnType)).append('\'');
    sb.append(", returnIndex='").append(StringUtil.truncValue(returnIndex)).append('\'');
    sb.append('}');
    return sb.toString();
  }


//  @Override
//  public PurchaseInventoryDTO clone() throws CloneNotSupportedException {
//        PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
//    purchaseInventoryDTO.setShopName(this.shopName);
//    purchaseInventoryDTO.setShopAddress(this.shopAddress);
//     purchaseInventoryDTO.setShopLandLine(this.shopLandLine);
//    purchaseInventoryDTO.setDate(this.date);
//
//     purchaseInventoryDTO.setNo(this.no);
//    purchaseInventoryDTO.setRefNo(this.refNo);
//     purchaseInventoryDTO.setPurchaseOrderId(this.purchaseOrderId);
//    purchaseInventoryDTO.setPurchaseOrderNo(this.purchaseOrderNo);
//    purchaseInventoryDTO.setDeptId(this.deptId);
//    purchaseInventoryDTO.setDept(this.dept);
//    purchaseInventoryDTO.setSupplierId(this.supplierId);
//    purchaseInventoryDTO.setSupplier(this.supplier);
//   purchaseInventoryDTO.setExecutor(this.executor);
//    purchaseInventoryDTO.setExecutorId(this.executorId);
//
//    this.total = total;
//    this.deliveryDate = deliveryDate;
//    this.memo = memo;
//    this.editorId = editorId;
//    this.editor = editor;
//    this.editDate = editDate;
//    this.editDateStr = editDateStr;
//    this.reviewerId = reviewerId;
//    this.reviewer = reviewer;
//    this.reviewDate = reviewDate;
//    this.invalidatorId = invalidatorId;
//    this.invalidator = invalidator;
//    this.invalidateDate = invalidateDate;
//    this.acceptor = acceptor;
//    this.itemDTOs = itemDTOs;
//    this.itemList = itemList;
//    this.contact = contact;
//    this.mobile = mobile;
//    this.address = address;
//    this.bank = bank;
//    this.account = account;
//    this.businessScope = businessScope;
//    this.accountName = accountName;
//    this.category = category;
//    this.abbr = abbr;
//    this.settlementType = settlementType;
//    this.landline = landline;
//    this.fax = fax;
//    this.qq = qq;
//    this.invoiceCategory = invoiceCategory;
//    this.email = email;
//    this.totalStr = totalStr;
//    this.vestDate = vestDate;
//    this.vestDateStr = vestDateStr;
//    this.repairOrderId = repairOrderId;
//    this.productAmount = productAmount;
//    this.status = status;
//    this.inventoryLimitDTO = inventoryLimitDTO;
//    this.shopUnits = shopUnits;
//    this.creationDate = creationDate;
//    this.lastModified = lastModified;
//    this.returnType = returnType;
//    this.returnIndex = returnIndex;
//        this.shopName = shopName;
//    this.shopAddress = shopAddress;
//    this.shopLandLine = shopLandLine;
//    this.date = date;
//    this.no = no;
//    this.refNo = refNo;
//    this.purchaseOrderId = purchaseOrderId;
//    this.purchaseOrderNo = purchaseOrderNo;
//    this.deptId = deptId;
//    this.dept = dept;
//    this.supplierId = supplierId;
//    this.supplier = supplier;
//    this.executorId = executorId;
//    this.executor = executor;
//    this.total = total;
//    this.deliveryDate = deliveryDate;
//    this.memo = memo;
//    this.editorId = editorId;
//    this.editor = editor;
//    this.editDate = editDate;
//    this.editDateStr = editDateStr;
//    this.reviewerId = reviewerId;
//    this.reviewer = reviewer;
//    this.reviewDate = reviewDate;
//    this.invalidatorId = invalidatorId;
//    this.invalidator = invalidator;
//    this.invalidateDate = invalidateDate;
//    this.acceptor = acceptor;
//    this.itemDTOs = itemDTOs;
//    this.itemList = itemList;
//    this.contact = contact;
//    this.mobile = mobile;
//    this.address = address;
//    this.bank = bank;
//    this.account = account;
//    this.businessScope = businessScope;
//    this.accountName = accountName;
//    this.category = category;
//    this.abbr = abbr;
//    this.settlementType = settlementType;
//    this.landline = landline;
//    this.fax = fax;
//    this.qq = qq;
//    this.invoiceCategory = invoiceCategory;
//    this.email = email;
//    this.totalStr = totalStr;
//    this.vestDate = vestDate;
//    this.vestDateStr = vestDateStr;
//    this.repairOrderId = repairOrderId;
//    this.productAmount = productAmount;
//    this.status = status;
//    this.inventoryLimitDTO = inventoryLimitDTO;
//    this.shopUnits = shopUnits;
//    this.creationDate = creationDate;
//    this.lastModified = lastModified;
//    this.returnType = returnType;
//    this.returnIndex = returnIndex;
//    return super.clone();    //To change body of overridden methods use File | Settings | File Templates.
//  }

  public SupplierDTO generateSupplierDTO(){
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO.setName(getSupplier());
    supplierDTO.setContact(getContact());
    supplierDTO.setContactId(getContactId());
    if (DataValidation.mobileValidation(getMobile())) {
      supplierDTO.setMobile(getMobile());
    } else {
      supplierDTO.setMobile("");
    }
    supplierDTO.setLandLine(getLandline());
    supplierDTO.setAddress(getAddress());
    supplierDTO.setBank(getBank());
    supplierDTO.setAccount(getAccount());
    supplierDTO.setAccountName(getAccountName());

    supplierDTO.setCategory(StringUtil.nullToObject(getCategory()));
    supplierDTO.setAbbr(getAbbr());
    supplierDTO.setSettlementTypeId(getSettlementType());
    // supplierDTO.setLandLine(getLandline());
    supplierDTO.setFax(getFax());
    supplierDTO.setQq(getQq());
    supplierDTO.setInvoiceCategoryId(getInvoiceCategory());
    supplierDTO.setEmail(getEmail());
    if (getContactId() != null) {
      ContactDTO contactDTO = new ContactDTO();
      contactDTO.setId(getContactId());
      contactDTO.setShopId(getShopId());
      contactDTO.setName(getContact());
      contactDTO.setMobile(getMobile());
      ContactDTO[] contactDTOs = new ContactDTO[3];
      contactDTOs[0] = contactDTO;
      supplierDTO.setContacts(contactDTOs);
    }

    return supplierDTO;
  }

  @Override
  public PurchaseInventoryDTO clone() throws CloneNotSupportedException {
    PurchaseInventoryDTO purchaseInventoryDTO = new PurchaseInventoryDTO();
    purchaseInventoryDTO.shopName = shopName;
    purchaseInventoryDTO.shopAddress = shopAddress;
    purchaseInventoryDTO.shopLandLine = shopLandLine;
    purchaseInventoryDTO.date = date;
    purchaseInventoryDTO.no = no;
    purchaseInventoryDTO.refNo = refNo;
    purchaseInventoryDTO.purchaseOrderId = purchaseOrderId;
    purchaseInventoryDTO.purchaseOrderNo = purchaseOrderNo;
    purchaseInventoryDTO.deptId = deptId;
    purchaseInventoryDTO.dept = dept;
    purchaseInventoryDTO.supplierId = supplierId;
    purchaseInventoryDTO.supplier = supplier;
    purchaseInventoryDTO.executorId = executorId;
    purchaseInventoryDTO.executor = executor;
    purchaseInventoryDTO.total = total;
    purchaseInventoryDTO.deliveryDate = deliveryDate;
    purchaseInventoryDTO.memo = memo;
    purchaseInventoryDTO.editorId = editorId;
    purchaseInventoryDTO.editor = editor;
    purchaseInventoryDTO.editDate = editDate;
    purchaseInventoryDTO.editDateStr = editDateStr;
    purchaseInventoryDTO.reviewerId = reviewerId;
    purchaseInventoryDTO.reviewer = reviewer;
    purchaseInventoryDTO.reviewDate = reviewDate;
    purchaseInventoryDTO.invalidatorId = invalidatorId;
    purchaseInventoryDTO.invalidator = invalidator;
    purchaseInventoryDTO.invalidateDate = invalidateDate;
    purchaseInventoryDTO.acceptor = acceptor;
    purchaseInventoryDTO.itemDTOs = itemDTOs;
    purchaseInventoryDTO.itemList = itemList;
    purchaseInventoryDTO.contact = contact;
    purchaseInventoryDTO.contactId = contactId;
    purchaseInventoryDTO.contactIdStr = contactIdStr;
    purchaseInventoryDTO.mobile = mobile;
    purchaseInventoryDTO.address = address;
    purchaseInventoryDTO.bank = bank;
    purchaseInventoryDTO.account = account;
    purchaseInventoryDTO.accountName = accountName;
    purchaseInventoryDTO.category = category;
    purchaseInventoryDTO.abbr = abbr;
    purchaseInventoryDTO.settlementType = settlementType;
    purchaseInventoryDTO.landline = landline;
    purchaseInventoryDTO.fax = fax;
    purchaseInventoryDTO.qq = qq;
    purchaseInventoryDTO.invoiceCategory = invoiceCategory;
    purchaseInventoryDTO.email = email;
    purchaseInventoryDTO.totalStr = totalStr;
    purchaseInventoryDTO.vestDate = vestDate;
    purchaseInventoryDTO.vestDateStr = vestDateStr;
    purchaseInventoryDTO.repairOrderId = repairOrderId;
    purchaseInventoryDTO.productAmount = productAmount;
    purchaseInventoryDTO.status = status;
    purchaseInventoryDTO.inventoryLimitDTO = inventoryLimitDTO;
    purchaseInventoryDTO.shopUnits = shopUnits;
    purchaseInventoryDTO.creationDate = creationDate;
    purchaseInventoryDTO.lastModified = lastModified;
    purchaseInventoryDTO.returnType = returnType;
    purchaseInventoryDTO.returnIndex = returnIndex;

    /*付款历史信息*/
    /* 扣款*/
    purchaseInventoryDTO.deduction = deduction;
    /*欠款挂账*/
    purchaseInventoryDTO.creditAmount = creditAmount;
    /*现金*/
    purchaseInventoryDTO.cash = cash;
    /*银行卡*/
    purchaseInventoryDTO.bankCardAmount = bankCardAmount;
    /*支票*/
    purchaseInventoryDTO.checkAmount = checkAmount;
    /*支票号码*/
    purchaseInventoryDTO.checkNo = checkNo;
    /*定金*/
    purchaseInventoryDTO.depositAmount = depositAmount;
    /*实付*/
    purchaseInventoryDTO.actuallyPaid = actuallyPaid;
    /*入库单界面*/
    /*实付*/
    purchaseInventoryDTO.stroageActuallyPaid = stroageActuallyPaid;
    /*挂账*/
    purchaseInventoryDTO.stroageCreditAmount = stroageCreditAmount;
    /*供应商入库作废后：退款（1）退现金 （2）退定金*/
    purchaseInventoryDTO.returnMoneyType=returnMoneyType;
    purchaseInventoryDTO.stroageSupplierDeduction = stroageSupplierDeduction;
    purchaseInventoryDTO.paidtype=paidtype;
    purchaseInventoryDTO.setId(this.getId());
    purchaseInventoryDTO.setShopId(this.getShopId());
    purchaseInventoryDTO.setStorehouseId(this.getStorehouseId());
    return purchaseInventoryDTO;
  }

  public OrderIndexDTO toOrderIndexDTO(){
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.INVENTORY);
    orderIndexDTO.setMemo(this.getMemo());
    //入库单状态
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setCreationDate(this.getCreationDate());
    orderIndexDTO.setCustomerOrSupplierId(this.getSupplierId());
    orderIndexDTO.setCustomerOrSupplierName(this.getSupplier());
    orderIndexDTO.setContactNum(this.getMobile());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setAddress(this.getAddress());
    orderIndexDTO.setContact(this.getContact());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    //comment zjt:如果结算改了 这边需要修改
    List<PayMethod> payMethods = new ArrayList<PayMethod>();
    if (NumberUtil.doubleVal(this.getCash()) > 0) { //现金
      payMethods.add(PayMethod.CASH);
    }
    if (NumberUtil.doubleVal(this.getBankCardAmount()) > 0) { //银行卡
      payMethods.add(PayMethod.BANK_CARD);
    }
    if (NumberUtil.doubleVal(this.getCheckAmount()) > 0) {// 支票
      payMethods.add(PayMethod.CHEQUE);
    }
    if (NumberUtil.doubleVal(this.getDepositAmount()) > 0) {   //定金
      payMethods.add(PayMethod.DEPOSIT);
    }
    if (this.getStatementAccountOrderId() != null) {//对账支付
      payMethods.add(PayMethod.STATEMENT_ACCOUNT);
    }
    orderIndexDTO.setPayMethods(payMethods);
    //欠款
    orderIndexDTO.setArrears(this.getCreditAmount());
    orderIndexDTO.setOrderDebt(this.getCreditAmount());
    orderIndexDTO.setOrderSettled(this.getActuallyPaid());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setDiscount(this.getDeduction());

    StringBuffer str = new StringBuffer();
    if (this.getItemDTOs() != null && this.getItemDTOs().length > 0) {
      str.append("入库商品:");
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : this.getItemDTOs()) {
        if (purchaseInventoryItemDTO == null) continue;
        //添加每个单据的产品信息
        itemIndexDTOList.add(purchaseInventoryItemDTO.toItemIndexDTO(this));
        inOutRecordDTOList.add(purchaseInventoryItemDTO.toInOutRecordDTO(this));
        //保存入库内容
        str.append("(").append("品名:").append(purchaseInventoryItemDTO.getProductName());
        if (!StringUtils.isBlank(purchaseInventoryItemDTO.getBrand())) {
          str.append("品牌:").append(purchaseInventoryItemDTO.getBrand());
        }
        if (purchaseInventoryItemDTO.getPurchasePrice() != null && purchaseInventoryItemDTO.getPurchasePrice() > -0.0001) {
          str.append("单价:").append(purchaseInventoryItemDTO.getPurchasePrice());
        }
        if (purchaseInventoryItemDTO.getAmount() > 0.0) {
          str.append("数量:").append(purchaseInventoryItemDTO.getAmount());
        }
        str.append(");");
      }
    }
    orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
    orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    if (str.length() > 1) {
      String orderContent = str.substring(0, str.length() - 1);
      if (orderContent.length() > 450) {
        orderContent = orderContent.substring(0, 450);
        orderContent = orderContent + "等";
      }
      orderIndexDTO.setOrderContent(orderContent);
    }
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
    return orderIndexDTO;
  }

  public void clearSupplierInfo() {
    setSupplierId(null);
    setSupplier(null);
    setContact(null);
    setMobile(null);
    setLandline(null);
    setAddress(null);
    setBusinessScope(null);
    setBank(null);
    setAccount(null);
    setAccountName(null);
    setCategory(null);
    setAbbr(null);
    setSettlementType(null);
    setFax(null);
    setQq(null);
    setInvoiceCategory(null);
    setEmail(null);
  }

  public void setPayableDTO(PayableDTO payableDTO) {
    setCash(payableDTO.getCash());
    setBankCardAmount(payableDTO.getBankCard());
    setCheckAmount(payableDTO.getCheque());
    setCreditAmount(payableDTO.getCreditAmount());
    setDepositAmount(payableDTO.getDeposit());
    setDeduction(payableDTO.getDeduction());
    setStrikeAmount(payableDTO.getStrikeAmount());
  }

  public Set<String> getProductKindNames() {
    Set<String> productKindNames = new HashSet<String>();
    if(!ArrayUtil.isEmpty(this.getItemDTOs())){
      for(PurchaseInventoryItemDTO purchaseInventoryItemDTO : getItemDTOs()){
        if(StringUtil.isNotEmpty(purchaseInventoryItemDTO.getProductName())
          && StringUtil.isNotEmpty(purchaseInventoryItemDTO.getProductKind())){
          purchaseInventoryItemDTO.setProductKind(purchaseInventoryItemDTO.getProductKind().trim());
          productKindNames.add(purchaseInventoryItemDTO.getProductKind());
        }
      }
    }
    return productKindNames;
  }

  public void setKindIds(Map<String, KindDTO> kindDTOMap) {
    if (MapUtils.isEmpty(kindDTOMap) || CollectionUtils.isEmpty(getProductKindNames())) {
      return;
    }
    for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : getItemDTOs()) {
      if (StringUtil.isNotEmpty(purchaseInventoryItemDTO.getProductName())
        && StringUtil.isNotEmpty(purchaseInventoryItemDTO.getProductKind())) {
        KindDTO kindDTO = kindDTOMap.get(purchaseInventoryItemDTO.getProductKind());
        if (kindDTO != null) {
          purchaseInventoryItemDTO.setProductKindId(kindDTO.getId());
        }
      }
    }
  }
}
