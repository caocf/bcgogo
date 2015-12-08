package com.bcgogo.product.dto;

import com.bcgogo.common.PojoCommon;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductAdStatus;
import com.bcgogo.enums.ProductModifyFields;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.ProductRequest;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:33
 * To change this template use File | Settings | File Templates.
 */
public class ProductDTO implements Serializable,Cloneable {
  private String productInfo;
  private Long version;
  private Long kindId;
  private String kindName;
  private Double amount;
  private String brand;
  private String model;
  private String spec;
  private String name;
  private String nameEn;
  private String mfr;
  private String mfrEn;
  private Integer originNo;
  private Integer productVehicleStatus;
  private String origin;
  private String productVehicleBrand;          //产品本身带的车辆信息
  private String productVehicleModel;
  private String productVehicleYear;
  private String productVehicleEngine;
  private Long productVehicleBrandId;
  private Long productVehicleModelId;
  private Long productVehicleYearId;
  private Long productVehicleEngineId;
  private String unit;
  private Long parentId;
  private Integer checkStatus;
  private Long normalProductId;
  private String shopName;
  private String[] shopContactQQs;
  private String shopAreaInfo;
  private ProductRelevanceStatus relevanceStatus;
  private NormalProductDTO normalProductDTO;
  private String productInfoStr;//商品7个基本属性的拼接信息
  private PromotionsProductDTO promotionsProductDTO;
  private PromotionsDTO promotionsDTO;
  private List<PromotionsProductDTO> promotionsProductDTOList;
  private String description;
  private ProductAdStatus adStatus;
  //供应商评价
  private CommentStatDTO commentStatDTO;

  //productMapping
  private String lastPurchaseDateStr;
  private Double lastPurchaseAmount;
  private Double lastPurchasePrice;

  private Long lastModified;


  //员工业绩提成
  private AchievementType salesTotalAchievementType;//商品销售额提成方式 按销售量 按销售额
  private Double salesTotalAchievementAmount;//商品销售额提成数额
  private AchievementType salesProfitAchievementType;      //商品销售利润配置方式 按销售量 按销售额
  private Double salesProfitAchievementAmount;   //商品销售利润配置金额


  private SearchConditionDTO searchConditionDTO;//保存匹配种子

  //供求中心首页使用 是否本区域供应商
  private boolean localCity = false;

  private ProductCategoryDTO productCategoryDTO;
  private String productCategoryInfo;
  private String productCategoryName;
  private Long productCategoryId;

  public String getProductCategoryInfo() {
    return productCategoryInfo;
  }

  public void setProductCategoryInfo(String productCategoryInfo) {
    this.productCategoryInfo = productCategoryInfo;
  }

  public String getProductCategoryName() {
    return productCategoryName;
  }

  public void setProductCategoryName(String productCategoryName) {
    this.productCategoryName = productCategoryName;
  }

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public ProductCategoryDTO getProductCategoryDTO() {
    return productCategoryDTO;
  }

  public void setProductCategoryDTO(ProductCategoryDTO productCategoryDTO) {
    this.productCategoryDTO = productCategoryDTO;
    if(productCategoryDTO!=null){
      if(ProductCategoryType.SECOND_CATEGORY.equals(productCategoryDTO.getCategoryType())){
        productCategoryInfo = productCategoryDTO.getFirstCategoryName()+" >> "+productCategoryDTO.getSecondCategoryName();
      }else if(ProductCategoryType.THIRD_CATEGORY.equals(productCategoryDTO.getCategoryType())){
        productCategoryInfo = productCategoryDTO.getFirstCategoryName()+" >> "+productCategoryDTO.getSecondCategoryName()+" >> "+productCategoryDTO.getThirdCategoryName();
      }
      this.productCategoryName = productCategoryDTO.getName();

    }
  }

  public String[] getShopContactQQs() {
    return shopContactQQs;
  }

