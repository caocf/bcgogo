package com.bcgogo.txn.model;

import com.bcgogo.enums.ProductStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.ProductHistoryDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-12-6
 * Time: 下午2:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_history")
public class ProductHistory extends LongIdentifier {
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

  private String description; // 商品描述 add by zhuj
  private Long productCategoryId;


  @Column(name = "in_sales_price")
  public Double getInSalesPrice() {
    return inSalesPrice;
  }

  public void setInSalesPrice(Double inSalesPrice) {
    this.inSalesPrice = inSalesPrice;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProductHistory() {
  }

  public void setProductDTO(ProductDTO productDTO){
    if(productDTO == null){
      return;
    }
    this.productId = productDTO.getId();
    this.kindId = productDTO.getKindId();
    this.kindName = productDTO.getKindName();
    this.brand = productDTO.getBrand();
    this.model = productDTO.getModel();
    this.spec = productDTO.getSpec();
    this.name = productDTO.getName();
    this.nameEn = productDTO.getNameEn();
    this.mfr = productDTO.getMfr();
    this.mfrEn = productDTO.getMfrEn();
    this.originNo = productDTO.getOriginNo();
    this.origin = productDTO.getOrigin();
    this.unit = productDTO.getUnit();
    this.parentId = productDTO.getParentId();
    this.checkStatus = productDTO.getCheckStatus();
    this.state = productDTO.getState();
    this.productVehicleBrand = productDTO.getProductVehicleBrand();
    this.productVehicleModel = productDTO.getProductVehicleModel();
    this.productVehicleYear = productDTO.getProductVehicleYear();
    this.productVehicleEngine = productDTO.getProductVehicleEngine();
    this.memo = productDTO.getMemo();
    this.firstLetter = productDTO.getFirstLetter();
    this.firstLetterCombination = productDTO.getFirstLetterCombination();
    this.shopId = productDTO.getShopId();
    this.productVehicleStatus = productDTO.getProductVehicleStatus();
    this.barcode = productDTO.getBarcode();
    this.commodityCode = productDTO.getCommodityCode();
    this.status = productDTO.getStatus();
    this.productVersion = productDTO.getVersion();
    this.description = productDTO.getDescription();
    this.inSalesPrice = productDTO.getInSalesPrice();
    this.guaranteePeriod = productDTO.getGuaranteePeriod();
  }

  public void setProductLocalInfoDTO(ProductLocalInfoDTO productLocalInfoDTO){
    if(productLocalInfoDTO == null){
      return;
    }
    this.setProductLocalInfoId(productLocalInfoDTO.getId());
    this.setRate(productLocalInfoDTO.getRate());
    this.setSellUnit(productLocalInfoDTO.getSellUnit());
    this.setStorageUnit(productLocalInfoDTO.getStorageUnit());
	  this.setStorageBin(productLocalInfoDTO.getStorageBin());
	  this.setTradePrice(productLocalInfoDTO.getTradePrice());
    this.setBusinessCategoryId(productLocalInfoDTO.getBusinessCategoryId());
    this.setBusinessCategoryName(productLocalInfoDTO.getBusinessCategoryName());
    this.setPercentage(productLocalInfoDTO.getPercentage());
    this.setPercentageAmount(productLocalInfoDTO.getPercentageAmount());
    this.setPointsExchangeable(productLocalInfoDTO.getPointsExchangeable());
    this.setProductLocalInfoVersion(productLocalInfoDTO.getVersion());
    this.setPurchasePrice(productLocalInfoDTO.getPurchasePrice());
    this.setInSalesPrice(productLocalInfoDTO.getInSalesPrice());
    this.setGuaranteePeriod(productLocalInfoDTO.getGuaranteePeriod());
  }

  public void setInventoryDTO(InventoryDTO inventoryDTO){
    if(inventoryDTO==null){
      return;
    }
    this.setInventoryAmount(inventoryDTO.getAmount());
    this.setUpperLimit(inventoryDTO.getUpperLimit());
    this.setLowerLimit(inventoryDTO.getLowerLimit());
	  this.setNoOrderInventory(inventoryDTO.getNoOrderInventory());
    this.setSalesPrice(inventoryDTO.getSalesPrice());
    this.setLatestInventoryPrice(inventoryDTO.getLatestInventoryPrice());
    this.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
    this.setInventoryVersion(inventoryDTO.getVersion());
  }

  @Column(name="product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "product_vehicle_year", length = 10)
  public String getProductVehicleYear() {
    return productVehicleYear;
  }

  public void setProductVehicleYear(String productVehicleYear) {
    this.productVehicleYear = productVehicleYear;
  }

  @Column(name = "product_vehicle_engine", length = 10)
  public String getProductVehicleEngine() {
    return productVehicleEngine;
  }

  public void setProductVehicleEngine(String productVehicleEngine) {
    this.productVehicleEngine = productVehicleEngine;
  }

  @Column(name = "product_vehicle_brand", length = 50)
  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  @Column(name = "product_vehicle_model", length = 50)
  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  @Column(name = "first_letter", length = 100)
  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  @Column(name = "first_letter_combination", length = 200)
  public String getFirstLetterCombination() {
    return firstLetterCombination;
  }

  public void setFirstLetterCombination(String firstLetterCombination) {
    this.firstLetterCombination = firstLetterCombination;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "kind_id")
  public Long getKindId() {
    return kindId;
  }

  public void setKindId(Long kindId) {
    this.kindId = kindId;
  }

  @Column(name="kind_name")
  public String getKindName() {
    return kindName;
  }

  public void setKindName(String kindName) {
    this.kindName = kindName;
  }

  @Column(name = "brand", length = 200)
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "model", length = 200)
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "spec", length = 2000)
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "name", length = 200)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "name_en", length = 200)
  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  @Column(name = "mfr", length = 200)
  public String getMfr() {
    return mfr;
  }

  public void setMfr(String mfr) {
    this.mfr = mfr;
  }

  @Column(name = "mfr_en", length = 200)
  public String getMfrEn() {
    return mfrEn;
  }

  public void setMfrEn(String mfrEn) {
    this.mfrEn = mfrEn;
  }

  @Column(name = "origin_no")
  public Integer getOriginNo() {
    return originNo;
  }

  public void setOriginNo(Integer originNo) {
    this.originNo = originNo;
  }

  @Column(name = "origin", length = 200)
  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Column(name = "unit", length = 200)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "memo", length = 2000)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "check_status")
  public Integer getCheckStatus() {
    return checkStatus;
  }

  public void setCheckStatus(Integer checkStatus) {
    this.checkStatus = checkStatus;
  }

  @Column(name = "product_vehicle_status")
  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }

  @Column(name = "barcode", length = 20)
  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

	@Column(name = "commodity_code" ,length = 50)
	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		if(StringUtils.isNotBlank(commodityCode)){
			this.commodityCode = commodityCode.trim().toUpperCase();
		}else {
			this.commodityCode = null;
		}
	}

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
	}

  @Column(name="product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  @Column(name = "storage_unit", length = 20)
  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  @Column(name = "sell_unit", length = 20)
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

  @Column(name = "points_exchangeable")
  public String getPointsExchangeable() {
    return pointsExchangeable;
  }

  public void setPointsExchangeable(String pointsExchangeable) {
    this.pointsExchangeable = pointsExchangeable;
  }

  @Column(name = "trade_price")
  public Double getTradePrice() {
    return tradePrice;
  }

  public void setTradePrice(Double tradePrice) {
    this.tradePrice = tradePrice;
  }

  @Column(name = "storage_bin" , length = 20)
  public String getStorageBin() {
    return storageBin;
  }

  public void setStorageBin(String storageBin) {
    this.storageBin = storageBin;
  }

  @Column(name="business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name="business_category_name")
  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  @Column(name="inventory_amount")
  public double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
  }

  @Column(name="lower_limit")
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

  @Column(name = "no_order_inventory")
  public Double getNoOrderInventory() {
    return noOrderInventory;
  }

  public void setNoOrderInventory(Double noOrderInventory) {
    this.noOrderInventory = noOrderInventory;
  }

  @Column(name = "sales_price")
  public Double getSalesPrice() {
    return salesPrice;
  }

  public void setSalesPrice(Double salesPrice) {
    this.salesPrice = salesPrice;
  }

  @Column(name = "latest_inventory_price")
  public Double getLatestInventoryPrice() {
    return latestInventoryPrice;
  }

  public void setLatestInventoryPrice(Double latestInventoryPrice) {
    this.latestInventoryPrice = latestInventoryPrice;
  }

  @Column(name = "inventory_average_price")
  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

  @Column(name="product_version")
  public Long getProductVersion() {
    return productVersion;
  }

  public void setProductVersion(Long productVersion) {
    this.productVersion = productVersion;
  }

  @Column(name="product_local_info_version")
  public Long getProductLocalInfoVersion() {
    return productLocalInfoVersion;
  }

  public void setProductLocalInfoVersion(Long productLocalInfoVersion) {
    this.productLocalInfoVersion = productLocalInfoVersion;
  }

  @Column(name="inventory_version")
  public Long getInventoryVersion() {
    return inventoryVersion;
  }

  public void setInventoryVersion(Long inventoryVersion) {
    this.inventoryVersion = inventoryVersion;
  }

  @Column(name="purchase_price")
  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  @Column(name="guarantee_period")
  public String getGuaranteePeriod() {
    return guaranteePeriod;
  }

  public void setGuaranteePeriod(String guaranteePeriod) {
    this.guaranteePeriod = guaranteePeriod;
  }

  @Column(name="product_category_id")
  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public ProductHistoryDTO toDTO(){
    ProductHistoryDTO productHistoryDTO = new ProductHistoryDTO();
    productHistoryDTO.setId(getId());
    productHistoryDTO.setProductId(productId);
    productHistoryDTO.setKindId(kindId);
    productHistoryDTO.setKindName(kindName);
    productHistoryDTO.setBrand(brand);
    productHistoryDTO.setModel(model);
    productHistoryDTO.setSpec(spec);
    productHistoryDTO.setName(name);
    productHistoryDTO.setNameEn(nameEn);
    productHistoryDTO.setMfr(mfr);
    productHistoryDTO.setMfrEn(mfrEn);
    productHistoryDTO.setOriginNo(originNo);
    productHistoryDTO.setOrigin(origin);
    productHistoryDTO.setUnit(unit);
    productHistoryDTO.setParentId(parentId);
    productHistoryDTO.setCheckStatus(checkStatus);
    productHistoryDTO.setState(state);
    productHistoryDTO.setProductVehicleBrand(productVehicleBrand);
    productHistoryDTO.setProductVehicleModel(productVehicleModel);
    productHistoryDTO.setProductVehicleYear(productVehicleYear);
    productHistoryDTO.setProductVehicleEngine(productVehicleEngine);
    productHistoryDTO.setMemo(memo);
    productHistoryDTO.setFirstLetter(firstLetter);
    productHistoryDTO.setFirstLetterCombination(firstLetterCombination);
    productHistoryDTO.setShopId(shopId);
    productHistoryDTO.setProductVehicleStatus(productVehicleStatus);
    productHistoryDTO.setBarcode(barcode);
    productHistoryDTO.setCommodityCode(commodityCode);
    productHistoryDTO.setStatus(status);

    productHistoryDTO.setProductLocalInfoId(productLocalInfoId);
    productHistoryDTO.setStorageUnit(storageUnit);
    productHistoryDTO.setSellUnit(sellUnit);
    productHistoryDTO.setRate(rate);
    productHistoryDTO.setPercentage(percentage);
    productHistoryDTO.setPercentageAmount(percentageAmount);
    productHistoryDTO.setPointsExchangeable(pointsExchangeable);
    productHistoryDTO.setTradePrice(tradePrice);
    productHistoryDTO.setStorageBin(storageBin);
    productHistoryDTO.setPurchasePrice(purchasePrice);
    productHistoryDTO.setBusinessCategoryId(businessCategoryId);
    productHistoryDTO.setBusinessCategoryName(businessCategoryName);
    productHistoryDTO.setInventoryAmount(inventoryAmount);
    productHistoryDTO.setLowerLimit(lowerLimit);
    productHistoryDTO.setUpperLimit(upperLimit);
    productHistoryDTO.setNoOrderInventory(noOrderInventory);
    productHistoryDTO.setSalesPrice(salesPrice);
    productHistoryDTO.setLatestInventoryPrice(latestInventoryPrice);
    productHistoryDTO.setInventoryAveragePrice(inventoryAveragePrice);

    productHistoryDTO.setProductVersion(productVersion);
    productHistoryDTO.setProductLocalInfoVersion(productLocalInfoVersion);
    productHistoryDTO.setInventoryVersion(inventoryVersion);
    productHistoryDTO.setDescription(description);
    productHistoryDTO.setHistoryCreateTime(DateUtil.convertDateLong2SimpleCNFormat(getCreationDate())); // add by zhuj
    productHistoryDTO.setInSalesPrice(inSalesPrice == null ? 0.0 : inSalesPrice);
    productHistoryDTO.setGuaranteePeriod(guaranteePeriod);
    productHistoryDTO.setProductCategoryId(productCategoryId);

    return productHistoryDTO;
  }
}
