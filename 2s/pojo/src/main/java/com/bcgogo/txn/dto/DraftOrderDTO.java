package com.bcgogo.txn.dto;

import com.bcgogo.BooleanEnum;
import com.bcgogo.enums.DraftOrderStatus;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-8
 * Time: 上午5:02
 * To change this template use File | Settings | File Templates.
 */
public class DraftOrderDTO extends BcgogoOrderDto{
  private static final Logger LOG = LoggerFactory.getLogger(DraftOrderDTO.class);
  private String customerOrSupplierName;
  private Long customerOrSupplierId;
  private Long customerOrSupplierShopId;
  private Long contactId;
  private String contactIdStr;
  private String contact;
  private String mobile;
  private String landLine;
  private String address;
  private OrderTypes orderTypeEnum;
  private String orderTypeStr;
  private DraftOrderStatus status;   // 草稿箱状态
  private OrderStatus txnOrderStatus;   // 原始单据状态
  private String memo;
  private Long editDate;       //入库日期   ，采购日期 ，销售日期 ，退货日期 。
  private Long saveTime;//草稿箱保存时间
  private String userName;
  private String saveTimeStr;
  private Double total;
  private Double settledAmount;//施工单，销售单实收
  private Double debt;
  private Long repaymentTime;//施工单，销售单还款时间
  private Long billProducerId;   //制单人Id   转换为入库单的 acceptorId;销售单的 goodsSalerId，退货单的editorId;
  private String billProducer;
  private String deliveryDateStr;
  private Long purchaseOrderId;   //入库单关联采购单Id
  private Long txnOrderId;   //与草稿箱关联的进销存Id
  //退货
  private String returnPayableType;
  private String bank;
  private String account;
  private String businessScope;
  private String accountName;
  private Long category;
  private String abbr;
  private Long settlementType;
  private String fax;
  private String qq;
  private Long invoiceCategory;
  private String email;
  //施工单
  private Long vehicleId;
  private String vehicle;
  private String brand;
  private String model;
  private String engineNo;
  private String color;
  private String chassisNo;
  private String vehicleContact;
  private String vehicleMobile;
  private Double startMileage;
  private Double endMileage;
  private String fuelNumber;
  private Long startDate;
  private Long endDate;
  private String vehicleHandover;//接车人
  private String productSales;
  private Long maintainTime;   //预约保养时间
  private Long insureTime;      //预约保险时间
  private Long examineTime;    //预约验车时间
  private Long maintainMileage;//保养里程
  private Long consumingRecordId;  //记录施工单对应的代金券消费记录id add by LiTao 2015-11-17

  // material，serviceContent用于存储。。
  private String material;       //材料
  private String materialStr;
  private String serviceContent;    //服务内容
  private String serviceContentStr;
  private Long vestDate;
  private String vestDateStr;
  private String shopName;
  private String shopLandLine;
  private String shopAddress;
  //针对退货单
  private Long originalOrderId;
  private OrderTypes originalOrderType;
  private String originalReceiptNo;
  private static final int MAX_MATERIAL_LENGTH=50;
  private DraftOrderItemDTO[] itemDTOs;
  private List<DraftOrderOtherIncomeItemDTO> otherIncomeItemDTOList;

  public DraftOrderStatus editStatus;

  private String description;  // 故障说明
  private String receiptNo;
  private Long appointOrderId;  //相关联的预约单Id
  private Long vehicleHandoverId;

  public String getVehicleHandover() {
    return vehicleHandover;
  }

