package com.bcgogo.search.dto;

import com.bcgogo.common.PojoCommon;
import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.StoreHouseInventoryDTO;
import com.bcgogo.txn.dto.SupplierInventoryDTO;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-1-6
 * Time: 下午9:05
 * To change this template use File | Settings | File Templates.
 */
public class InventorySearchIndexDTO implements Serializable {
  private Long id;
  private Long shopId;         //店面ID
  private Long productId;
  private Long editDate; // 最新入库时间
  private String editDateStr;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private Long brandId;
  private Long modelId;
  private String brand;
  private String model;
  private String year;
  private String engine;
  private Double amount,amountApprox;//库存量
  private String pageStatus;
  private Integer maxResult;
  private Integer productVehicleStatus;
  private Integer indexNum;//搜索产品的按钮所在行数
  private Boolean vehicleNull;
  private String orderType;
  private Double price;
  private Double purchasePrice,purchasePriceApprox;
  private Long parentProductId;
  private Double recommendedPrice,recommendedPriceApprox;
  private String barcode;
  private String sortStatus;
  private String unit;
  private String storageUnit;
  private String sellUnit;
  private Long rate;
  private Long lastModified;

  private String productIdStr;
  private String parentProductIdStr;

  private Double lowerLimit;
  private Double upperLimit;
	private Double tradePrice;   //批发价
	private String storageBin;// 仓位
  private Double inventoryAveragePrice; //库存平均价
	private String commodityCode;//商品编码
	private Long supplierProductId;  //供应商产品id
	private String supplierProductIdStr;  //供应商产品id
  private Double supplierTradePrice;
  private Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap;//key  仓库id
  private List<PromotionsDTO> promotionsDTOs;
  private ProductStatus salesStatus;
  private List<SupplierInventoryDTO> supplierInventoryDTOs ;
  private Double maxPurchasePrice;
  private Double minPurchasePrice;
  private ImageCenterDTO imageCenterDTO;

  public InventorySearchIndexDTO setLocalProduct(ProductDTO productDTO) {
		this.setProductId(productDTO.getProductLocalInfoId());
		this.setStorageBin(productDTO.getStorageBin());
		this.setTradePrice(productDTO.getTradePrice());
		return this;
	}

  public InventorySearchIndexDTO setInventoryDTO(InventoryDTO inventoryDTO){
    if (inventoryDTO != null) {
      this.setAmount(NumberUtil.doubleVal(inventoryDTO.getAmount()));
      this.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
      this.setRecommendedPrice(inventoryDTO.getSalesPrice());
      this.setLowerLimit(inventoryDTO.getLowerLimit());
      this.setUpperLimit(inventoryDTO.getUpperLimit());
      this.setPurchasePrice(inventoryDTO.getLatestInventoryPrice());
    }else{
      this.setAmount(0d);
    }
    return this;
  }

  public InventorySearchIndexDTO setProductDTO(ProductDTO productDTO) {
    if(productDTO !=null){
      this.setSellUnit(productDTO.getSellUnit());
      this.setUnit(productDTO.getSellUnit());
      this.setStorageUnit(productDTO.getStorageUnit());
      this.setRate(productDTO.getRate());
      this.setCommodityCode(productDTO.getCommodityCode());
      this.setProductName(productDTO.getName());
      this.setProductBrand(productDTO.getBrand());
      this.setProductModel(productDTO.getModel());
      this.setProductSpec(productDTO.getSpec());
      this.setBrand(productDTO.getProductVehicleBrand());
      this.setModel(productDTO.getProductVehicleModel());
      this.setId(productDTO.getProductLocalInfoId());
      this.setProductId(productDTO.getProductLocalInfoId());
      this.setTradePrice(productDTO.getTradePrice());
      this.setBusinessCategoryName(productDTO.getBusinessCategoryName());
      this.setBusinessCategoryIdStr(productDTO.getBusinessCategoryId() == null ? "" : productDTO.getBusinessCategoryId().toString());
      this.setKindName(productDTO.getKindName());
      this.setStatus(productDTO.getStatus());
      this.setSalesStatus(productDTO.getSalesStatus());
      this.setStorageBin(productDTO.getStorageBin());
      this.setImageCenterDTO(productDTO.getImageCenterDTO());
    }
    return this;
  }

