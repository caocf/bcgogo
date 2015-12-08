package com.bcgogo.txn.dto;


import com.bcgogo.common.DataValidation;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopUnitDTO;
import com.bcgogo.enums.*;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.txn.dto.supplierComment.SupplierCommentRecordDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PurchaseOrderDTO extends BcgogoOrderDto {
  private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderDTO.class);

  public void parseVehicleBrand() {
    if (itemDTOs != null) {
      for (PurchaseOrderItemDTO itemDTO : itemDTOs) {
//        if (StringUtils.isBlank(itemDTO.getVehicleBrand())
//            && StringUtils.isBlank(itemDTO.getVehicleModel())
//            && StringUtils.isBlank(itemDTO.getVehicleEngine())
//            && StringUtils.isBlank(itemDTO.getVehicleYear())) {
//          itemDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
//        } else {
//          itemDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
//        }
        if (itemDTO.getProductVehicleStatus() == null) {
          itemDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
        }
      }
    }
  }

  public void parseOrder() {
    //去除空行
    if (itemDTOs != null) {
      List<PurchaseOrderItemDTO> purchaseOrderItemDTOList = new ArrayList<PurchaseOrderItemDTO>();
      for (PurchaseOrderItemDTO purchaseOrderItemDTO : itemDTOs) {
        if (purchaseOrderItemDTO != null && StringUtils.isNotBlank(purchaseOrderItemDTO.getProductName())) {
          purchaseOrderItemDTOList.add(purchaseOrderItemDTO);
        }
      }
      if (CollectionUtils.isNotEmpty(purchaseOrderItemDTOList)) {
        itemDTOs = purchaseOrderItemDTOList.toArray(new PurchaseOrderItemDTO[purchaseOrderItemDTOList.size()]);
      } else {
        itemDTOs = new PurchaseOrderItemDTO[0];
      }
    }
    //增加产品车型关系
    if (itemDTOs != null) {
      for (PurchaseOrderItemDTO itemDTO : itemDTOs) {
        if (itemDTO.getProductVehicleStatus() == null) {
          itemDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
        }
      }
    }
    //校验手机号
    if (DataValidation.mobileValidation(this.getMobile())) {
      this.setMobile(this.getMobile());
    } else {
      this.setMobile("");
    }

    //处理单据时间
    try {
      this.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, this.getEditDateStr()));
      if(this.getEditDate()==null) this.setEditDate(System.currentTimeMillis());
      this.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getVestDateStr()));
      if(this.getVestDate()==null) this.setVestDate(System.currentTimeMillis());

      this.setDeliveryDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, this.getDeliveryDateStr()));
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }


  }

  public PurchaseOrderDTO() {
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
    if(supplierId != null){
      setSupplierIdStr(supplierId.toString());
    }else{
      setSupplierIdStr("");
    }
  }

  public String getSupplierIdStr() {
    return supplierIdStr;
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
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

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
    this.totalStr = MoneyUtil.toBigType(String.valueOf(total));
  }

  public String getTotalStr() {
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

  public PurchaseOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(PurchaseOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public Long[] getSupplierProductIds(){
    List<Long> productIdList = new ArrayList<Long>();
    if(!ArrayUtils.isEmpty(this.getItemDTOs())){
      for (PurchaseOrderItemDTO itemDTO : this.getItemDTOs()) {
        if(itemDTO.getSupplierProductId()!=null){
          productIdList.add(itemDTO.getSupplierProductId());
        }
      }
    }
    return productIdList.toArray(new Long[productIdList.size()]);
  }

  public Set<Long> getSupplierProductIdsSet(){
    Set<Long> productIdsSet = new HashSet<Long>();
    if(!ArrayUtils.isEmpty(this.getItemDTOs())){
      for (PurchaseOrderItemDTO itemDTO : this.getItemDTOs()) {
        if(itemDTO.getSupplierProductId()!=null){
          productIdsSet.add(itemDTO.getSupplierProductId());
        }
      }
    }
    return productIdsSet;
  }
//  public Long getId() {
//    return this.id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
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

  public String getExecutor() {
    return executor;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Long getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(Long invalidateDate) {
    this.invalidateDate = invalidateDate;
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

  public String getDeliveryDateStr() {
    return deliveryDateStr;
  }

  public void setDeliveryDateStr(String deliveryDateStr) {
    this.deliveryDateStr = deliveryDateStr;
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

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
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

  public String getQqArray() {
    return qqArray;
  }

  public void setQqArray(String qqArray) {
    this.qqArray = qqArray;
  }

  public Long getInvoiceCategory() {
    return invoiceCategory;
  }

  public void setInvoiceCategory(Long invoiceCategory) {
    this.invoiceCategory = invoiceCategory;
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

  public String getBillProducer() {
    return billProducer;
  }

  public void setBillProducer(String billProducer) {
    this.billProducer = billProducer;
  }

  public ShopUnitDTO[] getShopUnits() {
    return shopUnits;
  }

  public void setShopUnits(ShopUnitDTO[] shopUnits) {
    this.shopUnits = shopUnits;
  }

  public Long getVestDate() {
    return this.vestDate;
  }

  public void setVestDate(Long vestDate) {
    if (StringUtils.isNotBlank(vestDateStr)) {
      this.vestDateStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", vestDate);
    }
    this.vestDate = vestDate;
  }

  public String getVestDateStr() {
    return this.vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public InventoryLimitDTO getInventoryLimitDTO() {
    return inventoryLimitDTO;
  }

  public void setInventoryLimitDTO(InventoryLimitDTO inventoryLimitDTO) {
    this.inventoryLimitDTO = inventoryLimitDTO;
  }

  //  private Long id;
//  private Long shopId;
  private String shopName;
  private String shopAddress;
  private String shopLandLine;
  private Long date;
  private String no;
  private String refNo;
  private Integer deptId;
  private String dept;
  private Long supplierId;
  private String supplierIdStr;
  private String supplier;
  private Long executorId;
  private String executor;
  private double total;
  private String totalStr;
  private Long deliveryDate;
  private String deliveryDateStr;
  private OrderStatus status;
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
  private String billProducer;
  private Long billProducerId;
  private PurchaseOrderItemDTO[] itemDTOs;
  private Long inventoryVestDate;

  private String contact;
  private Long contactId;
  private String contactIdStr;



  private String mobile;
  private String address;
  private String bank;
  private String account;
  private String businessScope;
  private String accountName;
  private CustomerStatus supplierStatus;
  private Long category;
  private String abbr;
  private Long settlementType;
  private String landline;
  private String fax;
  private String qq;
  private String qqArray;//多联系人后台传到前台一组qq号，筛选出一个在线的。多个qq逗号区分
  private Long invoiceCategory;
  private Long vestDate;
  private String vestDateStr;
  private String email;
  private String receiptNo;
  private String draftOrderIdStr;
  private boolean isWholesalerPurchase = false;        //是否批发商采购
  private Long supplierShopId;
  private String supplierShopIdStr;
  private InventoryLimitDTO inventoryLimitDTO;
  private String refuseMsg;//拒绝理由
  private BtnType btnType;//操作类型，保存，改单
  private Long expressId;//快递信息
  private Long preDispatchDate;//卖家预计发货时间
  private String preDispatchDateStr;//卖家预计发货时间
  private String waybills; //快递号
  private String company;  //快递公司名
  private String dispatchMemo;  //快递备注
  private String saleOrderReceiptNo;//关联销售单号
  private boolean isShortage = false; //卖方是否有缺料
  private Long province;
  private Long city;
  private Long region;
  private String areaInfo;//所属区域的文案，省市区。
  private String promotionsInfoJson;
  private PromotionsInfoDTO promotionsInfoDTO;
  private String shopIdStr;

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public String getPromotionsInfoJson() {
    return promotionsInfoJson;
  }

  public void setPromotionsInfoJson(String promotionsInfoJson) {
    this.promotionsInfoJson = promotionsInfoJson;
    if(StringUtils.isNotBlank(promotionsInfoJson)){
      PromotionsInfoDTO infoDTO = (PromotionsInfoDTO)JsonUtil.jsonToObject(promotionsInfoJson, PromotionsInfoDTO.class);
      setPromotionsInfoDTO(infoDTO);
    }
  }

  private boolean fromQuotedPreBuyOrder = false;
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

  public String getAreaInfo() {
    return areaInfo;
  }

  public void setAreaInfo(String areaInfo) {
    this.areaInfo = areaInfo;
  }

  //供应商点评相关
  private String supplierCommentRecordIdStr;//供应商点评记录idStr
  private boolean addContent;//是否可以追加评论
  private Double qualityScore;  //质量分数
  private Double performanceScore; //性价比分数
  private Double speedScore; //发货速度分数
  private Double attitudeScore;   //服务态度分数
  private String supplierCommentContent;
  private String addCommentContent;//追加的内容
  private Long purchaseInventoryId;  //采购单对应的入库单id
  private String purchaseInventoryIdStr;
  private String commentStatusStr;//供应商记录评价状态
  private String purchaseInventoryReceiptNo;//入库单单据号

  public String getCommentStatusStr() {
    return commentStatusStr;
  }

  public void setCommentStatusStr(String commentStatusStr) {
    this.commentStatusStr = commentStatusStr;
  }

  public String getAddCommentContent() {
    return addCommentContent;
  }

  public void setAddCommentContent(String addCommentContent) {
    this.addCommentContent = addCommentContent;
  }

  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
    if(purchaseInventoryId != null){
      setPurchaseInventoryIdStr(purchaseInventoryId.toString());
    }else{
      setPurchaseInventoryIdStr("");
    }
  }

  public String getPurchaseInventoryIdStr() {
    return purchaseInventoryIdStr;
  }

  public void setPurchaseInventoryIdStr(String purchaseInventoryIdStr) {
    this.purchaseInventoryIdStr = purchaseInventoryIdStr;
  }

  public String getPurchaseInventoryReceiptNo() {
    return purchaseInventoryReceiptNo;
  }

  public void setPurchaseInventoryReceiptNo(String purchaseInventoryReceiptNo) {
    this.purchaseInventoryReceiptNo = purchaseInventoryReceiptNo;
  }

  public CustomerStatus getSupplierStatus() {
    return supplierStatus;
  }

  public void setSupplierStatus(CustomerStatus supplierStatus) {
    this.supplierStatus = supplierStatus;
  }

  public String getSupplierCommentContent() {
    return supplierCommentContent;
  }

  public void setSupplierCommentContent(String supplierCommentContent) {
    this.supplierCommentContent = supplierCommentContent;
  }

  public boolean isFromQuotedPreBuyOrder() {
    return fromQuotedPreBuyOrder;
  }

  public void setFromQuotedPreBuyOrder(boolean fromQuotedPreBuyOrder) {
    this.fromQuotedPreBuyOrder = fromQuotedPreBuyOrder;
  }

  public Double getQualityScore() {
    return qualityScore;
  }

  public void setQualityScore(Double qualityScore) {
    this.qualityScore = qualityScore;
  }

  public Double getPerformanceScore() {
    return performanceScore;
  }

  public void setPerformanceScore(Double performanceScore) {
    this.performanceScore = performanceScore;
  }

  public Double getSpeedScore() {
    return speedScore;
  }

  public void setSpeedScore(Double speedScore) {
    this.speedScore = speedScore;
  }

  public Double getAttitudeScore() {
    return attitudeScore;
  }

  public void setAttitudeScore(Double attitudeScore) {
    this.attitudeScore = attitudeScore;
  }

  public boolean isAddContent() {
    return addContent;
  }

  public void setAddContent(boolean addContent) {
    this.addContent = addContent;
  }

  public String getSupplierCommentRecordIdStr() {
    return supplierCommentRecordIdStr;
  }

  public void setSupplierCommentRecordIdStr(String supplierCommentRecordIdStr) {
    this.supplierCommentRecordIdStr = supplierCommentRecordIdStr;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  private ShopUnitDTO[] shopUnits;

  private Long creationDate;

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
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

  public Long getBillProducerId() {
    return billProducerId;
  }

  public void setBillProducerId(Long billProducerId) {
    this.billProducerId = billProducerId;
  }

  public String getDraftOrderIdStr() {
    return draftOrderIdStr;
  }

  public void setDraftOrderIdStr(String draftOrderIdStr) {
    this.draftOrderIdStr = draftOrderIdStr;
  }

  public boolean isWholesalerPurchase() {
    return isWholesalerPurchase;
  }

  public void setWholesalerPurchase(boolean wholesalerPurchase) {
    isWholesalerPurchase = wholesalerPurchase;
  }

  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    if (supplierShopId != null) {
      isWholesalerPurchase = true;
    }else {
      isWholesalerPurchase = false;
    }
    this.supplierShopId = supplierShopId;
    if (supplierShopId != null) {
      setSupplierShopIdStr(supplierShopId.toString());
    } else {
      setSupplierShopIdStr("");
    }
  }

  public String getSupplierShopIdStr() {
    return supplierShopIdStr;
  }

  public void setSupplierShopIdStr(String supplierShopIdStr) {
    this.supplierShopIdStr = supplierShopIdStr;
  }

  public String getRefuseMsg() {
    return refuseMsg;
  }

  public void setRefuseMsg(String refuseMsg) {
    this.refuseMsg = refuseMsg;
  }

  public BtnType getBtnType() {
    return btnType;
  }

  public void setBtnType(BtnType btnType) {
    this.btnType = btnType;
  }

  public void setBtnTypeStr(String btnType) {
    if (StringUtils.isNotBlank(btnType)) {
      this.btnType = BtnType.valueOf(btnType.toUpperCase());
    } else {
      this.btnType = null;
    }
  }

  public Long getExpressId() {
    return expressId;
  }

  public void setExpressId(Long expressId) {
    this.expressId = expressId;
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

  public boolean isShortage() {
    return isShortage;
  }

  public void setShortage(boolean shortage) {
    isShortage = shortage;
  }

  public String getSaleOrderReceiptNo() {
    return saleOrderReceiptNo;
  }

  public void setSaleOrderReceiptNo(String saleOrderReceiptNo) {
    this.saleOrderReceiptNo = saleOrderReceiptNo;
  }

  public Long getInventoryVestDate() {
    return inventoryVestDate;
  }

  public void setInventoryVestDate(Long inventoryVestDate) {
    this.inventoryVestDate = inventoryVestDate;
  }

  private Long lastModified;

  public void setSupplierDTO(SupplierDTO supplierDTO) {
    if (supplierDTO == null) {
      return;
    }
    supplierId = supplierDTO.getId();
    supplier = supplierDTO.getName();
    mobile = supplierDTO.getMobile();
    address = supplierDTO.getAddress();
    account = supplierDTO.getAccount();
    bank = supplierDTO.getBank();
    accountName = supplierDTO.getAccountName();
    category = supplierDTO.getCategory();
    abbr = supplierDTO.getAbbr();
    settlementType = supplierDTO.getSettlementTypeId();
    landline = supplierDTO.getLandLine();
    fax = supplierDTO.getFax();
    qq = supplierDTO.getQq();
    email = supplierDTO.getEmail();
    invoiceCategory = supplierDTO.getInvoiceCategoryId();
    supplierShopId = supplierDTO.getSupplierShopId();
    if (supplierDTO.getSupplierShopId() != null) {
      isWholesalerPurchase = true;
    }else {
      isWholesalerPurchase = false;
    }
    supplierStatus = supplierDTO.getStatus();
    province = supplierDTO.getProvince();
    city = supplierDTO.getCity();
    region = supplierDTO.getRegion();

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
        setContactIdStr(contactDTO.getId() == null ? null : contactDTO.getId().toString());
        setQq(contactDTO.getQq());
        setEmail(contactDTO.getEmail());
      }
    }
  }

  public void set(ShopDTO shopDTO) {
    if (null != shopDTO) {
      super.setShopId(shopDTO.getId());
      shopName = shopDTO.getName();
      shopAddress = shopDTO.getAddress();
      shopLandLine = shopDTO.getLandline();
    }
  }

  public void set(Long shopId, Long userId, String userName) {
    super.setShopId(shopId);
    editorId = userId;
    editor = userName;
    long curTime = System.currentTimeMillis();
    editDate = curTime;
    vestDate = curTime;
    editDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, curTime);
    vestDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime);
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("PurchaseOrderDTO");
//    sb.append("{id=").append(id == null ? "" : id);
//    sb.append(", shopId=").append(shopId == null ? "" : shopId);
    sb.append(", shopName='").append(StringUtil.truncValue(shopName)).append('\'');
    sb.append(", billProducerId='").append(StringUtil.truncValue(null == billProducerId ? "" : billProducerId.toString())).append('\'');
    sb.append(", shopAddress='").append(StringUtil.truncValue(shopAddress)).append('\'');
    sb.append(", shopLandLine='").append(StringUtil.truncValue(shopLandLine)).append('\'');
    sb.append(", date=").append(date == null ? "" : date);
    sb.append(", no='").append(StringUtil.truncValue(no)).append('\'');
    sb.append(", refNo='").append(StringUtil.truncValue(refNo)).append('\'');
    sb.append(", deptId=").append(deptId == null ? "" : deptId);
    sb.append(", dept='").append(StringUtil.truncValue(dept)).append('\'');
    sb.append(", supplierId=").append(supplierId == null ? "" : supplierId);
    sb.append(", supplier='").append(StringUtil.truncValue(supplier)).append('\'');
    sb.append(", executorId=").append(executorId == null ? "" : executorId);
    sb.append(", executor='").append(StringUtil.truncValue(executor)).append('\'');
    sb.append(", total=").append(total);
    sb.append(", deliveryDate=").append(deliveryDate == null ? "" : deliveryDate);
    sb.append(", deliveryDateStr='").append(StringUtil.truncValue(deliveryDateStr)).append('\'');
    sb.append(", status=").append(status == null ? "" : status);
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
    sb.append(", billProducer='").append(StringUtil.truncValue(billProducer)).append('\'');
    if (itemDTOs != null) {
      for (PurchaseOrderItemDTO itemDTO : itemDTOs) {
        if (itemDTO == null) {
          continue;
        }
        sb.append(itemDTO.toString());
      }
    }
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
    sb.append(", fax='").append(StringUtil.truncValue(fax)).append('\'');
    sb.append(", qq='").append(StringUtil.truncValue(qq)).append('\'');
    sb.append(", invoiceCategory=").append(invoiceCategory == null ? "" : invoiceCategory);
    sb.append(", lastModified=").append(lastModified == null ? "" : lastModified);
    sb.append('}');
    return sb.toString();
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.PURCHASE);
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setCreationDate(this.getCreationDate() == null ? System.currentTimeMillis() : this.getCreationDate());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setInventoryVestDate(getInventoryVestDate());
    StringBuffer orderContent = new StringBuffer();
    //comment zjt:如果结算改了 这边需要修改
    List<PayMethod> payMethods = new ArrayList<PayMethod>();
    payMethods.add(PayMethod.CASH);
    orderIndexDTO.setPayMethods(payMethods);
    if (this.getItemDTOs() != null && this.getItemDTOs().length > 0) {
      orderContent.append("采购商品:");
      for (PurchaseOrderItemDTO purchaseOrderItemDTO : this.getItemDTOs()) {
        //添加每个单据的产品信息
        itemIndexDTOList.add(purchaseOrderItemDTO.toItemIndexDTO(this));
        //保存入库内容
        orderContent.append("(");
        orderContent.append("品名:" + purchaseOrderItemDTO.getProductName());
        if (!StringUtils.isBlank(purchaseOrderItemDTO.getBrand())) {
          orderContent.append("品牌:").append(purchaseOrderItemDTO.getBrand());
        }
        if (purchaseOrderItemDTO.getPrice() != null && purchaseOrderItemDTO.getPrice() >= 0.0) {
          orderContent.append(",采购价:").append(purchaseOrderItemDTO.getPrice());

        }
        if (purchaseOrderItemDTO.getAmount() != null && purchaseOrderItemDTO.getAmount() > 0.0) {
          orderContent.append("数量:").append(purchaseOrderItemDTO.getAmount());
        }
        orderContent.append(");");
      }
    }
    orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
    //数据表的长度为500，这里只存450个字符
    String str = "";
    if (orderContent != null && orderContent.length() > 1) {
      str = orderContent.substring(0, orderContent.length() - 1);
    }
    if (str.length() > 450) {
      str = str.substring(0, 450);
      str = str + "等";
    }
    orderIndexDTO.setOrderContent(str);
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setCustomerOrSupplierId(this.getSupplierId());
    orderIndexDTO.setCustomerOrSupplierName(this.getSupplier());
    orderIndexDTO.setContactNum(this.getMobile());
    orderIndexDTO.setAddress(this.getAddress());
    orderIndexDTO.setContact(this.getContact());
    orderIndexDTO.setArrears(0d);
    orderIndexDTO.setCustomerOrSupplierShopId(this.getSupplierShopId());

    return orderIndexDTO;
  }

  public SupplierDTO generateSupplierDTO() {
    SupplierDTO supplierDTO = new SupplierDTO();
    supplierDTO.setId(getSupplierId());
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
    supplierDTO.setSettlementTypeId(getSettlementType());
    supplierDTO.setLandLine(getLandline());
    supplierDTO.setFax(getFax());
    supplierDTO.setQq(getQq());
    supplierDTO.setEmail(getEmail());
    supplierDTO.setInvoiceCategoryId(getInvoiceCategory());
    supplierDTO.setSupplierShopId(getSupplierShopId());
    return supplierDTO;
  }

  public void clearSupplierInfo() {
    supplierId = null;
    supplier = null;
    contact = null;
    contactId = null;
    contactIdStr = null;
    mobile = null;
    address = null;
    account = null;
    bank = null;
    accountName = null;
    category = null;
    abbr = null;
    settlementType = null;
    landline = null;
    fax = null;
    qq = null;
    email = null;
    invoiceCategory = null;
    businessScope = null;
    supplierShopId = null;
    isWholesalerPurchase = false;
  }

  public void setCommentRecordDTO(SupplierCommentRecordDTO commentRecordDTO) {
    if (commentRecordDTO == null) {
      this.setAddContent(false);
    } else {
      this.setCommentStatusStr(commentRecordDTO.getCommentStatus().toString());
      this.setSupplierCommentRecordIdStr(commentRecordDTO.getId().toString());
      this.setQualityScore(commentRecordDTO.getQualityScore());
      this.setSpeedScore(commentRecordDTO.getSpeedScore());
      this.setAttitudeScore(commentRecordDTO.getAttitudeScore());
      this.setPerformanceScore(commentRecordDTO.getPerformanceScore());
      this.setSupplierCommentContent(commentRecordDTO.getFirstCommentContent());
      if (com.bcgogo.utils.StringUtil.isNotEmpty(commentRecordDTO.getAddCommentContent())) {
        this.setAddContent(false);
        this.setAddCommentContent(commentRecordDTO.getAddCommentContent());
      } else {
        this.setAddContent(true);
      }
    }
  }

  public enum BtnType {
    SAVE("确定采购"),
    MODIFY("改单");

    String value;

    public String getValue() {
      return value;
    }

    private BtnType(String value) {
      this.value = value;
    }
  }


  public void setExpressDTO(ExpressDTO expressDTO) {
    if(expressDTO == null){
      return;
    }
    this.setCompany(expressDTO.getCompany());
    this.setWaybills(expressDTO.getWaybills());
    this.setDispatchMemo(expressDTO.getMemo());
  }

  public PromotionsInfoDTO getPromotionsInfoDTO() {
    return promotionsInfoDTO;
  }

  public void setPromotionsInfoDTO(PromotionsInfoDTO promotionsInfoDTO) {
    this.promotionsInfoDTO = promotionsInfoDTO;
  }
}