  public void setShopContactQQs(String[] shopContactQQs) {
    this.shopContactQQs = shopContactQQs;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProductAdStatus getAdStatus() {
    return adStatus;
  }

  public void setAdStatus(ProductAdStatus adStatus) {
    this.adStatus = adStatus;
  }

  public SearchConditionDTO getSearchConditionDTO() {
    return searchConditionDTO;
  }

  public void setSearchConditionDTO(SearchConditionDTO searchConditionDTO) {
    this.searchConditionDTO = searchConditionDTO;
  }

  public String getShopAreaInfo() {
    return shopAreaInfo;
  }

  public void setShopAreaInfo(String shopAreaInfo) {
    this.shopAreaInfo = shopAreaInfo;
  }

  public CommentStatDTO getCommentStatDTO() {
    return commentStatDTO;
  }

  public void setCommentStatDTO(CommentStatDTO commentStatDTO) {
    this.commentStatDTO = commentStatDTO;
  }

  public String getLastPurchaseDateStr() {
    return lastPurchaseDateStr;
  }

  public void setLastPurchaseDateStr(String lastPurchaseDateStr) {
    this.lastPurchaseDateStr = lastPurchaseDateStr;
  }

  public Double getLastPurchaseAmount() {
    return lastPurchaseAmount;
  }

  public void setLastPurchaseAmount(Double lastPurchaseAmount) {
    this.lastPurchaseAmount = lastPurchaseAmount;
  }

  public Double getLastPurchasePrice() {
    return lastPurchasePrice;
  }

  public void setLastPurchasePrice(Double lastPurchasePrice) {
    this.lastPurchasePrice = lastPurchasePrice;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getProductInfoStr() {
    return productInfoStr;
  }

  public void setProductInfoStr(String productInfoStr) {
    this.productInfoStr = productInfoStr;
  }

  public List<PromotionsProductDTO> getPromotionsProductDTOList() {
    return promotionsProductDTOList;
  }

  public void setPromotionsProductDTOList(List<PromotionsProductDTO> promotionsProductDTOList) {
    this.promotionsProductDTOList = promotionsProductDTOList;
  }

  public PromotionsDTO getPromotionsDTO() {
    return promotionsDTO;
  }

  public void setPromotionsDTO(PromotionsDTO promotionsDTO) {
    this.promotionsDTO = promotionsDTO;
  }

  public PromotionsProductDTO getPromotionsProductDTO() {
    return promotionsProductDTO;
  }

  public void setPromotionsProductDTO(PromotionsProductDTO promotionsProductDTO) {
    this.promotionsProductDTO = promotionsProductDTO;
  }

  private Double price;
  private Long state;
  private String memo;
  private Long id;
  private String firstLetter;      //产品名称首字母
  private String firstLetterCombination;   //产品名称首字母组合
  private Long shopId;
  private Long userId;
  private String shopIdStr;
  private String searchWord;
  private String virtualField;
  private Float virtualPrice;
  private Double inventoryNum;//库存数量，虚拟字段。
  private Integer inventoryAlarmNum;//建议库存数量，虚拟字段。
  private Double purchasePrice; //采购价，虚拟字段。

  private String vehicleBrand;            //维修单产生的车辆信息      库存页面商品修改，前台传入的值也是这个，应该是传错了，保险单传入也是这个错了，后面请注意
  private String vehicleModel;
  private String vehicleYear;
  private String vehicleEngine;
  private Long vehicleBrandId;
  private Long vehicleModelId;
  private Long vehicleYearId;
  private Long vehicleEngineId;


  private Long productLocalInfoId;
  private MultipartFile productFile;
  private String productFileType;
  private String barcode;
  private Double recommendedPrice;

  private Long editDate;//入库时间

  private String storageUnit;
  private String sellUnit;
  private Long rate;

  private String idStr;
  private String productLocalInfoIdStr;//将id,productLocalInfoId 转化成string 用于json传值

  private Double lowerLimit;
  private Double upperLimit;

  private Double salesPrice;   //销售价
  private Double tradePrice;   //批发价
  private String storageBin;// 仓位

  private String commodityCode;//商品编码
  private Double inventoryAveragePrice;
  private ProductStatus status;//商品状态
  //	private List<ProductSupplierDTO> productSupplierDTOs;//产品供应商关系（最近三个）
  private List<SupplierInventoryDTO> supplierInventoryDTOs;
  private StoreHouseInventoryDTO[] storeHouseInventoryDTOs;//仓库版本使用


  private Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap;//仓库id 为key
  private Long businessCategoryId;
  private String businessCategoryName;
  //畅销品、滞销品统、退回统计使用
  private double salesAmount;   //销售数量 畅销滞销商品统计使用
  private double salesTotal;     //销售总额畅销滞销商品统计使用
  private int returnTimes; //商品在某段时间内退货的数量
  private String queryResultStr;//商品信息: 品名 品牌/产地 规格 型号 适合车型 车辆品牌
  private double queryResult; //返回结果 用于饼图


  private ProductStatus salesStatus;//上架下架状态
  private PromotionsEnum.PromotionStatus promotionsStatus;
  private Long lastInSalesTime;//最后上架时间
  private String lastInSalesTimeStr;
  private Long lastOffSalesTime;//最后下架时间
  private Double inSalesAmount;//上架量
  private String inSalesAmountStr;
  private Double inSalesPrice;//上架价格 原来放在 tradePrice 这个字段
  private String guaranteePeriod;
  private String inSalesUnit;
  private Double viewedCount;

  private List<PromotionsDTO> promotionsDTOs;
  private List<String> promotionsTypeList;
  private List<String> promotionsIdList;

  private Long promotionsId;
  private Boolean applyFlag;//是否已经提交过关联申请

  private Boolean shopRelatedFlag;//是否是关联供应商的商品



  private Long shopProvince;     //省
  private Long shopCity;          //市
  private Long shopRegion;        //区域

  private Double customScore;
  private Double averagePrice;


  private ImageCenterDTO imageCenterDTO;

  /* add by zhuj  */
  private Double inSalesPriceAfterCal; // 计算促销以后的价格
  private String promotionTypesShortStr; // 页面显示用的 促销短信息拼接的str 对应枚举为 PromotionsTypesShort
  private String promotionTypesStr; // 页面显示用的 促销短信息拼接的str 对应枚举为 PromotionsTypesShort
  private String promotionContent; // 页面显示的促销内容str
  private Boolean hasBargain=false;
  /* add end */

  public String getPromotionContent() {
    return promotionContent;
  }

  public void setPromotionContent(String promotionContent) {
    this.promotionContent = promotionContent;
  }

  public Boolean getHasBargain() {
    return hasBargain;
  }

  public void setHasBargain(Boolean hasBargain) {
    this.hasBargain = hasBargain;
  }
  public ProductDTO(SearchConditionDTO searchConditionDTO) {
    this.name = searchConditionDTO.getProductName();
    this.brand = searchConditionDTO.getProductBrand();
    this.spec = searchConditionDTO.getProductSpec();
    this.model = searchConditionDTO.getProductModel();
    this.productVehicleBrand = searchConditionDTO.getProductVehicleBrand();
    this.productVehicleModel = searchConditionDTO.getProductVehicleModel();
  }

  public ProductDTO(String productName, String productBrand, String productSpec, String productModel,
                    String pvBrand, String pvModel, String pvYear, String pvEngine) {
    this.name = productName;
    this.brand = productBrand;
    this.spec = productSpec;
    this.model = productModel;
    this.productVehicleBrand = pvBrand;
    this.productVehicleEngine = pvEngine;
    this.productVehicleModel = pvModel;
    this.productVehicleYear = pvYear;
  }

  public ProductDTO(Long shopId, PurchaseOrderItemDTO purchaseOrderItemDTO) {
    setShopId(shopId);
    name = purchaseOrderItemDTO.getProductName();
    brand = purchaseOrderItemDTO.getBrand();
    spec = purchaseOrderItemDTO.getSpec();
    model = purchaseOrderItemDTO.getModel();
    productVehicleBrand = purchaseOrderItemDTO.getVehicleBrand();
    productVehicleModel = purchaseOrderItemDTO.getVehicleModel();
    productVehicleYear = purchaseOrderItemDTO.getVehicleYear();
    productVehicleEngine = purchaseOrderItemDTO.getVehicleEngine();
    memo = purchaseOrderItemDTO.getMemo();
    productVehicleStatus = purchaseOrderItemDTO.getProductVehicleStatus();
    price = Double.valueOf(0);
    purchasePrice = 0d;
    if (StringUtils.isEmpty(purchaseOrderItemDTO.getSellUnit()) && (StringUtils.isEmpty(purchaseOrderItemDTO.getStorageUnit()))) {
      storageUnit = purchaseOrderItemDTO.getUnit();
      sellUnit = purchaseOrderItemDTO.getUnit();
    } else {
      sellUnit = purchaseOrderItemDTO.getSellUnit();
      storageUnit = purchaseOrderItemDTO.getStorageUnit();
      rate = purchaseOrderItemDTO.getRate();
    }
    storageBin = purchaseOrderItemDTO.getStorageBin();
    tradePrice = purchaseOrderItemDTO.getTradePrice();
    commodityCode = purchaseOrderItemDTO.getCommodityCode();
    kindName = purchaseOrderItemDTO.getProductKind();
    kindId = purchaseOrderItemDTO.getProductKindId();
  }

  public ProductDTO(Long shopId, PreBuyOrderItemDTO itemDTO) {
    setShopId(shopId);
    name = itemDTO.getProductName();
    brand = itemDTO.getBrand();
    spec = itemDTO.getSpec();
    model = itemDTO.getModel();
    productVehicleBrand = itemDTO.getVehicleBrand();
    productVehicleModel = itemDTO.getVehicleModel();
    commodityCode = itemDTO.getCommodityCode();
    this.setProductLocalInfoId(itemDTO.getProductId());
  }

  public ProductDTO(Long shopId, QuotedPreBuyOrderItemDTO itemDTO) {
    setShopId(shopId);
    name = itemDTO.getProductName();
    brand = itemDTO.getBrand();
    spec = itemDTO.getSpec();
    model = itemDTO.getModel();
    productVehicleBrand = itemDTO.getVehicleBrand();
    productVehicleModel = itemDTO.getVehicleModel();
    commodityCode = itemDTO.getCommodityCode();
    this.sellUnit=itemDTO.getSellUnit();
    this.storageUnit=itemDTO.getStorageUnit();
    this.rate=itemDTO.getRate();
    this.inSalesAmount=itemDTO.getInSalesAmount();
    this.setProductLocalInfoId(itemDTO.getProductId());
  }

  public ProductDTO(Long shopId, PurchaseReturnItemDTO itemDTO) {
    setShopId(shopId);
    name = itemDTO.getProductName();
    brand = itemDTO.getBrand();
    spec = itemDTO.getSpec();
    model = itemDTO.getModel();
    productVehicleBrand = itemDTO.getVehicleBrand();
    productVehicleModel = itemDTO.getVehicleModel();
    productVehicleYear = itemDTO.getVehicleYear();
    productVehicleEngine = itemDTO.getVehicleEngine();
    commodityCode = itemDTO.getCommodityCode();
  }

  public ProductDTO(Long shopId, RepairOrderItemDTO itemDTO) {
    setShopId(shopId);
    name = itemDTO.getProductName();
    brand = itemDTO.getBrand();
    spec = itemDTO.getSpec();
    model = itemDTO.getModel();
    productVehicleBrand = itemDTO.getVehicleBrand();
    productVehicleModel = itemDTO.getVehicleModel();
    productVehicleYear = itemDTO.getVehicleYear();
    productVehicleEngine = itemDTO.getVehicleEngine();
    price = itemDTO.getPrice();
    purchasePrice = itemDTO.getPurchasePrice();
    storageUnit = itemDTO.getUnit();
    sellUnit = itemDTO.getUnit();
    commodityCode = itemDTO.getCommodityCode();
    setBusinessCategoryId(itemDTO.getBusinessCategoryId());
  }

  public ProductDTO(Long shopId, PurchaseInventoryItemDTO purchaseInventoryItemDTO) {
    this.setShopId(shopId);
    this.setName(purchaseInventoryItemDTO.getProductName());
    this.setBrand(purchaseInventoryItemDTO.getBrand());
    this.setSpec(purchaseInventoryItemDTO.getSpec());
    this.setModel(purchaseInventoryItemDTO.getModel());
    this.setProductVehicleBrand(purchaseInventoryItemDTO.getVehicleBrand());
    this.setProductVehicleModel(purchaseInventoryItemDTO.getVehicleModel());
    this.setProductVehicleYear(purchaseInventoryItemDTO.getVehicleYear());
    this.setProductVehicleEngine(purchaseInventoryItemDTO.getVehicleEngine());
    this.setMemo(purchaseInventoryItemDTO.getMemo());
    this.setProductVehicleStatus(purchaseInventoryItemDTO.getProductVehicleStatus());
    this.setPrice(Double.valueOf(0));
    this.setPurchasePrice(purchaseInventoryItemDTO.getPurchasePrice());
    this.setBarcode(purchaseInventoryItemDTO.getBarcode());
    this.setStorageUnit(purchaseInventoryItemDTO.getUnit());
    this.setSellUnit(purchaseInventoryItemDTO.getUnit());   //todo add same unit for new product
    this.setStorageBin(purchaseInventoryItemDTO.getStorageBin());
    this.setTradePrice(purchaseInventoryItemDTO.getTradePrice());
    this.setCommodityCode(purchaseInventoryItemDTO.getCommodityCode());
    this.setKindName(purchaseInventoryItemDTO.getProductKind());
    this.setKindId(purchaseInventoryItemDTO.getProductKindId());
  }



  public ProductDTO(Long shopId, SalesOrderItemDTO itemDTO) {
    this.setShopId(shopId);
    this.setName(itemDTO.getProductName());
    this.setBrand(itemDTO.getBrand());
    this.setSpec(itemDTO.getSpec());
    this.setModel(itemDTO.getModel());
    this.setProductVehicleBrand(itemDTO.getVehicleBrand());
    this.setProductVehicleModel(itemDTO.getVehicleModel());
    this.setProductVehicleYear(itemDTO.getVehicleYear());
    this.setProductVehicleEngine(itemDTO.getVehicleEngine());
    this.setMemo(itemDTO.getMemo());
    this.setPrice(itemDTO.getPrice());
    this.setPurchasePrice(itemDTO.getPurchasePrice());
    this.setStorageUnit(itemDTO.getUnit());
    this.setSellUnit(itemDTO.getUnit());
    this.setCommodityCode(itemDTO.getCommodityCode());
    this.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
    this.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
  }


  public ProductDTO(Long shopId, SalesReturnItemDTO itemDTO){
    this.setShopId(shopId);
    this.setName(itemDTO.getProductName());
    this.setBrand(itemDTO.getBrand());
    this.setSpec(itemDTO.getSpec());
    this.setModel(itemDTO.getModel());
    this.setProductVehicleBrand(itemDTO.getVehicleBrand());
    this.setProductVehicleModel(itemDTO.getVehicleModel());
    this.setProductVehicleYear(itemDTO.getVehicleYear());
    this.setProductVehicleEngine(itemDTO.getVehicleEngine());
    this.setMemo(itemDTO.getMemo());
    this.setPrice(itemDTO.getPrice());
    this.setStorageUnit(itemDTO.getUnit());
    this.setSellUnit(itemDTO.getUnit());
    this.setCommodityCode(itemDTO.getCommodityCode());
  }

  public ProductDTO clone() throws CloneNotSupportedException{
    return (ProductDTO)super.clone();
  }

  public String getPromotionTypesShortStr() {
    return promotionTypesShortStr;
  }

  public void setPromotionTypesShortStr(String promotionTypesShortStr) {
    this.promotionTypesShortStr = promotionTypesShortStr;
  }

  public Double getAveragePrice() {
    return averagePrice;
  }

  public void setAveragePrice(Double averagePrice) {
    this.averagePrice = averagePrice;
  }

  public Boolean getShopRelatedFlag() {
    return shopRelatedFlag;
  }

  public void setShopRelatedFlag(Boolean shopRelatedFlag) {
    this.shopRelatedFlag = shopRelatedFlag;
  }

  public Boolean getApplyFlag() {
    return applyFlag;
  }

  public void setApplyFlag(Boolean applyFlag) {
    this.applyFlag = applyFlag;
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public Map<Long, StoreHouseInventoryDTO> getStoreHouseInventoryDTOMap() {
    return storeHouseInventoryDTOMap;
  }

  public void setStoreHouseInventoryDTOMap(Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap) {
    this.storeHouseInventoryDTOMap = storeHouseInventoryDTOMap;
  }

  public StoreHouseInventoryDTO[] getStoreHouseInventoryDTOs() {
    return storeHouseInventoryDTOs;
  }

  public void setStoreHouseInventoryDTOs(StoreHouseInventoryDTO[] storeHouseInventoryDTOs) {
    this.storeHouseInventoryDTOs = storeHouseInventoryDTOs;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getProductLocalInfoIdStr() {
    return productLocalInfoIdStr;
  }

  public void setProductLocalInfoIdStr(String productLocalInfoIdStr) {
    this.productLocalInfoIdStr = productLocalInfoIdStr;
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

  public void set(Long shopId, PurchaseOrderItemDTO purchaseOrderItemDTO) {
    setShopId(shopId);
    setProductLocalInfoId(purchaseOrderItemDTO.getSupplierProductId());
    setCommodityCode(purchaseOrderItemDTO.getCommodityCode());
    name = purchaseOrderItemDTO.getProductName();
    brand = purchaseOrderItemDTO.getBrand();
    spec = purchaseOrderItemDTO.getSpec();
    model = purchaseOrderItemDTO.getModel();
    productVehicleBrand = purchaseOrderItemDTO.getVehicleBrand();
    productVehicleModel = purchaseOrderItemDTO.getVehicleModel();
    productVehicleYear = purchaseOrderItemDTO.getVehicleYear();
    productVehicleEngine = purchaseOrderItemDTO.getVehicleEngine();
    memo = purchaseOrderItemDTO.getMemo();
    productVehicleStatus = purchaseOrderItemDTO.getProductVehicleStatus();
    price = 0D;
    purchasePrice = purchaseOrderItemDTO.getPrice();
    if(StringUtils.isEmpty(purchaseOrderItemDTO.getSellUnit())&&(StringUtils.isEmpty(purchaseOrderItemDTO.getStorageUnit()))){
      storageUnit = purchaseOrderItemDTO.getUnit();
      sellUnit = purchaseOrderItemDTO.getUnit();
    }else{
      sellUnit = purchaseOrderItemDTO.getSellUnit();
      storageUnit = purchaseOrderItemDTO.getStorageUnit();
      rate = purchaseOrderItemDTO.getRate();
    }
    storageBin = purchaseOrderItemDTO.getStorageBin();
    tradePrice = purchaseOrderItemDTO.getTradePrice();

  }

  public InventorySearchIndexDTO toInventorySearchIndexDTO() {
    InventorySearchIndexDTO inventorySearchIndexDTO = new InventorySearchIndexDTO();
    inventorySearchIndexDTO.setShopId(shopId);
    inventorySearchIndexDTO.setProductId(this.productLocalInfoId);
    inventorySearchIndexDTO.setParentProductId(id);
    inventorySearchIndexDTO.setProductName(name);
    inventorySearchIndexDTO.setProductBrand(brand);
    inventorySearchIndexDTO.setProductSpec(spec);
    inventorySearchIndexDTO.setProductModel(model);
    inventorySearchIndexDTO.setBrand(productVehicleBrand);
    inventorySearchIndexDTO.setModel(productVehicleModel);
    inventorySearchIndexDTO.setYear(productVehicleYear);
    inventorySearchIndexDTO.setEngine(productVehicleEngine);
    inventorySearchIndexDTO.setProductVehicleStatus(productVehicleStatus);
    inventorySearchIndexDTO.setAmount(inventoryNum == null ? null : inventoryNum);
    inventorySearchIndexDTO.setPurchasePrice(purchasePrice);
    inventorySearchIndexDTO.setPrice(this.price);
    inventorySearchIndexDTO.setEditDate(editDate);
    inventorySearchIndexDTO.setStorageUnit(storageUnit);
    inventorySearchIndexDTO.setSellUnit(sellUnit);
    inventorySearchIndexDTO.setUnit(unit);
    inventorySearchIndexDTO.setRate(rate);
    inventorySearchIndexDTO.setLowerLimit(lowerLimit);
    inventorySearchIndexDTO.setUpperLimit(upperLimit);
    inventorySearchIndexDTO.setRecommendedPrice(this.recommendedPrice);
    inventorySearchIndexDTO.setTradePrice(this.tradePrice);
    inventorySearchIndexDTO.setStorageBin(this.getStorageBin());
    inventorySearchIndexDTO.setCommodityCode(this.getCommodityCode());
    inventorySearchIndexDTO.setInventoryAveragePrice(this.inventoryAveragePrice);
    return inventorySearchIndexDTO;
  }


  public Double getInSalesPriceAfterCal() {
    return inSalesPriceAfterCal;
  }

  public void setInSalesPriceAfterCal(Double inSalesPriceAfterCal) {
    this.inSalesPriceAfterCal = inSalesPriceAfterCal;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public String getProductFileType() {
    return productFileType;
  }

  public void setProductFileType(String productFileType) {
    this.productFileType = productFileType;
  }

  public MultipartFile getProductFile() {
    return productFile;
  }

  public void setProductFile(MultipartFile productFile) {
    this.productFile = productFile;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
    if (productLocalInfoId != null) {
      this.productLocalInfoIdStr = String.valueOf(productLocalInfoId);
    } else {
      this.productLocalInfoIdStr = null;
    }

  }

  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  public Long getVehicleYearId() {
    return vehicleYearId;
  }

  public void setVehicleYearId(Long vehicleYearId) {
    this.vehicleYearId = vehicleYearId;
  }

  public Long getVehicleEngineId() {
    return vehicleEngineId;
  }

  public void setVehicleEngineId(Long vehicleEngineId) {
    this.vehicleEngineId = vehicleEngineId;
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

  public Long getProductVehicleBrandId() {
    return productVehicleBrandId;
  }

  public void setProductVehicleBrandId(Long productVehicleBrandId) {
    this.productVehicleBrandId = productVehicleBrandId;
  }

  public Long getProductVehicleModelId() {
    return productVehicleModelId;
  }

  public void setProductVehicleModelId(Long productVehicleModelId) {
    this.productVehicleModelId = productVehicleModelId;
  }

  public Long getProductVehicleYearId() {
    return productVehicleYearId;
  }

  public void setProductVehicleYearId(Long productVehicleYearId) {
    this.productVehicleYearId = productVehicleYearId;
  }

  public Long getProductVehicleEngineId() {
    return productVehicleEngineId;
  }

  public void setProductVehicleEngineId(Long productVehicleEngineId) {
    this.productVehicleEngineId = productVehicleEngineId;
  }

  public Integer getInventoryAlarmNum() {
    return inventoryAlarmNum;
  }

  public void setInventoryAlarmNum(Integer inventoryAlarmNum) {
    this.inventoryAlarmNum = inventoryAlarmNum;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }

  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }



  public ProductDTO() {
  }

  public ProductDTO(ProductRequest request) {
    setFirstLetter(request.getFirstLetter());
    setFirstLetterCombination(request.getFirstLetterCombination());
    setKindId(request.getKindId());
    setBrand(request.getBrand());
    setModel(request.getModel());
    setSpec(request.getSpec());
    setName(request.getName());
    setNameEn(request.getNameEn());
    setMfr(request.getMfr());
    setMfrEn(request.getMfrEn());
    setOriginNo(request.getOriginNo());
    setOrigin(request.getOrigin());
    setUnit(request.getUnit());
    setState(request.getState());
    setMemo(request.getMemo());
    setShopId(request.getShopId());
    setCheckStatus(request.getCheckStatus());
    setParentId(request.getParentId());
//    setPrice(request.getPrice());
    setProductVehicleStatus(request.getProductVehicleStatus());
    setProductVehicleBrand(request.getProductVehicleBrand());
    setProductVehicleModel(request.getProductVehicleModel());
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

  public Double getInventoryNum() {
    return inventoryNum;
  }

  public void setInventoryNum(Double inventoryNum) {
    this.inventoryNum = inventoryNum;
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

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

//  public BigDecimal getPrice() {
//    return price;
//  }
//
//  public void setPrice(BigDecimal price) {
//    this.price = price;
//  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    if(shopId!=null){
      shopIdStr = shopId.toString();
    }
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = String.valueOf(id);
  }

  public String getSearchWord() {
    return searchWord;
  }

  public void setSearchWord(String searchWord) {
    this.searchWord = searchWord;
  }

  public String getVirtualField() {
    return virtualField;
  }

  public void setVirtualField(String virtualField) {
    this.virtualField = virtualField;
  }

  public Float getVirtualPrice() {
    return virtualPrice;
  }

  public void setVirtualPrice(Float virtualPrice) {
    this.virtualPrice = virtualPrice;
  }

  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
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

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Double getRecommendedPrice() {
    return recommendedPrice;
  }

  public void setRecommendedPrice(Double recommendedPrice) {
    this.recommendedPrice = recommendedPrice;
  }

  public String toJsonStr() {
    return "{\"barcode\":\"" + PojoCommon.toJsonStr(this.getBarcode()) + "\"," +
      "\"productName\":\"" + PojoCommon.toJsonStr(this.getName()) + "\"," +
      "\"productId\":\"" + PojoCommon.toJsonStr(this.getId()) + "\"," +
      "\"productBrand\":\"" + PojoCommon.toJsonStr(this.getBrand()) + "\"," +
      "\"productSpec\":\"" + PojoCommon.toJsonStr(this.getSpec()) + "\"," +
      "\"productModel\":\"" + PojoCommon.toJsonStr(this.getModel()) + "\"," +
      "\"productVehicleStatus\":\"" + PojoCommon.toJsonStr(this.getProductVehicleStatus()) + "\"," +
      "\"vehicleBrand\":\"" + PojoCommon.toJsonStr(this.getVehicleBrand()) + "\"," +
      "\"vehicleModel\":\"" + PojoCommon.toJsonStr(this.getVehicleModel()) + "\"," +
      "\"vehicleYear\":\"" + PojoCommon.toJsonStr(this.getVehicleYear()) + "\"," +
      "\"purchasePrice\":\"" + PojoCommon.toJsonStr(this.getPurchasePrice()) + "\"," +
      "\"price\":\"" + PojoCommon.toJsonStr(this.getPrice()) + "\"," +
      "\"inventoryAveragePrice\":\"" + PojoCommon.toJsonStr(this.getInventoryAveragePrice()) + "\"," +
      "\"vehicleEngine\":\"" + PojoCommon.toJsonStr(this.getVehicleEngine()) + "\"}";
  }

  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  public Long getRate() {
    return rate;
  }

  public void setRate(Long rate) {
    this.rate = rate;
  }

  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    this.sellUnit = sellUnit;
  }

  public Double getTradePrice() {
    return tradePrice;
  }

  public void setTradePrice(Double tradePrice) {
    this.tradePrice = tradePrice;
  }

  public Double getSalesPrice() {
    return salesPrice;
  }

  public void setSalesPrice(Double salesPrice) {
    this.salesPrice = salesPrice;
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
    if(StringUtils.isNotBlank(commodityCode)){
      this.commodityCode = commodityCode.trim().toUpperCase();
    }else {
      this.commodityCode = null;
    }
  }

  public ProductStatus getStatus() {
    return status;
  }

  public void setStatus(ProductStatus status) {
    this.status = status;
  }

  public List<SupplierInventoryDTO> getSupplierInventoryDTOs() {
    return supplierInventoryDTOs;
  }

  public void setSupplierInventoryDTOs(List<SupplierInventoryDTO> supplierInventoryDTOs) {
    this.supplierInventoryDTOs = supplierInventoryDTOs;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }


  public ProductDTO(InventorySearchIndexDTO inventorySearchIndex){
    setId(inventorySearchIndex.getParentProductId());
    setShopId(inventorySearchIndex.getShopId());
    setProductLocalInfoId(inventorySearchIndex.getProductId());
    setName(inventorySearchIndex.getProductName());
    setBrand(inventorySearchIndex.getProductBrand());
    setSpec(inventorySearchIndex.getProductSpec());
    setModel(inventorySearchIndex.getProductModel());

    setProductVehicleBrand(inventorySearchIndex.getBrand());
    setProductVehicleModel(inventorySearchIndex.getModel());
    setProductVehicleYear(inventorySearchIndex.getYear());
    setProductVehicleEngine(inventorySearchIndex.getEngine());

//    setVehicleBrand(inventorySearchIndex.getBrand());
//    setVehicleModel(inventorySearchIndex.getModel());
//    setVehicleYear(inventorySearchIndex.getYear());
//    setVehicleEngine(inventorySearchIndex.getEngine());

    setRecommendedPrice(inventorySearchIndex.getRecommendedPrice());
    setInventoryNum(inventorySearchIndex.getAmount());
    setPrice(inventorySearchIndex.getPrice());
    setPurchasePrice(inventorySearchIndex.getPurchasePrice());

    setStorageUnit(inventorySearchIndex.getStorageUnit());
    setSellUnit(inventorySearchIndex.getSellUnit());
    setRate(inventorySearchIndex.getRate());

    setBarcode(inventorySearchIndex.getBarcode());
    setLowerLimit(inventorySearchIndex.getLowerLimit());
    setUpperLimit(inventorySearchIndex.getUpperLimit());
    setCommodityCode(inventorySearchIndex.getCommodityCode());
    setInventoryAveragePrice(inventorySearchIndex.getInventoryAveragePrice());
    setKindName(inventorySearchIndex.getKindName());
  }

  public ProductDTO(InventorySearchIndexDTO inventorySearchIndexDTO, InventoryDTO inventoryDTO) {
    setId(inventorySearchIndexDTO.getParentProductId());
    setShopId(inventorySearchIndexDTO.getShopId());
    setProductLocalInfoId(inventorySearchIndexDTO.getProductId());

    setName(inventorySearchIndexDTO.getProductName());
    setBrand(inventorySearchIndexDTO.getProductBrand());
    setSpec(inventorySearchIndexDTO.getProductSpec());
    setModel(inventorySearchIndexDTO.getProductModel());

    setProductVehicleBrand(inventorySearchIndexDTO.getBrand());
    setProductVehicleModel(inventorySearchIndexDTO.getModel());
    setProductVehicleYear(inventorySearchIndexDTO.getYear());
    setProductVehicleEngine(inventorySearchIndexDTO.getEngine());

//      setVehicleBrand(inventorySearchIndexDTO.getBrand());
//      setVehicleModel(inventorySearchIndexDTO.getModel());
//      setVehicleYear(inventorySearchIndexDTO.getYear());
//      setVehicleEngine(inventorySearchIndexDTO.getEngine());

    setRecommendedPrice(inventorySearchIndexDTO.getRecommendedPrice());
    setInventoryNum(inventorySearchIndexDTO.getAmount());
    setPrice(inventorySearchIndexDTO.getPrice());
    setPurchasePrice(inventorySearchIndexDTO.getPurchasePrice());

    setStorageUnit(inventorySearchIndexDTO.getStorageUnit());
    setSellUnit(inventorySearchIndexDTO.getSellUnit());
    setRate(inventorySearchIndexDTO.getRate());
    setLowerLimit(inventoryDTO.getLowerLimit());
    setUpperLimit(inventoryDTO.getUpperLimit());
    setInventoryAveragePrice(inventorySearchIndexDTO.getInventoryAveragePrice());
  }

  public void setVehicleInfoIds(Long[] ids) {
    this.productVehicleBrandId = ids[0];
    this.vehicleBrandId = ids[0];
    this.productVehicleModelId = ids[1];
    this.vehicleModelId = ids[1];
    this.productVehicleYearId = ids[2];
    this.vehicleYearId = ids[2];
    this.productVehicleEngineId = ids[3];
    this.vehicleEngineId = ids[3];
  }

  /*
  * 为了转成前台js 约定好的json格式
  */
  public Map<String, String> toProductHistorySuggestionMap() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("id", id==null?"":id.toString());
    map.put("product_id", productLocalInfoId==null?"":productLocalInfoId.toString());
    map.put("product_name", name);
    map.put("commodity_code", commodityCode);
    map.put("product_brand", brand);
    map.put("product_model", model);
    map.put("product_spec", spec);
    map.put("product_vehicle_brand", productVehicleBrand==null?"":productVehicleBrand.toString());
    map.put("product_vehicle_model", productVehicleModel==null?"":productVehicleModel.toString());
    map.put("inventoryNum", inventoryNum==null?"0.0":inventoryNum.toString());
    map.put("sellUnit", sellUnit);
    map.put("purchasePrice", purchasePrice==null?"0.0":purchasePrice.toString());   //最新入库价
    map.put("inventoryAveragePrice", inventoryAveragePrice==null?"0.0":inventoryAveragePrice.toString());
    map.put("recommendedPrice", recommendedPrice==null?"0.0":recommendedPrice.toString());
    map.put("inSalesAmount", inSalesAmount==null?"0.0":inSalesAmount.toString());
    map.put("tradePrice", tradePrice==null?"0.0":tradePrice.toString());
    String commodityInfo = (commodityCode == null ? "" : commodityCode) + " " + (name == null ? "" : name) + " " + (brand == null ? "" : brand) + " " + (spec == null ? "" : spec) + " " + (model == null ? "" : model);
    map.put("commodityInfo",commodityInfo);
    map.put("storageBin",storageBin);
    return map;
  }

  public ProductDTO formInventorySearchIndexDTO(InventorySearchIndexDTO inventorySearchIndex) {
    setId(inventorySearchIndex.getParentProductId());
    setShopId(inventorySearchIndex.getShopId());
    setProductLocalInfoId(inventorySearchIndex.getProductId());
    setName(inventorySearchIndex.getProductName());
    setBrand(inventorySearchIndex.getProductBrand());
    setSpec(inventorySearchIndex.getProductSpec());
    setModel(inventorySearchIndex.getProductModel());
    setProductVehicleBrand(inventorySearchIndex.getBrand());
    setProductVehicleModel(inventorySearchIndex.getModel());
    setProductVehicleYear(inventorySearchIndex.getYear());
    setProductVehicleEngine(inventorySearchIndex.getEngine());
    setRecommendedPrice(inventorySearchIndex.getRecommendedPrice());
    setInventoryNum(inventorySearchIndex.getAmount());
    setPrice(inventorySearchIndex.getPrice());
    setPurchasePrice(inventorySearchIndex.getPurchasePrice());
    setStorageUnit(inventorySearchIndex.getStorageUnit());
    setSellUnit(inventorySearchIndex.getSellUnit());
    setRate(inventorySearchIndex.getRate());
    setBarcode(inventorySearchIndex.getBarcode());
    setLowerLimit(inventorySearchIndex.getLowerLimit());
    setUpperLimit(inventorySearchIndex.getUpperLimit());
    setInventoryAveragePrice(inventorySearchIndex.getInventoryAveragePrice());
    return this;
  }


  public void setProductLocalInfoDTO(ProductLocalInfoDTO productLocalInfoDTO) {
    if(productLocalInfoDTO==null) return;
    this.setProductLocalInfoId(productLocalInfoDTO.getId());
    this.setPrice(productLocalInfoDTO.getPrice());
    this.setShopId(productLocalInfoDTO.getShopId());
    this.setStorageUnit(productLocalInfoDTO.getStorageUnit());
    this.setSellUnit(productLocalInfoDTO.getSellUnit());
    this.setRate(productLocalInfoDTO.getRate());
    this.setPurchasePrice(productLocalInfoDTO.getPurchasePrice());
    this.setTradePrice(productLocalInfoDTO.getTradePrice());
    this.setStorageBin(productLocalInfoDTO.getStorageBin());
    this.setBusinessCategoryId(productLocalInfoDTO.getBusinessCategoryId());
    this.setBusinessCategoryName(productLocalInfoDTO.getBusinessCategoryName());
    this.setLastInSalesTime(productLocalInfoDTO.getLastInSalesTime());
    this.setLastOffSalesTime(productLocalInfoDTO.getLastOffSalesTime());
    this.setInSalesAmount(productLocalInfoDTO.getInSalesAmount());
    this.setInSalesPrice(productLocalInfoDTO.getInSalesPrice());
    this.setInSalesUnit(productLocalInfoDTO.getInSalesUnit());
    this.setGuaranteePeriod(productLocalInfoDTO.getGuaranteePeriod());
    this.setSalesStatus(productLocalInfoDTO.getSalesStatus());
    this.setAdStatus(productLocalInfoDTO.getAdStatus());
    this.setPromotionsDTOs(productLocalInfoDTO.getPromotionsDTOs());
    this.generateProductInfo();
  }

  public void setInventoryDTO(InventoryDTO inventoryDTO){
    if(inventoryDTO==null) return;
    this.inventoryNum=inventoryDTO.getAmount();
    this.price=NumberUtil.round(inventoryDTO.getSalesPrice());
  }

  @Override
  public String toString(){
    return JsonUtil.objectToJson(this);
  }

  public double getSalesAmount() {
    return salesAmount;
  }

  public void setSalesAmount(double salesAmount) {
    this.salesAmount = salesAmount;
  }

  public double getSalesTotal() {
    return salesTotal;
  }

  public void setSalesTotal(double salesTotal) {
    this.salesTotal = salesTotal;
  }

  public String getQueryResultStr() {
    return queryResultStr;
  }

  public void setQueryResultStr(String queryResultStr) {
    this.queryResultStr = queryResultStr;
  }

  public int getReturnTimes() {
    return returnTimes;
  }

  public void setReturnTimes(int returnTimes) {
    this.returnTimes = returnTimes;
  }

  public double getQueryResult() {
    return queryResult;
  }

  public void setQueryResult(double queryResult) {
    this.queryResult = queryResult;
  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public String getProductMsg() {
    StringBuffer sb = new StringBuffer();
    if (StringUtils.isNotBlank(name)) {
      sb.append("品名:" + name);
    }
    if (StringUtils.isNotBlank(brand)) {
      sb.append(",品牌/产地:" + brand);
    }
    if (StringUtils.isNotBlank(model)) {
      sb.append(",规格:" + model);
    }
    if (StringUtils.isNotBlank(spec)) {
      sb.append(",型号:" + spec);
    }
    sb.append("<br>");
    return sb.toString();
  }

  public ProductDTO fromSalesReturnItemDTO(SalesReturnItemDTO salesReturnItemDTO) {
    if (salesReturnItemDTO == null) {
      return null;
    }
    this.name = salesReturnItemDTO.getProductName();
    this.brand = salesReturnItemDTO.getBrand();
    this.model = salesReturnItemDTO.getModel();
    this.spec = salesReturnItemDTO.getSpec();
//    this.vehicleBrand = salesReturnItemDTO.getVehicleBrand();
//    this.vehicleModel = salesReturnItemDTO.getVehicleModel();
    this.setProductVehicleBrand(salesReturnItemDTO.getVehicleBrand());
    this.setProductVehicleModel(salesReturnItemDTO.getVehicleModel());
    this.sellUnit = salesReturnItemDTO.getSellUnit();
    this.storageUnit = salesReturnItemDTO.getStorageUnit();
    this.rate = salesReturnItemDTO.getRate();
    this.tradePrice = salesReturnItemDTO.getTradePrice();
    this.storageBin = salesReturnItemDTO.getStorageBin();
    this.commodityCode = salesReturnItemDTO.getCommodityCode();
    return this;
  }

  public void fromInsuranceOrderItemDTO(InsuranceOrderItemDTO insuranceOrderItemDTO) {
    if (insuranceOrderItemDTO != null) {
      this.name = insuranceOrderItemDTO.getProductName();
      this.brand = insuranceOrderItemDTO.getBrand();
      this.model = insuranceOrderItemDTO.getModel();
      this.spec = insuranceOrderItemDTO.getSpec();
//      this.vehicleBrand = insuranceOrderItemDTO.getVehicleBrand();
//      this.vehicleModel = insuranceOrderItemDTO.getVehicleModel();
      this.productVehicleBrand = insuranceOrderItemDTO.getVehicleBrand();
      this.productVehicleModel = insuranceOrderItemDTO.getVehicleModel();
      this.commodityCode = insuranceOrderItemDTO.getCommodityCode();
    }
  }

  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public ProductRelevanceStatus getRelevanceStatus() {
    return relevanceStatus;
  }

  public void setRelevanceStatus(ProductRelevanceStatus relevanceStatus) {
    this.relevanceStatus = relevanceStatus;
  }

  public NormalProductDTO getNormalProductDTO() {
    return normalProductDTO;
  }

  public void setNormalProductDTO(NormalProductDTO normalProductDTO) {
    this.normalProductDTO = normalProductDTO;
  }

  private String normalProductName;
  private String normalCommodityCode;
  private String normalBrand;
  private String normalModel;
  private String normalSpec;
  private String normalVehicleBrand;
  private String normalVehicleModel;
  private String normalVehicleBrandModelInfo;
  private String normalUnit;
  private Boolean hideExpander;
  private List<ProductModifyFields> productModifyFieldsList;

  public List<ProductModifyFields> getProductModifyFieldsList() {
    return productModifyFieldsList;
  }

  public void setProductModifyFieldsList(List<ProductModifyFields> productModifyFieldsList) {
    this.productModifyFieldsList = productModifyFieldsList;
  }

  public String getNormalVehicleBrandModelInfo() {
    return normalVehicleBrandModelInfo;
  }

  public void setNormalVehicleBrandModelInfo(String normalVehicleBrandModelInfo) {
    this.normalVehicleBrandModelInfo = normalVehicleBrandModelInfo;
  }

  public Boolean getHideExpander() {
    return hideExpander;
  }

  public void setHideExpander(Boolean hideExpander) {
    this.hideExpander = hideExpander;
  }

  public String getNormalCommodityCode() {
    return normalCommodityCode;
  }

  public void setNormalCommodityCode(String normalCommodityCode) {
    this.normalCommodityCode = normalCommodityCode;
  }

  public String getNormalProductName() {
    return normalProductName;
  }

  public void setNormalProductName(String normalProductName) {
    this.normalProductName = normalProductName;
  }

  public String getNormalBrand() {
    return normalBrand;
  }

  public void setNormalBrand(String normalBrand) {
    this.normalBrand = normalBrand;
  }

  public String getNormalModel() {
    return normalModel;
  }

  public void setNormalModel(String normalModel) {
    this.normalModel = normalModel;
  }

  public String getNormalSpec() {
    return normalSpec;
  }

  public void setNormalSpec(String normalSpec) {
    this.normalSpec = normalSpec;
  }

  public String getNormalVehicleBrand() {
    return normalVehicleBrand;
  }

  public void setNormalVehicleBrand(String normalVehicleBrand) {
    this.normalVehicleBrand = normalVehicleBrand;
  }

  public String getNormalVehicleModel() {
    return normalVehicleModel;
  }

  public void setNormalVehicleModel(String normalVehicleModel) {
    this.normalVehicleModel = normalVehicleModel;
  }

  public String getNormalUnit() {
    return normalUnit;
  }

  public void setNormalUnit(String normalUnit) {
    this.normalUnit = normalUnit;
  }

  public void setNormalProductProperty(NormalProductDTO normalProductDTO) {
    if (null == normalProductDTO) {
      return;
    }
    this.setHideExpander(false);
    this.setNormalBrand(normalProductDTO.getBrand());
    this.setNormalCommodityCode(normalProductDTO.getCommodityCode());
    this.setNormalProductName(normalProductDTO.getProductName());
    this.setNormalSpec(normalProductDTO.getSpec());
    this.setNormalModel(normalProductDTO.getModel());
    this.setNormalVehicleBrand(normalProductDTO.getVehicleBrand());
    this.setNormalVehicleModel(normalProductDTO.getVehicleModel());
    this.setNormalUnit(normalProductDTO.getUnit());
    this.setNormalVehicleBrandModelInfo(normalProductDTO.getVehicleBrandModelInfo());
  }


  public ProductStatus getSalesStatus() {
    return salesStatus;
  }

  public void setSalesStatus(ProductStatus salesStatus) {
    this.salesStatus = salesStatus;
  }

  public PromotionsEnum.PromotionStatus getPromotionsStatus() {
    return promotionsStatus;
  }

  public void setPromotionsStatus(PromotionsEnum.PromotionStatus promotionsStatus) {
    this.promotionsStatus = promotionsStatus;
  }

  public Long getLastInSalesTime() {
    return lastInSalesTime;
  }

  public void setLastInSalesTime(Long lastInSalesTime) {
    this.lastInSalesTime = lastInSalesTime;
    this.lastInSalesTimeStr=DateUtil.convertDateLongToDateString(DateUtil.STANDARD,lastInSalesTime);
  }

  public String getLastInSalesTimeStr() {
    return lastInSalesTimeStr;
  }

  public void setLastInSalesTimeStr(String lastInSalesTimeStr) {
    this.lastInSalesTimeStr = lastInSalesTimeStr;
  }

  public Long getLastOffSalesTime() {
    return lastOffSalesTime;
  }

  public void setLastOffSalesTime(Long lastOffSalesTime) {
    this.lastOffSalesTime = lastOffSalesTime;
  }

  public Double getInSalesAmount() {
    return inSalesAmount;
  }

  public void setInSalesAmount(Double inSalesAmount) {
    this.inSalesAmount = inSalesAmount;
  }

  public String getInSalesUnit() {
    return inSalesUnit;
  }

  public void setInSalesUnit(String inSalesUnit) {
    this.inSalesUnit = inSalesUnit;
  }

  public Double getViewedCount() {
    return viewedCount;
  }

  public void setViewedCount(Double viewedCount) {
    this.viewedCount = viewedCount;
  }

  public List<PromotionsDTO> getPromotionsDTOs() {
    return promotionsDTOs;
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

  public void setPromotionsDTOs(List<PromotionsDTO> promotionsDTOs) {
    this.promotionsDTOs = promotionsDTOs;
  }

  public List<String> getPromotionsTypeList() {
    return promotionsTypeList;
  }

  public void setPromotionsTypeList(List<String> promotionsTypeList) {
    this.promotionsTypeList = promotionsTypeList;
  }

  public List<String> getPromotionsIdList() {
    return promotionsIdList;
  }

  public void setPromotionsIdList(List<String> promotionsIdList) {
    this.promotionsIdList = promotionsIdList;
  }

  // endtime为空的是无限期的   这里过滤过期的
  public void filterPromotions() throws Exception {
    if(CollectionUtil.isNotEmpty(promotionsDTOs)){
      Iterator<PromotionsDTO> iterator = promotionsDTOs.iterator();
      PromotionsDTO promotionsDTO = null;
      while (iterator.hasNext()){
        promotionsDTO = iterator.next();
        if (promotionsDTO!=null && promotionsDTO.getEndTime() != null && promotionsDTO.getEndTime() < DateUtil.getToday(DateUtil.DATE_STRING_FORMAT_DAY, new Date())) {
          iterator.remove();
        }
      }
    }
  }

  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }

  public AchievementType getSalesTotalAchievementType() {
    return salesTotalAchievementType;
  }

  public void setSalesTotalAchievementType(AchievementType salesTotalAchievementType) {
    this.salesTotalAchievementType = salesTotalAchievementType;
  }

  public Double getSalesTotalAchievementAmount() {
    return salesTotalAchievementAmount;
  }

  public void setSalesTotalAchievementAmount(Double salesTotalAchievementAmount) {
    this.salesTotalAchievementAmount = salesTotalAchievementAmount;
  }

  public AchievementType getSalesProfitAchievementType() {
    return salesProfitAchievementType;
  }

  public void setSalesProfitAchievementType(AchievementType salesProfitAchievementType) {
    this.salesProfitAchievementType = salesProfitAchievementType;
  }

  public Double getSalesProfitAchievementAmount() {
    return salesProfitAchievementAmount;
  }

  public void setSalesProfitAchievementAmount(Double salesProfitAchievementAmount) {
    this.salesProfitAchievementAmount = salesProfitAchievementAmount;
  }

  public void setPromotionsDTOByPromotionsInfoJson(String promotionsInfoJson) {    //todo to be changed
    if(promotionsDTOs==null){
      promotionsDTOs=new ArrayList<PromotionsDTO>();
    }
    JsonUtil.jsonArrayToList(promotionsInfoJson, PromotionsDTO.class,promotionsDTOs);
    if(CollectionUtil.isNotEmpty(promotionsDTOs)){
      for(PromotionsDTO promotionsDTO:promotionsDTOs){
        if(promotionsDTO==null) continue;
        if (CollectionUtils.isNotEmpty(promotionsDTO.getPromotionsRuleDTOList())) {
          promotionsDTO.checkUnexpired();
          Collections.sort(promotionsDTO.getPromotionsRuleDTOList(), PromotionsRuleDTO.SORT_BY_LEVEL);
        }
      }
    }
  }

  /**
   * 基本属性是否一致， 包括：品名，品牌，规格，型号，车辆品牌，车型，商品编码
   * @param formProductDTO
   * @return
   */
  public boolean checkSameBasicProperties(ProductDTO formProductDTO) {
    if(formProductDTO == null){
      return false;
    }
    if(!StringUtil.isEqualIgnoreBlank(formProductDTO.getCommodityCode(), getCommodityCode())){
      return false;
    }
    if(!StringUtil.isEqualIgnoreBlank(formProductDTO.getName(), getName())){
      return false;
    }
    if(!StringUtil.isEqualIgnoreBlank(formProductDTO.getBrand(), getBrand())){
      return false;
    }
    if(!StringUtil.isEqualIgnoreBlank(formProductDTO.getProductVehicleBrand(), getProductVehicleBrand())){
      return false;
    }
    if(!StringUtil.isEqualIgnoreBlank(formProductDTO.getProductVehicleModel(), getProductVehicleModel())){
      return false;
    }
    if(!StringUtil.isEqualIgnoreBlank(formProductDTO.getModel(), getModel())){
      return false;
    }
    if(!StringUtil.isEqualIgnoreBlank(formProductDTO.getSpec(), getSpec())){
      return false;
    }
    return true;
  }


  public String generateDataResourceKey(){
    return StringUtils.defaultIfEmpty(name,"_")
      +StringUtils.defaultIfEmpty(brand,"_")
      +StringUtils.defaultIfEmpty(model,"_")
      +StringUtils.defaultIfEmpty(spec,"_")
      +StringUtils.defaultIfEmpty(productVehicleBrand,"_")
      +StringUtils.defaultIfEmpty(productVehicleModel,"_");
  }

  public String generateShopDataResultKey(){
    return StringUtils.defaultIfEmpty(shopId+"","_")
      +StringUtils.defaultIfEmpty(name,"_")
      +StringUtils.defaultIfEmpty(brand,"_")
      +StringUtils.defaultIfEmpty(model,"_")
      +StringUtils.defaultIfEmpty(spec,"_")
      +StringUtils.defaultIfEmpty(productVehicleBrand,"_")
      +StringUtils.defaultIfEmpty(productVehicleModel,"_");
  }

  public void setShopInfo(ShopDTO shopDTO){
    if(shopDTO==null){
      return;
    }
    this.setShopName(shopDTO.getName());
    this.setShopCity(shopDTO.getCity());
    this.setShopProvince(shopDTO.getProvince());
    this.setShopRegion(shopDTO.getRegion());
  }

  public Long getShopProvince() {
    return shopProvince;
  }
  public String generateProductInfo() {
    StringBuffer sb=new StringBuffer();
    sb.append(StringUtil.truncValue(this.getCommodityCode())).append(" ").append(StringUtil.truncValue(this.getName())).append(" ")
      .append(StringUtil.truncValue(this.getBrand())).append(" ").append(StringUtil.truncValue(this.getSpec())).append(" ")
      .append(StringUtil.truncValue(this.getModel())).append(" ").append(StringUtil.truncValue(this.getVehicleModel())).append(" ")
      .append(StringUtil.truncValue(this.getVehicleBrand()));
     this.setProductInfoStr(sb.toString());
    return  productInfoStr;
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

  public Double getCustomScore() {
    return customScore;
  }

  public void setCustomScore(Double customScore) {
    this.customScore = customScore;
  }

  public boolean isLocalCity() {
    return localCity;
  }

  public void setLocalCity(boolean localCity) {
    this.localCity = localCity;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public void setProductInfo(String productInfo) {
    this.productInfo = productInfo;
  }

  public Long getLastModified() {
    return lastModified;
  }

  public void setLastModified(Long lastModified) {
    this.lastModified = lastModified;
  }

  public String getProductInfo(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getCommodityCode())){
      sb.append(this.getCommodityCode()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getName())){
      sb.append(this.getName()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getBrand())){
      sb.append(this.getBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getSpec())){
      sb.append(this.getSpec()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getModel())){
      sb.append(this.getModel()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductVehicleBrand())){
      sb.append(this.getProductVehicleBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductVehicleModel())){
      sb.append(this.getProductVehicleModel()).append(" ");
    }
    this.productInfo = sb.toString().trim();
    return this.productInfo;
  }
  public String generateCustomMatchNBContent(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getName())){
      sb.append(this.getName()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getBrand())){
      sb.append(this.getBrand()).append(" ");
    }
    return sb.toString().trim();
  }
  public String generateCustomMatchPContent(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getName())){
      sb.append(this.getName()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getBrand())){
      sb.append(this.getBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getSpec())){
      sb.append(this.getSpec()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getModel())){
      sb.append(this.getModel()).append(" ");
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

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  public String getPromotionTypesStr() {
    return promotionTypesStr;
  }

  public void setPromotionTypesStr(String promotionTypesStr) {
    this.promotionTypesStr = promotionTypesStr;
  }

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

  public NormalProductInventoryStatDTO toNormalProductInventoryStatDTO(){
    NormalProductInventoryStatDTO statDTO = new NormalProductInventoryStatDTO();
    statDTO.setShopName(this.getShopName());

    statDTO.setCommodityCode(StringUtil.isEmpty(this.getCommodityCode()) ? "--" : this.getCommodityCode());
    statDTO.setNameAndBrand(StringUtil.isEmpty(this.getName()) ? "--" : this.getName() + "/" + (StringUtil.isEmpty(this.getBrand()) ? "--" : this.getBrand()));
    statDTO.setSpecAndModel(StringUtil.isEmpty(this.getSpec()) ? "--" : this.getSpec() + "/" + (StringUtil.isEmpty(this.getModel()) ? "--" : this.getModel()));
    statDTO.setProductVehicleBrand(StringUtil.isEmpty(this.getProductVehicleModel()) ? "--" : this.getProductVehicleModel() + "/" + (StringUtil.isEmpty(this.getProductVehicleBrand()) ? "--" : this.getProductVehicleBrand()));
    if(StringUtils.isNotEmpty(this.getStorageUnit()) && StringUtil.isNotEmpty(this.getSellUnit()) && !this.getStorageUnit().equals(this.getSellUnit())){
         statDTO.setUnit(this.getStorageUnit() +"/" + this.getSellUnit());
    }else{
      statDTO.setUnit(StringUtil.isEmpty(this.getSellUnit()) ? "--" : this.getSellUnit());
    }
    return statDTO;
  }

}