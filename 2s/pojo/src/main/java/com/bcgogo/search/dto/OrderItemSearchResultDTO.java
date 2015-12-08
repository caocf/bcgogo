package com.bcgogo.search.dto;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.*;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.preBuyOrder.QuotedResult;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderItemDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class OrderItemSearchResultDTO {
  private static final Logger LOG = LoggerFactory.getLogger(OrderItemSearchResultDTO.class);
  private Long shopId;         //店面ID
  private String shopIdStr;
  private String shopAreaInfo;
  private String shopName;

  private Long shopProvince;     //省
  private Long shopCity;          //市
  private Long shopRegion;        //区域

  private String editor;
  private Long orderId;        //单子ID
  private String orderIdStr;        //单子ID
  private String orderType;    // 单子类型
  private String orderTypeValue;    // 单子类型 Value
  private String orderStatus;     //  状态
  private String orderStatusValue;     //状态Value
  private String vehicle;      // 车牌号
  private String customerOrSupplierName;   // 客户或供应商名字  求购买家
  private Long customerOrSupplierId;
  private CustomerStatus customerStatus;
  private Long createdTime;
  private String createdTimeStr;

  private Long endDate;
  private String endDateStr;
  private String orderReceiptNo; //单据号

  private Long vestDate;
  private String vestDateStr;

  private ItemTypes itemType;
  private Long itemId;
  private String itemIdStr;
  private Double itemPrice;
  private Double itemTotalCostPrice;
  private Double itemCount;
  private Double itemTotal;

  private String service;
  private Long serviceId;
  private ConsumeType consumeType;
  private String consumeTypeStr;

  private Long productId;
  private String productName;
  private String productBrand;
  private String productModel;
  private String productSpec;
  private String productVehicleBrand;          //产品本身带的车辆信息
  private String productVehicleModel;
  private String commodityCode;
  private String productInfo;
  private String productInfoStr;
  private Long supplierProductId;
  private String unit;
  private QuotedResult quotedResult;
  private Integer quotedCount;
  private String quotedResultValue;
  private String itemMemo;
  private String preBuyOrderStatusStr;
  private Double customScore;//
  private OrderSearchConditionDTO orderSearchConditionDTO;
  private Long customerOrSupplierShopId;
  private String customerOrSupplierShopIdStr;
  private ImageCenterDTO imageCenterDTO;
 private String fuzzyAmountStr;

  private QuotedPreBuyOrderItemDTO myQuotedPreBuyOrderItemDTO;
  private boolean myQuoted;
  private String businessChanceType;
  private String businessChanceTypeValue;



  public void setShopInfo(ShopDTO shopDTO){
    if(shopDTO==null){
      return;
    }
    this.setShopName(shopDTO.getName());
    this.setShopCity(shopDTO.getCity());
    this.setShopProvince(shopDTO.getProvince());
    this.setShopRegion(shopDTO.getRegion());
  }

  public OrderItemSearchResultDTO() {
  }
  public OrderItemSearchResultDTO(Long shopId,PreBuyOrderItemDTO preBuyOrderItemDTO) {
    this.setShopId(shopId);
    this.setProductName(preBuyOrderItemDTO.getProductName());
    this.setProductBrand(preBuyOrderItemDTO.getBrand());
    this.setProductSpec(preBuyOrderItemDTO.getSpec());
    this.setProductModel(preBuyOrderItemDTO.getModel());
    this.setProductVehicleBrand(preBuyOrderItemDTO.getVehicleBrand());
    this.setProductVehicleModel(preBuyOrderItemDTO.getVehicleModel());
    this.setItemId(preBuyOrderItemDTO.getId());
    this.setOrderId(preBuyOrderItemDTO.getPreBuyOrderId());
  }

  public OrderSearchConditionDTO getOrderSearchConditionDTO() {
    return orderSearchConditionDTO;
  }

  public void setOrderSearchConditionDTO(OrderSearchConditionDTO orderSearchConditionDTO) {
    this.orderSearchConditionDTO = orderSearchConditionDTO;
  }

  public Double getCustomScore() {
    return customScore;
  }

  public void setCustomScore(Double customScore) {
    this.customScore = customScore;
  }

  public String getFuzzyAmountStr() {
    return fuzzyAmountStr;
  }

  public void setFuzzyAmountStr(String fuzzyAmountStr) {
    this.fuzzyAmountStr = fuzzyAmountStr;
  }

  public Long getShopProvince() {
    return shopProvince;
  }

  public void setShopProvince(Long shopProvince) {
    this.shopProvince = shopProvince;
  }

  public Long getShopCity() {
    return shopCity;
  }

  public void setShopCity(Long shopCity) {
    this.shopCity = shopCity;
  }

  public Long getShopRegion() {
    return shopRegion;
  }

  public void setShopRegion(Long shopRegion) {
    this.shopRegion = shopRegion;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopAreaInfo() {
    return shopAreaInfo;
  }

  public void setShopAreaInfo(String shopAreaInfo) {
    this.shopAreaInfo = shopAreaInfo;
  }

  public String getPreBuyOrderStatusStr() {
    return preBuyOrderStatusStr;
  }

  public void setPreBuyOrderStatusStr(String preBuyOrderStatusStr) {
    this.preBuyOrderStatusStr = preBuyOrderStatusStr;
  }

  private String customerOrSupplierIdStr;
  private String couponType;

  public Long getEndDate() {
    return endDate;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public void setEndDate(Long endDate) {
    this.endDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, endDate);
    this.endDate = endDate;
    if(endDate!=null){
      try {
        if(endDate>=DateUtil.getTheDayTime()){
          this.preBuyOrderStatusStr="有效";
        }else{
          this.preBuyOrderStatusStr ="过期";
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
    this.vestDateStr = DateUtil.convertDateLongToDateString(DateUtil.DEFAULT, vestDate);
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getItemMemo() {
    return itemMemo;
  }

  public void setItemMemo(String itemMemo) {
    this.itemMemo = itemMemo;
  }

  public String getItemIdStr() {
    return itemIdStr;
  }

  public void setItemIdStr(String itemIdStr) {
    this.itemIdStr = itemIdStr;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Integer getQuotedCount() {
    return quotedCount;
  }

  public void setQuotedCount(Integer quotedCount) {
    this.quotedCount = quotedCount;
  }

  public QuotedResult getQuotedResult() {
    return quotedResult;
  }

  public void setQuotedResult(QuotedResult quotedResult) {
    if(quotedResult!=null){
      this.quotedResultValue = quotedResult.getName();
    }
    this.quotedResult = quotedResult;
  }

  public String getQuotedResultValue() {
    return quotedResultValue;
  }

  public void setQuotedResultValue(String quotedResultValue) {
    this.quotedResultValue = quotedResultValue;
  }

  public String getCustomerOrSupplierIdStr() {
    return customerOrSupplierIdStr;
  }

  public void setCustomerOrSupplierIdStr(String customerOrSupplierIdStr) {
    this.customerOrSupplierIdStr = customerOrSupplierIdStr;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Double getItemTotalCostPrice() {
    return itemTotalCostPrice;
  }

  public void setItemTotalCostPrice(Double itemTotalCostPrice) {
    this.itemTotalCostPrice = itemTotalCostPrice;
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
    if(shopId != null){
      shopIdStr = shopId.toString();
    }
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderIdStr = String.valueOf(orderId);
    this.orderId = orderId;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    try {
      //转换 orderType
      orderTypeValue = OrderTypes.valueOf(orderType).getName();
      this.orderType = orderType;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    if (StringUtils.isEmpty(orderStatus)) return;
    try {
      //转换 orderStatus
      this.orderStatusValue = OrderStatus.valueOf(orderStatus).getName();
      this.orderStatus = orderStatus;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, createdTime);
    this.createdTime = createdTime;
  }

  public String getCreatedTimeStr() {
    return createdTimeStr;
  }

  public void setCreatedTimeStr(String createdTimeStr) {
    this.createdTimeStr = createdTimeStr;
  }

  public String getOrderTypeValue() {
    return orderTypeValue;
  }

  public void setOrderTypeValue(String orderTypeValue) {
    this.orderTypeValue = orderTypeValue;
  }

  public String getOrderStatusValue() {
    return orderStatusValue;
  }

  public void setOrderStatusValue(String orderStatusValue) {
    this.orderStatusValue = orderStatusValue;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
    if(customerOrSupplierId != null){
      this.customerOrSupplierIdStr = String.valueOf(customerOrSupplierId);
    }
  }

  public String getOrderReceiptNo() {
    return orderReceiptNo;
  }

  public void setOrderReceiptNo(String orderReceiptNo) {
    this.orderReceiptNo = orderReceiptNo;
  }

  public ItemTypes getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    if (StringUtils.isEmpty(itemType)) return;
    try {
      this.itemType = ItemTypes.valueOf(itemType);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public Double getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(Double itemPrice) {
    this.itemPrice = itemPrice;
  }

  public Double getItemCount() {
    return itemCount;
  }

  public void setItemCount(Double itemCount) {
    this.itemCount = itemCount;
  }

  public Double getItemTotal() {
    return itemTotal;
  }

  public void setItemTotal(Double itemTotal) {
    this.itemTotal = itemTotal;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    if(itemId != null){
      this.itemIdStr = String.valueOf(itemId);
    }
    this.itemId = itemId;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public ConsumeType getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(String consumeType) {
    if (StringUtils.isEmpty(consumeType)) return;
    try {
      this.consumeType = ConsumeType.valueOf(consumeType);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    consumeTypeStr = this.consumeType == null? "": this.consumeType.getType();
  }

  public String getProductInfo() {
    StringBuffer str = new StringBuffer();
    if (!StringUtils.isBlank(this.getProductName())) {
      str.append("品名:").append(this.getProductName());
    }
    if (!StringUtils.isBlank(this.getProductBrand())) {
      str.append(",品牌:").append(this.getProductBrand());
    }
    if (!StringUtils.isBlank(this.getProductSpec())) {
      str.append(",规格:").append(this.getProductSpec());
    }
    if (!StringUtils.isBlank(this.getProductModel())) {
      str.append(",型号:").append(this.getProductModel());
    }
    return str.toString();
  }

  public void setProductInfo(String productInfo) {
    this.productInfo = productInfo;
  }

  public String generateProductInfo(){
    StringBuffer sb=new StringBuffer();
    sb.append(StringUtil.truncValue(this.getCommodityCode())).append(" ").append(StringUtil.truncValue(this.getProductName())).append(" ")
      .append(StringUtil.truncValue(this.getProductBrand())).append(" ").append(StringUtil.truncValue(this.getProductSpec())).append(" ")
      .append(StringUtil.truncValue(this.getProductModel())).append(" ").append(StringUtil.truncValue(this.getProductVehicleModel())).append(" ")
      .append(StringUtil.truncValue(this.getProductVehicleBrand()));
    return sb.toString();
  }

  public String getProductInfoStr() {
    return productInfoStr;
  }

  public void setProductInfoStr(String productInfoStr) {
    this.productInfoStr = productInfoStr;
  }

  public CustomerStatus getCustomerStatus() {
    return customerStatus;
  }

  public void setCustomerStatus(CustomerStatus customerStatus) {
    this.customerStatus = customerStatus;
  }

  public Map<String, String> toOrderItemSuggestionMap() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("id", orderId == null ? "" : orderId.toString());
    map.put("receipt_no", orderReceiptNo);
    map.put("order_type", orderType);
    map.put("order_status", orderStatus == null ? "" : OrderStatus.valueOf(orderStatus).getName());
    map.put("vehicle", vehicle);
    map.put("customer_or_supplier_name", customerOrSupplierName);
    map.put("customer_or_supplier_id", customerOrSupplierId == null ? "" : customerOrSupplierId.toString());
    map.put("created_time", createdTimeStr);
    map.put("item_id", itemId == null ? "" : itemId.toString());
    map.put("services", service);
    map.put("service_id", serviceId == null ? "" : serviceId.toString());
    map.put("product_id", productId == null ? "" : productId.toString());
    map.put("supplier_product_id", supplierProductId == null ? "" : supplierProductId.toString());
    map.put("commodity_code", commodityCode);
    map.put("product_name", productName);
    map.put("product_brand",productBrand);
    map.put("product_spec",productSpec);
    map.put("product_model",productModel);
    map.put("product_vehicle_brand",productVehicleBrand);
    map.put("product_vehicle_model",productVehicleModel);
    map.put("consume_type", consumeType == null ? "" : consumeType.toString());
    map.put("item_count", itemCount == null ? "" :itemCount.toString());
    map.put("item_price", itemPrice == null ? "" : itemPrice.toString());
    map.put("item_type", itemType == null ? "" : itemType.toString());
    map.put("unit", unit);
    map.put("item_total", (itemCount == null ? 0 : itemCount) * (itemPrice == null ? 0 : itemPrice) + "");

    return map;
  }

  public Long getSupplierProductId() {
    return supplierProductId;
  }

  public void setSupplierProductId(Long supplierProductId) {
    this.supplierProductId = supplierProductId;
  }

  public void setCouponType(String couponType) {
    this.couponType = couponType;
  }

  public String getCouponType() {
    return couponType;
  }

  public String getConsumeTypeStr() {
    return consumeTypeStr;
  }

  public void setConsumeTypeStr(String consumeTypeStr) {
    this.consumeTypeStr = consumeTypeStr;
  }

  public String generateShopDataResultKey(){
    return StringUtils.defaultIfEmpty(shopId.toString(),"_")
        +StringUtils.defaultIfEmpty(productName,"_")
        +StringUtils.defaultIfEmpty(productBrand,"_")
        +StringUtils.defaultIfEmpty(productModel,"_")
        +StringUtils.defaultIfEmpty(productSpec,"_")
        +StringUtils.defaultIfEmpty(productVehicleBrand,"_")
        +StringUtils.defaultIfEmpty(productVehicleModel,"_");
  }

  public String generateCustomMatchPContent(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getProductName())){
      sb.append(this.getProductName()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductBrand())){
      sb.append(this.getProductBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductSpec())){
      sb.append(this.getProductSpec()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductModel())){
      sb.append(this.getProductModel()).append(" ");
    }
    return sb.toString().trim();
  }

  public String generateCustomMatchPVContent(){
    StringBuffer sb = new StringBuffer();

    if(StringUtils.isNotBlank(this.getProductVehicleBrand())){
      sb.append(this.getProductVehicleBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductVehicleModel())){
      sb.append(this.getProductVehicleModel()).append(" ");
    }
    return sb.toString().trim();
  }

  public Long getCustomerOrSupplierShopId() {
    return customerOrSupplierShopId;
  }

  public void setCustomerOrSupplierShopId(Long customerOrSupplierShopId) {
    this.customerOrSupplierShopId = customerOrSupplierShopId;
  }

  public String getCustomerOrSupplierShopIdStr() {
    return customerOrSupplierShopIdStr;
  }

  public void setCustomerOrSupplierShopIdStr(String customerOrSupplierShopIdStr) {
    this.customerOrSupplierShopIdStr = customerOrSupplierShopIdStr;
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  public QuotedPreBuyOrderItemDTO getMyQuotedPreBuyOrderItemDTO() {
    return myQuotedPreBuyOrderItemDTO;
  }

  public void setMyQuotedPreBuyOrderItemDTO(QuotedPreBuyOrderItemDTO myQuotedPreBuyOrderItemDTO) {
    this.myQuotedPreBuyOrderItemDTO = myQuotedPreBuyOrderItemDTO;
    if(myQuotedPreBuyOrderItemDTO!=null){
      myQuoted=true;
    }
  }

  public boolean isMyQuoted() {
    return myQuoted;
  }

  public void setMyQuoted(boolean myQuoted) {
    this.myQuoted = myQuoted;
  }

  public void setBusinessChanceType(String businessChanceType) {
    if (StringUtils.isEmpty(businessChanceType)) return;
    try {
      this.businessChanceTypeValue = BusinessChanceType.valueOf(businessChanceType).getName();
      this.businessChanceType = businessChanceType;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public String getBusinessChanceType() {
    return businessChanceType;
  }

  public String getBusinessChanceTypeValue() {
    return businessChanceTypeValue;
  }

  public void setBusinessChanceTypeValue(String businessChanceTypeValue) {
    this.businessChanceTypeValue = businessChanceTypeValue;
  }
}
