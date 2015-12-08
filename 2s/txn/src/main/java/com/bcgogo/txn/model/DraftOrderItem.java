package com.bcgogo.txn.model;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.DraftOrderItemDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-8
 * Time: 上午3:28
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "draft_order_item")
public class DraftOrderItem extends LongIdentifier {
  private Long draftOrderId;
  private Long productLocalInfoId;
  private Long supplierProductLocalInfoId;
  private Long itemId;//txn的
  //  private Long productId;
  private double amount;
  private double price;
  private Double returnAmount;
  private Double returnPrice;
  private double total;
  private String memo;
  private String unit;

  private String brand;
  private String model;
  private String spec;
  private String productType;
  private String productName;
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleYear;
  private String vehicleEngine;
  private String storageUnit;
  private String sellUnit;
  private Long rate;

  private Double reserved;
  private Long shopId;
  private Double costPrice;
  private Double totalCostPrice;
  private Double percentage;
  private Double percentageAmount;
  private Double recommendedPrice;
  private Double lowerLimit;
  private Double upperLimit;



  //施工单服务项目
  private Long serviceId;
  private String service;
  private String workers;
  private String workerIds;
  private ConsumeType consumeType;
  private ItemTypes itemTypes;

  private String storageBin;
  private Double tradePrice;

  private String commodityCode;//商品编码
  private Long businessCategoryId;
  private Long productKindId;

  //商品出入库打通 保存下拉供应商使用数量,supplierId--useRelatedAmount的map形式
  private String   useAmountJson;


  //施工单相关
  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时单价
  private Double actualHours;//实际工时


  @Column(name = "draft_order_id")
  public Long getDraftOrderId() {
    return draftOrderId;
  }

  public void setDraftOrderId(Long draftOrderId) {
    this.draftOrderId = draftOrderId;
  }
  @Column(name = "item_id")
  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }
//  @Column(name = "product_id")
//  public Long getProductId() {
//    return productId;
//  }
//
//  public void setProductId(Long productId) {
//    this.productId = productId;
//  }

  @Column(name = "product_local_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  @Column(name = "amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name = "price")
  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Column(name = "return_amount")
  public Double getReturnAmount() {
    return returnAmount;
  }

  public void setReturnAmount(Double returnAmount) {
    this.returnAmount = returnAmount;
  }

    @Column(name = "return_price")
  public Double getReturnPrice() {
    return returnPrice;
  }

  public void setReturnPrice(Double returnPrice) {
    this.returnPrice = returnPrice;
  }


  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }


  @Column(name = "reserved")
  public Double getReserved() {
    return reserved;
  }

  public void setReserved(Double reserved) {
    this.reserved = reserved;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "cost_price")
  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  @Column(name = "total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  @Column(name = "percentage")
  public Double getPercentage() {
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  @Column(name = "percentage_amount")
  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  @Column(name = "vehicle")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name = "vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name = "vehicle_year")
  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }

  @Column(name = "vehicle_engine")
  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "service")
  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  @Column(name = "workers")
  public String getWorkers() {
    return workers;
  }

  public void setWorkers(String workers) {
    this.workers = workers;
  }

  @Column(name = "worker_ids")
  public String getWorkerIds() {
    return workerIds;
  }

  public void setWorkerIds(String workerIds) {
    this.workerIds = workerIds;
  }
  @Column(name = "item_type")
  @Enumerated(EnumType.STRING)
  public ItemTypes getItemTypes() {
    return itemTypes;
  }

  public void setItemTypes(ItemTypes itemTypes) {
    this.itemTypes = itemTypes;
  }

  @Column(name = "consume_type")
  @Enumerated(EnumType.STRING)
  public ConsumeType getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(ConsumeType consumeType) {
    this.consumeType = consumeType;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "storage_unit")
  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  @Column(name = "sell_unit")
  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    this.sellUnit = sellUnit;
  }

  @Column(name = "rate")
  public Long getRate() {
    return rate;
  }

  public void setRate(Long rate) {
    this.rate = rate;
  }

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "spec")
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "product_type")
  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  @Column(name = "recommended_price")
  public Double getRecommendedPrice() {
    return recommendedPrice;
  }

  public void setRecommendedPrice(Double recommendedPrice) {
    this.recommendedPrice = recommendedPrice;
  }

  @Column(name = "lower_limit")
  public Double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(Double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  @Column(name = "upper_limit")
  public Double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(Double upperLimit) {
    this.upperLimit = upperLimit;
  }

  @Column(name = "storage_bin")
  public String getStorageBin() {
    return storageBin;
  }

  public void setStorageBin(String storageBin) {
    this.storageBin = storageBin;
  }

  @Column(name = "trade_price")
  public Double getTradePrice() {
    if(tradePrice==null) return 0D;
    return tradePrice;
  }

  public void setTradePrice(Double tradePrice) {
    this.tradePrice = tradePrice;
  }

  @Column(name = "commodity_code", length = 50)
  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  @Column(name = "business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name="product_kind_id")
  public Long getProductKindId() {
    return productKindId;
  }

  public void setProductKindId(Long productKindId) {
    this.productKindId = productKindId;
  }

  @Column(name="use_amount_json")
  public String getUseAmountJson() {
    return useAmountJson;
  }

  public void setUseAmountJson(String useAmountJson) {
    this.useAmountJson = useAmountJson;
  }

  @Column(name="standard_hours")
  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  @Column(name="standard_unit_price")
  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  @Column(name="actual_hours")
  public Double getActualHours() {
    return actualHours;
  }

  public void setActualHours(Double actualHours) {
    this.actualHours = actualHours;
  }


  public DraftOrderItem fromDTO(DraftOrderItemDTO draftOrderItemDTO){
    this.setProductLocalInfoId(draftOrderItemDTO.getProductLocalInfoId());
    this.setItemId(draftOrderItemDTO.getItemId());
    this.setBrand(draftOrderItemDTO.getBrand());
    this.setModel(draftOrderItemDTO.getModel());
    this.setSpec(draftOrderItemDTO.getSpec());
    this.setProductType(draftOrderItemDTO.getProductType());
    this.setProductName(draftOrderItemDTO.getProductName());
    this.setRate(draftOrderItemDTO.getRate());
    this.setSellUnit(draftOrderItemDTO.getSellUnit());
    this.setStorageUnit(draftOrderItemDTO.getStorageUnit());
    this.setUnit(draftOrderItemDTO.getUnit());
    this.setCommodityCode(draftOrderItemDTO.getCommodityCode());
    this.setDraftOrderId(draftOrderItemDTO.getDraftOrderId());
    this.setAmount(draftOrderItemDTO.getAmount());
    this.setPrice(draftOrderItemDTO.getPrice());
    this.setReturnAmount(draftOrderItemDTO.getReturnAmount());
    this.setReturnPrice(draftOrderItemDTO.getReturnPrice());
    this.setTotal(draftOrderItemDTO.getTotal());
    this.setMemo(draftOrderItemDTO.getMemo());
    this.setReserved(draftOrderItemDTO.getReserved());
    this.setShopId(draftOrderItemDTO.getShopId());
    this.setCostPrice(draftOrderItemDTO.getCostPrice());
    this.setTotalCostPrice(draftOrderItemDTO.getTotalCostPrice());
    this.setPercentage(draftOrderItemDTO.getPercentage());
    this.setPercentageAmount(draftOrderItemDTO.getPercentageAmount());
    this.setVehicleBrand(draftOrderItemDTO.getVehicleBrand());
    this.setVehicleModel(draftOrderItemDTO.getVehicleModel());
    this.setVehicleYear(draftOrderItemDTO.getVehicleYear());
    this.setVehicleEngine(draftOrderItemDTO.getVehicleEngine());
    this.setConsumeType(draftOrderItemDTO.getConsumeType());
    this.setService(draftOrderItemDTO.getService());
    this.setServiceId(draftOrderItemDTO.getServiceId());
    this.setWorkerIds(draftOrderItemDTO.getWorkerIds());
    this.setWorkers(draftOrderItemDTO.getWorkers());
    this.setRecommendedPrice(draftOrderItemDTO.getRecommendedPrice());
    this.setItemTypes(draftOrderItemDTO.getItemTypes());
    this.setLowerLimit(draftOrderItemDTO.getLowerLimit());
    this.setUpperLimit(draftOrderItemDTO.getUpperLimit());
    this.setStorageBin(draftOrderItemDTO.getStorageBin());
    this.setTradePrice(draftOrderItemDTO.getTradePrice());
    this.setBusinessCategoryId(draftOrderItemDTO.getBusinessCategoryId());
    this.setProductKindId(draftOrderItemDTO.getProductKindId());
    this.setSupplierProductLocalInfoId(draftOrderItemDTO.getSupplierProductLocalInfoId());
    this.setUseAmountJson(draftOrderItemDTO.getUseAmountJson());

    this.setStandardHours(draftOrderItemDTO.getStandardHours());
    this.setStandardUnitPrice(draftOrderItemDTO.getStandardUnitPrice());
    this.setActualHours(draftOrderItemDTO.getActualHours());
    return this;
  }

  public DraftOrderItemDTO toDTO(){
    DraftOrderItemDTO draftOrderItemDTO=new DraftOrderItemDTO();
    draftOrderItemDTO.setItemId(this.getItemId());
    draftOrderItemDTO.setBrand(this.getBrand());
    draftOrderItemDTO.setProductName(this.getProductName());
    draftOrderItemDTO.setModel(this.getModel());
    draftOrderItemDTO.setSpec(this.getSpec());
    draftOrderItemDTO.setProductType(this.getProductType());
    draftOrderItemDTO.setSellUnit(this.getSellUnit());
    draftOrderItemDTO.setStorageUnit(this.getStorageUnit());
    draftOrderItemDTO.setRate(this.getRate());

    draftOrderItemDTO.setDraftOrderId(this.getDraftOrderId());
    draftOrderItemDTO.setProductLocalInfoId(this.getProductLocalInfoId());
    draftOrderItemDTO.setProductId(this.getProductLocalInfoId()); //todo write
    draftOrderItemDTO.setAmount(this.getAmount());
    draftOrderItemDTO.setPrice(this.getPrice());
    draftOrderItemDTO.setTotal(this.getTotal());
    draftOrderItemDTO.setReturnPrice(this.getReturnPrice());
    draftOrderItemDTO.setReturnAmount(this.getReturnAmount());
    draftOrderItemDTO.setMemo(this.getMemo());
    draftOrderItemDTO.setUnit(this.getUnit());
    draftOrderItemDTO.setReserved(this.getReserved()==null?0d:this.getReserved());
    draftOrderItemDTO.setShopId(this.getShopId());
    draftOrderItemDTO.setCostPrice(this.getCostPrice());
    draftOrderItemDTO.setTotalCostPrice(this.getTotalCostPrice());
    draftOrderItemDTO.setPercentage(this.getPercentage());
    draftOrderItemDTO.setPercentageAmount(this.getPercentageAmount());
    draftOrderItemDTO.setVehicleBrand(this.getVehicleBrand());
    draftOrderItemDTO.setVehicleModel(this.getVehicleModel());
    draftOrderItemDTO.setVehicleYear(this.getVehicleYear());
    draftOrderItemDTO.setVehicleEngine(this.getVehicleEngine());
    draftOrderItemDTO.setRecommendedPrice(this.getRecommendedPrice());
    draftOrderItemDTO.setItemTypes(this.getItemTypes());
    draftOrderItemDTO.setLowerLimit(this.getLowerLimit());
    draftOrderItemDTO.setUpperLimit(this.getUpperLimit());
    draftOrderItemDTO.setStorageBin(this.getStorageBin());
    draftOrderItemDTO.setTradePrice(this.getTradePrice());

    draftOrderItemDTO.setServiceId(this.getServiceId());
    draftOrderItemDTO.setService(this.getService());
    draftOrderItemDTO.setWorkerIds(this.getWorkerIds());
    draftOrderItemDTO.setWorkers(this.getWorkers());
    draftOrderItemDTO.setConsumeType(this.getConsumeType());
    draftOrderItemDTO.setTotal(this.getTotal());
    draftOrderItemDTO.setCommodityCode(this.getCommodityCode());

    draftOrderItemDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    draftOrderItemDTO.setProductKindId(this.getProductKindId());
    draftOrderItemDTO.setSupplierProductLocalInfoId(this.getSupplierProductLocalInfoId());

    draftOrderItemDTO.setUseAmountJson(this.getUseAmountJson());

    draftOrderItemDTO.setStandardHours(this.getStandardHours());
    draftOrderItemDTO.setStandardUnitPrice(this.getStandardUnitPrice());
    draftOrderItemDTO.setActualHours(this.getActualHours());
    return draftOrderItemDTO;
  }


  @Column(name = "supplier_product_local_info_id")
  public Long getSupplierProductLocalInfoId() {
    return supplierProductLocalInfoId;
  }

  public void setSupplierProductLocalInfoId(Long supplierProductLocalInfoId) {
    this.supplierProductLocalInfoId = supplierProductLocalInfoId;
  }
}