  public void setVehicleHandover(String vehicleHandover) {
    this.vehicleHandover = vehicleHandover;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public DraftOrderStatus getEditStatus() {
    return editStatus;
  }

  public void setEditStatus(DraftOrderStatus editStatus) {
    this.editStatus = editStatus;
  }

  public String getReturnPayableType() {
    return returnPayableType;
  }

  public Long getCustomerOrSupplierShopId() {
    return customerOrSupplierShopId;
  }

  public void setCustomerOrSupplierShopId(Long customerOrSupplierShopId) {
    this.customerOrSupplierShopId = customerOrSupplierShopId;
  }

  public void setReturnPayableType(String returnPayableType) {
    this.returnPayableType = returnPayableType;
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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getDebt() {
    if(debt==null) return 0D;
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
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

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }

  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  public DraftOrderStatus getStatus() {
    return status;
  }

  public void setStatus(DraftOrderStatus status) {
    this.status = status;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public Long getSaveTime() {
    return saveTime;
  }

  public void setSaveTime(Long saveTime) {
    this.saveTime = saveTime;
  }

  public String getSaveTimeStr() {
    return saveTimeStr;
  }

  public void setSaveTimeStr(String saveTimeStr) {
    this.saveTimeStr = saveTimeStr;
  }

  public Double getSettledAmount() {
    if(settledAmount==null) return 0D;
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public Long getRepaymentTime() {
    return repaymentTime;
  }

  public void setRepaymentTime(Long repaymentTime) {
    this.repaymentTime = repaymentTime;
  }

  public Long getBillProducerId() {
    return billProducerId;
  }

  public void setBillProducerId(Long billProducerId) {
    this.billProducerId = billProducerId;
  }

  public String getBillProducer() {
    return billProducer;
  }

  public void setBillProducer(String billProducer) {
    this.billProducer = billProducer;
  }

  public String getDeliveryDateStr() {
    return deliveryDateStr;
  }

  public void setDeliveryDateStr(String deliveryDateStr) {
    this.deliveryDateStr = deliveryDateStr;
  }

  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
  }

  public Long getTxnOrderId() {
    return txnOrderId;
  }

  public void setTxnOrderId(Long txnOrderId) {
    this.txnOrderId = txnOrderId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
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

  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  public Double getEndMileage() {
    return endMileage;
  }

  public void setEndMileage(Double endMileage) {
    this.endMileage = endMileage;
  }

  public String getFuelNumber() {
    return fuelNumber;
  }

  public void setFuelNumber(String fuelNumber) {
    this.fuelNumber = fuelNumber;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public String getProductSales() {
    return productSales;
  }

  public void setProductSales(String productSales) {
    this.productSales = productSales;
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public String getMaterialStr() {
    return materialStr;
  }

  public void setMaterialStr(String materialStr) {
    this.materialStr = materialStr;
  }

  public String getServiceContent() {
    return serviceContent;
  }

  public void setServiceContent(String serviceContent) {
    this.serviceContent = serviceContent;
  }

  public String getServiceContentStr() {
    return serviceContentStr;
  }

  public void setServiceContentStr(String serviceContentStr) {
    this.serviceContentStr = serviceContentStr;
  }

  public OrderStatus getTxnOrderStatus() {
    return txnOrderStatus;
  }

  public void setTxnOrderStatus(OrderStatus txnOrderStatus) {
    this.txnOrderStatus = txnOrderStatus;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public Long getMaintainTime() {
    return maintainTime;
  }

  public void setMaintainTime(Long maintainTime) {
    this.maintainTime = maintainTime;
  }

  public Long getInsureTime() {
    return insureTime;
  }

  public void setInsureTime(Long insureTime) {
    this.insureTime = insureTime;
  }

  public Long getExamineTime() {
    return examineTime;
  }

  public void setExamineTime(Long examineTime) {
    this.examineTime = examineTime;
  }

  public Long getMaintainMileage() {
    return maintainMileage;
  }

  public void setMaintainMileage(Long maintainMileage) {
    this.maintainMileage = maintainMileage;
  }

  public DraftOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(DraftOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopLandLine() {
    return shopLandLine;
  }

  public void setShopLandLine(String shopLandLine) {
    this.shopLandLine = shopLandLine;
  }

  public String getShopAddress() {
    return shopAddress;
  }

  public void setShopAddress(String shopAddress) {
    this.shopAddress = shopAddress;
  }

  public Long getOriginalOrderId() {
    return originalOrderId;
  }

  public void setOriginalOrderId(Long originalOrderId) {
    this.originalOrderId = originalOrderId;
  }

  public OrderTypes getOriginalOrderType() {
    return originalOrderType;
  }

  public void setOriginalOrderType(OrderTypes originalOrderType) {
    this.originalOrderType = originalOrderType;
  }

  public String getOriginalReceiptNo() {
    return originalReceiptNo;
  }

  public void setOriginalReceiptNo(String originalReceiptNo) {
    this.originalReceiptNo = originalReceiptNo;
  }

  public List<DraftOrderOtherIncomeItemDTO> getOtherIncomeItemDTOList() {
    return otherIncomeItemDTOList;
  }

  public void setOtherIncomeItemDTOList(List<DraftOrderOtherIncomeItemDTO> otherIncomeItemDTOList) {
    this.otherIncomeItemDTOList = otherIncomeItemDTOList;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  public  DraftOrderDTO fromPurchaseOrderDTO(PurchaseOrderDTO purchaseOrderDTO){
    this.setId(NumberUtil.longValue(purchaseOrderDTO.getDraftOrderIdStr()));
    this.setStorehouseId(purchaseOrderDTO.getStorehouseId());
    this.setCustomerOrSupplierId(purchaseOrderDTO.getSupplierId());
    this.setCustomerOrSupplierShopId(purchaseOrderDTO.getSupplierShopId());
    this.setCustomerOrSupplierName(purchaseOrderDTO.getSupplier());
    this.setContact(purchaseOrderDTO.getContact());
    this.setContactId(purchaseOrderDTO.getContactId()); // add by zhuj
    this.setMobile(purchaseOrderDTO.getMobile());
    this.setLandLine(purchaseOrderDTO.getLandline());
    this.setAddress(purchaseOrderDTO.getAddress());
    this.setBank(purchaseOrderDTO.getBank());
    this.setAccount(purchaseOrderDTO.getAccount());
    this.setBusinessScope(purchaseOrderDTO.getBusinessScope());
    this.setAccountName(purchaseOrderDTO.getAccountName());
    this.setCategory(purchaseOrderDTO.getCategory());
    this.setAbbr(purchaseOrderDTO.getAbbr());
    this.setSettlementType(purchaseOrderDTO.getSettlementType());
    this.setQq(purchaseOrderDTO.getQq());
    this.setFax(purchaseOrderDTO.getFax());
    this.setEmail(purchaseOrderDTO.getEmail());
    this.setInvoiceCategory(purchaseOrderDTO.getInvoiceCategory());

    this.setOrderTypeEnum(OrderTypes.PURCHASE);
    this.setVestDateStr(purchaseOrderDTO.getVestDateStr());
    this.setMemo(purchaseOrderDTO.getMemo());
    this.setEditDate(purchaseOrderDTO.getEditDate());
    this.setTotal(purchaseOrderDTO.getTotal());
    this.setBillProducerId(purchaseOrderDTO.getBillProducerId());
    this.setDeliveryDateStr(purchaseOrderDTO.getDeliveryDateStr());
    this.setShopLandLine(purchaseOrderDTO.getShopLandLine());
    this.setShopAddress(purchaseOrderDTO.getShopAddress());
    this.setShopName(purchaseOrderDTO.getShopName());
    this.setReceiptNo(purchaseOrderDTO.getReceiptNo());
    List<DraftOrderItemDTO> itemDTOList=new ArrayList<DraftOrderItemDTO>();
    StringBuffer materialBuffer=new StringBuffer();
    for(PurchaseOrderItemDTO purchaseOrderItemDTO:purchaseOrderDTO.getItemDTOs() ){
      if(StringUtil.isAllEmpty(purchaseOrderItemDTO.getProductName(),purchaseOrderItemDTO.getBrand(),
          purchaseOrderItemDTO.getModel(),purchaseOrderItemDTO.getSpec(),
          purchaseOrderItemDTO.getVehicleBrand(),purchaseOrderItemDTO.getVehicleModel(),
          purchaseOrderItemDTO.getCommodityCode())) continue;
      DraftOrderItemDTO itemDTO=new DraftOrderItemDTO();
      itemDTO.setShopId(this.getShopId());
      itemDTO.setAmount(purchaseOrderItemDTO.getAmount());
      itemDTO.setPrice(purchaseOrderItemDTO.getPrice());
      itemDTO.setTotal(purchaseOrderItemDTO.getTotal());
      itemDTO.setMemo(purchaseOrderItemDTO.getMemo());
      itemDTO.setBrand(purchaseOrderItemDTO.getBrand());
      itemDTO.setModel(purchaseOrderItemDTO.getModel());
      itemDTO.setSpec(purchaseOrderItemDTO.getSpec());
      itemDTO.setProductType(purchaseOrderItemDTO.getProductType());
      itemDTO.setProductId(purchaseOrderItemDTO.getProductId());
      itemDTO.setProductIdStr(purchaseOrderItemDTO.getProductIdStr());
      itemDTO.setProductLocalInfoId(purchaseOrderItemDTO.getProductId());
      itemDTO.setSupplierProductLocalInfoId(purchaseOrderItemDTO.getSupplierProductId());
      itemDTO.setProductName(purchaseOrderItemDTO.getProductName());
      itemDTO.setStorageBin(purchaseOrderItemDTO.getStorageBin());
      itemDTO.setTradePrice(purchaseOrderItemDTO.getTradePrice());
      itemDTO.setUnit(purchaseOrderItemDTO.getUnit());
      itemDTO.setStorageUnit(purchaseOrderItemDTO.getStorageUnit());
      itemDTO.setSellUnit(purchaseOrderItemDTO.getSellUnit());
      itemDTO.setVehicleBrand(purchaseOrderItemDTO.getVehicleBrand());
      itemDTO.setVehicleModel(purchaseOrderItemDTO.getVehicleModel());
      itemDTO.setVehicleYear(purchaseOrderItemDTO.getVehicleYear());
      itemDTO.setVehicleEngine(purchaseOrderItemDTO.getVehicleEngine());
      itemDTO.setRate(purchaseOrderItemDTO.getRate());
      itemDTO.setLowerLimit(purchaseOrderItemDTO.getLowerLimit());
      itemDTO.setUpperLimit(purchaseOrderItemDTO.getUpperLimit());
      itemDTO.setCommodityCode(purchaseOrderItemDTO.getCommodityCode());
      itemDTO.setProductKind(purchaseOrderItemDTO.getProductKind());
      itemDTO.setProductKindId(purchaseOrderItemDTO.getProductKindId());
      materialBuffer.append(purchaseOrderItemDTO.getProductName());
      materialBuffer.append(";");
      itemDTOList.add(itemDTO);
    }
    String materialTitle=materialBuffer.toString();
    if(materialTitle.length()>DraftOrderDTO.MAX_MATERIAL_LENGTH){
      this.setMaterial(materialTitle.substring(0,DraftOrderDTO.MAX_MATERIAL_LENGTH));
    } else {
      this.setMaterial(materialTitle);
    }
    this.setItemDTOs(itemDTOList.toArray(new DraftOrderItemDTO[itemDTOList.size()]));
    return this;
  }

  public  DraftOrderDTO fromPurchaseInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception{
    if(NumberUtil.isNumber(purchaseInventoryDTO.getDraftOrderIdStr())){
      this.setId(NumberUtil.longValue(purchaseInventoryDTO.getDraftOrderIdStr()));
    }
    this.setStorehouseId(purchaseInventoryDTO.getStorehouseId());
    this.setCustomerOrSupplierId(purchaseInventoryDTO.getSupplierId());
    this.setCustomerOrSupplierName(purchaseInventoryDTO.getSupplier());
    this.setContact(purchaseInventoryDTO.getContact());
    this.setContactId(purchaseInventoryDTO.getContactId()); // add by zhuj
    this.setMobile(purchaseInventoryDTO.getMobile());
    this.setLandLine(purchaseInventoryDTO.getLandline());
    this.setAddress(purchaseInventoryDTO.getAddress());
    this.setBank(purchaseInventoryDTO.getBank());
    this.setAccount(purchaseInventoryDTO.getAccount());
    this.setBusinessScope(purchaseInventoryDTO.getBusinessScope());
    this.setAccountName(purchaseInventoryDTO.getAccountName());
    this.setCategory(purchaseInventoryDTO.getCategory());
    this.setAbbr(purchaseInventoryDTO.getAbbr());
    this.setSettlementType(purchaseInventoryDTO.getSettlementType());
    this.setQq(purchaseInventoryDTO.getQq());
    this.setFax(purchaseInventoryDTO.getFax());
    this.setEmail(purchaseInventoryDTO.getEmail());
    this.setInvoiceCategory(purchaseInventoryDTO.getInvoiceCategory());
    this.setReceiptNo(purchaseInventoryDTO.getReceiptNo());
    this.setOrderTypeEnum(OrderTypes.INVENTORY);
    this.setMemo(purchaseInventoryDTO.getMemo());
    this.setEditDate(purchaseInventoryDTO.getEditDate());
    this.setBillProducerId(purchaseInventoryDTO.getAcceptorId());
    this.setBillProducer(purchaseInventoryDTO.getAcceptor());
    this.setTxnOrderId(purchaseInventoryDTO.getId());
    this.setSettledAmount(purchaseInventoryDTO.getStroageActuallyPaid());
    this.setDebt(purchaseInventoryDTO.getStroageCreditAmount());
    this.setTotal(purchaseInventoryDTO.getTotal());
    this.setVestDateStr(purchaseInventoryDTO.getVestDateStr());
    this.setShopLandLine(purchaseInventoryDTO.getShopLandLine());
    this.setShopAddress(purchaseInventoryDTO.getShopAddress());
    this.setShopName(purchaseInventoryDTO.getShopName());

    List<DraftOrderItemDTO> itemDTOList=new ArrayList<DraftOrderItemDTO>();
    StringBuffer materialBuffer=new StringBuffer();
    if(!ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())){
      for(PurchaseInventoryItemDTO purchaseInventoryItemDTO:purchaseInventoryDTO.getItemDTOs() ){
        if(StringUtil.isAllEmpty(purchaseInventoryItemDTO.getProductName(),purchaseInventoryItemDTO.getBrand(),
            purchaseInventoryItemDTO.getModel(),purchaseInventoryItemDTO.getSpec(),
            purchaseInventoryItemDTO.getVehicleBrand(),purchaseInventoryItemDTO.getVehicleModel(),
            purchaseInventoryItemDTO.getCommodityCode())){
          continue;
        }

        DraftOrderItemDTO itemDTO=new DraftOrderItemDTO();
        itemDTO.setShopId(this.getShopId());
        itemDTO.setAmount(purchaseInventoryItemDTO.getAmount());
        itemDTO.setPrice(purchaseInventoryItemDTO.getPurchasePrice());
        itemDTO.setTotal(purchaseInventoryItemDTO.getTotal());
        itemDTO.setMemo(purchaseInventoryItemDTO.getMemo());
        itemDTO.setBrand(purchaseInventoryItemDTO.getBrand());
        itemDTO.setModel(purchaseInventoryItemDTO.getModel());
        itemDTO.setSpec(purchaseInventoryItemDTO.getSpec());
        itemDTO.setProductType(purchaseInventoryItemDTO.getProductType());
        itemDTO.setProductLocalInfoId(purchaseInventoryItemDTO.getProductId());
        itemDTO.setProductIdStr(purchaseInventoryItemDTO.getProductIdStr());
        itemDTO.setProductName(purchaseInventoryItemDTO.getProductName());
        itemDTO.setUnit(purchaseInventoryItemDTO.getUnit());
        itemDTO.setStorageUnit(purchaseInventoryItemDTO.getStorageUnit());
        itemDTO.setSellUnit(purchaseInventoryItemDTO.getSellUnit());
        itemDTO.setVehicleBrand(purchaseInventoryItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(purchaseInventoryItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(purchaseInventoryItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(purchaseInventoryItemDTO.getVehicleEngine());
        itemDTO.setRate(purchaseInventoryItemDTO.getRate());
        itemDTO.setLowerLimit(purchaseInventoryItemDTO.getLowerLimit());
        itemDTO.setUpperLimit(purchaseInventoryItemDTO.getUpperLimit());
        itemDTO.setRecommendedPrice(purchaseInventoryItemDTO.getRecommendedPrice());
        itemDTO.setStorageBin(purchaseInventoryItemDTO.getStorageBin());
        itemDTO.setTradePrice(purchaseInventoryItemDTO.getTradePrice());
        itemDTO.setCommodityCode(purchaseInventoryItemDTO.getCommodityCode());
        itemDTO.setProductKind(purchaseInventoryItemDTO.getProductKind());
        itemDTO.setProductKindId(purchaseInventoryItemDTO.getProductKindId());
        materialBuffer.append(purchaseInventoryItemDTO.getProductName());
        materialBuffer.append(";");
        itemDTOList.add(itemDTO);
      }
      String materialTitle=materialBuffer.toString();
      if(materialTitle.length()>DraftOrderDTO.MAX_MATERIAL_LENGTH){
        this.setMaterial(materialTitle.substring(0,DraftOrderDTO.MAX_MATERIAL_LENGTH));
      } else {
        this.setMaterial(materialTitle);
      }
      this.setItemDTOs(itemDTOList.toArray(new DraftOrderItemDTO[itemDTOList.size()]));
    }
    return this;
  }

  public  DraftOrderDTO fromRepairOrderDTO(RepairOrderDTO repairOrderDTO) throws Exception{
    this.setStorehouseId(repairOrderDTO.getStorehouseId());
    this.setTxnOrderId(repairOrderDTO.getId());
    this.setTxnOrderStatus(repairOrderDTO.getStatus());
    this.setCustomerOrSupplierId(repairOrderDTO.getCustomerId());
    this.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
    this.setContact(repairOrderDTO.getContact());
    this.setContactId(repairOrderDTO.getContactId()); // add by zhuj
    this.setMobile(repairOrderDTO.getMobile());
    this.setLandLine(repairOrderDTO.getLandLine());
    this.setStartDate(repairOrderDTO.getStartDate());
    this.setEndDate(repairOrderDTO.getEndDate());
    this.setStartMileage(repairOrderDTO.getStartMileage());
    this.setEndMileage(repairOrderDTO.getEndMileage());
    this.setFuelNumber(repairOrderDTO.getFuelNumber());
    this.setProductSales(repairOrderDTO.getProductSaler());
    this.setDebt(repairOrderDTO.getDebt());
    this.setTotal(repairOrderDTO.getTotal());
    this.setRepaymentTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getHuankuanTime()));
    this.setMaintainTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getMaintainTimeStr()));
    this.setInsureTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getInsureTimeStr()));
    this.setExamineTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getExamineTimeStr()));
    this.setMaintainMileage(repairOrderDTO.getMaintainMileage());
    this.setOrderTypeEnum(OrderTypes.REPAIR);
    this.setMemo(repairOrderDTO.getMemo());
    this.setEditDate(repairOrderDTO.getEditDate());
    this.setSettledAmount(repairOrderDTO.getSettledAmount());
    this.setVehicle(repairOrderDTO.getVechicle());
    this.setVehicleId(repairOrderDTO.getVechicleId());
    this.setBrand(repairOrderDTO.getBrand());
    this.setModel(repairOrderDTO.getModel());
    this.setVehicleContact(repairOrderDTO.getVehicleContact());
    this.setVehicleMobile(repairOrderDTO.getVehicleMobile());
    this.setColor(repairOrderDTO.getVehicleColor());
    this.setEngineNo(repairOrderDTO.getVehicleEngineNo());
    this.setChassisNo(repairOrderDTO.getVehicleChassisNo());
    this.setTxnOrderId(repairOrderDTO.getId());
    this.setShopLandLine(repairOrderDTO.getShopLandLine());
    this.setShopAddress(repairOrderDTO.getShopAddress());
    this.setShopName(repairOrderDTO.getShopName());
    this.setDescription(repairOrderDTO.getDescription());
    this.setReceiptNo(repairOrderDTO.getReceiptNo());
    this.setAppointOrderId(repairOrderDTO.getAppointOrderId());
    this.setVehicleHandover(repairOrderDTO.getVehicleHandover());
    this.setVehicleHandoverId(repairOrderDTO.getVehicleHandoverId());
    this.setConsumingRecordId(repairOrderDTO.getConsumingRecordId());
    List<DraftOrderItemDTO> itemDTOList=new ArrayList<DraftOrderItemDTO>();
    StringBuffer materialBuffer=new StringBuffer();
    if(!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())){
      for(RepairOrderItemDTO repairOrderItemDTO:repairOrderDTO.getItemDTOs() ){
        if(StringUtil.isAllEmpty(repairOrderItemDTO.getProductName(),repairOrderItemDTO.getBrand(),repairOrderItemDTO.getModel(),repairOrderItemDTO.getSpec())) continue;
        DraftOrderItemDTO itemDTO=new DraftOrderItemDTO();
        itemDTO.setItemId(repairOrderItemDTO.getId());
        itemDTO.setShopId(this.getShopId());
        itemDTO.setAmount(repairOrderItemDTO.getAmount());
        itemDTO.setPrice(repairOrderItemDTO.getPrice());
        itemDTO.setTotal(repairOrderItemDTO.getTotal());
        itemDTO.setMemo(repairOrderItemDTO.getMemo());
        itemDTO.setReserved(repairOrderItemDTO.getReserved());
        itemDTO.setCostPrice(repairOrderItemDTO.getCostPrice());
        itemDTO.setTotalCostPrice(repairOrderItemDTO.getTotalCostPrice());
        itemDTO.setPercentage(repairOrderItemDTO.getPercentage());
        itemDTO.setPercentageAmount(repairOrderItemDTO.getPercentageAmount());
        itemDTO.setBrand(repairOrderItemDTO.getBrand());
        itemDTO.setProductLocalInfoId(repairOrderItemDTO.getProductId());
        itemDTO.setModel(repairOrderItemDTO.getModel());
        itemDTO.setSpec(repairOrderItemDTO.getSpec());
        itemDTO.setProductType(String.valueOf(repairOrderItemDTO.getProductType()));
        itemDTO.setProductId(repairOrderItemDTO.getProductId());
        itemDTO.setProductIdStr(repairOrderItemDTO.getProductIdStr());
        itemDTO.setUnit(repairOrderItemDTO.getUnit());
        itemDTO.setStorageUnit(repairOrderItemDTO.getStorageUnit());
        itemDTO.setSellUnit(repairOrderItemDTO.getSellUnit());
        itemDTO.setVehicleBrand(repairOrderItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(repairOrderItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(repairOrderItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(repairOrderItemDTO.getVehicleEngine());
        itemDTO.setRate(repairOrderItemDTO.getRate());
        itemDTO.setProductName(repairOrderItemDTO.getProductName());
        itemDTO.setItemTypes(ItemTypes.MATERIAL);
        itemDTO.setCommodityCode(repairOrderItemDTO.getCommodityCode());
        itemDTO.setBusinessCategoryId(repairOrderItemDTO.getBusinessCategoryId());
        itemDTO.setBusinessCategoryName(repairOrderItemDTO.getBusinessCategoryName());
        materialBuffer.append(repairOrderItemDTO.getProductName());
        materialBuffer.append(";");
        itemDTOList.add(itemDTO);
      }
    }

    StringBuffer serviceBuffer=new StringBuffer();
    if(!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())){
      for(RepairOrderServiceDTO repairOrderServiceDTO:repairOrderDTO.getServiceDTOs()){
        if(StringUtil.isEmpty(repairOrderServiceDTO.getService())) continue;
        DraftOrderItemDTO itemDTO=new DraftOrderItemDTO();
        itemDTO.setShopId(this.getShopId());
        itemDTO.setServiceId(repairOrderServiceDTO.getServiceId());
        itemDTO.setService(repairOrderServiceDTO.getService());
        itemDTO.setWorkerIds(repairOrderServiceDTO.getWorkerIds());
        itemDTO.setWorkers(repairOrderServiceDTO.getWorkers());
        itemDTO.setTotal(repairOrderServiceDTO.getTotal());
        itemDTO.setConsumeType(repairOrderServiceDTO.getConsumeType());
        itemDTO.setItemTypes(ItemTypes.SERVICE);
        itemDTO.setBusinessCategoryId(repairOrderServiceDTO.getBusinessCategoryId());
        itemDTO.setBusinessCategoryName(repairOrderServiceDTO.getBusinessCategoryName());
        itemDTO.setStandardHours(repairOrderServiceDTO.getStandardHours());
        itemDTO.setStandardUnitPrice(repairOrderServiceDTO.getStandardUnitPrice());
        itemDTO.setActualHours(repairOrderServiceDTO.getActualHours());
        serviceBuffer.append(repairOrderServiceDTO.getService());
        serviceBuffer.append(";");
        itemDTOList.add(itemDTO);
      }
    }
    String materialTitle=materialBuffer.toString();
    if(materialTitle.length()>DraftOrderDTO.MAX_MATERIAL_LENGTH){
      this.setMaterial(materialTitle.substring(0,DraftOrderDTO.MAX_MATERIAL_LENGTH));
    } else {
      this.setMaterial(materialTitle);
    }
    this.setServiceContent(serviceBuffer.toString());
    this.setItemDTOs(itemDTOList.toArray(new DraftOrderItemDTO[itemDTOList.size()]));

    List<DraftOrderOtherIncomeItemDTO> otherIncomeItemDTOList = null;

    if(CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
      for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
        if (StringUtils.isBlank(itemDTO.getName())) {
          continue;
        }

        if (null == otherIncomeItemDTOList) {
          otherIncomeItemDTOList = new ArrayList<DraftOrderOtherIncomeItemDTO>();
        }
        DraftOrderOtherIncomeItemDTO otherIncomeItemDTO = new DraftOrderOtherIncomeItemDTO();
        otherIncomeItemDTO.setPrice(itemDTO.getPrice());
        otherIncomeItemDTO.setMemo(itemDTO.getMemo());
        otherIncomeItemDTO.setName(itemDTO.getName());
        otherIncomeItemDTO.setShopId(itemDTO.getShopId());

        otherIncomeItemDTO.setOtherIncomeCalculateWay(itemDTO.getOtherIncomeCalculateWay());
        otherIncomeItemDTO.setOtherIncomeRate(NumberUtil.doubleVal(itemDTO.getOtherIncomeRate()));

        if (itemDTO.getOtherIncomeCostPrice() == null) {
          otherIncomeItemDTO.setCalculateCostPrice(BooleanEnum.FALSE);
        } else {
          otherIncomeItemDTO.setCalculateCostPrice(BooleanEnum.TRUE);
        }
        otherIncomeItemDTO.setOtherIncomeCostPrice(NumberUtil.doubleVal(itemDTO.getOtherIncomeCostPrice()));

        otherIncomeItemDTOList.add(otherIncomeItemDTO);
      }
    }

    this.setOtherIncomeItemDTOList(otherIncomeItemDTOList);
    return this;
  }

  public  DraftOrderDTO fromSalesOrderDTO(SalesOrderDTO salesOrderDTO){
    this.setId(NumberUtil.longValue(salesOrderDTO.getDraftOrderIdStr()));
    this.setStorehouseId(salesOrderDTO.getStorehouseId());
    this.setCustomerOrSupplierId(salesOrderDTO.getCustomerId());
    this.setCustomerOrSupplierName(salesOrderDTO.getCustomer());
    this.setContact(salesOrderDTO.getContact());
    this.setContactId(salesOrderDTO.getContactId()); //add by zhuj
    this.setMobile(salesOrderDTO.getMobile());
    this.setVehicleId(salesOrderDTO.getVehicleId());
    this.setVehicle(salesOrderDTO.getLicenceNo());
    this.setVehicleContact(salesOrderDTO.getVehicleContact());
    this.setVehicleMobile(salesOrderDTO.getVehicleMobile());
    this.setAddress(salesOrderDTO.getAddress());
    this.setOrderTypeEnum(OrderTypes.SALE);
    this.setMemo(salesOrderDTO.getMemo());
    this.setEditDate(salesOrderDTO.getEditDate());
    this.setSettledAmount(salesOrderDTO.getSettledAmount());
    this.setDebt(salesOrderDTO.getDebt());
    this.setTotal(salesOrderDTO.getTotal());
    this.setBillProducerId(salesOrderDTO.getGoodsSalerId());
    this.setVestDateStr(salesOrderDTO.getVestDateStr());
    this.setShopLandLine(salesOrderDTO.getShopLandLine());
    this.setShopAddress(salesOrderDTO.getShopAddress());
    this.setShopName(salesOrderDTO.getShopName());
    this.setReceiptNo(salesOrderDTO.getReceiptNo());
    List<DraftOrderItemDTO> itemDTOList=new ArrayList<DraftOrderItemDTO>();
    StringBuffer materialBuffer=new StringBuffer();
    if(!ArrayUtils.isEmpty(salesOrderDTO.getItemDTOs())){
      for(SalesOrderItemDTO salesOrderItemDTO:salesOrderDTO.getItemDTOs() ){
        if(StringUtil.isAllEmpty(salesOrderItemDTO.getProductName(),salesOrderItemDTO.getBrand(),salesOrderItemDTO.getModel(),salesOrderItemDTO.getSpec(),salesOrderItemDTO.getVehicleBrand(),salesOrderItemDTO.getVehicleModel())) continue;
        DraftOrderItemDTO itemDTO=new DraftOrderItemDTO();
        itemDTO.setProductLocalInfoId(salesOrderItemDTO.getProductId());
        itemDTO.setBrand(salesOrderItemDTO.getBrand());
        itemDTO.setModel(salesOrderItemDTO.getModel());
        itemDTO.setSpec(salesOrderItemDTO.getSpec());
        itemDTO.setProductType(salesOrderItemDTO.getProductType());
        itemDTO.setProductName(salesOrderItemDTO.getProductName());
        itemDTO.setRate(salesOrderItemDTO.getRate());
        itemDTO.setSellUnit(salesOrderItemDTO.getSellUnit());
        itemDTO.setStorageUnit(salesOrderItemDTO.getStorageUnit());
        itemDTO.setUnit(salesOrderItemDTO.getUnit());
        itemDTO.setBusinessCategoryId(salesOrderItemDTO.getBusinessCategoryId());
        itemDTO.setBusinessCategoryName(salesOrderItemDTO.getBusinessCategoryName());
        itemDTO.setShopId(this.getShopId());
        itemDTO.setAmount(salesOrderItemDTO.getAmount());
        itemDTO.setPrice(salesOrderItemDTO.getPrice());
        itemDTO.setTotal(salesOrderItemDTO.getTotal());
        itemDTO.setMemo(salesOrderItemDTO.getMemo());
        itemDTO.setCostPrice(salesOrderItemDTO.getCostPrice());
        itemDTO.setTotalCostPrice(salesOrderItemDTO.getTotalCostPrice());
        itemDTO.setPercentage(salesOrderItemDTO.getPercentage());
        itemDTO.setPercentageAmount(salesOrderItemDTO.getPercentageAmount());
        itemDTO.setProductId(salesOrderItemDTO.getProductId());
        itemDTO.setProductIdStr(salesOrderItemDTO.getProductIdStr());
        itemDTO.setVehicleBrand(salesOrderItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(salesOrderItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(salesOrderItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(salesOrderItemDTO.getVehicleEngine());
        itemDTO.setRate(salesOrderItemDTO.getRate());
        itemDTO.setCommodityCode(salesOrderItemDTO.getCommodityCode());
        materialBuffer.append(salesOrderItemDTO.getProductName());
        materialBuffer.append(";");
        itemDTO.setUseAmountJson(toUserAmountJson(salesOrderItemDTO));
        itemDTOList.add(itemDTO);
      }
    }


    if(CollectionUtils.isNotEmpty(salesOrderDTO.getOtherIncomeItemDTOList())) {
      List<DraftOrderOtherIncomeItemDTO> orderOtherIncomeItemDTOList = new ArrayList<DraftOrderOtherIncomeItemDTO>();
      for (SalesOrderOtherIncomeItemDTO salesOrderOtherIncomeItemDTO : salesOrderDTO.getOtherIncomeItemDTOList()) {
        if (StringUtils.isBlank(salesOrderOtherIncomeItemDTO.getName())) {
          continue;
        }
        DraftOrderOtherIncomeItemDTO orderOtherIncomeItemDTO = new DraftOrderOtherIncomeItemDTO();
        orderOtherIncomeItemDTO.setShopId(salesOrderOtherIncomeItemDTO.getShopId());
        orderOtherIncomeItemDTO.setMemo(salesOrderOtherIncomeItemDTO.getMemo());
        orderOtherIncomeItemDTO.setName(salesOrderOtherIncomeItemDTO.getName());
        orderOtherIncomeItemDTO.setPrice(salesOrderOtherIncomeItemDTO.getPrice());

        orderOtherIncomeItemDTO.setOtherIncomeCalculateWay(salesOrderOtherIncomeItemDTO.getOtherIncomeCalculateWay());
        orderOtherIncomeItemDTO.setOtherIncomeRate(NumberUtil.doubleVal(salesOrderOtherIncomeItemDTO.getOtherIncomeRate()));

        if (salesOrderOtherIncomeItemDTO.getOtherIncomeCostPrice() == null) {
          orderOtherIncomeItemDTO.setCalculateCostPrice(BooleanEnum.FALSE);
        } else {
          orderOtherIncomeItemDTO.setCalculateCostPrice(BooleanEnum.TRUE);
        }
        orderOtherIncomeItemDTO.setOtherIncomeCostPrice(NumberUtil.doubleVal(salesOrderOtherIncomeItemDTO.getOtherIncomeCostPrice()));
        orderOtherIncomeItemDTOList.add(orderOtherIncomeItemDTO);
      }
      this.setOtherIncomeItemDTOList(orderOtherIncomeItemDTOList);
    }
    String materialTitle=materialBuffer.toString();
    if(materialTitle.length()>DraftOrderDTO.MAX_MATERIAL_LENGTH){
      this.setMaterial(materialTitle.substring(0,DraftOrderDTO.MAX_MATERIAL_LENGTH));
    } else {
      this.setMaterial(materialTitle);
    }
    this.setItemDTOs(itemDTOList.toArray(new DraftOrderItemDTO[itemDTOList.size()]));
    return this;
  }

  /**
   * 供应商打通，商品下拉的供应商信息
   * @param itemDto
   * @return
   */
  private String toUserAmountJson(BcgogoOrderItemDto itemDto){
    Map<String,Double> useAmountMap=null;
    OutStorageRelationDTO[] outStorageRelationDTOs=itemDto.getOutStorageRelationDTOs();
    if(!ArrayUtil.isEmpty(outStorageRelationDTOs)){
      useAmountMap=new HashMap<String,Double>();
      for(OutStorageRelationDTO relationDTO:outStorageRelationDTOs){
        if(relationDTO==null||relationDTO.getRelatedSupplierId()==null){
          LOG.warn("打通版本供应商信息异常！");
        }
        if(relationDTO.getUseRelatedAmount()!=null&&relationDTO.getUseRelatedAmount()>0.001d)
          useAmountMap.put(ObjectUtil.generateKey(relationDTO.getRelatedSupplierId(),relationDTO.getSupplierType()),relationDTO.getUseRelatedAmount());
      }
      if(useAmountMap.keySet().size()>0){
        return JsonUtil.mapToJson(useAmountMap);
      }
    }
    return null;
  }

  public  DraftOrderDTO fromSalesReturnDTO(SalesReturnDTO salesReturnDTO){
    this.setId(NumberUtil.longValue(salesReturnDTO.getDraftOrderIdStr()));
    this.setStorehouseId(salesReturnDTO.getStorehouseId());
    this.setCustomerOrSupplierId(salesReturnDTO.getCustomerId());
    this.setCustomerOrSupplierName(salesReturnDTO.getCustomerStr());
    this.setContact(salesReturnDTO.getContact());
    this.setContactId(salesReturnDTO.getContactId()); // add by zhuj
    this.setMobile(salesReturnDTO.getMobile());
    this.setAddress(salesReturnDTO.getAddress());
    this.setOrderTypeEnum(OrderTypes.SALE_RETURN);
    this.setMemo(salesReturnDTO.getMemo());
    this.setEditDate(salesReturnDTO.getEditDate());
    this.setSettledAmount(salesReturnDTO.getSettledAmount());
//    this.setDebt(salesReturnDTO.getDebt());
    this.setTotal(salesReturnDTO.getTotal());
    this.setBillProducerId(salesReturnDTO.getSalesReturnerId());
    this.setBillProducer(salesReturnDTO.getSalesReturner());
    this.setVestDateStr(salesReturnDTO.getVestDateStr());
    this.setOriginalOrderId(salesReturnDTO.getOriginOrderId());
    this.setOriginalOrderType(salesReturnDTO.getOriginOrderType());
    this.setOriginalReceiptNo(salesReturnDTO.getOriginReceiptNo());
    if(salesReturnDTO.getReadOnly()!=null&&salesReturnDTO.getReadOnly()){
      this.setEditStatus(DraftOrderStatus.DRAFT_READ_ONLY);
    }else {
      this.setEditStatus(DraftOrderStatus.DRAFT_READ_WRITE);
    }
    List<DraftOrderItemDTO> itemDTOList=new ArrayList<DraftOrderItemDTO>();
    StringBuffer materialBuffer=new StringBuffer();
    if(ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())){
      return this;
    }
    this.setReceiptNo(salesReturnDTO.getReceiptNo());
    for(SalesReturnItemDTO salesReturnItemDTO:salesReturnDTO.getItemDTOs() ){
      if(StringUtil.isAllEmpty(salesReturnItemDTO.getProductName(),salesReturnItemDTO.getBrand(),salesReturnItemDTO.getModel(),salesReturnItemDTO.getSpec(),salesReturnItemDTO.getVehicleBrand(),salesReturnItemDTO.getVehicleModel())) continue;
      DraftOrderItemDTO itemDTO=new DraftOrderItemDTO();
      itemDTO.setProductLocalInfoId(salesReturnItemDTO.getProductId());
      itemDTO.setBrand(salesReturnItemDTO.getBrand());
      itemDTO.setModel(salesReturnItemDTO.getModel());
      itemDTO.setSpec(salesReturnItemDTO.getSpec());
//      itemDTO.setProductType(salesReturnItemDTO.getProductType());
      itemDTO.setProductName(salesReturnItemDTO.getProductName());
      itemDTO.setRate(salesReturnItemDTO.getRate());
      itemDTO.setSellUnit(salesReturnItemDTO.getSellUnit());
      itemDTO.setStorageUnit(salesReturnItemDTO.getStorageUnit());
      itemDTO.setUnit(salesReturnItemDTO.getUnit());
      itemDTO.setBusinessCategoryId(salesReturnItemDTO.getBusinessCategoryId());
      itemDTO.setBusinessCategoryName(salesReturnItemDTO.getBusinessCategoryName());
      itemDTO.setShopId(this.getShopId());

      itemDTO.setAmount(salesReturnItemDTO.getOriginSaleAmount());
      itemDTO.setPrice(salesReturnItemDTO.getOriginSalesPrice());
      itemDTO.setReturnAmount(salesReturnItemDTO.getAmount());
      itemDTO.setReturnPrice(salesReturnItemDTO.getPrice());

      itemDTO.setCostPrice(salesReturnItemDTO.getCostPrice());
      itemDTO.setTotalCostPrice(salesReturnItemDTO.getTotalCostPrice());
      itemDTO.setTotal(salesReturnItemDTO.getTotal());
      itemDTO.setPurchasePriceForSale(salesReturnItemDTO.getPurchasePrice());

      itemDTO.setProductId(salesReturnItemDTO.getProductId());
      itemDTO.setProductIdStr(salesReturnItemDTO.getProductIdStr());
      itemDTO.setVehicleBrand(salesReturnItemDTO.getVehicleBrand());
      itemDTO.setVehicleModel(salesReturnItemDTO.getVehicleModel());
      itemDTO.setVehicleYear(salesReturnItemDTO.getVehicleYear());
      itemDTO.setVehicleEngine(salesReturnItemDTO.getVehicleEngine());
      itemDTO.setRate(salesReturnItemDTO.getRate());
      itemDTO.setCommodityCode(salesReturnItemDTO.getCommodityCode());
      itemDTO.setMemo(salesReturnItemDTO.getMemo());
      itemDTO.setUseAmountJson(toUserAmountJson(salesReturnItemDTO));
      materialBuffer.append(salesReturnItemDTO.getProductName());
      materialBuffer.append(";");
      itemDTOList.add(itemDTO);
    }
    String materialTitle=materialBuffer.toString();
    if(materialTitle.length()>DraftOrderDTO.MAX_MATERIAL_LENGTH){
      this.setMaterial(materialTitle.substring(0,DraftOrderDTO.MAX_MATERIAL_LENGTH));
    } else {
      this.setMaterial(materialTitle);
    }
    this.setItemDTOs(itemDTOList.toArray(new DraftOrderItemDTO[itemDTOList.size()]));
    return this;
  }


  public PurchaseInventoryDTO toPurchaseInventoryDTO(){
    PurchaseInventoryDTO purchaseInventoryDTO=new PurchaseInventoryDTO();
    purchaseInventoryDTO.setStorehouseId(this.getStorehouseId());
    purchaseInventoryDTO.setSupplierId(this.getCustomerOrSupplierId());
    purchaseInventoryDTO.setSupplier(this.getCustomerOrSupplierName());
    purchaseInventoryDTO.setContact(this.getContact());
    purchaseInventoryDTO.setContactId(this.getContactId());  // add by zhuj
    if (this.getContactId() != null) {
      purchaseInventoryDTO.setContactIdStr(String.valueOf(this.getContactId()));
    }
    purchaseInventoryDTO.setMobile(this.getMobile());
    purchaseInventoryDTO.setAddress(this.getAddress());
    purchaseInventoryDTO.setLandline(this.getLandLine());
    purchaseInventoryDTO.setBank(this.getBank());
    purchaseInventoryDTO.setAccount(this.getAccount());
    purchaseInventoryDTO.setBusinessScope(this.getBusinessScope());
    purchaseInventoryDTO.setAccountName(this.getAccountName());
    purchaseInventoryDTO.setCategory(this.getCategory());
    purchaseInventoryDTO.setAbbr(this.getAbbr());
    purchaseInventoryDTO.setSettlementType(this.getSettlementType());
    purchaseInventoryDTO.setQq(this.getQq());
    purchaseInventoryDTO.setFax(this.getFax());
    purchaseInventoryDTO.setEmail(this.getEmail());
    purchaseInventoryDTO.setInvoiceCategory(this.getInvoiceCategory());

    purchaseInventoryDTO.setMemo(this.getMemo());
    purchaseInventoryDTO.setEditDate(this.getEditDate());
    purchaseInventoryDTO.setStroageActuallyPaid(this.getSettledAmount());
    purchaseInventoryDTO.setStroageCreditAmount(this.getDebt());
    purchaseInventoryDTO.setTotal(this.getTotal());
    purchaseInventoryDTO.setAcceptorId(this.getBillProducerId());
    purchaseInventoryDTO.setAcceptor(this.getBillProducer());
    purchaseInventoryDTO.setDraftOrderIdStr(this.getIdStr());
    purchaseInventoryDTO.setVestDateStr(this.getVestDateStr());
    purchaseInventoryDTO.setShopLandLine(this.getShopLandLine());
    purchaseInventoryDTO.setShopAddress(this.getShopAddress());
    purchaseInventoryDTO.setShopName(this.getShopName());
    List<PurchaseInventoryItemDTO> itemDTOList=new ArrayList<PurchaseInventoryItemDTO>();
    if(!ArrayUtils.isEmpty(this.getItemDTOs())){
      for(DraftOrderItemDTO draftOrderItemDTO:this.getItemDTOs() ){
        PurchaseInventoryItemDTO itemDTO=new PurchaseInventoryItemDTO();
        itemDTO.setBrand(draftOrderItemDTO.getBrand());
        itemDTO.setModel(draftOrderItemDTO.getModel());
        itemDTO.setSpec(draftOrderItemDTO.getSpec());
        itemDTO.setProductName(draftOrderItemDTO.getProductName());
        itemDTO.setProductType(String.valueOf(draftOrderItemDTO.getProductType()));
        itemDTO.setVehicleBrand(draftOrderItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(draftOrderItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(draftOrderItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(draftOrderItemDTO.getVehicleEngine());
        itemDTO.setSellUnit(draftOrderItemDTO.getSellUnit());
        itemDTO.setStorageUnit(draftOrderItemDTO.getStorageUnit());
        itemDTO.setRate(draftOrderItemDTO.getRate());
        itemDTO.setInventoryAmount(draftOrderItemDTO.getInventoryAmount());
        itemDTO.setAmount(draftOrderItemDTO.getAmount());
        itemDTO.setPurchasePrice(draftOrderItemDTO.getPrice());
        itemDTO.setTotal(draftOrderItemDTO.getTotal());
        itemDTO.setMemo(draftOrderItemDTO.getMemo());
        itemDTO.setPrice(draftOrderItemDTO.getPrice());
        itemDTO.setProductId(draftOrderItemDTO.getProductLocalInfoId());
        itemDTO.setProductIdStr(draftOrderItemDTO.getProductIdStr());
        itemDTO.setUpperLimit(draftOrderItemDTO.getUpperLimit());
        itemDTO.setLowerLimit(draftOrderItemDTO.getLowerLimit());
        itemDTO.setRecommendedPrice(draftOrderItemDTO.getRecommendedPrice());
        itemDTO.setUnit(draftOrderItemDTO.getUnit());
        itemDTO.setStorageBin(draftOrderItemDTO.getStorageBin());
        itemDTO.setTradePrice(draftOrderItemDTO.getTradePrice());
        itemDTO.setCommodityCode(draftOrderItemDTO.getCommodityCode());
        itemDTO.setProductKindId(draftOrderItemDTO.getProductKindId());
        itemDTO.setProductKind(draftOrderItemDTO.getProductKind());
        itemDTOList.add(itemDTO);
      }
    }
    purchaseInventoryDTO.setItemDTOs(itemDTOList.toArray(new PurchaseInventoryItemDTO[itemDTOList.size()]));
    purchaseInventoryDTO.setReceiptNo(this.getReceiptNo());
    return purchaseInventoryDTO;
  }

  public PurchaseOrderDTO toPurchaseOrderDTO(){
    PurchaseOrderDTO purchaseOrderDTO=new PurchaseOrderDTO();
    purchaseOrderDTO.setStorehouseId(this.getStorehouseId());
    purchaseOrderDTO.setSupplierShopId(this.getCustomerOrSupplierShopId());
    purchaseOrderDTO.setSupplierId(this.getCustomerOrSupplierId());
    purchaseOrderDTO.setSupplier(this.getCustomerOrSupplierName());
    purchaseOrderDTO.setContact(this.getContact());
    purchaseOrderDTO.setContactId(this.getContactId());
    if (this.getContactId() != null) {
      purchaseOrderDTO.setContactIdStr(String.valueOf(this.getContactId()));
    }
    purchaseOrderDTO.setMobile(this.getMobile());
    purchaseOrderDTO.setLandline(this.getLandLine());
    purchaseOrderDTO.setAddress(this.getAddress());
    purchaseOrderDTO.setBank(this.getBank());
    purchaseOrderDTO.setAccount(this.getAccount());
    purchaseOrderDTO.setBusinessScope(this.getBusinessScope());
    purchaseOrderDTO.setAccountName(this.getAccountName());
    purchaseOrderDTO.setCategory(this.getCategory());
    purchaseOrderDTO.setAbbr(this.getAbbr());
    purchaseOrderDTO.setSettlementType(this.getSettlementType());
    purchaseOrderDTO.setQq(this.getQq());
    purchaseOrderDTO.setFax(this.getFax());
    purchaseOrderDTO.setEmail(this.getEmail());
    purchaseOrderDTO.setInvoiceCategory(this.getInvoiceCategory());

    purchaseOrderDTO.setMemo(this.getMemo());
    purchaseOrderDTO.setEditDate(this.getEditDate());
    purchaseOrderDTO.setBillProducerId(this.getBillProducerId());
    purchaseOrderDTO.setBillProducer(this.getBillProducer());
    purchaseOrderDTO.setDeliveryDateStr(this.getDeliveryDateStr());
    purchaseOrderDTO.setDraftOrderIdStr(this.getIdStr());
    purchaseOrderDTO.setVestDateStr(this.getVestDateStr());
    purchaseOrderDTO.setTotal(this.getTotal());
    purchaseOrderDTO.setShopLandLine(this.getShopLandLine());
    purchaseOrderDTO.setShopName(this.getShopName());
    purchaseOrderDTO.setShopAddress(this.getShopAddress());
    purchaseOrderDTO.setReceiptNo(this.getReceiptNo());
    List<PurchaseOrderItemDTO> itemDTOList=new ArrayList<PurchaseOrderItemDTO>();
    if(!ArrayUtils.isEmpty(this.getItemDTOs())){
      for(DraftOrderItemDTO draftOrderItemDTO:this.getItemDTOs() ){
        PurchaseOrderItemDTO itemDTO=new PurchaseOrderItemDTO();
        itemDTO.setBrand(draftOrderItemDTO.getBrand());
        itemDTO.setModel(draftOrderItemDTO.getModel());
        itemDTO.setSpec(draftOrderItemDTO.getSpec());
        itemDTO.setProductName(draftOrderItemDTO.getProductName());
        itemDTO.setProductType(String.valueOf(draftOrderItemDTO.getProductType()));
        itemDTO.setVehicleBrand(draftOrderItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(draftOrderItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(draftOrderItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(draftOrderItemDTO.getVehicleEngine());
        itemDTO.setSellUnit(draftOrderItemDTO.getSellUnit());
        itemDTO.setStorageUnit(draftOrderItemDTO.getStorageUnit());
        itemDTO.setRate(draftOrderItemDTO.getRate());
        itemDTO.setInventoryAmount(draftOrderItemDTO.getInventoryAmount());
        itemDTO.setAmount(draftOrderItemDTO.getAmount());
        itemDTO.setQuotedPrice(draftOrderItemDTO.getPrice());
        itemDTO.setPrice(draftOrderItemDTO.getPrice());
        itemDTO.setTotal(draftOrderItemDTO.getTotal());
        itemDTO.setMemo(draftOrderItemDTO.getMemo());
        itemDTO.setProductId(draftOrderItemDTO.getProductLocalInfoId());
        itemDTO.setSupplierProductId(draftOrderItemDTO.getSupplierProductLocalInfoId());
        itemDTO.setProductIdStr(draftOrderItemDTO.getProductIdStr());
        itemDTO.setLowerLimit(draftOrderItemDTO.getLowerLimit());
        itemDTO.setUpperLimit(draftOrderItemDTO.getUpperLimit());
        itemDTO.setUnit(draftOrderItemDTO.getUnit());
        itemDTO.setStorageBin(draftOrderItemDTO.getStorageBin());
        itemDTO.setTradePrice(draftOrderItemDTO.getTradePrice());
        itemDTO.setCommodityCode(draftOrderItemDTO.getCommodityCode());
        itemDTO.setProductKind(draftOrderItemDTO.getProductKind());
        itemDTO.setProductKindId(draftOrderItemDTO.getProductKindId());
        itemDTOList.add(itemDTO);
      }
    }
    purchaseOrderDTO.setItemDTOs(itemDTOList.toArray(new PurchaseOrderItemDTO[itemDTOList.size()]));
    return purchaseOrderDTO;
  }

  public SalesOrderDTO toSalesOrderDTO(){
    SalesOrderDTO salesOrderDTO=new SalesOrderDTO();
    salesOrderDTO.setStorehouseId(this.getStorehouseId());
    salesOrderDTO.setCustomerId(this.getCustomerOrSupplierId());
    salesOrderDTO.setCustomer(this.getCustomerOrSupplierName());
    salesOrderDTO.setContact(this.getContact());
    salesOrderDTO.setContactId(this.getContactId()); // add by zhuj
    salesOrderDTO.setMobile(this.getMobile());
    salesOrderDTO.setVehicleId(this.getVehicleId());
    salesOrderDTO.setLicenceNo(this.getVehicle());
    salesOrderDTO.setVehicleContact(this.getVehicleContact());
    salesOrderDTO.setVehicleMobile(this.getVehicleMobile());
    salesOrderDTO.setAddress(this.getAddress());
    salesOrderDTO.setFax(this.getFax());
    salesOrderDTO.setMemo(this.getMemo());
    salesOrderDTO.setEditDate(this.getEditDate());
    salesOrderDTO.setSettledAmount(this.getSettledAmount());
    salesOrderDTO.setDebt(this.getDebt());
    salesOrderDTO.setTotal(this.getTotal());
    salesOrderDTO.setGoodsSalerId(this.getBillProducerId());
    salesOrderDTO.setGoodsSaler(this.getBillProducer());
    salesOrderDTO.setDraftOrderIdStr(this.getIdStr());
    salesOrderDTO.setVestDateStr(this.getVestDateStr());
    salesOrderDTO.setShopLandLine(this.getShopLandLine());
    salesOrderDTO.setShopAddress(this.getShopAddress());
    salesOrderDTO.setShopName(this.getShopName());
    salesOrderDTO.setReceiptNo(this.getReceiptNo());
    List<SalesOrderItemDTO> itemDTOList=new ArrayList<SalesOrderItemDTO>();
    if(!ArrayUtils.isEmpty(this.getItemDTOs())){
      for(DraftOrderItemDTO draftOrderItemDTO:this.getItemDTOs() ){
        SalesOrderItemDTO itemDTO=new SalesOrderItemDTO();
        itemDTO.setShopId(this.getShopId());
        itemDTO.setBrand(draftOrderItemDTO.getBrand());
        itemDTO.setModel(draftOrderItemDTO.getModel());
        itemDTO.setSpec(draftOrderItemDTO.getSpec());
        itemDTO.setProductName(draftOrderItemDTO.getProductName());
        itemDTO.setProductType(String.valueOf(draftOrderItemDTO.getProductType()));
        itemDTO.setVehicleBrand(draftOrderItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(draftOrderItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(draftOrderItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(draftOrderItemDTO.getVehicleEngine());
        itemDTO.setSellUnit(draftOrderItemDTO.getSellUnit());
        itemDTO.setStorageUnit(draftOrderItemDTO.getStorageUnit());
        itemDTO.setRate(draftOrderItemDTO.getRate());
        itemDTO.setInventoryAmount(draftOrderItemDTO.getInventoryAmount());
        itemDTO.setPurchasePrice(draftOrderItemDTO.getPurchasePriceForSale());
        itemDTO.setAmount(draftOrderItemDTO.getAmount());
        itemDTO.setQuotedPrice(draftOrderItemDTO.getPrice());
        itemDTO.setPrice(draftOrderItemDTO.getPrice());
        itemDTO.setTotal(draftOrderItemDTO.getTotal());
        itemDTO.setMemo(draftOrderItemDTO.getMemo());
        itemDTO.setCostPrice(draftOrderItemDTO.getCostPrice());
        itemDTO.setTotalCostPrice(draftOrderItemDTO.getTotalCostPrice());
        itemDTO.setPercentage(draftOrderItemDTO.getPercentage());
        itemDTO.setPercentageAmount(draftOrderItemDTO.getPercentageAmount());
        itemDTO.setProductId(draftOrderItemDTO.getProductLocalInfoId());
        itemDTO.setProductIdStr(draftOrderItemDTO.getProductIdStr());
        itemDTO.setUnit(draftOrderItemDTO.getUnit());
        itemDTO.setStorageBin(draftOrderItemDTO.getStorageBin());
        itemDTO.setCommodityCode(draftOrderItemDTO.getCommodityCode());
        itemDTO.setOutStorageRelationDTOs(draftOrderItemDTO.getOutStorageRelationDTOs());
        itemDTOList.add(itemDTO);
      }
    }

    if(CollectionUtils.isNotEmpty(this.getOtherIncomeItemDTOList())) {
      List<SalesOrderOtherIncomeItemDTO> incomeItemDTOList = new ArrayList<SalesOrderOtherIncomeItemDTO>();

      for (DraftOrderOtherIncomeItemDTO itemDTO : this.getOtherIncomeItemDTOList()) {
        SalesOrderOtherIncomeItemDTO incomeItemDTO = new SalesOrderOtherIncomeItemDTO();
        incomeItemDTO.setMemo(itemDTO.getMemo());
        incomeItemDTO.setName(itemDTO.getName());
        incomeItemDTO.setPrice(itemDTO.getPrice());
        incomeItemDTO.setShopId(itemDTO.getShopId());
        incomeItemDTO.setCalculateCostPrice(itemDTO.getCalculateCostPrice() == null ? null : itemDTO.getCalculateCostPrice().name());
        incomeItemDTO.setOtherIncomeCostPrice(itemDTO.getOtherIncomeCostPrice());
        incomeItemDTO.setOtherIncomeCalculateWay(itemDTO.getOtherIncomeCalculateWay());
        incomeItemDTO.setOtherIncomeRate(itemDTO.getOtherIncomeRate());
        incomeItemDTOList.add(incomeItemDTO);
      }

      salesOrderDTO.setOtherIncomeItemDTOList(incomeItemDTOList);
    }

    salesOrderDTO.setItemDTOs(itemDTOList.toArray(new SalesOrderItemDTO[itemDTOList.size()]));
    return salesOrderDTO;
  }

  public SalesReturnDTO toSalesReturnDTO(){
    SalesReturnDTO salesReturnDTO=new SalesReturnDTO();
    salesReturnDTO.setStorehouseId(this.getStorehouseId());
    salesReturnDTO.setShopId(this.getShopId());
    salesReturnDTO.setCustomerId(this.getCustomerOrSupplierId());
    salesReturnDTO.setCustomer(this.getCustomerOrSupplierName());
    salesReturnDTO.setContact(this.getContact());
    salesReturnDTO.setContactId(this.getContactId()); // add by zhuj
    if (this.getContactId() != null) {
      salesReturnDTO.setContactIdStr(String.valueOf(this.getContactId()));
    }
    salesReturnDTO.setMobile(this.getMobile());
    salesReturnDTO.setAddress(this.getAddress());
    salesReturnDTO.setMemo(this.getMemo());
    salesReturnDTO.setEditDate(this.getEditDate());
    salesReturnDTO.setSettledAmount(this.getSettledAmount());
    salesReturnDTO.setTotal(this.getTotal());
    salesReturnDTO.setSalesReturnerId(this.getBillProducerId());
    salesReturnDTO.setSalesReturner(this.getBillProducer());
    salesReturnDTO.setDraftOrderIdStr(this.getIdStr());
    salesReturnDTO.setVestDateStr(this.getVestDateStr());
    salesReturnDTO.setOriginOrderId(this.getOriginalOrderId());
    salesReturnDTO.setOriginOrderType(this.getOriginalOrderType());
    salesReturnDTO.setOriginReceiptNo(this.getOriginalReceiptNo());
    salesReturnDTO.setReceiptNo(this.getReceiptNo());
    List<SalesReturnItemDTO> itemDTOList=new ArrayList<SalesReturnItemDTO>();
    if(!ArrayUtils.isEmpty(this.getItemDTOs())){
      for(DraftOrderItemDTO draftOrderItemDTO:this.getItemDTOs() ){
        SalesReturnItemDTO itemDTO=new SalesReturnItemDTO();
        //      itemDTO.setShopId(this.getShopId());
        itemDTO.setBrand(draftOrderItemDTO.getBrand());
        itemDTO.setModel(draftOrderItemDTO.getModel());
        itemDTO.setSpec(draftOrderItemDTO.getSpec());
        itemDTO.setProductName(draftOrderItemDTO.getProductName());
        itemDTO.setVehicleBrand(draftOrderItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(draftOrderItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(draftOrderItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(draftOrderItemDTO.getVehicleEngine());
        itemDTO.setStorageUnit(draftOrderItemDTO.getStorageUnit());
        itemDTO.setRate(draftOrderItemDTO.getRate());
        itemDTO.setInventoryAmount(draftOrderItemDTO.getInventoryAmount());
        itemDTO.setPurchasePrice(draftOrderItemDTO.getPurchasePriceForSale());
        itemDTO.setSellUnit(draftOrderItemDTO.getSellUnit());     //原始销售单位
        itemDTO.setUnit(draftOrderItemDTO.getUnit());   //退货单位
        if(StringUtil.isNotEmpty(draftOrderItemDTO.getSellUnit())&&draftOrderItemDTO.getAmount()!=null){
          itemDTO.setOriginSaleAmountStr(String.valueOf(draftOrderItemDTO.getAmount())+draftOrderItemDTO.getSellUnit());
        }else {
          itemDTO.setOriginSaleAmountStr(String.valueOf(draftOrderItemDTO.getAmount()));
        }
        itemDTO.setOriginSalesPrice(draftOrderItemDTO.getPrice());
        itemDTO.setOriginSaleAmount(draftOrderItemDTO.getAmount());
        itemDTO.setOriginSaleTotal(itemDTO.getOriginSalesPrice()*itemDTO.getOriginSaleAmount());
        itemDTO.setPrice(draftOrderItemDTO.getReturnPrice());
        itemDTO.setAmount(draftOrderItemDTO.getReturnAmount());
        itemDTO.setTotal(draftOrderItemDTO.getTotal());
        itemDTO.setMemo(draftOrderItemDTO.getMemo());
        itemDTO.setCostPrice(draftOrderItemDTO.getCostPrice());
        itemDTO.setTotalCostPrice(draftOrderItemDTO.getTotalCostPrice());
        itemDTO.setProductId(draftOrderItemDTO.getProductLocalInfoId());
        itemDTO.setProductIdStr(draftOrderItemDTO.getProductIdStr());
        itemDTO.setUnit(draftOrderItemDTO.getUnit());
        itemDTO.setStorageBin(draftOrderItemDTO.getStorageBin());
        itemDTO.setCommodityCode(draftOrderItemDTO.getCommodityCode());
        itemDTO.setBusinessCategoryId(draftOrderItemDTO.getBusinessCategoryId());
        itemDTO.setBusinessCategoryName(draftOrderItemDTO.getBusinessCategoryName());
        itemDTO.setOutStorageRelationDTOs(draftOrderItemDTO.getOutStorageRelationDTOs());
        itemDTOList.add(itemDTO);
      }
    }
    salesReturnDTO.setItemDTOs(itemDTOList.toArray(new SalesReturnItemDTO[itemDTOList.size()]));
    return salesReturnDTO;
  }


  public RepairOrderDTO toRepairOrderDTO() {
    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    repairOrderDTO.setStorehouseId(this.getStorehouseId());
    repairOrderDTO.setId(this.getTxnOrderId());
    repairOrderDTO.setDraftOrderIdStr(this.getId().toString());
    repairOrderDTO.setStatus(this.getTxnOrderStatus());
    repairOrderDTO.setCustomerId(this.getCustomerOrSupplierId());
    repairOrderDTO.setCustomerName(this.getCustomerOrSupplierName());
    repairOrderDTO.setContact(this.getContact());
    repairOrderDTO.setContactId(this.getContactId()); // add by zhuj
    if (this.getContactId() != null) {
      repairOrderDTO.setContactIdStr(String.valueOf(this.getContactId()));
    }
    repairOrderDTO.setMobile(this.getMobile());
    repairOrderDTO.setServiceType(OrderTypes.REPAIR);
    repairOrderDTO.setLandLine(this.getLandLine());
    repairOrderDTO.setStartDate(this.getStartDate());
    repairOrderDTO.setEndDate(this.getEndDate());
    repairOrderDTO.setStartMileage(this.getStartMileage() == null ? 0d : this.getStartMileage());
    repairOrderDTO.setEndMileage(this.getEndMileage() == null ? 0d : this.getEndMileage());
    repairOrderDTO.setFuelNumber(this.getFuelNumber());
    repairOrderDTO.setProductSaler(this.getProductSales());
    repairOrderDTO.setDebt(this.getDebt() == null ? 0d : this.getDebt());
    repairOrderDTO.setDept(this.getDebt() == null ? "0" : this.getDebt().toString());
    repairOrderDTO.setHuankuanTime(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, this.getRepaymentTime()));
    repairOrderDTO.setMemo(this.getMemo());
    repairOrderDTO.setEditDate(this.getEditDate());
    repairOrderDTO.setSettledAmount(this.getSettledAmount() == null ? 0d : this.getSettledAmount());
    repairOrderDTO.setVechicle(StringUtils.isBlank(this.getVehicle()) ? null : this.getVehicle());
    repairOrderDTO.setVechicleId(this.getVehicleId());
    repairOrderDTO.setBrand(this.getBrand());
    repairOrderDTO.setVehicleContact(this.getVehicleContact());
    repairOrderDTO.setVehicleMobile(this.getVehicleMobile());
    repairOrderDTO.setVehicleColor(this.getColor());
    repairOrderDTO.setVehicleEngineNo(this.getEngineNo());
    repairOrderDTO.setVehicleChassisNo(this.getChassisNo());
    repairOrderDTO.setModel(this.getModel());
    repairOrderDTO.setTotal(this.getTotal() == null ? 0d : this.getTotal());
    repairOrderDTO.setMaintainTimeStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", this.getMaintainTime()));
    repairOrderDTO.setInsureTimeStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", this.getInsureTime()));
    repairOrderDTO.setExamineTimeStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", this.getExamineTime()));
    repairOrderDTO.setMaintainMileage(getMaintainMileage());
    repairOrderDTO.setShopLandLine(this.getShopLandLine());
    repairOrderDTO.setShopName(this.getShopName());
    repairOrderDTO.setShopAddress(this.getShopAddress());
    repairOrderDTO.setDescription(this.getDescription());
    repairOrderDTO.setAppointOrderId(this.getAppointOrderId());
    repairOrderDTO.setVehicleHandover(this.getVehicleHandover());
    repairOrderDTO.setVehicleHandoverId(this.getVehicleHandoverId());
    repairOrderDTO.setConsumingRecordId(this.getConsumingRecordId());
    RepairOrderItemDTO repairOrderItemDTO = null;
    RepairOrderServiceDTO repairOrderServiceDTO = null;
    List<RepairOrderItemDTO> itemDTOList = new ArrayList<RepairOrderItemDTO>();
    List<RepairOrderServiceDTO> serviceDTOList = new ArrayList<RepairOrderServiceDTO>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (DraftOrderItemDTO draftOrderItemDTO : this.getItemDTOs()) {
        if (ItemTypes.SERVICE.equals(draftOrderItemDTO.getItemTypes())) {
          repairOrderServiceDTO = new RepairOrderServiceDTO();
          repairOrderServiceDTO.setId(draftOrderItemDTO.getItemId());
          repairOrderServiceDTO.setShopId(draftOrderItemDTO.getShopId());
          repairOrderServiceDTO.setServiceId(draftOrderItemDTO.getServiceId());
          repairOrderServiceDTO.setService(draftOrderItemDTO.getService());
          repairOrderServiceDTO.setWorkerIds(draftOrderItemDTO.getWorkerIds());
          repairOrderServiceDTO.setWorkers(draftOrderItemDTO.getWorkers());
          repairOrderServiceDTO.setConsumeType(draftOrderItemDTO.getConsumeType());
          repairOrderServiceDTO.setTotal(draftOrderItemDTO.getTotal());
          repairOrderServiceDTO.setRepairOrderId(repairOrderDTO.getId());
          repairOrderServiceDTO.setActualHours(draftOrderItemDTO.getActualHours());
          repairOrderServiceDTO.setStandardHours(draftOrderItemDTO.getStandardHours());
          repairOrderServiceDTO.setStandardUnitPrice(draftOrderItemDTO.getStandardUnitPrice());
          serviceDTOList.add(repairOrderServiceDTO);
        } else {
          repairOrderItemDTO = new RepairOrderItemDTO();
          repairOrderItemDTO.setRepairOrderId(repairOrderDTO.getId());
          repairOrderItemDTO.setId(draftOrderItemDTO.getItemId());
          repairOrderItemDTO.setShopId(draftOrderItemDTO.getShopId());
          repairOrderItemDTO.setAmount(draftOrderItemDTO.getAmount());
          repairOrderItemDTO.setPrice(draftOrderItemDTO.getPrice());
          repairOrderItemDTO.setTotal(draftOrderItemDTO.getTotal());
          repairOrderItemDTO.setMemo(draftOrderItemDTO.getMemo());
          repairOrderItemDTO.setReserved(draftOrderItemDTO.getReserved());
          repairOrderItemDTO.setCostPrice(draftOrderItemDTO.getCostPrice());
          repairOrderItemDTO.setTotalCostPrice(draftOrderItemDTO.getTotalCostPrice());
          repairOrderItemDTO.setPercentage(draftOrderItemDTO.getPercentage());
          repairOrderItemDTO.setPercentageAmount(draftOrderItemDTO.getPercentageAmount());
          repairOrderItemDTO.setInventoryAmount(draftOrderItemDTO.getInventoryAmount());
          repairOrderItemDTO.setBrand(draftOrderItemDTO.getBrand());
          repairOrderItemDTO.setModel(draftOrderItemDTO.getModel());
          repairOrderItemDTO.setSpec(draftOrderItemDTO.getSpec());
          if (NumberUtil.isNumber(draftOrderItemDTO.getProductType())) {
            repairOrderItemDTO.setProductType(NumberUtil.intValue(draftOrderItemDTO.getProductType()));
          }

          repairOrderItemDTO.setProductId(draftOrderItemDTO.getProductId());
          repairOrderItemDTO.setProductIdStr(draftOrderItemDTO.getProductIdStr());
          repairOrderItemDTO.setUnit(draftOrderItemDTO.getUnit());
          repairOrderItemDTO.setStorageUnit(draftOrderItemDTO.getStorageUnit());
          repairOrderItemDTO.setSellUnit(draftOrderItemDTO.getSellUnit());
          repairOrderItemDTO.setVehicleBrand(draftOrderItemDTO.getVehicleBrand());
          repairOrderItemDTO.setVehicleModel(draftOrderItemDTO.getVehicleModel());
          repairOrderItemDTO.setVehicleYear(draftOrderItemDTO.getVehicleYear());
          repairOrderItemDTO.setVehicleEngine(draftOrderItemDTO.getVehicleEngine());
          repairOrderItemDTO.setRate(draftOrderItemDTO.getRate());
          repairOrderItemDTO.setProductName(draftOrderItemDTO.getProductName());
          repairOrderItemDTO.setCommodityCode(draftOrderItemDTO.getCommodityCode());
          repairOrderItemDTO.setCommodityCodeModifyFlag(draftOrderItemDTO.getCommodityCodeModifyFlag());

          itemDTOList.add(repairOrderItemDTO);
        }
      }
    }
    if (CollectionUtils.isNotEmpty(itemDTOList)) {
      repairOrderDTO.setItemDTOs(itemDTOList.toArray(new RepairOrderItemDTO[itemDTOList.size()]));
    }
    if (CollectionUtils.isNotEmpty(serviceDTOList)) {
      repairOrderDTO.setServiceDTOs(serviceDTOList.toArray(new RepairOrderServiceDTO[serviceDTOList.size()]));
    }

    List<RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOList = null;
    if (CollectionUtils.isNotEmpty(this.getOtherIncomeItemDTOList())) {
      for (DraftOrderOtherIncomeItemDTO itemDTO : this.getOtherIncomeItemDTOList()) {
        if (StringUtils.isBlank(itemDTO.getName())) {
          continue;
        }
        if (null == otherIncomeItemDTOList) {
          otherIncomeItemDTOList = new ArrayList<RepairOrderOtherIncomeItemDTO>();
        }
        RepairOrderOtherIncomeItemDTO otherIncomeItemDTO = new RepairOrderOtherIncomeItemDTO();
        otherIncomeItemDTO.setId(itemDTO.getItemId());
        otherIncomeItemDTO.setMemo(itemDTO.getMemo());
        otherIncomeItemDTO.setName(itemDTO.getName());
        otherIncomeItemDTO.setPrice(itemDTO.getPrice());
        otherIncomeItemDTO.setShopId(itemDTO.getShopId());
        otherIncomeItemDTO.setCalculateCostPrice(itemDTO.getCalculateCostPrice() == null ? null : itemDTO.getCalculateCostPrice().name());
        otherIncomeItemDTO.setOtherIncomeCostPrice(itemDTO.getOtherIncomeCostPrice());
        otherIncomeItemDTO.setOtherIncomeCalculateWay(itemDTO.getOtherIncomeCalculateWay());
        otherIncomeItemDTO.setOtherIncomeRate(itemDTO.getOtherIncomeRate());
        otherIncomeItemDTOList.add(otherIncomeItemDTO);
      }
    }

    repairOrderDTO.setOtherIncomeItemDTOList(otherIncomeItemDTOList);
    repairOrderDTO.setReceiptNo(this.getReceiptNo());
    return repairOrderDTO;
  }

  public  DraftOrderDTO fromPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    this.setId(NumberUtil.longValue(purchaseReturnDTO.getDraftOrderIdStr()));
    this.setStorehouseId(purchaseReturnDTO.getStorehouseId());
    this.setCustomerOrSupplierId(purchaseReturnDTO.getSupplierId());
    this.setCustomerOrSupplierShopId(purchaseReturnDTO.getSupplierShopId());
    this.setCustomerOrSupplierName(purchaseReturnDTO.getSupplier());
    this.setContact(purchaseReturnDTO.getContact());
    this.setContactId(purchaseReturnDTO.getContactId()); // add by zhuj
    if (this.getContactId() != null) {
      this.setContactIdStr(String.valueOf(purchaseReturnDTO.getContactId()));
    }
    this.setMobile(purchaseReturnDTO.getMobile());
    this.setAddress(purchaseReturnDTO.getAddress());
    this.setLandLine(purchaseReturnDTO.getLandline());
    this.setReceiptNo(purchaseReturnDTO.getReceiptNo());
    this.setReturnPayableType(purchaseReturnDTO.getReturnPayableType());
    this.setBank(purchaseReturnDTO.getBank());
    this.setAccount(purchaseReturnDTO.getAccount());
    this.setBusinessScope(purchaseReturnDTO.getBusinessScope());
    this.setAccountName(purchaseReturnDTO.getAccountName());
    this.setCategory(purchaseReturnDTO.getCategory());
    this.setAbbr(purchaseReturnDTO.getAbbr());
    this.setSettlementType(purchaseReturnDTO.getSettlementType());
    this.setQq(purchaseReturnDTO.getQq());
    this.setFax(purchaseReturnDTO.getFax());
    this.setEmail(purchaseReturnDTO.getEmail());
    this.setInvoiceCategory(purchaseReturnDTO.getInvoiceCategory());

    this.setVestDateStr(purchaseReturnDTO.getVestDateStr());
    this.setVestDate(purchaseReturnDTO.getVestDate());
    this.setBillProducer(purchaseReturnDTO.getEditor());
    this.setBillProducerId(purchaseReturnDTO.getEditorId());
    this.setTotal(purchaseReturnDTO.getTotal());
    this.setOrderTypeEnum(OrderTypes.RETURN);
    this.setTxnOrderStatus(purchaseReturnDTO.getStatus());
    this.setTxnOrderId(purchaseReturnDTO.getId());
    this.setMemo(purchaseReturnDTO.getMemo());
    this.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY,purchaseReturnDTO.getEditDateStr()));
    this.setShopAddress(purchaseReturnDTO.getShopAddress());
    this.setShopLandLine(purchaseReturnDTO.getShopLandLine());
    this.setShopName(purchaseReturnDTO.getShopName());
    this.setOriginalOrderId(purchaseReturnDTO.getOriginOrderId());
    this.setOriginalReceiptNo(purchaseReturnDTO.getOriginReceiptNo());
    if(purchaseReturnDTO.getOriginOrderId()!=null){
      this.setEditStatus(DraftOrderStatus.DRAFT_READ_ONLY);
    }else {
      this.setEditStatus(DraftOrderStatus.DRAFT_READ_WRITE);
    }
    List<DraftOrderItemDTO> itemDTOList = new ArrayList<DraftOrderItemDTO>();
    StringBuffer materialBuffer = new StringBuffer();
    if(!ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())){
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
        if (StringUtil.isAllEmpty(purchaseReturnItemDTO.getProductName(), purchaseReturnItemDTO.getBrand(), purchaseReturnItemDTO.getModel(), purchaseReturnItemDTO.getSpec(), purchaseReturnItemDTO.getVehicleBrand(), purchaseReturnItemDTO.getVehicleModel()))
          continue;
        DraftOrderItemDTO itemDTO = new DraftOrderItemDTO();
        itemDTO.setProductLocalInfoId(purchaseReturnItemDTO.getProductId());
        itemDTO.setBrand(purchaseReturnItemDTO.getBrand());
        itemDTO.setModel(purchaseReturnItemDTO.getModel());
        itemDTO.setSpec(purchaseReturnItemDTO.getSpec());
        itemDTO.setProductName(purchaseReturnItemDTO.getProductName());
        itemDTO.setRate(purchaseReturnItemDTO.getRate());
        itemDTO.setSellUnit(purchaseReturnItemDTO.getSellUnit());
        itemDTO.setStorageUnit(purchaseReturnItemDTO.getStorageUnit());
        itemDTO.setUnit(purchaseReturnItemDTO.getUnit());

        itemDTO.setShopId(this.getShopId());
        itemDTO.setAmount(purchaseReturnItemDTO.getAmount());
        itemDTO.setPrice(purchaseReturnItemDTO.getPrice());
        itemDTO.setReturnAmount(purchaseReturnItemDTO.getIamount());
        itemDTO.setReturnPrice(purchaseReturnItemDTO.getIprice());
        itemDTO.setTotal(purchaseReturnItemDTO.getTotal());
        itemDTO.setMemo(purchaseReturnItemDTO.getMemo());
        itemDTO.setProductId(purchaseReturnItemDTO.getProductId());
        itemDTO.setProductIdStr(purchaseReturnItemDTO.getProductIdStr());
        itemDTO.setVehicleBrand(purchaseReturnItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(purchaseReturnItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(purchaseReturnItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(purchaseReturnItemDTO.getVehicleEngine());
        itemDTO.setRate(purchaseReturnItemDTO.getRate());
        itemDTO.setCommodityCode(purchaseReturnItemDTO.getCommodityCode());
        itemDTO.setUseAmountJson(toUserAmountJson(purchaseReturnItemDTO));
        materialBuffer.append(purchaseReturnItemDTO.getProductName());
        materialBuffer.append(";");
        itemDTOList.add(itemDTO);
      }
    }

    this.setMaterial(materialBuffer.toString());
    this.setItemDTOs(itemDTOList.toArray(new DraftOrderItemDTO[itemDTOList.size()]));
    return this;
  }

  public PurchaseReturnDTO toPurchaseReturnDTO() {
    PurchaseReturnDTO purchaseReturnDTO = new PurchaseReturnDTO();
    purchaseReturnDTO.setStorehouseId(this.getStorehouseId());
    purchaseReturnDTO.setDraftOrderIdStr(this.getIdStr());
    purchaseReturnDTO.setSupplierShopId(this.getCustomerOrSupplierShopId());
    purchaseReturnDTO.setSupplierId(this.getCustomerOrSupplierId());
    purchaseReturnDTO.setSupplier(this.getCustomerOrSupplierName());
    purchaseReturnDTO.setContact(this.getContact());
    purchaseReturnDTO.setContactId(this.getContactId()); // add by zhuj
    if (this.getContactId() != null){
      purchaseReturnDTO.setContactIdStr(String.valueOf(this.getContactId()));
    }
    purchaseReturnDTO.setMobile(this.getMobile());
    purchaseReturnDTO.setAddress(this.getAddress());
    purchaseReturnDTO.setLandline(this.getLandLine());

    purchaseReturnDTO.setReturnPayableType(this.getReturnPayableType());
    purchaseReturnDTO.setBank(this.getBank());
    purchaseReturnDTO.setAccount(this.getAccount());
    purchaseReturnDTO.setBusinessScope(this.getBusinessScope());
    purchaseReturnDTO.setAccountName(this.getAccountName());
    purchaseReturnDTO.setCategory(this.getCategory());
    purchaseReturnDTO.setAbbr(this.getAbbr());
    purchaseReturnDTO.setSettlementType(this.getSettlementType());
    purchaseReturnDTO.setQq(this.getQq());
    purchaseReturnDTO.setFax(this.getFax());
    purchaseReturnDTO.setEmail(this.getEmail());
    purchaseReturnDTO.setInvoiceCategory(this.getInvoiceCategory());

    purchaseReturnDTO.setEditor(this.getBillProducer());
    purchaseReturnDTO.setEditorId(this.getBillProducerId());
    purchaseReturnDTO.setTotal(this.getTotal());
    purchaseReturnDTO.setMemo(this.getMemo());
    purchaseReturnDTO.setEditDate(this.getEditDate());
    purchaseReturnDTO.setEditDateStr(DateUtil.convertDateLongToString(this.getEditDate(),DateUtil.DATE_STRING_FORMAT_DAY));
    purchaseReturnDTO.setVestDateStr(this.getVestDateStr());
    purchaseReturnDTO.setVestDate(this.getVestDate());
    purchaseReturnDTO.setShopLandLine(this.getShopLandLine());
    purchaseReturnDTO.setShopName(this.getShopName());
    purchaseReturnDTO.setShopAddress(this.getShopAddress());
    purchaseReturnDTO.setStatus(this.getTxnOrderStatus());
    purchaseReturnDTO.setOriginOrderId(this.getOriginalOrderId());
    purchaseReturnDTO.setOriginReceiptNo(this.getOriginalReceiptNo());
    List<PurchaseReturnItemDTO> itemDTOList = new ArrayList<PurchaseReturnItemDTO>();
    Double totalReturnAmount = 0d;
    if(!ArrayUtils.isEmpty(this.getItemDTOs())){
      for (DraftOrderItemDTO draftOrderItemDTO : this.getItemDTOs()) {
        PurchaseReturnItemDTO itemDTO = new PurchaseReturnItemDTO();
        itemDTO.setBrand(draftOrderItemDTO.getBrand());
        itemDTO.setModel(draftOrderItemDTO.getModel());
        itemDTO.setSpec(draftOrderItemDTO.getSpec());
        itemDTO.setProductName(draftOrderItemDTO.getProductName());
        itemDTO.setRate(draftOrderItemDTO.getRate());
        itemDTO.setSellUnit(draftOrderItemDTO.getSellUnit());
        itemDTO.setStorageUnit(draftOrderItemDTO.getStorageUnit());
        itemDTO.setUnit(draftOrderItemDTO.getUnit());
        itemDTO.setInventoryAmount(draftOrderItemDTO.getInventoryAmount());
        itemDTO.setAmount(draftOrderItemDTO.getAmount());

        totalReturnAmount+=itemDTO.getAmount();

        itemDTO.setPrice(draftOrderItemDTO.getPrice());
        itemDTO.setTotal(draftOrderItemDTO.getTotal());
        itemDTO.setIprice(draftOrderItemDTO.getReturnPrice());
        itemDTO.setIamount(draftOrderItemDTO.getReturnAmount());
        itemDTO.setMemo(draftOrderItemDTO.getMemo());
        itemDTO.setProductId(draftOrderItemDTO.getProductId());
        itemDTO.setProductIdStr(draftOrderItemDTO.getProductIdStr());
        itemDTO.setVehicleBrand(draftOrderItemDTO.getVehicleBrand());
        itemDTO.setVehicleModel(draftOrderItemDTO.getVehicleModel());
        itemDTO.setVehicleYear(draftOrderItemDTO.getVehicleYear());
        itemDTO.setVehicleEngine(draftOrderItemDTO.getVehicleEngine());
        itemDTO.setRate(draftOrderItemDTO.getRate());
        itemDTO.setCommodityCode(draftOrderItemDTO.getCommodityCode());
        itemDTO.setOutStorageRelationDTOs(draftOrderItemDTO.getOutStorageRelationDTOs());
        itemDTO.setInventoryAveragePrice(draftOrderItemDTO.getInventoryAveragePrice());
        itemDTOList.add(itemDTO);
      }
    }
    purchaseReturnDTO.setTotalReturnAmount(totalReturnAmount);
    purchaseReturnDTO.setItemDTOs(itemDTOList.toArray(new PurchaseReturnItemDTO[itemDTOList.size()]));
    purchaseReturnDTO.setReceiptNo(this.getReceiptNo());
    return purchaseReturnDTO;
  }

  public String getChassisNo() {
    return chassisNo;
  }

  public void setChassisNo(String chassisNo) {
    this.chassisNo = chassisNo;
  }

  public String getColor() {

    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getEngineNo() {

    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
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

  public Long getVehicleHandoverId() {
    return vehicleHandoverId;
  }

  public void setVehicleHandoverId(Long vehicleHandoverId) {
    this.vehicleHandoverId = vehicleHandoverId;
  }

  public Long getConsumingRecordId() {
    return consumingRecordId;
  }

  public void setConsumingRecordId(Long consumingRecordId) {
    this.consumingRecordId = consumingRecordId;
  }
}
