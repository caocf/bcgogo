package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-12-7
 * Time: 下午12:43
 * To change this template use File | Settings | File Templates.
 */
public class ProductHistoryDTO implements Serializable{
  private Long id;
  private Long productId;
  private Long kindId;
  private String kindName;
  private String brand;
  private String model;
  private String spec;
  private String name;
  private String nameEn;
  private String mfr;
  private String mfrEn;
  private Integer originNo;
  private String origin;
  private String unit;
  private Long parentId;
  private Integer checkStatus;
  private Long state;
  private String productVehicleBrand;
  private String productVehicleModel;
  private String productVehicleYear;
  private String productVehicleEngine;
  private String memo;
  private String firstLetter;      //产品名称首字母
  private String firstLetterCombination;   //产品名称首字母组合
  private Long shopId;
  private Integer productVehicleStatus;
  private String barcode;
	private String commodityCode;//商品编码
	private ProductStatus status;   //商品状态
  private Long productVersion;

  //ProductLocalInfo
  private Long productLocalInfoId;
  private String storageUnit;
  private String sellUnit;
  private Long rate;
  private Double percentage;
  private Double percentageAmount;
  private String pointsExchangeable;
	private Double tradePrice;   //批发价
	private String storageBin;// 仓位
  private Long businessCategoryId;
  private String businessCategoryName;
  private Long productLocalInfoVersion;
  private Double purchasePrice;
  private Double inSalesPrice;
  private String inSalesUnit;   //与ProductDTO同步，为商品详情页面展示.实际值为unit.
  private String guaranteePeriod;

  //Inventory
  private double inventoryAmount;
  private Double lowerLimit;
  private Double upperLimit;
	private Double noOrderInventory;    //无单据库存, 小微店
  private Double salesPrice;    //销售价
  private Double latestInventoryPrice;    //最近入库价
  private Double inventoryAveragePrice;   //库存平均价
  private Long inventoryVersion;

  private List<PromotionOrderRecordDTO> promotionOrderRecordDTOs;  // 促销记录列表 add by zhuj 从promotionsRecord表中获取

  /* add by zhuj  */
  private Double inSalesPriceAfterCal; // 计算促销以后的价格
  private String promotionTypesShortStr; // 页面显示用的 促销短信息拼接的str 对应枚举为 PromotionsTypesShort
  private String promotionContent; // 页面显示的促销内容str
  private String historyCreateTime; // 产品历史创建时间
  private String lastUpdateTime; // 单个产品下 最有一个历史记录的创建时间
  private String productInfoStr;
  private Double inSalesAmount;
  private Boolean hasBargain=false;

  private ImageCenterDTO imageCenterDTO;
  private String promotionTypesStr;
  private Long productCategoryId;

  private ProductCategoryDTO productCategoryDTO;
  private String productCategoryInfo;

  public Double getInSalesAmount() {
    return inSalesAmount;
  }

  public void setInSalesAmount(Double inSalesAmount) {
    this.inSalesAmount = inSalesAmount;
  }

  public Boolean getHasBargain() {
    return hasBargain;
  }

  public void setHasBargain(Boolean hasBargain) {
    this.hasBargain = hasBargain;
  }

  /* add end */

  private String description; // 商品描述

  public String generateProductInfo() {
    String productInfoStr = StringUtil.truncValue(this.getCommodityCode()) + " " + StringUtil.truncValue(this.getName()) + " " + StringUtil.truncValue(this.getBrand()) + " " + StringUtil.truncValue(this.getSpec()) + " " +
        StringUtil.truncValue(this.getModel()) + " " + StringUtil.truncValue(this.getProductVehicleModel()) + " " + StringUtil.truncValue(this.getProductVehicleBrand());
    this.setProductInfoStr(productInfoStr);
    return productInfoStr;
  }

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  public String getProductInfoStr() {
    return productInfoStr;
  }

  public void setProductInfoStr(String productInfoStr) {
    this.productInfoStr = productInfoStr;
  }

  public Double getInSalesPriceAfterCal() {
    return inSalesPriceAfterCal;
  }

  public void setInSalesPriceAfterCal(Double inSalesPriceAfterCal) {
    this.inSalesPriceAfterCal = inSalesPriceAfterCal;
  }

  public String getPromotionTypesShortStr() {
    return promotionTypesShortStr;
  }

  public void setPromotionTypesShortStr(String promotionTypesShortStr) {
    this.promotionTypesShortStr = promotionTypesShortStr;
  }

  public String getPromotionContent() {
    return promotionContent;
  }

  public void setPromotionContent(String promotionContent) {
    this.promotionContent = promotionContent;
  }

    public String getHistoryCreateTime() {
    return historyCreateTime;
  }

  public void setHistoryCreateTime(String historyCreateTime) {
    this.historyCreateTime = historyCreateTime;
  }