	public InventorySearchIndexDTO setSupplierProduct(ProductDTO productDTO) {
		this.setSellUnit(productDTO.getSellUnit());
		this.setStorageUnit(productDTO.getStorageUnit());
		this.setRate(productDTO.getRate());
		this.setCommodityCode(productDTO.getCommodityCode());
		this.setProductName(productDTO.getName());
		this.setProductBrand(productDTO.getBrand());
		this.setProductModel(productDTO.getModel());
		this.setProductSpec(productDTO.getSpec());
		this.setBrand(productDTO.getProductVehicleBrand());
		this.setModel(productDTO.getProductVehicleModel());
		this.setSupplierProductId(productDTO.getProductLocalInfoId());
    this.setSupplierTradePrice(productDTO.getTradePrice());
    this.setSalesStatus(productDTO.getSalesStatus());
		return this;
	}
  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

	private ProductStatus status;
  private String kindName;//商品分类名称

  private String businessCategoryIdStr;
  private String businessCategoryName;

  public ProductStatus getSalesStatus() {
    return salesStatus;
  }

  public void setSalesStatus(ProductStatus salesStatus) {
    this.salesStatus = salesStatus;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getLastModified() {
    return lastModified;
  }

  public void setLastModified(Long lastModified) {
    this.lastModified = lastModified;
  }

  public String getSortStatus() {
    return sortStatus;
  }

  public void setSortStatus(String sortStatus) {
    this.sortStatus = sortStatus;
  }

  public Long getParentProductId() {
    return parentProductId;
  }

  public void setParentProductId(Long parentProductId) {
    this.parentProductId = parentProductId;
    this.parentProductIdStr = String.valueOf(parentProductId);
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public Boolean getVehicleNull() {
    return vehicleNull;
  }

  public void setVehicleNull(Boolean vehicleNull) {
    this.vehicleNull = vehicleNull;
  }

  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public Integer getIndexNum() {
    return indexNum;
  }

  public void setIndexNum(Integer indexNum) {
    this.indexNum = indexNum;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }

  public Integer getMaxResult() {
    return maxResult;
  }

  public void setMaxResult(Integer maxResult) {
    this.maxResult = maxResult;
  }

  public String getPageStatus() {
    return pageStatus;
  }

  public void setPageStatus(String pageStatus) {
    this.pageStatus = pageStatus;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
    this.productIdStr = String.valueOf(productId);
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
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

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
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

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getAmountApprox() {
    amountApprox=NumberUtil.round(amount,2);
    return amountApprox;
  }

  public void setAmountApprox(Double amountApprox) {
    this.amountApprox = amountApprox;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public Double getPurchasePriceApprox() {
    purchasePriceApprox=NumberUtil.round(purchasePrice,NumberUtil.MONEY_PRECISION);
    return purchasePriceApprox;
  }

  public void setPurchasePriceApprox(Double purchasePriceApprox) {
    this.purchasePriceApprox = purchasePriceApprox;
  }

  public Double getRecommendedPrice() {
    return recommendedPrice;
  }

  public void setRecommendedPrice(Double recommendedPrice) {
    this.recommendedPrice = recommendedPrice;
  }

  public Double getRecommendedPriceApprox() {
    recommendedPriceApprox=NumberUtil.round(recommendedPrice,NumberUtil.MONEY_PRECISION);
    return recommendedPriceApprox;
  }

  public void setRecommendedPriceApprox(Double recommendedPriceApprox) {
    this.recommendedPriceApprox = recommendedPriceApprox;
  }

  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    this.sellUnit = sellUnit;
  }

  public Long getRate() {
    return rate;
  }

  public void setRate(Long rate) {
    this.rate = rate;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getProductIdStr() {
    return productIdStr;
  }

  public void setProductIdStr(String productIdStr) {
    this.productIdStr = productIdStr;
  }

  public String getParentProductIdStr() {
    return parentProductIdStr;
  }

  public void setParentProductIdStr(String parentProductIdStr) {
    this.parentProductIdStr = parentProductIdStr;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

	public Double getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(Double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public String getStorageBin() {
		return storageBin;
	}

	public void setStorageBin(String storageBin) {
		this.storageBin = storageBin;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
	}

  public String getKindName() {
    return kindName;
  }

  public void setKindName(String kindName) {
    this.kindName = kindName;
  }

	public String toJsonStr(){

   // if (StringUtils.isNotBlank(this.getBarcode())) return "";
    return "{\"barcode\":\"" + PojoCommon.toJsonStr(this.getBarcode()) + "\"," +
        "\"productName\":\"" + PojoCommon.toJsonStr(this.getProductName()) + "\"," +
        "\"productId\":\""+PojoCommon.toJsonStr(this.getProductId())+ "\","+
        "\"productBrand\":\"" + PojoCommon.toJsonStr(this.getProductBrand()) + "\"," +
        "\"productSpec\":\"" + PojoCommon.toJsonStr(this.getProductSpec()) + "\"," +
        "\"productModel\":\"" + PojoCommon.toJsonStr(this.getProductModel()) + "\"," +
        "\"productVehicleStatus\":\"" + PojoCommon.toJsonStr(this.getProductVehicleStatus()) + "\"," +
        "\"vehicleBrand\":\"" + PojoCommon.toJsonStr(this.getBrand()) + "\"," +
        "\"vehicleModel\":\"" + PojoCommon.toJsonStr(this.getModel()) + "\"," +
        "\"vehicleYear\":\"" + PojoCommon.toJsonStr(this.getYear()) + "\"," +
        "\"inventoryAmount\":\""+PojoCommon.toJsonStr(this.getAmount())+"\","+
        "\"purchasePrice\":\"" + PojoCommon.toJsonStr(this.getPurchasePrice()) + "\"," +
        "\"price\":\""+PojoCommon.toJsonStr(this.getPrice())+"\","+
        "\"inventoryAveragePrice\":\"" + PojoCommon.toJsonStr(this.getInventoryAveragePrice()) +"\","+
        "\"vehicleEngine\":\"" + PojoCommon.toJsonStr(this.getEngine()) + "\"}";

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

		@Override
	public String toString(){
		return JsonUtil.objectToJson(this);
	}

  public String getBusinessCategoryIdStr() {
    return businessCategoryIdStr;
  }

  public void setBusinessCategoryIdStr(String businessCategoryIdStr) {
    this.businessCategoryIdStr = businessCategoryIdStr;
  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

	public Long getSupplierProductId() {
		return supplierProductId;
	}

	public void setSupplierProductId(Long supplierProductId) {
		this.supplierProductId = supplierProductId;
		if (supplierProductId != null) {
			this.setSupplierProductIdStr(supplierProductId.toString());
		} else {
			this.setSupplierProductIdStr(null);
		}
	}

	public String getSupplierProductIdStr() {
		return supplierProductIdStr;
	}

	public void setSupplierProductIdStr(String supplierProductIdStr) {
		this.supplierProductIdStr = supplierProductIdStr;
	}

  public Map<Long, StoreHouseInventoryDTO> getStoreHouseInventoryDTOMap() {
    return storeHouseInventoryDTOMap;
  }

  public void setStoreHouseInventoryDTOMap(Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap) {
    this.storeHouseInventoryDTOMap = storeHouseInventoryDTOMap;
  }

  public List<PromotionsDTO> getPromotionsDTOs() {
    return promotionsDTOs;
  }

  public void setPromotionsDTOs(List<PromotionsDTO> promotionsDTOs) {
    this.promotionsDTOs = promotionsDTOs;
  }

  public Double getSupplierTradePrice() {
    return supplierTradePrice;
  }

  public void setSupplierTradePrice(Double supplierTradePrice) {
    this.supplierTradePrice = supplierTradePrice;
  }

  public List<SupplierInventoryDTO> getSupplierInventoryDTOs() {
    return supplierInventoryDTOs;
  }

  public void setSupplierInventoryDTOs(List<SupplierInventoryDTO> supplierInventoryDTOs) {
    this.supplierInventoryDTOs = supplierInventoryDTOs;
  }

  public Double getMaxPurchasePrice() {
    return maxPurchasePrice;
  }

  public void setMaxPurchasePrice(Double maxPurchasePrice) {
    this.maxPurchasePrice = maxPurchasePrice;
  }

  public Double getMinPurchasePrice() {
    return minPurchasePrice;
  }

  public void setMinPurchasePrice(Double minPurchasePrice) {
    this.minPurchasePrice = minPurchasePrice;
  }

  public void setMaxAndMinPurchasePrice(List<SupplierInventoryDTO> supplierInventoryDTOs){
    Double maxPurchasePrice = null;
    Double minPurchasePrice = null;
    if (CollectionUtils.isNotEmpty(supplierInventoryDTOs)) {
      for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
        if (maxPurchasePrice == null) {
          maxPurchasePrice = supplierInventoryDTO.getMaxStoragePrice();
        } else if (supplierInventoryDTO.getMaxStoragePrice() != null && maxPurchasePrice < supplierInventoryDTO.getMaxStoragePrice()) {
          maxPurchasePrice = supplierInventoryDTO.getMaxStoragePrice();
        }
        if (minPurchasePrice == null) {
          minPurchasePrice = supplierInventoryDTO.getMinStoragePrice();
        } else if (supplierInventoryDTO.getMinStoragePrice() != null && minPurchasePrice > supplierInventoryDTO.getMinStoragePrice()) {
          minPurchasePrice = supplierInventoryDTO.getMinStoragePrice();
        }
      }
    }
    this.setMaxPurchasePrice(maxPurchasePrice);
    this.setMinPurchasePrice(minPurchasePrice);
  }

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }
}
