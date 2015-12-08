package com.bcgogo.txn.dto;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.utils.UnitUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-10
 * Time: 上午2:01
 * To change this template use File | Settings | File Templates.
 */
public class DraftOrderItemDTO extends BcgogoOrderItemDto {
  private String idStr;
  private Long itemId;
  private Long draftOrderId;
  private Long productLocalInfoId;
  private Long supplierProductLocalInfoId;

  private String productType;
  private Double inventoryAmount;  //库存剩余数量
  private Double price;//单据中每个item的单价
  private Double purchasePriceForSale;//目前只用在销售单的hidden中
  private Double total;
  private String memo;

  private Long shopId;
  private Double costPrice;   //
  private Double totalCostPrice;   //施工单 销售单
  private Double percentage;   //施工单 销售单
  private Double percentageAmount;


  //施工单服务项目
  private Long serviceId;
  private String service;
  private String workers;
  private String workerIds;
  private ConsumeType consumeType;
  private ItemTypes itemTypes;

  private Double recommendedPrice;
  private Double lowerLimit;
  private Double upperLimit;

  //	private String commodityCode;//商品编码
//	private boolean commodityCodeModifyFlag = true;//商品编码修改Flag true 表示用草稿箱的编码，false表示用自己原先的编码
  private Long businessCategoryId;
  private String businessCategoryName;
  private Long productKindId;
  private String productKind;

  private Double returnAmount;
  private Double returnPrice;

  //商品出入库打通 保存下拉供应商使用数量,supplierId--useRelatedAmount的map形式
  private String   useAmountJson;

  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时单价
  private Double actualHours;//实际工时