  public String getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(String lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<PromotionOrderRecordDTO> getPromotionOrderRecordDTOs() {
    return promotionOrderRecordDTOs;
  }

  public void setPromotionOrderRecordDTOs(List<PromotionOrderRecordDTO> promotionOrderRecordDTOs) {
    this.promotionOrderRecordDTOs = promotionOrderRecordDTOs;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getKindId() {
    return kindId;
  }

  public void setKindId(Long kindId) {
    this.kindId = kindId;
  }

  public String getKindName() {
    return kindName;
  }

  public void setKindName(String kindName) {
    this.kindName = kindName;
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

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getMfr() {
    return mfr;
  }

  public void setMfr(String mfr) {
    this.mfr = mfr;
  }

  public String getMfrEn() {
    return mfrEn;
  }

  public void setMfrEn(String mfrEn) {
    this.mfrEn = mfrEn;
  }

  public Integer getOriginNo() {
    return originNo;
  }

  public void setOriginNo(Integer originNo) {
    this.originNo = originNo;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
    this.inSalesUnit = unit;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public Integer getCheckStatus() {
    return checkStatus;
  }

  public void setCheckStatus(Integer checkStatus) {
    this.checkStatus = checkStatus;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
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

  public String getProductVehicleYear() {
    return productVehicleYear;
  }

  public void setProductVehicleYear(String productVehicleYear) {
    this.productVehicleYear = productVehicleYear;
  }

  public String getProductVehicleEngine() {
    return productVehicleEngine;
  }

  public void setProductVehicleEngine(String productVehicleEngine) {
    this.productVehicleEngine = productVehicleEngine;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  public String getFirstLetterCombination() {
    return firstLetterCombination;
  }

  public void setFirstLetterCombination(String firstLetterCombination) {
    this.firstLetterCombination = firstLetterCombination;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
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

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
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

  public Double getPercentage() {
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public String getPointsExchangeable() {
    return pointsExchangeable;
  }

  public void setPointsExchangeable(String pointsExchangeable) {
    this.pointsExchangeable = pointsExchangeable;
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

  public double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
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

  public Double getNoOrderInventory() {
    return noOrderInventory;
  }

  public void setNoOrderInventory(Double noOrderInventory) {
    this.noOrderInventory = noOrderInventory;
  }

  public Double getSalesPrice() {
    return salesPrice;
  }

  public void setSalesPrice(Double salesPrice) {
    this.salesPrice = salesPrice;
  }

  public Double getLatestInventoryPrice() {
    return latestInventoryPrice;
  }

  public void setLatestInventoryPrice(Double latestInventoryPrice) {
    this.latestInventoryPrice = latestInventoryPrice;
  }

  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

  public Long getProductVersion() {
    return productVersion;
  }

  public void setProductVersion(Long productVersion) {
    this.productVersion = productVersion;
  }

  public Long getProductLocalInfoVersion() {
    return productLocalInfoVersion;
  }

  public void setProductLocalInfoVersion(Long productLocalInfoVersion) {
    this.productLocalInfoVersion = productLocalInfoVersion;
  }

  public Long getInventoryVersion() {
    return inventoryVersion;
  }

  public void setInventoryVersion(Long inventoryVersion) {
    this.inventoryVersion = inventoryVersion;
  }

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public Double getInSalesPrice() {
    return inSalesPrice;
  }

  public void setInSalesPrice(Double inSalesPrice) {
    this.inSalesPrice = inSalesPrice;
  }

  public String getGuaranteePeriod() {
    return guaranteePeriod;
  }

  public void setGuaranteePeriod(String guaranteePeriod) {
    this.guaranteePeriod = guaranteePeriod;
  }

  /**
   * 比对产品与历史单据中相应产品信息是否一致. 比较字段包括：
   * name, brand, model, spec, vehicle_brand, vehicle_model
   * @param productDTO
   */
  public boolean compareSame(ProductDTO productDTO) {
    if(!StringUtil.compareSame(getName(), productDTO.getName())){
      return false;
    }
    if(!StringUtil.compareSame(getBrand(), productDTO.getBrand())){
      return false;
    }
    if(!StringUtil.compareSame(getModel(), productDTO.getModel())){
      return false;
    }
    if(!StringUtil.compareSame(getSpec(), productDTO.getSpec())){
      return false;
    }
    if(!StringUtil.compareSame(getProductVehicleBrand(), productDTO.getProductVehicleBrand())){
      return false;
    }
    if(!StringUtil.compareSame(getProductVehicleModel(), productDTO.getProductVehicleModel())){
      return false;
    }
    return true;
  }

  public void setPromotionTypesStr(String promotionTypesStr) {
    this.promotionTypesStr = promotionTypesStr;
  }

  public String getPromotionTypesStr() {
    return promotionTypesStr;
  }

  public String getInSalesUnit() {
    return inSalesUnit;
  }

  public void setInSalesUnit(String inSalesUnit) {
    this.inSalesUnit = inSalesUnit;
  }

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public void setProductCategoryDTO(ProductCategoryDTO productCategoryDTO) {
    this.productCategoryDTO = productCategoryDTO;
    if(productCategoryDTO!=null){
      if(ProductCategoryType.SECOND_CATEGORY.equals(productCategoryDTO.getCategoryType())){
        productCategoryInfo = productCategoryDTO.getFirstCategoryName()+" >> "+productCategoryDTO.getSecondCategoryName();
      }else if(ProductCategoryType.THIRD_CATEGORY.equals(productCategoryDTO.getCategoryType())){
        productCategoryInfo = productCategoryDTO.getFirstCategoryName()+" >> "+productCategoryDTO.getSecondCategoryName()+" >> "+productCategoryDTO.getThirdCategoryName();
      }
    }
  }

  public ProductCategoryDTO getProductCategoryDTO() {
    return productCategoryDTO;
  }

  public String getProductCategoryInfo() {
    return productCategoryInfo;
  }

  public void setProductCategoryInfo(String productCategoryInfo) {
    this.productCategoryInfo = productCategoryInfo;
  }
}
