package com.bcgogo.txn.model;

import com.bcgogo.enums.DraftOrderStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.DraftOrderDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-9-8
 * Time: 上午3:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "draft_order")
public class DraftOrder extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private String customerOrSupplierName;
  private Long customerOrSupplierId; //采购单 supplierId,销售单 customerId,施工单customerId ?
  private Long customerOrSupplierShopId; //采购单 supplierId,销售单 customerId,施工单customerId shop
  private String contact;
  private Long contactId; // add by zhuj 联系人id
  private String mobile;
  private String landLine;
  private String address;
  private OrderTypes orderTypeEnum;
  private DraftOrderStatus status;   // 草稿箱状态
  private OrderStatus txnOrderStatus;   // 原始单据状态
  private String memo;    //单据备注
  private Long editDate;       //入库日期   ，采购日期 ，销售日期 ，退货日期 。
  private Long saveTime;//草稿箱保存时间
  private Double total;//
  private Double settledAmount;//施工单，销售单实收
  private Long repaymentTime;//施工单，销售单还款时间
  private Double debt;// 欠款     
  private Long billProducerId;   //制单人Id   转换为入库单的 acceptorId;销售单的 goodsSalerId，退货单的editorId;
  private Long deliveryDate;//采购交货日期
  private Long purchaseOrderId;   //入库单关联采购单Id
  private Long txnOrderId;   //与草稿箱关联的进销存Id
  //退货的供应商详细信息
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
  private String vehicleContact;
  private String vehicleMobile;
  private String brand;
  private String model;
  private String engineNo;
  private String color;
  private String chassisNo;
  private Double startMileage;
  private Double endMileage;
  private String fuelNumber;
  private Long startDate;
  private Long endDate;
  private String productSales;
  private Long vestDate;
  private String vehicleHandover;//接车人
  private Long vehicleHandoverId;//接车人
  private Long maintainTime;   //预约保养时间
  private Long insureTime;      //预约保险时间
  private Long examineTime;    //预约验车时间
  private Long maintainMileage;//保养里程

  private String material;       //材料
  private String serviceContent;    //施工内容
  private Long consumingRecordId;  //记录施工单对应的代金券消费记录id add by LiTao 2015-11-17
  //针对退货单
  private Long originalOrderId;
  private OrderTypes originalOrderType;
  private String originalReceiptNo;
  private DraftOrderStatus editStatus;

  private Long storehouseId;
  private String description;
  private String receiptNo;
  private Long appointOrderId;  //相关联的预约单Id
  @Column(name = "vehicle_handover_id")
  public Long getVehicleHandoverId() {
    return vehicleHandoverId;
  }

  public void setVehicleHandoverId(Long vehicleHandoverId) {
    this.vehicleHandoverId = vehicleHandoverId;
  }

  @Column(name = "vehicle_handover")
  public String getVehicleHandover() {
    return vehicleHandover;
  }

  public void setVehicleHandover(String vehicleHandover) {
    this.vehicleHandover = vehicleHandover;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "debt")
  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  @Column(name = "bill_producer_id")
  public Long getBillProducerId() {
    return billProducerId;
  }

  public void setBillProducerId(Long billProducerId) {
    this.billProducerId = billProducerId;
  }


  @Column(name = "order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  @Column(name = "purchaseOrder_id")
  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
  }

  @Column(name = "customer_Supplier_name")
  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  @Column(name = "customer_Supplier_Id")
  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
  }

  @Column(name = "contact")
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Column(name = "contact_id")
  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "landline")
  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  @Column(name = "address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }


  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public DraftOrderStatus getStatus() {
    return status;
  }

  public void setStatus(DraftOrderStatus status) {
    this.status = status;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "save_time")
  public Long getSaveTime() {
    return saveTime;
  }

  public void setSaveTime(Long saveTime) {
    this.saveTime = saveTime;
  }

  @Column(name = "settled_amount")
  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  @Column(name = "repayment_time")
  public Long getRepaymentTime() {
    return repaymentTime;
  }

  public void setRepaymentTime(Long repaymentTime) {
    this.repaymentTime = repaymentTime;
  }

  @Column(name = "delivery_date")
  public Long getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Long deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  @Column(name = "txnOrder_id")
  public Long getTxnOrderId() {
    return txnOrderId;
  }

  public void setTxnOrderId(Long txnOrderId) {
    this.txnOrderId = txnOrderId;
  }

  @Column(name = "vechicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Column(name = "vechicle")
  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "start_mileage")
  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  @Column(name = "end_mileage")
  public Double getEndMileage() {
    return endMileage;
  }

  public void setEndMileage(Double endMileage) {
    this.endMileage = endMileage;
  }

  @Column(name = "fuel_number")
  public String getFuelNumber() {
    return fuelNumber;
  }

  public void setFuelNumber(String fuelNumber) {
    this.fuelNumber = fuelNumber;
  }

  @Column(name = "start_date")
  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "end_date")
  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  @Column(name = "productSales")
  public String getProductSales() {
    return productSales;
  }

  public void setProductSales(String productSales) {
    this.productSales = productSales;
  }

  @Column(name = "material")
  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  @Column(name = "service_content")
  public String getServiceContent() {
    return serviceContent;
  }

  public void setServiceContent(String serviceContent) {
    this.serviceContent = serviceContent;
  }

  @Column(name="consuming_record_id")
  public Long getConsumingRecordId() {
    return consumingRecordId;
  }

  public void setConsumingRecordId(Long consumingRecordId) {
    this.consumingRecordId = consumingRecordId;
  }

  @Column(name="original_order_id")
  public Long getOriginalOrderId() {
    return originalOrderId;
  }

  public void setOriginalOrderId(Long originalOrderId) {
    this.originalOrderId = originalOrderId;
  }

  @Column(name="original_order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOriginalOrderType() {
    return originalOrderType;
  }

  public void setOriginalOrderType(OrderTypes originalOrderType) {
    this.originalOrderType = originalOrderType;
  }

    @Column(name="original_receipt_no")
  public String getOriginalReceiptNo() {
    return originalReceiptNo;
  }

  public void setOriginalReceiptNo(String originalReceiptNo) {
    this.originalReceiptNo = originalReceiptNo;
  }

  @Column(name="edit_status")
  @Enumerated(EnumType.STRING)
  public DraftOrderStatus getEditStatus() {
    return editStatus;
  }

  public void setEditStatus(DraftOrderStatus editStatus) {
    this.editStatus = editStatus;
  }


  @Column(name = "txn_order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getTxnOrderStatus() {
    return txnOrderStatus;
  }

  public void setTxnOrderStatus(OrderStatus txnOrderStatus) {
    this.txnOrderStatus = txnOrderStatus;
  }

  @Column(name = "email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "return_payable_type")
  public String getReturnPayableType() {
    return returnPayableType;
  }

  public void setReturnPayableType(String returnPayableType) {
    this.returnPayableType = returnPayableType;
  }

  @Column(name = "bank")
  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  @Column(name = "account")
  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  @Column(name = "business_scope")
  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  @Column(name = "account_name")
  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  @Column(name = "category")
  public Long getCategory() {
    return category;
  }

  public void setCategory(Long category) {
    this.category = category;
  }

  @Column(name = "abbr")
  public String getAbbr() {
    return abbr;
  }

  public void setAbbr(String abbr) {
    this.abbr = abbr;
  }

  @Column(name = "settlement_type")
  public Long getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(Long settlementType) {
    this.settlementType = settlementType;
  }

  @Column(name = "fax")
  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  @Column(name = "qq")
  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  @Column(name = "invoice_category")
  public Long getInvoiceCategory() {
    return invoiceCategory;
  }

  public void setInvoiceCategory(Long invoiceCategory) {
    this.invoiceCategory = invoiceCategory;
  }

  @Column(name = "maintain_time")
  public Long getMaintainTime() {
    return maintainTime;
  }

  public void setMaintainTime(Long maintainTime) {
    this.maintainTime = maintainTime;
  }

  @Column(name = "examine_time")
  public Long getExamineTime() {
    return examineTime;
  }

  public void setExamineTime(Long examineTime) {
    this.examineTime = examineTime;
  }

  @Column(name = "insure_time")
  public Long getInsureTime() {
    return insureTime;
  }

  public void setInsureTime(Long insureTime) {
    this.insureTime = insureTime;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "appoint_order_id")
  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  @Column(name = "maintain_mileage")
  public Long getMaintainMileage() {
    return maintainMileage;
  }

  public void setMaintainMileage(Long maintainMileage) {
    this.maintainMileage = maintainMileage;
  }

  public DraftOrder fromDTO(DraftOrderDTO draftOrderDTO) throws ParseException {
    this.setShopId(draftOrderDTO.getShopId());
    this.setUserId(draftOrderDTO.getUserId());
    this.setStorehouseId(draftOrderDTO.getStorehouseId());
    this.setCustomerOrSupplierShopId(draftOrderDTO.getCustomerOrSupplierShopId());
    this.setCustomerOrSupplierId(draftOrderDTO.getCustomerOrSupplierId());
    this.setCustomerOrSupplierName(draftOrderDTO.getCustomerOrSupplierName());
    this.setStartDate(draftOrderDTO.getStartDate());
    this.setEndDate(draftOrderDTO.getEndDate());
    this.setStartMileage(draftOrderDTO.getStartMileage());
    this.setEndMileage(draftOrderDTO.getEndMileage());
    this.setFuelNumber(draftOrderDTO.getFuelNumber());
    this.setProductSales(draftOrderDTO.getProductSales());

    this.setContact(draftOrderDTO.getContact());
    this.setContactId(draftOrderDTO.getContactId()); //add by zhuj
    this.setMobile(draftOrderDTO.getMobile());
    this.setLandLine(draftOrderDTO.getLandLine());
    this.setAddress(draftOrderDTO.getAddress());
    this.setOrderTypeEnum(draftOrderDTO.getOrderTypeEnum());
    this.setMemo(draftOrderDTO.getMemo());
    this.setEditDate(draftOrderDTO.getEditDate());
    this.setSettledAmount(draftOrderDTO.getSettledAmount());
    this.setDebt(draftOrderDTO.getDebt());
    this.setRepaymentTime(draftOrderDTO.getRepaymentTime());
    this.setBillProducerId(draftOrderDTO.getBillProducerId());
    this.setMaintainTime(draftOrderDTO.getMaintainTime());
    this.setInsureTime(draftOrderDTO.getInsureTime());
    this.setExamineTime(draftOrderDTO.getExamineTime());
    this.setDeliveryDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, draftOrderDTO.getDeliveryDateStr()));
    this.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderDTO.getVestDateStr()));
    this.setTxnOrderId(draftOrderDTO.getTxnOrderId());
    this.setVehicle(draftOrderDTO.getVehicle());
    this.setVehicleId(draftOrderDTO.getVehicleId());
    this.setBrand(draftOrderDTO.getBrand());
    this.setModel(draftOrderDTO.getModel());
    this.setVehicleContact(draftOrderDTO.getVehicleContact());
    this.setVehicleMobile(draftOrderDTO.getVehicleMobile());
    this.setChassisNo(draftOrderDTO.getChassisNo());
    this.setEngineNo(draftOrderDTO.getEngineNo());
    this.setColor(draftOrderDTO.getColor());
    this.setMaterial(draftOrderDTO.getMaterial());
    this.setServiceContent(draftOrderDTO.getServiceContent());
    this.setTxnOrderStatus(draftOrderDTO.getTxnOrderStatus());
    this.setTotal(draftOrderDTO.getTotal());

    this.setReturnPayableType(draftOrderDTO.getReturnPayableType());
    this.setBank(draftOrderDTO.getBank());
    this.setAccount(draftOrderDTO.getAccount());
    this.setAccountName(draftOrderDTO.getAccountName());
    this.setBusinessScope(draftOrderDTO.getBusinessScope());
    this.setCategory(draftOrderDTO.getCategory());
    this.setAbbr(draftOrderDTO.getAbbr());
    this.setSettlementType(draftOrderDTO.getSettlementType());
    this.setFax(draftOrderDTO.getFax());
    this.setQq(draftOrderDTO.getQq());
    this.setInvoiceCategory(draftOrderDTO.getInvoiceCategory());
    this.setEmail(draftOrderDTO.getEmail());
    this.setOriginalOrderId(draftOrderDTO.getOriginalOrderId());
    this.setOriginalOrderType(draftOrderDTO.getOriginalOrderType());
    this.setOriginalReceiptNo(draftOrderDTO.getOriginalReceiptNo());
    this.setEditStatus(draftOrderDTO.getEditStatus());
    this.setDescription(draftOrderDTO.getDescription());
    this.setReceiptNo(draftOrderDTO.getReceiptNo());
    this.setVehicleHandover(draftOrderDTO.getVehicleHandover());
    this.setVehicleHandoverId(draftOrderDTO.getVehicleHandoverId());
    this.setAppointOrderId(draftOrderDTO.getAppointOrderId());
    this.setMaintainMileage(draftOrderDTO.getMaintainMileage());
    this.setConsumingRecordId(draftOrderDTO.getConsumingRecordId());
    return this;
  }

  public DraftOrderDTO toDTO() {
    DraftOrderDTO draftOrderDTO = new DraftOrderDTO();
    draftOrderDTO.setTotal(this.getTotal());
    draftOrderDTO.setId(this.getId());
    draftOrderDTO.setStorehouseId(this.getStorehouseId());
    draftOrderDTO.setIdStr(String.valueOf(this.getId()));
    draftOrderDTO.setId(this.getId());
    draftOrderDTO.setShopId(this.getShopId());
    draftOrderDTO.setUserId(this.getUserId());
    draftOrderDTO.setCustomerOrSupplierShopId(this.getCustomerOrSupplierShopId());
    draftOrderDTO.setCustomerOrSupplierId(this.getCustomerOrSupplierId());
    draftOrderDTO.setCustomerOrSupplierName(this.getCustomerOrSupplierName());
    draftOrderDTO.setContact(this.getContact());
    draftOrderDTO.setContactId(this.getContactId());
    if (this.getContactId() != null) {
      draftOrderDTO.setContactIdStr(String.valueOf(this.getContactId()));
    }
    draftOrderDTO.setMobile(this.getMobile());
    draftOrderDTO.setLandLine(this.getLandLine());
    draftOrderDTO.setAddress(this.getAddress());
    draftOrderDTO.setSaveTime(this.getSaveTime());
    draftOrderDTO.setSettledAmount(this.getSettledAmount());
    draftOrderDTO.setDebt(this.getDebt());
    draftOrderDTO.setOrderTypeEnum(this.getOrderTypeEnum());
    draftOrderDTO.setOrderTypeStr(this.getOrderTypeEnum().getName());
    draftOrderDTO.setMemo(this.getMemo());
    draftOrderDTO.setEditDate(this.getEditDate());
    draftOrderDTO.setSaveTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getSaveTime()));
    draftOrderDTO.setVestDate(this.getVestDate());
    draftOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, this.getVestDate()));
    draftOrderDTO.setMaintainTime(this.getMaintainTime());
    draftOrderDTO.setInsureTime(this.getInsureTime());
    draftOrderDTO.setExamineTime(this.getExamineTime());
    draftOrderDTO.setRepaymentTime(this.getRepaymentTime());
    draftOrderDTO.setBillProducerId(this.getBillProducerId());
    draftOrderDTO.setDeliveryDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, this.getDeliveryDate()));
    draftOrderDTO.setTxnOrderId(this.getTxnOrderId());
    draftOrderDTO.setVehicle(this.getVehicle());
    draftOrderDTO.setVehicleId(this.getVehicleId());
    draftOrderDTO.setBrand(this.getBrand());
    draftOrderDTO.setModel(this.getModel());
    draftOrderDTO.setVehicleContact(this.getVehicleContact());
    draftOrderDTO.setVehicleMobile(this.getVehicleMobile());
    draftOrderDTO.setChassisNo(this.getChassisNo());
    draftOrderDTO.setEngineNo(this.getEngineNo());
    draftOrderDTO.setColor(this.getColor());
    draftOrderDTO.setStatus(this.getStatus());
    draftOrderDTO.setVehicleHandover(this.getVehicleHandover());
    draftOrderDTO.setVehicleHandoverId(this.getVehicleHandoverId());
    draftOrderDTO.setDescription(this.getDescription());
    if (!StringUtil.isEmpty(this.getMaterial()) && this.getMaterial().length() > 15) {
      draftOrderDTO.setMaterialStr(this.getMaterial().substring(0, 15) + "...");
    } else {
      draftOrderDTO.setMaterialStr(this.getMaterial());
    }
    if (!StringUtil.isEmpty(this.getServiceContent()) && this.getServiceContent().length() > 15) {
      draftOrderDTO.setServiceContentStr(this.getServiceContent().substring(0, 15) + "...");
    } else {
      draftOrderDTO.setServiceContentStr(this.getServiceContent());
    }
    draftOrderDTO.setMaterial(this.getMaterial());
    draftOrderDTO.setServiceContent(this.getServiceContent());

    draftOrderDTO.setStartDate(this.getStartDate());
    draftOrderDTO.setEndDate(this.getEndDate());
    draftOrderDTO.setStartMileage(this.getStartMileage());
    draftOrderDTO.setEndMileage(this.getEndMileage());
    draftOrderDTO.setFuelNumber(this.getFuelNumber());
    draftOrderDTO.setTxnOrderStatus(this.getTxnOrderStatus());
    draftOrderDTO.setProductSales(this.getProductSales());

    draftOrderDTO.setReturnPayableType(this.getReturnPayableType());
    draftOrderDTO.setBank(this.getBank());
    draftOrderDTO.setAccount(this.getAccount());
    draftOrderDTO.setAccountName(this.getAccountName());
    draftOrderDTO.setBusinessScope(this.getBusinessScope());
    draftOrderDTO.setCategory(this.getCategory());
    draftOrderDTO.setAbbr(this.getAbbr());
    draftOrderDTO.setSettlementType(this.getSettlementType());
    draftOrderDTO.setFax(this.getFax());
    draftOrderDTO.setQq(this.getQq());
    draftOrderDTO.setInvoiceCategory(this.getInvoiceCategory());
    draftOrderDTO.setEmail(this.getEmail());
    draftOrderDTO.setOriginalOrderId(this.getOriginalOrderId());
    draftOrderDTO.setOriginalOrderType(this.getOriginalOrderType());
    draftOrderDTO.setOriginalReceiptNo(this.getOriginalReceiptNo());
    draftOrderDTO.setEditStatus(this.getEditStatus());
    draftOrderDTO.setReceiptNo(this.getReceiptNo());
    draftOrderDTO.setAppointOrderId(this.getAppointOrderId());
    draftOrderDTO.setMaintainMileage(this.getMaintainMileage());
    draftOrderDTO.setConsumingRecordId(this.getConsumingRecordId());
    return draftOrderDTO;
  }
  @Column(name = "customer_or_supplier_shop_id")
  public Long getCustomerOrSupplierShopId() {
    return customerOrSupplierShopId;
  }

  public void setCustomerOrSupplierShopId(Long customerOrSupplierShopId) {
    this.customerOrSupplierShopId = customerOrSupplierShopId;
  }

  @Column(name = "storehouse_id")
  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  @Column(name = "receiptNo")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "engine_no")
  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  @Column(name = "color")
  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  @Column(name = "chassis_no")
  public String getChassisNo() {
    return chassisNo;
  }

  public void setChassisNo(String chassisNo) {
    this.chassisNo = chassisNo;
  }

  @Column(name = "vehiclec_contact")
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
}