  public Long getDraftOrderId() {
    return draftOrderId;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public void setDraftOrderId(Long draftOrderId) {
    this.draftOrderId = draftOrderId;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  public Double getPurchasePriceForSale() {
    return purchasePriceForSale;
  }

  public void setPurchasePriceForSale(Double purchasePriceForSale) {
    this.purchasePriceForSale = purchasePriceForSale;
  }

  public ItemTypes getItemTypes() {
    return itemTypes;
  }

  public void setItemTypes(ItemTypes itemTypes) {
    this.itemTypes = itemTypes;
  }

  public Double getInventoryAmount() {
    if(inventoryAmount==null){
      return 0D;
    }
    return inventoryAmount;
  }

  public void setInventoryAmount(Double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
  }

  public Double getPrice() {
    if(price==null){
      return 0D;
    }
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public Double getTotal() {
    if(total==null){
      return 0D;
    }
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getCostPrice() {
    if(costPrice==null){
      return 0D;
    }
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  public Double getTotalCostPrice() {
    if(totalCostPrice==null){
      return null;
    }
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  public Double getPercentage() {
    if(percentage==null){
      return 0D;
    }
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public Double getPercentageAmount() {
    if(percentageAmount==null){
      return 0D;
    }
    return percentageAmount;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

//  public String getVehicleBrand() {
//    return vehicleBrand;
//  }
//
//  public void setVehicleBrand(String vehicleBrand) {
//    this.vehicleBrand = vehicleBrand;
//  }
//
//  public String getVehicleModel() {
//    return vehicleModel;
//  }
//
//  public void setVehicleModel(String vehicleModel) {
//    this.vehicleModel = vehicleModel;
//  }
//
//  public String getVehicleYear() {
//    return vehicleYear;
//  }
//
//  public void setVehicleYear(String vehicleYear) {
//    this.vehicleYear = vehicleYear;
//  }
//
//  public String getVehicleEngine() {
//    return vehicleEngine;
//  }
//
//  public void setVehicleEngine(String vehicleEngine) {
//    this.vehicleEngine = vehicleEngine;
//  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getWorkers() {
    return workers;
  }

  public void setWorkers(String workers) {
    this.workers = workers;
  }

  public String getWorkerIds() {
    return workerIds;
  }

  public void setWorkerIds(String workerIds) {
    this.workerIds = workerIds;
  }

  public ConsumeType getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(ConsumeType consumeType) {
    this.consumeType = consumeType;
  }

  public Double getRecommendedPrice() {
    return recommendedPrice;
  }

  public void setRecommendedPrice(Double recommendedPrice) {
    this.recommendedPrice = recommendedPrice;
  }

  public Double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(Double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public Double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(Double upperLimit) {
    this.upperLimit = upperLimit;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public Double getReturnAmount() {
    return returnAmount;
  }

  public void setReturnAmount(Double returnAmount) {
    this.returnAmount = returnAmount;
  }

  public Double getReturnPrice() {
    return returnPrice;
  }

  public void setReturnPrice(Double returnPrice) {
    this.returnPrice = returnPrice;
  }

  //	public String getCommodityCode() {
//		return commodityCode;
//	}
//
//	public void setCommodityCode(String commodityCode) {
//		this.commodityCode = commodityCode;
//	}

//	public boolean getCommodityCodeModifyFlag() {
//		return commodityCodeModifyFlag;
//	}
//
//	public void setCommodityCodeModifyFlag(boolean commodityCodeModifyFlag) {
//		this.commodityCodeModifyFlag = commodityCodeModifyFlag;
//	}


  public String getUseAmountJson() {
    return useAmountJson;
  }

  public void setUseAmountJson(String useAmountJson) {
    this.useAmountJson = useAmountJson;
  }


    public Double getStandardHours() {
        return standardHours;
    }

    public void setStandardHours(Double standardHours) {
        this.standardHours = standardHours;
    }

    public Double getStandardUnitPrice() {
        return standardUnitPrice;
    }

    public void setStandardUnitPrice(Double standardUnitPrice) {
        this.standardUnitPrice = standardUnitPrice;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    public DraftOrderItemDTO toLastedProductAndInventoryInfo(ProductDTO productDTO,InventoryDTO inventoryDTO,DraftOrderDTO draftOrderDTO){
    this.setProductLocalInfoId(productDTO.getProductLocalInfoId());
    this.setBrand(productDTO.getBrand());
    this.setModel(productDTO.getModel());
    this.setSpec(productDTO.getSpec());
    this.setProductName(productDTO.getName());
    this.setProductType(String.valueOf(productDTO.getProductVehicleStatus()));
    this.setVehicleBrand(productDTO.getProductVehicleBrand());
    this.setVehicleModel(productDTO.getProductVehicleModel());
    this.setVehicleYear(productDTO.getProductVehicleYear());
    this.setVehicleEngine(productDTO.getProductVehicleEngine());
//    this.setSellUnit(productDTO.getSellUnit());
    this.setStorageUnit(productDTO.getStorageUnit());
    this.setRate(productDTO.getRate());

    if(inventoryDTO!=null){
      this.setInventoryAmount(inventoryDTO.getAmount());
      this.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
      if(UnitUtil.isStorageUnit(this.getUnit(),productDTO)){
        this.setInventoryAmount(inventoryDTO.getAmount() / productDTO.getRate());
        this.setPurchasePriceForSale((productDTO.getPurchasePrice() == null ? 0d : productDTO.getPurchasePrice())* productDTO.getRate());
      }else {
        this.setInventoryAmount(inventoryDTO.getAmount());
        this.setPurchasePriceForSale(productDTO.getPurchasePrice() == null ? 0d : productDTO.getPurchasePrice());
      }
    }

    if (draftOrderDTO.getOrderTypeEnum().toString().equals("SALE") || draftOrderDTO.getOrderTypeEnum().toString().equals("REPAIR")){
      this.setStorageBin(productDTO.getStorageBin());
    }
    return this;
  }

  public Long getSupplierProductLocalInfoId() {
    return supplierProductLocalInfoId;
  }

  public void setSupplierProductLocalInfoId(Long supplierProductLocalInfoId) {
    this.supplierProductLocalInfoId = supplierProductLocalInfoId;
  }
}
